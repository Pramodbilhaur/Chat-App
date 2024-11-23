package com.example.chattingapp

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chattingapp.apis.NotificationAPI
import com.example.chattingapp.databinding.ActivityChatBinding
import com.example.chattingapp.model.Notification
import com.example.chattingapp.model.NotificationData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatBinding
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messageList: ArrayList<Message>
    private lateinit var mdbRef: DatabaseReference
    private var receiverRoom: String? = null
    private var senderRoom: String? = null
    private var receiverUid: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Extract intent data
        val name = intent.getStringExtra("name")
        receiverUid = intent.getStringExtra("uid")
        val senderUid = FirebaseAuth.getInstance().currentUser?.uid

        // Initialize Firebase reference and chat rooms
        mdbRef = FirebaseDatabase.getInstance().getReference()
        senderRoom = "$receiverUid$senderUid"
        receiverRoom = "$senderUid$receiverUid"

        // Set action bar title
        supportActionBar?.title = name

        // Initialize message list and adapter
        messageList = ArrayList()
        messageAdapter = MessageAdapter(this, messageList)

        // Setup RecyclerView
        binding.rvChat.layoutManager = LinearLayoutManager(this)
        binding.rvChat.adapter = messageAdapter

        // Check notification permission
        checkNotificationPermissions()

        // Subscribe to a test topic and get FCM token
        FirebaseMessaging.getInstance().subscribeToTopic("test")
        fetchAndStoreFcmToken(senderUid)

        // Handle keyboard adjustments for better UX
        adjustLayoutForKeyboard()

        // Load existing messages
        loadMessages()

        // Send message button click listener
        binding.btnSend.setOnClickListener {
            val message = binding.chatMessage.text.toString()
            if (message.isNotEmpty()) {
                checkMessageLimitAndSend(message, senderUid)
            } else {
                Toast.makeText(this, "Please enter a message.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkNotificationPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Notification permission granted.", Toast.LENGTH_SHORT).show()
            } else {
                requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else if (!NotificationManagerCompat.from(this).areNotificationsEnabled()) {
            Toast.makeText(this, "Please enable notifications in settings.", Toast.LENGTH_LONG).show()
        }
    }

    private val requestNotificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            val message = if (isGranted) {
                "Notification permission granted."
            } else {
                "Notification permission denied."
            }
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }

    private fun fetchAndStoreFcmToken(senderUid: String?) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.d("FCM Token", "Fetching FCM token failed")
                return@addOnCompleteListener
            }
            val token = task.result
            senderUid?.let {
                mdbRef.child("user").child(it).child("fcmToken").setValue(token)
            }
        }
    }

    private fun adjustLayoutForKeyboard() {
        val rootView = binding.root
        rootView.viewTreeObserver.addOnGlobalLayoutListener {
            val rect = Rect()
            rootView.getWindowVisibleDisplayFrame(rect)
            val heightDiff = rootView.height - (rect.bottom - rect.top)
            if (heightDiff > 200) {
                binding.rvChat.post {
                    binding.rvChat.scrollToPosition(messageList.size - 1)
                }
            }
        }
    }

    private fun checkMessageLimitAndSend(message: String, senderUid: String?) {
        val messageCountRef = mdbRef.child("user").child(senderUid!!).child("messageCount")
        val currentDate = getCurrentDate()

        messageCountRef.child(currentDate).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val messageCount = snapshot.getValue(Int::class.java) ?: 0
                if (messageCount < 10) {
                    sendMessage(message, senderUid)
                    messageCountRef.child(currentDate).setValue(messageCount + 1)
                } else {
                    Toast.makeText(this@ChatActivity, "You have reached today's message limit.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun sendMessage(message: String, senderUid: String) {
        val messageObject = Message(message, senderUid, System.currentTimeMillis())
        mdbRef.child("chats").child(senderRoom!!).child("messages").push()
            .setValue(messageObject).addOnSuccessListener {
                mdbRef.child("chats").child(receiverRoom!!).child("messages").push()
                    .setValue(messageObject)
                notifyReceiver(message)
                binding.chatMessage.text.clear()
            }
    }

    private fun notifyReceiver(message: String) {
        mdbRef.child("user").child(receiverUid!!).child("fcmToken").get()
            .addOnSuccessListener { snapshot ->
                val recipientToken = snapshot.getValue(String::class.java)
                if (!recipientToken.isNullOrBlank()) {
                    Log.d("FCM Token", "Retrieved token: $recipientToken")
                    sendNotification(recipientToken, "New Message", message)
                } else {
                    Log.e("FCM Token", "Token is null or blank for user: $receiverUid")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("FCM Token", "Error retrieving token", exception)
            }
    }

    private fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        return sdf.format(Date())
    }

    private fun loadMessages() {
        mdbRef.child("chats").child(senderRoom!!).child("messages")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    messageList.clear()
                    for (postSnapshot in snapshot.children) {
                        val message = postSnapshot.getValue(Message::class.java)
                        message?.let { messageList.add(it) }
                    }
                    messageAdapter.notifyDataSetChanged()
                    binding.rvChat.scrollToPosition(messageList.size - 1)
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@ChatActivity, "Failed to load messages.", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun sendNotification(to: String, title: String, body: String) {
        // Create the notification data
        val notificationData = NotificationData(title, body)

        // Create the message payload
        val message = com.example.chattingapp.model.Message(
            token = to,
            notification = notificationData,
            data = null // Optional: Add custom key-value pairs if needed
        )

        // Wrap the message in the Notification object
        val notification = Notification(message)
        NotificationAPI.createService().notification(notification).enqueue(object : Callback<Notification> {
            override fun onResponse(call: Call<Notification>, response: Response<Notification>) {
                if (response.isSuccessful) {
                    Log.d("Notification", "Notification sent successfully: ${response.body()}")
                    Toast.makeText(this@ChatActivity, "Notification Sent", Toast.LENGTH_SHORT).show()
                } else {
                    Log.e("Notification", "Failed to send notification: ${response.errorBody()?.string()}")
                    Toast.makeText(this@ChatActivity, "Failed to send notification.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Notification>, t: Throwable) {
                Log.e("Notification", "Notification API call failed", t)
                Toast.makeText(this@ChatActivity, "Failed to send notification.", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
