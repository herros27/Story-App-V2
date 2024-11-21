package com.example.storyapp.view.adapter

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.storyapp.data.remote.response.ListStoryItem
import com.example.storyapp.databinding.ViewStoryBinding
import com.example.storyapp.utils.formatTime
import com.example.storyapp.view.ui.detail.DetailActivity

class StoryAdapter: PagingDataAdapter<ListStoryItem, StoryAdapter.MainViewHolder>(DIFF_CALLBACK){

    inner class MainViewHolder(private val binding: ViewStoryBinding) : RecyclerView.ViewHolder(binding.root) {

        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(story: ListStoryItem) {
            binding.tvJudul.text = story.name
            binding.tvSummary.text = formatTime(itemView.context, story.createdAt)
            Glide.with(itemView.context)
                .load(story.photoUrl)
                .into(binding.ivContent)


            binding.root.setOnClickListener {
                val intent = Intent(binding.root.context, DetailActivity::class.java)
                intent.putExtra("EXTRA_STORY", story)

                
                val optionsCompat: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        itemView.context as Activity,
                        Pair(binding.ivContent, "story"),
                        Pair(binding.tvJudul, "name"),
                        Pair(binding.tvSummary, "summary"),
                    )

                itemView.context.startActivity(intent, optionsCompat.toBundle())
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {

        val binding = ViewStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MainViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {

        val story = getItem(position)
        if (story != null) {
            holder.bind(story)
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem.id == newItem.id
            }
            override fun areContentsTheSame(
                oldItem: ListStoryItem,
                newItem: ListStoryItem
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

}