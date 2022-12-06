package com.vroomvroom.android.data.model.order

import com.google.gson.annotations.SerializedName
import com.vroomvroom.android.data.model.BaseEntity
import org.json.JSONObject

data class Order (
	@SerializedName("_id") val id: String?,
	val payment : Payment,
	val merchantId: String,
	val deliveryAddress: DeliveryAddress,
	val orderDetail: OrderDetail,
) : BaseEntity {
	override fun toJson(): JSONObject {
		return JSONObject()
			.put("id", id)
			.put("payment", payment)
			.put("merchantId", merchantId)
			.put("deliveryAddress", deliveryAddress)
			.put("orderDetail", orderDetail)
	}

	override fun getPrimaryKey(): String? {
		return id
	}
}

data class Payment (
	val method : String,
	val reference : String?,
)

data class DeliveryAddress (
	val address: String?,
	val city: String?,
	@SerializedName("additional_information") val addInfo: String? = null,
	val coordinates: List<Double>
)

data class OrderDetail (
	@SerializedName("delivery_fee") val deliveryFee : Double,
	@SerializedName("total_price") val totalPrice : Double,
	val products : List<OrderProduct>,
)

data class OrderProduct (
	@SerializedName("_id") val id: String?,
	@SerializedName("product_id") val productId : String,
	val name : String,
	@SerializedName("product_img_url") val productImgUrl : String?,
	val price : Double,
	val quantity : Int,
	val instructions : String? = null,
	val options : List<OrderProductOption>?
)

data class OrderProductOption (
	val name : String,
	val additionalPrice : Double?,
	val optionType : String
)