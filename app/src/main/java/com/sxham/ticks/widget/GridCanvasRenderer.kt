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
import kotlin.math.ceil
import kotlin.math.roundToInt

object GridCanvasRenderer {

    /**
     * @param widthDp  available widget width in dp  (from AppWidgetOptions, or 0 to use default)
     * @param heightDp available widget height in dp (from AppWidgetOptions, or 0 to use default)
     */
    fun drawDotGrid(
        context: Context,
        startDate: LocalDate,
        endDate: LocalDate,
        widthDp: Int = 0,
        heightDp: Int = 0
    ): Bitmap {
        val density = context.resources.displayMetrics.density

        // ── Resolve pixel dimensions ──────────────────────────────────────────
        // Fall back to sensible defaults if no size provided
        val bitmapWidth  = if (widthDp  > 0) (widthDp  * density).roundToInt() else 1000
        val bitmapHeight = if (heightDp > 0) (heightDp * density).roundToInt() else 400

        // ── Compute column count from available width ─────────────────────────
        // Each dot cell is roughly 28px at 1x density; scale with density
        val minCellPx = (28 * density).roundToInt()
        val columns = (bitmapWidth / minCellPx).coerceIn(4, 16)

        // ── Date maths (unchanged from original) ─────────────────────────────
        val today = LocalDate.now(ZoneId.systemDefault())
        val totalDays = (ChronoUnit.DAYS.between(startDate, endDate).toInt() + 1).coerceAtLeast(1)
        val rawPassedDays = ChronoUnit.DAYS.between(startDate, today).toInt()
        val absolutePassedDays = rawPassedDays.coerceIn(0, totalDays)

        Log.d("TicksEngine", "Start: $startDate | Today: $today | End: $endDate")
        Log.d("TicksEngine", "Total Days: $totalDays | Absolute Passed: $absolutePassedDays")

        // ── Sliding window (unchanged) ────────────────────────────────────────
        val maxVisibleDots = columns * (bitmapHeight / minCellPx).coerceIn(2, 20)

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
            val currentSegment = absolutePassedDays / maxVisibleDots
            val startOffset = currentSegment * maxVisibleDots
            val actualWindowSize = (totalDays - startOffset).coerceAtMost(maxVisibleDots)
            renderPassedCount = (absolutePassedDays - startOffset).coerceIn(0, actualWindowSize)
        }

        Log.d("TicksEngine", "Bitmap: ${bitmapWidth}x${bitmapHeight} | Columns: $columns | Dots: $displayDays")

        // ── Grid metrics ──────────────────────────────────────────────────────
        val rows = ceil(displayDays.toDouble() / columns).toInt().coerceAtLeast(1)

        val spacingX = bitmapWidth.toFloat() / (columns + 1)
        val spacingY = (bitmapHeight - 100f) / (rows + 1) // reserve 100px for footer text

        val paddingLeft = spacingX
        val paddingTop  = spacingY

        // Dot radius scales with the smaller of the two spacings, capped sensibly
        val dotRadius = (minOf(spacingX, spacingY) * 0.25f).coerceIn(6f, 24f)

        // ── Bitmap + Canvas ───────────────────────────────────────────────────
        val bitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.TRANSPARENT)

        // ── Paints ────────────────────────────────────────────────────────────
        val passedPaint = Paint().apply {
            color = Color.parseColor("#FFFFFF")
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

        // ── Draw dots ─────────────────────────────────────────────────────────
        for (i in 0 until displayDays) {
            val row = i / columns
            val col = i % columns
            val cx = paddingLeft + col * spacingX
            val cy = paddingTop  + row * spacingY
            canvas.drawCircle(cx, cy, dotRadius, if (i < renderPassedCount) passedPaint else remainingPaint)
        }

        // ── Footer text ───────────────────────────────────────────────────────
        val progressPercent = ((absolutePassedDays.toFloat() / totalDays) * 100)
            .coerceIn(0f, 100f).toInt()
        val footerText = if (isWindowed) {
            "$absolutePassedDays / ${totalDays}d ($progressPercent%) • window"
        } else {
            "$absolutePassedDays / ${totalDays}d passed ($progressPercent%)"
        }
        canvas.drawText(footerText, bitmapWidth / 2f, bitmapHeight - 30f, textPaint)

        return bitmap
    }
}