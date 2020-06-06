package com.studio29.gharibyan.peoplearoundmemap.ui.loginform

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.studio29.gharibyan.peoplearoundmemap.ConnectionActivity
import com.studio29.gharibyan.peoplearoundmemap.R
import com.studio29.gharibyan.peoplearoundmemap.ui.connection.ConnectionViewModel

class ConfirmEmialFragment: Fragment() {

    // Views
    private lateinit var continueButton: Button
    private lateinit var confirmText: TextView

    // Initialization
    private lateinit var connectionViewModel: ConnectionViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_confirm_email,container,false)

        continueButton = view.findViewById(R.id.continue_button)
        confirmText = view.findViewById(R.id.confirm_text_textview)

        val email = connectionViewModel.currentUserEmail

        confirmText.text = "We have sent an email to $email for confirmation. Verify your email and press Continue button"
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