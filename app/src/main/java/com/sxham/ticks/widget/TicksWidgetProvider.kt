package com.sxham.ticks.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.sxham.ticks.MainActivity
import com.sxham.ticks.R
import com.sxham.ticks.data.WidgetPreferences

class TicksWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            WidgetPreferences.deleteWidgetData(context, appWidgetId)
        }
    }

    companion object {
        fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
            // 1. Fetch dates saved in SharedPreferences
            val startDate = WidgetPreferences.getStartDate(context, appWidgetId)
            val endDate = WidgetPreferences.getEndDate(context, appWidgetId)

            // 2. Render the minimalist grid using our Canvas engine
            val gridBitmap = GridCanvasRenderer.drawDotGrid(context, startDate, endDate)

            // 3. Bind the bitmap image to our XML layout ImageView
            val views = RemoteViews(context.packageName, R.layout.widget_layout)
            views.setImageViewBitmap(R.id.widget_grid_image, gridBitmap)

            // 4. Route widget clicks directly to the MainActivity Full-Screen Dashboard
            val dashboardIntent = Intent(context, MainActivity::class.java).apply {
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }

            val pendingIntent = PendingIntent.getActivity(
                context,
                appWidgetId, // Unique request code per instance to avoid overlapping mixups
                dashboardIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            // Bind click action tracking to the outer root widget layout framework
            views.setOnClickPendingIntent(R.id.widget_grid_image, pendingIntent)

            // 5. Push the update live to the home screen
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}