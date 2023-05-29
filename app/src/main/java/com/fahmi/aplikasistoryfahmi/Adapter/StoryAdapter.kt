package com.fahmi.aplikasistoryfahmi.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fahmi.aplikasistoryfahmi.R
import com.fahmi.aplikasistoryfahmi.StoryResponse
import com.fahmi.aplikasistoryfahmi.databinding.ItemStoryBinding

import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil

class StoryAdapter : PagingDataAdapter<StoryResponse, StoryAdapter.ViewHolder>(StoryDiffCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val story = getItem(position)
        story?.let { holder.bind(it) }
    }

    inner class ViewHolder(private val binding: ItemStoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(story: StoryResponse) {
            binding.tvStoryTitle.text = story.name
            binding.tvStoryDescription.text = story.description
            Glide.with(binding.root.context)
                .load(story.photoUrl)
                .centerCrop()
                .placeholder(R.drawable.ic_place_holder)
                .into(binding.ivStoryPhoto)
            binding.tvLatitude.text = story.lat.toString()
            binding.tvLongitude.text = story.lon.toString()

            binding.tombolUntukLihatDetail.setOnClickListener {
                listener?.onItemClick(story)
            }
        }
    }


    private var listener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(story: StoryResponse)
    }

    fun LihatDetail(listener: OnItemClickListener) {
        this.listener = listener
    }

    class StoryDiffCallback : DiffUtil.ItemCallback<StoryResponse>() {
        override fun areItemsTheSame(oldItem: StoryResponse, newItem: StoryResponse): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: StoryResponse, newItem: StoryResponse): Boolean {
            return oldItem == newItem
        }
    }

    companion object {
     val DIFF_CALLBACK = object : DiffUtil.ItemCallback<StoryResponse>() {
            override fun areItemsTheSame(oldItem: StoryResponse, newItem: StoryResponse): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: StoryResponse, newItem: StoryResponse): Boolean {
                return oldItem == newItem
            }
        }
    }
}
