package com.vroomvroom.android.repository.merchant

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.vroomvroom.android.data.api.MerchantService
import com.vroomvroom.android.data.model.merchant.*
import com.vroomvroom.android.repository.BaseRepository
import com.vroomvroom.android.view.resource.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject


class MerchantRepositoryImpl @Inject constructor(
    private val service: MerchantService,
    private val merchantMapper: MerchantMapper
) : MerchantRepository, BaseRepository()  {
    //TODO handle error body response
    private lateinit var mFirestore: FirebaseFirestore
    override suspend fun getCategories(
        type: String,
        _categories: MutableLiveData<Resource<List<Category>>>
    ) {
        var data: Resource<List<Category>>? = null
        mFirestore = FirebaseFirestore.getInstance()
        mFirestore.firestoreSettings = FirebaseFirestoreSettings.Builder().build()
        val arrayList: ArrayList<Category> = ArrayList()
        mFirestore.collection("Category").whereEqualTo("type", type).get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val fCategory: Category = document.toObject(Category::class.java)
                    fCategory.id = document.id
                    arrayList.add(fCategory)
                }
                data = handleSuccess(arrayList.toList())
                _categories.postValue(data);
            }.addOnFailureListener {
            _categories.postValue(handleException(GENERAL_ERROR_CODE))
        }

    }

    override suspend fun getMerchants(
        category: String?,
        searchTerm: String?,
        onResult: (Resource<List<Merchant>>) -> Unit
    ) {
        try {
            mFirestore = FirebaseFirestore.getInstance()
            mFirestore.firestoreSettings = FirebaseFirestoreSettings.Builder().build()
            val arrayList: ArrayList<MerchantDto> = ArrayList()
            var temp: Task<QuerySnapshot>? = null
            if(category == null && searchTerm == null){
                temp = mFirestore.collection("Merchants").get()
            }
            if(category != null){
                temp = mFirestore.collection("Merchants").whereArrayContains("categories", category).get()
            }
            if(searchTerm != null) {
                temp = mFirestore.collection("Merchants").startAt(searchTerm).endAt(searchTerm + "\uf8ff").get()
            }
            val documents = temp!!.await().documents
                for (document in documents) {
                    val fmerchant: MerchantDto = document.toObject(MerchantDto::class.java)!!
                    val de = mFirestore.collection("Merchants").document(document.id).collection("favorites").document("favorite").get()
                    val results = de.await().get("favorites") as List<HashMap<String?, String?>>
                    for(result in results) {
                        if(result["user_id"].toString() == FirebaseAuth.getInstance().currentUser!!.uid){
                            fmerchant.favorite = true
                        }
                    }
                    val tde = mFirestore.collection("Merchants").document(document.id).collection("reviews").get()
                    val tresults = tde.await().documents
                    var sum: Int = 0;
                    for(result in tresults) {
                        sum += result.getLong("rate")!!.toInt()
                    }
                    fmerchant.rates = tresults.size
                    fmerchant.ratings = sum.toDouble()/ tresults.size
                    fmerchant._id = document.id
                    arrayList.add(fmerchant)
                }
                val merchants = merchantMapper.mapToDomainModelList(arrayList)
                onResult(handleSuccess(merchants))
        } catch (e: Exception) {
            Log.d(TAG, "Error: ${e.message}")
            onResult(handleException(GENERAL_ERROR_CODE))
        }
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
            val favorite = mFirestore.collection("Merchants").document(document.id).collection("favorites").document("favorite").get()
            val fresults = favorite.await().get("favorites") as List<HashMap<String?, String?>>
            for(result in fresults) {
                if(result["user_id"].toString() == FirebaseAuth.getInstance().currentUser!!.uid){
                    merchantDto.favorite = true
                }
            }
            val tde = mFirestore.collection("Merchants").document(document.id).collection("reviews").get()
            val tresults = tde.await().documents
            var sum: Int = 0;
            for(result in tresults) {
                sum += result.getLong("rate")!!.toInt()
            }
            merchantDto.rates = tresults.size
            merchantDto.ratings = sum.toDouble()/ tresults.size
            val productSectionsDto: ArrayList<ProductSectionsDto> = arrayListOf()
            val psections = mFirestore.collection("Merchants").document(document.id).collection("product_sections").get()
            val secresults = psections.await().documents
            for(sresult in secresults){
                val temp:ProductSectionsDto = sresult.toObject(ProductSectionsDto::class.java)!!
                val products: ArrayList<ProductDto> = arrayListOf()
                val productsde = mFirestore.collection("Merchants").document(document.id).collection("product_sections").document(sresult.id).collection("products").get()
                val proresults = productsde.await().documents
                for(proresult in proresults){
                    products.add(proresult.toObject(ProductDto::class.java)!!)
                }
                temp.products = products.toList()
                productSectionsDto.add(temp)
            }
            merchantDto.product_sections = productSectionsDto.toList()
            val reviewDto: ArrayList<ReviewDto> = arrayListOf()
            val reviews = mFirestore.collection("Merchants").document(document.id).collection("reviews").get()
            val sreviews = reviews.await().documents
            for(sreview in sreviews){
                reviewDto.add(sreview.toObject(ReviewDto::class.java)!!)
            }
            merchantDto.reviews = reviewDto.toList()
            data = handleSuccess(merchantMapper.mapToDomainModel(merchantDto))
//            val result = service.getMerchant(id)
//            if (result.isSuccessful) {
//                result.body()?.data?.let {
//                    withContext(Dispatchers.Default) {
//                        data = handleSuccess(merchantMapper.mapToDomainModel(it))
//                    }
//                }
//            }
        } catch (e: Exception) {
            Log.d(TAG, "Error: ${e.message}")
            return handleException(GENERAL_ERROR_CODE)
        }
        return data
    }

    override suspend fun getFavorites(): Resource<List<Merchant>>? {
        var data: Resource<List<Merchant>>? = null
        try {
            val result = service.getFavorites()
            if (result.isSuccessful) {
                result.body()?.data?.let {
                    withContext(Dispatchers.Default) {
                        data = handleSuccess(merchantMapper.mapToDomainModelList(it))
                    }
                }
            }
        } catch (e: Exception) {
            Log.d(TAG, "Error: ${e.message}")
            return handleException(GENERAL_ERROR_CODE)
        }
        return data
    }

    override suspend fun updateFavorite(id: String): Resource<Boolean> {
        val data: Resource<Boolean>
        try {
            val result = service.updateFavorite(id)
            data = if (result.isSuccessful && result.code() == 201) {
                handleSuccess(true)
            } else {
                handleSuccess(false)
            }
        } catch (e: Exception) {
            Log.d(TAG, "Error: ${e.message}")
            return handleException(GENERAL_ERROR_CODE)
        }
        return data
    }

    companion object {
        const val TAG = "MerchantRepositoryImpl"
    }
}