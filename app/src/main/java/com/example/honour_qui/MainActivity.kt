package com.example.honour_qui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.honour_qui.databinding.ActivityMainBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.*
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: TutorialListAdapter
    private lateinit var database: DatabaseReference
    private lateinit var dataLoader: FirebaseData
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance().reference
        dataLoader = FirebaseData(database)
        auth = FirebaseAuth.getInstance()

        if (auth.currentUser == null) {
            redirectToLogin()
        } else {
            setupRecyclerView()
            lifecycleScope.launch {
                loadData()
            }
        }

        binding.btnLogout.setOnClickListener {
            Firebase.auth.signOut()
            redirectToLogin()
        }
    }

    private fun redirectToLogin() {
        val intent = Intent(this, Login::class.java)
        startActivity(intent)
        finish()
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
            val users = dataLoader.loadUsers()

            withContext(Dispatchers.Main) {
                adapter.updateList(tutorials)
                Log.i("firebase", "Tutorials Loaded Successfully: $tutorials")
                Log.i("firebase", "Quizzes Loaded Successfully: $quizzes")
                Log.i("firebase", "Users Loaded Successfully: $users")

            }
        } catch (e: Exception) {
            Log.e("firebase", "Error loading data: ${e.localizedMessage}", e)
        }
    }




}
