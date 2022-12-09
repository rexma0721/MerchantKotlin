package com.vroomvroom.fooddeliverys.repository.local

import androidx.lifecycle.LiveData
import com.vroomvroom.fooddeliverys.data.model.search.SearchEntity
import com.vroomvroom.fooddeliverys.data.db.dao.SearchDao
import com.vroomvroom.fooddeliverys.data.db.dao.UserDao
import com.vroomvroom.fooddeliverys.data.model.user.LocationEntity
import javax.inject.Inject

class RoomRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val searchDao: SearchDao
) : RoomRepository  {

    //UserLocation
    override suspend fun insertLocation(locationEntity: LocationEntity) = userDao.insertLocation(locationEntity)
    override suspend fun updateLocation(locationEntity: LocationEntity) = userDao.updateLocation(locationEntity)
    override suspend fun updateLocations() = userDao.updateLocations()
    override suspend fun deleteLocation(locationEntity: LocationEntity) = userDao.deleteLocation(locationEntity)
    override suspend fun deleteAllAddress() = userDao.deleteAllAddress()
    override fun getUserLocation(): LiveData<List<LocationEntity>> = userDao.getLocation()

    //Cart


    //Search
    override suspend fun insertSearch(searchEntity: SearchEntity) = searchDao.insertSearch(searchEntity)
    override suspend fun deleteSearch(searchEntity: SearchEntity) = searchDao.deleteSearch(searchEntity)
    override suspend fun getAllSearch(): List<SearchEntity> = searchDao.getAllSearch()
}