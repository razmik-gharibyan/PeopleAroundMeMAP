package com.gharibyan.razmik.peoplearoundmemap.ui.userlist

import android.os.Bundle
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
import com.gharibyan.razmik.peoplearoundmemap.ui.CustomViewModelFactory
import com.gharibyan.razmik.peoplearoundmemap.ui.map.MapViewModel
import com.gharibyan.razmik.peoplearoundmemap.ui.recyclerview.UserListAdapter
import kotlinx.android.synthetic.main.fragment_map.*

class UserListFragment : Fragment() {

    // Initialization
    private lateinit var mapViewModel: MapViewModel

    // Views
    private lateinit var recyclerView: RecyclerView

    // Variables
    private var uselist = ArrayList<FirestoreUserDAO>()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val customViewModelFactory = CustomViewModelFactory(activity?.baseContext!!,viewLifecycleOwner)
        mapViewModel =
            ViewModelProviders.of(this,customViewModelFactory).get(MapViewModel::class.java)
        val view = inflater.inflate(R.layout.fragment_userlist, container, false)

        recyclerView = view.findViewById(R.id.user_list_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this.context)
        recyclerView.adapter = UserListAdapter(this.context!!,uselist)

        getInBoundUsers()

        return view
    }

    private fun getInBoundUsers() {
        mapViewModel.inBoundUsersList.observe(viewLifecycleOwner, Observer {
            uselist = it
            recyclerView.adapter!!.notifyDataSetChanged()
        })
    }
}
