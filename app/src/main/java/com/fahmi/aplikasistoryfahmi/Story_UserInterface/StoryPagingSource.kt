package com.fahmi.aplikasistoryfahmi.Story_UserInterface

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.fahmi.aplikasistoryfahmi.ApiService
import com.fahmi.aplikasistoryfahmi.StoryResponse

class StoryPagingSource(
    private val apiService: ApiService,
    private val authorizationHeader: String
) : PagingSource<Int, StoryResponse>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, StoryResponse> {
        try {
            val page = params.key ?: 1
            val response = apiService.getAllStories(authorizationHeader, page, params.loadSize, null)
            val stories = response.listStory

            val prevKey = if (page > 1) page - 1 else null
            val nextKey = if (stories.isNotEmpty()) page + 1 else null

            return LoadResult.Page(
                data = stories,
                prevKey = prevKey,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, StoryResponse>): Int? {
        return state.anchorPosition
    }
}