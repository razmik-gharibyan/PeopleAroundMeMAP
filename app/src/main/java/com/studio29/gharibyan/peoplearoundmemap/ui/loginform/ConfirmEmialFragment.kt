package com.studio29.gharibyan.peoplearoundmemap.ui.loginform

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.studio29.gharibyan.peoplearoundmemap.ConnectionActivity
import com.studio29.gharibyan.peoplearoundmemap.R
import com.studio29.gharibyan.peoplearoundmemap.ui.CustomViewModelFactory
import com.studio29.gharibyan.peoplearoundmemap.ui.connection.ConnectionViewModel

class ConfirmEmialFragment: Fragment() {

    // Views
    private lateinit var continueButton: Button

    // Initialization
    private lateinit var auth: FirebaseAuth
    private lateinit var connectionViewModel: ConnectionViewModel
    private lateinit var customViewModelFactory: CustomViewModelFactory

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_confirm_email,container,false)

        continueButton = view.findViewById(R.id.continue_button)

        // ViewModel
        customViewModelFactory = CustomViewModelFactory(activity?.baseContext!!,viewLifecycleOwner)
        connectionViewModel = ViewModelProviders.of(this,customViewModelFactory).get(
            ConnectionViewModel::class.java)

        auth = FirebaseAuth.getInstance()
        continueButtonListener()

        return view
    }

    private fun continueButtonListener() {
        continueButton.setOnClickListener {
            if(auth.currentUser!!.isEmailVerified) {
                connectionViewModel.currentUserID = auth.currentUser!!.uid
                connectionViewModel.registerNewUser = true
                (activity as ConnectionActivity).openInstagramLoaderFragment()
            }
        }
    }
}