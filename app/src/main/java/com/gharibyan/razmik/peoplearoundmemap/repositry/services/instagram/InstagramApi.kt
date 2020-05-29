package com.gharibyan.razmik.peoplearoundmemap.repositry.services.instagram

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.gharibyan.razmik.peoplearoundmemap.api.instagram.InstagramPlaceHolderApi
import com.gharibyan.razmik.peoplearoundmemap.repositry.models.instagram.InstagramUserDAO
import com.gharibyan.razmik.peoplearoundmemap.repositry.models.instagram.UserInfo
import com.gharibyan.razmik.peoplearoundmemap.repositry.models.instagram.UserToken
import com.gharibyan.razmik.peoplearoundmemap.repositry.models.instagram.UserTokenLong
import com.gharibyan.razmik.peoplearoundmemap.repositry.models.instagram.personal.ObjectResponse
import com.gharibyan.razmik.peoplearoundmemap.repositry.models.singletons.Singletons
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class InstagramApi(val instagramPlaceHolderApi: InstagramPlaceHolderApi) {

    // Constants
    private val TAG = javaClass.name
    private val OAUTH_ERROR_MESSAGE = "Invalid OAuth access token."

    // LiveData
    private var _infoSuccessLD = MutableLiveData<Boolean>()
    private var _oAuthErrorLD = MutableLiveData<Boolean>()
    var infoSuccess: LiveData<Boolean> = _infoSuccessLD
    var oAuthError: LiveData<Boolean> = _oAuthErrorLD

    // Variables
    private var userId: Long? = null
    private var longToken: String? = null

    // Initialization
    private var instagramUserDAO = Singletons.instagramUserDAO

    suspend fun getProfileInfo(code: String) {
        withContext(Dispatchers.IO) {
            getTokenByCode(code)
        }

    }

    private fun getTokenByCode(code: String) {
        val call: Call<UserToken> = instagramPlaceHolderApi.getToken(
            460485674626498L,
            "0962b86387a8461431728427dfb3a9e6",
            "authorization_code",
            "https://narsad.github.io/Loading-map/",
            code
        )
        call.enqueue(object :Callback<UserToken>{
            override fun onFailure(call: Call<UserToken>, t: Throwable) {
                t.printStackTrace()
            }

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
                instagramUserDAO.instagram_id = userId
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
            override fun onFailure(call: Call<UserInfo>, t: Throwable) {
                t.printStackTrace()
            }

            override fun onResponse(call: Call<UserInfo>, response: Response<UserInfo>) {
                if(!response.isSuccessful) {
                    val jsonObject = JSONObject(response.errorBody()!!.string())
                    val errorJsonObject = JSONObject(jsonObject.getString("error"))
                    val errorMassage = errorJsonObject.getString("message")
                    if(errorMassage == OAUTH_ERROR_MESSAGE) {
                        _oAuthErrorLD.value = true
                        return
                    }
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