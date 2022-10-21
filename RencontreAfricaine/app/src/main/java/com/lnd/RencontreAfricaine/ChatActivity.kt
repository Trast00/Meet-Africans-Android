package com.lnd.RencontreAfricaine

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.lnd.RencontreAfricaine.ui.main.DiscussionFragment
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieAdapter
import com.xwray.groupie.GroupieViewHolder

class ChatActivity : AppCompatActivity() {
    companion object{
        var chatPartner: UserChat? = null
        var isNewPartner = true
    }

    val adapter = GroupAdapter<GroupieViewHolder>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        unit()

    }

    private lateinit var progressBar: ProgressBar
    private lateinit var imgProfile: ImageView
    private lateinit var txtName: TextView

    private lateinit var edMessage: AppCompatEditText
    private lateinit var btnSend: AppCompatButton

    private lateinit var recyclerView: RecyclerView
    private fun unit() {
        progressBar = findViewById(R.id.progress)
        imgProfile = findViewById(R.id.imgProfileChat)
        txtName = findViewById(R.id.txtNameChat)

        edMessage = findViewById(R.id.edMessageChat)
        btnSend = findViewById(R.id.btnSendChat)

        recyclerView = findViewById(R.id.recyclerChat)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        val toolbar = findViewById<Toolbar>(R.id.toolbarChat)
        setSupportActionBar(toolbar)

        Glide.with(this).clear(imgProfile)
        Glide.with(this).load(chatPartner?.imgProfile.toString())
            .diskCacheStrategy(DiskCacheStrategy.DATA)
            .into(imgProfile)

        txtName.text = chatPartner?.name

        btnSend.isEnabled = false
        btnSend.setOnClickListener {
            btnSend.isEnabled = false
            val message = edMessage.text.toString()
            if (edMessage.text==null || message.isEmpty()){return@setOnClickListener}

            val messageInfo = MessageInfo(chatPartner!!.idChat, MainActivity.currentUser!!.userData.id, chatPartner!!.toUserId, message, "text")

            val fbChat = FirebaseDatabase.getInstance().reference.child("Discussions")
                .child(chatPartner!!.idChat)

            if (isNewPartner){
                val newMap: MutableMap<String, Any?> = HashMap()
                newMap["idChat"] = chatPartner!!.idChat
                newMap["name"] = "${MainActivity.currentUser!!.userData.nom} ${MainActivity.currentUser!!.userData.prenom}"
                newMap["imgProfile"] = MainActivity.currentUser!!.userData.imgProfileUrl
                newMap["lastMessage"] = message
                newMap["nbrNewMessage"] = 1
                newMap["connected"] = false
                FirebaseDatabase.getInstance().reference.child("Users")
                    .child(chatPartner!!.toUserId)
                    .child("userChats").child(MainActivity.currentUser!!.userData.id)
                    .updateChildren(newMap)
                    .addOnSuccessListener {
                        fbChat.child(fbChat.push().key.toString()).setValue(messageInfo)
                            .addOnSuccessListener {
                                edMessage.setText("")
                                btnSend.isEnabled = true
                            }.addOnFailureListener {
                                Toast.makeText(this, "Erreur d'envoie", Toast.LENGTH_LONG).show()
                                btnSend.isEnabled = true
                            }
                    }
            }
            else{
                fbChat.child(fbChat.push().key.toString()).setValue(messageInfo)
                    .addOnSuccessListener {
                        edMessage.setText("")
                        btnSend.isEnabled = true
                    }.addOnFailureListener {
                        Toast.makeText(this, "Erreur d'envoie", Toast.LENGTH_LONG).show()
                        btnSend.isEnabled = true
                    }
            }
        }

        loadData()

    }

    private fun loadData() {
        progressBar.isVisible=true

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
                        progressBar.isVisible = false
                        btnSend.isEnabled = true

                        if (fromUserId==MainActivity.currentUser!!.userData.id){
                            isNewPartner = false
                        }
                    }
                }
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.chatmenu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.showDetail -> {
                //Load the userData
                for (userChat in DiscussionFragment.listChats){
                    if (chatPartner!!.toUserId == userChat.toUserId){
                        DetailProfileActivity.isUnlocked = true
                        Log.d("ProfileAdapter", "ID1: TRUE")
                        break
                    }
                }
                DetailProfileActivity.selectedProfileID= chatPartner!!.toUserId
                startActivity(Intent(this,DetailProfileActivity::class.java))
                true
            }
            R.id.reportUser -> {
                ReportBugActivity.title = "Report l utilisateur " +
                        "id= ${chatPartner?.toUserId} " +
                        "&& chatID = ${chatPartner?.idChat}"
                startActivity(Intent(this,ReportBugActivity::class.java))

                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun finish() {
        isNewPartner = true
        super.finish()
    }
}