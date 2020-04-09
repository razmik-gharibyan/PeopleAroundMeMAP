package com.gharibyan.razmik.peoplearoundmemap.repositry.services.firestore

import com.gharibyan.razmik.peoplearoundmemap.repositry.models.firestore.FirestoreUserDAO

interface FirestoreInter {

    suspend fun addNewUser(firestoreDAO: FirestoreUserDAO)

    suspend fun updateUser(firestoreDAO: FirestoreUserDAO, documentName: String)

    suspend fun findUser(userName: String): FirestoreUserDAO?

    suspend fun findUserDocument(userName: String): String?

    suspend fun findAllUsers(): ArrayList<FirestoreUserDAO>

}