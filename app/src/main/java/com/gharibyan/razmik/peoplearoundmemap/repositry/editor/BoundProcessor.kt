package com.gharibyan.razmik.peoplearoundmemap.repositry.editor

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLngBounds
import com.google.firebase.firestore.GeoPoint

class BoundProcessor {

    fun isUserInBounds(map: GoogleMap, userLocation: GeoPoint): Boolean {
        val bounds: LatLngBounds = map.getProjection().getVisibleRegion().latLngBounds

        val neLongitude = bounds.northeast.longitude
        val neLatitude = bounds.northeast.latitude
        val swLongitude = bounds.southwest.longitude
        val swLatitude = bounds.southwest.latitude

        val myLongitude: Double = userLocation.getLongitude()
        val myLatitude: Double = userLocation.getLatitude()

        return if (myLatitude in swLatitude..neLatitude) {
            myLongitude in swLongitude..neLongitude
        }else {
            false
        }
    }

}