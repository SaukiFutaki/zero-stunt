package com.saukikikiki.zerostunt.data.api

import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

data class LoginRequest(val email: String, val password: String)
data class LoginResponse(val success: Boolean, val message: String, val token: String?)
data class RegisterRequest(val name: String, val email: String, val password: String)
data class RegisterResponse(val success: Boolean, val message: String)


interface AuthService {
    @POST("/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @POST("/register")
    fun register(@Body request: RegisterRequest): Call<RegisterResponse>
}

interface ApiService {
    @Multipart
    @POST("predict")
    fun predict(@Part file: MultipartBody.Part): Call<ResponseBody>
}
