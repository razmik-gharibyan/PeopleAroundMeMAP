package com.studio29.gharibyan.peoplearoundmemap.repositry.models.markers

import android.graphics.Bitmap
import com.studio29.gharibyan.peoplearoundmemap.repositry.models.firestore.FirestoreUserDAO
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MarkerDAO {

    var markerOptions: MarkerOptions? = null
    var moveCamera: Boolean? = null
    var latLng: LatLng? = null
    var documentId: String? = null
    var firestoreUserDAO: FirestoreUserDAO? = null
    var markerBitmap: Bitmap? = null
}