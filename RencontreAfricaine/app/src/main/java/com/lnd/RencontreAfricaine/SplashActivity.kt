package com.lnd.RencontreAfricaine

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.lnd.RencontreAfricaine.R
import com.lnd.RencontreAfricaine.ui.main.DiscoverFragment
import com.lnd.RencontreAfricaine.ui.main.DiscussionFragment


class SplashActivity : AppCompatActivity() {
    companion object{
        val CURRENT_VERSION = 3
        var infoServer: InfoServer? =null
    }

    val progressStep = 20

    private val TAG = "SplachActivity"
    lateinit var progress : ProgressBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splache)

        progress = findViewById(R.id.progress)
        progress.progress = 0
        //startActivity(Intent(this, RegisterActivity::class.java))
        unit(0)
        //correctData()

    }


    var userID = ""
    private fun unit(step:Int) {
        Log.d(TAG, "unit")
        //Check Internet connection && Get InfoServer
        if (step==0){
            //if internet is on: Get InfoServer
            if (checkConnectionType()){
                progress.progress = 20
                getInfoServer()
                progress.progress = 40
            }
            else {
                AlertDialog.Builder(this)
                    .setTitle("Echec de connexion")
                    .setMessage("Impossible d'accedez a internet:"+"\n"+ "Veuilllez verifier votre connection internet puis reessayer")
                    .setPositiveButton("Ressayer"){_,_ ->
                        unit(0)
                    }
                    .setNegativeButton("Quitter"){_,_ -> finish()}
                    .setCancelable(false)
                    .create().show()
            }
        }
        //check authentification
        else if (step==1){
            Handler(Looper.getMainLooper())
                .postDelayed({
                    MainActivity.newUserData = null
                    MainActivity.currentUser = null
                    DiscoverFragment.listUserFiltered = mutableListOf()
                    DiscoverFragment.listUser = mutableListOf()
                    DiscussionFragment.listChats = mutableListOf()

                    if (FirebaseAuth.getInstance().currentUser!=null){
                        userID = FirebaseAuth.getInstance().currentUser!!.uid
                        progress.progress = 60
                        getUserData()
                    }
                    else{
                        progress.progress = 100
                        startActivity(Intent(this, TutoActivity::class.java))
                        finish()
                    }
            }, 500)
        }


    }

    private fun getUserData() {
        //Try to get newUser Data (if not exist do nothing)
        FirebaseDatabase.getInstance().reference.child("NewUsers")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                        val id = snapshot.child("id").value.toString()
                        val phone = snapshot.child("phone").value.toString()
                        val nbrKey = snapshot.child("nbrKey").value.toString()

                        val newMap = HashMap<String, Any>()
                        newMap["id"] = id
                        newMap["phone"] = phone
                        newMap["nbrKey"] = nbrKey

                        //get Chat Item
                        for (item in snapshot.child("userChats").children){
                            val idChat = item.child("idChat").value.toString()
                            val name = item.child("name").value.toString()
                            val imgProfile = item.child("imgProfile").value.toString()
                            val toUserId = item.child("toUserId").value.toString()
                            val lastMessage = item.child("lastMessage").value.toString()
                            val nbrNewMessage = item.child("nbrNewMessage").value.toString().toInt()
                            val connected = item.child("connected").value.toString().toBoolean()
                            DiscussionFragment.listChats.add(UserChat(idChat, name, imgProfile, toUserId, lastMessage, nbrNewMessage, connected))
                        }


                        MainActivity.newUserData = newMap
                        progress.progress = 100
                        startActivity(Intent(this@SplashActivity, TutoActivity::class.java))
                        finish()
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })

        //Try to get User Data (if not exist do nothing)
        FirebaseDatabase.getInstance().reference.child("Users")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
            .addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists() && snapshot.child("userData").child("id").exists()){
                        val id = FirebaseAuth.getInstance().currentUser!!.uid
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

                        val nbrKey = snapshot.child("userStatue").child("nbrKey").value.toString().toInt()
                        val connected = true
                        val premiumDays = snapshot.child("userStatue").child("premiumDays").value.toString().toInt()
                        val userStatue = UserStatue(nbrKey, connected, premiumDays)

                        var whatsapp = ""
                        var messenger = ""
                        var gmail = ""
                        var wantedSex = ""
                        var wantedRelation = ""
                        var wantedAge = ""
                        var bio = ""
                        if (snapshot.child("userInfo").exists()){
                            whatsapp = snapshot.child("userInfo").child("contact").child("whatsapp").value.toString()
                            messenger = snapshot.child("userInfo").child("contact").child("messenger").value.toString()
                            gmail = snapshot.child("userInfo").child("contact").child("gmail").value.toString()

                            wantedSex = snapshot.child("userInfo").child("searching").child("sexe").value.toString()
                            wantedRelation = snapshot.child("userInfo").child("searching").child("relation").value.toString()
                            wantedAge = snapshot.child("userInfo").child("searching").child("age").value.toString()

                            bio = snapshot.child("userInfo").child("info").child("bio").value.toString()
                        }
                        val userInfo = UserInfo(Contacts(whatsapp, messenger, gmail),
                            Searching(wantedSex, wantedRelation, wantedAge),
                            Info(bio, null))

                        MainActivity.currentUser = Users(userData, userStatue, userInfo)
                        progress.progress= 80
                        //get Chat Item
                        for (item in snapshot.child("userChats").children){
                            val idChat = item.child("idChat").value.toString()
                            val name = item.child("name").value.toString()
                            val imgProfile = item.child("imgProfile").value.toString()
                            val toUserId = item.child("toUserId").value.toString()
                            val lastMessage = item.child("lastMessage").value.toString()
                            val nbrNewMessage = item.child("nbrNewMessage").value.toString().toInt()
                            val connected = item.child("connected").value.toString().toBoolean()

                            DiscussionFragment.listChats.add(UserChat(idChat, name, imgProfile, toUserId, lastMessage, nbrNewMessage, connected))
                        }

                        progress.progress= 100
                        startActivity(Intent(this@SplashActivity, TutoActivity::class.java))
                        finish()
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun checkConnectionType():Boolean{
        val connectionManager = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val wifiConnection = connectionManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        val mobileDataConnection = connectionManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)

        var isConnected = false
        if(wifiConnection!=null && wifiConnection.isConnectedOrConnecting){
            isConnected = true
        }
        else if (mobileDataConnection!=null && mobileDataConnection.isConnectedOrConnecting)
        {
            isConnected = true
        }
        return isConnected
    }

    private fun getInfoServer(){
        FirebaseDatabase.getInstance().reference.child("InfoServer")
            .addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val adminPhone = snapshot.child("adminPhone").value.toString()
                    val adminEmail = snapshot.child("adminEmail").value.toString()
                    val isAvailable = snapshot.child("isAvailable").value.toString().toBoolean()
                    val minVersion = snapshot.child("minVersion").value.toString().toInt()
                    val premiumPhone = snapshot.child("premuimPhone").value.toString()
                    val title = snapshot.child("messages").child("title").value.toString()
                    val description = snapshot.child("messages").child("description").value.toString()
                    infoServer = InfoServer(adminPhone, adminEmail, isAvailable, minVersion, premiumPhone, title, description)

                    appDisabled(isAvailable)
                    updateNeeded(minVersion)

                    PremiumActivity.buyPhone = premiumPhone
                    PremiumActivity.buyGmail = snapshot.child("premuimGmail").value.toString()
                    PremiumActivity.buyUrl = snapshot.child("buyUrl").value.toString()
                    unit(1)
                }
            }


            override fun onCancelled(error: DatabaseError) {}

        })

    }

    private fun updateNeeded(minVersion: Int) {
        if (minVersion>CURRENT_VERSION){
            AlertDialog.Builder(this)
                .setTitle("Mise a jours  requise")
                .setMessage("Une nouvelle version de l'application est disponible ! \nTelecharger la afin d'acceder au derniere fonctionnalité !")
                .setPositiveButton("Mettre a jours"){_,_ ->
                    updateNeeded(minVersion)
                }
                .setNegativeButton("Quitter"){_,_ -> finish()}
                .setCancelable(false)
                .create().show()
        }
    }

    private fun appDisabled(available: Boolean) {
        if (!available){
            AlertDialog.Builder(this)
                .setTitle("L application est desactivé")
                .setMessage("L'application est actuellement desactivé pour des problemes de maintenance !" +
                        "\n\nContactez nous si vous avez des question:" +
                        "\nEmail: ${infoServer?.adminEmail}" +
                        "\nNumero de telephone: ${infoServer?.adminPhone}")
                .setNegativeButton("Quitter"){_,_ -> finish()}
                .setCancelable(false)
                .create().show()
        }

    }

}