package com.gharibyan.razmik.peoplearoundmemap.repositry.editor

import android.graphics.*
import android.util.Base64
import java.io.ByteArrayOutputStream

class ImageProcessing(followerProcessing: FollowerProcessing) {
        private val followerProcessing: FollowerProcessing

        //This method takes users bitmap and remake it with prefered width and height
        fun getResizedBitmap(bitmap: Bitmap?, followers: Long): Bitmap {
            val PicWidthHeight: IntArray = followerProcessing.picSizeViaFollower(followers)
            return Bitmap.createScaledBitmap(
                bitmap!!,
                PicWidthHeight[0],
                PicWidthHeight[1],
                false
            )
        }

        //This Method resizing bitmap to UserList Fragment size type
        fun getResizedBitmapForUserListFragment(bitmap: Bitmap?): Bitmap {
            return Bitmap.createScaledBitmap(bitmap!!, 100, 100, false)
        }

        //CONVERT BITMAP TO STRING
        fun bitmapToString(bitmap: Bitmap): String {
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
            val b = baos.toByteArray()
            return Base64.encodeToString(b, Base64.DEFAULT)
        }

        //CONVERT STRING TO BITMAP
        fun stringToBitmap(string: String?): Bitmap? {
            return try {
                val encodeByte =
                    Base64.decode(string, Base64.DEFAULT)
                BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.size)
            } catch (e: Exception) {
                e.message
                null
            }
        }

        //This method makes standard image to circle image as Instagram have
        fun getCroppedBitmap(bitmap: Bitmap): Bitmap {
            val output =
                Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(output)
            val color = -0xbdbdbe
            val paint = Paint()
            val rect =
                Rect(0, 0, bitmap.width, bitmap.height)
            paint.isAntiAlias = true
            canvas.drawARGB(0, 0, 0, 0)
            paint.color = color
            canvas.drawCircle(
                bitmap.width / 2.toFloat(),
                bitmap.height / 2.toFloat(),
                bitmap.width / 2.toFloat(),
                paint
            )
            paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
            canvas.drawBitmap(bitmap, rect, rect, paint)
            return output
        }

        init {
            this.followerProcessing = followerProcessing
        }
}