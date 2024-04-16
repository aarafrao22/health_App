package com.azeroth.healthapp.utils

import android.content.Context

class PreferencesManager(context: Context) {

    companion object {
        private const val PREF_FILE_NAME = "MyAppPrefs"
        private const val KEY_STEPS_COUNT = "steps_count"
        private const val KEY_CALORIE = "calorie"
        private const val KEY_DISTANCE = "distance"
    }

    private val sharedPreferences =
        context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE)

    fun saveStepsCount(stepsCount: Int) {
        with(sharedPreferences.edit()) {
            putInt(KEY_STEPS_COUNT, stepsCount)
            apply()
        }
    }

    fun getStepsCount(): Int {
        return sharedPreferences.getInt(KEY_STEPS_COUNT, 0)
    }

    fun saveCalorie(calorie: Float) {
        with(sharedPreferences.edit()) {
            putFloat(KEY_CALORIE, calorie)
            apply()
        }
    }

    fun getCalorie(): Float {
        return sharedPreferences.getFloat(KEY_CALORIE, 0f)
    }

    fun saveDistance(distance: Float) {
        with(sharedPreferences.edit()) {
            putFloat(KEY_DISTANCE, distance)
            apply()
        }
    }

    fun getDistance(): Float {
        return sharedPreferences.getFloat(KEY_DISTANCE, 0f)
    }
}
