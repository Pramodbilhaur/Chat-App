package com.example.chattingapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService: FirebaseMessagingService() {


    private val channelID = "class-update"
    private val channelName = "class-updates"


    private val notificationManager: NotificationManager by lazy {
        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onMessageReceived(message: RemoteMessage) {
        message.notification?.let {
            sendNotification(it.title, it.body)
            Log.d("message received", "onMessageReceived: Message hsa been received.")
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    private fun sendNotification(title: String?, messageBody: String?) {
        val intent = Intent(this, ChatActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        }

        // Use FLAG_IMMUTABLE for Android 12+ compliance
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val channelId = "chat_notifications"
        val channelName = "Chat Notifications"

        // Create a notification channel if on Android O or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Channel for chat notifications"
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Build the notification
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.notification) // Replace with your actual icon
            .setContentTitle(title)
            .setContentText(messageBody)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setOngoing(false)
            .setLights(
                ContextCompat.getColor(applicationContext, R.color.black),
                5000,
                5000
            )

        // Notify the user
        notificationManager.notify(0, notificationBuilder.build())
    }



}