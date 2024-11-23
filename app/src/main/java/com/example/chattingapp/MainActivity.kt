package com.example.chattingapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.widget.Toast
import com.example.chattingapp.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        mAuth = FirebaseAuth.getInstance()

        onClick(binding)
    }

    private fun onClick(binding: ActivityMainBinding) {

        binding.tvCreateAccount.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
//            finish()
        }

        binding.tvForgotPassword.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.etUserName.text.toString()
            val password = binding.etPassword.text.toString()
            if(email.isNotEmpty() && password.isNotEmpty()){
                login(email, password)
            } else {
                Toast.makeText(this, "Please Enter Username and Password", Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun login(email: String, password: String) {
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val intent = Intent(this, HomeActivity::class.java)
                    finish()
                    startActivity(intent)
//                    finish()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("TAG", "signInWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext,
                        task.exception?.message.toString(),
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }

    }
}