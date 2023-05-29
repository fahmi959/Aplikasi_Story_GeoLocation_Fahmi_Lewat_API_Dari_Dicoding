package com.fahmi.aplikasistoryfahmi.Story_UserInterface

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.bumptech.glide.Glide
import com.fahmi.aplikasistoryfahmi.*
import com.fahmi.aplikasistoryfahmi.databinding.ActivityDetailStoryBinding

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailStoryBinding

    private fun getToken(): String? {
        val sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("token", null)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val id = intent.getStringExtra("STORY_ID")
        val token = getToken()

        if (token != null) {
            val authorizationHeader = "Bearer $token"
            val apiConfig = ApiConfig()
            val service = apiConfig.getApiService()

            if (id != null) {
                Log.d("DetailStoryActivity", "Story ID: $id")
                service.getStorybyId(authorizationHeader, id)
                    .enqueue(object : Callback<StoryResponsez> {
                        override fun onResponse(
                            call: Call<StoryResponsez>,
                            response: Response<StoryResponsez>
                        ) {
                            if (response.isSuccessful) {
                                val story = response.body()?.story
                                Toast.makeText(this@DetailStoryActivity, "BERHASIL", Toast.LENGTH_SHORT).show()
                                if (story != null) {
                                    binding.tvId.text = story.id
                                    binding.tvName.text = story.name
                                    binding.tvDescription.text = story.description
                                    Glide.with(this@DetailStoryActivity)
                                        .load(story.photoUrl)
                                        .into(binding.imgStory)
                                    binding.tvLat.text = story.lat?.toString() ?: ""
                                    binding.tvLon.text = story.lon?.toString() ?: ""
                                    binding.tvCreatedAt.text = story.createdAt
                                }
                            } else {
                                Toast.makeText(this@DetailStoryActivity, "Failed to get story detail: ${response.code()}", Toast.LENGTH_SHORT).show()
                                throw Exception("Failed to get story detail: ${response.code()}")
                            }
                        }

                        override fun onFailure(call: Call<StoryResponsez>, t: Throwable) {
                            Toast.makeText(this@DetailStoryActivity, "Failed to get story detail: ${t.message}", Toast.LENGTH_SHORT).show()
                            Log.e("DetailStoryActivity", "Failed to get story detail", t)
                        }
                    })
            }
        }

        binding.logoutButton.setOnClickListener{
            val sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.clear()
            editor.apply()
            startActivity(Intent(this, MainActivity::class.java))
            finish()}
    }
}
