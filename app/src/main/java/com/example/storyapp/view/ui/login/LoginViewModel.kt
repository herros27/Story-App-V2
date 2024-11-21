package com.example.storyapp.view.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapp.data.pref.UserModel
import com.example.storyapp.data.remote.response.LoginResponse
import com.example.storyapp.data.remote.service.ApiException
import com.example.storyapp.data.repository.StoryRepository
import com.example.storyapp.data.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: UserRepository, private val storyRepository: StoryRepository) : ViewModel() {
    private val _loginResponse = MutableLiveData<LoginResponse?>()
    val loginResponse: LiveData<LoginResponse?> get() = _loginResponse

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun saveSession(user: UserModel) {
        viewModelScope.launch {
            repository.saveSession(user)
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = storyRepository.login(email, password)
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse?.error == true) {
                        _errorMessage.postValue(loginResponse.message ?: "Login failed")
                    } else {
                        _loginResponse.postValue(loginResponse)
                    }
                } else {
                    _errorMessage.postValue(response.message() ?: "Unexpected error occurred")
                }
            } catch (e: ApiException) {
                _errorMessage.postValue(e.message ?: "API error occurred")
            } catch (e: Exception) {
                _errorMessage.postValue("Unexpected error: ${e.message}")
            }
        }
    }
}