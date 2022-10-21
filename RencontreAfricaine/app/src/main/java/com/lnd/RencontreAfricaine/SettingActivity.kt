package com.lnd.RencontreAfricaine

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.webkit.MimeTypeMap
import android.widget.ImageView
import android.widget.RatingBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class SettingActivity : AppCompatActivity() {
    companion object{
        var listener: SettingListener? = null
    }

    private lateinit var loadingDiag: AlertDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        loadingDiag = AlertDialog.Builder(this)
            .setView(LayoutInflater.from(this).inflate(R.layout.loadingdiag, null))
            .setCancelable(false)
            .create()


        //btn to quit
        findViewById<ImageView>(R.id.btnBackSetting)
            .setOnClickListener { finish() }

        val cardRateUs = findViewById<CardView>(R.id.cardRateUs)
        val cardContactUs = findViewById<CardView>(R.id.cardContactUs)
        val cardReportBug = findViewById<CardView>(R.id.cardReportBug)
        val cardSignOut = findViewById<CardView>(R.id.cardSignOut)
        val cardDeleteAccount = findViewById<CardView>(R.id.cardDeleteAccount)

        cardContactUs.setOnClickListener {
            startActivity(Intent(this, ContactUsActivity::class.java))
        }
        cardReportBug.setOnClickListener {
            startActivity(Intent(this, ReportBugActivity::class.java))
        }
        cardSignOut.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Deconnexion")
                .setMessage("Voulez vous vraiment vous deconnecter ?" +
                        "\n\n-Vous souvenez vous de votre mot de passe ?")
                .setPositiveButton("Deconnexion"){_,_ ->
                    listener?.onCloseApp()
                    FirebaseAuth.getInstance().signOut()
                    startActivity(Intent(this, SplashActivity::class.java))
                    finish()
                }
                .setNegativeButton("Annuler"){_,_->}
                .create().show()
        }

        cardRateUs.setOnClickListener {
            val v = LayoutInflater.from(this).inflate(R.layout.rateusdiag, null)
            AlertDialog.Builder(this)
                .setTitle("Notez nous !")
                .setView(v)
                .setPositiveButton("Confirmer"){_,_->
                    val rate = v.findViewById<RatingBar>(R.id.rateDiag).rating
                    val applicationName = "com.lnd.RencontreAfricaine&hl=fr"//this.applicationInfo.nonLocalizedLabel.toString()
                    if (rate>3){
                        //Open Play Store for a real rate
                        startActivity(
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("market://details?id=$applicationName")
                            )
                        )
                    }
                    else{
                        AlertDialog.Builder(this)
                            .setTitle("Merci !")
                            .setMessage("Merci de votre appreciation !" +
                                    "\nDesolé d'entendre que notre application ne satisfait pas vos esperance." +
                                    "\nAider Nous a ameliorer l'application: Contactez Nous et faite nous une suggestion!")
                            .setPositiveButton("Faire Une Suggestion"){_,_->
                                ContactUsActivity.title = "Suggestion: "
                                startActivity(Intent(this, ContactUsActivity::class.java))
                            }
                            .setNegativeButton("Non"){_,_-> }
                            .create().show()
                    }
                }
                .create().show()
        }


        cardDeleteAccount.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Supprimer de compte")
                .setIcon(R.drawable.iconsreportbug)
                .setMessage("Voulez vous vraiment supprimer votre compte ?" +
                        "\nVous ne pourrez plus vous connecter et discuter avec vos amis !")
                .setPositiveButton("SUPPRIMER MON COMPTE"){_,_->
                    val v = LayoutInflater.from(this).inflate(R.layout.deleteaccountdiag, null)
                    val edConfirm = v.findViewById<TextInputEditText>(R.id.edConfirmDiag)

                    edConfirm.addTextChangedListener {
                        if (edConfirm.text.toString()!="SUPPRIMER"){
                            edConfirm.error = "Vous devez ecrire 'SUPPRIMER' en majuscule et sans erreur"
                        }
                    }
                    AlertDialog.Builder(this)
                        .setTitle("Supprission de compte")
                        .setMessage("Nous somme desolé de vous voir nous quitter.")
                        .setView(v)
                        .setPositiveButton("Comfirmer"){_,_->
                            val confirmText = edConfirm.text.toString()
                            if (confirmText!="SUPPRIMER"){
                                showError("Vous n'avez pas confirmer la suppression" +
                                        "\n\nCode de confirmation Incorrecte")
                                return@setPositiveButton
                            }
                            if (MainActivity.currentUser!=null){
                                deleteUser(MainActivity.currentUser!!.userData.id)
                            }
                            else if (MainActivity.newUserData!=null){
                                deleteUser(MainActivity.newUserData!!["id"].toString())
                            }

                        }.create().show()
                }
                .setNegativeButton("Annuler"){_,_->}
                .create().show()

        }

        if (MainActivity.currentUser==null && MainActivity.newUserData==null){
            cardSignOut.isVisible = false
            cardSignOut.isEnabled = false
            cardDeleteAccount.isVisible = false
            cardDeleteAccount.isEnabled = false
        }
    }

    private fun deleteUser(id: String) {
        loadingDiag.show()
        var path = "NewUsers"
        if (MainActivity.currentUser!=null){
            path = "Users"
        }


        //Delete Data
        FirebaseDatabase.getInstance().reference.child(path)
            .child(id).removeValue()
            .addOnSuccessListener {
                //Delete Profile Image
                //Delete the old image profile before upload
                if (MainActivity.currentUser!=null){
                    val nameOld = getPathStorageFromUrl(MainActivity.currentUser!!.userData.imgProfileUrl)
                    val fileReferenceOld = FirebaseStorage.getInstance().getReference("profiles")
                        .child(nameOld)
                    fileReferenceOld.delete().addOnCompleteListener {
                        FirebaseAuth.getInstance().signOut()
                        listener?.onCloseApp()
                        AlertDialog.Builder(this)
                            .setTitle("Au Revoir")
                            .setMessage("Nous somme encore désoler de vous voir partir. " +
                                    "\nNous esperons que vous avez rencontré votre âme soeur")
                            .setPositiveButton("Au Revoir"){_,_->
                                finish()
                            }
                            .create().show()
                    }
                }
                else{
                    FirebaseAuth.getInstance().signOut()
                    listener?.onCloseApp()
                    AlertDialog.Builder(this)
                        .setTitle("Au Revoir")
                        .setMessage("Nous somme encore désoler de vous voir partir. " +
                                "\nNous esperons que vous avez rencontré votre âme soeur")
                        .setPositiveButton("Au Revoir"){_,_->
                            finish()
                        }
                        .create().show()
                }
            }
            .addOnFailureListener {
                showError(it.toString())
            }

    }

    fun getPathStorageFromUrl(url:String):String{

        val baseUrl = "https://firebasestorage.googleapis.com/v0/b/project-80505.appspot.com/o/";

        var imagePath:String = url.replace(baseUrl,"");

        val indexOfEndPath = imagePath.indexOf("?");

        imagePath = imagePath.substring(0,indexOfEndPath);

        imagePath = imagePath.replace("%2F","/");


        return imagePath;
    }

    private fun getFileExtension(uri:Uri):String?{
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(contentResolver.getType(uri))
    }

    private fun showError(e: String) {
        AlertDialog.Builder(this)
            .setTitle("Erreur")
            .setMessage("Une erreur est survenue: \n$e")
            .setPositiveButton("Ok"){_,_ -> }
            .create().show()
    }

    interface SettingListener {
        fun onCloseApp()
    }
}