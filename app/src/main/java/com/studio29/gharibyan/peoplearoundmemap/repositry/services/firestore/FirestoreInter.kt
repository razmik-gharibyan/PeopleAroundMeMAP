package com.studio29.gharibyan.peoplearoundmemap.repositry.services.firestore

import androidx.lifecycle.LiveData
import com.studio29.gharibyan.peoplearoundmemap.repositry.models.firestore.CurrentFirestoreUserDAO
import com.studio29.gharibyan.peoplearoundmemap.repositry.models.firestore.FirestoreUserDAO
import com.google.android.gms.maps.GoogleMap

interface FirestoreInter {

    suspend fun addNewUser(currentFirestoreUserDAO: CurrentFirestoreUserDAO): LiveData<String>

    suspend fun addNewUserWithUID(currentFirestoreUserDAO: CurrentFirestoreUserDAO, uid: String)

    suspend fun updateUser(currentFirestoreUserDAO: CurrentFirestoreUserDAO)

    suspend fun findUser(userName: String): FirestoreUserDAO?

    suspend fun findUserWithDocumentId(documentId: String): LiveData<FirestoreUserDAO>

    suspend fun findUserDocument(userName: String): LiveData<FirestoreUserDAO>

    suspend fun findAllUsers(): ArrayList<FirestoreUserDAO>

    suspend fun findAllUsersInBounds(map: GoogleMap)

    suspend fun findAllUsersMatchingSearch(nameText: String): LiveData<ArrayList<FirestoreUserDAO>>

}