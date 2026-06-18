package com.sxham.ticks.data

import android.content.Context
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object WidgetPreferences {
    private const val PREFS_NAME = "com.sxham.ticks.PREFERENCES"
    private const val KEY_START_DATE = "start_date_"
    private const val KEY_END_DATE = "end_date_"

    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE

    // Save dates for a specific widget instance
    fun saveWidgetData(context: Context, appWidgetId: Int, startDate: LocalDate, endDate: LocalDate) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
        prefs.putString(KEY_START_DATE + appWidgetId, startDate.format(formatter))
        prefs.putString(KEY_END_DATE + appWidgetId, endDate.format(formatter))
        prefs.apply()
    }

    // Load Start Date (Defaults to today if missing)
    fun getStartDate(context: Context, appWidgetId: Int): LocalDate {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val dateString = prefs.getString(KEY_START_DATE + appWidgetId, null)
        return if (dateString != null) LocalDate.parse(dateString, formatter) else LocalDate.now()
    }

    // Load End Date (Defaults to today + 30 days if missing)
    fun getEndDate(context: Context, appWidgetId: Int): LocalDate {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val dateString = prefs.getString(KEY_END_DATE + appWidgetId, null)
        return if (dateString != null) LocalDate.parse(dateString, formatter) else LocalDate.now().plusDays(30)
    }

    // Clean up when a widget is deleted
    fun deleteWidgetData(context: Context, appWidgetId: Int) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
        prefs.remove(KEY_START_DATE + appWidgetId)
        prefs.remove(KEY_END_DATE + appWidgetId)
        prefs.apply()
    }
}