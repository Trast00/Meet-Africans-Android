package com.lnd.RencontreAfricaine

import android.content.Context
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
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

// feature branch
class SplashActivity : AppCompatActivity() {
    val CURRENT_VERSION = 3
    val progressStep = 20

    val TAG = "SplachActivity"
    lateinit var progress : ProgressBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splache)

        progress = findViewById(R.id.progress)
        progress.progress = 0
        unit(0)

    }

    var userID = ""
    private fun unit(step:Int) {
        Log.d(TAG, "unit")
        //Check Internet connection && Get InfoServer
        if (step==0){
            //if internet is on: Get InfoServer
            if (checkConnectionType()){
                Toast.makeText(this, "Connecter", Toast.LENGTH_LONG).show()
                getInfoServer()

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
            if (FirebaseAuth.getInstance().currentUser!=null){
                userID = FirebaseAuth.getInstance().currentUser!!.uid
                unit(2)
            }
        }
        else if (step==3){
            getInfoServer()
        }

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

    var infoServer: InfoServer? =null
    fun getInfoServer(){
        FirebaseDatabase.getInstance().reference.child("InfoServer")
            .addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    Log.d(TAG, "fb addSL")
                    val adminPhone = snapshot.child("adminPhone").value.toString()
                    val adminEmail = snapshot.child("adminEmail").value.toString()
                    val isAvailable = snapshot.child("isAvailable").value.toString().toBoolean()
                    val minVersion = snapshot.child("minVersion").value.toString().toInt()
                    val premuimPhone = snapshot.child("premuimPhone").value.toString()

                    val title = snapshot.child("messages").child("title").value.toString()
                    val description = snapshot.child("messages").child("description").value.toString()
                    infoServer = InfoServer(adminPhone, adminEmail, isAvailable, minVersion, premuimPhone, title, description)
                    appDisabled(isAvailable)
                    updateNeeded(minVersion)
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