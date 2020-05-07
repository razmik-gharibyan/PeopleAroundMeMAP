package com.gharibyan.razmik.peoplearoundmemap.repositry.models.firestore

import com.google.firebase.firestore.GeoPoint

class CurrentFirestoreUserDAO {

    var token: String? = null
    var userName: String? = null
    var followers: Long? = null
    var picture: String? = null
    var location: GeoPoint? = null
    var isVisible: Boolean? = null
    var isPrivate: Boolean? = null
    var isVerified: Boolean? = null
    var documentId: String? = null
    var isActive: Boolean? = null

    companion object{

    }
}