package com.vroomvroom.fooddeliverys.repository.order

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.vroomvroom.fooddeliverys.data.api.OrderService
import com.vroomvroom.fooddeliverys.data.model.cart.CartItemWithOptions
import com.vroomvroom.fooddeliverys.data.model.order.*
import com.vroomvroom.fooddeliverys.data.model.order.OrderMapper.mapToOrder
import com.vroomvroom.fooddeliverys.data.model.user.LocationEntity
import com.vroomvroom.fooddeliverys.repository.BaseRepository
//import com.vroomvroom.android.repository.services.RemoteDataRepository
//import com.vroomvroom.android.repository.services.RemoteDataRepositoryImpl
import com.vroomvroom.fooddeliverys.view.resource.Resource
import kotlinx.coroutines.tasks.await
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class OrderRepositoryImpl @Inject constructor(
    private val service: OrderService,
) : OrderRepository, BaseRepository() {
    private lateinit var mFirestore: FirebaseFirestore

    override suspend fun getOrders(status: Status): Resource<List<OrderDto>>? {
        var data: Resource<List<OrderDto>>? = null
        try {
            Log.v("status.ordinal", status.ordinal.toString())
            val currentUser = FirebaseAuth.getInstance().currentUser
            mFirestore = FirebaseFirestore.getInstance()
            val orderDtos: ArrayList<OrderDto> = arrayListOf()
//            val orderlist = arrayOf("Pending", "Confirmed", "To Receive", "Delivered", "Cancelled")
            val reuslts = mFirestore.collection("Orders").whereEqualTo("customerId", currentUser!!.uid).whereEqualTo("status.ordinal", status.ordinal).get().await().documents
            for(reuslt in reuslts) {
                val order: OrderDto = reuslt.toObject(OrderDto::class.java)!!
                orderDtos.add(order)
            }
            data = handleSuccess(orderDtos)
        } catch (e: Exception) {
            Log.d(TAG, "Error: ${e.message}")
            return handleException(GENERAL_ERROR_CODE)
        }
        return data
    }

    override suspend fun getOrder(id: String): Resource<OrderDto>? {
        var data: Resource<OrderDto>? = null
        mFirestore = FirebaseFirestore.getInstance()
        try {
            val reuslt = mFirestore.collection("Orders").document(id).get().await()
            data = handleSuccess(reuslt.toObject(OrderDto::class.java)!!)
        } catch (e: Exception) {
            Log.d(TAG, "Error: ${e.message}")
            return handleException(GENERAL_ERROR_CODE)
        }
        return data
    }

    override suspend fun createOrders(
        merchantId: String,
        payment: Payment,
        deliveryFee: Double,
        totalPrice: Double,
        locationEntity: LocationEntity,
        cartItems: List<CartItemWithOptions>,
        onResult: (Resource<String>) -> Unit
    ) {
        var data: Resource<String>? = null
        mFirestore = FirebaseFirestore.getInstance()
        val de = mFirestore.collection("Users").document(FirebaseAuth.getInstance().currentUser!!.uid).get()
        val currentUser = de.await()
        try {
            val orderDat = HashMap<String, Any>()
            val order = mapToOrder(merchantId, payment, deliveryFee, totalPrice, locationEntity, cartItems)
            val customer = HashMap<String, Any>()
            customer["_id"] = FirebaseAuth.getInstance().currentUser!!.uid
            customer["name"] = currentUser.getString("name").toString()
            customer["phone"] = currentUser.get("phone") as HashMap<String, Any>
            orderDat["customer"] = customer
            orderDat["customerId"] = FirebaseAuth.getInstance().currentUser!!.uid
            val merchant = HashMap<String, Any>()
            val mer = mFirestore.collection("Merchants").document(merchantId).get().await()
            merchant["_id"] = mer.getString("_id").toString()
            merchant["name"] = mer.getString("name").toString()
            orderDat["merchant"] = merchant
            orderDat["merchantId"] = merchantId
            orderDat["payment"] = order.payment
            orderDat["delivery_address"] = order.deliveryAddress
            orderDat["order_detail"] = order.orderDetail
            val status = HashMap<String, Any>()
            status["label"] = "Pending"
            status["ordinal"] = 0
            orderDat["status"] = status
            orderDat["cancellation_reason"] = "null"
            orderDat["notified"] = false
            orderDat["reviewed"] = false
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            orderDat["created_at"] = LocalDateTime.now().format(formatter)

            mFirestore.collection("Orders").add(orderDat).addOnSuccessListener { documentReference ->
                mFirestore.collection("Orders").document(documentReference.id).update("_id",documentReference.id).addOnSuccessListener {
                    onResult( handleSuccess(documentReference.id))
                }
            }.addOnFailureListener {
                onResult(handleException(GENERAL_ERROR_CODE))
            }
//            val result = service.createOrder(order)
//            if (result.isSuccessful) {
//                result.body()?.data?.let {
//                    data = handleSuccess(it["orderId"].orEmpty())
//                }
////                if (Order::class.simpleName != null && order.getPrimaryKey() != null) {
//////                    remoteDataRepository.saveRemoteData(order, Order::class.simpleName!!, order.getPrimaryKey()!!)
////                }
//            }
        } catch (e: Exception) {
            Log.d(TAG, "Error: ${e.message}")
            onResult(handleException(GENERAL_ERROR_CODE))
        }
    }

    override suspend fun cancelOrder(
        id: String,
        reason: String,
        onResult: (Resource<Boolean>) -> Unit
    ) {
        var data: Resource<Boolean>? = null
        try {
            mFirestore = FirebaseFirestore.getInstance()
            val status = HashMap<String, Any>()
            status["label"] = "Cancelled"
            status["ordinal"] = 4
            mFirestore.collection("Orders").document(id).update("status",status, "cancellation_reason", reason).addOnSuccessListener {
                onResult( handleSuccess(true))
            }
//            val body = mapOf("reason" to reason)
//            val result = service.cancelOrder(id, body)
//            if (result.isSuccessful && result.code() == 200) {
//                data = handleSuccess(true)
//            }
        } catch (e: Exception) {
            Log.d(TAG, "Error: ${e.message}")
            onResult( handleException(GENERAL_ERROR_CODE))
        }
    }

    override suspend fun updateOrderAddress(
        id: String,
        location: LocationEntity,
        onResult: (Resource<Boolean>) -> Unit
    ) {
        var data: Resource<Boolean>? = null
        mFirestore = FirebaseFirestore.getInstance()
        try {
            val deliveryAddress = DeliveryAddress(location.address, location.city,
                location.addInfo, listOf(location.latitude, location.longitude))
            mFirestore.collection("Orders").document(id).update("delivery_address", deliveryAddress).addOnSuccessListener {
                onResult(handleSuccess(true))
            }
//            val result = service.updateOrderAddress(id, deliveryAddress)
//            if (result.isSuccessful && result.code() == 201) {
//                data =
//            }
        } catch (e: Exception) {
            Log.d(TAG, "Error: ${e.message}")
            onResult(handleException(GENERAL_ERROR_CODE))
        }
    }

    override suspend fun createReview(
        id: String,
        merchantId: String,
        rate: Int,
        comment: String,
        onResult: (Resource<Boolean>) -> Unit
    ) {
        var data: Resource<Boolean>? = null
        try {
            mFirestore = FirebaseFirestore.getInstance()
            val currentUser = FirebaseAuth.getInstance().currentUser!!.uid
            val reviews = HashMap<String, Any>()
            reviews["comment"] = comment
            reviews["rate"] = rate
            reviews["user_id"] = currentUser
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            reviews["created_at"] = LocalDateTime.now().format(formatter)
            mFirestore.collection("Merchants").document(merchantId).collection("reviews").add(reviews).addOnSuccessListener {
                mFirestore.collection("Merchants").document(merchantId).collection("reviews").document(it.id).update("_id", it.id).addOnSuccessListener {
                    mFirestore.collection("Orders").document(id).update("reviewed", true).addOnSuccessListener {
                        onResult(handleSuccess(true))
                    }
                }
            }
//            val result = service.createReview(id, body)
//            if (result.isSuccessful && result.code() == 201) {
//                data =
//            }
        } catch (e: Exception) {
            Log.d(TAG, "Error: ${e.message}")
            onResult(handleException(GENERAL_ERROR_CODE))
        }
    }


    companion object {
        const val TAG = "OrderRepositoryImpl"
    }
}