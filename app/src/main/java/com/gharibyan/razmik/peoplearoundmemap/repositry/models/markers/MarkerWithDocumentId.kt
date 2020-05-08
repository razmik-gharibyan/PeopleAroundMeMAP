package com.gharibyan.razmik.peoplearoundmemap.repositry.models.markers

import com.gharibyan.razmik.peoplearoundmemap.repositry.models.firestore.FirestoreUserDAO
import com.gharibyan.razmik.peoplearoundmemap.repositry.services.marker.markerCluster.MarkerItem
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class MarkerWithDocumentId {

    var markerItem: MarkerItem? = null
    var documentId: String? = null
    var markerOptions: MarkerOptions? = null
    var firestoreUserDAO: FirestoreUserDAO? = null
}