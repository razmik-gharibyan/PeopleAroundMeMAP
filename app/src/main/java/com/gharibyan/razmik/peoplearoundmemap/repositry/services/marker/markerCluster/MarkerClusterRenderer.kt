package com.gharibyan.razmik.peoplearoundmemap.repositry.services.marker.markerCluster

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.ImageView
import androidx.core.graphics.createBitmap
import com.gharibyan.razmik.peoplearoundmemap.repositry.models.firestore.FirestoreUserDAO
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterItem
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import com.google.maps.android.ui.IconGenerator

class MarkerClusterRenderer<T: MarkerItem>(context: Context,
                                    map: GoogleMap?,
                                    clusterManager: ClusterManager<T>?
): DefaultClusterRenderer<T>(context, map, clusterManager) {

    override fun shouldRenderAsCluster(cluster: Cluster<T>?): Boolean {
        return cluster!!.size >= 2
    }

    override fun onBeforeClusterItemRendered(item: T, markerOptions: MarkerOptions?) {
        markerOptions!!.icon(item.bitmapDescriptor)
        markerOptions.title(item.myTitle)
        markerOptions.snippet(item.snippet)
    }

    override fun onBeforeClusterRendered(cluster: Cluster<T>?, markerOptions: MarkerOptions?) {
        // Find user with highest followers number
        var max: Long = 0
        var userName: String? = null
        for(item in cluster!!.items) {
            if(item.followers >= max) {
                max = item.followers
                userName = item.userName
            }
        }
        markerOptions!!.title("Highest number of followers in this group have $userName")
        super.onBeforeClusterRendered(cluster, markerOptions)
    }

    fun updateMarker(markerItem: MarkerItem, newPosition: LatLng) {
        getMarker(markerItem as T).position = newPosition
    }
}