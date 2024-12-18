package com.dicoding.picodiploma.loginwithanimation.data

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserPreference
import com.dicoding.picodiploma.loginwithanimation.data.response.AddResponse
import com.dicoding.picodiploma.loginwithanimation.data.response.ListStoryItem
import com.dicoding.picodiploma.loginwithanimation.data.response.Story
import com.dicoding.picodiploma.loginwithanimation.data.retrofit.ApiConfig
import com.dicoding.picodiploma.loginwithanimation.data.retrofit.ApiService
import kotlinx.coroutines.flow.first
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response

class StoryRepository private constructor (
    private val apiService: ApiService,
    private val userPreference: UserPreference
) {
    private val storiesCache  = mutableListOf<ListStoryItem>()


    companion object {
        @Volatile
        private var instance: StoryRepository? = null

        fun getInstance(apiService: ApiService, userPreference: UserPreference): StoryRepository {
            return instance ?: synchronized(this) {
                instance ?: StoryRepository(apiService, userPreference).also { instance = it }
            }
        }
    }

    /**suspend fun getStories(): List<ListStoryItem> {
        val token = getToken()
        val apiService = ApiConfig.getApiService(token)
        val response = apiService.getStories()

        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                storiesCache.clear()
                storiesCache.addAll(body.listStory)
                return body.listStory // Akses listStory dari body
            } else {
                throw Exception("Response body is null")
            }
        } else {
            throw Exception("Failed to fetch stories: ${response.message()}")
        }

    }**/

    fun getStory(): LiveData<PagingData<ListStoryItem>> {
        return Pager (
            config = PagingConfig(
                pageSize = 5
            ),
            pagingSourceFactory = {
                StoryPagingSource(apiService)
            }
        ).liveData
    }


    suspend fun getStoryDetail(storyId: String): Story {
        val token = getToken()
        val apiService = ApiConfig.getApiService(token)
        val response = apiService.getStoryDetail(storyId)

        if (response.isSuccessful) {
            val body = response.body()
            if (body != null && !body.error) {
                return body.story
            } else {
                throw Exception("Error: ${body?.message}")
            }
        } else {
            throw Exception("Failed to fetch story detail: ${response.message()}")
        }
    }

    suspend fun getToken(): String {
        return userPreference.getToken().first()
    }

    suspend fun addStory(
        description: RequestBody,
        photo: MultipartBody.Part
    ): Response<AddResponse> {
        val token = getToken()
        val apiService = ApiConfig.getApiService(token)
        val response = apiService.addStory(description, photo)
        if (response.isSuccessful) {
            val body = response.body()
            if (body != null && !body.error) {

                val newStory = ListStoryItem(
                    id = "new_story_id",
                    name = "User",
                    description = description.toString(),
                    photoUrl = "Uploaded URL",
                    createdAt = System.currentTimeMillis().toString(),
                    lon = 0.0,
                    lat = 0.0
                )
                storiesCache.add(0, newStory)
            }
            return response
        } else {
            throw Exception("Failed to add story: ${response.message()}")
        }

    }



}