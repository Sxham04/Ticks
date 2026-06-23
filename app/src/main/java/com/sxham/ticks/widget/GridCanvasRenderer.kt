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
        val passedDays = ChronoUnit.DAYS.between(startDate, LocalDate.now()).toInt().coerceIn(0, totalDays)

        // 1. Dynamic Column Sizing based on Timeframe Scale
        val columns = when {
            totalDays <= 60  -> 8   // Short sprint
            totalDays <= 365 -> 14  // Year scale
            else             -> 24  // Large multi-year tracking
        }
        val rows = Math.ceil(totalDays.toDouble() / columns).toInt().coerceAtLeast(1)

        // 2. Proportional Grid Math (Fixes wide vertical gaps)
        val bitmapWidth = 1000
        val baseSpacing = bitmapWidth / (columns + 1) // Force square aspect ratio boundaries
        val spacingX = baseSpacing.toFloat()
        val spacingY = baseSpacing.toFloat() // Forces equal vertical/horizontal gaps

        // Calculate dynamic height based directly on row count
        val paddingTop = spacingY
        val paddingLeft = spacingX
        val bitmapHeight = ((rows + 1) * baseSpacing).coerceAtLeast(400)

        // Create the tailored bitmap surface
        val bitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.TRANSPARENT)

        // 3. Dynamic Dot Sizing (Shrinks cleanly if user inputs massive spans)
        val dotRadius = when {
            totalDays <= 60  -> 24f
            totalDays <= 365 -> 14f
            else             -> 8f
        }

        // 4. Fine-Tuned Minimalist Aesthetics
        val passedPaint = Paint().apply {
            color = Color.parseColor("#FFFFFF") // Bright, solid white (This day is over and locked in)
            isAntiAlias = true
            style = Paint.Style.FILL
        }

        val remainingPaint = Paint().apply {
            color = Color.parseColor("#40FFFFFF") // Soft, muted translucent white (The future)
            isAntiAlias = true
            style = Paint.Style.FILL
        }

        // 5. Build Grid
        for (i in 0 until totalDays) {
            val row = i / columns
            val col = i % columns

            // Grid coordinate calculations
            val cx = paddingLeft + (col * spacingX)
            val cy = paddingTop + (row * spacingY)

            val currentPaint = if (i < passedDays) passedPaint else remainingPaint
            canvas.drawCircle(cx, cy, dotRadius, currentPaint)
        }

        return bitmap
    }
}