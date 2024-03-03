package com.example.audioapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import androidx.viewpager2.widget.ViewPager2
import com.example.audioapp.adapters.TabPagerAdapter
import com.example.audioapp.data.AudioRecordDao
import com.example.audioapp.data.Records
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {

    private lateinit var recordsDatabase: Records
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recordsDatabase = Room.databaseBuilder(applicationContext, Records::class.java, "audio_records").build()

        val viewPager = findViewById<ViewPager2>(R.id.viewPager)
        viewPager.adapter = TabPagerAdapter(this, recordsDatabase)

        val tabLayout = findViewById<TabLayout>(R.id.tabLayout)

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "Запись"
                1 -> tab.text = "Записи"
            }
        }.attach()
    }
}