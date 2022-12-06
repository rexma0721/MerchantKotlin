package com.vroomvroom.android.data.model.merchant

data class MerchantDto(
    var _id: String  = "",
    var name: String = "",
    var img_url: String = "",
    var categories: List<String> = arrayListOf(),
    var product_sections: List<ProductSectionsDto>? = arrayListOf(),
    var rates: Int? = 0,
    var ratings: Double? = 0.0,
    var favorite: Boolean? = false,
    var location: List<String>? = arrayListOf(),
    var opening: Int = 0,
    var closing: Int = 0,
    var isOpen: Boolean = true,
    var reviews: List<ReviewDto>? = arrayListOf()
)

data class ProductSectionsDto(
    val _id: String = "",
    val name: String = "",
    var products: List<ProductDto> = arrayListOf()
)

data class ProductDto(
    val _id: String = "",
    val name: String = "",
    val product_img_url: String? = "",
    val price: Double = 0.0,
    val description: String? = "",
    val option: List<OptionDto>? = arrayListOf()
)

data class OptionDto(
    val name: String = "",
    val required: Boolean = false,
    val choice: List<ChoiceDto> = arrayListOf()
)

data class ChoiceDto(
    val name: String = "",
    val additional_price: Double? = 0.0
)

data class ReviewDto(
    val _id: String = "",
    val user_id: String = "",
    val comment: String? = "",
    val rate: Int = 0,
    val created_at: String = ""
)
