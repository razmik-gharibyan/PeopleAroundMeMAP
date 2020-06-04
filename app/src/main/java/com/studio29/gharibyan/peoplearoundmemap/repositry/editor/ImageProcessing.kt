package com.studio29.gharibyan.peoplearoundmemap.repositry.editor

import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.util.Base64
import android.util.DisplayMetrics
import android.util.TypedValue
import java.io.ByteArrayOutputStream


class ImageProcessing(followerProcessing: FollowerProcessing) {
        private val followerProcessing: FollowerProcessing

        //This method takes users bitmap and remake it with prefered width and height
        fun getResizedBitmap(bitmap: Bitmap?, followers: Long, context: Context): Bitmap {
            val picWidthHeight: IntArray = followerProcessing.picSizeViaFollower(followers)
            return Bitmap.createScaledBitmap(
                bitmap!!,
                picWidthHeight[0],
                picWidthHeight[1],
                false
            )
        }

        private fun convertPixelsToDp(px: Float, context: Context): Float {
            return px * (context.getResources()
                .getDisplayMetrics().densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
        }

        private fun convertDpToPixels(dp: Int, context: Context): Float {
            val r: Resources = context.resources
            return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,dp.toFloat(),r.displayMetrics)
        }

        //This Method resizing bitmap to UserList Fragment size type
        fun getResizedBitmapForUserListFragment(bitmap: Bitmap?): Bitmap {
            return Bitmap.createScaledBitmap(bitmap!!, 100, 100, false)
        }

        fun getResizedBitmapWithAspectRatio(bitmap: Bitmap?): Bitmap {
            val maxHeight = 100
            val maxWidth = 100
            if (maxHeight > 0 && maxWidth > 0) {
                val width: Int = bitmap!!.width
                val height: Int = bitmap.height
                val ratioBitmap = width.toFloat() / height.toFloat()
                val ratioMax =
                    maxWidth.toFloat() / maxHeight.toFloat()
                var finalWidth = maxWidth
                var finalHeight = maxHeight
                if (ratioMax > 1) {
                    finalWidth = (maxHeight.toFloat() * ratioBitmap).toInt()
                } else {
                    finalHeight = (maxWidth.toFloat() / ratioBitmap).toInt()
                }
                return Bitmap.createScaledBitmap(bitmap, finalWidth, finalHeight, true)
            } else {
                return bitmap!!
            }
        }

        fun scaleBitmapAndKeepRatio(targetBmp: Bitmap?): Bitmap {
            val reqHeightInPixels = 100
            val reqWidthInPixels = 100
            val matrix = Matrix()
            matrix.setRectToRect(
            RectF(0F, 0F, targetBmp!!.width.toFloat(), targetBmp.height.toFloat()),
            RectF(0F, 0F, reqWidthInPixels.toFloat(), reqHeightInPixels.toFloat()),
            Matrix.ScaleToFit.CENTER
            )
            return Bitmap.createBitmap(targetBmp, 0, 0, targetBmp.width, targetBmp.height, matrix, true)
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
                    Base64.decode(string, Base64.URL_SAFE)
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