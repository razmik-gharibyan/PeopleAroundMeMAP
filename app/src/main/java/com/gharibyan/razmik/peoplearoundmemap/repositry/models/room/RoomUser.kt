package com.gharibyan.razmik.peoplearoundmemap.repositry.models.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class RoomUser(
    @ColumnInfo val documentid: String?,
    @ColumnInfo val picture: String?,
    @ColumnInfo val username: String?,
    @ColumnInfo val followers: Long?,
    @ColumnInfo val longitude: Double?,
    @ColumnInfo val latitude: Double?,
    @ColumnInfo val token: String?,
    @ColumnInfo val visible: Boolean?,
    @ColumnInfo val private: Boolean?,
    @ColumnInfo val verified: Boolean?
){
    @PrimaryKey(autoGenerate = true) val id: Long? = null
}