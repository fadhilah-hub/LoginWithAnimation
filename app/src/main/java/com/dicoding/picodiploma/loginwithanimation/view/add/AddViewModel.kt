package com.dicoding.picodiploma.loginwithanimation.view.add

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.picodiploma.loginwithanimation.data.StoryRepository
import com.dicoding.picodiploma.loginwithanimation.utils.reduceFileImage
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class AddViewModel (private val storyRepository: StoryRepository): ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _uploadResult = MutableLiveData<Result<String>>()
    val uploadResult: LiveData<Result<String>> = _uploadResult

    fun uploadStory(description: String, imageFile: File, context: Context){

        val descriptionBody = description.toRequestBody("text/plain".toMediaType())
        val photoBody = imageFile.asRequestBody("image/*".toMediaType())
        val photoPart = MultipartBody.Part.createFormData("photo", imageFile.name, photoBody)

        viewModelScope.launch {
            _isLoading.value = true
            try{
                val token = storyRepository.getToken()

                val reducedImageFile = imageFile.reduceFileImage()

                val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val photoMultipart = MultipartBody.Part.createFormData(
                    "photo",
                    imageFile.name,
                    requestImageFile
                )
                val descRequestBody = description.toRequestBody("text/plain".toMediaTypeOrNull())
                val response = storyRepository.addStory(descRequestBody, photoMultipart)

                if (response.isSuccessful) {
                    _uploadResult.value = Result.success("Story berhasil diunggah")
                } else {
                    _uploadResult.value = Result.failure(Exception(response.message()))
                }

            } catch (e: Exception) {
                _uploadResult.value = Result.failure(e)
            } finally {
                _isLoading.value = false
            }
        }
    }
}