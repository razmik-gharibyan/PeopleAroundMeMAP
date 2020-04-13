package com.gharibyan.razmik.peoplearoundmemap.repositry.models.markers

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.GeoPoint

class MarkerDAO {
    var markerOptions: MarkerOptions? = null
    var moveCamera: Boolean? = null
    var latLng: LatLng? = null
    var documentId: String? = null
}