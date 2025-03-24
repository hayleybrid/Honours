package com.example.honour_qui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.honour_qui.databinding.ActivityTutorialBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.squareup.picasso.Picasso


class TutorialActivity : AppCompatActivity(), View.OnClickListener {

    companion object {
        var tutorialSteps: List<StepsModel> = listOf()
    }

    private var currentStepIndex = 0

    private lateinit var stepDescription: TextView
    private lateinit var stepInfo: TextView
    private lateinit var stepImage: ImageView
    private lateinit var btnNext: Button
    private lateinit var btnBack: Button
    private lateinit var btnQuiz: Button
    private lateinit var binding: ActivityTutorialBinding
    private lateinit var tutorialModel: TutorialModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTutorialBinding.inflate(layoutInflater)
        setContentView(binding.root)

        stepDescription = findViewById(R.id.description_textview)
        stepInfo = findViewById(R.id.info_textview)
        stepImage = findViewById(R.id.tutorialImage)
        btnNext = findViewById(R.id.btnNxt)
        btnBack = findViewById(R.id.btnBack)
        btnQuiz = findViewById(R.id.btnQuiz)

        btnNext.setOnClickListener(this)
        btnBack.setOnClickListener(this)
        btnQuiz.setOnClickListener(this)


        tutorialModel = intent.getSerializableExtra("tutorial_data") as? TutorialModel ?: run {
            Log.e("TutorialActivity", "Error: Tutorial data missing!")
            finish()
            return
        }

        Log.d("TutorialActivity", "Received tutorial: $tutorialModel")

        tutorialSteps = tutorialModel.steps

        if (tutorialSteps.isEmpty()) {
            Log.e("TutorialActivity", "Error: No tutorial steps found!")
            finish()
            return
        }

        loadTutorial()

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

    private fun loadTutorial() {
        if (currentStepIndex >= tutorialSteps.size) {
            Log.d("TutorialActivity", "Tutorial finished! Redirecting to QuizActivity...")
            startQuizActivity()
            return
        }

        val currentStep = tutorialSteps[currentStepIndex]
        Log.d("TutorialActivity", "Displaying step: ${currentStepIndex + 1} / ${tutorialSteps.size}")

        stepDescription.text = currentStep.description
        stepInfo.text = currentStep.info


        /*
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.getReferenceFromUrl(currentStep.imageUrl)

        storageRef.downloadUrl.addOnSuccessListener { uri ->
            Picasso.get().load(uri).into(stepImage)
        }.addOnFailureListener {
            Log.e("FirebaseStorage", "Failed to get image URL: ${it.message}")
        }
*/
        val resID = resources.getIdentifier(currentStep.imageUrl, "drawable", packageName)

        if (resID != 0) {
            Picasso.get().load(resID).into(stepImage)
        } else {
            Log.e("Image Loading", "Image resource not found: ${currentStep.imageUrl}")
            // Handle the case where the image isn't found (e.g., display a placeholder image)
        }

        btnBack.visibility = if (currentStepIndex == 0) View.GONE else View.VISIBLE
        btnNext.visibility = if (currentStepIndex == tutorialSteps.size - 1) View.GONE else View.VISIBLE
        btnQuiz.visibility = if (currentStepIndex == tutorialSteps.size - 1) View.VISIBLE else View.GONE
    }


    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btnNxt -> {
                if (currentStepIndex < tutorialSteps.size - 1) {
                    currentStepIndex++
                    loadTutorial()
                }
            }
            R.id.btnBack -> {
                if (currentStepIndex > 0) {
                    currentStepIndex--
                    loadTutorial()
                }
            }
            R.id.btnQuiz -> {
                startQuizActivity()
            }
        }
    }


    private fun startQuizActivity() {


        val intent = Intent(this, QuizActivity::class.java).apply {
            putExtra("quiz_id", tutorialModel.quizId)
        }

        startActivity(intent)
        finish()


    }
}
