package com.lnd.RencontreAfricaine

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.lnd.RencontreAfricaine.ui.main.DiscussionFragment
import com.lnd.RencontreAfricaine.utils.SpinnerAdapter

class EditProfileActivity : AppCompatActivity() {
    companion object{
        var isNewUser = false
        var listener: OnFinishEdit? = null
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

    private val countryList = mutableListOf("Pays", "Mali", "Algerie",
        "Senegal", "Burkina Faso", "Niger", "Nigeria", "Togo", "Benin", "Cape Vert", "Gambie",
        "Ghana", "Guinee Bissau")

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
    private lateinit var spinCountry: Spinner
    private lateinit var cardFrench: CardView
    private lateinit var cardEnglish: CardView
    private lateinit var txtSelectedLanguage: TextView

    //Wanted
    private lateinit var constWanted: ConstraintLayout
    private lateinit var edBio: TextInputEditText
    private lateinit var edWantedAge1: TextInputEditText
    private lateinit var edWantedAge2: TextInputEditText
    private lateinit var imgWantedMen: ImageView
    private lateinit var imgWantedWomen: ImageView
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
        spinCountry = findViewById(R.id.spinnerCountry)
        cardFrench = findViewById(R.id.cardFrench)
        cardEnglish = findViewById(R.id.cardEnglish)
        txtSelectedLanguage = findViewById(R.id.txtSelectedLanguage)

        constWanted = findViewById(R.id.constWant)
        edBio = findViewById(R.id.edBio)
        edWantedAge1 = findViewById(R.id.edAgeWanted1)
        edWantedAge2 = findViewById(R.id.edAgeWanted2)
        imgWantedMen = findViewById(R.id.imgMenWanted)
        imgWantedWomen = findViewById(R.id.imgWomenWanted)

        constName.isVisible = false
        constName.isEnabled = false

        constWanted.isVisible = false
        constWanted.isEnabled = false

        constCountry.isVisible = false
        constCountry.isEnabled = false

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


        /*edWantedAge1.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0.toString().toInt()<18){
                    findViewById<TextInputLayout>(R.id.edlayoutAgeWanted1).editText!!.setText("18")
                }
            }
            override fun afterTextChanged(p0: Editable?) {}
        })
        edWantedAge2.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0.toString().toInt()<18){
                    findViewById<TextInputLayout>(R.id.edlayoutAgeWanted2).editText!!.setText("18")
                }
            }
            override fun afterTextChanged(p0: Editable?) {}
        })*/
        imgWantedMen.setOnClickListener {
            if (!selectedWantedSex.contains("Homme")){
                imgWantedMen.setImageResource(R.drawable.men_yes)
                selectedWantedSex = if (selectedWantedSex.contains("Femme")) "Homme / Femme" else "Homme"
            }else{
                imgWantedMen.setImageResource(R.drawable.men_no)
                selectedWantedSex = if (selectedWantedSex.contains("Femme")) "Femme" else ""
            }

            findViewById<TextView>(R.id.txtno8).text = "Je veux rencontrer un(e) $selectedWantedSex"

        }
        imgWantedWomen.setOnClickListener {
            if (!selectedWantedSex.contains("Femme")){
                imgWantedWomen.setImageResource(R.drawable.women_yes)
                selectedWantedSex = if (selectedWantedSex.contains("Homme")) "Homme / Femme" else "Femme"
            }else{
                imgWantedWomen.setImageResource(R.drawable.women_no)
                selectedWantedSex = if (selectedWantedSex.contains("Homme")) "Homme" else ""
            }

            findViewById<TextView>(R.id.txtno8).text = "Je veux rencontrer un(e) $selectedWantedSex"
        }

        //Listener Sex
        imgSexMen.setOnClickListener {
            imgSexMen.setImageResource(R.drawable.men_yes)
            imgSexWomen.setImageResource(R.drawable.women_no)

            Toast.makeText(this, "Je suis un Homme", Toast.LENGTH_LONG).show()
            selectedSex = "Homme"
        }
        imgSexWomen.setOnClickListener {
            imgSexMen.setImageResource(R.drawable.men_no)
            imgSexWomen.setImageResource(R.drawable.women_yes)

            Toast.makeText(this, "Je suis une Femme", Toast.LENGTH_LONG).show()
            selectedSex = "Femme"
        }

        //Listener Select Profile Image
        imgSelectedProfile.setOnClickListener {
            val intentProfile = Intent()
            intentProfile.type = "image/*"
            intentProfile.action = Intent.ACTION_GET_CONTENT

            resultLauncher.launch(intentProfile)
        }

        //Listener Card Relation
        cardMarriage.setOnClickListener {
            checkRelation("Marriage")
            cardMarriage.backgroundTintList = this.resources.getColorStateList(R.color.gray_background, null)
            selectedRelation = "Marriage"
        }
        cardRelationSerious.setOnClickListener {
            checkRelation("Relation Serieuse (Pouvant aboutir au marriage)")
            cardRelationSerious.backgroundTintList = this.resources.getColorStateList(R.color.gray_background, null)
            selectedRelation = "Relation Serieuse"
        }
        cardLove.setOnClickListener {
            checkRelation("Relation Amoureuse")
            cardLove.backgroundTintList = this.resources.getColorStateList(R.color.gray_background, null)
            selectedRelation = "Amour"
        }
        cardFriend.setOnClickListener {
            checkRelation("Relation amical")
            cardFriend.backgroundTintList = this.resources.getColorStateList(R.color.gray_background, null)
            selectedRelation = "Amitie"
        }
        cardSex.setOnClickListener {
            checkRelation("Relation Sexuelle")
            cardSex.backgroundTintList = this.resources.getColorStateList(R.color.gray_background, null)
            selectedRelation = "Relation Sexuel"
        }
        cardUnknown.setOnClickListener {
            checkRelation("Inconnue")
            cardUnknown.backgroundTintList = this.resources.getColorStateList(R.color.gray_background, null)
            selectedRelation = "Inconnue"
        }

        //Select Country
        spinCountry.adapter = SpinnerAdapter(this, countryList)

        spinCountry.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val filter = p1?.findViewById<TextView>(R.id.txtItemSpinner)?.text.toString()
                if(filter!="null" && filter!="Pays"){
                    selectedLocalisation = if (filter!="Monde / Partout"){
                        filter
                    } else{
                        ""
                    }
                }
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        cardFrench.setOnClickListener{
            if (selectedLanguage.contains("Français")){
                cardFrench.backgroundTintList = this.resources.getColorStateList(R.color.white, null);
                selectedLanguage = if (selectedLanguage.contains("Anglais")) "Anglais" else ""
            }
            else {

                cardFrench.backgroundTintList = this.resources.getColorStateList(R.color.gray_background, null);
                selectedLanguage = if (selectedLanguage.contains("Anglais")) "Français et Anglais" else "Français"
            }

            txtSelectedLanguage.text = selectedLanguage

        }

        cardEnglish.setOnClickListener{
            if (selectedLanguage.contains("Anglais")){
                cardEnglish.backgroundTintList = this.resources.getColorStateList(R.color.white, null);
                selectedLanguage = if (selectedLanguage.contains("Français")) "Français" else ""
            }else {
                cardEnglish.backgroundTintList = this.resources.getColorStateList(R.color.gray_background, null);
                selectedLanguage = if (selectedLanguage.contains("Français")) "Français et Anglais" else "Anglais"
            }

            txtSelectedLanguage.text = selectedLanguage
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

            }
        }
    }

    private var mImageUri: Uri? = null
    var mOldImageUrl: String? = null
    private val imgProfileRef = FirebaseStorage.getInstance().getReference("profiles")
    private fun uploadImage(){
        if(mImageUri != null){
            val name = if(MainActivity.currentUser!=null){
                MainActivity.currentUser!!.userData.id+ "."+getFileExtension(mImageUri!!)}
            else{ MainActivity.newUserData!!["id"].toString()+ "."+getFileExtension(mImageUri!!)}

            //System.currentTimeMillis().toString() + "."+getFileExtension(mImageUri!!)
            val fileReference = imgProfileRef.child(name)

            //if user already uploaded a image profile
            /*if (mOldImageUrl!=null) {
                //Delete the old image profile before upload
                val nameOld = System.currentTimeMillis().toString() + "."+getFileExtension(mImageUri!!)
                val fileReferenceOld = imgProfileRef.child(nameOld)
                fileReferenceOld.delete().addOnSuccessListener {

                }
            }*/
            fileReference.putFile(mImageUri!!)
                .addOnSuccessListener {
                    fileReference.downloadUrl.addOnSuccessListener { uriUrl ->
                        profileUrl = uriUrl.toString()
                        mOldImageUrl = profileUrl
                        finishEdit()
                    }.addOnFailureListener{
                        errorEdit(it.toString())
                    }


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
        btnBack.isEnabled = true
        btnBack.isVisible = true
        btnConfirm.isEnabled = true
        btnConfirm.isVisible = true

        constFinish.isVisible = false
        constFinish.isEnabled = false
        goStep(7)

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
        lastProgress.progress = 0
        if (mImageUri!=null){
            uploadImage()
        }else{
            finishEdit()
        }

    }

    private fun finishEdit(){
        lastProgress.progress = 0
        val newMapData: MutableMap<String, Any?> = HashMap()
        val newMapStatue: MutableMap<String, Any?>  = HashMap()
        //in user Info
        val newMapContact: MutableMap<String, Any?>  = HashMap()
        val newMapSearching: MutableMap<String, Any?>  = HashMap()
        val newMapInfo: MutableMap<String, Any?>  = HashMap()
        //Register //prefer map then userData

        val id = if (isNewUser) MainActivity.newUserData!!["id"].toString() else MainActivity.currentUser!!.userData.id
        newMapData["id"] = id
        newMapData["phone"] = if (isNewUser) MainActivity.newUserData!!["phone"] else MainActivity.currentUser!!.userData.phone
        newMapStatue["nbrKey"] = if (isNewUser) MainActivity.newUserData!!["nbrKey"] else MainActivity.currentUser!!.userStatue!!.nbrKey
        newMapStatue["premiumDays"] = if (isNewUser) {
            if (MainActivity.newUserData!!["isPremium"].toString().toBoolean()){ 31 } else { 0 }
        }else MainActivity.currentUser!!.userStatue!!.premiumDays


        newMapData["nom"] = edName.text.toString()
        newMapData["prenom"] = ""
        newMapData["age"] = edAge.text.toString().toInt()
        newMapData["sexe"] = selectedSex
        newMapData["mdp"] = ""
        newMapData["imgProfileUrl"] = profileUrl
        newMapData["localisation"] = selectedLocalisation
        newMapData["language"] = selectedLanguage
        newMapData["relation"] = selectedRelation

        newMapStatue["connected"] = true


        //user Contact
        newMapContact["gmail"] = ""
        newMapContact["whatsapp"] = ""
        newMapContact["messenger"] = ""

        if (edWhatsapp.text.toString().isNotEmpty() &&
            edWhatsapp.text.toString()!="null"){
            newMapContact["whatsapp"] = edWhatsapp.text.toString()
        }
        if (edMessenger.text.toString().isNotEmpty() &&
            edMessenger.text.toString()!="null"){
            newMapContact["messenger"] = edMessenger.text.toString()
        }
        if (edGmail.text.toString().isNotEmpty() &&
            edGmail.text.toString()!="null"){
            newMapContact["gmail"] = edGmail.text.toString()
        }

        //user Wanted
        newMapSearching["sexe"] = selectedWantedSex
        newMapSearching["relation"] = selectedRelation
        newMapSearching["age"] = "${edWantedAge1.text}-${edWantedAge2.text}"


        //user Info
        newMapInfo["bio"] = edBio.text.toString().trim()
        newMapInfo["hobbies"] = ""

        mUserRef.child(id).child("userData").updateChildren(newMapData)
            .addOnSuccessListener {
                lastProgress.progress = 20
                mUserRef.child(id).child("userStatue").updateChildren(newMapStatue)
                    .addOnSuccessListener {
                        lastProgress.progress = 30
                        mUserRef.child(id).child("userInfo").child("contact").updateChildren(newMapContact)
                            .addOnSuccessListener {
                                lastProgress.progress = 40
                                mUserRef.child(id).child("userInfo").child("searching").updateChildren(newMapSearching)
                                    .addOnSuccessListener {
                                        lastProgress.progress = 60
                                        mUserRef.child(id).child("userInfo").child("info").updateChildren(newMapInfo)
                                            .addOnSuccessListener {
                                                lastProgress.progress = 80
                                                MainActivity.currentUser = Users(
                                                    UserData(newMapData["id"].toString(), newMapData["phone"].toString(),
                                                        newMapData["nom"].toString(), newMapData["prenom"].toString(),
                                                        newMapData["age"].toString().toInt(), newMapData["sexe"].toString(),
                                                        newMapData["mdp"].toString(), newMapData["imgProfileUrl"].toString(),
                                                        newMapData["relation"].toString(), newMapData["localisation"].toString(),
                                                        newMapData["language"].toString()),
                                                    UserStatue(newMapStatue["nbrKey"].toString().toInt(),
                                                        newMapStatue["connected"].toString().toBoolean(),
                                                        newMapStatue["premiumDays"].toString().toInt()),
                                                    UserInfo(
                                                        Contacts(newMapContact["whatsapp"].toString(),
                                                            newMapContact["messenger"].toString(), newMapContact["gmail"].toString()),
                                                        Searching(newMapSearching["sexe"].toString(), newMapSearching["relation"].toString(),
                                                            newMapSearching["age"].toString()),
                                                        Info(newMapInfo["bio"].toString(), null))
                                                )
                                                lastProgress.progress = 90

                                                if (isNewUser){
                                                    var i =0
                                                    for (userChat in DiscussionFragment.listChats){
                                                        val newMap : MutableMap<String, Any?> = HashMap()
                                                        newMap["toUserId"] = userChat.toUserId
                                                        newMap["imgProfile"] = userChat.imgProfile
                                                        newMap["lastMessage"] = userChat.lastMessage
                                                        newMap["nbrNewMessage"] = userChat.nbrNewMessage
                                                        newMap["connected"] = userChat.connected
                                                        newMap["toUserId"] = userChat.imgProfile
                                                        newMap["name"] = userChat.name
                                                        FirebaseDatabase.getInstance().reference.child("Users")
                                                            .child(id)
                                                            .child("userChats").child(userChat.toUserId)
                                                            .updateChildren(newMap)
                                                            .addOnSuccessListener {
                                                                progress.progress = 95
                                                                if (i==DiscussionFragment.listChats.size-1){
                                                                    //last item
                                                                    FirebaseDatabase.getInstance().reference.child("NewUsers")
                                                                        .child(FirebaseAuth.getInstance().currentUser!!.uid).removeValue()
                                                                        .addOnSuccessListener {
                                                                            MainActivity.newUserData = null
                                                                            isNewUser = false
                                                                            lastProgress.progress = 100
                                                                            listener?.onFinishEdit()
                                                                            startActivity(Intent(this, SplashActivity::class.java))
                                                                            finish()
                                                                        }
                                                                }
                                                                i++
                                                            }
                                                    }

                                                    if (DiscussionFragment.listChats.isEmpty()){
                                                        FirebaseDatabase.getInstance().reference.child("NewUsers")
                                                            .child(FirebaseAuth.getInstance().currentUser!!.uid).removeValue()
                                                            .addOnSuccessListener {
                                                                MainActivity.newUserData = null
                                                                isNewUser = false
                                                                lastProgress.progress = 100
                                                                listener?.onFinishEdit()
                                                                startActivity(Intent(this, SplashActivity::class.java))
                                                                finish()
                                                            }
                                                    }

                                                }else{
                                                    FirebaseDatabase.getInstance().reference.child("NewUsers")
                                                        .child(FirebaseAuth.getInstance().currentUser!!.uid).removeValue()
                                                        .addOnSuccessListener {
                                                            MainActivity.newUserData = null
                                                            isNewUser = false
                                                            lastProgress.progress = 100
                                                            listener?.onFinishEdit()
                                                            startActivity(Intent(this, SplashActivity::class.java))
                                                            finish()
                                                        }
                                                }
                                            }
                                            .addOnFailureListener { errorEdit(it.toString()) }
                                    }
                                    .addOnFailureListener { errorEdit(it.toString()) }
                            }
                    }.addOnFailureListener { errorEdit(it.toString()) }
            }
        //add a tout les user les donnée: localisation, language
    }

    private fun getFileExtension(uri:Uri):String?{
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(contentResolver.getType(uri))
    }

    private fun checkRelation(type:String) {
        cardMarriage.backgroundTintList = this.resources.getColorStateList(R.color.white, null)
        cardRelationSerious.backgroundTintList = this.resources.getColorStateList(R.color.white, null)
        cardLove.backgroundTintList = this.resources.getColorStateList(R.color.white, null)
        cardFriend.backgroundTintList = this.resources.getColorStateList(R.color.white, null)
        cardSex.backgroundTintList = this.resources.getColorStateList(R.color.white, null)
        cardUnknown.backgroundTintList = this.resources.getColorStateList(R.color.white, null)

        findViewById<TextView>(R.id.txtSeeking).text = type
    }

    private val timeBetweenStep:Long = 500
    private fun goStep(step: Int) {
        when(step){
            1->{constName.isVisible = true
                constName.isEnabled = true
            }
            2->{constWanted.isVisible = true
                constWanted.isEnabled =true
            }
            3->{constCountry.isVisible = true
                constCountry.isEnabled = true
            }
            4->{constProfile.isVisible = true
                constProfile.isEnabled = true
            }
            5->{constContact.isVisible = true
                constContact.isEnabled = true
            }
            6->{constRelation.isVisible = true
                constRelation.isEnabled = true
            }
            7->{constConfirm.isVisible = true
                constConfirm.isEnabled = true
            }
            8->{constFinish.isVisible = true
                constFinish.isEnabled = true
            }
        }

        if (step==1) {
            btnBack.isVisible = false
            btnBack.isEnabled = false
        }
        else{
            btnBack.isVisible = true
            btnBack.isEnabled = true
            btnBack.setOnClickListener {
                progress.isVisible = true
                when(step){
                    2->{constWanted.isVisible = false
                        constWanted.isEnabled = false
                    }
                    3->{constCountry.isVisible = false
                        constCountry.isEnabled = false
                    }
                    4->{constProfile.isVisible = false
                        constProfile.isEnabled = false
                    }
                    5->{constContact.isVisible = false
                        constContact.isEnabled = false
                    }
                    6->{constRelation.isVisible = false
                        constRelation.isEnabled = false
                    }
                    7->{constConfirm.isVisible = false
                        constConfirm.isEnabled = false
                    }
                    8->{constFinish.isVisible = false
                        constFinish.isEnabled = false
                    }
                }
                Handler(Looper.getMainLooper()).postDelayed({
                    progress.isVisible = false
                    goStep(step-1)
                }, timeBetweenStep)
            }
        }

        if(step==7){
            //Loaded data to confirm
            Glide.with(this).clear(imgProfile)
            if(mImageUri!=null){
                Glide.with(this).load(mImageUri).into(imgProfile)
            }else {
                Glide.with(this).load(profileUrl).into(imgProfile)
            }

            txtCountry.text = ""
            txtIsOnline.text = "En ligne"
            txtName.text = edName.text.toString()
            txtName2.text = edName.text.toString()
            txtSexAge.text = "$selectedSex - ${edAge.text} ans"
            when(selectedRelation){
                "Marriage"->imgWanted.setImageResource(R.drawable.iconsring)
                "Relation Serieuse"->imgWanted.setImageResource(R.drawable.iconsflower)
                "Amour"->imgWanted.setImageResource(R.drawable.iconsheart)
                "Amitie"->imgWanted.setImageResource(R.drawable.iconsfriend)
                "Relation Sexuel"->imgWanted.setImageResource(R.drawable.iconssex)
                else->imgWanted.setImageResource(R.drawable.iconsunknown)
            }

            if (edGmail.text!!.isNotEmpty()){
                txtGmail.text = edGmail.text
                txtGmail.isVisible = true
                findViewById<ImageView>(R.id.imgnoGmail).isVisible = true
            }else{
                txtGmail.isVisible = false
                findViewById<ImageView>(R.id.imgnoGmail).isVisible = false
            }
            if (edWhatsapp.text!!.isNotEmpty()){
                txtWhatsapp.text = edWhatsapp.text
                txtWhatsapp.isVisible = true
                findViewById<ImageView>(R.id.imgnoWhatsapp).isVisible = true
            }else{
                txtWhatsapp.isVisible = false
                findViewById<ImageView>(R.id.imgnoWhatsapp).isVisible = false
            }
            if (edMessenger.text!!.isNotEmpty()){
                txtMessenger.text = edMessenger.text
                txtMessenger.isVisible = true
                findViewById<ImageView>(R.id.imgnoMessenger).isVisible = true
            }else{
                txtMessenger.isVisible = false
                findViewById<ImageView>(R.id.imgnoMessenger).isVisible = false
            }

        }
        else if(step==8){
            btnBack.isEnabled = false
            btnBack.isVisible = false
            btnConfirm.isEnabled = false
            btnConfirm.isVisible = false
            confirmEdit()
        }

        btnConfirm.setOnClickListener {
            if(!checkValidity(step)){
                return@setOnClickListener
            }
            progress.isVisible = true
            when(step){
                1->{constName.isVisible = false
                    constName.isEnabled = false
                }
                2->{constWanted.isVisible = false
                    constWanted.isEnabled = false
                }
                3->{constCountry.isVisible = false
                    constCountry.isEnabled = false
                }
                4->{constProfile.isVisible = false
                    constProfile.isEnabled = false
                }
                5->{constContact.isVisible = false
                    constContact.isEnabled = false
                }
                6->{constRelation.isVisible = false
                    constRelation.isEnabled = false
                }
                7->{constConfirm.isVisible = false
                    constConfirm.isEnabled = false
                }
            }
            Handler(Looper.getMainLooper()).postDelayed({
                progress.isVisible = false
                goStep(step+1)
            }, timeBetweenStep)
        }


    }

    private var selectedSex = ""
    private var selectedWantedSex = ""
    private var profileUrl = ""
    private var selectedRelation = ""
    private var selectedLocalisation = ""
    private fun checkValidity(step: Int):Boolean{
        var error = ""
        when(step){
            1 -> {
                if (edName.text.toString().length<6){
                    error += "\n-Le nom doit contenir au moins 6 caractère"
                }
                if (selectedSex.isEmpty()){
                    error+= "\n-vous devez choisir votre sexe (Homme ou Femme) "
                }
                if (edAge.text.toString().isEmpty() || edAge.text.toString()=="null"){

                    error+= "\n-vous devez choisir votre Age (au moins 18 ans) "
                }else if(edAge.text.toString().toInt()<18){
                    error = "Votre age doit être au minimum de 18 ans pour utiliser cet application"
                }

            }
            2->{

                if (edWantedAge1.text.toString().isEmpty() || edWantedAge2.text.toString().isEmpty()){
                    error += "\n-vous devez choisir l'age votre de partenaire (exemple: entre 18 et 30 ans)"
                }
                if (selectedWantedSex.isEmpty()){
                    error += "\n-vous devez choisir le sexe de partenaire que vous rechercher (Homme ou/et Femme)"
                }
                if ( edWantedAge1.text.toString().isNotEmpty() && edWantedAge2.text.toString().isNotEmpty()
                        && (edWantedAge1.text.toString().toInt()> edWantedAge2.text.toString().toInt())){
                    val c = edWantedAge1.text.toString()
                    findViewById<TextInputLayout>(R.id.edlayoutAgeWanted1).editText!!.setText(edWantedAge2.text.toString())
                    findViewById<TextInputLayout>(R.id.edlayoutAgeWanted2).editText!!.setText(c)
                }
                if (edWantedAge1.text.toString().isNotEmpty()
                    && edWantedAge1.text.toString().toInt()<18){
                    error += "L'age minimum est de 18 ans, appuyer sur Confirmer a nouveau !"
                    findViewById<TextInputLayout>(R.id.edlayoutAgeWanted1).editText!!.setText("18")
                }
            }
            3->{
                if (selectedLanguage.isEmpty()){
                    error+= "\nVous devez choisir le(s) language(s) que vous parler"
                }
                if (selectedLocalisation.isEmpty()){
                    error += "\nVous devez selectionner votre pays"
                }
            }
            4-> {
                if (profileUrl.isEmpty() && mImageUri==null){
                    error+= "\n-vous devez choisir un image de profile "
                }
            }
            5-> {
                if ((edGmail.text.toString().isNotEmpty() ||
                            edMessenger.text.toString().isNotEmpty() ||
                            edWhatsapp.text.toString().isNotEmpty())
                    && !checkBoxContact.isChecked){
                    error += "\n-Vous devez accepter la visibilité public des informations que vous avez remplie" +
                            "\nCocher la case: J'accepte d'être contacter par Email, Messenger ou Whatsapp ..." +
                            "\n\nOu supprimer toute les information dans vos contacte"
                }
            }
            6-> {
                if (selectedRelation.isEmpty()){
                    error+= "\n-vous devez choisir le type de relation que vous rechercher de profile "
                }
            }
        }
        if (error.isNotEmpty()){
            showError(error)
            return false
        }
        return true
    }

    private fun showError(error: String) {
        AlertDialog.Builder(this)
            .setTitle("Erreur")
            .setMessage("Une erreur est survenue:" +
                    "\n$error" +
                    "\nCorriger ces erreur puis confirmer")
            .setPositiveButton("Ok"){_,_->}
            .create().show()
    }

    private fun loadData() {
        progress.isVisible = true
        val user = MainActivity.currentUser!!

        //step 1: Name
        constName.isEnabled = true
        edName.setText(user.userData.nom)
        edAge.setText(user.userData.age.toString())
        if (user.userData.sexe=="Homme"){
            imgSexMen.performClick()
        }else{
            imgSexWomen.performClick()
        }
        constName.isEnabled = false

        //step 2 : Wanted
        constWanted.isEnabled = true
        edBio.setText(user.userInfo.info!!.bio)
        user.userInfo.searching.age
        edWantedAge1.setText(user.userInfo.searching.age.subSequence(0,2))
        edWantedAge2.setText(user.userInfo.searching.age.subSequence(3,5))

        if (user.userInfo.searching.sexe.contains("Homme")){
            imgWantedMen.performClick()
        }
        if (user.userInfo.searching.sexe.contains("Femme")){
            imgWantedWomen.performClick()
        }
        constWanted.isEnabled = false

        //step 3: Country and language
        constCountry.isEnabled = true

        for ((i, it) in countryList.withIndex()){
            if (user.userData.localisation==it){
                spinCountry.setSelection(i)
            }
        }
        if (user.userData.language.contains("Français")){
            cardFrench.performClick()
        }
        if (user.userData.language.contains("Anglais")){
            cardEnglish.performClick()
        }
        constCountry.isEnabled = false

        //step 4: Profile
        constProfile.isEnabled = true
        profileUrl = user.userData.imgProfileUrl
        mOldImageUrl = profileUrl
        Glide.with(this).clear(imgSelectedProfile)
        Glide.with(this).load(user.userData.imgProfileUrl)
            .into(imgSelectedProfile)

        constProfile.isEnabled = false

        //step 5: Contact
        constContact.isEnabled = true
        edGmail.setText(user.userInfo.contacts.gmail)
        edMessenger.setText(user.userInfo.contacts.messenger)
        edWhatsapp.setText(user.userInfo.contacts.whatsapp)
        checkBoxContact.isChecked =true
        constContact.isEnabled = false

        //step 6: relation
        constRelation.isEnabled = true
        when(user.userData.relation){
            "Marriage"->cardMarriage.performClick()
            "Relation Serieuse"->cardRelationSerious.performClick()
            "Amour"->cardLove.performClick()
            "Amitie"->cardFriend.performClick()
            "Relation Sexuel"->cardSex.performClick()
            else->cardUnknown.performClick()
        }
        constRelation.isEnabled = false

        //finished now go back to step 1
        progress.isVisible = false
        goStep(1)

    }


    interface OnFinishEdit{
        fun onFinishEdit()
    }
}