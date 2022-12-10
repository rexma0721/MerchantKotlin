package com.vroomvroom.fooddeliverys.repository.merchant

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.vroomvroom.fooddeliverys.data.api.MerchantService
import com.vroomvroom.fooddeliverys.data.model.merchant.*
import com.vroomvroom.fooddeliverys.repository.BaseRepository
import com.vroomvroom.fooddeliverys.view.resource.Resource
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class MerchantRepositoryImpl @Inject constructor(
    private val service: MerchantService,
    private val merchantMapper: MerchantMapper
) : MerchantRepository, BaseRepository() {
    //TODO handle error body response
    private lateinit var mFirestore: FirebaseFirestore

    override suspend fun getCategories(type: String, onResult: (Resource<List<Category>>) -> Unit) {
        val data: Resource<List<Category>>?
        mFirestore = FirebaseFirestore.getInstance()
        mFirestore.firestoreSettings = FirebaseFirestoreSettings.Builder().build()
        try {
            val arrayList: ArrayList<Category> = ArrayList()
            val temp: Task<QuerySnapshot>? = if (type == "search") {
                mFirestore.collection("Category").get()
            } else {
                mFirestore.collection("Category").whereEqualTo("type", type).get()
            }
            val documents = temp!!.await().documents
            for (document in documents) {
                val fCategory: Category = document.toObject(Category::class.java)!!
                fCategory.id = document.id
                arrayList.add(fCategory)
            }
            data = handleSuccess(arrayList.toList())
            onResult(data)
        } catch (e: Exception) {
            Log.v("eerrr", e.message.toString())
            onResult(handleException(GENERAL_ERROR_CODE))
        }
    }

    override suspend fun getMerchants(
        category: String?,
        searchTerm: String?,
        onResult: (Resource<List<Merchant>>) -> Unit
    ) {
        try {
            Log.v("category", category.toString())
            Log.v("searchTerm", searchTerm.toString())
            mFirestore = FirebaseFirestore.getInstance()
            mFirestore.firestoreSettings = FirebaseFirestoreSettings.Builder().build()
            val arrayList: ArrayList<MerchantDto> = ArrayList()
            var temp: Task<QuerySnapshot>? = null
            if (category == null && searchTerm == null) {
                temp = mFirestore.collection("Merchants").get()
            }
            if (category != null) {
                temp = mFirestore.collection("Merchants").whereArrayContains("categories", category)
                    .get()
            }
            if (searchTerm != null) {
                temp = mFirestore.collection("Merchants").get()
            }
            val documents = temp!!.await().documents
            for (document in documents) {
                val fmerchant: MerchantDto = document.toObject(MerchantDto::class.java)!!
                if (searchTerm != null) {
                    val checkflag: Boolean = checkcontainflag(fmerchant, searchTerm)
                    if (!checkflag) {
                        continue
                    }
                }
                val results = document.get("favorites") as List<String>
                val uid = if (FirebaseAuth.getInstance().currentUser != null) {
                    FirebaseAuth.getInstance().currentUser!!.uid
                } else {
                    null
                }
                fmerchant.favorite = if (uid == null) {
                    false
                } else {
                    results.contains(uid)
                }
                val tde =
                    mFirestore.collection("Merchants").document(document.id).collection("reviews")
                        .get()
                val tresults = tde.await().documents
                var sum: Int = 0;
                for (result in tresults) {
                    sum += result.getLong("rate")!!.toInt()
                }
                fmerchant.rates = tresults.size
                fmerchant.ratings = if (tresults.size == 0) {
                    0.0
                } else {
                    sum.toDouble() / tresults.size
                }
                arrayList.add(fmerchant)
            }
            val merchants = merchantMapper.mapToDomainModelList(arrayList)
            onResult(handleSuccess(merchants))
        } catch (e: Exception) {
            Log.e(TAG, "Error1: ${e.message}")
            onResult(handleException(GENERAL_ERROR_CODE))
        }
    }

    private fun checkcontainflag(fmerchant: MerchantDto, searchTerm: String): Boolean {
        if(fmerchant.name.toLowerCase().contains(searchTerm.toLowerCase())){
            return true
        }
        if(fmerchant.categories.any { it.contains(searchTerm, ignoreCase = true) }){
            return true
        }
        for(productSections in fmerchant.product_sections!!){
            for(product in productSections.products){
                if(product.name.contains(searchTerm, ignoreCase = true)){
                    return true
                }
                if(product.description!!.contains(searchTerm, ignoreCase = true)){
                    return true
                }
            }
        }
        return false
    }

    override suspend fun getMerchant(id: String): Resource<Merchant>? {
        var data: Resource<Merchant>? = null
        var merchantDto: MerchantDto = MerchantDto()
        try {
            mFirestore = FirebaseFirestore.getInstance()
            mFirestore.firestoreSettings = FirebaseFirestoreSettings.Builder().build()
            val temp = mFirestore.collection("Merchants").document(id).get()
            val document = temp.await()
            merchantDto = document.toObject(MerchantDto::class.java)!!
            val fresults = document.get("favorites") as List<String>
            val uid = if (FirebaseAuth.getInstance().currentUser != null) {
                FirebaseAuth.getInstance().currentUser!!.uid
            } else {
                null
            }
            merchantDto.favorite = if (uid == null) {
                false
            } else {
                fresults.contains(uid)
            }
            val tde =
                mFirestore.collection("Merchants").document(document.id).collection("reviews").get()
            val tresults = tde.await().documents
            var sum: Int = 0;
            for (result in tresults) {
                sum += result.getLong("rate")!!.toInt()
            }
            merchantDto.rates = tresults.size
            merchantDto.ratings = if (tresults.size == 0) {
                0.0
            } else {
                sum.toDouble() / tresults.size
            }
            val productSectionsDto: ArrayList<ProductSectionsDto> = arrayListOf()
            val psections = mFirestore.collection("Merchants").document(document.id)
                .collection("product_sections").get()
            val secresults = psections.await().documents
            for (sresult in secresults) {
                val temp: ProductSectionsDto = sresult.toObject(ProductSectionsDto::class.java)!!
                val products: ArrayList<ProductDto> = arrayListOf()
                val productsde = mFirestore.collection("Merchants").document(document.id)
                    .collection("product_sections").document(sresult.id).collection("products")
                    .get()
                val proresults = productsde.await().documents
                for (proresult in proresults) {
                    products.add(proresult.toObject(ProductDto::class.java)!!)
                }
                temp.products = products.toList()
                productSectionsDto.add(temp)
            }
            merchantDto.product_sections = productSectionsDto.toList()
            val reviewDto: ArrayList<ReviewDto> = arrayListOf()
            val reviews =
                mFirestore.collection("Merchants").document(document.id).collection("reviews").get()
            val sreviews = reviews.await().documents
            for (sreview in sreviews) {
                reviewDto.add(sreview.toObject(ReviewDto::class.java)!!)
            }
            merchantDto.reviews = reviewDto.toList()
            data = handleSuccess(merchantMapper.mapToDomainModel(merchantDto))

        } catch (e: Exception) {
            Log.d(TAG, "Error: ${e.message}")
            return handleException(GENERAL_ERROR_CODE)
        }
        return data
    }

    override suspend fun getFavorites(): Resource<List<Merchant>>? {
        mFirestore = FirebaseFirestore.getInstance()
        mFirestore.firestoreSettings = FirebaseFirestoreSettings.Builder().build()
        val data: Resource<List<Merchant>>?
        try {
            val temp = mFirestore.collection("Merchants")
                .whereArrayContains("favorites", FirebaseAuth.getInstance().currentUser!!.uid).get()
            val tempresult = temp.await().documents
            val merchantDto: ArrayList<MerchantDto> = arrayListOf()
            for (document in tempresult) {
                val fmerchant: MerchantDto = document.toObject(MerchantDto::class.java)!!
                fmerchant.favorite = true
                val tde =
                    mFirestore.collection("Merchants").document(document.id).collection("reviews")
                        .get()
                val tresults = tde.await().documents
                var sum: Int = 0;
                for (result in tresults) {
                    sum += result.getLong("rate")!!.toInt()
                }
                fmerchant.rates = tresults.size
                fmerchant.ratings = if (tresults.size == 0) {
                    0.0
                } else {
                    sum.toDouble() / tresults.size
                }
                merchantDto.add(fmerchant)
            }
            data = handleSuccess(merchantMapper.mapToDomainModelList(merchantDto.toList()))
        } catch (e: Exception) {
            Log.e(TAG, "Error: ${e.message}")
            return handleException(GENERAL_ERROR_CODE)
        }
        return data
    }

    override suspend fun updateFavorite(id: String): Resource<Boolean> {
        val data: Resource<Boolean>
        mFirestore = FirebaseFirestore.getInstance()
        mFirestore.firestoreSettings = FirebaseFirestoreSettings.Builder().build()
        val uid: String = FirebaseAuth.getInstance().currentUser!!.uid
        try {
            val temp = mFirestore.collection("Merchants").document(id).get()
            val tempresult = temp.await().get("favorites") as List<String>
            if (tempresult.contains(uid)) {
                mFirestore.collection("Merchants").document(id)
                    .update("favorites", FieldValue.arrayRemove(uid)).await()
            } else {
                mFirestore.collection("Merchants").document(id)
                    .update("favorites", FieldValue.arrayUnion(uid)).await()
            }
            data = handleSuccess(true)
        } catch (e: Exception) {
            Log.e(TAG, "Error: ${e.message}")
            return handleException(GENERAL_ERROR_CODE)
        }
        return data
    }

    companion object {
        const val TAG = "MerchantRepositoryImpl"
    }
}