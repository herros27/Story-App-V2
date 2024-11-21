package com.example.storyapp.view.widget

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.util.Log
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.bumptech.glide.Glide
import com.example.storyapp.R
import com.example.storyapp.data.remote.response.ListStoryItem
import com.example.storyapp.data.repository.StoryRepository
import com.example.storyapp.utils.ResultStories
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

internal class StoryWidgetFactory(
    private val context: Context,
    private val storyRepository: StoryRepository
) : RemoteViewsService.RemoteViewsFactory {

    private var storyList: List<ListStoryItem> = emptyList()
    private var lastFetchTime: Long = 0

    override fun onCreate() {
        fetchStories() 
        Log.d("StoryWidgetFactory", "Jumlah data story: ${storyList.size}")
    }

    private fun fetchStories() {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastFetchTime > 2 * 60 * 1000) {
            CoroutineScope(Dispatchers.IO).launch {
                val result = storyRepository.getStories(0)
                if (result is ResultStories.Success) {
                    storyList = result.data?.listStory ?: emptyList()
                    lastFetchTime = currentTime
                    AppWidgetManager.getInstance(context).notifyAppWidgetViewDataChanged(
                        AppWidgetManager.getInstance(context).getAppWidgetIds(
                            ComponentName(context, StoryBannerWidget::class.java)
                        ),
                        R.id.stackView
                    )
                }
            }
        }
    }


    override fun onDataSetChanged() {
        fetchStories()
    }

    override fun onDestroy() {
        storyList = emptyList()
    }

    override fun getCount(): Int = storyList.size

    override fun getViewAt(position: Int): RemoteViews {
        val rv = RemoteViews(context.packageName, R.layout.widget_item)
        if (position >= storyList.size) return rv 

        val story = storyList[position]

        
        try {
            val bitmap = Glide.with(context)
                .asBitmap()
                .load(story.photoUrl)
                .submit()
                .get() 

            rv.setImageViewBitmap(R.id.ivContent_widget, bitmap)
        } catch (e: Exception) {
            e.printStackTrace()
            rv.setImageViewResource(R.id.ivContent_widget, R.drawable.image_logo) 
        }
        return rv
    }

    override fun getLoadingView(): RemoteViews? = null

    override fun getViewTypeCount(): Int = 1

    override fun getItemId(position: Int): Long = position.toLong()

    override fun hasStableIds(): Boolean = true 
}
