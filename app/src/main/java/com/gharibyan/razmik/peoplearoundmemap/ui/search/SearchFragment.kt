package com.gharibyan.razmik.peoplearoundmemap.ui.search

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gharibyan.razmik.peoplearoundmemap.MainActivity
import com.gharibyan.razmik.peoplearoundmemap.R
import com.gharibyan.razmik.peoplearoundmemap.repositry.models.firestore.FirestoreUserDAO
import com.gharibyan.razmik.peoplearoundmemap.ui.CustomViewModelFactory
import com.gharibyan.razmik.peoplearoundmemap.ui.map.MapViewModel
import com.gharibyan.razmik.peoplearoundmemap.ui.recyclerview.UserSearchAdapter
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.fragment_map.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class SearchFragment : Fragment() {

    // Initialization
    private lateinit var mapViewModel: MapViewModel
    private lateinit var adapter: UserSearchAdapter
    private lateinit var manager: FragmentManager

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
        val view = inflater.inflate(R.layout.fragment_search, container, false)

        // Views
        recyclerView = view.findViewById(R.id.search_user_list_recycler_view)
        searchEditText = view.findViewById(R.id.search_edit_text_view)

        manager = parentFragmentManager

        recyclerView.layoutManager = LinearLayoutManager(this.context)
        if(activity is MainActivity) adapter = UserSearchAdapter(this.activity!!.applicationContext,userlist,mapViewModel,
            activity!! as MainActivity
        )
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
                if(searchText.isNotEmpty()) {
                    mapViewModel.findUsersBySearch(searchText)
                }else{
                    userlist.clear()
                    adapter.notifyDataSetChanged()
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun listenToUserSearchResult() {
        mapViewModel.usersFoundBySearch.observe(viewLifecycleOwner, Observer {
            if(it.isNotEmpty()) {
                userlist.clear()
                userlist.addAll(it)
            }else{
                userlist.clear()
            }
            adapter.notifyDataSetChanged()
        })
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        mapViewModel = (activity as MainActivity).mapViewModel
    }
}
