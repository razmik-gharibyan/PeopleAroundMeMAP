package com.gharibyan.razmik.peoplearoundmemap

import android.os.Bundle
import android.view.MenuItem
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.gharibyan.razmik.peoplearoundmemap.ui.CustomViewModelFactory
import com.gharibyan.razmik.peoplearoundmemap.ui.map.MapFragment
import com.gharibyan.razmik.peoplearoundmemap.ui.map.MapViewModel
import com.gharibyan.razmik.peoplearoundmemap.ui.search.SearchFragment
import com.gharibyan.razmik.peoplearoundmemap.ui.userlist.UserListFragment

class MainActivity : AppCompatActivity() {

    // Initialization
    private lateinit var customViewModelFactory: CustomViewModelFactory
    lateinit var mapViewModel: MapViewModel

    // Views
    private lateinit var navView: BottomNavigationView
    private val mapFragment = MapFragment()
    private val listFragment = UserListFragment()
    private val searchFragment = SearchFragment()
    private var active: Fragment = mapFragment
    private val fragmentManager = supportFragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        customViewModelFactory = CustomViewModelFactory(baseContext,this)
        mapViewModel =
            ViewModelProviders.of(this,customViewModelFactory).get(MapViewModel::class.java)

        fragmentManager.beginTransaction().add(R.id.nav_host_fragment,mapFragment).commit()
        fragmentManager.beginTransaction().add(R.id.nav_host_fragment,searchFragment).hide(searchFragment).commit()

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
                fragmentManager.beginTransaction().hide(active).add(R.id.nav_host_fragment,listFragment).commit()
                active = listFragment
                return@setOnNavigationItemSelectedListener true
            }else if(it.itemId == R.id.navigation_search) {
                if(active == listFragment) {
                    fragmentManager.beginTransaction().remove(listFragment).commit()
                }
                fragmentManager.beginTransaction().hide(mapFragment).show(searchFragment).commit()
                active = searchFragment
                return@setOnNavigationItemSelectedListener true
            }
            return@setOnNavigationItemSelectedListener false
        }
    }

    fun hideSearchShowMap() {
        navView.selectedItemId = R.id.navigation_map
    }

}
