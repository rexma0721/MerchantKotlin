package com.vroomvroom.android.repository.user

import androidx.lifecycle.LiveData
import com.google.firebase.auth.FirebaseUser
import com.vroomvroom.android.data.model.user.UserEntity
import com.vroomvroom.android.view.resource.Resource

interface UserRepository {

    suspend fun getUser( onResult: (Resource<Boolean>) -> Unit)
    suspend fun updateName(name: String): Resource<Boolean>?
    suspend fun generatePhoneOtp(number: String): Resource<Boolean>?
    suspend fun verifyOtp(otp: String): Resource<Boolean>?

    //Room
    suspend fun updateUserLocale(userEntity: UserEntity)
    suspend fun updateNameLocale(id: String, name: String)
    suspend fun deleteUserLocale()
    fun getUserLocale(): LiveData<UserEntity>

}