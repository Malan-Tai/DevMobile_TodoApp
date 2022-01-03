package com.example.erwancastioni.todo.network

import com.example.erwancastioni.todo.authentication.LoginForm
import com.example.erwancastioni.todo.authentication.LoginResponse
import com.example.erwancastioni.todo.authentication.SignupForm
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface UserWebService {
    @GET("users/info")
    suspend fun getInfo(): Response<UserInfo>

    @Multipart
    @PATCH("users/update_avatar")
    suspend fun updateAvatar(@Part avatar: MultipartBody.Part): Response<UserInfo>

    @PATCH("users")
    suspend fun update(@Body user: UserInfo): Response<UserInfo>

    @POST("users/login")
    suspend fun login(@Body user: LoginForm): Response<LoginResponse>

    @POST("users/sign_up")
    suspend fun signUp(@Body user: SignupForm): Response<LoginResponse>
}