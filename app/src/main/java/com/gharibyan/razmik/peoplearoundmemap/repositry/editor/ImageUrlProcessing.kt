package com.gharibyan.razmik.peoplearoundmemap.repositry.editor

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL

class ImageUrlProcessing {

    suspend fun processImage(imageUrl: String): Bitmap? {
        return withContext(Dispatchers.IO) {
            val url = URL(imageUrl)
            val httpURLConnection = url.openConnection()
            httpURLConnection.doInput = true
            httpURLConnection.connect()
            val inputStream = httpURLConnection.getInputStream()
            BitmapFactory.decodeStream(inputStream)
        }

    }

}