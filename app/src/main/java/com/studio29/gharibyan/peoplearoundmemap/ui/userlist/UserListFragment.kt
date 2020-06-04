package com.studio29.gharibyan.peoplearoundmemap.ui.userlist

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.studio29.gharibyan.peoplearoundmemap.MainActivity
import com.studio29.gharibyan.peoplearoundmemap.R
import com.studio29.gharibyan.peoplearoundmemap.repositry.models.markers.MarkerDAO
import com.studio29.gharibyan.peoplearoundmemap.ui.map.MapViewModel
import com.studio29.gharibyan.peoplearoundmemap.ui.recyclerview.UserListAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserListFragment : Fragment() {

    // Initialization
    private lateinit var mapViewModel: MapViewModel

    // Views
    private lateinit var recyclerView: RecyclerView
    private lateinit var textView: TextView

    // Variables
    private var userlist = ArrayList<MarkerDAO>()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_userlist, container, false)

        recyclerView = view.findViewById(R.id.user_list_recycler_view)
        textView = view.findViewById(R.id.noone_here_text_view)
        recyclerView.layoutManager = LinearLayoutManager(this.context)

        CoroutineScope(Dispatchers.Main).launch {
            userlist = mapViewModel.inBoundArrayList
            if(userlist.isEmpty()) {
                textView.visibility = View.VISIBLE
            }
            recyclerView.adapter = UserListAdapter(context!!,userlist)
        }

        return view
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        mapViewModel = (activity as MainActivity).mapViewModel
    }
}
