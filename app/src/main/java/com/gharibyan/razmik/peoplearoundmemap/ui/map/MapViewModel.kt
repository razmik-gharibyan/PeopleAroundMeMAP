package com.gharibyan.razmik.peoplearoundmemap.ui.map

import android.content.Context
import android.location.Location
import androidx.lifecycle.*
import com.gharibyan.razmik.peoplearoundmemap.repositry.editor.BoundProcessor
import com.gharibyan.razmik.peoplearoundmemap.repositry.models.firestore.CurrentFirestoreUserDAO
import com.gharibyan.razmik.peoplearoundmemap.repositry.models.firestore.FirestoreUserDAO
import com.gharibyan.razmik.peoplearoundmemap.repositry.models.markers.MarkerDAO
import com.gharibyan.razmik.peoplearoundmemap.repositry.models.room.RoomUser
import com.gharibyan.razmik.peoplearoundmemap.repositry.models.room.UsersDatabase
import com.gharibyan.razmik.peoplearoundmemap.repositry.models.singletons.Singletons
import com.gharibyan.razmik.peoplearoundmemap.repositry.services.firestore.FirestoreApi
import com.gharibyan.razmik.peoplearoundmemap.repositry.services.location.LocationApi
import com.gharibyan.razmik.peoplearoundmemap.repositry.services.marker.MarkerApi
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
    private var _currentUserDocumentLD = MutableLiveData<FirestoreUserDAO>()
    private var _newUserDocumentLD = MutableLiveData<String>()
    private var _markersLD = MutableLiveData<ArrayList<MarkerDAO>>()
    private var _usersFoundBySearchLD = MutableLiveData<ArrayList<FirestoreUserDAO>>()
    var locationUpdates: LiveData<Location> = _locationLD
    var allUsersList: LiveData<ArrayList<FirestoreUserDAO>> = _allUsersLD
    var inBoundUsersList: LiveData<ArrayList<FirestoreUserDAO>> = _inBoundUsersLD
    var currentUser: LiveData<FirestoreUserDAO> = _currentUserLD
    var currentUserDocument: LiveData<FirestoreUserDAO> = _currentUserDocumentLD
    var newUserDocumentId: LiveData<String> = _newUserDocumentLD
    var markersList: LiveData<ArrayList<MarkerDAO>> = _markersLD
    var usersFoundBySearch: LiveData<ArrayList<FirestoreUserDAO>> = _usersFoundBySearchLD

    // Initialization
    // -- Location
    private val locationApi = LocationApi(context)
    private val observer = object: Observer<Location>{
        override fun onChanged(location: Location?) {
            firestoreUserDAO.location = GeoPoint(location!!.latitude,location.longitude)
            _locationLD.value = location
        }
    }
    // -- Firebase
    private val firestoreApi = FirestoreApi()
    private val markerApi = MarkerApi()


    // Models
    private var instagramUserDAO = Singletons.instagramUserDAO
    private var firestoreUserDAO = Singletons.firestoreUserDAO
    private var currentFirestoreUserDAO = Singletons.currentFirestoreUserDAO

    fun initModels() {
        currentFirestoreUserDAO.userName = instagramUserDAO.userName
        currentFirestoreUserDAO.picture = instagramUserDAO.picture
        currentFirestoreUserDAO.followers = instagramUserDAO.followers
        currentFirestoreUserDAO.token = instagramUserDAO.token
        currentFirestoreUserDAO.isPrivate = instagramUserDAO.isPrivate
        currentFirestoreUserDAO.isVerified = instagramUserDAO.isVerified
    }

    fun startLocationUpdates() {
        CoroutineScope(Dispatchers.Main).launch {
            locationApi.startLocationUpdates()
            locationApi.locationUpdate.observeForever(observer)
        }
    }

    fun addNewUser(currentFirestoreUserDAO: CurrentFirestoreUserDAO) {
        // Check if user is already added in firestore
        // If not added , add new user, otherwise update existing user
        CoroutineScope(Dispatchers.Main).launch {
            firestoreApi.addNewUser(currentFirestoreUserDAO).observe(lifecycleOwner, Observer {
                _newUserDocumentLD.value = it
            })
        }
    }

    fun updateUser(currentFirestoreUserDAO: CurrentFirestoreUserDAO) {
        CoroutineScope(Dispatchers.IO).launch {
            firestoreApi.updateUser(currentFirestoreUserDAO)
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

    fun findUsersBySearch(userName: String) {
        CoroutineScope(Dispatchers.Main).launch {
            firestoreApi.findAllUsersMatchingSearch(userName)
        }
    }

    fun getFoundUserBySearch() {
        firestoreApi.usersFoundBySearch.observe(lifecycleOwner, Observer {
            _usersFoundBySearchLD.value = it
        })
    }

    fun markerOperations() {
        inBoundUsersList.observe(lifecycleOwner, Observer {
            val markerList: ArrayList<MarkerDAO> = ArrayList()
            if(it.isNotEmpty()) {
                CoroutineScope(Dispatchers.Main).launch {
                    for (firestoreUserDAO in it) {
                        if(firestoreUserDAO.isVisible!!) {
                            val moveCamera = firestoreUserDAO.documentId.equals(currentFirestoreUserDAO.documentId) &&
                                    !currentFirestoreUserDAO.isUserMarkerOnMap!!
                            markerList.add(markerApi.addMarker(firestoreUserDAO,moveCamera)!!)
                        }
                    }
                    _markersLD.value = markerList
                }
            }else{
                _markersLD.value = markerList
            }
        })
    }

    override fun onCleared() {
        locationApi.locationUpdate.removeObserver(observer)
        super.onCleared()
    }
}