package com.gharibyan.razmik.peoplearoundmemap.repositry.services.marker.markerCluster

import android.content.Context
import com.google.android.gms.maps.GoogleMap
import com.google.maps.android.clustering.ClusterManager

class MarkerCluster(private val context: Context) {

    private lateinit var clusterManager: ClusterManager<MarkerItem>

    fun setupCluster(map: GoogleMap) {
        clusterManager = ClusterManager(context,map)
        map.setOnCameraIdleListener(clusterManager)
        map.setOnMarkerClickListener(clusterManager)
    }

    fun addMarkersToCluster() {
      //  TODO("Implement this method with arraylist of markers if they're in bounds")
    }

}