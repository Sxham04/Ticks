package com.sxham.ticks.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sxham.ticks.R

// One item per week column — shows month name or empty
class MonthLabelAdapter(
    private val columns: List<WeekColumn>
) : RecyclerView.Adapter<MonthLabelAdapter.LabelViewHolder>() {

    class LabelViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.tv_month_label)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        LabelViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_month_label, parent, false))

    override fun onBindViewHolder(holder: LabelViewHolder, position: Int) {
        holder.textView.text = columns[position].monthLabel ?: ""
    }

    override fun getItemCount() = columns.size
}