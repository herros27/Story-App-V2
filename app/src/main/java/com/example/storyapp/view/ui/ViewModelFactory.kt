package com.example.storyapp.view.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.data.repository.StoryRepository
//import com.example.storyapp.data.repository.UserRepository
import com.example.storyapp.data.repository.UserRepository
import com.example.storyapp.di.Injection
import com.example.storyapp.view.ui.login.LoginViewModel
import com.example.storyapp.view.ui.main.MainViewModel
import com.example.storyapp.view.ui.maps.MapsViewModel
import com.example.storyapp.view.ui.signup.RegisterViewModel
import com.example.storyapp.view.ui.upload.UploadViewModel

class ViewModelFactory(private val repository: UserRepository, private val storyRepository: StoryRepository) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(repository, storyRepository) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(repository, storyRepository) as T
            }
            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> {
                RegisterViewModel(storyRepository) as T
            }
            modelClass.isAssignableFrom(UploadViewModel::class.java) -> {
                UploadViewModel(storyRepository) as T
            }
            modelClass.isAssignableFrom(MapsViewModel::class.java) -> {
                MapsViewModel(storyRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null
        @JvmStatic
        fun getInstance(context: Context): ViewModelFactory {
            if (INSTANCE == null) {
                synchronized(ViewModelFactory::class.java) {
                    INSTANCE = ViewModelFactory(Injection.provideUserRepository(context),Injection.provideStoryRepository(context))
                }
            }
            return INSTANCE as ViewModelFactory
        }
    }
}