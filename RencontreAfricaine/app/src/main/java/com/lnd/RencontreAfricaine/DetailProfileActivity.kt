package com.lnd.RencontreAfricaine

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Im
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.lnd.RencontreAfricaine.ui.main.DiscussionFragment

class DetailProfileActivity : AppCompatActivity() {
    companion object{
        var selectedProfile : UserData? = null
        var selectedProfileID = ""
        var selectedUser: Users? = null
        var isUnlocked = false
        var listener: DetailActivityListener? = null
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_profile)

        if (selectedProfile==null && selectedProfileID.isEmpty()){
            finish()
            return
        }

        unit()
        val id = if (selectedProfile!=null){
            selectedProfile!!.id
        } else{
            selectedProfileID
        }

        FirebaseDatabase.getInstance().reference.child("Users").child(id)
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists() && snapshot.child("userData").value!=null){
                        val phone = snapshot.child("userData").child("phone").value.toString()
                        val nom = snapshot.child("userData").child("nom").value.toString()
                        val prenom = snapshot.child("userData").child("prenom").value.toString()
                        val age = snapshot.child("userData").child("age").value.toString().toInt()
                        val sexe = snapshot.child("userData").child("sexe").value.toString()
                        val mdp = snapshot.child("userData").child("mdp").value.toString()
                        val imgProfileUrl = snapshot.child("userData").child("imgProfileUrl").value.toString()
                        val relation = snapshot.child("userData").child("relation").value.toString()
                        val localisation = snapshot.child("userData").child("localisation").value.toString()
                        val language = snapshot.child("userData").child("language").value.toString()
                        val userData = UserData(id, phone, nom, prenom, age, sexe, mdp, imgProfileUrl, relation, localisation, language)

                        val gmail = snapshot.child("userInfo").child("contact").child("gmail").value.toString()
                        val whatsapp = snapshot.child("userInfo").child("contact").child("whatsapp").value.toString()
                        val messenger = snapshot.child("userInfo").child("contact").child("messenger").value.toString()

                        val sex = snapshot.child("userInfo").child("searching").child("sexe").value.toString()
                        val relationWanted = snapshot.child("userInfo").child("searching").child("relation").value.toString()
                        val ageWanted = snapshot.child("userInfo").child("searching").child("age").value.toString()

                        val bio = snapshot.child("userInfo").child("info").child("bio").value.toString()

                        selectedProfile = userData
                        selectedUser = Users(userData, null,
                            UserInfo(Contacts(whatsapp, messenger, gmail),
                                Searching(sex, relationWanted, ageWanted), Info(bio, null)
                            ))
                        loadData()
                    }
                    else if(!snapshot.child("userData").exists()){
                        AlertDialog.Builder(this@DetailProfileActivity)
                            .setTitle("Cet Compte a été supprimer")
                            .setMessage("Cet utilisateur a supprimer son compte")
                            .setPositiveButton("OK"){_,_-> finish()}
                            .setCancelable(false)
                            .create().show()
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

        if (selectedUser!!.userInfo.contacts.messenger.isNotEmpty()
            && selectedUser!!.userInfo.contacts.messenger!="null"){
            val txt = selectedUser!!.userInfo.contacts.messenger.subSequence(0, 3)
            txtMessenger.text = "${txt}*** *"
            txtMessenger.isVisible = true
            if (isUnlocked){
                txtMessenger.text = selectedUser!!.userInfo.contacts.messenger
            }
        }else{
            findViewById<ImageView>(R.id.imgno3).isVisible = false
            txtMessenger.isVisible = false
        }
        if (selectedUser!!.userInfo.contacts.gmail.isNotEmpty()
            && selectedUser!!.userInfo.contacts.gmail!="null"){
            val txt = selectedUser!!.userInfo.contacts.gmail.subSequence(0, 3)
            txtGmail.text = "${txt}*** *"
            txtGmail.isVisible = true
            if (isUnlocked){
                txtGmail.text = selectedUser!!.userInfo.contacts.gmail
            }
        }else{
            findViewById<ImageView>(R.id.imgno1).isVisible = false
            txtGmail.isVisible = false
        }
        if (selectedUser!!.userInfo.contacts.whatsapp.isNotEmpty()
            && selectedUser!!.userInfo.contacts.whatsapp!="null"){
            val txt = selectedUser!!.userInfo.contacts.whatsapp.subSequence(0, 3)
            txtWhatsapp.text = "${txt}*** *"
            txtWhatsapp.isVisible = true
            if (isUnlocked){
                txtWhatsapp.text = selectedUser!!.userInfo.contacts.whatsapp
            }

        }else{
            findViewById<ImageView>(R.id.imgno2).isVisible = false
            txtWhatsapp.isVisible = false
        }

        if ( selectedUser!!.userInfo!=null && selectedUser!!.userInfo.info !=null
                && selectedUser!!.userInfo.info!!.bio.isNotEmpty()
            && selectedUser!!.userInfo.info!!.bio!="null"){
            edBio.isEnabled = true
            edBio.isVisible = true
            edBio.setText(selectedUser!!.userInfo.info!!.bio)
            edBio.isFocusable = false
        }else{
            edBio.isFocusable = false
            findViewById<ConstraintLayout>(R.id.constBio).isVisible = false
        }

        when(selectedUser!!.userInfo.searching.relation){
            "Marriage"->imgRelationWanted.setImageResource(R.drawable.iconsring)
            "Relation Serieuse"->imgRelationWanted.setImageResource(R.drawable.iconsflower)
            "Amour"->imgRelationWanted.setImageResource(R.drawable.iconsheart)
            "Amitie"->imgRelationWanted.setImageResource(R.drawable.iconsfriend)
            "Relation Sexuel"->imgRelationWanted.setImageResource(R.drawable.iconssex)
        }

        if (selectedUser!!.userInfo.searching.relation.isNotEmpty()
            && selectedUser!!.userInfo.searching.relation!="null"){
            txtRelationWanted.text = selectedUser!!.userInfo.searching.relation
        }

        val sexWanted = selectedUser!!.userInfo.searching.sexe
        if (sexWanted.contains("Homme") && !sexWanted.contains("Femme")){
            imgSexWanted.setImageResource(R.drawable.iconsmen)
            txtSexWanted.text = "Homme"
        }
        else if (!sexWanted.contains("Homme") && sexWanted.contains("Femme")){
            imgSexWanted.setImageResource(R.drawable.iconswomen)
            txtSexWanted.text = "Femme"
        }
        else if(sexWanted=="null" || sexWanted.isEmpty()){
            imgSexWanted.isVisible = false
            txtSexWanted.isVisible = false
        }

        if (selectedUser!!.userInfo.searching.age!="null"
            && selectedUser!!.userInfo.searching.age.isNotEmpty()){
            txtAgeWanted.text = selectedUser!!.userInfo.searching.age
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

    private lateinit var imgSexWanted: ImageView
    private lateinit var txtSexWanted: TextView
    private lateinit var txtAgeWanted: TextView
    private lateinit var imgRelationWanted: ImageView
    private lateinit var txtRelationWanted: TextView
    private lateinit var edBio : TextInputEditText
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
        btnContact = findViewById(R.id.btnContactDetail)

        imgRelationWanted = findViewById(R.id.imgRelationWanted)
        txtRelationWanted = findViewById(R.id.txtRelationWanted)
        imgSexWanted = findViewById(R.id.imgSexWanted)
        txtSexWanted = findViewById(R.id.txtSexWanted)
        txtAgeWanted = findViewById(R.id.txtAgeWanted)

        edBio = findViewById(R.id.edBioDetail)

        loadingDiag?.dismiss()
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
            if (MainActivity.currentUser!=null &&
                selectedProfile!!.id == MainActivity.currentUser!!.userData.id){
                Toast.makeText(this, "Merci de regarder dans un mirroire pour contacter cet utilisateur !" +
                        "\n\nVous ne pouvez pas vous concater vous même", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            if (isUnlocked){
                if (MainActivity.currentUser==null){
                    AlertDialog.Builder(this)
                        .setTitle("Completer votre profile")
                        .setMessage("Pour demarrer une conversation, vous devez completer votre profile")
                        .setPositiveButton("Completer mon profile"){_,_->
                            EditProfileActivity.isNewUser = true
                            startActivity(Intent(this, EditProfileActivity::class.java))
                            finish()
                        }
                        .create().show()
                    return@setOnClickListener
                }
                for (item in DiscussionFragment.listChats){
                    if (item.toUserId== selectedUser!!.userData.id){
                        ChatActivity.chatPartner = item
                    }
                }
                startActivity(Intent(this, ChatActivity::class.java))
            }else{
                if (MainActivity.newUserData==null && MainActivity.currentUser==null){
                    AlertDialog.Builder(this)
                        .setTitle("Inscription")
                        .setMessage("Vous devez vous inscrire pour parler a cet utilisateur" +
                                "\n\nInscription en 1 minute: Seul le numero de telephone et un mot passe sont requise")
                        .setPositiveButton("Inscription"){_,_->
                            startActivity(Intent(this, RegisterActivity::class.java))
                            finish()
                        }
                        .setNegativeButton("Retour"){_,_-> }
                        .create().show()
                } else if(MainActivity.newUserData!=null && MainActivity.currentUser==null){
                    AlertDialog.Builder(this)
                        .setTitle("Contacter sans Completer votre profile")
                        .setMessage("Voulez vous vraiment contacter cet utilisateur sans completer votre profile ?" +
                                "\nVous pourrez voir ses contacte, mais pas parler a cet utilisateur ! " +
                                "\nVous devez completer votre profile pour parler a cet utilisateur")
                        .setPositiveButton("Completer mon profile"){_,_->
                            EditProfileActivity.isNewUser = true
                            startActivity(Intent(this, EditProfileActivity::class.java))
                            finish()
                        }
                        .setNegativeButton("Debloquer cet utilisateur"){_,_->
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
                        .create().show()
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
                        .create().show()
                }
            }
        }
    }

    private var loadingDiag : AlertDialog?= null
    private fun unlockUser() {
        loadingDiag?.show()

        val fb = if (MainActivity.newUserData!=null){
             FirebaseDatabase.getInstance().reference.child("NewUsers")
                .child(MainActivity.newUserData!!["id"].toString())
                .child("userChats")
        } else {
            FirebaseDatabase.getInstance().reference.child("Users")
                .child(MainActivity.currentUser!!.userData.id)
                .child("userChats")
        }

            val idChat = FirebaseDatabase.getInstance().reference.child("Discussions").push().key

            val newMap: MutableMap<String,Any?> = HashMap()
            newMap["idChat"] = idChat
            newMap["name"] = "${selectedUser!!.userData.nom} ${selectedUser!!.userData.prenom}"
            newMap["imgProfile"] = selectedUser!!.userData.imgProfileUrl
            newMap["toUserId"] = selectedUser!!.userData.id
            newMap["lastMessage"] = "Vous avez commencer une conversation"
            newMap["nbrNewMessage"] = 1
            newMap["connected"] = false


            /*DiscussionFragment.listChats
                .add(UserChat(newMap["idChat"].toString(),
                    newMap["name"].toString(),
                    newMap["imgProfile"].toString(),
                    newMap["toUserId"].toString(),
                    newMap["lastMessage"].toString(),
                    newMap["nbrNewMessage"].toString().toInt(),
                    newMap["connected"].toString().toBoolean()))*/

            fb.child(selectedUser!!.userData.id)
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
                            if (MainActivity.currentUser!=null){
                                val id = MainActivity.currentUser!!.userData.id
                                val newNbrKey = MainActivity.currentUser!!.userStatue!!.nbrKey -1

                                FirebaseDatabase.getInstance().reference.child("Users")
                                    .child(id).child("userStatue")
                                    .child("nbrKey").setValue(newNbrKey)
                                    .addOnSuccessListener {
                                        MainActivity.currentUser!!.userStatue!!.nbrKey = newNbrKey

                                        listener?.onNbrKeyChanged(newNbrKey)

                                        isUnlocked = true
                                        reUnit()
                                    }
                            }
                            else{
                                val id = MainActivity.newUserData!!["id"].toString()
                                val newNbrKey = MainActivity.newUserData!!["nbrKey"].toString().toInt() -1

                                FirebaseDatabase.getInstance().reference.child("NewUsers")
                                    .child(id).child("nbrKey")
                                    .setValue(newNbrKey)
                                    .addOnSuccessListener {

                                        MainActivity.newUserData!!["NewUsers"] = newNbrKey
                                        listener?.onNbrKeyChanged(newNbrKey)

                                        isUnlocked = true
                                        reUnit()
                                    }
                            }
                        }
                }
    }

    private fun reUnit() {
        if (selectedProfile==null && selectedProfileID.isEmpty()){
            finish()
            return
        }

        unit()
        val id = if (selectedProfile!=null){
            selectedProfile!!.id
        } else{
            selectedProfileID
        }

        FirebaseDatabase.getInstance().reference.child("Users").child(id)
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                        val phone = snapshot.child("userData").child("phone").value.toString()
                        val nom = snapshot.child("userData").child("nom").value.toString()
                        val prenom = snapshot.child("userData").child("prenom").value.toString()
                        val age = snapshot.child("userData").child("age").value.toString().toInt()
                        val sexe = snapshot.child("userData").child("sexe").value.toString()
                        val mdp = snapshot.child("userData").child("mdp").value.toString()
                        val imgProfileUrl = snapshot.child("userData").child("imgProfileUrl").value.toString()
                        val relation = snapshot.child("userData").child("relation").value.toString()
                        val localisation = snapshot.child("userData").child("localisation").value.toString()
                        val language = snapshot.child("userData").child("language").value.toString()
                        val userData = UserData(id, phone, nom, prenom, age, sexe, mdp, imgProfileUrl, relation, localisation, language)

                        val gmail = snapshot.child("userInfo").child("contact").child("gmail").value.toString()
                        val whatsapp = snapshot.child("userInfo").child("contact").child("whatsapp").value.toString()
                        val messenger = snapshot.child("userInfo").child("contact").child("messenger").value.toString()

                        val sex = snapshot.child("userInfo").child("searching").child("sexe").value.toString()
                        val relationWanted = snapshot.child("userInfo").child("searching").child("relation").value.toString()
                        val ageWanted = snapshot.child("userInfo").child("searching").child("age").value.toString()

                        val bio = snapshot.child("userInfo").child("info").child("bio").value.toString()

                        selectedProfile = userData
                        selectedUser = Users(userData, null,
                            UserInfo(Contacts(whatsapp, messenger, gmail),
                                Searching(sex, relationWanted, ageWanted), Info(bio, null)
                            ))
                        loadData()
                    }
                }

                override fun onCancelled(error: DatabaseError) {}

            })
    }

    override fun onPause() {
        selectedProfile = null
        selectedProfileID = ""
        selectedUser = null
        isUnlocked = false
        super.onPause()
    }
    interface DetailActivityListener{
        fun onNbrKeyChanged(newNbrKey:Int)
    }
}