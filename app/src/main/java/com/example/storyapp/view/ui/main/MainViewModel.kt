package com.example.storyapp.view.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.storyapp.data.pref.UserModel
import com.example.storyapp.data.remote.response.ListStoryItem
import com.example.storyapp.data.repository.StoryRepository
import com.example.storyapp.data.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class MainViewModel(private val repository: UserRepository, private val storyRepository: StoryRepository) : ViewModel() {

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun userName(): Flow<UserModel> {
        return repository.getSession()
    }

    val storiesPage: LiveData<PagingData<ListStoryItem>> =
        storyRepository.getStories().cachedIn(viewModelScope)

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }
}
