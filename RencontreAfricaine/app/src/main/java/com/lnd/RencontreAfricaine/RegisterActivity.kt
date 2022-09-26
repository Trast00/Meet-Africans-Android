package com.lnd.RencontreAfricaine

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        unit()
    }

    private fun unit() {
        val btnInscription = findViewById<ImageView>(R.id.btnInscription)
        val btnConnexion = findViewById<ImageView>(R.id.btnInscription)
        val btnGoInscription = findViewById<TextView>(R.id.btnGoInscription)
        val btnGoConnexion = findViewById<TextView>(R.id.btnGoConnexion)
        val constInscription = findViewById<ConstraintLayout>(R.id.constInscription)
        val  constConnexion = findViewById<ConstraintLayout>(R.id.constConnexion)
        val edPhoneInscription = findViewById<TextInputEditText>(R.id.edPhoneInscription)
        val edPhoneConnexion = findViewById<TextInputEditText>(R.id.edPhoneConnexion)
        val edPasswordInscription = findViewById<TextInputEditText>(R.id.edpasswordInscription)
        val edPasswordConfirm = findViewById<TextInputEditText>(R.id.edpasswordConfirmInscription)
        val edPasswordConnexion = findViewById<TextInputEditText>(R.id.edpasswordConnexion)
        val progress = findViewById<ProgressBar>(R.id.progress)

        val mauth = FirebaseAuth.getInstance()

        btnInscription.setOnClickListener {
            constInscription.isVisible = false
            constInscription.isEnabled = false
            progress.isVisible = true

            val error = checkValidity(edPhoneInscription.text.toString(),
                edPasswordInscription.text.toString(), edPasswordConfirm.text.toString())

            if (error!=null){
                AlertDialog.Builder(this)
                    .setTitle("Erreur")
                    .setMessage(error)
                    .setCancelable(false)
                    .setPositiveButton("Ok"){_,_ ->}
                    .create().show()
                return@setOnClickListener
            }

            val phone = edPhoneInscription.text.toString()
            val email = edPhoneInscription.text.toString()+"@gmail.com"
            val password = edPasswordInscription.text.toString()

            if (mauth.currentUser==null){
                mauth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener{
                        progress.isVisible = false
                        constInscription.isVisible = true
                        constInscription.isEnabled = true
                    }
                    .addOnSuccessListener {
                        val id = mauth.currentUser?.uid.toString()
                        finishRegister(id, phone)
                    }
                    .addOnFailureListener {
                        AlertDialog.Builder(this)
                            .setTitle("Erreur")
                            .setMessage("Une erreur c'est produite lors de l'inscription, ressayer !" +
                                    "\nVerifier votre connexion internet !")
                            .setCancelable(false)
                            .setPositiveButton("Ressayer"){_,_-> }
                            .create().show()
                    }
            }
            else{finishRegister(mauth.currentUser?.uid.toString(), phone)}

        }

        btnConnexion.setOnClickListener {
            constConnexion.isVisible = false
            constConnexion.isEnabled = false
            progress.isVisible = true


        }
    }

    private fun finishRegister(id:String, phone:String) {
        val newMap = HashMap<Any, Any>()
        newMap["id"] = id
        newMap["phone"] = phone
        newMap["nbrKey"] = 15
        fb.child(id).setValue(newMap)
            .addOnSuccessListener {
                AlertDialog.Builder(this)
                    .setTitle("Inscription Reussi !")
                    .setMessage("Vous avez terminer votre inscription, vous pouvez maintenant " +
                            "\n-Acceder au contacte des autres utilisateur." +
                            "\n-Devenir membre premuim" +
                            "\n\nVous devez completer votre profils a fin d'envoyer des messages ou participer au evenement ! " +
                            "Voulez vous completer votre profile mainntenant ?")
                    .setPositiveButton("Completer Mon Profile"){_,_->
                        finish()
                        startActivity(Intent(this, EditProfileActivity::class.java))
                    }
                    .setNegativeButton("Plus tard"){_,_-> finish()}
                    .setCancelable(false)
                    .create().show()
            }.addOnFailureListener {
                AlertDialog.Builder(this)
                    .setTitle("Erreur")
                    .setMessage("Une erreur c'est produite lors de l'inscription, ressayer !" +
                            "\nVerifier votre connexion internet !")
                    .setCancelable(false)
                    .setPositiveButton("Ressayer"){_,_->
                        finishRegister(id, phone)
                    }
                    .create().show()
            }
    }

    val fb = FirebaseDatabase.getInstance().reference.child("NewUsers")

    private fun checkValidity(phone: String, password: String, passwordConfirm: String?):String?{
        if (phone.length<6 || phone.length>15){return "Le numero de telephone doit contenir au minimum 6 chiffre et au maximum 15"}
        if (password.length<6){return "Le mot de passe doit contenir au moins 6 caractere"}
        if (passwordConfirm!=null && passwordConfirm!=password){return "Le mot de passe et la confirmation de mot de passe sont different"}
        return null
    }
}