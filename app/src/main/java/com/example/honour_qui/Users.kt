package com.example.honour_qui

import java.io.Serializable

data class Users(
    val userId: String,
    val name: String,
    val email: String,
    val password: String,
    //val createdAt: Date,
 //   val quizzesScores: Map<String, Int> = mapOf(),  // map to store quiz ID and score
   // val overallScore: Int = 0  //total score
) : Serializable
{
    constructor() : this("", "","", "")//, mapOf(), 0, Date()
}
    // calculate total score
 //   fun getTotalScore(): Int {
   //     return quizzesScores.values.sum()  // Sum of all quiz scores
    //}

