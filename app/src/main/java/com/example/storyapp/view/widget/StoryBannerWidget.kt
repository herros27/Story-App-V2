package com.example.storyapp.view.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import android.widget.Toast
import androidx.core.net.toUri
import com.example.storyapp.R

class StoryBannerWidget : AppWidgetProvider() {

    companion object {
        private const val TOAST_ACTION = "com.example.TOAST_ACTION"
        const val EXTRA_ITEM = "com.example.EXTRA_ITEM"

        private fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val intent = Intent(context, WidgetStackService::class.java).apply {
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                data = toUri(Intent.URI_INTENT_SCHEME).toUri().buildUpon()
                    .appendQueryParameter("appWidgetId", appWidgetId.toString())
                    .build()
            }

            val views = RemoteViews(context.packageName, R.layout.story_banner_widget)
            views.setRemoteAdapter(R.id.stackView, intent)
            views.setEmptyView(R.id.stackView, R.id.empty_view)

            val toastIntent = Intent(context, StoryBannerWidget::class.java).apply {
                action = TOAST_ACTION
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            }

            val toastPendingIntent = PendingIntent.getBroadcast(
                context, 0, toastIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_MUTABLE else 0)
            )
            views.setPendingIntentTemplate(R.id.stackView, toastPendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
            AppWidgetManager.getInstance(context).notifyAppWidgetViewDataChanged(appWidgetId, R.id.stackView)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == TOAST_ACTION) {
            val viewIndex = intent.getIntExtra(EXTRA_ITEM, 0)
            Toast.makeText(context, "Touched view $viewIndex", Toast.LENGTH_SHORT).show()
        }
    }
}
