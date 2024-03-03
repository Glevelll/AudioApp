package com.example.audioapp.fragments

import AudioRecordsAdapter
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.audioapp.R
import com.example.audioapp.data.AudioRecordDao
import com.example.audioapp.data.AudioRecords
import com.example.audioapp.data.Records
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Tab2Fragment(private val recordsDatabase: Records) : Fragment() {

    private lateinit var audioDao: AudioRecordDao
    private lateinit var recordsList: MutableList<AudioRecords>
    private lateinit var adapter: AudioRecordsAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_tab2, container, false)

        audioDao = recordsDatabase.audioRecordDao()

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        recordsList = mutableListOf()
        adapter = AudioRecordsAdapter(recordsList)
        recyclerView.adapter = adapter

        return view
    }

    override fun onResume() {
        super.onResume()
        CoroutineScope(Dispatchers.IO).launch {
            val newRecordsList = audioDao.getAllRecords()
            withContext(Dispatchers.Main) {
                recordsList.clear()
                recordsList.addAll(newRecordsList)
                adapter.notifyDataSetChanged()
            }
        }
    }
}