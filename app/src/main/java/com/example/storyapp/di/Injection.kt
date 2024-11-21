package com.example.storyapp.di

import android.content.Context
import com.example.storyapp.data.local.StoryDatabase
import com.example.storyapp.data.pref.UserPreference
import com.example.storyapp.data.pref.dataStore
import com.example.storyapp.data.remote.service.ApiConfig
import com.example.storyapp.data.repository.StoryRepository
import com.example.storyapp.data.repository.UserRepository

object Injection {
    fun provideUserRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        return UserRepository.getInstance(pref)
    }

    fun provideStoryRepository(context: Context): StoryRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val apiService = ApiConfig(pref,context).apiService
        val database = StoryDatabase.getDatabase(context)
        return StoryRepository(database,apiService)
    }
}