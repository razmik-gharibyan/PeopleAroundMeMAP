package com.gharibyan.razmik.peoplearoundmemap.ui.search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gharibyan.razmik.peoplearoundmemap.R
import com.gharibyan.razmik.peoplearoundmemap.repositry.models.firestore.FirestoreUserDAO
import com.gharibyan.razmik.peoplearoundmemap.ui.CustomViewModelFactory
import com.gharibyan.razmik.peoplearoundmemap.ui.map.MapViewModel
import com.gharibyan.razmik.peoplearoundmemap.ui.recyclerview.UserSearchAdapter
import com.google.android.material.textfield.TextInputEditText


class SearchFragment : Fragment() {

    // Initialization
    private lateinit var mapViewModel: MapViewModel
    private lateinit var adapter: UserSearchAdapter

    // Views
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchEditText: TextInputEditText

    // Variables
    private var userlist = ArrayList<FirestoreUserDAO>()


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val customViewModelFactory = CustomViewModelFactory(activity?.baseContext!!,viewLifecycleOwner)
        mapViewModel =
            ViewModelProviders.of(this,customViewModelFactory).get(MapViewModel::class.java)
        val view = inflater.inflate(R.layout.fragment_search, container, false)

        // Views
        recyclerView = view.findViewById(R.id.search_user_list_recycler_view)
        searchEditText = view.findViewById(R.id.search_edit_text_view)

        recyclerView.layoutManager = LinearLayoutManager(this.context)
        adapter = UserSearchAdapter(this.activity!!.applicationContext,userlist)
        recyclerView.adapter = adapter

        searchUser()
        mapViewModel.getFoundUserBySearch()
        listenToUserSearchResult()

        return view
    }

    private fun searchUser() {
        searchEditText.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val searchText = s.toString()
                mapViewModel.findUsersBySearch(searchText)
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun listenToUserSearchResult() {
        mapViewModel.usersFoundBySearch.observe(viewLifecycleOwner, Observer {
            if(it.isNotEmpty()) {
                userlist.addAll(it)
            }else{
                userlist.clear()
            }
            adapter.notifyDataSetChanged()
        })
    }
}
