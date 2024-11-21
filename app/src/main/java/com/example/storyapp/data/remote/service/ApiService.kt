package com.example.storyapp.data.remote.service

import com.example.storyapp.data.remote.response.DetailStoryResponse
import com.example.storyapp.data.remote.response.ListStoryItem
import com.example.storyapp.data.remote.response.LoginResponse
import com.example.storyapp.data.remote.response.RegisterResponse
import com.example.storyapp.data.remote.response.StoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): RegisterResponse

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Response<LoginResponse>

    @GET("stories")
    suspend fun getStories(
        @Query("location") location: Int,
    ): StoryResponse

    @GET("stories")
    suspend fun getStoriesPaging(
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 20,


    ): StoryResponse

    @Multipart
    @POST("stories")
    suspend fun postStories(
        @Part("description") description: RequestBody,
        @Part photo: MultipartBody.Part,
        @Part("lat") lat: Float?,
        @Part("lon") lon: Float?
    ): StoryResponse

    @GET("stories/{id}")
    suspend fun getDetailStory(@Path("id") id: String): DetailStoryResponse





}