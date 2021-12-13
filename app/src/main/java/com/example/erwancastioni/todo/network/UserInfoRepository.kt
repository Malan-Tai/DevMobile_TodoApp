package com.example.erwancastioni.todo.network

import android.content.ContentResolver
import android.net.Uri
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class UserInfoRepository {
    private val userWebService = Api.userWebService

    suspend fun loadUserInfo(): UserInfo? {
        val response = userWebService.getInfo()
        return if (response.isSuccessful) response.body() else null
    }

    suspend fun updateAvatar(contentResolver: ContentResolver, imageUri: Uri) : UserInfo? {
        val response = userWebService.updateAvatar(convert(contentResolver, imageUri))
        return if (response.isSuccessful) response.body() else null
    }

    private fun convert(contentResolver: ContentResolver, uri: Uri): MultipartBody.Part {
        return MultipartBody.Part.createFormData(
            name = "avatar",
            filename = "temp.jpeg",
            body = contentResolver.openInputStream(uri)!!.readBytes().toRequestBody()
        )
    }
}