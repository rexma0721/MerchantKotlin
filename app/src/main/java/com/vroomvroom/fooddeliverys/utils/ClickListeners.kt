package com.vroomvroom.fooddeliverys.utils

import android.content.Intent
import com.vroomvroom.fooddeliverys.data.model.merchant.Option
import com.vroomvroom.fooddeliverys.data.model.merchant.Product
import com.vroomvroom.fooddeliverys.view.resource.Resource


interface OnProductClickListener {
    fun onClick(product: Product)
}

interface OnOptionClickListener {
    fun onClick(option: Option, optionType: String)
}

interface SmsBroadcastReceiverListener {
    fun onIntent(intent: Resource<Intent>)
}