package com.vroomvroom.fooddeliverys.repository.merchant

import com.vroomvroom.fooddeliverys.data.model.merchant.Category
import com.vroomvroom.fooddeliverys.data.model.merchant.Merchant
import com.vroomvroom.fooddeliverys.view.resource.Resource

interface MerchantRepository {

    suspend fun getCategories(type: String,onResult: (Resource<List<Category>>) -> Unit)
    suspend fun getMerchants(category: String?, searchTerm: String?, onResult: (Resource<List<Merchant>>) -> Unit)
    suspend fun getMerchant(id: String): Resource<Merchant>?
    suspend fun getFavorites(): Resource<List<Merchant>>?
    suspend fun updateFavorite(id: String): Resource<Boolean>

}