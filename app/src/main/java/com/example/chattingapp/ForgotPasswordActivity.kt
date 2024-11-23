package com.example.chattingapp

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.chattingapp.databinding.ActivityForgotPasswordBinding
import com.google.firebase.auth.FirebaseAuth


class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityForgotPasswordBinding
    private lateinit var mAuth: FirebaseAuth



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mAuth = FirebaseAuth.getInstance()

        click(binding)
    }

    private fun click(binding: ActivityForgotPasswordBinding) {

        binding.btnSubmit.setOnClickListener {
            if(binding.emailInput.text.toString().isEmailValid()){
                mAuth.sendPasswordResetEmail(binding.emailInput.text.toString())
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d("TAG", "Email sent.")
                            alertDialogShow()
//                            Toast.makeText(this, "Please check your email to reset the password.", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Please enter a valid email.", Toast.LENGTH_SHORT).show()
            }

        }
    }

    fun String.isEmailValid(): Boolean {
        return !TextUtils.isEmpty(this) && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
    }

    fun alertDialogShow(){
        val builder =  AlertDialog.Builder(this)
        builder.setIcon(this.getDrawable(R.drawable.chat_icon))
        builder.setMessage("Please check your email to reset the password.")
        builder.setCancelable(false)
        builder.setTitle("Message")

        builder.setPositiveButton("OK"){
            dialog, which -> finish()
        }

        val alertDialog = builder.create()
        alertDialog.show()
    }
}