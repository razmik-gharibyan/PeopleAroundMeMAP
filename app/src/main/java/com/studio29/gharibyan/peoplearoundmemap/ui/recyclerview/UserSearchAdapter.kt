package com.studio29.gharibyan.peoplearoundmemap.ui.recyclerview

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.studio29.gharibyan.peoplearoundmemap.MainActivity
import com.studio29.gharibyan.peoplearoundmemap.R
import com.studio29.gharibyan.peoplearoundmemap.repositry.editor.FollowerProcessing
import com.studio29.gharibyan.peoplearoundmemap.repositry.editor.ImageProcessing
import com.studio29.gharibyan.peoplearoundmemap.repositry.editor.ImageUrlProcessing
import com.studio29.gharibyan.peoplearoundmemap.repositry.models.firestore.FirestoreUserDAO
import com.studio29.gharibyan.peoplearoundmemap.ui.map.MapViewModel
import kotlinx.android.synthetic.main.userlist_item.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class UserSearchAdapter(val context: Context, val userlist: ArrayList<FirestoreUserDAO>, val mapViewModel: MapViewModel,
                        val activity: MainActivity):
    RecyclerView.Adapter<UserSearchAdapter.UserListViewHolder>() {

    // Constants
    private val TAG = javaClass.name

    // Initialization
    private val imageUrlProcessing = ImageUrlProcessing()
    private val followerProcessing = FollowerProcessing()
    private val imageProcessing = ImageProcessing(followerProcessing)


    class UserListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView = itemView.image_view
        val usernameView = itemView.username_view
        val followerView = itemView.follower_view
        val gotoProfileButton = itemView.goto_profile_button
        val onlineView = itemView.online_view

        fun clickOnUser(firestoreUserDAO: FirestoreUserDAO,mapViewModel: MapViewModel,activity: MainActivity) {
            itemView.setOnClickListener {
                mapViewModel.sendSearchedUserToMap(firestoreUserDAO)
                activity.hideSearchShowMap()
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): UserListViewHolder {
        return UserListViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.userlist_item,parent,false))
    }

    override fun onBindViewHolder(holder: UserListViewHolder, position: Int) {
        CoroutineScope(Dispatchers.Main).launch {
            if(userlist.isNotEmpty()) {
                Log.d(TAG,"userList size is ${userlist.size}")
                val tempBitmap = imageUrlProcessing.processImage(userlist.get(position).picture!!)
                val resizedBitmap = imageProcessing.getResizedBitmapForUserListFragment(tempBitmap)
                val croppedBitmap = imageProcessing.getCroppedBitmap(resizedBitmap)

                val username = userlist.get(position).userName
                val followers = followerProcessing.instagramFollowersType(userlist.get(position).followers!!)

                holder.imageView.setImageBitmap(croppedBitmap)
                holder.usernameView.text = username
                holder.followerView.text = followers
                if(userlist.get(position).isActive!!) {
                    holder.onlineView.setImageResource(R.drawable.ic_account_online_24dp)
                    holder.clickOnUser(userlist.get(position),mapViewModel,activity)
                }else{
                    holder.onlineView.setImageResource(R.drawable.ic_account_offline_24dp)
                }
                holder.gotoProfileButton.setOnClickListener {
                    openInstagramApp(username!!)
                }
            }
        }

    }

    // Get number of users in list
    override fun getItemCount(): Int {
        return userlist.size
    }

    private fun openInstagramApp(username: String) {
        val uri: Uri = Uri.parse("http://instagram.com/_u/$username")
        val likeIng = Intent(Intent.ACTION_VIEW, uri)
        likeIng.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        likeIng.setPackage("com.instagram.android")

        try {
            context.startActivity(likeIng)
        } catch (e: ActivityNotFoundException) {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("http://instagram.com/$username")
                ).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        }
    }

}