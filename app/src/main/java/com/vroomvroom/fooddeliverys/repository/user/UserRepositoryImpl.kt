package com.vroomvroom.fooddeliverys.repository.user

import android.util.Log
import androidx.lifecycle.LiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.ktx.Firebase
import com.vroomvroom.fooddeliverys.data.api.UserService
import com.vroomvroom.fooddeliverys.data.db.dao.UserDao
import com.vroomvroom.fooddeliverys.data.model.user.UserDto
import com.vroomvroom.fooddeliverys.data.model.user.UserEntity
import com.vroomvroom.fooddeliverys.data.model.user.UserMapper
import com.vroomvroom.fooddeliverys.repository.BaseRepository
import com.vroomvroom.fooddeliverys.repository.merchant.MerchantRepositoryImpl
import com.vroomvroom.fooddeliverys.view.resource.Resource
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val service: UserService,
    private val userDao: UserDao,
    private val userMapper: UserMapper
) : UserRepository, BaseRepository() {
    private lateinit var mFirestore: FirebaseFirestore
    override suspend fun getUser(onResult: (Resource<Boolean>) -> Unit) {
        mFirestore = FirebaseFirestore.getInstance()
        mFirestore.firestoreSettings = FirebaseFirestoreSettings.Builder().build()
        var data: Resource<Boolean>? = null
        try {
            val user = FirebaseAuth.getInstance().currentUser
            mFirestore.collection("Users").document(user!!.uid).get()
                .addOnSuccessListener { document ->
                    val userDto: UserDto = document.toObject(UserDto::class.java)!!
                    userDto._id = document.id
                    userDao.insertUser(userMapper.mapToDomainModel(userDto))
                    onResult(handleSuccess(true))
                }.addOnFailureListener {
                    onResult(handleSuccess(false))
                }

        } catch (e: Exception) {
            Log.e(TAG, "Error: ${e.message}")
            onResult(handleException(GENERAL_ERROR_CODE))
        }
    }


    override suspend fun updateName(name: String, onResult: (Resource<Boolean>) -> Unit) {
        var data: Resource<Boolean>? = null
        mFirestore = FirebaseFirestore.getInstance()
        mFirestore.firestoreSettings = FirebaseFirestoreSettings.Builder().build()
        try {
            val user = FirebaseAuth.getInstance().currentUser
            mFirestore.collection("Users").document(user!!.uid).update("name", name).await()
            updateNameLocale(user.uid, name)
            data = handleSuccess(true)
            onResult(data)

        } catch (e: Exception) {
            Log.e(TAG, "Error: ${e.message}")
            onResult(handleException(GENERAL_ERROR_CODE))
        }
    }

    override suspend fun generatePhoneOtp(number: String): Resource<Boolean>? {
        var data: Resource<Boolean>? = null
        try {
            val body = mapOf("number" to number)
            val result = service.registerPhoneNumber(body)
            if (result.isSuccessful && result.code() == 201) {
                data = handleSuccess(true)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error: ${e.message}")
            return handleException(GENERAL_ERROR_CODE)
        }
        return data
    }

    override suspend fun verifyOtp(
        otp: String,
        otp1: String,
        onResult: (Resource<Boolean>) -> Unit
    ) {
        var data: Resource<Boolean>? = null
        try {
            val credential = PhoneAuthProvider.getCredential(otp, otp1)
            mFirestore = FirebaseFirestore.getInstance()
            mFirestore.firestoreSettings = FirebaseFirestoreSettings.Builder().build()
            Firebase.auth.currentUser!!.linkWithCredential(credential).addOnSuccessListener {
                Log.v("heres", it.user!!.uid)
                val uid = it.user!!.uid
                val phonedata = HashMap<String, Any>()
                phonedata["number"] = it.user!!.phoneNumber.toString()
                phonedata["verified"] = true
                mFirestore.collection("Users").document(it.user!!.uid).update("phone", phonedata).addOnSuccessListener {
                    mFirestore.collection("Users").document(uid).get()
                        .addOnSuccessListener { document ->
                            val userDto: UserDto = document.toObject(UserDto::class.java)!!
                            userDto._id = document.id
                            userDao.updateUser(userMapper.mapToDomainModel(userDto))
                            onResult(handleSuccess(true))
                        }.addOnFailureListener {
                            onResult(handleException(GENERAL_ERROR_CODE))
                        }
                }.addOnFailureListener {
                    onResult(handleException(GENERAL_ERROR_CODE))
                }
            }.addOnFailureListener {
                Log.v("heres", "bugs")
                onResult(handleException(GENERAL_ERROR_CODE))
            }
        } catch (e: Exception) {
            Log.d(MerchantRepositoryImpl.TAG, "Error: ${e.message}")
            onResult(handleException(GENERAL_ERROR_CODE))
        }
    }

    //Room
    override suspend fun updateUserLocale(userEntity: UserEntity) = userDao.updateUser(userEntity)
    override suspend fun updateNameLocale(id: String, name: String) =
        userDao.updateUserName(id, name)

    override suspend fun deleteUserLocale() = userDao.deleteUser()
    override fun getUserLocale(): LiveData<UserEntity> = userDao.getUser()

    companion object {
        const val TAG = "UserRepositoryImpl"
    }
}