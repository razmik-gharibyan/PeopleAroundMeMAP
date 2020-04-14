package com.gharibyan.razmik.peoplearoundmemap.ui.map

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.get
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.gharibyan.razmik.peoplearoundmemap.R
import com.gharibyan.razmik.peoplearoundmemap.repositry.models.firestore.FirestoreUserDAO
import com.gharibyan.razmik.peoplearoundmemap.repositry.models.markers.MarkerDAO
import com.gharibyan.razmik.peoplearoundmemap.repositry.models.markers.MarkerWithDocumentId
import com.gharibyan.razmik.peoplearoundmemap.repositry.models.singletons.Singletons
import com.gharibyan.razmik.peoplearoundmemap.ui.CustomViewModelFactory
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.Marker
import com.google.firebase.firestore.GeoPoint
import kotlinx.android.synthetic.main.fragment_map.view.*

class MapFragment : Fragment() {

    // Constants
    private val TAG: String = javaClass.name

    // Initialization
    private lateinit var mapViewModel: MapViewModel
    private lateinit var customViewModelFactory: CustomViewModelFactory

    // Views
    private lateinit var headerLayout: LinearLayout
    private lateinit var headerTextView: TextView
    private lateinit var visibilityButton: Button
    private lateinit var mapView: MapView

    // Vars global
    private var firestoreUserDAO = Singletons.firestoreUserDAO
    private var currentFirestoreUserDAO = Singletons.currentFirestoreUserDAO
    private var userVisibility: Boolean = false
    private var userDocumentId: String? = null
    private var map: GoogleMap? = null
    private var markerList = ArrayList<MarkerWithDocumentId>()

    private lateinit var usersInBoundList: ArrayList<FirestoreUserDAO>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        customViewModelFactory = CustomViewModelFactory(activity?.baseContext!!,viewLifecycleOwner)
        mapViewModel =
                ViewModelProviders.of(this,customViewModelFactory).get(MapViewModel::class.java)
        val view = inflater.inflate(R.layout.fragment_map, container, false)

        // Views
        headerLayout = view.findViewById(R.id.header_layout)
        headerTextView = view.findViewById(R.id.header_text)
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
    }

    private fun initMap() {
        mapView.getMapAsync {
            if(map == null) map = it
            // Users list operations
            mapViewModel.findAllUsersInBounds(map!!)
            //listenToUsersInBounds()
            mapViewModel.markerOperations()
            listenToMarkersChanges()
        }
    }

    private fun listenToLocationUpdates() {
        mapViewModel.locationUpdates.observe(viewLifecycleOwner, Observer {
            if(it != null) {
                currentFirestoreUserDAO.location = GeoPoint(it.latitude,it.longitude)
                if(userDocumentId != null) {
                    mapViewModel.updateUser(currentFirestoreUserDAO)
                }
            }
        })
    }

    private fun listenIfUserIsAdded() {
        mapViewModel.currentUserDocument.observe(viewLifecycleOwner, Observer {
            userDocumentId = it
            currentFirestoreUserDAO.documentId = it
            if(it == null || !currentFirestoreUserDAO.isVisible!!) {
                // Enter this case if users isVisible is on false, or user is logged first time , then by default visibility is false
                if(it == null) {
                    mapViewModel.addNewUser(currentFirestoreUserDAO)
                }else{
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

    // This function should return users that are in bounds every 3 seconds
    private fun listenToUsersInBounds() {
        mapViewModel.inBoundUsersList.observe(viewLifecycleOwner, Observer {
            usersInBoundList = it
            Log.d(TAG, "Showing + ${it.size} new users in map")
        })
    }

    private fun visibilityChanger(visible: Boolean) {
        if(visible) {
            headerTextView.text = "You are now in visible mode, other people can see you on map"
            visibilityButton.text = "CHANGE TO INVISIBLE"
            userVisibility = true
            currentFirestoreUserDAO.isVisible = true
        }else{
            headerTextView.text = "You are now in invisible mode, other people can't see you on map."
            visibilityButton.text = "CHANGE TO VISIBLE"
            userVisibility = false
            currentFirestoreUserDAO.isVisible = false
        }

        if(!headerLayout.isVisible) headerLayout.visibility = View.VISIBLE
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
        mapViewModel.markersList.observe(viewLifecycleOwner, Observer {
            val markerListCopy = markerList // Use copy of markerList to remove white iterating
            for(markerDAO in it) {
                if(markerList.isNotEmpty()) {
                    markerList.forEachIndexed { index, currentMarker ->
                        if(markerDAO.documentId.equals(currentMarker.documentId)) {
                            if(!currentMarker.markerOptions!!.equals(markerDAO.markerOptions)) {
                                // If marker is still in bounds but changed it's location or personal info (update marker)
                                currentMarker.marker!!.remove()
                                markerListCopy.remove(currentMarker)
                                addMarkerToMap(markerDAO)
                                return@forEachIndexed // Break for each loop if found equal marker with same document id
                            }
                        }else{
                            if(index == markerList.size - 1) {
                                // If loop is finished and there were no marker on map with this document id, then add marker
                                addMarkerToMap(markerDAO)
                            }
                        }
                    }
                }else{
                    // Add marker if there is no marker on map
                    addMarkerToMap(markerDAO)
                }
            }
            markerList = markerListCopy // Rewrite new data into original markerList
            if(markerList.isNotEmpty()) {
                for(currentMarker in markerList) {
                    if(it.isEmpty()) {
                        // If there are no markers in bounds received from inBoundUsers LiveData
                        // but there are active markers on map, then remove all active markers from map
                        currentMarker.marker!!.remove()
                        markerListCopy.remove(currentMarker)
                    }else{
                        it.forEachIndexed { index, markerDAO ->
                            if(currentMarker.documentId.equals(markerDAO.documentId)) {
                               return@forEachIndexed
                            }else{
                                if(index == it.size - 1) {
                                    // If the marker that is active on map is out of bounds or become
                                    // invisible , then remove that marker from map
                                    currentMarker.marker!!.remove()
                                    markerListCopy.remove(currentMarker)
                                }
                            }
                        }
                    }
                }
                markerList = markerListCopy // Rewrite new data into original markerList
            }
        })
    }

    private fun addMarkerToMap(markerDAO: MarkerDAO) {
        val marker = map!!.addMarker(markerDAO.markerOptions)
        val markerWithDocumentId = MarkerWithDocumentId()
        markerWithDocumentId.documentId = markerDAO.documentId
        markerWithDocumentId.marker = marker
        markerWithDocumentId.markerOptions = markerDAO.markerOptions
        markerList.add(markerWithDocumentId)
        if (markerDAO.moveCamera!!) {
            map!!.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    markerDAO.latLng,
                    19F
                )
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
