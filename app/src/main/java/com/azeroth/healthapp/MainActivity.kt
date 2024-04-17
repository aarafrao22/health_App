package com.azeroth.healthapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.azeroth.healthapp.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity(), View.OnClickListener {

    private var steps: Int = 0
    private var calorie: Int = 0
    private var distance: Int = 0

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize click listeners
        initClicks()
        getTodayData()
    }


    private fun getTodayData() {
        // TODO: getDataFromFirebase

        steps = 0
        calorie = 0
        distance = 0


        val content = "Hey, check out my progress for today:\n" +
                "Steps walked: $steps\n" +
                "Calories burned: $calorie\n" +
                "Distance traveled: $distance km"

        binding.imgShare.setOnClickListener {
            shareText(this@MainActivity, content)
        }
    }


    private fun shareText(context: Context, text: String) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, text)
        context.startActivity(Intent.createChooser(intent, "Share via"))
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
                startActivity(Intent(this@MainActivity, TrackingActivity::class.java))
            }

            R.id.card_tips -> {
                // Handle click for cardTips
                startActivity(Intent(this@MainActivity, TipsActivity::class.java))
            }
        }
    }
}
