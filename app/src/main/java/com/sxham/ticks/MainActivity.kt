package com.sxham.ticks

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sxham.ticks.data.WidgetPreferences
import com.sxham.ticks.ui.CalendarAdapter
import com.sxham.ticks.ui.CalendarBuilder
import com.sxham.ticks.ui.ConfigActivity
import com.sxham.ticks.ui.MonthLabelAdapter

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
        val columns   = CalendarBuilder.build(startDate, endDate)
        val dots      = CalendarBuilder.buildDotItems(columns)

        // ── Month label row (one LinearLayoutManager item per week column) ──
        val rvLabels = findViewById<RecyclerView>(R.id.rv_month_labels)
        val labelLM  = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvLabels.layoutManager = labelLM
        rvLabels.adapter = MonthLabelAdapter(columns)

        // ── Dot grid (7 rows × N columns, horizontal scroll) ────────────────
        val rvGrid = findViewById<RecyclerView>(R.id.rv_calendar_grid)
        val gridLM = GridLayoutManager(this, CalendarBuilder.SPAN_COUNT, GridLayoutManager.HORIZONTAL, false)
        rvGrid.layoutManager = gridLM
        rvGrid.adapter = CalendarAdapter(dots, startDate, endDate)

        // ── Sync scroll: dragging either RecyclerView scrolls both ───────────
        rvGrid.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                rvLabels.scrollBy(dx, 0)
            }
        })
        rvLabels.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                rvGrid.scrollBy(dx, 0)
            }
        })
    }
}