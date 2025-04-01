package com.example.honour_qui

import android.util.Log
import com.google.firebase.database.FirebaseDatabase

class Scores(val username: String, val score: Int)

fun submitScore(username: String, score: Int) {
    val database = FirebaseDatabase.getInstance().reference
    val leaderboardRef = database.child("leaderboard")

    val newEntry = leaderboardRef.push()
    val scoreEntry = Scores(username, score)

    newEntry.setValue(scoreEntry)
        .addOnSuccessListener {
            Log.d("Firebase", "Score submitted successfully!")
        }
        .addOnFailureListener { e ->
            Log.e("Firebase", "Failed to submit score", e)
        }
}
