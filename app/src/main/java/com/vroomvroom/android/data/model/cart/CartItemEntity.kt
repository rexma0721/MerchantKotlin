package com.vroomvroom.android.data.model.cart

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.vroomvroom.android.data.model.BaseEntity
import com.vroomvroom.android.utils.Constants.CART_ITEM_TABLE
import org.json.JSONObject

@Entity(tableName = CART_ITEM_TABLE)
data class CartItemEntity(
    @PrimaryKey
    val productId: String,
    @Embedded
    val cartMerchant: CartMerchantEntity,
    val name: String,
    val productImgUrl: String?,
    val price: Double,
    val quantity: Int,
    val specialInstructions: String? = null
) : BaseEntity {
    override fun toJson(): JSONObject {
        return JSONObject()
            .put("productId", productId)
            .put("cartMerchant", cartMerchant)
            .put("name", name)
            .put("productImgUrl", productImgUrl)
            .put("price", price)
            .put("quantity", quantity)
            .put("specialInstructions", specialInstructions)
    }

    override fun getPrimaryKey(): String {
        return productId
    }
}

data class CartMerchantEntity(
    val merchantId: String,
    val merchantName: String,
)
