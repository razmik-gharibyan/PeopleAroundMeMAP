package com.studio29.gharibyan.peoplearoundmemap.ui.profile

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.studio29.gharibyan.peoplearoundmemap.MainActivity
import com.studio29.gharibyan.peoplearoundmemap.R
import com.studio29.gharibyan.peoplearoundmemap.repositry.models.firestore.CurrentFirestoreUserDAO
import com.studio29.gharibyan.peoplearoundmemap.repositry.models.singletons.Singletons
import com.studio29.gharibyan.peoplearoundmemap.repositry.services.firestore.FirestoreApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AccountFragment: Fragment() {

    // Views
    private lateinit var listView: ListView
    private lateinit var addAccountButton: Button

    // Initialization
    private lateinit var firestoreApi: FirestoreApi

    // Vars
    private var pictureArray = ArrayList<Bitmap>()
    private var usernameArray = ArrayList<String>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_linked_accounts,container,false)

        firestoreApi = FirestoreApi()

        addAccountButton = view.findViewById(R.id.add_account_button)
        listView = view.findViewById(R.id.accounts_list)

        findUserAccounts()
        listenToFoundAccounts()

        listView.adapter = AccountItemAdapter(context!!,usernameArray,pictureArray)

        return view
    }

    private fun findUserAccounts() {
        val auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser!!.uid
        CoroutineScope(Dispatchers.IO).launch {
            firestoreApi.findUserWithDocumentId(userId)
        }
    }

    private fun listenToFoundAccounts() {
        //TODO("if not account found then dont continue until user links account")
    }

}