package com.vroomvroom.fooddeliverys.repository.user

import androidx.lifecycle.LiveData
import com.vroomvroom.fooddeliverys.data.model.user.UserEntity
import com.vroomvroom.fooddeliverys.view.resource.Resource

interface UserRepository {

    suspend fun getUser( onResult: (Resource<Boolean>) -> Unit)
    suspend fun updateName(name: String, onResult: (Resource<Boolean>) -> Unit)
    suspend fun generatePhoneOtp(number: String): Resource<Boolean>?
    suspend fun verifyOtp(otp: String, otp1: String, onResult: (Resource<Boolean>) -> Unit)

    //Room
    suspend fun updateUserLocale(userEntity: UserEntity)
    suspend fun updateNameLocale(id: String, name: String)
    suspend fun deleteUserLocale()
    fun getUserLocale(): LiveData<UserEntity>

}