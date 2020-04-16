package com.gharibyan.razmik.peoplearoundmemap.repositry.services.firestore

import android.os.Handler
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.gharibyan.razmik.peoplearoundmemap.repositry.editor.BoundProcessor
import com.gharibyan.razmik.peoplearoundmemap.repositry.models.firestore.CurrentFirestoreUserDAO
import com.gharibyan.razmik.peoplearoundmemap.repositry.models.firestore.FirestoreUserDAO
import com.gharibyan.razmik.peoplearoundmemap.repositry.models.singletons.Singletons
import com.google.android.gms.maps.GoogleMap
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class FirestoreApi: FirestoreInter {

    // Constants
    private val TAG: String = javaClass.name
    private val collectionName: String = "users"
    private val MARKER_UPDATE_INTERVAL: Long = 2000

    // Initialization
    private val db = Firebase.firestore
    private val boundProcessor = BoundProcessor()
    private val handler = Handler()

    // Models
    private var firestoreUserDAO = Singletons.firestoreUserDAO

    // LiveData
    private var _currentDocumentIDLD = MutableLiveData<FirestoreUserDAO>()
    private var _newDocumentIDLD = MutableLiveData<String>()
    private var _usersInBoundsLD = MutableLiveData<ArrayList<FirestoreUserDAO>>()
    var currentDocumentId: LiveData<FirestoreUserDAO> = _currentDocumentIDLD
    var newDocumentId: LiveData<String> = _newDocumentIDLD
    var usersInBoundsList: LiveData<ArrayList<FirestoreUserDAO>> = _usersInBoundsLD

    // Variables
    private lateinit var documentList: ArrayList<FirestoreUserDAO>

    override suspend fun addNewUser(currentFirestoreUserDAO: CurrentFirestoreUserDAO): LiveData<String> {
        db.collection(collectionName)
            .add(currentFirestoreUserDAO)
            .addOnSuccessListener {
                _newDocumentIDLD.value = it.id
                Log.d(TAG,"Document with id: $it successfully created")
            }
            .addOnFailureListener {
                Log.d(TAG, "Error adding document", it)
            }
        return newDocumentId
    }

    override suspend fun updateUser(currentFirestoreUserDAO: CurrentFirestoreUserDAO) {
        db.collection(collectionName).document(currentFirestoreUserDAO.documentId!!)
            .set(currentFirestoreUserDAO)
            .addOnSuccessListener {
                Log.d(TAG, "Document with id: ${currentFirestoreUserDAO.documentId} successfully updated")
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
                    val firestoreUserDAO = document.toObject(FirestoreUserDAO::class.java)
                    firestoreUserDAO.documentId = document.id
                    documentList.add(firestoreUserDAO)
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

    override suspend fun findAllUsersInBounds(map: GoogleMap) {
        val runnable = object: Runnable {
            override fun run() {
                db.collection(collectionName)
                    .get()
                    .addOnSuccessListener {
                        val usersInBoundsArrayList: ArrayList<FirestoreUserDAO> = ArrayList()
                        for(document in it) {
                            val firestoreUserDAO = document.toObject(FirestoreUserDAO::class.java)
                            firestoreUserDAO.documentId = document.id
                            if(boundProcessor.isUserInBounds(map,firestoreUserDAO.location!!)) {
                                usersInBoundsArrayList.add(firestoreUserDAO)
                            }
                        }
                        _usersInBoundsLD.value = usersInBoundsArrayList
                    }
                    .addOnFailureListener {
                        Log.d(TAG, "Error reading documents", it)
                    }
                handler.postDelayed(this,MARKER_UPDATE_INTERVAL)
            }
        }
        handler.post(runnable)
    }

    override suspend fun findUserDocument(userName: String): LiveData<FirestoreUserDAO> {
        var currentDocumentID: FirestoreUserDAO? = null
        db.collection(collectionName)
            .get()
            .addOnSuccessListener {
                for(document in it) {
                    val currentUserDocument = document.toObject<FirestoreUserDAO>()
                    if(currentUserDocument.userName.equals(userName)) {
                        currentDocumentID = currentUserDocument
                    }
                }
                _currentDocumentIDLD.value = currentDocumentID
            }
            .addOnFailureListener {
                Log.d(TAG, "Error reading documents", it)
            }
        return currentDocumentId
    }

}