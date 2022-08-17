package com.example.transportml

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import com.example.transportml.ui.login.RegisterActivity
import com.google.firebase.auth.FirebaseAuth

class Splach : AppCompatActivity() {

    lateinit var progress: ProgressBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splach)

        progress = findViewById(R.id.progressSplach)

        connectUser()

    }

    private fun connectUser() {
        if (checkConnectionType()){
            Toast.makeText(this, "Connected", Toast.LENGTH_LONG).show()
            Handler().post {
                val user = FirebaseAuth.getInstance().currentUser
                if(user==null){
                    startActivity(Intent(this, RegisterActivity::class.java))
                }
                else{
                    startActivity(Intent(this, MainActivity::class.java))
                }

            }
        }
        else {
            Toast.makeText(this, "NON Connected", Toast.LENGTH_LONG).show()
            progress.isVisible = false
            AlertDialog.Builder(this).setTitle("Erreur")
                .setMessage("Impossible de se connecter a internet")
                .setPositiveButton("Ressayer"){ _,_ ->
                    progress.isVisible = true
                    connectUser()
                }
                .setNegativeButton("Quitter"){_,_ -> finish()}
                .create().show()
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
}