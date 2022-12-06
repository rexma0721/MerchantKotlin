package com.vroomvroom.android.data.model.search

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.vroomvroom.android.data.model.BaseEntity
import com.vroomvroom.android.utils.Constants.SEARCH_TABLE
import org.json.JSONObject

@Entity(tableName = SEARCH_TABLE)
data class SearchEntity(
    @PrimaryKey
    val searchTerm: String,
    val fromLocal: Boolean = false,
    val createdAt: Long = 0
) : BaseEntity {
    override fun toJson(): JSONObject {
        return JSONObject()
            .put("searchTerm", searchTerm)
            .put("fromLocal", fromLocal)
            .put("cratedAt", createdAt)
    }

    override fun getPrimaryKey(): String {
        return searchTerm
    }
}
