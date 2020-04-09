package com.gharibyan.razmik.peoplearoundmemap.repositry.services.firestore

import android.util.Log
import com.gharibyan.razmik.peoplearoundmemap.repositry.models.firestore.FirestoreUserDAO
import com.gharibyan.razmik.peoplearoundmemap.repositry.models.singletons.Singletons
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class FirestoreApi: FirestoreInter {

    // Constants
    private val TAG: String = javaClass.name
    private val collectionName: String = "users"

    // Initialization
    private val db = Firebase.firestore
    private var firestoreUserDAO = Singletons.firestoreUserDAO

    // Variables
    private lateinit var documentList: ArrayList<FirestoreUserDAO>

    override suspend fun addNewUser(firestoreDAO: FirestoreUserDAO) {
        db.collection(collectionName)
            .add(firestoreDAO)
            .addOnSuccessListener {
                Log.d(TAG,"Document with id: $it successfully created")
            }
            .addOnFailureListener {
                Log.d(TAG, "Error adding document", it)
            }
    }

    override suspend fun updateUser(firestoreDAO: FirestoreUserDAO, documentName: String) {
        db.collection(collectionName).document(documentName)
            .set(firestoreDAO)
            .addOnSuccessListener {
                Log.d(TAG, "Document with id: $it successfully updated")
            }
            .addOnFailureListener {
                Log.d(TAG, "Error updating document", it)
            }
    }

    override suspend fun findAllUsers(): ArrayList<FirestoreUserDAO> {
        db.collection(collectionName)
            .get()
            .addOnSuccessListener {
                for(document in it) {
                    documentList.add(document.toObject())
                }
            }
            .addOnFailureListener {
                Log.d(TAG, "Error reading documents", it)
            }
        return documentList
    }

    override suspend fun findUser(userName: String): FirestoreUserDAO? {
        if(documentList.isEmpty()) {
            findAllUsers()
        }
        for(document in documentList) {
            if(userName.equals(document.userName)) {
                firestoreUserDAO = document
                return document
            }
        }
        // Return null if userName not found in collection
        return null
    }

    override suspend fun findUserDocument(userName: String): String? {
        var currentDocumentID: String? = null
        db.collection(collectionName)
            .get()
            .addOnSuccessListener {
                for(document in it) {
                    val currentUserDocument = document.toObject<FirestoreUserDAO>()
                    if(currentUserDocument.userName.equals(userName)) {
                        currentDocumentID = document.id
                    }
                }
            }
            .addOnFailureListener {
                Log.d(TAG, "Error reading documents", it)
            }
        return currentDocumentID
    }

}