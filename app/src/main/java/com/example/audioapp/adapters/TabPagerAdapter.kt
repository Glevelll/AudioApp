package com.example.audioapp.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.audioapp.data.Records
import com.example.audioapp.fragments.Tab1Fragment
import com.example.audioapp.fragments.Tab2Fragment

class TabPagerAdapter(fragmentActivity: FragmentActivity, private val recordsDatabase: Records) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> return Tab1Fragment(recordsDatabase)
            1 -> return Tab2Fragment(recordsDatabase)
        }
        return Tab1Fragment(recordsDatabase)
    }
}