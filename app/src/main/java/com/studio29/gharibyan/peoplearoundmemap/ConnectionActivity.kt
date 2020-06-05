package com.studio29.gharibyan.peoplearoundmemap

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.studio29.gharibyan.peoplearoundmemap.ui.instagram.InstagramLoaderFragment
import com.studio29.gharibyan.peoplearoundmemap.ui.loginform.ForgotPasswordFragment
import com.studio29.gharibyan.peoplearoundmemap.ui.loginform.LoginFormLoader
import com.studio29.gharibyan.peoplearoundmemap.ui.loginform.RegisterNewUserFragment

class ConnectionActivity: AppCompatActivity() {

    private var androidDefaultUEH: Thread.UncaughtExceptionHandler? = null

    // Fragments
    private val loginFormLoaderFragment = LoginFormLoader()
    private val forgotPasswordFragment = ForgotPasswordFragment()
    private val instagramLoaderFragment = InstagramLoaderFragment()
    private val registerNewUserFragment = RegisterNewUserFragment()

    // Initialization
    private val manager = supportFragmentManager

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_connection)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        Thread.setDefaultUncaughtExceptionHandler{thread: Thread,throwable: Throwable ->
            Log.e("ConnectionActivity", "exception is $throwable")
        }

        androidDefaultUEH = Thread.getDefaultUncaughtExceptionHandler()

        initFragments()

    }

    private fun initFragments() {
        manager.beginTransaction().add(R.id.nav_host_fragment_connection,loginFormLoaderFragment).commit()
    }

    fun openInstagramLoaderFragment() {
        manager.beginTransaction().replace(R.id.nav_host_fragment_connection,instagramLoaderFragment).commit()
    }

    fun openForgotPasswordFragment() {
        manager.beginTransaction().add(R.id.nav_host_fragment_connection,forgotPasswordFragment).commit()
    }

    fun closeForgotPasswordFragment() {
        manager.beginTransaction().remove(forgotPasswordFragment).commit()
    }

    fun openRegisterFragment() {
        manager.beginTransaction().remove(registerNewUserFragment).commit()
    }

}