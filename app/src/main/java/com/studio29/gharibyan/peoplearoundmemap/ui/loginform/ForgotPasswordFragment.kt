package com.studio29.gharibyan.peoplearoundmemap.ui.loginform

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.studio29.gharibyan.peoplearoundmemap.ConnectionActivity
import com.studio29.gharibyan.peoplearoundmemap.R
import java.util.regex.Pattern

class ForgotPasswordFragment: Fragment() {

    //Constants
    private val TAG = javaClass.name

    // Views
    private lateinit var enterEmailEditText: EditText
    private lateinit var sendButton: Button
    private lateinit var countdownCloseForgotPassTextView: TextView

    // Initialization
    private lateinit var auth: FirebaseAuth
    private val manager = activity!!.supportFragmentManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_forgot_password,container,false)

        enterEmailEditText = view.findViewById(R.id.forgot_pass_enter_email_edittext)
        sendButton = view.findViewById(R.id.send_password_button)
        countdownCloseForgotPassTextView = view.findViewById(R.id.countdown_close_forgot_pass)

        auth = FirebaseAuth.getInstance()

        return view
    }

    private fun checkEmail() {
        val email = enterEmailEditText.text.toString()
        if(email.isNotEmpty()) {
            if(checkIfEmailIsValid(email)) {
                auth.sendPasswordResetEmail(email).addOnCompleteListener {
                    if(it.isSuccessful) {
                        countdownCloseForgotPassTextView.visibility = View.VISIBLE
                        object: CountDownTimer(5000,1000) {
                            override fun onTick(millisUntilFinished: Long) {
                                val secondsUntilFinish: Int = (millisUntilFinished / 1000).toInt()
                                countdownCloseForgotPassTextView.text = "Email successfully sent, this window will be" +
                                        " closed automatically in $secondsUntilFinish"
                            }

                            override fun onFinish() {
                                (activity as ConnectionActivity).closeForgotPasswordFragment()
                            }
                        }
                    }else{
                        Toast.makeText(context,"There is no user with this email",Toast.LENGTH_LONG).show()
                    }
                }
            }else{
                Toast.makeText(context,"Invalid email",Toast.LENGTH_LONG).show()
            }
        }else{
            Toast.makeText(context,"Email field should not be empty",Toast.LENGTH_LONG).show()
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
}