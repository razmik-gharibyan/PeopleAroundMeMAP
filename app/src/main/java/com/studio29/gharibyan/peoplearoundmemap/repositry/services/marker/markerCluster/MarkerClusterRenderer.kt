package com.studio29.gharibyan.peoplearoundmemap.repositry.services.marker.markerCluster

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import com.studio29.gharibyan.peoplearoundmemap.R
import com.studio29.gharibyan.peoplearoundmemap.repositry.editor.FollowerProcessing
import com.studio29.gharibyan.peoplearoundmemap.repositry.editor.ImageProcessing
import java.lang.Exception

class MarkerClusterRenderer<T: MarkerItem>(context: Context,
                                    map: GoogleMap?,
                                    clusterManager: ClusterManager<T>?
): DefaultClusterRenderer<T>(context, map, clusterManager) {

    private val TAG = javaClass.name
    private var clusterList = ArrayList<Cluster<T>>()
    private lateinit var context: Context
    private lateinit var clusterIcon: Bitmap
    private val imageProcessing = ImageProcessing(FollowerProcessing())

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
        var maxFollowerBitmap: Bitmap? = null
        for(item in cluster!!.items) {
            if(item.followers >= max) {
                max = item.followers
                userName = item.userName
                maxFollowerBitmap = item.bitmap
            }
        }

        if(!clusterList.contains(cluster)) {
            clusterList.add(cluster)
        }

        if(maxFollowerBitmap != null) {
            markerOptions!!.title("$userName and more")
                .icon(BitmapDescriptorFactory.fromBitmap(imageProcessing.mergeTwoBitmaps(clusterIcon, maxFollowerBitmap)))
        }

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
        var maxFollowerBitmap: Bitmap? = null
        for(item in cluster!!.items) {
            if(item.followers >= max) {
                max = item.followers
                maxFollowerBitmap = item.bitmap
            }
        }
        if(maxFollowerBitmap != null) {
            marker!!.setIcon(BitmapDescriptorFactory.fromBitmap(imageProcessing.mergeTwoBitmaps(clusterIcon, maxFollowerBitmap)))
        }
        super.onClusterRendered(cluster, marker)
    }

    fun setContext(context: Context) {
        this.context = context
        val clusterIconOriginal = BitmapFactory.decodeResource(context.resources, R.drawable.cluster_bg)
        clusterIcon = Bitmap.createScaledBitmap(clusterIconOriginal,250,250,false)
    }
}