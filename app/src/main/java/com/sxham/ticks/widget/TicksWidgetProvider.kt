package com.sxham.ticks.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import com.sxham.ticks.R
import com.sxham.ticks.data.WidgetPreferences

class TicksWidgetProvider : AppWidgetProvider() {

    // Called when the widget needs to refresh (system interval or manual trigger)
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    // Called when a widget instance is deleted by the user
    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            WidgetPreferences.deleteWidgetData(context, appWidgetId)
        }
    }

    companion object {
        // Extracted helper function so our ConfigActivity can also force an immediate initial update
        fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
            // 1. Fetch dates saved in SharedPreferences for this specific widget instance
            val startDate = WidgetPreferences.getStartDate(context, appWidgetId)
            val endDate = WidgetPreferences.getEndDate(context, appWidgetId)

            // 2. Render the minimalist grid using our Canvas engine
            val gridBitmap = GridCanvasRenderer.drawDotGrid(context, startDate, endDate)

            // 3. Bind the bitmap image to our XML layout ImageView
            val views = RemoteViews(context.packageName, R.layout.widget_layout)
            views.setImageViewBitmap(R.id.widget_grid_image, gridBitmap)

            // 4. Push the update live to the home screen
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}