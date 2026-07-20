package com.sxham.ticks.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sxham.ticks.R
import java.time.LocalDate

// Pure dot grid — no labels mixed in
class CalendarAdapter(
    private val dots: List<LocalDate?>,
    private val startDate: LocalDate,
    private val endDate: LocalDate
) : RecyclerView.Adapter<CalendarAdapter.DayViewHolder>() {

    class DayViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val dotView: View = view.findViewById(R.id.view_dot)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        DayViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_day_dot, parent, false))

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        val date = dots[position]
        if (date == null) {
            holder.dotView.visibility = View.INVISIBLE
            return
        }

        holder.dotView.visibility = View.VISIBLE
        val today = LocalDate.now()

        holder.dotView.setBackgroundResource(when {
            date.isBefore(startDate) || date.isAfter(endDate) -> R.drawable.dot_hollow
            date == today                                      -> R.drawable.dot_today
            !date.isAfter(today)                              -> R.drawable.dot_complete
            else                                              -> R.drawable.dot_incomplete
        })
    }

    override fun getItemCount() = dots.size
}