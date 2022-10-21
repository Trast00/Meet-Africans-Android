package com.lnd.RencontreAfricaine.utils

import android.content.Context
import android.content.Intent
import android.provider.ContactsContract.CommonDataKinds.Im
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.lnd.RencontreAfricaine.DetailProfileActivity
import com.lnd.RencontreAfricaine.MainActivity
import com.lnd.RencontreAfricaine.R

import com.lnd.RencontreAfricaine.UserData
import com.lnd.RencontreAfricaine.ui.main.DiscoverFragment
import com.lnd.RencontreAfricaine.ui.main.DiscussionFragment

class ProfileAdapter(val context: Context?, val listRec: MutableList<UserData>) :RecyclerView.Adapter<ProfileAdapter.ViewHolder>(){
    class ViewHolder(private val itemView:View): RecyclerView.ViewHolder(itemView) {
        val card = itemView.findViewById<CardView>(R.id.cardItemProfile)
        val img = itemView.findViewById<ImageView>(R.id.imgProfileItemProfile)
        val name = itemView.findViewById<TextView>(R.id.txtNameItemProfile)
        val imgRelation = itemView.findViewById<ImageView>(R.id.imgRelationItemProfile)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.itemprofile, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val userData = listRec[position]

        holder.name.text = userData.nom

        when(userData.relation){
            "Marriage"->holder.imgRelation.setImageResource(R.drawable.iconsring)
            "Relation Serieuse"->holder.imgRelation.setImageResource(R.drawable.iconsflower)
            "Amour"->holder.imgRelation.setImageResource(R.drawable.iconsheart)
            "Amitie"->holder.imgRelation.setImageResource(R.drawable.iconsfriend)
            "Relation Sexuel"->holder.imgRelation.setImageResource(R.drawable.iconssex)
            "Inconnue"->holder.imgRelation.setImageResource(R.drawable.iconsunknown)
        }

        Glide.with(context!!).clear(holder.img)
        Glide.with(context)
            .load(userData.imgProfileUrl)
            .diskCacheStrategy(DiskCacheStrategy.DATA)
            .into(holder.img)


        //Check if last profile
        holder.card.setOnClickListener {
            for (userChat in DiscussionFragment.listChats){
                if (listRec[position].id == userChat.toUserId){
                    DetailProfileActivity.isUnlocked = true
                    break
                }
            }
            DetailProfileActivity.selectedProfile = listRec[position]
            context.startActivity(Intent(context, DetailProfileActivity::class.java))
        }

    }

    override fun getItemCount(): Int = listRec.size
}