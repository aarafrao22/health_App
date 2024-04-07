package com.azeroth.healthapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.azeroth.healthapp.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize click listeners
        initClicks()
    }

    private fun initClicks() {
        // Set click listeners for each card view
        binding.cardAct.setOnClickListener(this)
        binding.cardCalorie.setOnClickListener(this)
        binding.cardDistance.setOnClickListener(this)
        binding.cardSteps.setOnClickListener(this)
        binding.cardTips.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            // Handle click events for each card view
            R.id.card_act -> {
                // Handle click for cardAct
            }

            R.id.card_calorie -> {
                // Handle click for cardCalorie
            }

            R.id.card_distance -> {
                // Handle click for cardDistance
            }

            R.id.card_steps -> {
                // Handle click for cardSteps
            }

            R.id.card_tips -> {
                // Handle click for cardTips
                startActivity(Intent(this@MainActivity, TipsActivity::class.java))
            }
        }
    }
}
