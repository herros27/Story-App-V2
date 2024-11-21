package com.example.storyapp.utils

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.storyapp.data.local.StoryDatabase
import com.example.storyapp.data.remote.response.ListStoryItem
import com.example.storyapp.data.remote.service.ApiService

@OptIn(ExperimentalPagingApi::class)
class StoryRemoteMediator(
    private val database: StoryDatabase,
    private val apiService: ApiService
)  : RemoteMediator<Int, ListStoryItem>() {

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ListStoryItem>
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH ->{
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: INITIAL_PAGE_INDEX
            }
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.prevKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                prevKey
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                nextKey
            }
        }

        try {
            val responseData = apiService.getStoriesPaging(page, state.config.pageSize)

            val endOfPaginationReached = responseData.listStory.isEmpty()

            database.withTransaction {
                Log.d("RemoteMediator", "Starting database transaction")
                if (loadType == LoadType.REFRESH) {
                    database.remoteKeysDao().deleteRemoteKeys()
                    database.storyDao().deleteAll()
                    Log.d("RemoteMediator", "Cleared database")
                }
                val prevKey = if (page == 1) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1
                val keys = responseData.listStory.map {
                    RemoteKeys(
                        id = it.id,
                        prevKey = prevKey,
                        nextKey = nextKey
                    )
                }
                database.remoteKeysDao().insertAll(keys)
                database.storyDao().insertStories(responseData.listStory)

            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (exception: Exception) {
            return MediatorResult.Error(exception)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, ListStoryItem>): RemoteKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { data ->
            data.id.let { database.remoteKeysDao().getRemoteKeysId(it) }
        }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, ListStoryItem>): RemoteKeys? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()?.let { data ->
            data.id.let { database.remoteKeysDao().getRemoteKeysId(it) }
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, ListStoryItem>): RemoteKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                database.remoteKeysDao().getRemoteKeysId(id)
            }
        }
    }


    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }
}