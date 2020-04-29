package com.gharibyan.razmik.peoplearoundmemap.repositry.services.marker.markerCluster

import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

class MarkerItem(val myPosition: LatLng, val mySnippet: String, val myTitle: String,
                 val bitmapDescriptor: BitmapDescriptor, val followers: Long, val userName: String): ClusterItem {

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