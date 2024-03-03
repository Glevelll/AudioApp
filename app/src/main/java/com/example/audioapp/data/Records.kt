package com.example.audioapp.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [AudioRecords::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class Records : RoomDatabase() {
    abstract fun audioRecordDao(): AudioRecordDao
}