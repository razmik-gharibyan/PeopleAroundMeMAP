package com.studio29.gharibyan.peoplearoundmemap.ui

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.studio29.gharibyan.peoplearoundmemap.ui.connection.ConnectionViewModel
import com.studio29.gharibyan.peoplearoundmemap.ui.map.MapViewModel
import java.lang.IllegalArgumentException

class CustomViewModelFactory(var context: Context,val lifecycleOwner: LifecycleOwner): ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T{
        if(modelClass.isAssignableFrom(ConnectionViewModel::class.java)) {
            return ConnectionViewModel(context) as T
        }else if(modelClass.isAssignableFrom(MapViewModel::class.java)) {
            return MapViewModel(context,lifecycleOwner) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

