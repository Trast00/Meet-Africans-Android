package com.lnd.RencontreAfricaine.ui.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(private val fragmentManager: FragmentManager, private val lifeCycle: Lifecycle) : FragmentStateAdapter(fragmentManager, lifeCycle) {
    override fun getItemCount(): Int = 3
    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0-> {DiscoverFragment()}
            1-> {EventFragment()}
            2-> {DiscussionFragment()}
            else-> {Fragment()}
        }
    }
}