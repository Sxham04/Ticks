package com.sxham.ticks.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.RemoteViews
import com.sxham.ticks.MainActivity
import com.sxham.ticks.R
import com.sxham.ticks.data.WidgetPreferences

class TicksWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            val options = appWidgetManager.getAppWidgetOptions(appWidgetId)
            updateAppWidget(context, appWidgetManager, appWidgetId, options)
        }
    }

    // Called every time the user resizes the widget
    override fun onAppWidgetOptionsChanged(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        newOptions: Bundle
    ) {
        updateAppWidget(context, appWidgetManager, appWidgetId, newOptions)
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            WidgetPreferences.deleteWidgetData(context, appWidgetId)
        }
    }

    companion object {

        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int,
            options: Bundle? = null
        ) {
            val startDate = WidgetPreferences.getStartDate(context, appWidgetId)
            val endDate   = WidgetPreferences.getEndDate(context, appWidgetId)

            // Extract current widget size from options bundle
            // OPTION_APPWIDGET_MIN_WIDTH is the current width in portrait
            val widthDp  = options?.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH,  0) ?: 0
            val heightDp = options?.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT, 0) ?: 0

            val gridBitmap = GridCanvasRenderer.drawDotGrid(
                context, startDate, endDate, widthDp, heightDp
            )

            val views = RemoteViews(context.packageName, R.layout.widget_layout)
            views.setImageViewBitmap(R.id.widget_grid_image, gridBitmap)

            val dashboardIntent = Intent(context, MainActivity::class.java).apply {
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }

            val pendingIntent = PendingIntent.getActivity(
                context,
                appWidgetId,
                dashboardIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            views.setOnClickPendingIntent(R.id.widget_grid_image, pendingIntent)
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}