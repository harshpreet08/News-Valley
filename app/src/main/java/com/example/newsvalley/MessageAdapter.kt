package com.example.newsvalley

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth

class MessageAdapter(val context: Context, val messageList: ArrayList<Message>):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {    //implemented both view holders ie sent and receive, this is hard

    val ITEM_RECEIVE = 1
    val ITEM_SENT = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder { //i will inflate here accordingly

        if(viewType == 1){
            //inflating receive
            val view: View = LayoutInflater.from(context).inflate(R.layout.recieve, parent, false)
            return ReceiveViewHolder(view)
        } else{
            //inflating sent
            val view: View = LayoutInflater.from(context).inflate(R.layout.sent, parent, false)
            return SentViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val currentMessage = messageList[position]

        if(holder.javaClass == SentViewHolder::class.java){
            //i will do code for send view holder
            val viewHolder = holder as SentViewHolder
            holder.sentMessage.text = currentMessage.message



        }else{
            //now i will do code for receive view holder
            val viewHolder = holder as ReceiveViewHolder
            holder.receiveMessage.text = currentMessage.message

        }

    }

    override fun getItemViewType(position: Int): Int {      // imp: logic for actually choosing which viewholder(sent/receive) to inflate/make

        val currentMessage = messageList[position]

        if(FirebaseAuth.getInstance().currentUser?.uid.equals(currentMessage.senderId)){    //if current user is equals to sender uid, i know i will inflate sent viewholder
            return ITEM_SENT
        } else{
            return ITEM_RECEIVE //else i will inflate receive view holder
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    class SentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val sentMessage = itemView.findViewById<TextView>(R.id.txt_sent_message)
    }

    class ReceiveViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val receiveMessage = itemView.findViewById<TextView>(R.id.txt_receive_message)
    }
}