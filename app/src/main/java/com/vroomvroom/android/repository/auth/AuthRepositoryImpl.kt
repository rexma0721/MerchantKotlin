package com.vroomvroom.android.repository.auth

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.vroomvroom.android.data.api.AuthService
import com.vroomvroom.android.data.db.dao.UserDao
import com.vroomvroom.android.data.model.user.LocationEntity
import com.vroomvroom.android.data.model.user.UserEntity
import com.vroomvroom.android.data.model.user.UserMapper
import com.vroomvroom.android.repository.BaseRepository
import com.vroomvroom.android.view.resource.Resource
import okhttp3.internal.wait
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val service: AuthService,
    private val userDao: UserDao,
    private val userMapper: UserMapper
) : AuthRepository, BaseRepository() {
    private lateinit var mFirestore: FirebaseFirestore

    override suspend fun register(
        locationEntity: LocationEntity,
        userEntity: UserEntity,
        onResult: (Resource<Boolean>) -> Unit
    ) {
        mFirestore = FirebaseFirestore.getInstance()
        var data: Resource<Boolean>? = null
        try {
            val userdata = HashMap<String, Any>()
            userdata["name"] = userEntity.name!!
            userdata["email"] = userEntity.email!!
            userdata["phone"] = userEntity.phone!!
            userdata["location"] = locationEntity

             mFirestore.collection("Users").document(userEntity.id).set(userdata)
                .addOnSuccessListener {
                    userDao.insertUser(userEntity)
                    onResult(handleSuccess(true))
                }
                .addOnFailureListener { onResult(handleSuccess(false)) }

        } catch (e: Exception) {
            Log.d(TAG, "Error: ${e.message}")
            onResult(handleException(GENERAL_ERROR_CODE))
        }
    }

    override suspend fun generateEmailOtp(emailAddress: String): Resource<Boolean> {
        val data: Resource<Boolean>
        try {
            val body = mapOf("emailAddress" to emailAddress)
            val result = service.generateEmailOtp(body)
            if (result.isSuccessful && result.code() == 200) {
                data = handleSuccess(true)
            } else {
                return handleException(result.code(), result.errorBody())
            }
        } catch (e: Exception) {
            Log.d(TAG, "Error: ${e.message}")
            return handleException(GENERAL_ERROR_CODE)
        }
        return data
    }

    override suspend fun verifyEmailOtp(emailAddress: String, otp: String): Resource<Boolean> {
        val data: Resource<Boolean>
        try {
            val body = mapOf(
                "emailAddress" to emailAddress,
                "otp" to otp
            )
            val result = service.verifyEmailOtp(body)
            if (result.isSuccessful && result.code() == 200) {
                data = handleSuccess(true)
            } else {
                return handleException(result.code(), result.errorBody())
            }
        } catch (e: Exception) {
            Log.d(TAG, "Error: ${e.message}")
            return handleException(GENERAL_ERROR_CODE)
        }
        return data
    }

    companion object {
        const val TAG = "AuthRepositoryImpl"
    }
}