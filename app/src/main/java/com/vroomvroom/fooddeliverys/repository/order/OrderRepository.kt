package com.vroomvroom.fooddeliverys.repository.order

import com.vroomvroom.fooddeliverys.data.model.cart.CartItemWithOptions
import com.vroomvroom.fooddeliverys.data.model.order.OrderDto
import com.vroomvroom.fooddeliverys.data.model.order.Payment
import com.vroomvroom.fooddeliverys.data.model.order.Status
import com.vroomvroom.fooddeliverys.data.model.user.LocationEntity
import com.vroomvroom.fooddeliverys.view.resource.Resource

interface OrderRepository {

    suspend fun getOrders(status: Status): Resource<List<OrderDto>>?
    suspend fun getOrder(id: String): Resource<OrderDto>?
    suspend fun createOrders(
        merchantId: String,
        payment: Payment,
        deliveryFee: Double,
        totalPrice: Double,
        locationEntity: LocationEntity,
        cartItems: List<CartItemWithOptions>,
        onResult:(Resource<String>) -> Unit
    )
    suspend fun cancelOrder(id: String, reason: String, onResult: (Resource<Boolean>) -> Unit)
    suspend fun updateOrderAddress(
        id: String,
        location: LocationEntity,
        onResult: (Resource<Boolean>) -> Unit
    )
    suspend fun createReview(
        id: String,
        merchantId: String,
        rate: Int,
        comment: String,
        onResult: (Resource<Boolean>) -> Unit
    )
}