package com.lnd.RencontreAfricaine

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.viewbinding.ViewBinding
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item

import com.xwray.groupie.viewbinding.BindableItem


data class Users(val userData: UserData, val userStatue: UserStatue?, val userInfo: UserInfo)

data class UserData(val id:String, val phone:String, var nom:String, var prenom:String, var age:Int, var sexe:String, val mdp:String, var imgProfileUrl:String,
                    val relation:String, val localisation:String, val language:String)
//phone=="admin" for administrator account

data class UserStatue(var nbrKey:Int, val connected:Boolean, val premiumDays:Int)

data class UserChat(val idChat:String, val name:String, val imgProfile: String, val toUserId:String, var lastMessage:String, var nbrNewMessage:Int, var connected: Boolean)

data class UserInfo(val contacts: Contacts, val searching: Searching, val info: Info?)

data class Contacts(var whatsapp:String, var messenger:String, var gmail:String)
data class Searching(val sexe: String, val relation:String, val age: String)
data class Info(var bio:String, var hobbies:MutableList<String>?)
//sexe: (Homme/Femme)


data class MessageInfo(val idChat: String, val fromUserId: String, val toUser: String, val message:String, val type:String)
    :Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.findViewById<TextView>(R.id.txtMessageItem)
            .text = message
    }

    override fun getLayout(): Int {
        return if (fromUserId==MainActivity.currentUser?.userData?.id.toString()){
            R.layout.itemmymessage
        }
        else if(fromUserId=="systeme"){
            R.layout.itemsystemmessage
        }

        else {
            R.layout.itempartnermessage
        }
    }
}
/*: BindableItem<com.xwray.groupie.viewbinding.GroupieViewHolder>() {
    override fun getLayout(): Int {
        return if (fromUserId==MainActivity.currentUser?.userData?.id.toString()){
            R.layout.itemmymessage
        }
        else{
            R.layout.itempartnermessage
        }
    }

    override fun initializeViewBinding(view: View): ViewBinding {
        return ViewBinding { view }
    }

    override fun bind(viewBinding: ViewBinding, position: Int) {
        viewBinding.root.findViewById<TextView>(R.id.txtMessageItem)
    }




}*/

//type : text, image
data class InfoServer(val adminPhone:String, val adminEmail: String, val isAvailable:Boolean, val minVersion:Int=0, val premuimPhone:String, val msgTitle:String, val msgDescription:String)