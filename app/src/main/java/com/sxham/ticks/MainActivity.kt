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
import com.sxham.ticks.ui.CalendarItem
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
        val items     = CalendarBuilder.build(startDate, endDate)

        val recyclerView = findViewById<RecyclerView>(R.id.rv_calendar_grid)

        val spanCount = CalendarBuilder.SPAN_COUNT

        val layoutManager = GridLayoutManager(this, spanCount, GridLayoutManager.HORIZONTAL, false)

        // Month labels span 3 columns so the text has room to render fully.
        // All other cells span 1 column as normal.
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (items[position] is CalendarItem.MonthLabel) 3 else 1
            }
        }

        while (recyclerView.itemDecorationCount > 0) {
            recyclerView.removeItemDecorationAt(0)
        }
        recyclerView.addItemDecoration(MonthGapDecoration(this, items, spanCount))

        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = CalendarAdapter(items, startDate, endDate)
    }
}