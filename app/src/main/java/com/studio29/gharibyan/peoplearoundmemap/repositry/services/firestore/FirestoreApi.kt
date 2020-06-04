package com.studio29.gharibyan.peoplearoundmemap.repositry.services.firestore

import android.os.Handler
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.studio29.gharibyan.peoplearoundmemap.repositry.models.firestore.CurrentFirestoreUserDAO
import com.studio29.gharibyan.peoplearoundmemap.repositry.models.firestore.FirestoreUserDAO
import com.studio29.gharibyan.peoplearoundmemap.repositry.models.singletons.Singletons
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLngBounds
import com.google.firebase.firestore.GeoPoint
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
    private val handler = Handler()

    // Models
    private var firestoreUserDAO = Singletons.firestoreUserDAO

    // LiveData
    private var _currentDocumentIDLD = MutableLiveData<FirestoreUserDAO>()
    private var _newDocumentIDLD = MutableLiveData<String>()
    private var _usersInBoundsLD = MutableLiveData<ArrayList<FirestoreUserDAO>>()
    private var _usersFoundBySearchLD = MutableLiveData<ArrayList<FirestoreUserDAO>>()
    var currentDocumentId: LiveData<FirestoreUserDAO> = _currentDocumentIDLD
    var newDocumentId: LiveData<String> = _newDocumentIDLD
    var usersInBoundsList: LiveData<ArrayList<FirestoreUserDAO>> = _usersInBoundsLD
    var usersFoundBySearch: LiveData<ArrayList<FirestoreUserDAO>> = _usersFoundBySearchLD

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

    override suspend fun addNewUserWithUID(currentFirestoreUserDAO: CurrentFirestoreUserDAO, uid: String) {
        db.collection(collectionName)
            .document(uid)
            .set(currentFirestoreUserDAO)
            .addOnCompleteListener {
                if(it.isSuccessful) {
                    Log.d(TAG, "Added new document with current uid to firestore")
                }
            }
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

    override suspend fun findUserWithDocumentId(documentId: String): LiveData<FirestoreUserDAO> {
        db.collection(collectionName).document(documentId)
            .get()
            .addOnSuccessListener {
                val currentUserDocument = it.toObject<FirestoreUserDAO>()
                _currentDocumentIDLD.value = currentUserDocument
            }
            .addOnFailureListener {
                Log.d(TAG, "Error loading user")
            }
        return currentDocumentId
    }

    override suspend fun findAllUsersInBounds(map: GoogleMap) {
        val runnable = object: Runnable {
            override fun run() {
                val bounds: LatLngBounds = map.projection.visibleRegion.latLngBounds
                val neLongitude = bounds.northeast.longitude
                val neLatitude = bounds.northeast.latitude
                val swLongitude = bounds.southwest.longitude
                val swLatitude = bounds.southwest.latitude
                val lowGeoPoint = GeoPoint(swLatitude,swLongitude)
                val highGeoPoint = GeoPoint(neLatitude,neLongitude)

                db.collection(collectionName)
                    .whereGreaterThanOrEqualTo("location",lowGeoPoint)
                    .whereLessThanOrEqualTo("location",highGeoPoint)
                    .get()
                    .addOnSuccessListener {
                        val usersInBoundsArrayList: ArrayList<FirestoreUserDAO> = ArrayList()
                        for(document in it) {
                            val firestoreUserDAO = document.toObject(FirestoreUserDAO::class.java)
                            firestoreUserDAO.documentId = document.id
                            usersInBoundsArrayList.add(firestoreUserDAO)
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
            .whereEqualTo("userName", userName)
            .get()
            .addOnSuccessListener {
                for(document in it) {
                    val currentUserDocument = document.toObject<FirestoreUserDAO>()
                    currentDocumentID = currentUserDocument
                }
                _currentDocumentIDLD.value = currentDocumentID
            }
            .addOnFailureListener {
                Log.d(TAG, "Error reading documents", it)
            }
        return currentDocumentId
    }

    override suspend fun findAllUsersMatchingSearch(nameText: String): LiveData<ArrayList<FirestoreUserDAO>> {
       db.collection(collectionName)
           //.whereEqualTo("userName",nameText)
           .orderBy("userName")
           .startAt(nameText)
           .endAt(nameText + "\uf8ff")
           .get()
           .addOnSuccessListener {
               val searchedUserList = ArrayList<FirestoreUserDAO>()
               if(it != null && !it.isEmpty) {
                   for(document in it) {
                       val currentUserDocument = document.toObject<FirestoreUserDAO>()
                       searchedUserList.add(currentUserDocument)
                   }
               }
               _usersFoundBySearchLD.value = searchedUserList
           }
        return usersFoundBySearch
    }
}