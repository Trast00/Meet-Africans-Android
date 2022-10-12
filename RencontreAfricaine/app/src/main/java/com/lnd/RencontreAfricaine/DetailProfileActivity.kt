package com.lnd.RencontreAfricaine

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DetailProfileActivity : AppCompatActivity() {
    companion object{
        var selectedProfile : UserData? = null
        var selectedUser: Users? = null
        var isUnlocked = false
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_profile)

        if (selectedProfile==null){
            finish()
            return
        }

        unit()

        FirebaseDatabase.getInstance().reference.child("Users").child(selectedProfile!!.id)
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                        val gmail = snapshot.child("userInfo").child("contact").child("gmail").value.toString()
                        val whatsapp = snapshot.child("userInfo").child("contact").child("whatsapp").value.toString()
                        val messenger = snapshot.child("userInfo").child("contact").child("messenger").value.toString()

                        val sex = snapshot.child("userInfo").child("searching").child("sexe").value.toString()
                        val relation = snapshot.child("userInfo").child("searching").child("relation").value.toString()
                        val age = snapshot.child("userInfo").child("searching").child("age").value.toString()
                        selectedUser = Users(selectedProfile!!, null,
                            UserInfo(Contacts(whatsapp, messenger, gmail),
                                Searching(sex, relation, age), null), null)
                        loadData()
                    }
                }

                override fun onCancelled(error: DatabaseError) {}

            })
    }

    private fun loadData() {
        Glide.with(this).clear(imgProfile)
        Glide.with(this).load(selectedUser!!.userData.imgProfileUrl)
            .into(imgProfile)

        txtName.text = selectedUser!!.userData.nom
        txtSexAge.text = "${selectedUser!!.userData.sexe} - ${selectedUser!!.userData.age} ans"
        txtCountry.text = selectedUser!!.userData.localisation

        if (selectedUser!!.userInfo.contacts.messenger.isNotEmpty()){
            val txt = selectedUser!!.userInfo.contacts.messenger.subSequence(0, 5)
            txtMessenger.text = "$txt XXX XXX"
            txtMessenger.isVisible = true
            if (isUnlocked){
                txtMessenger.text = selectedUser!!.userInfo.contacts.messenger
            }
        }
        if (selectedUser!!.userInfo.contacts.gmail.isNotEmpty()){
            val txt = selectedUser!!.userInfo.contacts.gmail.subSequence(0, 5)
            txtGmail.text = "$txt XXX XXX"
            txtGmail.isVisible = true
            if (isUnlocked){
                txtGmail.text = selectedUser!!.userInfo.contacts.gmail
            }
        }
        if (selectedUser!!.userInfo.contacts.whatsapp.isNotEmpty()){
            val txt = selectedUser!!.userInfo.contacts.whatsapp.subSequence(0, 5)
            txtWhatsapp.text = "$txt XXX XXX"
            txtWhatsapp.isVisible = true
            if (isUnlocked){
                txtWhatsapp.text = selectedUser!!.userInfo.contacts.whatsapp
            }
        }

        progress.isVisible = false
        constDetail.isVisible = true
        constDetail.isEnabled = true
    }


    private lateinit var constDetail: ConstraintLayout
    private lateinit var progress: ProgressBar
    private lateinit var imgProfile: ImageView
    private lateinit var txtName: TextView
    private lateinit var txtSexAge: TextView
    private lateinit var txtCountry: TextView
    private lateinit var txtWhatsapp: TextView
    private lateinit var txtMessenger: TextView
    private lateinit var txtGmail: TextView
    private lateinit var btnContact : AppCompatButton
    private fun unit() {
        constDetail = findViewById(R.id.constDetail)
        progress = findViewById(R.id.progress)
        imgProfile = findViewById(R.id.imgProfileDetail)
        txtName = findViewById(R.id.txtNameDetail)
        txtSexAge = findViewById(R.id.txtSexAgeDetail)
        txtCountry = findViewById(R.id.txtCountry)
        txtWhatsapp = findViewById(R.id.txtwhatsappDetail)
        txtMessenger = findViewById(R.id.txtMessengerDetail)
        txtGmail = findViewById(R.id.txtGmailDetail)

        loadingDiag = AlertDialog.Builder(this)
            .setView(LayoutInflater.from(this).inflate(R.layout.loadingdiag, null))
            .setCancelable(false)
            .create()

        constDetail.isVisible = false
        constDetail.isEnabled = false
        progress.isVisible = true

        if (isUnlocked){
            btnContact.text = "Envoyer un message"
            findViewById<TextView>(R.id.txtBtnDescription).text = "Vous avez déjà contacter cette personne" +
                    "\nAppuyez sur 'Envoyer un message' pour continuer a discuter (gratuitement)"
        }
        btnContact.setOnClickListener {
            if (isUnlocked){
                ChatActivity.partner = selectedUser
                startActivity(Intent(this, ChatActivity::class.java))
            }else{
                if (MainActivity.newUserData==null){
                    AlertDialog.Builder(this)
                        .setTitle("Inscription")
                        .setMessage("Vous devez vous inscrire pour parler a cet utilisateur")
                        .setPositiveButton("Inscription"){_,_->
                            startActivity(Intent(this, RegisterActivity::class.java))
                        }
                        .setNegativeButton("Retour"){_,_-> }
                } else if(MainActivity.currentUser==null){
                    AlertDialog.Builder(this)
                        .setTitle("Completer profile")
                        .setMessage("Vous devez completer votre profile pour parler a cet utilisateur")
                        .setPositiveButton("Completer mon profile"){_,_->
                            startActivity(Intent(this, EditProfileActivity::class.java))
                        }
                        .setNegativeButton("Retour"){_,_-> }
                }
                else{
                    AlertDialog.Builder(this)
                        .setTitle("Debloquer cet utilisateur")
                        .setIcon(R.drawable.iconskey)
                        .setMessage("Vous avez besoin d'une clé pour parler a cet utilisateur")
                        .setPositiveButton("Debloquer cet utilisateur"){_,_->
                            //if user have enough key
                            if ((MainActivity.currentUser !=null
                                        && MainActivity.currentUser!!.userStatue!!.nbrKey>0)
                                || (MainActivity.newUserData!=null
                                        && MainActivity.newUserData!!["nbrKey"].toString().toInt()>0)){
                                unlockUser()
                            }else{
                                AlertDialog.Builder(this)
                                    .setTitle("Devenez premuim")
                                    .setIcon(R.drawable.iconskey)
                                    .setMessage("Vous n'avez pas assez de clé :(" +
                                            "\n\nVous pouvez acheter 500 clé en devenant premuim")
                                    .setPositiveButton("Devenir premuim"){_,_->
                                        startActivity(Intent(this, PremiumActivity::class.java))
                                    }
                            }
                        }
                }
            }
        }
    }

    private var loadingDiag : AlertDialog?= null
    private fun unlockUser() {
        loadingDiag?.show()
        if (MainActivity.currentUser!=null){
            val fb = FirebaseDatabase.getInstance().reference.child("Users")
                .child(MainActivity.currentUser!!.userData.id)
                .child("userChats")

            val idChat = fb.push().key

            var newMap: MutableMap<String,Any?> = HashMap()
            newMap["idChat"] = idChat
            newMap["name"] = "${selectedUser!!.userData.nom} ${selectedUser!!.userData.prenom}"
            newMap["imgProfile"] = selectedUser!!.userData.imgProfileUrl
            newMap["lastMessage"] = "Vous avez commencer une conversation"
            newMap["nbrNewMessage"] = 0
            newMap["connected"] = false

            fb.child(selectedUser!!.userData.id)
                .updateChildren(newMap)
                .addOnSuccessListener {
                    newMap= HashMap()
                    newMap["idChat"] = idChat
                    newMap["name"] = "${MainActivity.currentUser!!.userData.nom} ${MainActivity.currentUser!!.userData.prenom}"
                    newMap["imgProfile"] = MainActivity.currentUser!!.userData.imgProfileUrl
                    newMap["lastMessage"] = "Vous avez commencer une conversation"
                    newMap["nbrNewMessage"] = 0
                    newMap["connected"] = false
                    FirebaseDatabase.getInstance().reference.child("Users")
                        .child(selectedUser!!.userData.id)
                        .child("userChats").child(MainActivity.currentUser!!.userData.id)
                        .updateChildren(newMap)
                        .addOnSuccessListener {
                            val fbChat = FirebaseDatabase.getInstance().reference.child("Discussions")
                                .child(idChat.toString())
                            val firstMessageID = fbChat.push().key
                            val firstMessage: MutableMap<String, Any?> = HashMap()
                            firstMessage["idChat"] = idChat
                            firstMessage["fromUserId"] = "systeme"
                            firstMessage["toUser"] = ""
                            firstMessage["message"] = "SYSTEME: Debut de la conversation"
                            firstMessage["type"] = "text"

                            fbChat.child(firstMessageID.toString()).updateChildren(firstMessage)
                                .addOnSuccessListener {
                                    val newNbrKey = MainActivity.currentUser!!.userStatue!!.nbrKey -1
                                    FirebaseDatabase.getInstance().reference.child("Users")
                                        .child(MainActivity.currentUser!!.userData.id)
                                        .child("userStatue").child("nbrKey").setValue(newNbrKey)
                                        .addOnSuccessListener {
                                            loadingDiag?.dismiss()
                                            MainActivity.currentUser!!.userStatue!!.nbrKey = newNbrKey
                                            startActivity(Intent(this, DetailProfileActivity::class.java))
                                            isUnlocked = true
                                        }
                                }
                        }
                }

        }else{
            val newNbrKey = MainActivity.newUserData!!["nbrKey"].toString().toInt() -1
        }
    }
}