package com.vroomvroom.fooddeliverys.data.model.cart

import androidx.room.Embedded
import androidx.room.Relation

class CartItemWithOptions(
    @Embedded
    val cartItem: CartItemEntity,
    @Relation(parentColumn = "productId", entityColumn = "productId")
    val cartItemOptions: List<CartItemOptionEntity>?
)