package com.gharibyan.razmik.peoplearoundmemap.ui.recyclerview

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gharibyan.razmik.peoplearoundmemap.R
import com.gharibyan.razmik.peoplearoundmemap.repositry.editor.FollowerProcessing
import com.gharibyan.razmik.peoplearoundmemap.repositry.editor.ImageProcessing
import com.gharibyan.razmik.peoplearoundmemap.repositry.editor.ImageUrlProcessing
import com.gharibyan.razmik.peoplearoundmemap.repositry.models.firestore.FirestoreUserDAO
import kotlinx.android.synthetic.main.userlist_item.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class UserSearchAdapter(val context: Context, val userlist: ArrayList<FirestoreUserDAO>):
    RecyclerView.Adapter<UserSearchAdapter.UserListViewHolder>() {

    // Constants
    private val TAG = javaClass.name

    // Initialization
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
            if(userlist.isNotEmpty()) {
                val tempBitmap = imageUrlProcessing.processImage(userlist.get(position).picture!!)
                val resizedBitmap = imageProcessing.getResizedBitmapForUserListFragment(tempBitmap)
                val croppedBitmap = imageProcessing.getCroppedBitmap(resizedBitmap)

                val username = userlist.get(position).userName
                val followers = userlist.get(position).followers.toString()

                holder.imageView.setImageBitmap(croppedBitmap)
                holder.usernameView.text = username
                holder.followerView.text = followers
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