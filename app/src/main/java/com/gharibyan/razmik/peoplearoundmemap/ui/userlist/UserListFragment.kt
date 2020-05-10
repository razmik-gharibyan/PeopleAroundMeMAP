package com.gharibyan.razmik.peoplearoundmemap.ui.userlist

import android.app.Activity
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
import com.gharibyan.razmik.peoplearoundmemap.MainActivity
import com.gharibyan.razmik.peoplearoundmemap.R
import com.gharibyan.razmik.peoplearoundmemap.repositry.models.firestore.FirestoreUserDAO
import com.gharibyan.razmik.peoplearoundmemap.repositry.models.room.RoomUser
import com.gharibyan.razmik.peoplearoundmemap.repositry.models.room.RoomUserDao
import com.gharibyan.razmik.peoplearoundmemap.repositry.models.room.UsersDatabase
import com.gharibyan.razmik.peoplearoundmemap.ui.CustomViewModelFactory
import com.gharibyan.razmik.peoplearoundmemap.ui.map.MapViewModel
import com.gharibyan.razmik.peoplearoundmemap.ui.recyclerview.UserListAdapter
import kotlinx.android.synthetic.main.fragment_map.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserListFragment : Fragment() {

    // Initialization
    private lateinit var mapViewModel: MapViewModel
    private lateinit var roomUserDao: RoomUserDao

    // Views
    private lateinit var recyclerView: RecyclerView
    private lateinit var textView: TextView

    // Variables
    private var userlist = ArrayList<RoomUser>()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        val userDatabase = UsersDatabase.getInstance(activity!!.applicationContext)
        roomUserDao = userDatabase.userDao()
        super.onActivityCreated(savedInstanceState)
    }

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
            userlist = roomUserDao.getAll() as ArrayList<RoomUser>
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
