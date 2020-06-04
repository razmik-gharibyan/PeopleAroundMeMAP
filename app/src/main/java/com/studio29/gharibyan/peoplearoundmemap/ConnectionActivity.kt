package com.studio29.gharibyan.peoplearoundmemap

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

class ConnectionActivity: AppCompatActivity() {

    private var androidDefaultUEH: Thread.UncaughtExceptionHandler? = null

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_connection)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        Thread.setDefaultUncaughtExceptionHandler{thread: Thread,throwable: Throwable ->
            Log.e("ConnectionActivity", "exception is $throwable")
        }

        androidDefaultUEH = Thread.getDefaultUncaughtExceptionHandler()
    }



}