package com.lnd.RencontreAfricaine

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PremiumActivity : AppCompatActivity() {

    companion object{
        var buyUrl = ""
        var buyPhone = ""
        var buyGmail = ""
        var listener: PremiumListener? =null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_premuim)

        unit()
    }

    private lateinit var btnBuy: AppCompatButton
    private lateinit var edCode: TextInputEditText
    private lateinit var btnConfirm: AppCompatButton
    private var loadingDiag : AlertDialog?= null
    private fun unit() {
        btnBuy = findViewById(R.id.btnBuyCode)
        edCode = findViewById(R.id.edCodePremium)
        btnConfirm = findViewById(R.id.btnConfirmCode)

        loadingDiag = AlertDialog.Builder(this)
            .setView(LayoutInflater.from(this).inflate(R.layout.loadingdiag, null))
            .setCancelable(false)
            .create()

        btnBuy.setOnClickListener {
            if (buyUrl.isNotEmpty()){
                startActivity(Intent(this, BuyCodeActivity::class.java))
            }
            else{
                AlertDialog.Builder(this)
                    .setTitle("Acheter un code premium")
                    .setMessage("Pour acheter un code premium veuillez contacter: " +
                            "\nWhatsapp: $buyPhone " +
                            "\nGmail: $buyGmail")
                    .setPositiveButton("Ok"){_,_ ->}
                    .setCancelable(false)
                    .create().show()
            }
        }

        btnConfirm.setOnClickListener {
            loadingDiag?.show()
            val code = edCode.text.toString()
            if (code.length<4){
                edCode.error = "Il faut au moins 4 caractère pour un code premium"
                loadingDiag?.dismiss()
                return@setOnClickListener
            }
            checkCode(code)
        }
    }

    private fun checkCode(code:String) {
        FirebaseDatabase.getInstance().reference.child("Codes")
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                        if (snapshot.child(code).exists() && !snapshot.child(code).value.toString().toBoolean()){
                            givePremium(code)
                        } else if (snapshot.child(code).exists() && snapshot.child(code).value.toString().toBoolean()){
                            showError("Code expirer", "Cet Code a deja été utilisé")
                            loadingDiag?.dismiss()
                        }
                        else{
                            showError("Code inexistant", "Cet code n'existe pas")
                            loadingDiag?.dismiss()
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun givePremium(code:String) {
        if (MainActivity.newUserData!=null){
            FirebaseDatabase.getInstance().reference.child("Codes").child(code).setValue(true)
            FirebaseDatabase.getInstance().reference.child("NewUsers")
                .child(MainActivity.newUserData!!["id"].toString())
                .child("nbrKey").setValue(900)
                .addOnSuccessListener {
                    FirebaseDatabase.getInstance().reference.child("NewUsers")
                        .child(MainActivity.newUserData!!["id"].toString())
                        .child("isPremium").setValue(true)
                        .addOnSuccessListener {
                            finishedPremium()
                        }
                }



        } else if(MainActivity.currentUser!=null){
            FirebaseDatabase.getInstance().reference.child("Codes").child(code).setValue(true)

            FirebaseDatabase.getInstance().reference.child("Users")
                .child(MainActivity.currentUser!!.userData.id)
                .child("userStatue").child("nbrKey").setValue(900)
                .addOnSuccessListener {
                    FirebaseDatabase.getInstance().reference.child("Users")
                        .child(MainActivity.currentUser!!.userData.id)
                        .child("userStatue").child("premiumDays").setValue(31)
                        .addOnSuccessListener {
                            finishedPremium()
                        }
                }
        }
        else{
            loadingDiag?.dismiss()
            AlertDialog.Builder(this)
                .setTitle("Connection requise !")
                .setMessage("Vous devez être connecter pour devenir premuim")
                .setPositiveButton("Créer un Compte"){_,_->
                    startActivity(Intent(this, RegisterActivity::class.java))
                    finish()
                }
                .setCancelable(false)
                .create().show()
        }
    }

    private fun finishedPremium() {
        loadingDiag?.dismiss()
        AlertDialog.Builder(this)
            .setTitle("Félicitation")
            .setMessage("Merci, vous êtes maintenant premium !")
            .setPositiveButton("Ok"){_,_->
                listener?.onGivePremium()
                startActivity(Intent(this, SplashActivity::class.java))
                finish()
            }
            .setCancelable(false)
            .create().show()
    }

    private fun showError(title:String, description:String){
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(description)
            .setPositiveButton("Ok"){_,_-> }
            .setCancelable(false)
            .create().show()
    }

    interface PremiumListener{
        fun onGivePremium()
    }
}