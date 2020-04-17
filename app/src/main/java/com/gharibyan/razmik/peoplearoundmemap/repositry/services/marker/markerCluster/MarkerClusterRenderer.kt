package com.gharibyan.razmik.peoplearoundmemap.repositry.services.marker.markerCluster

import android.content.Context
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterItem
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer

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
}