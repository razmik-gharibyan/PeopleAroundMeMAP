package com.gharibyan.razmik.peoplearoundmemap.repositry.models.markers

import com.gharibyan.razmik.peoplearoundmemap.repositry.models.firestore.FirestoreUserDAO
import com.gharibyan.razmik.peoplearoundmemap.repositry.models.firestore.clone
import com.gharibyan.razmik.peoplearoundmemap.repositry.services.marker.markerCluster.MarkerItem
import com.gharibyan.razmik.peoplearoundmemap.repositry.services.marker.markerCluster.clone
import com.google.android.gms.maps.model.MarkerOptions

data class MarkerWithDocumentId(
    var markerItem: MarkerItem? = null,
    var documentId: String? = null,
    var markerOptions: MarkerOptions? = null,
    var firestoreUserDAO: FirestoreUserDAO? = null
)

fun MarkerWithDocumentId.clone() = with(this) {
    MarkerWithDocumentId(
        markerItem = markerItem?.clone(),
        documentId = documentId,
        markerOptions = markerOptions,
        firestoreUserDAO = firestoreUserDAO?.clone())
}