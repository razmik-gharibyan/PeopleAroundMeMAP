package com.gharibyan.razmik.peoplearoundmemap.repositry.models.markers

import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class MarkerWithDocumentId {

    var marker: Marker? = null
    var documentId: String? = null
    var markerOptions: MarkerOptions? = null
}