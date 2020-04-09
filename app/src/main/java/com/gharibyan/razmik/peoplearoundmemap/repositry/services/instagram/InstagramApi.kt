package com.gharibyan.razmik.peoplearoundmemap.repositry.services.instagram

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.gharibyan.razmik.peoplearoundmemap.api.instagram.InstagramPlaceHolderApi
import com.gharibyan.razmik.peoplearoundmemap.repositry.models.instagram.InstagramUserDAO
import com.gharibyan.razmik.peoplearoundmemap.repositry.models.instagram.UserInfo
import com.gharibyan.razmik.peoplearoundmemap.repositry.models.instagram.UserToken
import com.gharibyan.razmik.peoplearoundmemap.repositry.models.instagram.UserTokenLong
import com.gharibyan.razmik.peoplearoundmemap.repositry.models.instagram.personal.ObjectResponse
import com.gharibyan.razmik.peoplearoundmemap.repositry.models.singletons.Singletons
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class InstagramApi(val instagramPlaceHolderApi: InstagramPlaceHolderApi) {

    // LiveData
    private var _infoSuccessLD = MutableLiveData<Boolean>()
    var infoSuccess: LiveData<Boolean> = _infoSuccessLD

    // Variables
    private var userId: Long? = null
    private var longToken: String? = null

    // Initialization
    private var instagramUserDAO = Singletons.instagramUserDAO

    fun getProfileInfo(code: String) {
        getTokenByCode(code)
    }

    private fun getTokenByCode(code: String) {
        val call: Call<UserToken> = instagramPlaceHolderApi.getToken(
            460485674626498L,
            "0962b86387a8461431728427dfb3a9e6",
            "authorization_code",
            "https://github.com/R43M1K",
            code
        )
        call.enqueue(object :Callback<UserToken>{
            override fun onFailure(call: Call<UserToken>, t: Throwable) {}

            override fun onResponse(call: Call<UserToken>, response: Response<UserToken>) {
                if(!response.isSuccessful) {
                    return
                }
                val userToken = response.body()
                if (userToken != null) {
                    userId = userToken.user_id
                }

                getLongToken(userToken?.access_token!!,userToken.user_id!!)
            }

        })
    }

    fun getLongToken(shortToken: String,userId: Long) {
        val call: Call<UserTokenLong> = instagramPlaceHolderApi.getLongToken(
            "ig_exchange_token",
            "0962b86387a8461431728427dfb3a9e6",
            shortToken
        )
        call.enqueue(object :Callback<UserTokenLong> {
            override fun onFailure(call: Call<UserTokenLong>, t: Throwable) {}

            override fun onResponse(call: Call<UserTokenLong>, response: Response<UserTokenLong>) {
                if(!response.isSuccessful) {
                    return
                }
                val userTokenLong = response.body()
                if (userTokenLong != null) {
                    longToken = userTokenLong.access_token
                }
                instagramUserDAO.token = userTokenLong?.access_token
                getUserInfo(userId, userTokenLong?.access_token!!)
            }

        })
    }

    fun getUserInfo(userId: Long, access_token: String) {
        val call: Call<UserInfo> = instagramPlaceHolderApi.getUserInfo(
            userId,
            "account_type,id,media_count,username",
            access_token
        )
        call.enqueue(object: Callback<UserInfo> {
            override fun onFailure(call: Call<UserInfo>, t: Throwable) {}

            override fun onResponse(call: Call<UserInfo>, response: Response<UserInfo>) {
                if(!response.isSuccessful) {
                    return
                }
                val userInfo = response.body()
                instagramUserDAO.userName = userInfo?.username
                getUserPersonal(userInfo?.username!!)
            }
        })
    }

    fun getUserPersonal(username: String) {
        val call: Call<ObjectResponse> = instagramPlaceHolderApi.getUserPersonal(
            username,
            1
        )
        call.enqueue(object: Callback<ObjectResponse> {
            override fun onFailure(call: Call<ObjectResponse>, t: Throwable) {}

            override fun onResponse(call: Call<ObjectResponse>, response: Response<ObjectResponse>) {
                if(!response.isSuccessful) {
                    return
                }
                val objectResponse = response.body()
                _infoSuccessLD.value = true

                instagramUserDAO.picture = objectResponse?.graphql?.user?.profilePicUrlHD
                instagramUserDAO.followers = objectResponse?.graphql?.user?.edgeFollowedBy?.followersCount
                instagramUserDAO.isPrivate = objectResponse?.graphql?.user?.isPrivate
                instagramUserDAO.isVerified = objectResponse?.graphql?.user?.isVerified
            }
        })
    }

}