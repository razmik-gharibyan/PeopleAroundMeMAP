package com.studio29.gharibyan.peoplearoundmemap.ui.search

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.studio29.gharibyan.peoplearoundmemap.MainActivity
import com.studio29.gharibyan.peoplearoundmemap.R
import com.studio29.gharibyan.peoplearoundmemap.repositry.models.firestore.FirestoreUserDAO
import com.studio29.gharibyan.peoplearoundmemap.ui.map.MapViewModel
import com.studio29.gharibyan.peoplearoundmemap.ui.recyclerview.UserSearchAdapter


class SearchFragment : Fragment() {

    // Initialization
    private lateinit var mapViewModel: MapViewModel
    private lateinit var adapter: UserSearchAdapter
    private lateinit var manager: FragmentManager

    // Views
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchEditText: EditText

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
            val handler = Handler()

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                handler.removeCallbacksAndMessages(null)
            }

            override fun afterTextChanged(s: Editable?) {
                handler.postDelayed({
                    val searchText = s.toString()
                    if(searchText.isNotEmpty()) {
                        mapViewModel.findUsersBySearch(searchText)
                    }else{
                        userlist.clear()
                        adapter.notifyDataSetChanged()
                    }
                },500)
            }
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
