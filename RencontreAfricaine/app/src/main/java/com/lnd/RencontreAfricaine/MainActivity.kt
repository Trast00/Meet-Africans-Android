package com.lnd.RencontreAfricaine

import android.os.Bundle
import com.google.android.material.tabs.TabLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
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