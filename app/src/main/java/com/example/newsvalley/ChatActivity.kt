package com.example.newsvalley

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ChatActivity : AppCompatActivity() {

    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var messageBox: EditText
    private lateinit var sendButton: ImageView
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messageList: ArrayList<Message>
    private lateinit var mDbRef: DatabaseReference

    var receiverRoom: String? = null
    var senderRoom: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val name = intent.getStringExtra("name")
        val receiverUid = intent.getStringExtra("uid")

        val senderUid = FirebaseAuth.getInstance().currentUser?.uid
        mDbRef = FirebaseDatabase.getInstance().getReference()

        senderRoom = receiverUid + senderUid        //creates unique room for sender and receiver
        receiverRoom = senderUid + receiverUid      //creates unique room for sender and receiver

        supportActionBar?.title = name

        chatRecyclerView = findViewById(R.id.chatRecyclerView)
        messageBox = findViewById(R.id.messageBox)
        sendButton = findViewById(R.id.sendButton)
        messageList = ArrayList()
        messageAdapter = MessageAdapter(this,messageList)

        chatRecyclerView.layoutManager = LinearLayoutManager(this)  //setting layout manager
        chatRecyclerView.adapter = messageAdapter   //setting chat adapter

        //logic for adding message from database to recycler view
        mDbRef.child("chats").child(senderRoom!!).child("messages")
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    messageList.clear()     //for clearing old messages so they don't repeat again and again

                    for(postSnapshot in snapshot.children){     //to get all the messages

                        val message = postSnapshot.getValue(Message::class.java)
                        messageList.add(message!!)

                    }
                    messageAdapter.notifyDataSetChanged()

                }

                override fun onCancelled(error: DatabaseError) {

                }

            })

        //adding the message to database
        sendButton.setOnClickListener{

            val message = messageBox.text.toString()
            val messageObject = Message(message,senderUid)

            //updating sender room
            mDbRef.child("chats").child(senderRoom!!).child("messages").push() //push will create a unique node each time
                .setValue(messageObject).addOnSuccessListener {

                    //updating receiver room
                    mDbRef.child("chats").child(receiverRoom!!).child("messages").push() //push will create a unique node each time
                        .setValue(messageObject)
                }
            messageBox.setText("")  //after sending message, clearing the message box
        }
    }
}