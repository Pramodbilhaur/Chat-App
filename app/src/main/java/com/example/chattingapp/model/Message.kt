package com.example.chattingapp.model

data class Message(
    val token: String, // FCM token of the recipient
    val notification: NotificationData? = null,
    val data: Map<String, String>? = null // Optional custom key-value pairs
)
