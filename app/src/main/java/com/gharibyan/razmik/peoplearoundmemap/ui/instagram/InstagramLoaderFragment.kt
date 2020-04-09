package com.gharibyan.razmik.peoplearoundmemap.ui.instagram

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import com.gharibyan.razmik.peoplearoundmemap.MainActivity
import com.gharibyan.razmik.peoplearoundmemap.R
import com.gharibyan.razmik.peoplearoundmemap.api.instagram.InstagramPlaceHolderApi
import com.gharibyan.razmik.peoplearoundmemap.repositry.services.instagram.InstagramApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception

class InstagramLoaderFragment: Fragment() {

    // Constants
    private val authorizeUrl = "https://api.instagram.com/oauth/authorize?app_id=460485674626498&redirect_uri" +
                               "=https://github.com/R43M1K&scope=user_profile,user_media&response_type=code"
    private val redirectFullBeforeCode = "https://github.com/R43M1K?code="
    private val redirectBeforeCode = "R43M1K?code="
    private val redirectAfterCode = "#_"

    // Variables
    private lateinit var code: String

    // Classes
    private lateinit var instagramPlaceHolderApi: InstagramPlaceHolderApi
    private lateinit var instagramApi: InstagramApi

    // Views
    private lateinit var webView: WebView


    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.instagram_loader,container,false)

        webView = view.findViewById(R.id.web_view)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.instagram.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        instagramPlaceHolderApi = retrofit.create(InstagramPlaceHolderApi::class.java)
        instagramApi = InstagramApi(instagramPlaceHolderApi)

        var webSettings: WebSettings = webView.settings
        webSettings.javaScriptEnabled = true
        webView.webViewClient = object: WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                if(url!!.contains(redirectFullBeforeCode)) {
                    val index = url.lastIndexOf(redirectBeforeCode) + redirectBeforeCode.length
                    val hashTagIndex = url.lastIndexOf(redirectAfterCode)
                    code = url.substring(index,hashTagIndex)
                    instagramApi.getProfileInfo(code)
                }
                return false
            }
        }

        webView.loadUrl(authorizeUrl)

        instagramApi.infoSuccess.observe(viewLifecycleOwner, Observer {
            if(it) {
                val intent = Intent(activity,MainActivity::class.java)
                startActivity(intent)
            }else{
                throw Exception("Error loading user instagram data")
            }
        })

        return view
    }
}