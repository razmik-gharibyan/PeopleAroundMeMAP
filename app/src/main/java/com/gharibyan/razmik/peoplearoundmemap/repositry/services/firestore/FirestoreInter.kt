package com.gharibyan.razmik.peoplearoundmemap.repositry.services.firestore

import androidx.lifecycle.LiveData
import com.gharibyan.razmik.peoplearoundmemap.repositry.models.firestore.CurrentFirestoreUserDAO
import com.gharibyan.razmik.peoplearoundmemap.repositry.models.firestore.FirestoreUserDAO
import com.google.android.gms.maps.GoogleMap

interface FirestoreInter {

    suspend fun addNewUser(currentFirestoreUserDAO: CurrentFirestoreUserDAO): LiveData<String>

    suspend fun updateUser(currentFirestoreUserDAO: CurrentFirestoreUserDAO)

    suspend fun findUser(userName: String): FirestoreUserDAO?

    suspend fun findUserDocument(userName: String): LiveData<FirestoreUserDAO>

    suspend fun findAllUsers(): ArrayList<FirestoreUserDAO>

    suspend fun findAllUsersInBounds(map: GoogleMap)

}