package com.sxham.ticks.widget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit

object GridCanvasRenderer {

    fun drawDotGrid(context: Context, startDate: LocalDate, endDate: LocalDate): Bitmap {
        // 1. Force explicit System Default Timezone to avoid off-by-one calendar caching bugs
        val today = LocalDate.now(ZoneId.systemDefault())

        val totalDays = (ChronoUnit.DAYS.between(startDate, endDate).toInt() + 1).coerceAtLeast(1)

        // Calculate raw passed days without constraints first
        val rawPassedDays = ChronoUnit.DAYS.between(startDate, today).toInt()
        val absolutePassedDays = rawPassedDays.coerceIn(0, totalDays)

        // Debug log directly to Android Studio's system logcat window
        Log.d("TicksEngine", "Start: $startDate | Today: $today | End: $endDate")
        Log.d("TicksEngine", "Total Days: $totalDays | Absolute Passed: $absolutePassedDays")

        val maxVisibleDots = 80
        val columns = 8

        val displayDays: Int
        val renderPassedCount: Int
        val isWindowed: Boolean

        if (totalDays <= maxVisibleDots) {
            displayDays = totalDays
            renderPassedCount = absolutePassedDays
            isWindowed = false
        } else {
            displayDays = maxVisibleDots
            isWindowed = true

            // Sliding Window calculation fix
            val currentSegment = absolutePassedDays / maxVisibleDots
            val startOffset = currentSegment * maxVisibleDots
            val actualWindowSize = (totalDays - startOffset).coerceAtMost(maxVisibleDots)

            // Ensure render count accurately reflects passed days within this specific window slice
            renderPassedCount = (absolutePassedDays - startOffset).coerceIn(0, actualWindowSize)
        }

        Log.d("TicksEngine", "Rendering Window Size: $displayDays | Render Passed Dots: $renderPassedCount")


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