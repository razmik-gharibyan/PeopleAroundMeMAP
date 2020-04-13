package com.gharibyan.razmik.peoplearoundmemap.repositry.services.marker

import com.gharibyan.razmik.peoplearoundmemap.repositry.models.firestore.FirestoreUserDAO
import com.gharibyan.razmik.peoplearoundmemap.repositry.models.markers.MarkerDAO

interface MarkerInter {

    suspend fun addMarker(firestoreUserDAO: FirestoreUserDAO, moveCamera: Boolean): MarkerDAO?

}