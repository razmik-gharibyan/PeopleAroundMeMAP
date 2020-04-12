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
import com.gharibyan.razmik.peoplearoundmemap.repositry.models.singletons.Singletons
import com.gharibyan.razmik.peoplearoundmemap.ui.CustomViewModelFactory
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.SupportMapFragment
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
    private var userVisibility: Boolean = false
    private var userDocumentId: String? = null
    private var map: GoogleMap? = null

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
        firestoreUserDAO.isVisible = userVisibility
        mainOperations()


        return view
    }

    private fun mainOperations() {
        // Check user is existing in firestore or not
        mapViewModel.findUserDocument(firestoreUserDAO.userName!!)
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
            listenToUsersInBounds()
        }
    }

    private fun listenToLocationUpdates() {
        mapViewModel.locationUpdates.observe(viewLifecycleOwner, Observer {
            if(it != null && userDocumentId != null) {
                mapViewModel.updateUser(firestoreUserDAO,userDocumentId!!)
            }
        })
    }

    private fun listenIfUserIsAdded() {
        mapViewModel.currentUserDocument.observe(viewLifecycleOwner, Observer {
            userDocumentId = it
            if(it == null || !firestoreUserDAO.isVisible!!) {
                // Enter this case if users isVisible is on false, or user is logged first time , then by default visibility is false
                if(it == null) {
                    mapViewModel.addNewUser(firestoreUserDAO)
                }else{
                    mapViewModel.updateUser(firestoreUserDAO,it)
                }
                visibilityChanger(false)
            }else{
                // Enter this case if user is visible
                mapViewModel.updateUser(firestoreUserDAO,it)
                visibilityChanger(true)
            }
        })
    }

    private fun listenToNewAddedUser() {
        mapViewModel.newUserDocumentId.observe(viewLifecycleOwner, Observer {
            userDocumentId = it
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
            firestoreUserDAO.isVisible = true
        }else{
            headerTextView.text = "You are now in invisible mode, other people can't see you on map."
            visibilityButton.text = "CHANGE TO VISIBLE"
            userVisibility = false
            firestoreUserDAO.isVisible = false
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
