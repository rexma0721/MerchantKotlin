package com.vroomvroom.fooddeliverys.data.model

import org.json.JSONObject

interface BaseEntity {

    fun toJson(): JSONObject
    fun getPrimaryKey(): String?
}