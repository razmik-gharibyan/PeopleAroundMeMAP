package com.gharibyan.razmik.peoplearoundmemap.repositry.services.marker

import android.graphics.Bitmap
import com.gharibyan.razmik.peoplearoundmemap.repositry.editor.FollowerProcessing
import com.gharibyan.razmik.peoplearoundmemap.repositry.editor.ImageProcessing
import com.gharibyan.razmik.peoplearoundmemap.repositry.editor.ImageUrlProcessing
import com.gharibyan.razmik.peoplearoundmemap.repositry.models.firestore.FirestoreUserDAO
import com.gharibyan.razmik.peoplearoundmemap.repositry.models.markers.MarkerDAO
import com.gharibyan.razmik.peoplearoundmemap.repositry.models.markers.MarkerIconWithDocument
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.lang.Exception

class MarkerApi: MarkerInter {
    var iconList = ArrayList<MarkerIconWithDocument>()

    override suspend fun addMarker(firestoreUserDAO: FirestoreUserDAO, moveCamera: Boolean): MarkerDAO? {
        if(firestoreUserDAO.isVisible!!) {
            val followerProcessing = FollowerProcessing()
            val imageProcessing = ImageProcessing(followerProcessing)
            val imageUrlProcessing = ImageUrlProcessing()
            var bitmap: Bitmap? = null
            try {
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
                 */
                bitmap = imageUrlProcessing.processImage(firestoreUserDAO.picture!!)
                val markerOptions = MarkerOptions()
                val roundBitMap: Bitmap
                val resizedBitMap: Bitmap
                resizedBitMap = imageProcessing.getResizedBitmap(bitmap, firestoreUserDAO.followers!!) // Resize bitmap
                roundBitMap = imageProcessing.getCroppedBitmap(resizedBitMap) // Make current bitmap to round type
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
                return markerDAO
            }catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return null
    }
}