package com.lnd.RencontreAfricaine

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupieAdapter

class ChatActivity : AppCompatActivity() {
    companion object{
        var chatPartner: UserChats? = null
    }

    val adapter = GroupieAdapter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        unit()

    }

    private lateinit var progressBar: ProgressBar
    private lateinit var imgProfile: ImageView
    private lateinit var txtName: TextView
    private lateinit var constProfile: ImageView

    private lateinit var edMessage: AppCompatEditText
    private lateinit var btnSend: AppCompatButton

    private lateinit var recyclerView: RecyclerView
    private fun unit() {
        progressBar = findViewById(R.id.progress)
        imgProfile = findViewById(R.id.imgProfile)
        txtName = findViewById(R.id.txtName)
        constProfile = findViewById(R.id.constProfile)

        edMessage = findViewById(R.id.edMessageChat)
        btnSend = findViewById(R.id.btnSendChat)

        recyclerView = findViewById(R.id.recyclerChat)

        Glide.with(this).clear(imgProfile)
        Glide.with(this).load(chatPartner!!.imgProfile)
            .into(imgProfile)

        txtName.text = chatPartner!!.name

        btnSend.isEnabled = false
        btnSend.setOnClickListener {
            btnSend.isEnabled = false
            val message = edMessage.text.toString()
            if (edMessage.text!=null || message.isEmpty()){return@setOnClickListener}

            val messageInfo = MessageInfo(chatPartner!!.idChat, MainActivity.currentUser!!.userData.id, chatPartner!!.toUserId, message, "text")

            val fbChat = FirebaseDatabase.getInstance().reference.child("Discussions")
                .child(chatPartner!!.idChat)

            fbChat.child(fbChat.push().key.toString()).setValue(messageInfo)
                .addOnSuccessListener {
                    edMessage.setText("")
                    btnSend.isEnabled = true
                }.addOnFailureListener {
                    Toast.makeText(this, "Erreur d'envoie", Toast.LENGTH_LONG).show()
                    btnSend.isEnabled = true
                }

        }

        loadData()

    }

    private fun loadData() {
        FirebaseDatabase.getInstance().reference.child("Discussions")
            .child(chatPartner!!.idChat)
            .addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                        for (h0 in snapshot.children){
                            val idChat = h0.child("idChat").value.toString()
                            val fromUserId = h0.child("fromUserId").value.toString()
                            val toUser = h0.child("toUser").value.toString()
                            val message = h0.child("message").value.toString()
                            val type = h0.child("type").value.toString()

                            adapter.add(MessageInfo(idChat, fromUserId, toUser, message, type))
                        }
                        recyclerView.adapter = adapter
                        btnSend.isEnabled = true
                        progressBar.isVisible = false
                        listenNewMessage()
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })

    }

    private fun listenNewMessage() {
        FirebaseDatabase.getInstance().reference.child("Discussions")
            .child(chatPartner!!.idChat)
            .addChildEventListener(object : ChildEventListener{
                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onChildRemoved(snapshot: DataSnapshot) {}
                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onCancelled(error: DatabaseError) {}

                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    if (snapshot.exists()){
                        val idChat = snapshot.child("idChat").value.toString()
                        val fromUserId = snapshot.child("fromUserId").value.toString()
                        val toUser = snapshot.child("toUser").value.toString()
                        val message = snapshot.child("message").value.toString()
                        val type = snapshot.child("type").value.toString()

                        adapter.add(MessageInfo(idChat, fromUserId, toUser, message, type))
                        recyclerView.adapter?.notifyItemInserted(adapter.itemCount)
                    }
                }
            })
    }
}