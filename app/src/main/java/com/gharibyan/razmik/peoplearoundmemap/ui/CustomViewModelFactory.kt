package com.gharibyan.razmik.peoplearoundmemap.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.gharibyan.razmik.peoplearoundmemap.ui.connection.ConnectionViewModel
import com.gharibyan.razmik.peoplearoundmemap.ui.map.MapViewModel
import java.lang.IllegalArgumentException

class CustomViewModelFactory(var context: Context): ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T{
        if(modelClass.isAssignableFrom(ConnectionViewModel::class.java)) {
            return ConnectionViewModel(
                context
            ) as T
        }else if(modelClass.isAssignableFrom(MapViewModel::class.java)) {
            return MapViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

