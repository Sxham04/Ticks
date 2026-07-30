package com.sxham.ticks.widget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import kotlin.math.ceil
import kotlin.math.roundToInt

object GridCanvasRenderer {

    fun drawDotGrid(
        context: Context,
        startDate: LocalDate,
        endDate: LocalDate,
        widthDp: Int = 0,
        heightDp: Int = 0
    ): Bitmap {
        val density = context.resources.displayMetrics.density

        val bitmapWidth  = if (widthDp  > 0) (widthDp  * density).roundToInt() else 1000
        val bitmapHeight = if (heightDp > 0) (heightDp * density).roundToInt() else 400

        val minCellPx = (28 * density).roundToInt()
        val columns = (bitmapWidth / minCellPx).coerceIn(4, 16)

        val today = LocalDate.now(ZoneId.systemDefault())
        val totalDays = (ChronoUnit.DAYS.between(startDate, endDate).toInt() + 1).coerceAtLeast(1)
        val rawPassedDays = ChronoUnit.DAYS.between(startDate, today).toInt()
        val absolutePassedDays = rawPassedDays.coerceIn(0, totalDays)

        val todayIndex = when {
            today.isBefore(startDate) || today.isAfter(endDate) -> -1
            else -> ChronoUnit.DAYS.between(startDate, today).toInt()
        }

        val maxRows = (bitmapHeight / minCellPx).coerceIn(2, 20)
        val maxVisibleDots = columns * maxRows

        val displayDays: Int
        val windowStartOffset: Int
        val isWindowed: Boolean

        if (totalDays <= maxVisibleDots) {
            displayDays = totalDays
            windowStartOffset = 0
            isWindowed = false
        } else {
            displayDays = maxVisibleDots
            windowStartOffset = totalDays - maxVisibleDots
            isWindowed = true
        }

        val renderPassedCount = (absolutePassedDays - windowStartOffset).coerceIn(0, displayDays)
        val todayWindowIndex = if (todayIndex >= 0) todayIndex - windowStartOffset else -1

        val rows = ceil(displayDays.toDouble() / columns).toInt().coerceAtLeast(1)

        val spacingX = bitmapWidth.toFloat() / (columns + 1)
        val spacingY = (bitmapHeight - 100f) / (rows + 1)
        val paddingLeft = spacingX
        val paddingTop  = spacingY

        val dotRadius = (minOf(spacingX, spacingY) * 0.25f).coerceIn(6f, 24f)

        val bitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.TRANSPARENT)

        val passedPaint = Paint().apply {
            color = Color.parseColor("#FFFFFF")
            isAntiAlias = true
        }
        val todayPaint = Paint().apply {
            color = Color.parseColor("#4ADE80")
            isAntiAlias = true
        }
        val remainingPaint = Paint().apply {
            color = Color.parseColor("#33FFFFFF")
            isAntiAlias = true
        }
        val textPaint = Paint().apply {
            color = Color.parseColor("#88FFFFFF")
            textSize = (10 * density).coerceIn(18f, 28f)
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
        }

        for (i in 0 until displayDays) {
            val row = i / columns
            val col = i % columns
            val cx = paddingLeft + col * spacingX
            val cy = paddingTop  + row * spacingY

            val paint = when {
                i == todayWindowIndex -> todayPaint
                i < renderPassedCount -> passedPaint
                else                  -> remainingPaint
            }
            canvas.drawCircle(cx, cy, dotRadius, paint)
        }

        val progressPercent = ((absolutePassedDays.toFloat() / totalDays) * 100)
            .coerceIn(0f, 100f).toInt()
        val footerText = if (isWindowed) {
            "$absolutePassedDays / ${totalDays}d ($progressPercent%) • ${totalDays - absolutePassedDays}d left"
        } else {
            "$absolutePassedDays / ${totalDays}d passed ($progressPercent%)"
        }
        canvas.drawText(footerText, bitmapWidth / 2f, bitmapHeight - 30f, textPaint)

        return bitmap
    }
}