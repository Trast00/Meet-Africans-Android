package com.lnd.RencontreAfricaine

import android.content.Context
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.lnd.RencontreAfricaine.R

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splache)

        if (checkConnectionType()){
            Toast.makeText(this, "Connecter", Toast.LENGTH_LONG).show()
        }
        else {
            AlertDialog.Builder(this)
                .setTitle("Echec de connexion")
                .setMessage("Impoossible d'accedez a internet:"+"\n"+ "Veuilllez verifier votre connection internet puis reessayer")
                .setPositiveButton("Ressayer"){_,_ ->}
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