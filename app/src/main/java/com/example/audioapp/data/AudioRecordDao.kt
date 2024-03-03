package com.example.audioapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface AudioRecordDao {
    @Query("SELECT * FROM audio_records")
    fun getAllRecords(): List<AudioRecords>

    @Query("SELECT * FROM audio_records WHERE audio_id = :audioId")
    fun getRecordById(audioId: Int): AudioRecords

    @Insert
    fun insertRecord(audioRecord: AudioRecords)
}