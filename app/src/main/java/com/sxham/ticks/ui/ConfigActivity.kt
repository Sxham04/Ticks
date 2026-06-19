package com.sxham.ticks.ui

import android.app.Activity
import android.app.DatePickerDialog
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.sxham.ticks.R
import com.sxham.ticks.data.WidgetPreferences
import com.sxham.ticks.widget.TicksWidgetProvider
import java.time.LocalDate
import java.util.Calendar

class ConfigActivity : AppCompatActivity() {

    private var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID

    private var startDate: LocalDate? = null
    private var endDate: LocalDate? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Set the result to CANCELED. If the user backs out early, Android won't place a broken widget.
        setResult(Activity.RESULT_CANCELED)

        // 2. Point to the setup layout we built earlier
        setContentView(R.layout.activity_config)

        // 3. Extract the unique Widget ID passed by the Android OS
        val extras = intent.extras
        if (extras != null) {
            appWidgetId = extras.getInt(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID
            )
        }

        // If we don't get a valid ID, shut down immediately
        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }

        // 4. Reference our layout buttons
        val btnStart = findViewById<Button>(R.id.btn_start_date)
        val btnEnd = findViewById<Button>(R.id.btn_end_date)
        val btnActivate = findViewById<Button>(R.id.btn_add_widget)

        // 5. Handle Start Date click
        btnStart.setOnClickListener {
            showDatePicker { chosenDate ->
                startDate = chosenDate
                btnStart.text = "Starts: $chosenDate"
            }
        }

        // 6. Handle End Date click
        btnEnd.setOnClickListener {
            showDatePicker { chosenDate ->
                endDate = chosenDate
                btnEnd.text = "Finishes: $chosenDate"
            }
        }

        // 7. Handle Activation click
        btnActivate.setOnClickListener {
            val start = startDate
            val end = endDate

            if (start == null || end == null) {
                Toast.makeText(this, "Please select both dates first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (end.isBefore(start) || end.isEqual(start)) {
                Toast.makeText(this, "Finish date must be after start date", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val appWidgetManager = AppWidgetManager.getInstance(this)
            TicksWidgetProvider.updateAppWidget(this, appWidgetManager, appWidgetId)

            // Save our data to preferences
            WidgetPreferences.saveWidgetData(this, appWidgetId, start, end)

            // Notify the system that the widget is configured and ready to build
            val resultValue = Intent().apply {
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            }
            setResult(Activity.RESULT_OK, resultValue)
            finish()
        }
    }

    // Helper function to pop up the native Android calendar dialog
    private fun showDatePicker(onDateSelected: (LocalDate) -> Unit) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            // Note: DatePickerDialog months are 0-indexed (Jan = 0), LocalDate is 1-indexed (Jan = 1)
            val date = LocalDate.of(selectedYear, selectedMonth + 1, selectedDay)
            onDateSelected(date)
        }, year, month, day).show()
    }
}