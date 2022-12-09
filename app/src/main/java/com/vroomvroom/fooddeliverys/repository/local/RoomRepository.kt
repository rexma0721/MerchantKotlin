package com.vroomvroom.fooddeliverys.repository.local

import androidx.lifecycle.LiveData
import com.vroomvroom.fooddeliverys.data.model.search.SearchEntity
import com.vroomvroom.fooddeliverys.data.model.user.LocationEntity

interface RoomRepository {
    //User


    //UserLocation
    suspend fun insertLocation(locationEntity: LocationEntity)
    suspend fun updateLocation(locationEntity: LocationEntity)
    suspend fun updateLocations(): Int
    suspend fun deleteLocation(locationEntity: LocationEntity)
    suspend fun deleteAllAddress()
    fun getUserLocation(): LiveData<List<LocationEntity>>

    //Cart


    //Search
    suspend fun insertSearch(searchEntity: SearchEntity)
    suspend fun deleteSearch(searchEntity: SearchEntity)
    suspend fun getAllSearch(): List<SearchEntity>
}