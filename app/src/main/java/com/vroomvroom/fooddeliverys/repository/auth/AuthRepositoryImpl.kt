package com.vroomvroom.fooddeliverys.repository.auth

import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.vroomvroom.fooddeliverys.data.api.AuthService
import com.vroomvroom.fooddeliverys.data.db.dao.UserDao
import com.vroomvroom.fooddeliverys.data.model.user.LocationEntity
import com.vroomvroom.fooddeliverys.data.model.user.UserDto
import com.vroomvroom.fooddeliverys.data.model.user.UserEntity
import com.vroomvroom.fooddeliverys.data.model.user.UserMapper
import com.vroomvroom.fooddeliverys.repository.BaseRepository
import com.vroomvroom.fooddeliverys.view.resource.Resource
import kotlinx.coroutines.tasks.await
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
        val auth = Firebase.auth.currentUser
        try {
            val userdata = HashMap<String, Any>()
            if (userEntity.name == ""){
                val email = auth?.email.toString()
                userdata["name"] = email.split("@")[0]
                userdata["email"] = email
                userEntity.id = auth!!.uid
                userEntity.name = userdata["name"]?.toString()
                userEntity.email = email
            } else {
                userdata["name"] = userEntity.name!!
                userdata["email"] = userEntity.email!!
            }
            userdata["phone"] = userEntity.phone!!
            userdata["location"] = locationEntity
            val dupvalidator = mFirestore.collection("Users").whereEqualTo("email", auth!!.email).get().await().documents
            if(dupvalidator.size == 0) {
                mFirestore.collection("Users").document(userEntity.id).set(userdata)
                    .addOnSuccessListener {
                        userDao.insertUser(userEntity)
                        onResult(handleSuccess(true))
                    }
                    .addOnFailureListener { onResult(handleSuccess(false)) }
            } else {
                mFirestore.collection("Users").document(userEntity.id).get()
                    .addOnSuccessListener { document ->
                        val userDto: UserDto = document.toObject(UserDto::class.java)!!
                        userDto._id = document.id
                        userDao.insertUser(userMapper.mapToDomainModel(userDto))
                        onResult(handleSuccess(true))
                    }.addOnFailureListener {
                        onResult(handleSuccess(false))
                    }
            }


        } catch (e: Exception) {
            Log.d(TAG, "Error1: ${e.toString()}")
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