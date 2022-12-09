//package com.vroomvroom.android.repository.services
//
//import androidx.lifecycle.MutableLiveData
//import com.google.firebase.database.DatabaseReference
//import com.google.firebase.database.ktx.database
//import com.google.firebase.ktx.Firebase
//import com.vroomvroom.android.data.model.BaseEntity
//import com.vroomvroom.android.view.resource.Resource
//import javax.inject.Inject
//
//class RemoteDataRepositoryImpl @Inject constructor(
//    private val remoteRepository: DatabaseReference = Firebase.database.reference
//) : RemoteDataRepository {
//
//    private val _order by lazy { MutableLiveData<Resource<String>>() }
//
//    override suspend fun saveRemoteData(data: BaseEntity, dataType: String, primaryKey: String) {
//        try {
//            val dataJson = data.toJson()
//            remoteRepository.child(dataType).child(primaryKey).setValue(dataJson)
//            _order.postValue(Resource.Success(primaryKey))
//        } catch (e: Exception) {
//            _order.postValue(Resource.Error(e))
//        }
//    }
//
//    override suspend fun updateDataByPrimaryKey(dataType: String, primaryKey: String) {
//        remoteRepository.child(dataType).child(primaryKey).get()
//            .addOnSuccessListener {
//                // TODO : treat data fetched from firebase - implement conversion from DataSnapshot and saving to local database
//            }
//            .addOnFailureListener {
//
//            }
//    }
//
//}