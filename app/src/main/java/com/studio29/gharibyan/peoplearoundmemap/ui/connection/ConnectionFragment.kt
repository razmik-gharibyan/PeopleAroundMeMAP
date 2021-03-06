package com.studio29.gharibyan.peoplearoundmemap.ui.connection

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.studio29.gharibyan.peoplearoundmemap.ConnectionActivity
import com.studio29.gharibyan.peoplearoundmemap.R
import com.studio29.gharibyan.peoplearoundmemap.ui.CustomViewModelFactory

class ConnectionFragment: Fragment() {

    // Initialization
    private lateinit var connectionViewModel: ConnectionViewModel
    private lateinit var customViewModelFactory: CustomViewModelFactory

    //Views
    private lateinit var connectionLayout: ConstraintLayout
    private lateinit var navController: NavController
    private lateinit var reconnectButton: Button

    // Constants
    private val PERMISSION_REQUEST_CODE: Int = 9000

    // Vars
    var networkStatus: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_connection_check,container,false)
        reconnectButton = view.findViewById(R.id.reconnect_button)
        connectionLayout = view.findViewById(R.id.connection_layout)

        // ViewModel
        customViewModelFactory = CustomViewModelFactory(activity?.baseContext!!,viewLifecycleOwner)
        connectionViewModel = ViewModelProviders.of(this,customViewModelFactory).get(
            ConnectionViewModel::class.java)
        connectionViewModel.checkNetworkStatus()
        connectionViewModel.checkPermissionStatus()
        connectionViewModel.checkGPSStatus()

        reconnectButtonInteraction()

        // Here will be called responses for Network and GPS requests and permissions
        networkStatusResponse()
        permissionsResponse()

        return view
    }

    private fun networkStatusResponse() {
        connectionViewModel.networkAccess.observe(viewLifecycleOwner, Observer {
            networkStatus = it
            if(networkStatus) {
                (activity as ConnectionActivity).popBackStackByTag(this,"connectionTag")
                (activity as ConnectionActivity).openLoginFragment()

            }else{
                connectionLayout.visibility = View.VISIBLE
            }
        })
    }

    private fun permissionsResponse() {
        val permission = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
        connectionViewModel.permissionAccess.observe(viewLifecycleOwner, Observer {
            if(!it) {
                ActivityCompat.requestPermissions(activity!!,permission,PERMISSION_REQUEST_CODE)
                connectionViewModel.checkGPSStatus()
            }else{
                gpsStatusResponse()
            }
        })
    }

    private fun gpsStatusResponse() {
        connectionViewModel.gpsAccess.observe(viewLifecycleOwner, Observer {
            if(it) {
                //TODO()
            }
        })
    }

    private fun reconnectButtonInteraction() {
        if(!networkStatus) {
            reconnectButton.setOnClickListener {
                connectionViewModel.checkNetworkStatus()
            }
        }
    }
}