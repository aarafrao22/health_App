package com.azeroth.healthapp.utils

import android.content.Context
import android.content.SharedPreferences
import java.util.Calendar

object DailyDataManager {

    private const val PREF_NAME = "daily_data_pref"
    private const val KEY_PREFIX = "daily_data_"

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun saveValueForToday(context: Context, value: Int) {
        val currentDay = getDayName()
        val editor = getSharedPreferences(context).edit()
        val key = KEY_PREFIX + currentDay
        editor.putInt(key, value)
        editor.apply()
    }

    fun getValueForToday(context: Context, defaultValue: Int): Int {
        val currentDay = getDayName()
        val key = KEY_PREFIX + currentDay
        val sharedPreferences = getSharedPreferences(context)
        return sharedPreferences.getInt(key, defaultValue)
    }

    private fun getDayName(): String {
        val calendar = Calendar.getInstance()
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        return when (dayOfWeek) {
            Calendar.SUNDAY -> "Sunday"
            Calendar.MONDAY -> "Monday"
            Calendar.TUESDAY -> "Tuesday"
            Calendar.WEDNESDAY -> "Wednesday"
            Calendar.THURSDAY -> "Thursday"
            Calendar.FRIDAY -> "Friday"
            Calendar.SATURDAY -> "Saturday"
            else -> throw IllegalStateException("Invalid day of week")
        }
    }

    fun getValuesForLastNDays(context: Context, numberOfDays: Int): List<Pair<String, Int>> {
        val values = mutableListOf<Pair<String, Int>>()
        val sharedPreferences = getSharedPreferences(context)

        for (i in 0 until numberOfDays) {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_YEAR, -i)
            val dayName = getDayName(calendar)
            val key = KEY_PREFIX + dayName
            val value = sharedPreferences.getInt(key, 0)
            values.add(dayName to value)
        }

        return values
    }

    private fun getDayName(calendar: Calendar): String {
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        return when (dayOfWeek) {
            Calendar.SUNDAY -> "Sunday"
            Calendar.MONDAY -> "Monday"
            Calendar.TUESDAY -> "Tuesday"
            Calendar.WEDNESDAY -> "Wednesday"
            Calendar.THURSDAY -> "Thursday"
            Calendar.FRIDAY -> "Friday"
            Calendar.SATURDAY -> "Saturday"
            else -> throw IllegalStateException("Invalid day of week")
        }
    }
}
