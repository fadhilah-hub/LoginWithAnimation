package com.dicoding.picodiploma.loginwithanimation.view.story

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dicoding.picodiploma.loginwithanimation.data.StoryRepository
import com.dicoding.picodiploma.loginwithanimation.data.response.AddResponse
import com.dicoding.picodiploma.loginwithanimation.data.response.ListStoryItem
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

class ListStoryViewModel (private val storyRepository: StoryRepository): ViewModel() {

    /**private val _stories = MutableLiveData<List<ListStoryItem>>()
    val stories: LiveData<List<ListStoryItem>> = _stories**/

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val _uploadResult = MutableLiveData<Result<AddResponse>>()
    val uploadResult: LiveData<Result<AddResponse>> get() = _uploadResult


    /**fun fetchStories() {
        viewModelScope.launch {
            try {
                val storyList = storyRepository.getStories()
                _stories.value = storyList
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }**/

    val stories: LiveData<PagingData<ListStoryItem>> =
        storyRepository.getStory().cachedIn(viewModelScope)


}