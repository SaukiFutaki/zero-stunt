package com.saukikikiki.zerostunt.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private const val BASE_URL = "http://10.0.2.2:5000"
    private const val DAE_URL = "http://34.128.103.130:5000"
    private const val SCAN_URL = "http://34.128.103.130:6000"

    val authService: AuthService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthService::class.java)
    }

    val detectAndEvaluateService by lazy {
        Retrofit.Builder()
            .baseUrl(DAE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DetectAndEvaluateService::class.java)
    }

    val scanService by lazy {
        Retrofit.Builder()
            .baseUrl(SCAN_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ScanService::class.java)
    }


}