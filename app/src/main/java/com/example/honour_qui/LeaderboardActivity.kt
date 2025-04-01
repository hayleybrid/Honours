package com.example.honour_qui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.honour_qui.databinding.ActivityLeaderboardBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.FirebaseDatabase

class LeaderboardActivity  : AppCompatActivity() {

    private lateinit var binding: ActivityLeaderboardBinding
    private lateinit var recyclerView: RecyclerView
    private val database = FirebaseDatabase.getInstance().reference

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_leaderboard)

            recyclerView = findViewById(R.id.recycler_view)
            recyclerView.layoutManager = LinearLayoutManager(this)

            getLeaderboard { scores ->
                recyclerView.adapter = LeaderboardAdapter(scores)
            }

            binding.btnLogout.setOnClickListener {
                Firebase.auth.signOut()
                val intent = Intent(this, Login::class.java)
                startActivity(intent)
                finish()
            }
            binding.btnHome.setOnClickListener {
                val signupIntent = Intent(this, MainActivity::class.java)
                startActivity(signupIntent)
            }
        }

        private fun getLeaderboard(callback: (List<Users>) -> Unit) {
            val usersRef = database.child("users")

            usersRef.orderByChild("totalScore").get()
                .addOnSuccessListener { snapshot ->
                    val scores = snapshot.children.mapNotNull { it.getValue(Users::class.java) }
                        .sortedByDescending { it.totalScore } // sort by highest score

                    callback(scores) // send sorted users to adapter
                }
                .addOnFailureListener { e ->
                    Log.e("Firebase", "Failed to fetch leaderboard", e)
                }
        }

}