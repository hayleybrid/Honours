package com.example.honour_qui

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.honour_qui.databinding.ActivityMainBinding
import com.google.firebase.database.*
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: TutorialListAdapter
    private lateinit var database: DatabaseReference
    private lateinit var dataLoader: FirebaseData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance().reference
        dataLoader = FirebaseData(database)

        setupRecyclerView()

        lifecycleScope.launch {
            loadData()
        }
    }

    private fun setupRecyclerView() {
        adapter = TutorialListAdapter(mutableListOf())
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }

    private suspend fun loadData() {
        try {
            val tutorials = dataLoader.loadTutorials()
            val quizzes = dataLoader.loadQuizzes()

            withContext(Dispatchers.Main) {
                adapter.updateList(tutorials)
                Log.i("firebase", "Tutorials Loaded Successfully: $tutorials")
                Log.i("firebase", "Quizzes Loaded Successfully: $quizzes")
            }
        } catch (e: Exception) {
            Log.e("firebase", "Error loading data: ${e.localizedMessage}", e)
        }
    }
}
