package com.studio29.gharibyan.peoplearoundmemap.ui.instagram

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.studio29.gharibyan.peoplearoundmemap.MainActivity
import com.studio29.gharibyan.peoplearoundmemap.R
import com.studio29.gharibyan.peoplearoundmemap.api.instagram.InstagramPlaceHolderApi
import com.studio29.gharibyan.peoplearoundmemap.repositry.models.singletons.Singletons
import com.studio29.gharibyan.peoplearoundmemap.repositry.services.firestore.FirestoreApi
import com.studio29.gharibyan.peoplearoundmemap.repositry.services.instagram.InstagramApi
import com.google.firebase.auth.FirebaseAuth
import com.studio29.gharibyan.peoplearoundmemap.ConnectionActivity
import com.studio29.gharibyan.peoplearoundmemap.ui.connection.ConnectionViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class InstagramLoaderFragment: Fragment() {

    // Constants
    private val authorizeUrl = "https://api.instagram.com/oauth/authorize?app_id=460485674626498&redirect_uri" +
                               "=https://narsad.github.io/Loading-map/?fbclid=IwAR0PWa2a1FdrIBm1y3UayI3OMOxBsxbxkBfXv_ibKksmmCzg3MHXgNqTuaM" +
                               "&scope=user_profile,user_media&response_type=code"
    private val redirectFullBeforeCode = "https://narsad.github.io/Loading-map/?code="
    private val redirectBeforeCode = "Loading-map/?code="
    private val redirectAfterCode = "#_"
    private val logoutUrl = "https://www.instagram.com/"

    private val key = "1Hbfh667adfDEJ78"
    private val ALGORITHM = "AES"

    // Variables
    private lateinit var code: String
    private lateinit var auth: FirebaseAuth
    private var instagramUserDAO = Singletons.instagramUserDAO
    private var currentFirestoreUserDAO = Singletons.currentFirestoreUserDAO
    private var checkToken = false
    private var registerUser = false

    // Classes
    private lateinit var instagramPlaceHolderApi: InstagramPlaceHolderApi
    private lateinit var instagramApi: InstagramApi
    private val firestoreApi = FirestoreApi()
    lateinit var connectionViewModel: ConnectionViewModel

    // Views
    private lateinit var webView: WebView
    private lateinit var splashTextView: TextView

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.instagram_loader,container,false)

        auth = FirebaseAuth.getInstance()

        webView = view.findViewById(R.id.web_view)
        splashTextView = view.findViewById(R.id.splash_text)


        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.instagram.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        instagramPlaceHolderApi = retrofit.create(InstagramPlaceHolderApi::class.java)
        instagramApi = InstagramApi(instagramPlaceHolderApi)

        checkIfUserSignedIn()
        listenToCurrentUser()
        listenToInstagramLoginSuccess()
        oAuthErrorListener()

        return view
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun openWebView() {
        splashTextView.visibility = View.GONE
        val webSettings: WebSettings = webView.settings
        webSettings.javaScriptEnabled = true
        webView.webViewClient = object: WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                        if(url!!.contains(redirectFullBeforeCode)) {
                            val index = url.lastIndexOf(redirectBeforeCode) + redirectBeforeCode.length
                            val hashTagIndex = url.lastIndexOf(redirectAfterCode)
                            code = url.substring(index,hashTagIndex)
                            CoroutineScope(Dispatchers.Main).launch {
                                instagramApi.getProfileInfo(code)
                            }
                        }else if(url.equals(logoutUrl)) {
                            openWebView()
                }
                return false
            }
        }
        webView.loadUrl(authorizeUrl)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun listenToInstagramLoginSuccess() {
        instagramApi.infoSuccess.observe(viewLifecycleOwner, Observer {
            if(it) {
                // Enter here if successfully received user information from instagram
                logInOrRegisterUserToFirebase()
            }else{
                throw Exception("Error loading user instagram data")
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkIfUserSignedIn() {
        val currentUser = connectionViewModel.currentUserID!!
        // Enter here if user is logged in and open map after getting info from
        // its same uid document from firestore database
        instagramUserDAO.documentId = currentUser
        CoroutineScope(Dispatchers.IO).launch {
            firestoreApi.findUserWithDocumentId(currentUser)
        }
    }

    private fun openMapActivity() {
        val intent = Intent(activity,MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun listenToCurrentUser() {
        firestoreApi.currentDocumentId.observe(viewLifecycleOwner, Observer {
            if(it == null) {
                // Enter here if there is no user logged in
                if(registerUser) {
                    registerUserToFirebase(connectionViewModel.currentUserID!!)
                }else {
                    openWebView()
                    registerUser = true
                }
            }else {
                // Enter here if user is logged in, get firebase document for it
                // then check if token for that user is expired or not
                instagramUserDAO.documentId = it.documentId
                if(!checkToken) {
                    instagramUserDAO.userName = it.userName
                    instagramUserDAO.followers = it.followers
                    instagramUserDAO.picture = it.picture
                    instagramUserDAO.token = it.token
                    instagramUserDAO.isPrivate = it.isPrivate
                    instagramUserDAO.isVerified = it.isVerified
                    instagramUserDAO.instagram_id = it.instagram_id
                    instagramUserDAO.isActive = true

                    instagramApi.getUserInfo(instagramUserDAO.instagram_id!!, instagramUserDAO.token!!)
                    checkToken = true
                }else{
                    // Enter here if token is still valid and not expired, open map after this step
                    currentFirestoreUserDAO.documentId = instagramUserDAO.documentId
                    currentFirestoreUserDAO.userName = instagramUserDAO.userName
                    currentFirestoreUserDAO.followers = instagramUserDAO.followers
                    currentFirestoreUserDAO.picture = instagramUserDAO.picture
                    currentFirestoreUserDAO.token = instagramUserDAO.token
                    currentFirestoreUserDAO.isPrivate = instagramUserDAO.isPrivate
                    currentFirestoreUserDAO.isVerified = instagramUserDAO.isVerified
                    currentFirestoreUserDAO.isVisible = false
                    currentFirestoreUserDAO.isActive = true
                    currentFirestoreUserDAO.instagram_id = instagramUserDAO.instagram_id
                    openMapActivity()
                }
            }
        })
    }


    private fun logInOrRegisterUserToFirebase() {
        val currentUser = connectionViewModel.currentUserID
        instagramUserDAO.documentId = currentUser
        // Enter here if user is already registered, and should just log in to Instagram
        CoroutineScope(Dispatchers.IO).launch {
            firestoreApi.findUserWithDocumentId(currentUser!!)
        }
    }

    private fun registerUserToFirebase(currentUserId: String) {
        currentFirestoreUserDAO.documentId = currentUserId
        currentFirestoreUserDAO.userName = instagramUserDAO.userName
        currentFirestoreUserDAO.followers = instagramUserDAO.followers
        currentFirestoreUserDAO.picture = instagramUserDAO.picture
        currentFirestoreUserDAO.token = instagramUserDAO.token
        currentFirestoreUserDAO.isPrivate = instagramUserDAO.isPrivate
        currentFirestoreUserDAO.isVerified = instagramUserDAO.isVerified
        currentFirestoreUserDAO.isVisible = false
        currentFirestoreUserDAO.isActive = true
        currentFirestoreUserDAO.instagram_id = instagramUserDAO.instagram_id
        CoroutineScope(Dispatchers.Main).launch {
            firestoreApi.addNewUserWithUID(currentFirestoreUserDAO,currentUserId)
            openMapActivity()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun oAuthErrorListener() {
        instagramApi.oAuthError.observe(viewLifecycleOwner, Observer {
            if(it) {
                // Enter here if user token is expired, so redirect user to get new token
                openWebView()
            }
        })
    }

    override fun onAttach(activity: Activity) {
        connectionViewModel = (activity as ConnectionActivity).connectionViewModel
        super.onAttach(activity)
    }
}