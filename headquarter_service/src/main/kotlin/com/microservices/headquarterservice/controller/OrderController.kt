package com.microservices.headquarterservice.controller

import com.microservices.headquarterservice.model.headquarter.Order
import com.microservices.headquarterservice.model.headquarter.OrderProduct
import com.microservices.headquarterservice.model.headquarter.OrderRequest
import com.microservices.headquarterservice.service.ConditionService
import com.microservices.headquarterservice.service.OrderService
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@RestController("OrderController")
class OrderController (
    private val orderService: OrderService,
    ) {
    companion object {
        val logger = LoggerFactory.getLogger(ConditionService::class.java)
    }
    @PostMapping("/order/")
    fun create(
        @RequestBody orderRequest: OrderRequest
    ): Mono<String> {
       return orderService.createCustomerOrder(orderRequest)
    }

    @GetMapping( "/orders/")
    fun getAll(
    ): Flux<Order> {
        return orderService.getAllOrders()
    }

    @GetMapping( "/order/status/")
    fun getStatusByOrderId(
        @RequestParam orderId: String
    ): Mono<String> {
        return orderService.getStatusById(UUID.fromString(orderId))
    }

    @GetMapping( "/order-products/")
    fun getAllOrderProducts(
    ): Flux<OrderProduct> {
        return orderService.getAllOrderProducts()
    }
}