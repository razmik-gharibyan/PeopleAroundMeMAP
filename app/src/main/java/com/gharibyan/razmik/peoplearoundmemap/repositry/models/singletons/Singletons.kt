package com.gharibyan.razmik.peoplearoundmemap.repositry.models.singletons

import com.gharibyan.razmik.peoplearoundmemap.repositry.models.firestore.CurrentFirestoreUserDAO
import com.gharibyan.razmik.peoplearoundmemap.repositry.models.firestore.FirestoreUserDAO
import com.gharibyan.razmik.peoplearoundmemap.repositry.models.instagram.InstagramUserDAO

object Singletons {
    val instagramUserDAO = InstagramUserDAO()
    val firestoreUserDAO = FirestoreUserDAO()
    val currentFirestoreUserDAO = CurrentFirestoreUserDAO()
}