package com.example.honour_qui

import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class FirebaseData (private val database: DatabaseReference) {

    suspend fun loadTutorials(): List<TutorialModel> {
        return withContext(Dispatchers.IO) {
            val snapshot = database.child("tutorials").get().await()
            val tutorials = mutableListOf<TutorialModel>()
            for (child in snapshot.children) {
                val tutorialModel = child.getValue(TutorialModel::class.java)
                tutorialModel?.let { tutorials.add(it) }
            }
            return@withContext tutorials
        }
    }

    suspend fun loadQuizzes(): List<QuizModel> {
        return withContext(Dispatchers.IO) {
            val snapshot = database.child("quizzes").get().await()
            val quizzes = mutableListOf<QuizModel>()
            for (child in snapshot.children) {
                val quizModel = child.getValue(QuizModel::class.java)
                quizModel?.let { quizzes.add(it) }
            }
            return@withContext quizzes
        }
    }



}