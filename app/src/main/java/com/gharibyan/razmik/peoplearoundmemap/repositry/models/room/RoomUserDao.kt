package com.gharibyan.razmik.peoplearoundmemap.repositry.models.room

import androidx.room.*

@Dao
interface RoomUserDao {

    @Query("SELECT * FROM users")
    suspend fun getAll(): List<RoomUser>

    @Insert
    suspend fun insertUser(user: RoomUser)

    @Update
    suspend fun updateUser(user: RoomUser)

    @Query("SELECT * FROM users WHERE users.documentid LIKE :documentId")
    suspend fun findUserByDocumentId(documentId: String): RoomUser

    @Query("DELETE FROM users WHERE users.documentid LIKE :documentId")
    suspend fun deleteUserByDocumentId(documentId: String)

    @Delete
    suspend fun deleteUser(user: RoomUser)

    @Query("DELETE FROM users")
    suspend fun deleteAll()

}