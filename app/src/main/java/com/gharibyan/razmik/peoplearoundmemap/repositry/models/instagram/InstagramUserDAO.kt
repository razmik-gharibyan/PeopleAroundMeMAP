package com.gharibyan.razmik.peoplearoundmemap.repositry.models.instagram

class InstagramUserDAO {
    var documentId: String? = null
    var userName: String? = null
    var picture: String? = null
    var followers: Long? = null
    var token: String? = null
    var isPrivate: Boolean? = null
    var isVerified: Boolean? = null
    var isActive: Boolean? = null

    companion object {
    }
}