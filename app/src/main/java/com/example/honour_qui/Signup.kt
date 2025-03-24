package com.example.honour_qui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.honour_qui.databinding.ActivitySignupBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class Signup : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference("Users")  // 🔹 Reference to "Users"

        binding.btnHome.setOnClickListener {
            val signupIntent = Intent(this, MainActivity::class.java)
            startActivity(signupIntent)
        }

        binding.btnSignUp.setOnClickListener {
            val name = binding.nameEdit.text.toString().trim()
            val email = binding.emailEdit.text.toString().trim()  // Treating email as username
            val password = binding.passwordEdit.text.toString().trim()
            val confirmPassword = binding.confirmPasswordEdit.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()) {
                if (password == confirmPassword) {
                    // Create a new user with Firebase Authentication
                    auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val userId = auth.currentUser?.uid ?: ""
                            val username = email // You can replace this with a separate input for username if needed

                            // Create a user object to store in Firebase Realtime Database
                            val newUser = Users(userId, name, email, password)

                            // Save the user in Firebase Database (save only the user info, not password)
                            database.child(userId).setValue(newUser).addOnCompleteListener { dbTask ->
                                if (dbTask.isSuccessful) {
                                    Toast.makeText(this, "Signup Successful", Toast.LENGTH_SHORT).show()
                                    startActivity(Intent(this, Login::class.java))
                                    finish()  // Finish current activity so user can't go back to signup
                                } else {
                                    Toast.makeText(this, "Database Error: ${dbTask.exception?.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            // Handle Firebase Authentication error
                            val exception = task.exception
                            if (exception is com.google.firebase.auth.FirebaseAuthUserCollisionException) {
                                // The email address is already registered
                                Toast.makeText(this, "Email is already registered. Please log in.", Toast.LENGTH_SHORT).show()
                            } else {
                                // Other error during sign-up
                                Toast.makeText(this, "Authentication Error: ${exception?.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                } else {
                    // Password mismatch error
                    Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                }
            } else {
                // Validation for empty fields
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
