package com.sxham.ticks

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sxham.ticks.data.WidgetPreferences
import com.sxham.ticks.ui.CalendarAdapter
import com.sxham.ticks.ui.CalendarBuilder
import com.sxham.ticks.ui.ConfigActivity
import com.sxham.ticks.ui.MonthGapDecoration

class MainActivity : AppCompatActivity() {

    private var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        appWidgetId = intent.getIntExtra(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        )

        buildCalendar()

        findViewById<Button>(R.id.btn_edit_range).setOnClickListener {
            val intent = Intent(this, ConfigActivity::class.java).apply {
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            }
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        buildCalendar()
    }

    private fun buildCalendar() {
        val startDate = WidgetPreferences.getStartDate(this, appWidgetId)
        val endDate   = WidgetPreferences.getEndDate(this, appWidgetId)

        val items = CalendarBuilder.build(startDate, endDate)

        val recyclerView = findViewById<RecyclerView>(R.id.rv_calendar_grid)

        recyclerView.layoutManager = GridLayoutManager(
            this,
            CalendarBuilder.SPAN_COUNT,
            GridLayoutManager.HORIZONTAL,
            false
        )

        // Clear any previously attached decorations before re-adding
        while (recyclerView.itemDecorationCount > 0) {
            recyclerView.removeItemDecorationAt(0)
        }
        recyclerView.addItemDecoration(
            MonthGapDecoration(this, items, CalendarBuilder.SPAN_COUNT)
        )

        recyclerView.adapter = CalendarAdapter(items, startDate, endDate)
    }
}