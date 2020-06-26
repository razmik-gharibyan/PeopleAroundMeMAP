package com.studio29.gharibyan.peoplearoundmemap.repositry.services.marker.markerCluster

import android.content.Context
import android.util.Log
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import java.lang.Exception

class MarkerClusterRenderer<T: MarkerItem>(context: Context,
                                    map: GoogleMap?,
                                    clusterManager: ClusterManager<T>?
): DefaultClusterRenderer<T>(context, map, clusterManager) {

    private val TAG = javaClass.name
    private var clusterList = ArrayList<Cluster<T>>()

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
        if(!clusterList.contains(cluster)) {
            clusterList.add(cluster)
        }
        markerOptions!!.title("Highest number of followers in this group have $userName")
        super.onBeforeClusterRendered(cluster, markerOptions)
    }

    fun updateMarker(markerItem: MarkerItem?, newPosition: LatLng) {
        try {
            getMarker(markerItem as T).position = newPosition
        }catch (e: Exception) {
            Log.d(TAG,"Exception is $e")
        }
    }
}