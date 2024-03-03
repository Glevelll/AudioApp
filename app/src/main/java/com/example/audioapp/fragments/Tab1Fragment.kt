package com.example.audioapp.fragments

import android.Manifest
import android.animation.ValueAnimator
import android.content.pm.PackageManager
import android.graphics.drawable.GradientDrawable
import android.media.MediaRecorder
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.os.Environment
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Chronometer
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.room.Room
import com.example.audioapp.data.AudioRecordDao
import com.example.audioapp.data.AudioRecords
import com.example.audioapp.data.Records
import com.example.audioapp.databinding.FragmentTab1Binding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.util.Date

class Tab1Fragment : Fragment() {

    private val RECORD_AUDIO_PERMISSION_REQUEST_CODE = 123
    private lateinit var binding: FragmentTab1Binding
    private var isExpanded: Boolean = false
    private lateinit var mediaRecorder: MediaRecorder
    private var isRecording: Boolean = false
    private lateinit var chronometer: Chronometer
    private var outputFile: File? = null
    private var audioFilePath: String? = null
    private lateinit var audioRecordDao: AudioRecordDao

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Получение экземпляра базы данных и dao
        val db = Room.databaseBuilder(requireContext(), Records::class.java, "audio_records.db").build()
        audioRecordDao = db.audioRecordDao()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTab1Binding.inflate(inflater, container, false)
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.RECORD_AUDIO), RECORD_AUDIO_PERMISSION_REQUEST_CODE)
        }

        chronometer = binding.time

        binding.btnRec.setOnClickListener {
            if (!isRecording) {
                startRecording()
                startChronometer()
            } else {
                stopRecording()
                stopChronometer()
            }
            toggleButtonAnimation()
        }

        return binding.root
    }

    private fun toggleButtonAnimation() {
        val startRadius = if (isExpanded) 20f else 100f
        val endRadius = if (isExpanded) 100f else 20f

        val animator = ValueAnimator.ofFloat(startRadius, endRadius)
        animator.addUpdateListener { valueAnimator ->
            val value = valueAnimator.animatedValue as Float
            val shape = binding.btnRec.background as GradientDrawable
            shape.cornerRadius = value
        }
        animator.interpolator = AccelerateDecelerateInterpolator()
        animator.duration = 100
        animator.start()

        isExpanded = !isExpanded
    }

    private fun startRecording() {
        val recordingsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)
        audioFilePath = "${recordingsDir.absolutePath}/recording_${System.currentTimeMillis()}.wav"
        outputFile = File(audioFilePath!!)

        Log.d("Recording", "Recording file saved to: $audioFilePath") // Добавлен вывод пути в лог

        mediaRecorder = MediaRecorder()
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC)
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        mediaRecorder.setOutputFile(outputFile?.absolutePath)

        try {
            mediaRecorder.prepare()
            mediaRecorder.start()
            isRecording = true
        } catch (e: IOException) {
            Log.e("Recording", "MediaRecorder start() failed. ${e.message}")
        }
    }

    private fun stopRecording() {
        mediaRecorder.stop()
        mediaRecorder.reset()
        mediaRecorder.release()
        isRecording = false

        val currentDate = Date()
        val recordTime = SystemClock.elapsedRealtime() - chronometer.base

        val audioRecord = AudioRecords(0, currentDate, recordTime, audioFilePath ?: "")

        // Запуск корутины для выполнения операции записи на фоновом потоке
        CoroutineScope(Dispatchers.IO).launch {
            audioRecordDao.insertRecord(audioRecord)
            withContext(Dispatchers.Main) {
                Log.d("Recording", "Audio record saved to database")
            }
        }
    }

    private fun startChronometer() {
        chronometer.base = SystemClock.elapsedRealtime()
        chronometer.start()
    }

    private fun stopChronometer() {
        chronometer.stop()
        chronometer.base = SystemClock.elapsedRealtime()
    }
}