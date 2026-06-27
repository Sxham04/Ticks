package com.sxham.ticks.ui

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * Adds [monthGapPx] of extra left offset to every column whose row-0 item
 * is a [CalendarItem.MonthLabel]. This creates a visible breathing gap
 * between months without touching dot spacing elsewhere.
 *
 * Must be used with a HORIZONTAL GridLayoutManager with spanCount = 8.
 * Column index = position / spanCount.
 */
class MonthGapDecoration(
    context: Context,
    private val items: List<CalendarItem>,
    private val spanCount: Int = 8
) : RecyclerView.ItemDecoration() {

    // Extra gap before each new month column (in pixels)
    private val monthGapPx = (context.resources.displayMetrics.density * 6).toInt()

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        if (position == RecyclerView.NO_ID.toInt()) return

        // Which column does this item belong to?
        val column = position / spanCount

        // Row-0 item of this column is at index: column * spanCount
        val columnHeaderIndex = column * spanCount
        if (columnHeaderIndex >= items.size) return

        // If the header of this column is a MonthLabel, push the whole column right
        if (items[columnHeaderIndex] is CalendarItem.MonthLabel) {
            // Only apply to the first column (no gap before the very first month)
            if (column > 0) {
                outRect.left = monthGapPx
            }
        }
    }
}