package com.studio29.gharibyan.peoplearoundmemap.repositry.services.marker.markerCluster

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import com.studio29.gharibyan.peoplearoundmemap.R
import java.lang.Exception

class MarkerClusterRenderer<T: MarkerItem>(context: Context,
                                    map: GoogleMap?,
                                    clusterManager: ClusterManager<T>?
): DefaultClusterRenderer<T>(context, map, clusterManager) {

    private val TAG = javaClass.name
    private var clusterList = ArrayList<Cluster<T>>()
    private lateinit var context: Context
    private lateinit var clusterIcon: Bitmap

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

        markerOptions!!.title("$userName and more")
            .icon(BitmapDescriptorFactory.fromBitmap(clusterIcon))

    }

    fun updateMarker(markerItem: MarkerItem?, newPosition: LatLng) {
        try {
            getMarker(markerItem as T).position = newPosition
        }catch (e: Exception) {
            Log.d(TAG,"Exception is $e")
        }
    }

    override fun onClusterRendered(cluster: Cluster<T>?, marker: Marker?) {
        var max: Long = 0
        for(item in cluster!!.items) {
            if(item.followers >= max) {
                max = item.followers

            }
        }
        marker!!.setIcon(BitmapDescriptorFactory.fromBitmap(clusterIcon))
        super.onClusterRendered(cluster, marker)
    }

    fun setContext(context: Context) {
        this.context = context
        clusterIcon = BitmapFactory.decodeResource(context.resources, R.drawable.cluster_bg)
    }
}