package com.lnd.RencontreAfricaine

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class RegisterActivity : AppCompatActivity() {
    companion object{
        var listener: OnFinishRegister? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        unit()
    }

    private lateinit var constInscription : ConstraintLayout
    private lateinit var constConnexion : ConstraintLayout
    private lateinit var progress :ProgressBar
    private fun unit() {
        val btnInscription = findViewById<ImageView>(R.id.btnInscription)
        val btnConnexion = findViewById<ImageView>(R.id.btnConnexion)
        val btnGoInscription = findViewById<TextView>(R.id.btnGoInscription)
        val btnGoConnexion = findViewById<TextView>(R.id.btnGoConnexion)
        constInscription = findViewById<ConstraintLayout>(R.id.constInscription)
        constConnexion = findViewById<ConstraintLayout>(R.id.constConnexion)
        val edPhoneInscription = findViewById<TextInputEditText>(R.id.edPhoneInscription)
        val edPhoneConnexion = findViewById<TextInputEditText>(R.id.edPhoneConnexion)
        val edPasswordInscription = findViewById<TextInputEditText>(R.id.edpasswordInscription)
        val edPasswordConfirm = findViewById<TextInputEditText>(R.id.edpasswordConfirmInscription)
        val edPasswordConnexion = findViewById<TextInputEditText>(R.id.edpasswordConnexion)
        progress = findViewById<ProgressBar>(R.id.progress)

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

            val phone = edPhoneConnexion.text.toString()
            val email = phone+"@gmail.com"
            val password = edPasswordConnexion.text.toString()

            if (mauth.currentUser==null){
                mauth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener {
                        val id = mauth.currentUser?.uid.toString()
                        checkUser(id)
                    }
                    .addOnFailureListener {
                        progress.isVisible = false
                        constConnexion.isVisible = true
                        constConnexion.isEnabled = true
                        AlertDialog.Builder(this)
                            .setTitle("Erreur")
                            .setMessage("Une erreur c'est produite lors de la connection, ressayer !" +
                                    "\n\n-Verifier votre connexion internet !" +
                                    "\n-Verifier votre numero de telephone et mot de passe")
                            .setCancelable(false)
                            .setPositiveButton("Ressayer"){_,_-> }
                            .create().show()
                    }
            }

        }

        btnGoInscription.setOnClickListener {
            progress.isVisible = true
            constConnexion.isVisible = false
            constConnexion.isEnabled = false

            Handler(Looper.getMainLooper())
                .postDelayed({
                    progress.isVisible = false
                    constInscription.isVisible = true
                    constInscription.isEnabled = true
                }, 300)
        }

        btnGoConnexion.setOnClickListener {
            progress.isVisible = true
            constInscription.isVisible = false
            constInscription.isEnabled = false

            Handler(Looper.getMainLooper())
                .postDelayed({
                    progress.isVisible = false
                    constConnexion.isVisible = true
                    constConnexion.isEnabled = true
                }, 300)
        }
        constConnexion.isVisible = false
        constConnexion.isEnabled = false
    }

    private fun checkUser(id: String) {
        FirebaseDatabase.getInstance().reference
            .addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                        if (snapshot.child("NewUsers").child(id).exists()
                            || snapshot.child("Users").child(id).exists()){
                            listener?.onFinishRegister()
                            startActivity(Intent(this@RegisterActivity, SplashActivity::class.java))
                            finish()
                        }

                        if (!snapshot.child("NewUsers").child(id).exists()
                            && !snapshot.child("Users").child(id).exists()){
                            //this account is deleted
                            deletedAccount(id)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun deletedAccount(id: String) {
        progress.isVisible = false
        constConnexion.isVisible = true
        constConnexion.isEnabled = true
        AlertDialog.Builder(this)
            .setTitle("Compte Supprimer")
            .setMessage("Cet Compte a été supprimer" +
                    "\nVoulez vous le restaurer ? Vous deverez vous re-inscrire")
            .setNegativeButton("S'inscrire"){_,_->
                val phone = findViewById<TextInputEditText>(R.id.edPhoneConnexion).text.toString()
                finishRegister(id, phone)
            }
            .setCancelable(false)
            .setPositiveButton("Annuler"){_,_->}
            .create().show()
    }

    private fun finishRegister(id:String, phone:String) {
        val newMap = HashMap<String, Any>()
        newMap["id"] = id
        newMap["phone"] = phone
        newMap["nbrKey"] = 15
        newMap["isPremium"] = false
        fb.child(id).setValue(newMap)
            .addOnSuccessListener {
                MainActivity.newUserData = newMap
                AlertDialog.Builder(this)
                    .setTitle("Inscription Reussi !")
                    .setMessage("Vous avez terminer votre inscription, vous pouvez maintenant " +
                            "\n-Acceder au contacte des autres utilisateur." +
                            "\n-Devenir membre premuim" +
                            "\n\nVous devez completer votre profils a fin d'envoyer des messages ou participer au evenement ! " +
                            "Voulez vous completer votre profile mainntenant ?")
                    .setPositiveButton("Completer Mon Profile"){_,_->
                        EditProfileActivity.isNewUser = true
                        startActivity(Intent(this, EditProfileActivity::class.java))
                        finish()
                    }
                    .setNegativeButton("Plus tard"){_,_->
                        listener?.onFinishRegister()
                        startActivity(Intent(this, SplashActivity::class.java))
                        finish()
                    }
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


    interface OnFinishRegister{
        fun onFinishRegister()
    }
}