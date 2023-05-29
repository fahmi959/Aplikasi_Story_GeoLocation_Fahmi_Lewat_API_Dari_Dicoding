package com.fahmi.aplikasistoryfahmi.Story_UserInterface

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.fahmi.aplikasistoryfahmi.*
import com.fahmi.aplikasistoryfahmi.Adapter.StoryAdapter
import com.fahmi.aplikasistoryfahmi.databinding.ActivityListStoryBinding
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest

class ListStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityListStoryBinding
    private lateinit var adapter: StoryAdapter
    private lateinit var viewModel: ListStoryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.fabAdd.setOnClickListener {
            startActivity(Intent(this, TambahStoryActivity::class.java))
        }

        // Inisialisasi adapter dan RecyclerView
        adapter = StoryAdapter()
        binding.rvStory.adapter = adapter
        binding.rvStory.layoutManager = LinearLayoutManager(this)

        adapter.LihatDetail(object : StoryAdapter.OnItemClickListener {
            override fun onItemClick(story: StoryResponse) {
                val intent = Intent(this@ListStoryActivity, DetailStoryActivity::class.java)
                intent.putExtra("STORY_ID", story.id)
                startActivity(intent)
            }
        })

        binding.petaButton.setOnClickListener {
            startActivity(Intent(this, MapsActivity::class.java))
        }

        binding.logoutButton.setOnClickListener {
            val sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.clear()
            editor.apply()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        // Inisialisasi ApiService dan StoryRepository
        val apiService = ApiConfig().getApiService()
        val authorizationHeader = getAuthorizationHeader()
        val storyRepository = StoryRepository(apiService, authorizationHeader)

        // Inisialisasi ListStoryViewModel
        viewModel = ViewModelProvider(this, ListStoryViewModelFactory(storyRepository)).get(ListStoryViewModel::class.java)

        // Dapatkan LiveData dari ListStoryViewModel
        val storiesLiveData: LiveData<PagingData<StoryResponse>> = viewModel.getStoriesLiveData()

        // Observing the LiveData dan pembaruan adapter
        storiesLiveData.observe(this) { pagingData ->
            adapter.submitData(lifecycle, pagingData)
        }
    }

    private fun getAuthorizationHeader(): String {
        val sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)
        return "Bearer $token"
    }
}