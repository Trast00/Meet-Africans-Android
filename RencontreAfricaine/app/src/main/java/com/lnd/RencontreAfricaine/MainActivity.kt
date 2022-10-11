package com.lnd.RencontreAfricaine

import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.lnd.RencontreAfricaine.databinding.ActivityMainBinding

import com.lnd.RencontreAfricaine.ui.main.ViewPagerAdapter

class MainActivity : AppCompatActivity() {
    companion object {
        val newUserData: HashMap<String, Any>? = null //id, phone, mdp, key
        val currentUser: Users? = null
    }

    //Comment
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewPager2 = findViewById<ViewPager2>(R.id.view_pager2)
        val tabLayout = findViewById<TabLayout>(R.id.tabs)
        val adapter = ViewPagerAdapter(supportFragmentManager, lifecycle)
        viewPager2.adapter = adapter
        TabLayoutMediator(tabLayout, viewPager2){tab, position ->
            when(position){
                0->{tab.text = "Disc."}
                1->{tab.text = "Event"}
                2->{tab.text = "Chats"}
            }
        }.attach()

    }
}