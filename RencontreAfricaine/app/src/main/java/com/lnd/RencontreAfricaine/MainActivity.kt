package com.lnd.RencontreAfricaine

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.google.android.material.tabs.TabLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.lnd.RencontreAfricaine.databinding.ActivityMainBinding

import com.lnd.RencontreAfricaine.utils.ViewPagerAdapter

class MainActivity : AppCompatActivity() {
    companion object {
        var newUserData: HashMap<String, Any>? = null //id, phone, mdp, key
        var currentUser: Users? = null
    }



    //Comment 2
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar = findViewById<Toolbar>(R.id.toolbarMain)
        setSupportActionBar(toolbar)

        val viewPager2 = findViewById<ViewPager2>(R.id.view_pager2)
        val tabLayout = findViewById<TabLayout>(R.id.tabs)
        val adapter = ViewPagerAdapter(supportFragmentManager, lifecycle)
        viewPager2.adapter = adapter
        TabLayoutMediator(tabLayout, viewPager2){tab, position ->
            when(position){0->{tab.text = "Disc."}
                1->{tab.text = "Event"}
                2->{tab.text = "Chats"}
            }
        }.attach()

        unit()
        unitListener()

    }

    private lateinit var imgKey: ImageView
    private lateinit var txtKey: TextView
    private fun unit() {
        imgKey = findViewById(R.id.imgKeyMenuItem)
        txtKey = findViewById(R.id.txtNbrKeyMenuItem)
        imgKey.setOnClickListener {
            startActivity(Intent(this, PremiumActivity::class.java))
        }
        txtKey.setOnClickListener {
            startActivity(Intent(this, PremiumActivity::class.java))
        }
        if (currentUser!=null){
            txtKey.text = currentUser!!.userStatue!!.nbrKey.toString()
        }
        else if(newUserData!=null){
            txtKey.text = newUserData!!["nbrKey"].toString()
        }else{
            imgKey.isVisible = false
            txtKey.isVisible = false

            imgKey.isEnabled = false
            txtKey.isEnabled = false
        }

    }

    private fun unitListener() {
        RegisterActivity.listener = object : RegisterActivity.OnFinishRegister{
            override fun onFinishRegister() {
                finish()
            }
        }
        EditProfileActivity.listener = object : EditProfileActivity.OnFinishEdit{
            override fun onFinishEdit() {
                finish()
            }
        }
        DetailProfileActivity.listener = object : DetailProfileActivity.DetailActivityListener{
            override fun onNbrKeyChanged(newNbrKey: Int) {
                txtKey.text = newNbrKey.toString()
            }
        }
        SettingActivity.listener = object : SettingActivity.SettingListener{
            override fun onCloseApp() {
                finish()
            }
        }
        PremiumActivity.listener = object : PremiumActivity.PremiumListener{
            override fun onGivePremium() {
                finish()
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.mainmenu, menu)
        if (newUserData==null && currentUser==null){
            menu.findItem(R.id.profileItemMenu).title = "Inscription/Connexion"
            menu.findItem(R.id.signOut).isVisible = false
        }
        else if(newUserData!=null && currentUser==null){
            menu.findItem(R.id.profileItemMenu).title = "Completer Mon Profile"
        }
        else{
            menu.findItem(R.id.profileItemMenu).title = "Modifier Mon Profile"
        }

        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.profileItemMenu -> {
                if (newUserData==null && currentUser==null){
                    startActivity(Intent(this, RegisterActivity::class.java))
                }
                else if(newUserData!=null && currentUser==null){
                    EditProfileActivity.isNewUser = true
                    startActivity(Intent(this, EditProfileActivity::class.java))
                }
                else{
                    EditProfileActivity.isNewUser = false
                    startActivity(Intent(this, EditProfileActivity::class.java))
                }
                true
            }
            R.id.premium -> {
                startActivity(Intent(this, PremiumActivity::class.java))
                true
            }
            R.id.setting -> {
                startActivity(Intent(this, SettingActivity::class.java))
                true
            }
            R.id.contactUs -> {
                startActivity(Intent(this, ContactUsActivity::class.java))
                true
            }
            R.id.reportBug -> {
                startActivity(Intent(this, ReportBugActivity::class.java))
                true
            }
            R.id.signOut -> {
                AlertDialog.Builder(this)
                    .setTitle("Deconnexion")
                    .setMessage("Voulez vous vraiment vous deconnecter ?" +
                            "\n\n-Vous souvenez vous de votre mot de passe ?")
                    .setPositiveButton("Deconnexion"){_,_ ->
                        FirebaseAuth.getInstance().signOut()
                        finish()
                    }
                    .setNegativeButton("Annuler"){_,_->}
                    .create().show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}