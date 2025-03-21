package com.example.honour_qui

import java.io.Serializable

data class QuizModel(
    val quizId : String,
    val title : String,
    val time : String,
    val imageUrl: String,
    val questionList: List<QuestionModel> = emptyList()
) :Serializable

{
    constructor() : this( "", "",  "","", emptyList())
}


data class QuestionModel(
    val question : String,
    val options : List<String>,
    val correct : String,
    val imageUrl: String,
) :Serializable
{
    constructor() : this("", emptyList(), "", "")
}