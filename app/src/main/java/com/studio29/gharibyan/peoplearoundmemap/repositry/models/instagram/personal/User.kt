package com.studio29.gharibyan.peoplearoundmemap.repositry.models.instagram.personal

import com.google.gson.annotations.SerializedName

class User {

    @SerializedName("profile_pic_url")
    val profilePicUrlHD: String? = null

    @SerializedName("edge_followed_by")
    val edgeFollowedBy: EdgeFollowedBy? = null

    @SerializedName("is_private")
    val isPrivate: Boolean? = null

    @SerializedName("is_verified")
    val isVerified: Boolean? = null

}