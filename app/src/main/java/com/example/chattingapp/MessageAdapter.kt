package com.example.chattingapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.firebase.auth.FirebaseAuth

class MessageAdapter(val context: Context, val messageList: ArrayList<Message>):RecyclerView.Adapter<ViewHolder>() {

    val ITEM_RECEIVE = 1
    val ITEM_SENT = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        if(viewType == 1){
            val view= LayoutInflater.from(parent.context).inflate(R.layout.received_message, parent, false)
            return ReceiveViewHolder(view)
        } else {
            val view= LayoutInflater.from(parent.context).inflate(R.layout.sent_message, parent, false)
            return SentViewHolder(view)
        }
    }

    override fun getItemCount(): Int = messageList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentMessage = messageList[position]

        if(holder.javaClass == SentViewHolder::class.java){
            val viewHolder = holder as SentViewHolder
            holder.sentMessage.text = currentMessage.message
        } else {
            val viewHolder = holder as ReceiveViewHolder
            holder.receiveMessage.text = currentMessage.message
        }
    }

    override fun getItemViewType(position: Int): Int {
        val currentUid = FirebaseAuth.getInstance().currentUser?.uid
        if(currentUid.equals(messageList[position].senderId)){
            return ITEM_SENT
        } else
        {
            return ITEM_RECEIVE
        }
    }


    class SentViewHolder(view: View): ViewHolder(view){
        val sentMessage = itemView.findViewById<TextView>(R.id.sentMessage)
    }

    class ReceiveViewHolder(view: View): ViewHolder(view){
        val receiveMessage = itemView.findViewById<TextView>(R.id.receivedMessage)
    }




}