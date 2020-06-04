package com.studio29.gharibyan.peoplearoundmemap.repositry.models.singletons

import com.studio29.gharibyan.peoplearoundmemap.repositry.models.firestore.CurrentFirestoreUserDAO
import com.studio29.gharibyan.peoplearoundmemap.repositry.models.firestore.FirestoreUserDAO
import com.studio29.gharibyan.peoplearoundmemap.repositry.models.instagram.InstagramUserDAO

object Singletons {
    val instagramUserDAO = InstagramUserDAO()
    val firestoreUserDAO = FirestoreUserDAO()
    val currentFirestoreUserDAO = CurrentFirestoreUserDAO()
}