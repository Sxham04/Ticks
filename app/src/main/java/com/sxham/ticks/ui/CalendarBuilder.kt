package com.sxham.ticks.ui

import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

data class WeekColumn(
    val monthLabel: String?, // non-null on the first column of each month
    val days: List<LocalDate?> // always 7 entries, null = empty slot
)

object CalendarBuilder {

    const val SPAN_COUNT = 7

    fun build(startDate: LocalDate, endDate: LocalDate): List<WeekColumn> {
        val columns = mutableListOf<WeekColumn>()

        // Iterate month by month
        var monthStart = startDate.withDayOfMonth(1)
        val finalMonthEnd = endDate.withDayOfMonth(endDate.lengthOfMonth())

        while (!monthStart.isAfter(finalMonthEnd)) {
            val monthEnd = monthStart.withDayOfMonth(monthStart.lengthOfMonth())
            val month = monthStart.month
            val label = month.getDisplayName(TextStyle.SHORT, Locale.getDefault())

            // Collect all days of this month that fall within [startDate, endDate]
            // Days outside the user range are still included as real dates (rendered
            // as hollow dots by the adapter). Days don't exist = null (invisible pad).
            val allDays = (1..monthStart.lengthOfMonth()).map { day ->
                monthStart.withDayOfMonth(day)
            }

            // Chunk into groups of 7 (columns of 7 rows each)
            val chunks = allDays.chunked(7)

            chunks.forEachIndexed { index, chunk ->
                // Pad the last chunk to 7 with nulls
                val padded = if (chunk.size < 7) {
                    chunk + List(7 - chunk.size) { null }
                } else {
                    chunk.map { it } // already List<LocalDate?> compatible
                }

                columns.add(
                    WeekColumn(
                        monthLabel = if (index == 0) label else null,
                        days = padded
                    )
                )
            }

            monthStart = monthStart.plusMonths(1)
        }

        return columns
    }

    fun buildDotItems(columns: List<WeekColumn>): List<LocalDate?> =
        columns.flatMap { it.days }
}