package com.studio29.gharibyan.peoplearoundmemap.ui.loginform

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.studio29.gharibyan.peoplearoundmemap.ConnectionActivity
import com.studio29.gharibyan.peoplearoundmemap.R
import com.studio29.gharibyan.peoplearoundmemap.ui.connection.ConnectionViewModel

class ConfirmEmialFragment: Fragment() {

    // Views
    private lateinit var continueButton: Button

    // Initialization
    private lateinit var auth: FirebaseAuth
    private lateinit var connectionViewModel: ConnectionViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_confirm_email,container,false)

        continueButton = view.findViewById(R.id.continue_button)

        auth = FirebaseAuth.getInstance()
        continueButtonListener()

        return view
    }

    private fun continueButtonListener() {
        continueButton.setOnClickListener {
            (activity as ConnectionActivity).openLoginFragment()
        }
    }

    override fun onAttach(activity: Activity) {
        connectionViewModel = (activity as ConnectionActivity).connectionViewModel
        super.onAttach(activity)
    }
}