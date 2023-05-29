package com.fahmi.aplikasistoryfahmi.Story_UserInterface

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import com.fahmi.aplikasistoryfahmi.Adapter.StoryAdapter
import com.fahmi.aplikasistoryfahmi.DataDummy
import com.fahmi.aplikasistoryfahmi.MainDispatcherRule
import com.fahmi.aplikasistoryfahmi.StoryResponse
import com.fahmi.aplikasistoryfahmi.getOrAwaitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class ListStoryViewModelTest {
        @get:Rule
        val instantExecutorRule = InstantTaskExecutorRule()

        @get:Rule
        val mainDispatcherRules = MainDispatcherRule()

        @Mock
        private lateinit var storyRepository: StoryRepository

        @Test
        fun `when Get Quote Should Not Null and Return Data`() = runTest {
                val dummyQuote = DataDummy.generateDummyQuoteResponse()
                val data: PagingData<StoryResponse> = StoryPagingSource.snapshot(dummyQuote)
                val expectedQuote = MutableLiveData<PagingData<StoryResponse>>()
                expectedQuote.value = data
                Mockito.`when`(storyRepository.getStories()).thenReturn(expectedQuote)

                val mainViewModel = ListStoryViewModel(storyRepository)
                val actualQuote: PagingData<StoryResponse> = mainViewModel.getStoriesLiveData().getOrAwaitValue()

                val differ = AsyncPagingDataDiffer(
                        diffCallback = StoryAdapter.DIFF_CALLBACK,
                        updateCallback = noopListUpdateCallback,
                        workerDispatcher = Dispatchers.Main,
                )
                differ.submitData(actualQuote)

                Assert.assertNotNull(differ.snapshot())
                Assert.assertEquals(dummyQuote.size, differ.snapshot().size)
                Assert.assertEquals(dummyQuote[0], differ.snapshot()[0])
        }

        @Test
        fun `when Get Quote Empty Should Return No Data`() = runTest {
                val data: PagingData<StoryResponse> = PagingData.from(emptyList())
                val expectedQuote = MutableLiveData<PagingData<StoryResponse>>()
                expectedQuote.value = data
                Mockito.`when`(storyRepository.getStories()).thenReturn(expectedQuote)
                val mainViewModel = ListStoryViewModel(storyRepository)
                val actualQuote: PagingData<StoryResponse> = mainViewModel.getStoriesLiveData().getOrAwaitValue()
                val differ = AsyncPagingDataDiffer(
                        diffCallback = StoryAdapter.DIFF_CALLBACK,
                        updateCallback = noopListUpdateCallback,
                        workerDispatcher = Dispatchers.Main,
                )
                differ.submitData(actualQuote)
                Assert.assertEquals(0, differ.snapshot().size)
        }
}

class StoryPagingSource : PagingSource<Int, LiveData<List<StoryResponse>>>() {

        companion object {
                fun snapshot(items: List<StoryResponse>): PagingData<StoryResponse> {
                        return PagingData.from(items)
                }
        }

        override fun getRefreshKey(state: PagingState<Int, LiveData<List<StoryResponse>>>): Int {
                return 0
        }



        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<StoryResponse>>> {
                return LoadResult.Page(emptyList(), 0, 1)
        }
}

val noopListUpdateCallback = object : ListUpdateCallback {
        override fun onInserted(position: Int, count: Int) {}
        override fun onRemoved(position: Int, count: Int) {}
        override fun onMoved(fromPosition: Int, toPosition: Int) {}
        override fun onChanged(position: Int, count: Int, payload: Any?) {}
}