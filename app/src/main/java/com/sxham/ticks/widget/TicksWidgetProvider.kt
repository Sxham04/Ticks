package com.sxham.ticks.widget

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.widget.RemoteViews
import com.sxham.ticks.R
import com.sxham.ticks.data.WidgetPreferences
import com.sxham.ticks.ui.ConfigActivity

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
            val startDate = WidgetPreferences.getStartDate(context, appWidgetId)
            val endDate = WidgetPreferences.getEndDate(context, appWidgetId)

            val gridBitmap = GridCanvasRenderer.drawDotGrid(context, startDate, endDate)

            val views = RemoteViews(context.packageName, R.layout.widget_layout)
            views.setImageViewBitmap(R.id.widget_grid_image, gridBitmap)

            // 🎯 NEW CLICK LISTENER CONFIGURATION:
            // Create an intent that explicitly routes directly back to ConfigActivity
            val editIntent = Intent(context, ConfigActivity::class.java).apply {
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }

            // Wrap it inside a PendingIntent secure system wrapper token
            val pendingIntent = PendingIntent.getActivity(
                context,
                appWidgetId, // Unique request code per instance to avoid overlapping mixups
                editIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            // Bind click action tracking to the outer root widget layout framework
            views.setOnClickPendingIntent(R.id.widget_grid_image, pendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}