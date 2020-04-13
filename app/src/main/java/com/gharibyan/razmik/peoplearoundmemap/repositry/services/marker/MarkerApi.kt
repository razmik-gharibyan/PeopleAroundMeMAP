package com.gharibyan.razmik.peoplearoundmemap.repositry.services.marker

import android.graphics.Bitmap
import com.gharibyan.razmik.peoplearoundmemap.repositry.editor.FollowerProcessing
import com.gharibyan.razmik.peoplearoundmemap.repositry.editor.ImageProcessing
import com.gharibyan.razmik.peoplearoundmemap.repositry.editor.ImageUrlProcessing
import com.gharibyan.razmik.peoplearoundmemap.repositry.models.firestore.FirestoreUserDAO
import com.gharibyan.razmik.peoplearoundmemap.repositry.models.markers.MarkerDAO
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.lang.Exception

class MarkerApi: MarkerInter {

    override suspend fun addMarker(firestoreUserDAO: FirestoreUserDAO, moveCamera: Boolean): MarkerDAO? {
        if(firestoreUserDAO.isVisible!!) {
            val followerProcessing = FollowerProcessing()
            val imageProcessing = ImageProcessing(followerProcessing)
            val imageUrlProcessing = ImageUrlProcessing()
            try {
                val markerOptions = MarkerOptions()
                val bitmap = imageUrlProcessing.processImage(firestoreUserDAO.picture!!)
                val roundBitMap: Bitmap
                val resizedBitMap: Bitmap
                val userListFragmentBitmap: Bitmap
                resizedBitMap = imageProcessing.getResizedBitmap(bitmap, firestoreUserDAO.followers!!) // Resize bitmap
                roundBitMap = imageProcessing.getCroppedBitmap(resizedBitMap) // Make current bitmap to round type
                userListFragmentBitmap = imageProcessing.getCroppedBitmap(
                    imageProcessing.getResizedBitmapForUserListFragment(bitmap)
                ) // Make current bitmap for userlist fragment type
                val userPictureString: String =
                    imageProcessing.bitmapToString(userListFragmentBitmap) //Convert bitmap to String to send to fragment as param
                val fullPictureString: String = imageProcessing.bitmapToString(bitmap!!)

                val latLng = LatLng(firestoreUserDAO.location!!.latitude,firestoreUserDAO.location!!.longitude)
                markerOptions.position(latLng)
                markerOptions.visible(true)
                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(roundBitMap))
                val currentFollowers: String? =
                    followerProcessing.instagramFollowersType(firestoreUserDAO.followers!!)
                val private = if(firestoreUserDAO.isPrivate!!) {"private"}else{"public"}
                markerOptions.title("${firestoreUserDAO.userName} : $currentFollowers  Followers, $private account")
                val markerDAO = MarkerDAO()
                markerDAO.documentId = firestoreUserDAO.documentId
                markerDAO.latLng = latLng
                markerDAO.moveCamera = moveCamera
                markerDAO.markerOptions = markerOptions
                return markerDAO
            }catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return null
    }
}