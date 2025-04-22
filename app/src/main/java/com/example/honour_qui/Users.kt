package com.example.honour_qui

import java.io.Serializable
//users data
data class Users(
    val userId: String,
    val name: String,
    val email: String,
    val password: String,
    var totalScore: Int
) : Serializable
{
    constructor() : this("", "","", "", 0)
}


