package com.fahmi.aplikasistoryfahmi.Story_UserInterface

import androidx.lifecycle.*
import androidx.paging.*
import com.fahmi.aplikasistoryfahmi.ApiConfig
import com.fahmi.aplikasistoryfahmi.StoryResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException



//class ListStoryViewModel(private val storyRepository: StoryRepository) : ViewModel() {
//    fun getStoriesFlow(): Flow<PagingData<StoryResponse>> {
//        return storyRepository.getStories().cachedIn(viewModelScope)
//    }
//}
class ListStoryViewModel(private val storyRepository: StoryRepository) : ViewModel() {

    fun getStoriesLiveData(): LiveData<PagingData<StoryResponse>> {
        return storyRepository.getStories().map { pagingData ->
            pagingData.map { storyResponse ->
                StoryResponse(
                    id = storyResponse.id,
                    name = storyResponse.name,
                    description = storyResponse.description,
                    photoUrl = storyResponse.photoUrl,
                    createdAt = storyResponse.createdAt,
                    lat = storyResponse.lat,
                    lon = storyResponse.lon
                )
            }
        }.cachedIn(viewModelScope) // Cache the paging data to prevent unnecessary reloading
    }
}