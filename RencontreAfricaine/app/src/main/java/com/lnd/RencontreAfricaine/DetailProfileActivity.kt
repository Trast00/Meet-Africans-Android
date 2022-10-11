package com.lnd.RencontreAfricaine

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
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

        constDetail.isVisible = false
        constDetail.isEnabled = false
        progress.isVisible = true

        if (isUnlocked){
            btnContact.text = "Envoyer un message"
            findViewById<TextView>(R.id.txtBtnDescription).text = "Vous avez déjà contacter cette personne" +
                    "\nAppuyez sur 'Envoyer un message' pour continuer a discuter (gratuitement)"
        }
        btnContact.setOnClickListener {
            if (!isUnlocked){
                ChatActivity.partner = selectedUser
                startActivity(Intent(this, ChatActivity::class.java))
            }else{
                AlertDialog.Builder(this)
                    .setTitle("Debloquer cet utilisateur")
                    .setIcon(R.drawable.iconskey)
                    .setMessage("Vous avez besoin d'une clé pour parler a cet utilisateur")
                    .setPositiveButton("Debloquer cet utilisateur"){_,_->
                        //if user have enough key
                        if ((MainActivity.currentUser!=null
                                    && MainActivity.currentUser.userStatue!!.nbrKey>0)
                            || (MainActivity.newUserData!=null
                                    && MainActivity.newUserData["nbrKey"].toString().toInt()>0)){
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

    private fun unlockUser() {

    }
}