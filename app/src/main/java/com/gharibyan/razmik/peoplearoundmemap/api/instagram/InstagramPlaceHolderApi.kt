package com.gharibyan.razmik.peoplearoundmemap.api.instagram

import com.gharibyan.razmik.peoplearoundmemap.repositry.models.instagram.UserInfo
import com.gharibyan.razmik.peoplearoundmemap.repositry.models.instagram.UserToken
import com.gharibyan.razmik.peoplearoundmemap.repositry.models.instagram.UserTokenLong
import com.gharibyan.razmik.peoplearoundmemap.repositry.models.instagram.personal.ObjectResponse
import retrofit2.Call
import retrofit2.http.*

interface InstagramPlaceHolderApi {

    @FormUrlEncoded
    @POST("https://api.instagram.com/oauth/access_token")
    fun getToken(
        @Field("app_id") appId: Long,
        @Field("app_secret") appSecret: String,
        @Field("grant_type") grantType: String,
        @Field("redirect_uri") redirectUrl: String,
        @Field("code") code: String
    ): Call<UserToken>

    @GET("https://graph.instagram.com/access_token")
    fun getLongToken(
        @Query("grant_type") grantType: String,
        @Query("client_secret") clientSecret: String,
        @Query("access_token") accessToken: String
    ): Call<UserTokenLong>

    @GET("https://graph.instagram.com/{user_id}")
    fun getUserInfo(
        @Path("user_id") userId: Long,
        @Query("fields") fields: String,
        @Query("access_token") accessToken: String
    ): Call<UserInfo>

    @GET("https://www.instagram.com/{username}")
    fun getUserPersonal(
        @Path("username") username: String,
        @Query("__a") a: Int
    ): Call<ObjectResponse>

}