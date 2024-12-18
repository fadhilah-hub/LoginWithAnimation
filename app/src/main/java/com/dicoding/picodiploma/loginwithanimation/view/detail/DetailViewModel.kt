package com.dicoding.picodiploma.loginwithanimation.view.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.picodiploma.loginwithanimation.data.StoryRepository
import com.dicoding.picodiploma.loginwithanimation.data.response.Story
import kotlinx.coroutines.launch

class DetailViewModel (private val storyRepository: StoryRepository) : ViewModel() {

    private val _storyDetail = MutableLiveData<Story>()
    val storyDetail: LiveData<Story> = _storyDetail

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun fetchStoryDetail(storyId: String) {
        viewModelScope.launch {
            try {
                val story = storyRepository.getStoryDetail(storyId)
                _storyDetail.value = story // Set data story ke LiveData
            } catch (e: Exception) {
                _error.value = e.message // Set error ke LiveData jika gagal
            }
        }
    }
}