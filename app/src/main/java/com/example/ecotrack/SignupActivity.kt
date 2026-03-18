package com.jashan.ecotrack

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import models.users

class SignupActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        val etName = findViewById<EditText>(R.id.etName)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnSignup = findViewById<Button>(R.id.btnSignup)
        val progress = findViewById<ProgressBar>(R.id.signupProgress)

        btnSignup.setOnClickListener {

            val name = etName.text.toString()
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            progress.visibility = View.VISIBLE

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener {

                    if (it.isSuccessful) {

                        val user = auth.currentUser!!
                        val userId = user.uid

                        val userData = users(name, email)

                        database.reference.child("Users")
                            .child(userId)
                            .setValue(userData)

                        // 🔥 SEND VERIFICATION EMAIL
                        user.sendEmailVerification()
                            .addOnCompleteListener { verifyTask ->

                                progress.visibility = View.GONE

                                if (verifyTask.isSuccessful) {

                                    Toast.makeText(
                                        this,
                                        "Verification email sent. Check your inbox.",
                                        Toast.LENGTH_LONG
                                    ).show()

                                    auth.signOut()

                                    startActivity(Intent(this, LoginActivity::class.java))
                                    finish()

                                } else {
                                    Toast.makeText(
                                        this,
                                        "Failed to send verification email.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }

                    } else {
                        progress.visibility = View.GONE
                        Toast.makeText(this, "Signup Failed", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}