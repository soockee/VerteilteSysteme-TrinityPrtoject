package com.microservices.headquarterservice

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.slf4j.LoggerFactory
import org.springframework.amqp.core.*
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitAdmin
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct


@Configuration
class RabbitConfig(
    @Value("\${microservice.rabbitmq.queueCondition}") val queueCondition: String,
    @Value("\${microservice.rabbitmq.queueKIP}") val queueKIP: String,
    @Value("\${microservice.rabbitmq.queueOrder}") val queueOrder: String,
    @Value("\${microservice.rabbitmq.queueSupplier}") val queueSupplier: String,
    @Value("\${microservice.rabbitmq.queueSupplierResponse}") val queueSupplierResponse: String,
    @Value("\${microservice.rabbitmq.queueSupplierStatus}") val queueSupplierStatus: String,
    @Value("\${microservice.rabbitmq.queueSupplierStatusResponse}") val queueSupplierStatusResponse: String,

    private val connectionFactory: ConnectionFactory
) {
    companion object {
        val logger = LoggerFactory.getLogger(HeadquarterServiceApplication::class.java)
    }

    @Bean
    fun queueOrder(): Queue {
        // logger.warn(headquarterQueueName)
        return Queue(queueOrder, true)
    }

    @Bean
    fun queueKIP(): Queue {
        // logger.warn(headquarterQueueName)
        return Queue(queueKIP, true)
    }

    @Bean
    fun queueCondition(): Queue {
        // logger.warn(headquarterQueueName)
        return Queue(queueCondition, true)
    }

    @Bean
    fun queueSupplier(): Queue {
        // logger.warn(headquarterQueueName)
        return Queue(queueSupplier, true)
    }

    @Bean
    fun queueSupplierResponse(): Queue {
        // logger.warn(headquarterQueueName)
        return Queue(queueSupplierResponse, true)
    }

    @Bean
    fun queueSupplierStatus(): Queue {
        // logger.warn(headquarterQueueName)
        return Queue(queueSupplierStatus, true)
    }

    @Bean
    fun queueSupplierStatusResponse(): Queue {
        // logger.warn(headquarterQueueName)
        return Queue(queueSupplierStatusResponse, true)
    }

    @Bean
    fun amqpAdmin(): AmqpAdmin {
        val amqpAdmin = RabbitAdmin(connectionFactory)
        amqpAdmin.initialize()
        return amqpAdmin
    }

    @Bean
    fun createReceiverConfig() : ReceiverConfig{
        return ReceiverConfig(
            queueKIP(),
            queueOrder(),
            queueCondition(),
            queueSupplier(),
            queueSupplierResponse(),
            queueSupplierStatus(),
            queueSupplierStatusResponse(),
            amqpAdmin(),

        )
    }
    @Bean
    fun rabbitListenerContainerFactory(connectionFactory: ConnectionFactory): RabbitListenerContainerFactory<*>? {
        val factory = SimpleRabbitListenerContainerFactory()
        factory.setConnectionFactory(connectionFactory)
        // A custom objectMapper is needed, so the default values are set by kotlin
        val mapper = ObjectMapper()
            .registerModules(
                KotlinModule(),
                JavaTimeModule(),
            )
        factory.setMessageConverter(Jackson2JsonMessageConverter(mapper))
        return factory
    }

    @Component
    class ReceiverConfig(
        private val queueKIP: Queue,
        private val queueOrder: Queue,
        private val queueCondition: Queue,
        private val queueSupplier: Queue,
        private val queueSupplierResponse: Queue,
        private val queueSupplierStatus: Queue,
        private val queueSupplierStatusResponse: Queue,
        private val amqpAdmin: AmqpAdmin,
    ) {
        @PostConstruct
        fun createQueues() {
            logger.info("Initialize AMQP")
            amqpAdmin.declareQueue(queueKIP)
            amqpAdmin.declareQueue(queueOrder)
            amqpAdmin.declareQueue(queueCondition)
            amqpAdmin.declareQueue(queueSupplier)
            amqpAdmin.declareQueue(queueSupplierResponse)
            amqpAdmin.declareQueue(queueSupplierStatus)
            amqpAdmin.declareQueue(queueSupplierStatusResponse)
        }

    }
}
