package com.example.storyapp.utils

import com.example.storyapp.data.remote.response.ErrorResponse
import com.example.storyapp.data.remote.response.RegisterResponse

sealed class RegisterResult{
    data class Success(val registerResponse: RegisterResponse) : RegisterResult()
    data class Error(val errorResponse: ErrorResponse) : RegisterResult()
}
