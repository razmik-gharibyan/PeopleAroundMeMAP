package com.studio29.gharibyan.peoplearoundmemap.repositry.connection

interface CheckConnectionsInter {

    suspend fun checkInternetAccess(): Boolean

    suspend fun checkGooglePlayServicesEnabled(): Boolean

    suspend fun checkGPSEnabled(): Boolean

}