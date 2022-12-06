package com.vroomvroom.android.data.model.cart

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.vroomvroom.android.data.model.BaseEntity
import com.vroomvroom.android.utils.Constants
import org.json.JSONObject

@Entity(tableName = Constants.CART_ITEM_OPTION_TABLE)
data class CartItemOptionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val name: String,
    val additionalPrice: Double? = null,
    val optionType: String,
    val productId: String? = null
) : BaseEntity {
    override fun toJson(): JSONObject {
        return JSONObject()
            .put("id", id)
            .put("name", name)
            .put("additionalPrice", additionalPrice)
            .put("optionType", optionType)
            .put("productId", productId)
    }

    override fun getPrimaryKey(): String {
        return id.toString()
    }
}