package com.example.honour_qui

import java.io.Serializable

data class TutorialModel(
    val guideId: String,
    val title: String,
    val image: String,
    val steps: List<StepsModel>,
    val quizId: String
) : Serializable
{
    constructor() : this("", "", "", emptyList(), "")
}

data class StepsModel(
    val description: String,
    val info: String,
    val imageUrl: String
) : Serializable {
    constructor() : this("", "", "")
}


