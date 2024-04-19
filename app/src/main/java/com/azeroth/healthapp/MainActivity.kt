package com.azeroth.healthapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.azeroth.healthapp.databinding.ActivityMainBinding
import com.azeroth.healthapp.utils.LocationUtils
import com.azeroth.healthapp.utils.NotificationWorker
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity(), View.OnClickListener {

    private var steps: Int = 0
    private var calorie: Double = 0.0
    private var distance: Double = 0.0

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // Initialize click listeners
        initClicks()
        val workRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
            .setInitialDelay(24, TimeUnit.HOURS) // Change this delay as needed
            .build()

        steps = retrieveFromSharedPreferences(this, "steps", 0)
        binding.txtSteps.text = steps.toString()
        calorie = LocationUtils.countCalorie()
        distance = LocationUtils.countDistance()


        binding.txtCalorie.text = String.format("%.1f", calorie)
        binding.txtDistance.text = String.format("%.1f", distance)
        binding.txtSteps.text = steps.toString()


        WorkManager.getInstance(this).enqueue(workRequest)
        getTodayData()
    }

    private fun retrieveFromSharedPreferences(
        context: Context,
        key: String,
        defaultValue: Int,
    ): Int {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getInt(key, defaultValue)
    }

    private fun getTodayData() {
        // TODO: getDataFromFirebase


    }

    private fun shareText(context: Context, text: String) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, text)
        context.startActivity(Intent.createChooser(intent, "Share via"))
    }

    override fun onResume() {
        super.onResume()


        steps = retrieveFromSharedPreferences(this, "steps", 0)
        binding.txtSteps.text = steps.toString()
        calorie = LocationUtils.countCalorie()
        distance = LocationUtils.countDistance()


        binding.txtCalorie.text = String.format("%.1f", calorie)
        binding.txtDistance.text = String.format("%.1f", distance)
        binding.txtSteps.text = steps.toString()

        val content = "Hey, check out my progress for today:\n" +
                "Steps walked: $steps\n" +
                "Calories burned: $calorie\n" +
                "Distance traveled: $distance km"

        binding.imgShare.setOnClickListener {
            shareText(this@MainActivity, content)
        }
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
