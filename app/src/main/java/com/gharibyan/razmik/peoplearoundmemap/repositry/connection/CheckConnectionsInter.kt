package com.gharibyan.razmik.peoplearoundmemap.repositry.connection

interface CheckConnectionsInter {

    suspend fun checkInternetAccess(): Boolean

    suspend fun checkGooglePlayServicesEnabled(): Boolean

    suspend fun checkGPSEnabled(): Boolean

}