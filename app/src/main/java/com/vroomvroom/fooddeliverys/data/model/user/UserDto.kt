package com.vroomvroom.fooddeliverys.data.model.user

data class UserDto(
    var _id: String  = "",
    val name: String? = "",
    val email: String? = "",
    val phone: PhoneDto? = null
)

data class PhoneDto(
    val number: String? = "",
    val verified: Boolean = false
)
