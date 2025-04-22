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
        //initialise firebase instance
        database = FirebaseDatabase.getInstance().reference
        //load data
        dataLoader = FirebaseData(database)
        //authorisation
        auth = FirebaseAuth.getInstance()

        //validate user is logged in
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
        //leaderboard
        binding.btnLeaderboard.setOnClickListener{
            val intent = Intent(this, LeaderboardActivity::class.java)
            startActivity(intent)
        }
    }

    private fun redirectToLogin() {
        val intent = Intent(this, Login::class.java)
        startActivity(intent)
        finish()
    }
    //recycler view
    private fun setupRecyclerView() {
        adapter = TutorialListAdapter(mutableListOf())
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }
    //load data from firebase
    private suspend fun loadData() {
        try {
            val tutorials = dataLoader.loadTutorials()
            val quizzes = dataLoader.loadQuizzes()
            val users = dataLoader.loadUsers()

            withContext(Dispatchers.Main) {
                adapter.updateList(tutorials)
                Log.i("Firebase", "Tutorials Loaded Successfully: $tutorials")
                Log.i("Firebase", "Quizzes Loaded Successfully: $quizzes")
                Log.i("Firebase", "Users Loaded Successfully: $users")

            }
        } catch (e: Exception) {
            Log.e("Firebase", "error loading data: ${e.localizedMessage}", e)
        }
    }




}
