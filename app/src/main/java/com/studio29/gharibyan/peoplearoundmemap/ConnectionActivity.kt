package com.studio29.gharibyan.peoplearoundmemap

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.studio29.gharibyan.peoplearoundmemap.ui.CustomViewModelFactory
import com.studio29.gharibyan.peoplearoundmemap.ui.connection.ConnectionViewModel
import com.studio29.gharibyan.peoplearoundmemap.ui.instagram.InstagramLoaderFragment
import com.studio29.gharibyan.peoplearoundmemap.ui.loginform.ConfirmEmialFragment
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
    private val confirmEmailFragment = ConfirmEmialFragment()
    private var active: Fragment = loginFormLoaderFragment

    // Initialization
    private val manager = supportFragmentManager
    lateinit var connectionViewModel: ConnectionViewModel
    private lateinit var customViewModelFactory: CustomViewModelFactory


    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_connection)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        Thread.setDefaultUncaughtExceptionHandler{thread: Thread,throwable: Throwable ->
            Log.e("ConnectionActivity", "exception is $throwable")
        }

        // ViewModel
        customViewModelFactory = CustomViewModelFactory(baseContext!!,this)
        connectionViewModel = ViewModelProviders.of(this,customViewModelFactory).get(
            ConnectionViewModel::class.java)

        androidDefaultUEH = Thread.getDefaultUncaughtExceptionHandler()

    }

    fun openLoginFragment() {
        active = loginFormLoaderFragment
        manager.beginTransaction().replace(R.id.nav_host_fragment_connection,loginFormLoaderFragment).addToBackStack(null).commit()
    }

    fun openInstagramLoaderFragment() {
        active = instagramLoaderFragment
        manager.beginTransaction().replace(R.id.nav_host_fragment_connection,instagramLoaderFragment).addToBackStack(null).commit()
    }

    fun openForgotPasswordFragment() {
        active = forgotPasswordFragment
        manager.beginTransaction().replace(R.id.nav_host_fragment_connection,forgotPasswordFragment).commit()
    }

    fun closeForgotPasswordFragment() {
        active = loginFormLoaderFragment
        manager.beginTransaction().replace(R.id.nav_host_fragment_connection,loginFormLoaderFragment).commit()
    }

    fun openRegisterFragment() {
        active = registerNewUserFragment
        manager.beginTransaction().replace(R.id.nav_host_fragment_connection,registerNewUserFragment).addToBackStack(null).commit()
    }

    fun openConfirmEmailFragment() {
        active = confirmEmailFragment
        manager.beginTransaction().replace(R.id.nav_host_fragment_connection,confirmEmailFragment).addToBackStack(null).commit()
    }

    override fun onBackPressed() {
        val count = supportFragmentManager.backStackEntryCount

        if(active == forgotPasswordFragment) {
            openLoginFragment()
        }else if(active == loginFormLoaderFragment) {
            super.onBackPressed()
        }else if(active == registerNewUserFragment) {
            openLoginFragment()
        }else if(active == confirmEmailFragment) {
            openRegisterFragment()
        }else if(active == instagramLoaderFragment) {
            openLoginFragment()
        }
    }
}