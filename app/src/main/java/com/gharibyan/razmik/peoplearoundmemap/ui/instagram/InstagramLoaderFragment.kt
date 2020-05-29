package com.gharibyan.razmik.peoplearoundmemap.ui.instagram

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.gharibyan.razmik.peoplearoundmemap.MainActivity
import com.gharibyan.razmik.peoplearoundmemap.R
import com.gharibyan.razmik.peoplearoundmemap.api.instagram.InstagramPlaceHolderApi
import com.gharibyan.razmik.peoplearoundmemap.repositry.models.singletons.Singletons
import com.gharibyan.razmik.peoplearoundmemap.repositry.services.firestore.FirestoreApi
import com.gharibyan.razmik.peoplearoundmemap.repositry.services.instagram.InstagramApi
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.nio.charset.Charset
import java.security.Key
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

class InstagramLoaderFragment: Fragment() {

    // Constants
    private val authorizeUrl = "https://api.instagram.com/oauth/authorize?app_id=460485674626498&redirect_uri" +
                               "=https://narsad.github.io/Loading-map/?fbclid=IwAR0PWa2a1FdrIBm1y3UayI3OMOxBsxbxkBfXv_ibKksmmCzg3MHXgNqTuaM" +
                               "&scope=user_profile,user_media&response_type=code"
    private val redirectFullBeforeCode = "https://narsad.github.io/Loading-map/?code="
    private val redirectBeforeCode = "Loading-map/?code="
    private val redirectAfterCode = "#_"

    private val key = "1Hbfh667adfDEJ78"
    private val ALGORITHM = "AES"

    // Variables
    private lateinit var code: String
    private lateinit var auth: FirebaseAuth
    private var instagramUserDAO = Singletons.instagramUserDAO
    private var currentFirestoreUserDAO = Singletons.currentFirestoreUserDAO
    private var checkToken = false

    // Classes
    private lateinit var instagramPlaceHolderApi: InstagramPlaceHolderApi
    private lateinit var instagramApi: InstagramApi
    private val firestoreApi = FirestoreApi()

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
                }
                return false
            }
        }
        webView.loadUrl(authorizeUrl)

        listenToInstagramLoginSuccess()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun listenToInstagramLoginSuccess() {
        instagramApi.infoSuccess.observe(viewLifecycleOwner, Observer {
            if(it) {
                // Enter here if successfully recieved user information from instagram
                val email = encryptEmail(instagramUserDAO.userName!!)
                val password = encryptPassword(instagramUserDAO.userName!!)
                signInUser(email, password)
            }else{
                throw Exception("Error loading user instagram data")
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkIfUserSignedIn() {
        val currentUser = auth.currentUser
        if(currentUser != null) {
            // Enter here if user is logged in and open map after getting info from
            // its same uid document from firestore database
            instagramUserDAO.documentId = currentUser.uid
            CoroutineScope(Dispatchers.IO).launch {
                firestoreApi.findUserWithDocumentId(currentUser.uid)
            }
        }else{
            // Enter if user is not logged in, and open instagram panel
            openWebView()
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
                auth.currentUser!!.delete()
                openWebView()
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


    private fun signInUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email,password)
            .addOnCompleteListener {
                if(it.isSuccessful) {
                    // Enter here if user is logged in correctly and find it's document in database
                    val currentUser = it.result!!.user!!.uid
                    instagramUserDAO.documentId = currentUser
                    CoroutineScope(Dispatchers.IO).launch {
                        firestoreApi.findUserWithDocumentId(currentUser)
                    }
                }else{
                    // Enter here if user is not logged in correctly (not registered)
                    auth.createUserWithEmailAndPassword(email,password)
                        .addOnCompleteListener {
                            if(it.isSuccessful) {
                                val currentUser = it.result!!.user!!.uid
                                instagramUserDAO.documentId = currentUser
                                currentFirestoreUserDAO.documentId = currentUser
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
                                    firestoreApi.addNewUserWithUID(currentFirestoreUserDAO,currentUser)
                                    openMapActivity()
                                }
                            }
                        }
                }
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun encryptPassword(password: String): String {
        val key: Key = generateKey()
        val cipher: Cipher = Cipher.getInstance(ALGORITHM)
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val encryptedByteValue: ByteArray = cipher.doFinal(password.toByteArray(Charset.defaultCharset()))
        return Base64.getEncoder().encodeToString(encryptedByteValue)
    }

    private fun generateKey(): Key {
        return SecretKeySpec(key.toByteArray(Charset.defaultCharset()), ALGORITHM)
    }

    private fun encryptEmail(username: String): String {
        return "$username@razmikgharibyanpammap.com"
    }
}