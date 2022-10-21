package com.lnd.RencontreAfricaine.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.lnd.RencontreAfricaine.*
import com.lnd.RencontreAfricaine.utils.DiscussionAdapter

class DiscussionFragment : Fragment() {
    companion object{
        var listChats:MutableList<UserChat> = mutableListOf()
    }

    lateinit var recyclerView: RecyclerView
    lateinit var progressBar: ProgressBar

    lateinit var constInfoDiscussion: ConstraintLayout
    lateinit var txtInfo: TextView
    lateinit var btnRegister: AppCompatButton
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_discussion, container, false)
        progressBar = root.findViewById(R.id.progress)
        constInfoDiscussion = root.findViewById(R.id.constInfoDiscussion)
        txtInfo = root.findViewById(R.id.txtInfoDiscussion)
        btnRegister = root.findViewById(R.id.btnInscriptionDiscussion)
        recyclerView = root.findViewById(R.id.recyclerDiscussion)

        //User is not registered
        if (MainActivity.newUserData == null && MainActivity.currentUser==null){
            constInfoDiscussion.isEnabled=true
            constInfoDiscussion.isVisible=true
            btnRegister.setOnClickListener {
                startActivity(Intent(context, RegisterActivity::class.java))
            }
        }
        else if (MainActivity.newUserData != null && MainActivity.currentUser==null){
            constInfoDiscussion.isEnabled=true
            constInfoDiscussion.isVisible=true
            txtInfo.text = "Vous devez completer votre profile pour commencer des discussion"
            btnRegister.text = "Completer Profile"
            btnRegister.setOnClickListener {
                EditProfileActivity.isNewUser = true
                startActivity(Intent(context, EditProfileActivity::class.java))
            }
            loadData()
        }
        else{
            constInfoDiscussion.isEnabled=false
            constInfoDiscussion.isVisible=false


            recyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, true)
            recyclerView.adapter = DiscussionAdapter(requireContext(), listChats)

            loadData()
            //recyclerView

        }

        return root
    }

    private fun loadData() {

        val fb = if (MainActivity.newUserData!=null){
            FirebaseDatabase.getInstance().reference.child("NewUsers")
                .child(MainActivity.newUserData!!["id"].toString())
        } else{
            FirebaseDatabase.getInstance().reference.child("Users")
                .child(MainActivity.currentUser!!.userData.id)
        }


        fb.child("userChats")
            .addChildEventListener(object :ChildEventListener{
                override fun onChildRemoved(snapshot: DataSnapshot) {}
                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onCancelled(error: DatabaseError) {}

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    if (snapshot.exists()){
                        val toUserId = snapshot.child("toUserId").value.toString()
                        for ((i, userChat) in listChats.withIndex()){
                            if (userChat.toUserId==toUserId){
                                listChats[i].lastMessage = snapshot.child("lastMessage").value.toString()
                                listChats[i].nbrNewMessage = snapshot.child("nbrNewMessage").value.toString().toInt()
                                listChats[i].connected = snapshot.child("connected").value.toString().toBoolean()
                                recyclerView.adapter?.notifyItemChanged(i)
                            }
                        }
                    }
                }

                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    if (snapshot.exists()){
                        val idChat = snapshot.child("idChat").value.toString()

                        var isNewChat = true
                        for (item in listChats){
                            if (item.idChat == idChat){
                                isNewChat = false
                            }
                        }
                        if (!isNewChat) return

                        val name = snapshot.child("name").value.toString()
                        val imgProfile = snapshot.child("imgProfile").value.toString()
                        val toUserId = snapshot.child("toUserId").value.toString()
                        val lastMessage = snapshot.child("lastMessage").value.toString()
                        val nbrNewMessage = snapshot.child("nbrNewMessage").value.toString().toInt()
                        val connected = snapshot.child("connected").value.toString().toBoolean()

                        listChats.add(UserChat(idChat, name, imgProfile, toUserId, lastMessage, nbrNewMessage, connected))
                        recyclerView.adapter?.notifyItemInserted(listChats.size)
                    }
                }
            })
    }

}