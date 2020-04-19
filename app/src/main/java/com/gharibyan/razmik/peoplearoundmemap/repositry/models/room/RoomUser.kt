package com.gharibyan.razmik.peoplearoundmemap.repositry.models.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
class RoomUser() {
    @PrimaryKey(autoGenerate = true) var id: Long? = null
    var documentid: String? = null
    var picture: String? = null
    var username: String? = null
    var followers: Long? = null
    var longitude: Double? = null
    var latitude: Double? = null
    var token: String? = null
    var visible: Boolean? = null
    var private: Boolean? = null
    var verified: Boolean? = null
}