package com.lnd.RencontreAfricaine.utils

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.lnd.RencontreAfricaine.ui.main.DiscoverFragment
import com.lnd.RencontreAfricaine.ui.main.DiscussionFragment
import com.lnd.RencontreAfricaine.ui.main.EventFragment

class ViewPagerAdapter(private val fragmentManager: FragmentManager, private val lifeCycle: Lifecycle) : FragmentStateAdapter(fragmentManager, lifeCycle) {
    override fun getItemCount(): Int = 3
    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0-> {
                DiscoverFragment()
            }
            1-> {
                EventFragment()
            }
            2-> {
                DiscussionFragment()
            }
            else-> {Fragment()}
        }
    }
}