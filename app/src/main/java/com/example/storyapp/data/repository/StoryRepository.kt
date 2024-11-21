package com.example.storyapp.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.example.storyapp.data.local.StoryDatabase
import com.example.storyapp.utils.StoryRemoteMediator
import com.example.storyapp.data.remote.response.ErrorResponse
import com.example.storyapp.data.remote.response.ListStoryItem
import com.example.storyapp.data.remote.response.LoginResponse
import com.example.storyapp.data.remote.response.StoryResponse
import com.example.storyapp.data.remote.service.ApiException
import com.example.storyapp.data.remote.service.ApiService
import com.example.storyapp.utils.RegisterResult
import com.example.storyapp.utils.ResultStories
import com.example.storyapp.utils.wrapEspressoIdlingResource
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import java.io.File

class StoryRepository(private val storyDatabase: StoryDatabase, private val apiService: ApiService) {
    suspend fun register(name: String, email: String, password: String): RegisterResult {
        return try {
            val response = apiService.register(name, email, password)
            RegisterResult.Success(response)
        } catch (e: Exception) {
                RegisterResult.Error(ErrorResponse(true, "Email is already taken"))
        }
    }

    suspend fun getStories(location: Int): ResultStories<StoryResponse> {
        return try {
            val response = apiService.getStories(location)
            if (response.error) {
                ResultStories.Error(response.message)
            } else {
                ResultStories.Success(response)
            }
        } catch (e: Exception) {
            ResultStories.Error("Network error: ${e.message}")
        }
    }

    suspend fun getDetail(id: String):ListStoryItem{
        return apiService.getDetailStory(id).story
    }

    fun postStory(description: String, photoFile: File,lat:Float?,lon:Float?)= liveData {
        emit(ResultStories.Loading)
        val requestBody = description.toRequestBody("text/plain".toMediaTypeOrNull())
        val requestImageFile = photoFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val multipartBody = MultipartBody.Part.createFormData(
            "photo",
            photoFile.name,
            requestImageFile
        )

        try {
            val successResponse = apiService.postStories(requestBody, multipartBody, lat, lon)
            emit(ResultStories.Success(successResponse))
        }catch (e: Exception){
            val errorBody = e.message.toString()
            val errorResponse = ErrorResponse(true, errorBody)
            emit(errorResponse.message?.let { ResultStories.Error(it)})
        }
    }

    suspend fun login(email: String, password: String): Response<LoginResponse> {
        wrapEspressoIdlingResource{
            return try {
                val response = apiService.login(email, password)

                if (response.isSuccessful) response else {
                    throw ApiException("Login failed: ${response.message()}")
                }
            } catch (e: Exception) {
                throw ApiException("Error occurred during login: ${e.message}")
            }
        }
    }

    @OptIn(ExperimentalPagingApi::class)
    fun getStories(): LiveData<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = StoryRemoteMediator(storyDatabase, apiService), 
            pagingSourceFactory = { 
                storyDatabase.storyDao().getAllStory()
            }
        ).liveData
    }
}
