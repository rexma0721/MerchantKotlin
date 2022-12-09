package com.vroomvroom.fooddeliverys.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.vroomvroom.fooddeliverys.data.model.cart.CartItemOptionEntity
import com.vroomvroom.fooddeliverys.data.db.dao.CartItemDAO
import com.vroomvroom.fooddeliverys.data.model.cart.CartItemEntity
import com.vroomvroom.fooddeliverys.data.db.dao.SearchDao
import com.vroomvroom.fooddeliverys.data.model.search.SearchEntity
import com.vroomvroom.fooddeliverys.data.db.dao.UserDao
import com.vroomvroom.fooddeliverys.data.model.user.UserEntity
import com.vroomvroom.fooddeliverys.data.model.user.LocationEntity

@Database(
    entities = [
        CartItemEntity::class,
        CartItemOptionEntity::class,
        UserEntity::class,
        LocationEntity::class,
        SearchEntity::class
               ],
    version = 1,
    exportSchema = false
)
abstract class Database : RoomDatabase() {
    abstract fun cartItemDao(): CartItemDAO
    abstract fun userDao(): UserDao
    abstract fun searchDao(): SearchDao

}