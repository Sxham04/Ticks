package com.sxham.ticks.widget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import java.time.LocalDate
import java.time.temporal.ChronoUnit

object GridCanvasRenderer {

    fun drawDotGrid(context: Context, startDate: LocalDate, endDate: LocalDate): Bitmap {
        val totalDays = ChronoUnit.DAYS.between(startDate, endDate).toInt() + 1
        val absolutePassedDays = ChronoUnit.DAYS.between(startDate, LocalDate.now()).toInt()

        // 1. Establish our Macro Constraints
        val maxVisibleDots = 80 // Max dots displayed at once to keep them punchy and visible
        val columns = 8        // Clear 8 columns layout

        // Window slicing variables
        val displayDays: Int
        val renderPassedCount: Int
        val isWindowed: Boolean

        if (totalDays <= maxVisibleDots) {
            // Standard View: Timeline fits entirely inside our grid layout
            displayDays = totalDays
            renderPassedCount = absolutePassedDays.coerceIn(0, totalDays)
            isWindowed = false
        } else {
            // Sliding Window View: Slice out an 80-day block centered near today
            displayDays = maxVisibleDots
            isWindowed = true

            // Calculate how many rows/days to offset so "Today" stays visible in the matrix
            val currentSegment = (absolutePassedDays / maxVisibleDots)
            val startOffset = currentSegment * maxVisibleDots

            val remainingInTimeline = totalDays - startOffset
            val actualWindowSize = remainingInTimeline.coerceAtMost(maxVisibleDots)

            renderPassedCount = (absolutePassedDays - startOffset).coerceIn(0, actualWindowSize)
        }

        val rows = Math.ceil(displayDays.toDouble() / columns).toInt().coerceAtLeast(1)

        // 2. Proportional Grid Metrics
        val bitmapWidth = 1000
        val baseSpacing = bitmapWidth / (columns + 1)
        val spacingX = baseSpacing.toFloat()
        val spacingY = baseSpacing.toFloat()

        val paddingTop = spacingY
        val paddingLeft = spacingX

        // Add extra room at the bottom for a progress tracking text line
        val textHeightSpacer = 120f
        val bitmapHeight = ((rows + 1) * baseSpacing + textHeightSpacer).toInt().coerceAtLeast(400)

        val bitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.TRANSPARENT)

        val dotRadius = 22f

        // 3. Styling Brushes
        val passedPaint = Paint().apply {
            color = Color.parseColor("#FFFFFF") // Solid White
            isAntiAlias = true
        }

        val remainingPaint = Paint().apply {
            color = Color.parseColor("#33FFFFFF") // Muted Translucent White
            isAntiAlias = true
        }

        val textPaint = Paint().apply {
            color = Color.parseColor("#88FFFFFF") // Elegant secondary text tone
            textSize = 36f
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
        }

        // 4. Paint the Grid Matrix
        for (i in 0 until displayDays) {
            val row = i / columns
            val col = i % columns

            val cx = paddingLeft + (col * spacingX)
            val cy = paddingTop + (row * spacingY)

            val currentPaint = if (i < renderPassedCount) passedPaint else remainingPaint
            canvas.drawCircle(cx, cy, dotRadius, currentPaint)
        }

        // 5. Render Macro Progress Indicator Text
        val progressPercent = ((absolutePassedDays.toFloat() / totalDays.toFloat()) * 100).coerceIn(0f, 100f).toInt()
        val footerText = if (isWindowed) {
            "Total Time: $absolutePassedDays / ${totalDays}d passed ($progressPercent%) • Window view"
        } else {
            "$absolutePassedDays / ${totalDays}d passed ($progressPercent%)"
        }

        canvas.drawText(footerText, (bitmapWidth / 2f), (bitmapHeight - 50f), textPaint)

        return bitmap
    }
}