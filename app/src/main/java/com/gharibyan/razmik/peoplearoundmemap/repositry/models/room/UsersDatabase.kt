package com.gharibyan.razmik.peoplearoundmemap.repositry.models.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = arrayOf(RoomUser::class),version = 1)
abstract class UsersDatabase: RoomDatabase() {

    abstract fun userDao(): RoomUserDao

    companion object: SingletonHolder<UsersDatabase,Context>({
        Room.databaseBuilder(it.applicationContext,UsersDatabase::class.java,"PAMMAP-database")
            .build()
    })
}