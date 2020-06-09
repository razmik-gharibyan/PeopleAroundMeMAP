package com.studio29.gharibyan.peoplearoundmemap.ui.profile

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.studio29.gharibyan.peoplearoundmemap.ConnectionActivity
import com.studio29.gharibyan.peoplearoundmemap.MainActivity
import com.studio29.gharibyan.peoplearoundmemap.R
import com.studio29.gharibyan.peoplearoundmemap.repositry.services.firestore.FirestoreApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProfileFragment: Fragment() {

    // Views
    private lateinit var listView: ListView

    // Constants
    private val titleArray = arrayOf("Instagram accounts", "Sign Out", "Delete account")
    private val iconArray = arrayOf(R.drawable.ic_perm_identity_white_24dp,R.drawable.ic_logout_white_24dp
        ,R.drawable.ic_delete_white_24dp)

    // Initialization
    private lateinit var firestoreApi: FirestoreApi

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile,container,false)

        firestoreApi = FirestoreApi()

        listView = view.findViewById(R.id.menu_list)
        listView.adapter = MenuItemAdapter(context!!,titleArray,iconArray)
        listView.setOnItemClickListener { parent, view, position, id ->
            if (position == 0) {
                openAccountFragment()
            } else if (position == 1) {
                // Sign Out user, and redirect to login fragment
                FirebaseAuth.getInstance().signOut()
                openLoginFormFragment()
            } else if (position == 2) {
                // Delete user and redirect to login fragment
                val user = FirebaseAuth.getInstance().currentUser
                if (user != null) {
                    val userEmail = user.email
                    val userId = user.uid
                    val alertDialog = AlertDialog.Builder(context)
                    alertDialog.setPositiveButton("Delete",object : DialogInterface.OnClickListener {
                            override fun onClick(dialog: DialogInterface?, which: Int) {
                                CoroutineScope(Dispatchers.IO).launch {
                                    firestoreApi.deleteDocument(userId)
                                }
                                listenIfDocumentDeleted(user)
                            }
                        })
                    alertDialog.setNegativeButton("Cancel",object : DialogInterface.OnClickListener {
                            override fun onClick(dialog: DialogInterface?, which: Int) {
                                dialog!!.cancel()
                            }
                        })
                    alertDialog.setTitle("Are you sure you want to delete account?")
                    alertDialog.setMessage("Delete account connected to $userEmail?")
                    alertDialog.create()
                    alertDialog.show()
                }
            }
        }
        return view
    }

    private fun listenIfDocumentDeleted(user: FirebaseUser) {
        firestoreApi.documentDeleted.observe(this, Observer {
            if(it) {
                user.delete().addOnCompleteListener {
                    if(it.isSuccessful) {
                        Toast.makeText(context,"Account with ${user.email} email is successfully deleted",Toast.LENGTH_LONG).show()
                        openLoginFormFragment()
                    }else{
                        Toast.makeText(context,"Could not delete account, please try again later",Toast.LENGTH_LONG).show()
                    }
                }
            }else{
                Toast.makeText(context,"Could not delete account, please try again later",Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun openLoginFormFragment() {
        val intent = Intent(context, ConnectionActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }

    private fun openAccountFragment() {
        (activity as MainActivity).openAccountFragment()
    }

}