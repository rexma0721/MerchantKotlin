package com.vroomvroom.fooddeliverys.data.model.cart

import com.vroomvroom.fooddeliverys.data.model.merchant.Option

object CartItemMapper {

    fun mapToCartItemEntity(
        productId: String,
        name: String,
        productImgUrl: String?,
        price: Double,
        quantity: Int,
        specialInstructions: String?,
        merchantId: String,
        merchantName: String,
        merchantlat: String,
        merchantlon: String

    ): CartItemEntity {
        return CartItemEntity(
            productId,
            mapToMerchantEntity(merchantId, merchantName,merchantlat, merchantlon),
            name,
            productImgUrl,
            price,
            quantity,
            specialInstructions
        )
    }

    private fun mapToMerchantEntity(id: String, name: String, lat: String, lon: String): CartMerchantEntity {
        return CartMerchantEntity(id, name, lat, lon)
    }

    fun mapFromDomainModelList(model: Map<String, Option>): List<CartItemOptionEntity> {
        return model.map { (key, value) ->
            CartItemOptionEntity(null, value.name, value.additionalPrice, key)
        }
    }
}