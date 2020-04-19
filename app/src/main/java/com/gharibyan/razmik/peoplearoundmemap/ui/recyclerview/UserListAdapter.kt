package com.gharibyan.razmik.peoplearoundmemap.ui.recyclerview

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gharibyan.razmik.peoplearoundmemap.R
import com.gharibyan.razmik.peoplearoundmemap.repositry.editor.FollowerProcessing
import com.gharibyan.razmik.peoplearoundmemap.repositry.editor.ImageProcessing
import com.gharibyan.razmik.peoplearoundmemap.repositry.editor.ImageUrlProcessing
import com.gharibyan.razmik.peoplearoundmemap.repositry.models.firestore.FirestoreUserDAO
import com.gharibyan.razmik.peoplearoundmemap.repositry.models.room.RoomUser
import kotlinx.android.synthetic.main.userlist_item.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserListAdapter(val context: Context,val userlist: ArrayList<RoomUser>)
    : RecyclerView.Adapter<UserListAdapter.UserListViewHolder>() {

    private val imageUrlProcessing = ImageUrlProcessing()
    private val imageProcessing = ImageProcessing(FollowerProcessing())

    class UserListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView = itemView.image_view
        val usernameView = itemView.username_view
        val followerView = itemView.follower_view
        val gotoProfileButton = itemView.goto_profile_button
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): UserListViewHolder {
        return UserListViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.userlist_item,parent,false))
    }

    override fun onBindViewHolder(holder: UserListViewHolder, position: Int) {
        CoroutineScope(Dispatchers.Main).launch {
            val tempBitmap = imageUrlProcessing.processImage(userlist.get(position).picture!!)
            val resizedBitmap = imageProcessing.getResizedBitmapForUserListFragment(tempBitmap)
            val croppedBitmap = imageProcessing.getCroppedBitmap(resizedBitmap)

            val username = userlist.get(position).username
            val followers = userlist.get(position).followers.toString()

            holder.imageView.setImageBitmap(croppedBitmap)
            holder.usernameView.text = username
            holder.followerView.text = followers
            holder.gotoProfileButton.setOnClickListener {
                //TODO Open user profile on instagram
            }
        }

    }

    // Get number of users in list
    override fun getItemCount(): Int {
        return userlist.size
    }

    fun updateAdapterList(newUserList: ArrayList<FirestoreUserDAO>) {
        userlist.clear()
        val roomUser = RoomUser()
        for(user in newUserList) {
            roomUser.documentid = user.documentId
            roomUser.username = user.userName
            roomUser.picture = user.picture
            roomUser.followers = user.followers
            roomUser.longitude = user.location!!.longitude
            roomUser.latitude = user.location!!.latitude
            roomUser.token = user.token
            roomUser.private = user.isPrivate
            roomUser.visible = user.isVisible
            roomUser.verified = user.isVerified
            userlist.add(roomUser)
        }
    }

}