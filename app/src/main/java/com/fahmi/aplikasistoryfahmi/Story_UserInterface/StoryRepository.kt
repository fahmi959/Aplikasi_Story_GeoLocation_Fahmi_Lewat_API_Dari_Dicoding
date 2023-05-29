package com.fahmi.aplikasistoryfahmi.Story_UserInterface

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.fahmi.aplikasistoryfahmi.ApiService
import com.fahmi.aplikasistoryfahmi.Story
import com.fahmi.aplikasistoryfahmi.StoryResponse
import kotlinx.coroutines.flow.Flow




class StoryRepository(private val apiService: ApiService, private val authorizationHeader: String) {
    fun getStories(): LiveData<PagingData<StoryResponse>> {
        return Pager(
            config = PagingConfig(pageSize = 10),
            pagingSourceFactory = { StoryPagingSource(apiService, authorizationHeader) }
        ).liveData
    }
}
