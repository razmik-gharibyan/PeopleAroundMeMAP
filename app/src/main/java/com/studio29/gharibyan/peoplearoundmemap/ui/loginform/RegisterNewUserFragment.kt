package com.studio29.gharibyan.peoplearoundmemap.ui.loginform

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.studio29.gharibyan.peoplearoundmemap.ConnectionActivity
import com.studio29.gharibyan.peoplearoundmemap.R
import com.studio29.gharibyan.peoplearoundmemap.ui.connection.ConnectionViewModel
import java.util.regex.Pattern

class RegisterNewUserFragment: Fragment() {

    // Constants
    private val TAG = javaClass.name

    // Views
    private lateinit var emailEdittextReg: EditText
    private lateinit var passwordEditTextReg: EditText
    private lateinit var confirmPasswordEdittextReg: EditText
    private lateinit var registerButton: Button

    // Initialization
    private lateinit var auth: FirebaseAuth
    private lateinit var connectionViewModel: ConnectionViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_register,container,false)

        emailEdittextReg = view.findViewById(R.id.email_edittext_reg)
        passwordEditTextReg = view.findViewById(R.id.password_edittext_reg)
        confirmPasswordEdittextReg = view.findViewById(R.id.confirm_password_edittext_reg)
        registerButton = view.findViewById(R.id.register_button)

        auth = FirebaseAuth.getInstance()

        listenToRegisterButton()

        return view
    }

    private fun listenToRegisterButton() {
        registerButton.setOnClickListener {
            val email = emailEdittextReg.text.toString()
            val password = passwordEditTextReg.text.toString()
            val confirmPassword = confirmPasswordEdittextReg.text.toString()
            if(email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()) {
                if(checkIfEmailIsValid(email) && checkIfPasswordIsValid(password) && checkIfPasswordIsValid(confirmPassword)) {
                    if(password == confirmPassword) {
                        auth.createUserWithEmailAndPassword(email,password)
                            .addOnCompleteListener {
                            if(it.isSuccessful) {
                                val currentUser = it.result!!.user
                                currentUser!!.sendEmailVerification().addOnCompleteListener {
                                    if(it.isSuccessful) {
                                        connectionViewModel.currentUserEmail = email
                                        auth.signOut()
                                        (activity as ConnectionActivity).openConfirmEmailFragment()
                                    }else{
                                        Toast.makeText(context,it.exception!!.message, Toast.LENGTH_LONG).show()
                                    }
                                }
                            }
                        }.addOnFailureListener {
                                val toastMsg = "The email address is already in use by another account"
                                if(it.message!!.contains(toastMsg)) {
                                    Toast.makeText(context,toastMsg, Toast.LENGTH_LONG).show()
                                }
                        }
                    }else{
                        Toast.makeText(context,"Password and Confirm password does not match", Toast.LENGTH_LONG).show()
                    }
                }else{
                    Toast.makeText(context,"Incorrect email or password, password should contain letters and numbers",
                        Toast.LENGTH_LONG).show()
                }
            }else{
                Toast.makeText(context,"Email and Password fields should not be empty", Toast.LENGTH_LONG).show()
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
        return pat.matcher(password).matches() && password.length >= 6
    }

    override fun onAttach(activity: Activity) {
        connectionViewModel = (activity as ConnectionActivity).connectionViewModel
        super.onAttach(activity)
    }
}