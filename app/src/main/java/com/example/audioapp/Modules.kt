package com.example.audioapp

import androidx.room.Room
import com.example.audioapp.data.Records
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val appModule = module {
    single { Room.databaseBuilder(androidContext(), Records::class.java, "audio_records").build() }
    single { get<Records>().audioRecordDao() }
}