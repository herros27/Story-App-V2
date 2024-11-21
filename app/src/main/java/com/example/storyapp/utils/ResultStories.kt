package com.example.storyapp.utils

sealed class ResultStories<out T> {
    data class Success<out T>(val data: T?) : ResultStories<T>()
    data class Error(val error: String) : ResultStories<Nothing>()
    data object Loading : ResultStories<Nothing>()
}