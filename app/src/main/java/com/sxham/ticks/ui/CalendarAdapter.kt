package com.sxham.ticks.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sxham.ticks.R
import java.time.LocalDate

// ── Data model ────────────────────────────────────────────────────────────────

sealed class CalendarItem {
    /** date = null means an invisible padding cell */
    data class DayCell(val date: LocalDate?) : CalendarItem()
    data class MonthLabel(val label: String) : CalendarItem()
}

// ── Adapter ───────────────────────────────────────────────────────────────────

class CalendarAdapter(
    private val items: List<CalendarItem>,
    private val startDate: LocalDate,
    private val endDate: LocalDate
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_DOT   = 0
        private const val TYPE_LABEL = 1
    }

    // ViewHolders

    class DayViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val dotView: View = view.findViewById(R.id.view_dot)
    }

    class LabelViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.tv_month_label)
    }

    // Adapter overrides

    override fun getItemViewType(position: Int) = when (items[position]) {
        is CalendarItem.MonthLabel -> TYPE_LABEL
        is CalendarItem.DayCell    -> TYPE_DOT
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_LABEL -> LabelViewHolder(
                inflater.inflate(R.layout.item_month_label, parent, false)
            )
            else -> DayViewHolder(
                inflater.inflate(R.layout.item_day_dot, parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {

            is CalendarItem.MonthLabel -> {
                (holder as LabelViewHolder).textView.text = item.label
            }

            is CalendarItem.DayCell -> {
                val dotView = (holder as DayViewHolder).dotView
                val date    = item.date

                if (date == null) {
                    // Invisible padding — takes up grid space but shows nothing
                    dotView.visibility = View.INVISIBLE
                    return
                }

                dotView.visibility = View.VISIBLE
                val today = LocalDate.now()

                when {
                    // Outside user's active date range → hollow ring
                    date.isBefore(startDate) || date.isAfter(endDate) ->
                        dotView.setBackgroundResource(R.drawable.dot_hollow)

                    // Inside range, today or past → bright solid dot
                    !date.isAfter(today) ->
                        dotView.setBackgroundResource(R.drawable.dot_complete)

                    // Inside range, future → dim dot
                    else ->
                        dotView.setBackgroundResource(R.drawable.dot_incomplete)
                }
            }
        }
    }

    override fun getItemCount() = items.size
}