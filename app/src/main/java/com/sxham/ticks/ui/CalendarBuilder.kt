package com.sxham.ticks.ui

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

/**
 * Builds the flat [CalendarItem] list for [CalendarAdapter].
 *
 * Grid: spanCount = 8, orientation = HORIZONTAL
 *   Row 0          → month label (or invisible pad)
 *   Rows 1–7       → Monday … Sunday
 *
 * We iterate week-by-week (Mon→Sun). For each week column we emit 8 items:
 *   slot 0: MonthLabel if Monday of this column is the 1st of a month, else DayCell(null)
 *   slots 1–7: DayCell(date) for Mon–Sun, DayCell(null) for days outside [rangeStart, rangeEnd]
 */
object CalendarBuilder {

    // spanCount expected by the GridLayoutManager in MainActivity
    const val SPAN_COUNT = 8

    fun build(startDate: LocalDate, endDate: LocalDate): List<CalendarItem> {
        val rangeStart = startDate.withDayOfMonth(1)
        val rangeEnd   = endDate.withDayOfMonth(endDate.lengthOfMonth())

        // Start from the Monday on or before rangeStart
        var weekMonday = rangeStart.with(DayOfWeek.MONDAY).let {
            if (it.isAfter(rangeStart)) it.minusWeeks(1) else it
        }

        val items = mutableListOf<CalendarItem>()

        while (!weekMonday.isAfter(rangeEnd)) {
            // slot 0: month label if Monday == 1st of month, else invisible pad
            val isoMonday = weekMonday  // Monday of this column
            if (isoMonday.dayOfMonth == 1 && !isoMonday.isBefore(rangeStart) && !isoMonday.isAfter(rangeEnd)) {
                val label = isoMonday.month.getDisplayName(TextStyle.SHORT, Locale.getDefault())
                items.add(CalendarItem.MonthLabel(label))
            } else {
                // Check if any day in this week belongs to a new month (to catch months
                // that don't start on Monday — show label at the start of the week column
                // where the 1st of that month falls)
                val monthFirstInThisWeek = (0..6)
                    .map { weekMonday.plusDays(it.toLong()) }
                    .firstOrNull { it.dayOfMonth == 1 && !it.isBefore(rangeStart) && !it.isAfter(rangeEnd) }

                if (monthFirstInThisWeek != null) {
                    val label = monthFirstInThisWeek.month.getDisplayName(TextStyle.SHORT, Locale.getDefault())
                    items.add(CalendarItem.MonthLabel(label))
                } else {
                    items.add(CalendarItem.DayCell(null)) // invisible pad
                }
            }

            // slots 1–7: Mon through Sun
            for (offset in 0..6) {
                val day = weekMonday.plusDays(offset.toLong())
                if (!day.isBefore(rangeStart) && !day.isAfter(rangeEnd)) {
                    items.add(CalendarItem.DayCell(day))
                } else {
                    items.add(CalendarItem.DayCell(null)) // out-of-range pad
                }
            }

            weekMonday = weekMonday.plusWeeks(1)
        }

        return items
    }
}