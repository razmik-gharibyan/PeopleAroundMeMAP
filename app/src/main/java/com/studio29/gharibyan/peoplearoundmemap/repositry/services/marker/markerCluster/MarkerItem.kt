package com.studio29.gharibyan.peoplearoundmemap.repositry.services.marker.markerCluster

import android.graphics.Bitmap
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

class MarkerItem(val myPosition: LatLng, val mySnippet: String, val myTitle: String,
                 val bitmapDescriptor: BitmapDescriptor, val followers: Long, val userName: String, val documentId: String,
                 val bitmap: Bitmap
): ClusterItem {

    override fun getSnippet(): String {
       return mySnippet
    }

    override fun getTitle(): String {
        return myTitle
    }

    override fun getPosition(): LatLng {
        return myPosition
    }
}

fun MarkerItem.clone() = with(this) {
    MarkerItem(
        myPosition = LatLng(myPosition.latitude, myPosition.longitude),
        mySnippet = mySnippet,
        myTitle = myTitle,
        bitmapDescriptor = bitmapDescriptor,
        followers = followers,
        userName = userName,
        documentId = documentId,
        bitmap = bitmap
    )
}