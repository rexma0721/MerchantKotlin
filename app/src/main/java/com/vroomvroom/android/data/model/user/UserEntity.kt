package com.vroomvroom.android.data.model.user

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.vroomvroom.android.data.model.BaseEntity
import com.vroomvroom.android.utils.Constants.LOCATION_TABLE
import com.vroomvroom.android.utils.Constants.USER_TABLE
import kotlinx.parcelize.Parcelize
import org.json.JSONObject

@Entity(tableName = USER_TABLE)
data class UserEntity(
    @PrimaryKey
    val id: String,
    val name: String? = null,
    val email: String? = null,
    @Embedded
    val phone: Phone? = null
) : BaseEntity {
    override fun toJson(): JSONObject {
        return JSONObject()
            .put("id", id)
            .put("name", name)
            .put("email", email)
            .put("phone", phone)
    }

    override fun getPrimaryKey(): String {
        return id
    }
}

data class Phone(
    val number: String? = null,
    val verified: Boolean = false
)

@Parcelize
@Entity(tableName = LOCATION_TABLE)
data class LocationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val address: String? = null,
    val city: String? = null,
    val addInfo: String? = null,
    val latitude: Double,
    val longitude: Double,
    val currentUse: Boolean = true
): BaseEntity, Parcelable {
    override fun toJson(): JSONObject {
        return JSONObject()
            .put("id", id)
            .put("address", addInfo)
            .put("city", city)
            .put("addInfo", addInfo)
            .put("latitude", latitude)
            .put("longitude", longitude)
            .put("currentUse", currentUse)
    }

    override fun getPrimaryKey(): String {
        return id.toString()
    }
}