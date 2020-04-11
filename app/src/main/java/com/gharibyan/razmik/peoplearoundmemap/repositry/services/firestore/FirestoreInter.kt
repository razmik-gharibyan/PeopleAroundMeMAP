package com.gharibyan.razmik.peoplearoundmemap.repositry.services.firestore

import androidx.lifecycle.LiveData
import com.gharibyan.razmik.peoplearoundmemap.repositry.models.firestore.FirestoreUserDAO
import com.google.android.gms.maps.GoogleMap

interface FirestoreInter {

    suspend fun addNewUser(firestoreDAO: FirestoreUserDAO): LiveData<String>

    suspend fun updateUser(firestoreDAO: FirestoreUserDAO, documentName: String)

    suspend fun findUser(userName: String): FirestoreUserDAO?

    suspend fun findUserDocument(userName: String): LiveData<String>

    suspend fun findAllUsers(): ArrayList<FirestoreUserDAO>

    suspend fun findAllUsersInBounds(map: GoogleMap): ArrayList<FirestoreUserDAO>

}