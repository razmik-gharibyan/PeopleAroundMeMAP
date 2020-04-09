package com.gharibyan.razmik.peoplearoundmemap.ui.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.gharibyan.razmik.peoplearoundmemap.R
import com.gharibyan.razmik.peoplearoundmemap.repositry.models.singletons.Singletons
import com.gharibyan.razmik.peoplearoundmemap.ui.CustomViewModelFactory

class MapFragment : Fragment() {

    // Initialization
    private lateinit var mapViewModel: MapViewModel
    private lateinit var customViewModelFactory: CustomViewModelFactory

    // Views
    private lateinit var headerTextView: TextView
    private lateinit var visibilityButton: Button

    // Vars global
    private var firestoreUserDAO = Singletons.firestoreUserDAO
    private var userVisibility: Boolean = false


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        customViewModelFactory = CustomViewModelFactory(activity?.baseContext!!)
        mapViewModel =
                ViewModelProviders.of(this,customViewModelFactory).get(MapViewModel::class.java)
        val view = inflater.inflate(R.layout.fragment_map, container, false)

        // Views
        headerTextView = view.findViewById(R.id.header_text)
        visibilityButton = view.findViewById(R.id.visibility_button)

        mapViewModel.initModels()
        mainOperations()


        return view
    }

    private fun mainOperations() {
        // Check user is existing in firestore or not
        mapViewModel.findUserDocument(firestoreUserDAO.userName!!)
        listenIfUserIsAdded()
        mapViewModel.addOrUpdateUser(firestoreUserDAO)
        // Start location updates
        mapViewModel.startLocationUpdates()
        listenToLocationUpdates()

    }


    private fun listenToLocationUpdates() {
        mapViewModel.locationUpdates.observe(viewLifecycleOwner, Observer {
            if(it != null) {

            }
        })
    }

    private fun listenIfUserIsAdded() {
        mapViewModel.currentUserDocument.observe(viewLifecycleOwner, Observer {
            if(it == null || !firestoreUserDAO.isVisible!!) {
                // Enter this case if users isVisible is on false, or user is logged first time , then by default visibility is false
                visibilityChanger(false)
            }else{
                // Enter this case if user is visible
                visibilityChanger(true)
            }
        })
    }

    private fun visibilityChanger(visible: Boolean) {
        if(visible) {
            headerTextView.text = "You are now in visible mode, other people can see you on map"
            visibilityButton.text = "CHANGE TO INVISIBLE"
            userVisibility = true
        }else{
            headerTextView.text = "You are now in invisible mode, other people can't see you on map."
            visibilityButton.text = "CHANGE TO VISIBLE"
            userVisibility = false
        }
    }

    private fun listenVisibleButtonChanges() {
        visibilityButton.setOnClickListener {
            if(userVisibility) {
                visibilityChanger(true)
            }else{
                visibilityChanger(false)
            }
        }
    }
}
