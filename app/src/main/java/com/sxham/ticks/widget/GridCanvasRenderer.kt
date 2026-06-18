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
        // 1. Calculate day intervals
        val totalDays = ChronoUnit.DAYS.between(startDate, endDate).toInt() + 1
        val passedDays = ChronoUnit.DAYS.between(startDate, LocalDate.now()).toInt().coerceIn(0, totalDays)

        // 2. Set up the sizing math (Canvas dimensions in pixels)
        val bitmapWidth = 600
        val bitmapHeight = 800
        val bitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Clear canvas with a transparent background (or match your aesthetic preference)
        canvas.drawColor(Color.TRANSPARENT)

        // 3. Define the styling configurations
        val passedPaint = Paint().apply {
            color = Color.parseColor("#111111") // Sharp dark dot for spent days
            isAntiAlias = true
            style = Paint.Style.FILL
        }

        val remainingPaint = Paint().apply {
            color = Color.parseColor("#E5E5E5") // Soft gray dot for remaining days
            isAntiAlias = true
            style = Paint.Style.FILL
        }

        // 4. Grid Layout Math
        val columns = 10                  // 10 dots per row
        val dotRadius = 10f                // Size of each dot
        val spacingX = 50f                // Horizontal gap between dots
        val spacingY = 50f                // Vertical gap between rows
        val paddingLeft = 50f             // Margins from the edges
        val paddingTop = 50f

        // 5. Draw the grid
        for (i in 0 until totalDays) {
            val row = i / columns
            val col = i % columns

            // Compute exact pixel coordinate for this specific dot
            val cx = paddingLeft + (col * spacingX)
            val cy = paddingTop + (row * spacingY)

            // Pick color depending on whether the day has slipped away
            val currentPaint = if (i < passedDays) passedPaint else remainingPaint

            canvas.drawCircle(cx, cy, dotRadius, currentPaint)
        }

        return bitmap
    }
}