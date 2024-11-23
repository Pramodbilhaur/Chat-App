package com.example.chattingapp.apis

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NotificationAPI {

    private const val BASE_URL = "https://fcm.googleapis.com"
    private var retrofit: Retrofit? = null

    fun createService(): NotificationInterface {
        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofit!!.create(NotificationInterface::class.java)
    }
}