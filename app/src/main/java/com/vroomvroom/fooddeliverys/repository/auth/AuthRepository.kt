package com.vroomvroom.fooddeliverys.repository.auth

import com.vroomvroom.fooddeliverys.data.model.user.LocationEntity
import com.vroomvroom.fooddeliverys.data.model.user.UserEntity
import com.vroomvroom.fooddeliverys.view.resource.Resource

interface AuthRepository {

    suspend fun register(
        locationEntity: LocationEntity,
        userEntity: UserEntity,
        onResult: (Resource<Boolean>) -> Unit
    )
    suspend fun generateEmailOtp(emailAddress: String): Resource<Boolean>
    suspend fun verifyEmailOtp(emailAddress: String, otp: String): Resource<Boolean>

}