package com.studio29.gharibyan.peoplearoundmemap.ui.map

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.studio29.gharibyan.peoplearoundmemap.MainActivity
import com.studio29.gharibyan.peoplearoundmemap.R
import com.studio29.gharibyan.peoplearoundmemap.repositry.models.firestore.FirestoreUserDAO
import com.studio29.gharibyan.peoplearoundmemap.repositry.models.markers.MarkerDAO
import com.studio29.gharibyan.peoplearoundmemap.repositry.models.markers.MarkerWithDocumentId
import com.studio29.gharibyan.peoplearoundmemap.repositry.models.markers.clone
import com.studio29.gharibyan.peoplearoundmemap.repositry.models.room.RoomUser
import com.studio29.gharibyan.peoplearoundmemap.repositry.models.singletons.Singletons
import com.studio29.gharibyan.peoplearoundmemap.repositry.services.marker.markerCluster.MarkerClusterRenderer
import com.studio29.gharibyan.peoplearoundmemap.repositry.services.marker.markerCluster.MarkerItem
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.GeoPoint
import com.google.maps.android.clustering.ClusterManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MapFragment : Fragment() {

    companion object{
        @JvmStatic var shouldCluster_zoom: Boolean = false;
    }

    // Constants
    private val TAG = javaClass.name

    // Initialization
    private lateinit var mapViewModel: MapViewModel
    private var sharedPref: SharedPreferences? = null

    // MarkerCluster
    private lateinit var markerClusterRenderer: MarkerClusterRenderer<MarkerItem>
    private lateinit var clusterManager: ClusterManager<MarkerItem>

    // Views
    private lateinit var visibilityButton: Button
    private lateinit var mapView: MapView

    // Vars global
    private var currentFirestoreUserDAO = Singletons.currentFirestoreUserDAO
    private var userVisibility: Boolean = false
    private var userDocumentId: String? = null
    private var map: GoogleMap? = null
    private var markerList = ArrayList<MarkerWithDocumentId>()
    private var firstTimeCameraMove = false
    private var clusterManagerListCopy = ArrayList<MarkerItem>()
    private var allUsersList = ArrayList<FirestoreUserDAO>()
    private var fin = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_map, container, false)

        mapViewModel = (activity as MainActivity).mapViewModel
        sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)

        // Views
        visibilityButton = view.findViewById(R.id.visibility_button)
        mapView = view.findViewById(R.id.map)
        mapView.onCreate(savedInstanceState)
        try {
            MapsInitializer.initialize(activity)
        }catch (e: GooglePlayServicesNotAvailableException) {
            e.printStackTrace()
        }

        // Initialize model classes with values
        mapViewModel.initModels()
        // Initialize map
        initMap()
        currentFirestoreUserDAO.isVisible = userVisibility
        currentFirestoreUserDAO.isActive = true
        mainOperations()

        return view
    }

    private fun mainOperations() {
        // Check user is existing in firestore or not
        mapViewModel.findUserDocument(currentFirestoreUserDAO.userName!!)
        listenIfUserIsAdded()
        listenToNewAddedUser()
        // Start location updates
        mapViewModel.startLocationUpdates()
        listenToLocationUpdates()
        //Buttons
        listenVisibleButtonChanges()
        listenToSearchFragmentFoundUser()
    }

    private fun initMap() {
        mapView.getMapAsync {
            if(map == null) map = it
            //map!!.mapType = GoogleMap.MAP_TYPE_HYBRID
            // Users list operations
            mapViewModel.findAllUsersInBounds(map!!)
            mapViewModel.markerOperations()
            CoroutineScope(Dispatchers.Main).launch {
                listenToMarkersChanges()
            }
            // Marker Cluster Initialization
            clusterManager = ClusterManager(this.context,map)
            markerClusterRenderer = MarkerClusterRenderer(this.context!!,map,clusterManager)
            markerClusterRenderer.setContext(this.context!!)
            clusterManager.renderer = markerClusterRenderer
            listenToMarkerClick()
            map!!.setOnCameraIdleListener {
                shouldCluster_zoom = map!!.cameraPosition.zoom < 18
                clusterManager.onCameraIdle()
            }
        }
    }


    private fun listenToLocationUpdates() {
        mapViewModel.locationUpdates.observe(viewLifecycleOwner, Observer {
            if(it != null) {
                currentFirestoreUserDAO.location = GeoPoint(it.latitude,it.longitude)
                CoroutineScope(Dispatchers.IO).launch {
                    with (sharedPref!!.edit()) {
                        putString(getString(R.string.last_longitude), it.longitude.toString())
                        putString(getString(R.string.last_latitude),it.latitude.toString())
                        apply()
                    }
                }
                if(userDocumentId != null) {
                    mapViewModel.updateUser(currentFirestoreUserDAO)
                }
            }
        })
    }

    private fun listenIfUserIsAdded() {
        mapViewModel.currentUserDocument.observe(viewLifecycleOwner, Observer {
            userDocumentId = it.documentId
            currentFirestoreUserDAO.documentId = it.documentId
            currentFirestoreUserDAO.isVisible = it.isVisible
            userVisibility = isVisible
            if(it == null || !currentFirestoreUserDAO.isVisible!!) {
                // Enter this case if users isVisible is on false, or user is logged first time
                if(it == null) {
                    // First time logged in
                    mapViewModel.addNewUser(currentFirestoreUserDAO)
                }else{
                    // isVisible is false
                    val latitude: Double = sharedPref!!.getString(getString(R.string.last_latitude),"-73.9537724")!!.toDouble()
                    val longitude: Double = sharedPref!!.getString(getString(R.string.last_longitude),"40.790124")!!.toDouble()
                    currentFirestoreUserDAO.location = GeoPoint(latitude,longitude)
                    map!!.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(latitude,longitude),19F))
                    mapViewModel.updateUser(currentFirestoreUserDAO)
                }
                visibilityChanger(false)
            }else{
                // Enter this case if user is visible
                mapViewModel.updateUser(currentFirestoreUserDAO)
                visibilityChanger(true)
            }
        })
    }

    private fun listenToNewAddedUser() {
        mapViewModel.newUserDocumentId.observe(viewLifecycleOwner, Observer {
            userDocumentId = it
            currentFirestoreUserDAO.documentId = it
            mapViewModel.updateUser(currentFirestoreUserDAO)
        })
    }

    private fun visibilityChanger(visible: Boolean) {
        if(visible) {
            Toast.makeText(context,"You are now in visible mode, other people can see you on map",Toast.LENGTH_LONG).show()
            visibilityButton.setBackgroundResource(R.drawable.ic_visibility_light_purple_24dp)
            userVisibility = true
            currentFirestoreUserDAO.isVisible = true
        }else{
            Toast.makeText(context,"You are now in invisible mode, other people can't see you on map.",Toast.LENGTH_LONG).show()
            visibilityButton.setBackgroundResource(R.drawable.ic_visibility_off_light_purple_24dp)
            userVisibility = false
            currentFirestoreUserDAO.isVisible = false
        }
    }

    private fun listenVisibleButtonChanges() {
        visibilityButton.setOnClickListener {
            if(userVisibility) {
                visibilityChanger(false)
            }else{
                visibilityChanger(true)
            }
        }
    }

    private fun listenToMarkersChanges() {
        mapViewModel.markersList.observe(viewLifecycleOwner, Observer { list ->
            if(fin) {
                fin = false
                CoroutineScope(Dispatchers.Main).launch {
                    val markerListCopy = ArrayList<MarkerWithDocumentId>()
                    markerListCopy.addAll(markerList.map { marker -> marker.clone() })

                    withContext(Dispatchers.Default) {
                        var minFollowerUser: FirestoreUserDAO?
                        list.forEach{ firestoreUserDAO ->
                            minFollowerUser = minFollowerCheck()
                            // Check if user in allUserList is in ListInBounds
                            checkAllUserList(list)
                            // Add or Replace current user, instead of minimum follower user
                            if(!allUsersList.contains(firestoreUserDAO)) {
                                if(minFollowerUser == null || allUsersList.size < 1000) {
                                    // This case means there are no users in allUserList or allUserList have less users then 1000
                                    allUsersList.add(firestoreUserDAO)
                                }else{
                                    if(firestoreUserDAO.followers!! >= minFollowerUser!!.followers!!) {
                                        // Remove min follower user from allUserList,and add current user instead of it
                                        // then update min follower user
                                        allUsersList.remove(minFollowerUser as FirestoreUserDAO)
                                        allUsersList.add(firestoreUserDAO)
                                    }
                                }
                            }
                        }
                        allUsersList.forEach { firestoreUserDAO ->
                            if (markerList.isNotEmpty()) {
                                kotlin.run loop@{
                                    markerList.forEachIndexed { index, currentMarker ->
                                        if (firestoreUserDAO.documentId.equals(currentMarker.documentId)) {
                                            if (currentMarker.firestoreUserDAO!! != firestoreUserDAO) {
                                                // If marker is still in bounds but changed it's location or personal info (update marker)
                                                //clusterManager.removeItem(currentMarker.markerItem)
                                                //markerListCopy.removeAt(index)
                                                //addMarkerToCluster(firestoreUserDAO,markerListCopy)
                                                for (marker in clusterManagerListCopy) {
                                                    if (firestoreUserDAO.documentId == marker.documentId) {
                                                        val newPosition = LatLng(firestoreUserDAO.location!!.latitude, firestoreUserDAO.location!!.longitude)
                                                        withContext(Dispatchers.Main) { markerClusterRenderer.updateMarker(marker, newPosition) }
                                                        break
                                                    }
                                                }
                                            }
                                            return@loop // Break for each loop if found equal marker with same document id
                                        } else {
                                            if (index == markerList.size - 1) {
                                                // If loop is finished and there were no marker on map with this document id, then add marker
                                                val moveCam = currentFirestoreUserDAO.documentId == firestoreUserDAO.documentId
                                                val marker = mapViewModel.addMarker(firestoreUserDAO, moveCam)
                                                withContext(Dispatchers.Main) {addMarkerToCluster(marker!!, markerListCopy)}
                                            }
                                        }
                                    }
                                }
                            } else {
                                // Add marker if there is no marker on map
                                val moveCam = currentFirestoreUserDAO.documentId == firestoreUserDAO.documentId
                                val marker = mapViewModel.addMarker(firestoreUserDAO, moveCam)
                                withContext(Dispatchers.Main) {addMarkerToCluster(marker!!, markerListCopy)}
                            }
                        }
                    }
                    compareInvalidMarkers(list, markerListCopy)
                }
            }
        })
    }

    private suspend fun compareInvalidMarkers(initialList: List<FirestoreUserDAO>, markerListCopy: MutableList<MarkerWithDocumentId>) {
        markerList.clear()
        markerList.addAll(markerListCopy) // Rewrite new data into original markerList
        if(markerList.isNotEmpty()) {
            kotlin.run markerList@{
                var counter = 0
                markerList.forEachIndexed { index, markerWithDocumentId ->
                    if (initialList.isEmpty()) {
                        // If there are no markers in bounds received from inBoundUsers LiveData
                        // but there are active markers on map, then remove all active markers from map
                        withContext(Dispatchers.Main) { clusterManager.clearItems() }
                        clusterManagerListCopy.clear()
                        markerListCopy.clear()
                        mapViewModel.inBoundArrayList.clear()
                        return@markerList
                    } else {
                        kotlin.run lit@{
                            initialList.forEachIndexed { smallIndex, markerDAO ->
                                if (markerWithDocumentId.documentId.equals(markerDAO.documentId)) {
                                    return@lit
                                } else {
                                    if (smallIndex == initialList.size - 1) {
                                        // If the marker that is active on map is out of bounds or become
                                        // invisible , then remove that marker from map
                                        withContext(Dispatchers.Main) {
                                            clusterManager.removeItem(clusterManagerListCopy[index - counter])}
                                        markerListCopy.removeAt(index - counter)
                                        clusterManagerListCopy.removeAt(index - counter)
                                        mapViewModel.inBoundArrayList.removeAt(index - counter)
                                        counter++
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        markerList.clear()
        markerList.addAll(markerListCopy) // Rewrite new data into original markerList
        withContext(Dispatchers.Main) { clusterManager.cluster()}
        fin = true
    }

    private fun addMarkerToCluster(markerDAO: MarkerDAO, markerListCopy: ArrayList<MarkerWithDocumentId>) {
        val markerItem = MarkerItem(markerDAO.latLng!!,"Tap to open profile",
            markerDAO.markerOptions!!.title,markerDAO.markerOptions!!.icon, markerDAO.firestoreUserDAO!!.followers!!,
            markerDAO.firestoreUserDAO!!.userName!!,markerDAO.documentId!!,markerDAO.markerBitmap!!)
        mapViewModel.inBoundArrayList.add(markerDAO)
        clusterManager.addItem(markerItem)
        clusterManagerListCopy.add(markerItem)
        val markerWithDocumentId = MarkerWithDocumentId()
        markerWithDocumentId.markerItem = markerItem
        markerWithDocumentId.documentId = markerDAO.documentId
        markerWithDocumentId.markerOptions = markerDAO.markerOptions
        markerWithDocumentId.firestoreUserDAO = markerDAO.firestoreUserDAO
        markerListCopy.add(markerWithDocumentId)
        if (markerDAO.moveCamera!! && !firstTimeCameraMove) {
                map!!.moveCamera(CameraUpdateFactory.newLatLngZoom(markerDAO.latLng, 19F))
                firstTimeCameraMove = true
        }
    }

    private fun minFollowerCheck(): FirestoreUserDAO? {
        var minFollowerUser: FirestoreUserDAO? = null
        var min: Long = 0
        if(allUsersList.size >= 1) {
            min = allUsersList[0].followers!!
        }
        for (user in allUsersList) {
            if(user.followers!! <= min) {
                min = user.followers!!
                minFollowerUser = user
            }
        }
        return minFollowerUser
    }

    private fun checkAllUserList(list: List<FirestoreUserDAO>) {
        val allUserListCopy = ArrayList<FirestoreUserDAO>()
        allUserListCopy.addAll(allUsersList)
        for(user in allUserListCopy) {
            if(!list.contains(user)) {
                allUsersList.remove(user)
            }
        }
    }

    private fun changeFireStoreUserToRoomUser(firestoreUserDAO: FirestoreUserDAO): RoomUser {
        val roomUser = RoomUser()
        roomUser.documentid = firestoreUserDAO.documentId
        roomUser.username = firestoreUserDAO.userName
        roomUser.picture = firestoreUserDAO.picture
        roomUser.followers = firestoreUserDAO.followers
        roomUser.latitude = firestoreUserDAO.location!!.latitude
        roomUser.longitude = firestoreUserDAO.location!!.longitude
        roomUser.token = firestoreUserDAO.token
        roomUser.private = firestoreUserDAO.isPrivate
        roomUser.visible = firestoreUserDAO.isVisible
        roomUser.verified = firestoreUserDAO.isVerified
        return roomUser
    }

    private fun listenToSearchFragmentFoundUser() {
        mapViewModel.searchedUserToMap.observe(viewLifecycleOwner, Observer {
            map!!.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(it.location!!.latitude,it.location!!.longitude),
                    19F
                )
            )
        })
    }

    private fun listenToMarkerClick() {
        clusterManager.setOnClusterItemInfoWindowClickListener {
            val endpoint = it.title.indexOf(" :")
            openInstagramApp(it.title.substring(0,endpoint))
        }
    }

    private fun openInstagramApp(username: String) {
        val uri: Uri = Uri.parse("http://instagram.com/_u/$username")
        val likeIng = Intent(Intent.ACTION_VIEW, uri)
        likeIng.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        likeIng.setPackage("com.instagram.android")

        try {
            startActivity(likeIng)
        } catch (e: ActivityNotFoundException) {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("http://instagram.com/$username")
                ).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        }
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

}
