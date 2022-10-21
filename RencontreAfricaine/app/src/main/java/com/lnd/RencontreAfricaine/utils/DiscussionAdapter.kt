package com.lnd.RencontreAfricaine.utils

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.cache.DiskCache
import com.lnd.RencontreAfricaine.ChatActivity
import com.lnd.RencontreAfricaine.R
import com.lnd.RencontreAfricaine.UserChat

class DiscussionAdapter(val context: Context, private val listRec : MutableList<UserChat>): RecyclerView.Adapter<DiscussionAdapter.ViewHolder>() {
    class ViewHolder(private val itemView: View) : RecyclerView.ViewHolder(itemView) {
        val const = itemView.findViewById<ConstraintLayout>(R.id.constItemDiscussion)
        val imgProfile = itemView.findViewById<ImageView>(R.id.imgProfileItemDiscussion)
        val txtName = itemView.findViewById<TextView>(R.id.txtNameItemProfile)
        val txtLastMessage = itemView.findViewById<TextView>(R.id.txtLastMessageItemDiscussion)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.discussionitem, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int = listRec.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val userChat = listRec[position]
        Glide.with(context).clear(holder.imgProfile)
        Glide.with(context).load(userChat.imgProfile)
            .diskCacheStrategy(DiskCacheStrategy.DATA)
            .into(holder.imgProfile)


        holder.txtName.text = userChat.name
        holder.txtLastMessage.text = userChat.lastMessage

        holder.const.setOnClickListener {
            ChatActivity.chatPartner = listRec[position]
            context.startActivity(Intent(context, ChatActivity::class.java))
        }
    }


}