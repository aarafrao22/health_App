package com.azeroth.healthapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.azeroth.healthapp.databinding.ActivityBmiBinding


class BmiActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBmiBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBmiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonSubmit.setOnClickListener {
            calculateBMI()
        }
    }

    private fun calculateBMI() {
        val heightStr = binding.editTextHeight.text.toString()
        val weightStr = binding.editTextWeight.text.toString()

        if (heightStr.isEmpty() || weightStr.isEmpty()) {
            Toast.makeText(this, "Please enter both height and weight", Toast.LENGTH_SHORT).show()
            return
        }

        val height = heightStr.toFloat() / 100 // Convert height from cm to meters
        val weight = weightStr.toFloat()

        val bmi = weight / (height * height)

        Toast.makeText(this, "Your BMI is: %.2f".format(bmi), Toast.LENGTH_SHORT).show()
        startActivity(Intent(this@BmiActivity, MainActivity::class.java))
        finish()
    }
}
