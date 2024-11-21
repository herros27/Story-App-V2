package com.example.storyapp.view.ui.upload

import androidx.lifecycle.ViewModel
import com.example.storyapp.data.repository.StoryRepository
import java.io.File

class UploadViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    fun uploadStory(description: String, photoFile: File,lat:Float?,lon:Float?)  =
        storyRepository.postStory(
            description,
            photoFile,
            lat,
            lon
        )
}