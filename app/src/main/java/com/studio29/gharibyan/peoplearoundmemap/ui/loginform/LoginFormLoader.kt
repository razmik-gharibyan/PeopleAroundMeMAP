package com.studio29.gharibyan.peoplearoundmemap.ui.loginform

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.studio29.gharibyan.peoplearoundmemap.ConnectionActivity
import com.studio29.gharibyan.peoplearoundmemap.R
import com.studio29.gharibyan.peoplearoundmemap.repositry.models.singletons.Singletons
import java.util.regex.Pattern

class LoginFormLoader: Fragment() {

    // Constants
    private val TAG = javaClass.name

    // Views
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: EditText
    private lateinit var forgotPasswordTextView: TextView
    private lateinit var registerTextView: TextView

    // Initialization
    private lateinit var auth: FirebaseAuth

    // Vars
    private var instagramUserDAO = Singletons.instagramUserDAO

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login,container,false)

        emailEditText = view.findViewById(R.id.email_edittext)
        passwordEditText = view.findViewById(R.id.password_edittext)
        loginButton = view.findViewById(R.id.login_button)
        forgotPasswordTextView = view.findViewById(R.id.forgot_password_textview)
        registerTextView = view.findViewById(R.id.register_textview)

        auth = FirebaseAuth.getInstance()

        checkIfUserSignedIn()
        forgotPasswordClicked()
        registerNewUserClicked()

        return view
    }

    private fun checkIfUserSignedIn() {
        val currentUser = auth.currentUser
        if(currentUser != null) {
            // Enter here if user is logged in and open map after getting info from
            // its same uid document from firestore database
            instagramUserDAO.documentId = currentUser.uid
            openInstagramLoaderFragment()
        }else{
            // Enter if user is not logged in, and open instagram panel
            listenToLoginButton()
        }
    }


    private fun listenToLoginButton() {
        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            if(email.isNotEmpty() && password.isNotEmpty()) {
                if(checkIfEmailIsValid(email) && checkIfPasswordIsValid(password)) {
                    auth.signInWithEmailAndPassword(email,password).addOnCompleteListener {
                        if(it.isSuccessful) {
                            openInstagramLoaderFragment()
                        }else{
                            Toast.makeText(context,"Wrong email or password",Toast.LENGTH_LONG).show()
                        }
                    }
                }else{
                    Toast.makeText(context,"Wrong email or password",Toast.LENGTH_LONG).show()
                }
            }else{
                Toast.makeText(context,"Email and Password fields should not be empty",Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun checkIfEmailIsValid(email: String): Boolean {
        val emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$"

        val pat: Pattern = Pattern.compile(emailRegex)
        return pat.matcher(email).matches()
    }

    private fun checkIfPasswordIsValid(password: String): Boolean {
        val passwordRegex = "[a-zA-Z0-9]*"
        val pat: Pattern = Pattern.compile(passwordRegex)
        return pat.matcher(password).matches()
    }

    private fun openInstagramLoaderFragment() {
        (activity as ConnectionActivity).openInstagramLoaderFragment()
    }

    private fun forgotPasswordClicked() {
        forgotPasswordTextView.setOnClickListener {
            (activity as ConnectionActivity).openForgotPasswordFragment()
        }
    }

    private fun registerNewUserClicked() {
        registerTextView.setOnClickListener {
            (activity as ConnectionActivity).openRegisterFragment()
        }
    }
}