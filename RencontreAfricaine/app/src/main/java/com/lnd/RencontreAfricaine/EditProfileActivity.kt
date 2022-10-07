package com.lnd.RencontreAfricaine

import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.webkit.MimeTypeMap
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

class EditProfileActivity : AppCompatActivity() {
    companion object{
        var isNewUser = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        unit()

        if (isNewUser){
            goStep(1)
        }
        else{
            loadData()
        }
    }


    private lateinit var progress : ProgressBar
    private lateinit var btnConfirm : ImageView
    private lateinit var btnBack : ImageView

    private lateinit var constName: ConstraintLayout
    private lateinit var edName: TextInputEditText
    private lateinit var edAge: TextInputEditText
    private lateinit var imgSexMen: ImageView
    private lateinit var imgSexWomen: ImageView

    private lateinit var constProfile: ConstraintLayout
    private lateinit var imgSelectedProfile: ImageView

    private lateinit var constContact: ConstraintLayout
    private lateinit var edGmail: TextInputEditText
    private lateinit var edMessenger: TextInputEditText
    private lateinit var edWhatsapp: TextInputEditText
    private lateinit var checkBoxContact: CheckBox


    private lateinit var constRelation: ConstraintLayout
    private lateinit var cardMarriage: CardView
    private lateinit var cardRelationSerious: CardView
    private lateinit var cardLove: CardView
    private lateinit var cardFriend: CardView
    private lateinit var cardSex: CardView
    private lateinit var cardUnknown: CardView

    private lateinit var constConfirm: ConstraintLayout
    private lateinit var imgProfile: ImageView
    private lateinit var imgWanted: ImageView
    private lateinit var txtCountry : TextView
    private lateinit var txtIsOnline : TextView
    private lateinit var txtName : TextView
    private lateinit var txtName2 : TextView
    private lateinit var txtSexAge : TextView
    private lateinit var txtGmail : TextView
    private lateinit var txtWhatsapp : TextView
    private lateinit var txtMessenger : TextView

    private lateinit var constFinish: ConstraintLayout
    private lateinit var txtProgress : TextView
    private lateinit var lastProgress: ProgressBar

    private lateinit var constCountry: ConstraintLayout
    private lateinit var edCountry: TextInputEditText
    private lateinit var cardFrench: CardView
    private lateinit var cardEnglish: CardView
    private lateinit var txtSelectedLanguage: TextView
    private fun unit() {
        btnConfirm = findViewById(R.id.btnConfirm)
        btnBack = findViewById(R.id.btnback)
        progress = findViewById(R.id.progress)

        constName = findViewById(R.id.constName)
        edName = findViewById(R.id.edName)
        edAge = findViewById(R.id.edAge)
        imgSexMen = findViewById(R.id.imgMen)
        imgSexWomen = findViewById(R.id.imgWomen)

        constProfile = findViewById(R.id.constProfile)
        imgSelectedProfile = findViewById(R.id.imgSelectProfile)

        constContact = findViewById(R.id.constContact)
        edGmail = findViewById(R.id.edGmail)
        edMessenger = findViewById(R.id.edMessenger)
        edWhatsapp = findViewById(R.id.edWhatsapp)
        checkBoxContact = findViewById(R.id.checkContact)

        constRelation = findViewById(R.id.constRelation)
        cardMarriage = findViewById(R.id.cardMariage)
        cardRelationSerious = findViewById(R.id.cardRelationSerieus)
        cardLove = findViewById(R.id.cardLove)
        cardFriend = findViewById(R.id.cardFriend)
        cardSex = findViewById(R.id.cardSex)
        cardUnknown = findViewById(R.id.cardUnknown)

        constConfirm = findViewById(R.id.constConfirm)
        imgProfile = findViewById(R.id.imgProfile)
        imgWanted = findViewById(R.id.imgWanted)
        txtCountry = findViewById(R.id.txtCountry)
        txtIsOnline = findViewById(R.id.txtIsOnline)
        txtName = findViewById(R.id.txtName)
        txtName2 = findViewById(R.id.txtUsername)
        txtSexAge = findViewById(R.id.txtSexAge)
        txtGmail = findViewById(R.id.txtgmail)
        txtWhatsapp = findViewById(R.id.txtwhatsapp)
        txtMessenger = findViewById(R.id.txtmessenger)

        constFinish = findViewById(R.id.constFinish)
        lastProgress = findViewById(R.id.lastProgress)
        txtProgress = findViewById(R.id.txtProgress)

        constCountry = findViewById(R.id.constCountry)
        edCountry = findViewById(R.id.edCountry)
        cardFrench = findViewById(R.id.cardFrench)
        cardEnglish = findViewById(R.id.cardEnglish)
        txtSelectedLanguage = findViewById(R.id.txtSelectedLanguage)

        constName.isVisible = false
        constName.isEnabled = false

        constProfile.isVisible = false
        constProfile.isEnabled = false

        constContact.isVisible = false
        constContact.isEnabled = false

        constRelation.isVisible = false
        constRelation.isEnabled = false

        constConfirm.isVisible = false
        constConfirm.isEnabled = false

        constFinish.isVisible = false
        constFinish.isEnabled = false

        constCountry.isVisible = false
        constCountry.isEnabled = false

        //Listener Sex
        imgSexMen.setOnClickListener {
            imgSexMen.setImageResource(R.drawable.men_yes)
            imgSexWomen.setImageResource(R.drawable.women_no)

            Toast.makeText(this, "Je suis un Homme", Toast.LENGTH_LONG).show()
            selectedSex = "Male"
        }
        imgSexWomen.setOnClickListener {
            imgSexMen.setImageResource(R.drawable.men_no)
            imgSexWomen.setImageResource(R.drawable.women_yes)

            Toast.makeText(this, "Je suis une Femme", Toast.LENGTH_LONG).show()
            selectedSex = "Female"
        }

        //Listener Select Profile Image
        imgSelectedProfile.setOnClickListener {
            val intentProfile = Intent()
            intentProfile.type = "image/*"
            intentProfile.action = Intent.ACTION_GET_CONTENT

            resultLauncher.launch(intent)
        }

        //Listener Card Relation
        uncheckRelation("Marriage")
        cardMarriage.setOnClickListener {
            uncheckRelation("Marriage")
            cardMarriage.setBackgroundResource(R.color.white)
        }
        cardRelationSerious.setOnClickListener {
            uncheckRelation("Relation Seurieuse (Pouvant aboutir au mariage)")
            cardRelationSerious.setBackgroundResource(R.color.white)
        }
        cardLove.setOnClickListener {
            uncheckRelation("Relation Amoureuse")
            cardLove.setBackgroundResource(R.color.white)
        }
        cardFriend.setOnClickListener {
            uncheckRelation("Relation amical")
            cardFriend.setBackgroundResource(R.color.white)
        }
        cardSex.setOnClickListener {
            uncheckRelation("Relation Sexuelle")
            cardSex.setBackgroundResource(R.color.white)
        }
        cardUnknown.setOnClickListener {
            uncheckRelation("Inconnue")
            cardUnknown.setBackgroundResource(R.color.white)
        }

        //Select Country
        cardFrench.setOnClickListener{
            if (selectedLanguage.contains("Français,")){
                selectedLanguage.removePrefix("Français,")
                selectedLanguage.removeSuffix("Français,")
                cardFrench.setBackgroundResource(R.color.disabled_background)
            }
            else {
                selectedLanguage += "Français,"
                cardFrench.setBackgroundResource(R.color.white)
            }

            if(selectedLanguage.contains("Français") && selectedLanguage.contains("English")){
                txtSelectedLanguage.text = "Français et Anglais"
            }
            else if(selectedLanguage.contains("Français") && !selectedLanguage.contains("English")){
                txtSelectedLanguage.text = "Français"
            }
            else if(!selectedLanguage.contains("Français") && selectedLanguage.contains("English")){
                txtSelectedLanguage.text = "Anglais"
            }

        }

        cardEnglish.setOnClickListener{
            if (selectedLanguage.contains("English")){
                selectedLanguage.removePrefix("English")
                selectedLanguage.removeSuffix("English")
                cardFrench.setBackgroundResource(R.color.disabled_background)

            }else {
                selectedLanguage += "English,"
                cardEnglish.setBackgroundResource(R.color.white)
            }

            if(selectedLanguage.contains("Français") && selectedLanguage.contains("English")){
                txtSelectedLanguage.text = "Français et Anglais"
            }
            else if(selectedLanguage.contains("Français") && !selectedLanguage.contains("English")){
                txtSelectedLanguage.text = "Français"
            }
            else if(!selectedLanguage.contains("Français") && selectedLanguage.contains("English")){
                txtSelectedLanguage.text = "Anglais"
            }
        }


    }

    private var selectedLanguage = ""
    //Get Image Result after Image Picker
    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // There are no request codes
            val data: Intent? = result.data
            if (data!=null && data.data!=null){
                mImageUri = data.data

                Glide.with(this).clear(imgSelectedProfile)
                Glide.with(this)
                    .load(mImageUri)
                    .into(imgSelectedProfile)

                Glide.with(this).clear(imgProfile)
                Glide.with(this)
                    .load(mImageUri)
                    .into(imgProfile)

            }
        }
    }

    var mImageUri: Uri? = null
    var mOldImageUri: Uri? = null
    private val imgProfileRef = FirebaseStorage.getInstance().getReference("profiles")
    private fun uploadImage(){
        if(mImageUri != null){
            val name = System.currentTimeMillis().toString() + "."+getFileExtension(mImageUri!!)
            val fileReference = imgProfileRef.child(name)

            //if user already uploaded a image profile
            if (mOldImageUri!=null) {
                //Delete the old image profile before upload
                val nameOld = System.currentTimeMillis().toString() + "."+getFileExtension(mImageUri!!)
                val fileReferenceOld = imgProfileRef.child(nameOld)
                fileReferenceOld.delete()
            }
            fileReference.putFile(mImageUri!!)
                .addOnSuccessListener {
                    mOldImageUri = mImageUri
                    profileUrl = fileReference.downloadUrl.toString()
                    finishEdit()
                }
                .addOnFailureListener{
                    errorEdit(it.toString())
                }
                .addOnProgressListener {
                    val progression : Double = (100.0 * it.bytesTransferred / it.totalByteCount)
                    lastProgress.progress = progression.toInt()
                }
        }else{
            Toast.makeText(this, "Aucune image choisi", Toast.LENGTH_LONG).show()
        }
    }

    private fun errorEdit(e: String) {
        AlertDialog.Builder(this)
            .setTitle("Erreur")
            .setMessage("Il y a une erreur lors de la sauvegarde des données !" +
                    "\n\nEssayer:" +
                    "\n1-Verifier votre connection internet" +
                    "\n2-Changer votre image de profile par une autre" +
                    "\n\nError: $e")
            .setCancelable(false)
            .setPositiveButton("Ressayer"){_,_->
                confirmEdit()
            }
            .create().show()
    }

    private val mUserRef = FirebaseDatabase.getInstance().reference.child("Users")
    private fun confirmEdit() {
        progress.progress = 0
        uploadImage()

    }

    private fun finishEdit(){
        val newMapData: MutableMap<String, Any?> = HashMap()
        val newMapStatue: MutableMap<String, Any?>  = HashMap()
        //Register //prefer map then userData
        newMapData["id"] = MainActivity.newUserData!!["id"]
        newMapData["phone"] = MainActivity.newUserData["phone"]
        newMapStatue["nbrKey"] = MainActivity.newUserData["key"]


        newMapData["nom"] = edName.text.toString()
        newMapData["prenom"] = ""
        newMapData["age"] = edAge.text.toString().toInt()
        newMapData["sexe"] = selectedSex
        newMapData["mdp"] = ""
        newMapData["imgProfileUrl"] = profileUrl
        newMapData["localisation"] = edCountry.text.toString()
        newMapData["language"] = selectedLanguage

        newMapStatue["connected"] = true
        newMapStatue["premiumDays"] = 0

        mUserRef

        //add a tout les user les donnée: localisation, language
    }

    private fun getFileExtension(uri:Uri):String?{
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(contentResolver.getType(uri))
    }

    private fun uncheckRelation(type:String) {
        cardMarriage.setBackgroundResource(R.color.disabled_background)
        cardRelationSerious.setBackgroundResource(R.color.disabled_background)
        cardLove.setBackgroundResource(R.color.disabled_background)
        cardFriend.setBackgroundResource(R.color.disabled_background)
        cardSex.setBackgroundResource(R.color.disabled_background)
        cardUnknown.setBackgroundResource(R.color.disabled_background)

        findViewById<TextView>(R.id.txtSeeking).text = type

    }

    private val timeBetweenStep:Long = 300
    private fun goStep(step: Int) {
        when(step){
            1->{constName.isVisible = true
                constName.isEnabled = true
            }
            2->{constCountry.isVisible = true
                constCountry.isEnabled = true
            }
            3->{constProfile.isVisible = true
                constProfile.isEnabled = true
            }
            4->{constContact.isVisible = true
                constContact.isEnabled = true
            }
            5->{constRelation.isVisible = true
                constRelation.isEnabled = true
            }
            6->{constConfirm.isVisible = true
                constConfirm.isEnabled = true
            }
            7->{constFinish.isVisible = true
                constFinish.isEnabled = true
            }
        }

        if (step==1) {
            btnBack.isVisible = false
            btnBack.isEnabled = false
        }
        else if(step==2){
            btnBack.isVisible = true
            btnBack.isEnabled = true
            btnBack.setOnClickListener {
                goStep(step-1)
            }
        }

        btnConfirm.setOnClickListener {
            if(!checkValidity(step)){
                return@setOnClickListener
            }
            progress.isVisible = true
            Handler(Looper.getMainLooper()).postDelayed({
                when(step){
                    1->{constName.isVisible = false
                        constName.isEnabled = false
                    }
                    2->{constCountry.isVisible = false
                        constCountry.isEnabled = false
                    }
                    3->{constProfile.isVisible = false
                        constProfile.isEnabled = false
                    }
                    4->{constContact.isVisible = false
                        constContact.isEnabled = false
                    }
                    5->{constRelation.isVisible = false
                        constRelation.isEnabled = false
                    }
                    6->{constConfirm.isVisible = false
                        constConfirm.isEnabled = false
                    }
                    7->{constFinish.isVisible = false
                        constFinish.isEnabled = false
                    }
                }

                progress.isVisible = false
                goStep(step+1)
            }, timeBetweenStep)
        }


    }

    private var selectedSex = "none"
    private var profileUrl = "none"
    private var selectedRelation = "none"
    private fun checkValidity(step: Int):Boolean{
        var error = ""
        when(step){
            1 -> {
                if (edName.text.toString().length<6){
                    error += "\n-Le nom doit contenir au moins 6 caractère"
                }
                if (selectedSex=="none"){
                    error+= "\n-vous devez choisir votre sexe (Homme ou Femme) "
                }
            }
            2->{
                if (selectedLanguage.isEmpty()){
                    error+= "\nVous devez choisir les language que vous parler"
                }
                if (edCountry.text.toString().isEmpty()){
                    error += "\nVous devez ecrire le nom de votre pays"
                }
            }
            3 -> {
                if (profileUrl=="none"){
                    error+= "\n-vous devez choisir un image de profile "
                }
            }
            4 -> {
                if ((edGmail.text.toString().isNotEmpty() ||
                            edMessenger.text.toString().isNotEmpty() ||
                            edWhatsapp.text.toString().isNotEmpty())
                    && !checkBoxContact.isChecked){
                    error += "\n-Vous devez accepter la visibilité public des informations que vous avez remplie" +
                            "\nCocher la case: ${R.string.check1}" +
                            "\n\nOu supprimer toute les information dans vos contacte"
                }
            }
            5 -> {
                if (selectedRelation=="none"){
                    error+= "\n-vous devez choisir le type de relation que vous rechercher de profile "
                }
            }
        }
        return true
    }

    private fun loadData() {

    }
}