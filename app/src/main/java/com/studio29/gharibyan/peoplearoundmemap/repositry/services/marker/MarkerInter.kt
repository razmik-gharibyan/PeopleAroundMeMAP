package com.studio29.gharibyan.peoplearoundmemap.repositry.services.marker

import android.content.Context
import com.studio29.gharibyan.peoplearoundmemap.repositry.models.firestore.FirestoreUserDAO
import com.studio29.gharibyan.peoplearoundmemap.repositry.models.markers.MarkerDAO

interface MarkerInter {

    suspend fun addMarker(firestoreUserDAO: FirestoreUserDAO, moveCamera: Boolean, context: Context): MarkerDAO?

}