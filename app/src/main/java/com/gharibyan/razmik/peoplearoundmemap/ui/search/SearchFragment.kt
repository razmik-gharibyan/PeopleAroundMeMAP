package com.gharibyan.razmik.peoplearoundmemap.ui.search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gharibyan.razmik.peoplearoundmemap.R
import com.gharibyan.razmik.peoplearoundmemap.repositry.models.firestore.FirestoreUserDAO
import com.gharibyan.razmik.peoplearoundmemap.repositry.models.room.RoomUser
import com.gharibyan.razmik.peoplearoundmemap.ui.CustomViewModelFactory
import com.gharibyan.razmik.peoplearoundmemap.ui.map.MapViewModel
import com.gharibyan.razmik.peoplearoundmemap.ui.recyclerview.UserListAdapter
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SearchFragment : Fragment() {

    // Initialization
    private lateinit var mapViewModel: MapViewModel
    private lateinit var adapter: UserListAdapter

    // Views
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchEditText: TextInputEditText

    // Variables
    private var userlist = ArrayList<RoomUser>()


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
        adapter = UserListAdapter(context!!,userlist)
        recyclerView.adapter = adapter

        searchUser()
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
            adapter.updateAdapterList(it)
            adapter.notifyDataSetChanged()
        })
    }
}
