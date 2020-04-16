package com.gharibyan.razmik.peoplearoundmemap.repositry.models.firestore

import com.google.firebase.firestore.GeoPoint

class FirestoreUserDAO{
    var token: String? = null
    var userName: String? = null
    var followers: Long? = null
    var picture: String? = null
    var location: GeoPoint? = null
    var isVisible: Boolean? = null
    var isPrivate: Boolean? = null
    var isVerified: Boolean? = null
    var documentId: String? = null

    companion object{

    }

    override fun equals(other: Any?): Boolean {
        other as FirestoreUserDAO
        /*
        if (documentId == other.documentId) return true
        if (userName == other.userName) return true
        if (followers == other.followers) return true
        if (location == other.location) return true
        if (picture == other.picture) return true
        if (token == other.token) return true
        if (isVisible == other.isVisible) return true
        if (isPrivate == other.isPrivate) return true
        if (isVerified == other.isVerified) return true

         */
        if (documentId == other.documentId &&
            userName == other.userName &&
            followers == other.followers &&
            location == other.location &&
            picture == other.picture &&
            token == other.token &&
            isVisible == other.isVisible &&
            isPrivate == other.isPrivate &&
            isVerified == other.isVerified) return true
        return false
    }

    override fun hashCode(): Int {
        var result = token?.hashCode() ?: 0
        result = 31 * result + (userName?.hashCode() ?: 0)
        result = 31 * result + (followers?.hashCode() ?: 0)
        result = 31 * result + (picture?.hashCode() ?: 0)
        result = 31 * result + (location?.hashCode() ?: 0)
        result = 31 * result + (isVisible?.hashCode() ?: 0)
        result = 31 * result + (isPrivate?.hashCode() ?: 0)
        result = 31 * result + (isVerified?.hashCode() ?: 0)
        result = 31 * result + (documentId?.hashCode() ?: 0)
        return result
    }
}