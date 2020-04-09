package com.gharibyan.razmik.peoplearoundmemap.ui.map

import android.content.Context
import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.gharibyan.razmik.peoplearoundmemap.repositry.models.firestore.FirestoreUserDAO
import com.gharibyan.razmik.peoplearoundmemap.repositry.models.singletons.Singletons
import com.gharibyan.razmik.peoplearoundmemap.repositry.services.firestore.FirestoreApi
import com.gharibyan.razmik.peoplearoundmemap.repositry.services.location.LocationApi
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MapViewModel(val context: Context) : ViewModel() {

    // LiveData
    private var _locationLD = MutableLiveData<Location>()
    private var _allUsersLD = MutableLiveData<ArrayList<FirestoreUserDAO>>()
    private var _currentUserLD = MutableLiveData<FirestoreUserDAO>()
    private var _currentUserDocumentLD = MutableLiveData<String>()
    var locationUpdates: LiveData<Location> = _locationLD
    var allUsersList: LiveData<ArrayList<FirestoreUserDAO>> = _allUsersLD
    var currentUser: LiveData<FirestoreUserDAO> = _currentUserLD
    var currentUserDocument: LiveData<String> = _currentUserDocumentLD

    // Initialization
    private val locationApi = LocationApi(context)
    private val firestoreApi = FirestoreApi()
    private val observer = object: Observer<Location>{
        override fun onChanged(location: Location?) {
            firestoreUserDAO.location = GeoPoint(location!!.latitude,location.longitude)
            _locationLD.value = location
        }
    }

    private var instagramUserDAO = Singletons.instagramUserDAO
    private var firestoreUserDAO = Singletons.firestoreUserDAO

    fun initModels() {
        firestoreUserDAO.userName = instagramUserDAO.userName
        firestoreUserDAO.picture = instagramUserDAO.picture
        firestoreUserDAO.followers = instagramUserDAO.followers
        firestoreUserDAO.token = instagramUserDAO.token
        firestoreUserDAO.isPrivate = instagramUserDAO.isPrivate
        firestoreUserDAO.isVerified = instagramUserDAO.isVerified
    }

    fun startLocationUpdates() {
        CoroutineScope(Dispatchers.Main).launch {
            locationApi.startLocationUpdates()
            locationApi.locationUpdate.observeForever(observer)
        }
    }

    fun addOrUpdateUser(firestoreDAO: FirestoreUserDAO) {
        // Check if user is already added in firestore
        // If not added , add new user, otherwise update existing user
        CoroutineScope(Dispatchers.IO).launch {
            val currentDocumentId = firestoreApi.findUserDocument(firestoreDAO.userName!!)
            if(currentDocumentId == null) {
                firestoreApi.addNewUser(firestoreDAO)
            }else{
                updateUser(firestoreDAO,currentDocumentId)
            }
        }
    }

    fun updateUser(firestoreDAO: FirestoreUserDAO, documentName: String) {
        CoroutineScope(Dispatchers.IO).launch {
            firestoreApi.updateUser(firestoreDAO, documentName)
        }
    }

    fun findAllUsers() {
        CoroutineScope(Dispatchers.Main).launch {
            _allUsersLD.value = firestoreApi.findAllUsers()
        }
    }

    fun findUserByUsername(userName: String) {
        CoroutineScope(Dispatchers.Main).launch {
            _currentUserLD.value = firestoreApi.findUser(userName)
        }
    }

    fun findUserDocument(userName: String) {
        CoroutineScope(Dispatchers.Main).launch {
            _currentUserDocumentLD.value = firestoreApi.findUserDocument(userName)
        }
    }

    override fun onCleared() {
        locationApi.locationUpdate.removeObserver(observer)
        super.onCleared()
    }
}