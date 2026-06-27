package com.sxham.ticks.ui

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

data class WeekColumn(
    val monthLabel: String?,   // non-null only on the week where a new month starts
    val days: List<LocalDate?> // always 7 entries Mon–Sun, null = padding/out-of-range
)

object CalendarBuilder {

    const val SPAN_COUNT = 7  // pure 7-row dot grid, no label row mixed in

    fun build(startDate: LocalDate, endDate: LocalDate): List<WeekColumn> {
        val rangeStart = startDate.withDayOfMonth(1)
        val rangeEnd   = endDate.withDayOfMonth(endDate.lengthOfMonth())

        // Start from the Monday on or before rangeStart
        var weekMonday = rangeStart.with(DayOfWeek.MONDAY).let {
            if (it.isAfter(rangeStart)) it.minusWeeks(1) else it
        }

        val columns = mutableListOf<WeekColumn>()

        while (!weekMonday.isAfter(rangeEnd)) {
            val weekDays = (0..6).map { offset ->
                val day = weekMonday.plusDays(offset.toLong())
                if (!day.isBefore(rangeStart) && !day.isAfter(rangeEnd)) day else null
            }

            // Show month label on the column where the 1st of a month falls
            val monthLabel = (0..6)
                .map { weekMonday.plusDays(it.toLong()) }
                .firstOrNull { it.dayOfMonth == 1 && !it.isBefore(rangeStart) && !it.isAfter(rangeEnd) }
                ?.month
                ?.getDisplayName(TextStyle.SHORT, Locale.getDefault())

            columns.add(WeekColumn(monthLabel, weekDays))
            weekMonday = weekMonday.plusWeeks(1)
        }

        return columns
    }

    // Flat dot list for the GridLayoutManager (7 rows × N columns)
    fun buildDotItems(columns: List<WeekColumn>): List<LocalDate?> =
        columns.flatMap { it.days }
}