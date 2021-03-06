package com.studio29.gharibyan.peoplearoundmemap.repositry.services.marker

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.studio29.gharibyan.peoplearoundmemap.repositry.editor.FollowerProcessing
import com.studio29.gharibyan.peoplearoundmemap.repositry.editor.ImageProcessing
import com.studio29.gharibyan.peoplearoundmemap.repositry.editor.ImageUrlProcessing
import com.studio29.gharibyan.peoplearoundmemap.repositry.models.firestore.FirestoreUserDAO
import com.studio29.gharibyan.peoplearoundmemap.repositry.models.markers.MarkerDAO
import com.studio29.gharibyan.peoplearoundmemap.repositry.models.markers.MarkerIconWithDocument
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.lang.Exception

class MarkerApi: MarkerInter {
    private val followerProcessing = FollowerProcessing()
    private val imageProcessing = ImageProcessing(followerProcessing)
    private val imageUrlProcessing = ImageUrlProcessing()
    var iconList = ArrayList<MarkerIconWithDocument>()

    override suspend fun addMarker(firestoreUserDAO: FirestoreUserDAO, moveCamera: Boolean, context: Context): MarkerDAO? {
        if(firestoreUserDAO.isVisible!!) {

            var bitmap: Bitmap? = null
            try {
                val widthHeightArray = followerProcessing.picSizeViaFollower(firestoreUserDAO.followers!!)
                /*
                var iconWithDocument = MarkerIconWithDocument()
                for(document in iconList) {
                    if(document.documentId == firestoreUserDAO.documentId){
                        bitmap = document.icon
                        break
                    }
                }
                if(bitmap == null) {
                    bitmap = imageUrlProcessing.processImage(firestoreUserDAO.picture!!)
                    iconWithDocument.icon = bitmap
                    iconWithDocument.documentId = firestoreUserDAO.documentId
                    iconList.add(iconWithDocument)
                }

                bitmap = Glide.with(context)
                    .asBitmap()
                    .load(firestoreUserDAO.picture!!)
                    .into(widthHeightArray[0],widthHeightArray[1])
                    .get()
                 */
                if(bitmap == null) {
                    bitmap = imageUrlProcessing.processImage(firestoreUserDAO.picture!!)
                    bitmap = Bitmap.createScaledBitmap(bitmap!!,widthHeightArray[0],widthHeightArray[1],false)
                }
                val markerOptions = MarkerOptions()
                val roundBitMap = imageProcessing.getCroppedBitmap(bitmap!!) // Make current bitmap to round type
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
                markerDAO.firestoreUserDAO = firestoreUserDAO
                markerDAO.markerBitmap = roundBitMap
                return markerDAO
            }catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return null
    }
}