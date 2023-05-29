package com.fahmi.aplikasistoryfahmi.Story_UserInterface

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ListStoryViewModelFactory(private val storyRepository: StoryRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ListStoryViewModel::class.java)) {
            return ListStoryViewModel(storyRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}