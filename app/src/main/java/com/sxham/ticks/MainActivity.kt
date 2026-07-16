package com.sxham.ticks

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.View
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

    // Track scroll listeners so we can remove them before re-adding on onResume
    private var gridScrollListener: RecyclerView.OnScrollListener? = null
    private var labelScrollListener: RecyclerView.OnScrollListener? = null

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

        val rvLabels = findViewById<RecyclerView>(R.id.rv_month_labels)
        val rvGrid   = findViewById<RecyclerView>(R.id.rv_calendar_grid)

        // ── Remove stale scroll listeners before rebuilding ──────────────────
        gridScrollListener?.let  { rvGrid.removeOnScrollListener(it) }
        labelScrollListener?.let { rvLabels.removeOnScrollListener(it) }

        // ── Month label row ──────────────────────────────────────────────────
        rvLabels.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvLabels.setHasFixedSize(true)
        rvLabels.adapter = MonthLabelAdapter(columns)

        // ── Dot grid ─────────────────────────────────────────────────────────
        rvGrid.layoutManager = GridLayoutManager(
            this, CalendarBuilder.SPAN_COUNT, GridLayoutManager.HORIZONTAL, false
        ).also { lm ->
            // Pre-fetch more columns so fast flings don't stutter
            lm.initialPrefetchItemCount = CalendarBuilder.SPAN_COUNT * 6
        }
        rvGrid.setHasFixedSize(true)
        rvGrid.adapter = CalendarAdapter(dots, startDate, endDate)

        // ── Month gap decoration ─────────────────────────────────────────────
        // Clear old decorations first
        while (rvGrid.itemDecorationCount > 0) rvGrid.removeItemDecorationAt(0)
        while (rvLabels.itemDecorationCount > 0) rvLabels.removeItemDecorationAt(0)

        val monthGapPx = (resources.displayMetrics.density * 8).toInt()

        // On the dot grid: add left margin to every item in a month-boundary column
        rvGrid.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                val pos    = parent.getChildAdapterPosition(view)
                if (pos == RecyclerView.NO_ID.toInt()) return
                val col    = pos / CalendarBuilder.SPAN_COUNT   // which week column
                // Skip gap on first column
                if (col > 0 && columns[col].monthLabel != null) {
                    outRect.left = monthGapPx
                }
            }
        })

        // On the label row: same gap so labels stay aligned with dot columns
        rvLabels.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                val pos = parent.getChildAdapterPosition(view)
                if (pos == RecyclerView.NO_ID.toInt()) return
                if (pos > 0 && columns[pos].monthLabel != null) {
                    outRect.left = monthGapPx
                }
            }
        })

        // ── Sync scroll ───────────────────────────────────────────────────────
        var isSyncingGrid  = false
        var isSyncingLabel = false

        gridScrollListener = object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (isSyncingGrid) return
                isSyncingLabel = true
                rvLabels.scrollBy(dx, 0)
                isSyncingLabel = false
            }
        }
        labelScrollListener = object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (isSyncingLabel) return
                isSyncingGrid = true
                rvGrid.scrollBy(dx, 0)
                isSyncingGrid = false
            }
        }

        rvGrid.addOnScrollListener(gridScrollListener!!)
        rvLabels.addOnScrollListener(labelScrollListener!!)
    }
}