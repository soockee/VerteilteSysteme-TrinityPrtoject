package com.microservices.headquarterservice.service

import com.microservices.headquarterservice.model.Product
import com.microservices.headquarterservice.model.ProductPart
import com.microservices.headquarterservice.model.ProductResponse
import com.microservices.headquarterservice.persistence.ProductPartRepository
import com.microservices.headquarterservice.persistence.ProductRepository
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory
import org.springframework.amqp.core.AmqpTemplate
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@Service
class ProductService(
    private val productRepository: ProductRepository,
    private val productPartRepository: ProductPartRepository,
    private val rabbitTemplate: AmqpTemplate,
    @Value("\${microservice.rabbitmq.routingkey}") val headquarterRoutingKey: String,
    @Value("\${microservice.rabbitmq.queue}") val headquarterQueueName: String,
    @Value("\${microservice.rabbitmq.exchange}") val headquarterExchangeName: String,
    ){

    companion object {
        val logger = LoggerFactory.getLogger(ConditionService::class.java)
    }
    fun create(product: Product): Mono<Product> {
        return productRepository.save(product)
    }

    fun getProduct(productId: UUID): Mono<Product> {
        return productRepository.findById(productId)
    }

    fun getProductPartsByProductId(productId: UUID): Flux<ProductPart> {
        return productPartRepository.findAll().filter{elem -> elem.product_id == productId }
    }

    /**
     * Build ProductReponse by
     * 1) Get Product by productId
     * 2) Get ProductPart by productId
     * 3) Create ProductResponse Object
     * 4) Set ProductResponse fields without Parts
     * 5) Set Field Parts by creating Map with filtered productParts
     */
    fun getProductResponse(productId: UUID) {
        var product: Mono<Product> = getProduct(productId)
        var productParts: Flux<ProductPart> = getProductPartsByProductId(productId)

        var productObj: Product = product.block()!!
        var parts = productParts.collectList().block()!!

        var productResponse = ProductResponse(productObj,parts)

        rabbitTemplate.convertAndSend(
            headquarterExchangeName,
            headquarterRoutingKey,
            Json.encodeToString(productResponse))
    }

    fun getAllProductParts(): Flux<ProductPart> {
        return productPartRepository.findAll()
    }

    fun getAll(): Flux<Product> {
        return productRepository.findAll()
    }
}