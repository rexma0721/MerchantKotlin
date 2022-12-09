package com.vroomvroom.fooddeliverys.data.model.order

data class OrderDto(
    val _id: String = "",
    val customer: Customer? = null,
    val merchant: Merchant? = null,
    val payment: PaymentDto? = null,
    val delivery_address: DeliveryAddress? = null,
    val order_detail: OrderDetailDto? = null,
    val status: StatusDto? = null,
    val created_at: String = "",
    val reviewed: Boolean = false
)

data class OrderDetailDto(
    val deliveryFee: Double = 0.0,
    val totalPrice: Double =0.0,
    val products: List<OrderProductDto>? = null,
)

data class OrderProductDto(
    val id: String? = null,
    val productId: String? = null,
    val name: String? = null,
    val productImgUrl: String? = null,
    val price: Double? = null,
    val quantity: Int? = null,
    val instructions: String? = null,
    val options: List<OrderProductOptionDto>? = null
)

data class Customer(
    val name: String? = null,
    val phone: Phone? = null
)

data class Phone(
    val number: String? = null,
    val verified: Boolean? = false
)

data class Merchant(
    val _id: String = "",
    val name: String = ""
)

data class PaymentDto(
    val method: String = "",
    val created_at: String = ""
)

data class OrderProductOptionDto(
    val name: String = "",
    val additional_price: Double? = null,
    val option_type: String = ""
)

data class StatusDto(
    val label: String = "",
    val ordinal: Int = 0
)