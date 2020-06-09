package com.studio29.gharibyan.peoplearoundmemap

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.studio29.gharibyan.peoplearoundmemap.repositry.models.firestore.FirestoreUserDAO
import com.studio29.gharibyan.peoplearoundmemap.repositry.models.singletons.Singletons
import com.studio29.gharibyan.peoplearoundmemap.ui.CustomViewModelFactory
import com.studio29.gharibyan.peoplearoundmemap.ui.map.MapFragment
import com.studio29.gharibyan.peoplearoundmemap.ui.map.MapViewModel
import com.studio29.gharibyan.peoplearoundmemap.ui.search.SearchFragment
import com.studio29.gharibyan.peoplearoundmemap.ui.userlist.UserListFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.studio29.gharibyan.peoplearoundmemap.repositry.services.firestore.FirestoreApi
import com.studio29.gharibyan.peoplearoundmemap.ui.connection.ConnectionViewModel
import com.studio29.gharibyan.peoplearoundmemap.ui.profile.AccountFragment
import com.studio29.gharibyan.peoplearoundmemap.ui.profile.ProfileFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    // Initialization
    private lateinit var customViewModelFactory: CustomViewModelFactory
    lateinit var mapViewModel: MapViewModel
    private lateinit var firestoreApi: FirestoreApi

    // Variables
    private val currentFirestoreUserDAO = Singletons.currentFirestoreUserDAO

    // Views
    private lateinit var navView: BottomNavigationView
    private lateinit var mapFragment: Fragment
    private lateinit var listFragment: Fragment
    private lateinit var searchFragment: Fragment
    private lateinit var profileFragment: Fragment
    private lateinit var accountFragment: Fragment
    private lateinit var active: Fragment
    private val fragmentManager = supportFragmentManager

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        customViewModelFactory = CustomViewModelFactory(baseContext,this)
        mapViewModel =
            ViewModelProviders.of(this,customViewModelFactory).get(MapViewModel::class.java)

        firestoreApi = FirestoreApi()

        mapFragment = MapFragment()
        listFragment = UserListFragment()
        searchFragment = SearchFragment()
        profileFragment = ProfileFragment()
        accountFragment = AccountFragment()

        active = mapFragment

        fragmentManager.beginTransaction().add(R.id.nav_host_fragment,mapFragment).commit()
        fragmentManager.beginTransaction().add(R.id.nav_host_fragment,searchFragment).hide(searchFragment).commit()
        fragmentManager.beginTransaction().add(R.id.nav_host_fragment,profileFragment).hide((profileFragment)).commit()

        // Views
        navView = findViewById(R.id.nav_view)
        listenToNavViewChanges()

        //val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        /*val appBarConfiguration = AppBarConfiguration(setOf(
                R.id.navigation_map, R.id.navigation_list, R.id.navigation_search))
        setupActionBarWithNavController(navController, appBarConfiguration)

         */
        //navView.setupWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.settings_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.findMe_item) {
            val firestoreUserDao = FirestoreUserDAO()
            firestoreUserDao.location = currentFirestoreUserDAO.location
            mapViewModel.sendSearchedUserToMap(firestoreUserDao)
            hideSearchShowMap()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun listenToNavViewChanges() {
        navView.setOnNavigationItemSelectedListener {
            if(it.itemId == R.id.navigation_map) {
                if(active == listFragment) {
                    fragmentManager.beginTransaction().remove(listFragment).commit()
                }else if(active == searchFragment) {
                    fragmentManager.beginTransaction().hide(searchFragment).commit()
                }
                fragmentManager.beginTransaction().show(mapFragment).commit()
                active = mapFragment
                return@setOnNavigationItemSelectedListener true
            }else if(it.itemId == R.id.navigation_list) {
                if(active != listFragment) {
                    fragmentManager.beginTransaction().hide(active)
                        .add(R.id.nav_host_fragment, listFragment).commit()
                    active = listFragment
                    return@setOnNavigationItemSelectedListener true
                }
            }else if(it.itemId == R.id.navigation_search) {
                if(active == listFragment) {
                    fragmentManager.beginTransaction().remove(listFragment).commit()
                }
                fragmentManager.beginTransaction().hide(mapFragment).show(searchFragment).commit()
                active = searchFragment
                return@setOnNavigationItemSelectedListener true
            }else if(it.itemId == R.id.navigation_profile) {
                if(active == listFragment) {
                    fragmentManager.beginTransaction().remove(listFragment).commit()
                }
                fragmentManager.beginTransaction().hide(mapFragment).show(profileFragment).commit()
                active = profileFragment
                return@setOnNavigationItemSelectedListener true
            }
            return@setOnNavigationItemSelectedListener false
        }
    }

    fun hideSearchShowMap() {
        navView.selectedItemId = R.id.navigation_map
    }

    fun openAccountFragment() {
        fragmentManager.beginTransaction().hide(profileFragment).commit()
        fragmentManager.beginTransaction().add(R.id.nav_host_fragment,accountFragment).commit()
    }

    override fun onStop() {
        super.onStop()
        currentFirestoreUserDAO.isActive = false
        mapViewModel.updateUser(currentFirestoreUserDAO)
    }

    override fun onRestart() {
        super.onRestart()
        currentFirestoreUserDAO.isActive = true
        mapViewModel.updateUser(currentFirestoreUserDAO)
    }

    override fun onBackPressed() {
        moveTaskToBack(true)
    }
}
