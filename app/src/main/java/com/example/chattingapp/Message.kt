package com.example.chattingapp

data class Message(
    var message: String? = null,
    var senderId: String? = null,
    var timestamp: Long? = null
)