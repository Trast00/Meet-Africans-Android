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
import com.lnd.RencontreAfricaine.*
import com.lnd.RencontreAfricaine.utils.DiscussionAdapter

class DiscussionFragment : Fragment() {
    companion object{
        val listChats:MutableList<UserChats> = mutableListOf()
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
        }
        else{
            constInfoDiscussion.isEnabled=false
            constInfoDiscussion.isVisible=false
            //recyclerView
            recyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            recyclerView.adapter = DiscussionAdapter(requireContext(), listChats)
        }

        return root
    }

}