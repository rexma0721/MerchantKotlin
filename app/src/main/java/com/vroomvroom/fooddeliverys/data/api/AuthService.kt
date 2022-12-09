package com.vroomvroom.fooddeliverys.data.api

import com.vroomvroom.fooddeliverys.data.model.BaseResponse
import com.vroomvroom.fooddeliverys.data.model.user.LocationEntity
import com.vroomvroom.fooddeliverys.data.model.user.UserDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {

    @POST("auth/register")
    suspend fun register(
        @Body body: LocationEntity
    ): Response<BaseResponse<UserDto>>

    @POST("auth/email-otp")
    suspend fun generateEmailOtp(
        @Body body: Map<String, String>
    ): Response<BaseResponse<Map<String, Any>>>

    @POST("auth/verify-email-otp")
    suspend fun verifyEmailOtp(
        @Body body: Map<String, String>
    ): Response<BaseResponse<Map<String, Any>>>
}