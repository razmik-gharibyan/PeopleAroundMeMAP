package com.gharibyan.razmik.peoplearoundmemap.ui.map

import android.content.Context
import android.location.Location
import androidx.lifecycle.*
import com.gharibyan.razmik.peoplearoundmemap.repositry.editor.BoundProcessor
import com.gharibyan.razmik.peoplearoundmemap.repositry.models.firestore.FirestoreUserDAO
import com.gharibyan.razmik.peoplearoundmemap.repositry.models.singletons.Singletons
import com.gharibyan.razmik.peoplearoundmemap.repositry.services.firestore.FirestoreApi
import com.gharibyan.razmik.peoplearoundmemap.repositry.services.location.LocationApi
import com.google.android.gms.maps.GoogleMap
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MapViewModel(val context: Context, val lifecycleOwner: LifecycleOwner) : ViewModel() {

    // LiveData
    private var _locationLD = MutableLiveData<Location>()
    private var _allUsersLD = MutableLiveData<ArrayList<FirestoreUserDAO>>()
    private var _inBoundUsersLD = MutableLiveData<ArrayList<FirestoreUserDAO>>()
    private var _currentUserLD = MutableLiveData<FirestoreUserDAO>()
    private var _currentUserDocumentLD = MutableLiveData<String>()
    private var _newUserDocumentLD = MutableLiveData<String>()
    var locationUpdates: LiveData<Location> = _locationLD
    var allUsersList: LiveData<ArrayList<FirestoreUserDAO>> = _allUsersLD
    var inBoundUsersList: LiveData<ArrayList<FirestoreUserDAO>> = _inBoundUsersLD
    var currentUser: LiveData<FirestoreUserDAO> = _currentUserLD
    var currentUserDocument: LiveData<String> = _currentUserDocumentLD
    var newUserDocumentId: LiveData<String> = _newUserDocumentLD

    // Initialization
    private val locationApi = LocationApi(context)
    private val firestoreApi = FirestoreApi()
    private val observer = object: Observer<Location>{
        override fun onChanged(location: Location?) {
            firestoreUserDAO.location = GeoPoint(location!!.latitude,location.longitude)
            _locationLD.value = location
        }
    }

    // Models
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

    fun addNewUser(firestoreDAO: FirestoreUserDAO) {
        // Check if user is already added in firestore
        // If not added , add new user, otherwise update existing user
        CoroutineScope(Dispatchers.Main).launch {
            firestoreApi.addNewUser(firestoreDAO).observe(lifecycleOwner, Observer {
                _newUserDocumentLD.value = it
            })
        }
    }

    fun updateUser(firestoreDAO: FirestoreUserDAO, documentName: String) {
        CoroutineScope(Dispatchers.IO).launch {
            firestoreApi.updateUser(firestoreDAO, documentName)
        }
    }

    fun findAllUsersInBounds(map: GoogleMap) {
        CoroutineScope(Dispatchers.Main).launch {
            firestoreApi.findAllUsersInBounds(map)
            firestoreApi.usersInBoundsList.observe(lifecycleOwner, Observer {
                _inBoundUsersLD.value = it
            })
        }
    }

    fun findUserByUsername(userName: String) {
        CoroutineScope(Dispatchers.Main).launch {
            _currentUserLD.value = firestoreApi.findUser(userName)
        }
    }

    fun findUserDocument(userName: String) {
        CoroutineScope(Dispatchers.Main).launch {
            firestoreApi.findUserDocument(userName).observe(lifecycleOwner, Observer {
                _currentUserDocumentLD.value = it
            })
        }
    }

    override fun onCleared() {
        locationApi.locationUpdate.removeObserver(observer)
        super.onCleared()
    }
}