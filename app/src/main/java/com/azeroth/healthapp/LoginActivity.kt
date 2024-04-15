package com.azeroth.healthapp

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.azeroth.healthapp.databinding.ActivityLoginBinding
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private var edEmail: TextInputEditText? = null
    private var edPassword: TextInputEditText? = null
    private var btnSignIn: Button? = null
    private var firebaseAuth: FirebaseAuth? = null
    private var forgotPassword: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()

        binding.edEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                checkInputs()
            }

            override fun afterTextChanged(s: Editable) {}
        })
        binding.edContact.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                checkInputs()
            }

            override fun afterTextChanged(s: Editable) {}
        })
//        forgotPassword!!.setOnClickListener { }
        binding.btnLogin.setOnClickListener { checkEmailAndPassword() }
    }


    private fun sendToMainActivity(s: String) {
        startActivity(Intent(this@LoginActivity, MainActivity::class.java).putExtra("name", s))
        finish()
    }

    private fun checkEmailAndPassword() {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+.[a-z]+"
        if (binding.edEmail.getText().toString().matches(emailPattern.toRegex())) {
            if (binding.edContact.length() >= 8) {
                disable()
                firebaseAuth?.signInWithEmailAndPassword(
                    binding.edEmail.getText().toString(),
                    binding.edContact.getText().toString()
                )
                    ?.addOnCompleteListener { task ->
                        val user: FirebaseUser? = task.result.user
                        if (task.isSuccessful) {
                            if (user != null) {
                                user.displayName?.let { sendToMainActivity(it) }
                            }
                        } else {
                            disable()
                            Toast.makeText(this@LoginActivity, "error", Toast.LENGTH_LONG).show()
                        }
                    }
            } else {
                Toast.makeText(
                    this@LoginActivity,
                    "Incorrect Email or Password",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun enable() {
        btnSignIn!!.setEnabled(true)
        btnSignIn!!.setTextColor(Color.rgb(255, 255, 255))
    }

    private fun disable() {
        btnSignIn!!.setEnabled(false)
        btnSignIn!!.setTextColor(Color.argb(50, 255, 255, 255))
    }

    private fun checkInputs() {
        if (!TextUtils.isEmpty(edEmail!!.getText())) {
            if (!TextUtils.isEmpty(edPassword!!.getText())) {
                enable()
            } else {
                disable()
            }
        } else {
            disable()
        }
    }

    private fun initViews() {
        firebaseAuth = FirebaseAuth.getInstance()

    }


}