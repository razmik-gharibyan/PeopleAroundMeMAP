package com.studio29.gharibyan.peoplearoundmemap

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProviders
import com.studio29.gharibyan.peoplearoundmemap.ui.CustomViewModelFactory
import com.studio29.gharibyan.peoplearoundmemap.ui.connection.ConnectionFragment
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
    private val connectionFragment = ConnectionFragment()
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
        openConnectionFragment()
        androidDefaultUEH = Thread.getDefaultUncaughtExceptionHandler()

    }

    fun openConnectionFragment() {
        active = connectionFragment
        manager.beginTransaction().replace(R.id.nav_host_fragment_connection,connectionFragment).addToBackStack("connectionTag").commit()
    }

    fun openLoginFragment() {
        active = loginFormLoaderFragment
        manager.beginTransaction().replace(R.id.nav_host_fragment_connection,loginFormLoaderFragment).addToBackStack("loginTag").commit()
    }

    fun openInstagramLoaderFragment() {
        active = instagramLoaderFragment
        manager.beginTransaction().replace(R.id.nav_host_fragment_connection,instagramLoaderFragment).addToBackStack("instagramTag").commit()
    }

    fun openForgotPasswordFragment() {
        active = forgotPasswordFragment
        manager.beginTransaction().replace(R.id.nav_host_fragment_connection,forgotPasswordFragment).addToBackStack("forgotpassTag").commit()
    }

    fun openRegisterFragment() {
        active = registerNewUserFragment
        manager.beginTransaction().replace(R.id.nav_host_fragment_connection,registerNewUserFragment).addToBackStack("registerTag").commit()
    }

    fun openConfirmEmailFragment() {
        active = confirmEmailFragment
        manager.beginTransaction().replace(R.id.nav_host_fragment_connection,confirmEmailFragment).addToBackStack("confirmTag").commit()
    }

    fun closeForgotPasswordFragment() {
        active = loginFormLoaderFragment
        popBackStackByTag(forgotPasswordFragment,"forgotpassTag")
    }

    fun closeConfirmFragment() {
        active = loginFormLoaderFragment
        popBackStackByTag(confirmEmailFragment,"confirmTag")
        popBackStackByTag(registerNewUserFragment,"registerTag")
    }

    fun popBackStackByTag(fragment: Fragment,tag: String) {
        manager.beginTransaction().remove(fragment).commit()
        manager.popBackStack(tag,FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }

    override fun onBackPressed() {
        val count = supportFragmentManager.backStackEntryCount

        if(active == forgotPasswordFragment) {
            active = loginFormLoaderFragment
            popBackStackByTag(forgotPasswordFragment,"forgotpassTag")
        }else if(active == loginFormLoaderFragment) {
            popBackStackByTag(loginFormLoaderFragment,"loginTag")
            super.onBackPressed()
        }else if(active == registerNewUserFragment) {
            active = registerNewUserFragment
            popBackStackByTag(registerNewUserFragment,"registerTag")
        }else if(active == confirmEmailFragment) {
            active = loginFormLoaderFragment
            popBackStackByTag(confirmEmailFragment,"confirmTag")
            popBackStackByTag(registerNewUserFragment,"registerTag")
        }
    }
}