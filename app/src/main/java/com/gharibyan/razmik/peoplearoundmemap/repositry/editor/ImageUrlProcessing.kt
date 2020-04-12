package com.gharibyan.razmik.peoplearoundmemap.repositry.editor

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.IOException
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL

class ImageUrlProcessing {

    fun processImage(imageUrl: String): Bitmap? {
        return try {
            val url = URL(imageUrl)
            val httpURLConnection = url.openConnection()
            httpURLConnection.doInput = true
            httpURLConnection.connect()
            val inputStream = httpURLConnection.getInputStream()
            BitmapFactory.decodeStream(inputStream)
        }catch (e: Exception) {
            null
        }
    }

}