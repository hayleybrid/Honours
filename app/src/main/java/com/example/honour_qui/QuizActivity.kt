package com.example.honour_qui

import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.honour_qui.databinding.ActivityQuizBinding
import com.example.honour_qui.databinding.ScoreDialogBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch

class QuizActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityQuizBinding
    private var quizId: String? = null
    private var selectedQuiz: QuizModel? = null
    private var questionModelList: List<QuestionModel>? = null
    private var currentQuestionIndex = 0
    private var selectedAnswer = ""
    private var score = 0
    private var timer: CountDownTimer? = null
    private lateinit var firebaseData: FirebaseData
    val database: DatabaseReference = FirebaseDatabase.getInstance().reference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)

        quizId = intent.getStringExtra("quiz_id")
        if (quizId == null) {
            Log.e("QuizActivity", "Error: Quiz ID missing!")
            Toast.makeText(this, "Error loading quiz!", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        firebaseData = FirebaseData(database)

        loadQuizzesFromFirebaseData()
    }

    private fun loadQuizzesFromFirebaseData() {
        lifecycleScope.launch {
            try {
                val quizzes = firebaseData.loadQuizzes()
                selectedQuiz = quizzes.find { it.quizId == quizId }

                if (selectedQuiz == null) {
                    Log.e("QuizActivity", "Quiz not found!")
                    Toast.makeText(this@QuizActivity, "Quiz not found!", Toast.LENGTH_SHORT).show()
                    finish()
                    return@launch
                }

                questionModelList = selectedQuiz!!.questionList

                if (questionModelList.isNullOrEmpty()) {
                    Log.e("QuizActivity", "No questions found for quizId: $quizId")
                    Toast.makeText(this@QuizActivity, "No questions available!", Toast.LENGTH_SHORT).show()
                    finish()
                    return@launch
                }

                setupQuizUI()
            } catch (e: Exception) {
                Log.e("QuizActivity", "Failed to load quizzes: ${e.message}")
                Toast.makeText(this@QuizActivity, "Error loading quiz!", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun setupQuizUI() {
        binding.btn0.setOnClickListener(this)
        binding.btn1.setOnClickListener(this)
        binding.btn2.setOnClickListener(this)
        binding.btn3.setOnClickListener(this)
        binding.nextBtn.setOnClickListener(this)

        displayQuestion()
        startTimer()
    }

    private fun displayQuestion() {

        if (currentQuestionIndex >= questionModelList!!.size) {
            finishQuiz()
            return
        }

        val currentQuestion = questionModelList!![currentQuestionIndex]
        selectedAnswer = ""

        binding.questionIndicatorTextview.text =
            "Question ${currentQuestionIndex + 1} / ${questionModelList!!.size}"
        binding.questionTextview.text = currentQuestion.question
        binding.btn0.text = currentQuestion.options[0]
        binding.btn1.text = currentQuestion.options[1]
        binding.btn2.text = currentQuestion.options[2]
        binding.btn3.text = currentQuestion.options[3]

        resetButtonColors()

        val resID = resources.getIdentifier(currentQuestion.imageUrl, "drawable", packageName)

        if (resID != 0) {
            Picasso.get().load(resID).into(binding.questionImage)
        } else {
            Log.e("Image Loading", "Image resource not found: ${currentQuestion.imageUrl}")
            // Handle the case where the image isn't found (e.g., display a placeholder image)
        }
/*
        if (!currentQuestion.imageUrl.isNullOrEmpty()) {
            FirebaseStorage.getInstance().getReferenceFromUrl(currentQuestion.imageUrl)
                .downloadUrl
                .addOnSuccessListener { uri: Uri? ->
                    Picasso.get().load(uri).into(binding.questionImage)
                }
                .addOnFailureListener { e: Exception ->
                    Log.e("FirebaseStorage", "Failed to load image: " + e.message)
                }
        }*/
    }

    override fun onClick(view: View?) {
        if (view == null) return
        resetButtonColors()

        val clickedBtn = view as? android.widget.Button ?: return
        if (clickedBtn.id == R.id.next_btn) {
            if (selectedAnswer.isEmpty()) {
                Toast.makeText(this, "Please select an answer", Toast.LENGTH_SHORT).show()
                return
            }

            if (selectedAnswer == questionModelList!![currentQuestionIndex].correct) {
                score++
            }

            currentQuestionIndex++

            displayQuestion()
        } else {
            selectedAnswer = clickedBtn.text.toString()
            clickedBtn.setBackgroundColor(ContextCompat.getColor(this, R.color.Yellow))
        }
    }


    private fun resetButtonColors() {
        binding.btn0.setBackgroundColor(ContextCompat.getColor(this, R.color.Gray))
        binding.btn1.setBackgroundColor(ContextCompat.getColor(this, R.color.Gray))
        binding.btn2.setBackgroundColor(ContextCompat.getColor(this, R.color.Gray))
        binding.btn3.setBackgroundColor(ContextCompat.getColor(this, R.color.Gray))
    }

    private fun startTimer() {
        timer = object : CountDownTimer(30000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                binding.timerIndicatorTextview.text = "Time left: ${millisUntilFinished / 1000}s"
            }

            override fun onFinish() {
                Toast.makeText(this@QuizActivity, "Time's up!", Toast.LENGTH_SHORT).show()
                currentQuestionIndex++
                displayQuestion()
            }
        }.start()
    }

    private fun finishQuiz() {
        val totalQuestions = questionModelList?.size
        val percentage = ((score.toFloat() / totalQuestions!!.toFloat() ) *100 ).toInt()

        val dialogBinding = ScoreDialogBinding.inflate(layoutInflater)
        dialogBinding.apply {
            scoreProgressIndicator.progress = percentage
            scoreProgressText.text = "$percentage %"
            if (percentage > 60) {
                scoreTitle.text = "Congrats! You have passed"
                scoreTitle.setTextColor(Color.BLACK)

            } else {
                scoreTitle.text = "Oops! You have failed"
                scoreTitle.setTextColor(Color.RED)
            }
            scoreSubtitle.text = "$score out of $totalQuestions are correct"
            finishBtn.setOnClickListener {
                finish()
            }
        }

        AlertDialog.Builder(this)
            .setView(dialogBinding.root)
            .setCancelable(false)
            .show()

    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
    }
}
