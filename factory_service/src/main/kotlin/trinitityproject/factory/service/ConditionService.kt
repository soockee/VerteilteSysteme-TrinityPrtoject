package trinitityproject.factory.service

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.reactive.asFlow
import org.springframework.amqp.AmqpException
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.core.ParameterizedTypeReference
import org.springframework.stereotype.Component
import reactor.kotlin.adapter.rxjava.toFlowable
import reactor.kotlin.core.publisher.toMono
import trinitityproject.factory.model.condition.Condition
import trinitityproject.factory.model.condition.ConditionRequest
import trinitityproject.factory.model.condition.PartCondition
import java.io.IOException
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap


@Component
class ConditionService(
    private val conditionCache: ConcurrentMap<UUID, PartCondition>,
    private val template: RabbitTemplate,
) {

    suspend fun getCondition(partId: UUID): PartCondition? {
        return if (conditionCache.containsKey(partId)) {
            conditionCache[partId]
        } else {
            val newCondition = template.convertSendAndReceiveAsType(
                "conditionRequests",
                ConditionRequest(partId),
                object : ParameterizedTypeReference<PartCondition>() {}
            )
            conditionCache[partId] = newCondition
            newCondition
        }
    }

    suspend fun getBestCondition(partId: UUID): Condition {
        val partCondition =
            getCondition(partId)
                .toMono()
                .asFlow()
                .retry(5) { exception ->
                    (exception is AmqpException).also { if (it) delay(1000) }
                }
                .filterNotNull()
                .first()

        return partCondition
            .conditions
            .minByOrNull {
                it.currency
            }!!
    }

}