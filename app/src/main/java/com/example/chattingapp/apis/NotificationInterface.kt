package com.example.chattingapp.apis

import com.example.chattingapp.Accesstoken
import com.example.chattingapp.model.Notification
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface NotificationInterface {

    @POST("/v1/projects/chat-app-9c597/messages:send")
    @Headers(
        "Content-Type: application/json",
        "Accept: application/json"
    )
    fun notification(
        @Body message: Notification,
        @Header("Authorization") accessToken: String = "Bearer ${Accesstoken.getAccessToken()}"
    ): Call<Notification>
}