package com.microservices.headquarterservice.controller

import com.microservices.headquarterservice.model.headquarter.Supplier
import com.microservices.headquarterservice.model.supplier.SupplierOrder
import com.microservices.headquarterservice.model.supplier.SupplierOrderPart
import com.microservices.headquarterservice.model.supplier.SupplierOrderRequest
import com.microservices.headquarterservice.model.supplier.SupplierOrderResponse
import com.microservices.headquarterservice.service.SupplierService
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController("SupplierController")
class SupplierController (
    private val supplierService: SupplierService,
    ) {

    @GetMapping( "/suppliers/")
    fun getAll(
    ): Flux<Supplier> {
        return supplierService.getAll()
    }
    @PostMapping( "/supplier")
    fun createOrder(
        @RequestBody supplierOrderRequest: SupplierOrderRequest
    ): Mono<SupplierOrderResponse> {
        return supplierService.createOrderByRequest(supplierOrderRequest)
    }
    @GetMapping( "/supplier-orders/")
    fun getAllSupplierOrder(
    ): Flux<SupplierOrder> {
        return supplierService.getAllOrders()
    }
    @GetMapping( "/supplier-orders-parts/")
    fun getAllSupplierOrderParts(
    ): Flux<SupplierOrderPart> {
        return supplierService.getAllOrderParts()
    }
}