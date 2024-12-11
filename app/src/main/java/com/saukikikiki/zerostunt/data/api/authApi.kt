package com.saukikikiki.zerostunt.data.api

import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

data class LoginRequest(val email: String, val password: String)
data class LoginResponse(
    val success: Boolean,
    val message: String,
    val token: String?,
    val uid: String?
)

data class RegisterRequest(val name: String, val email: String, val password: String)
data class RegisterResponse(val success: Boolean, val message: String)


data class UserData(
    val email: String,
    val name: String,
    val createdAt: Map<String, Long>
)

data class User(
    val uid: String,
    val data: UserData
)

data class UserResponse(val success: Boolean, val message: String, val user: User)
data class DetectRequest(
    val foods: List<String>
)

data class DetectResponse(
    val recommendations: String,
    val summary: String,
    val success: Boolean,
    val message: String
)

interface AuthService {
    @POST("/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @POST("/register")
    fun register(@Body request: RegisterRequest): Call<RegisterResponse>

    @GET("/user/{uid}")
    fun getUser(@Path("uid") uid: String): Call<UserResponse>
}

interface DetectAndEvaluateService {
    @POST("/detect_and_evaluate")
    fun detect(@Body request: DetectRequest): Call<DetectResponse>
}

interface ScanService {
    @Multipart
    @POST("/predict")
    fun predict(@Part file: MultipartBody.Part): Call<ResponseBody>
}