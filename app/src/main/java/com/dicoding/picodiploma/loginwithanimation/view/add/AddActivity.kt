package com.dicoding.picodiploma.loginwithanimation.view.add

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityAddBinding
import com.dicoding.picodiploma.loginwithanimation.utils.getImageUri
import com.dicoding.picodiploma.loginwithanimation.utils.reduceFileImage
import com.dicoding.picodiploma.loginwithanimation.utils.uriToFile
import com.dicoding.picodiploma.loginwithanimation.view.ViewModelFactory
import com.dicoding.picodiploma.loginwithanimation.view.story.ListStoryActivity
import com.dicoding.picodiploma.loginwithanimation.view.story.ListStoryViewModel


class AddActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddBinding
    private var currentImageUri: Uri? = null
    private val addViewModel: AddViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }
    private val listStoryViewModel:ListStoryViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBinding.inflate(layoutInflater)
        setContentView(binding.root)


        observeUploadResult()

        binding.btnCamera.setOnClickListener { startCamera() }
        binding.btnGallery.setOnClickListener { startGallery() }
        binding.btnAdd.setOnClickListener {
            currentImageUri?.let { uri->
                val description = binding.edAddDescription.text.toString()
                upload(description, uri)
            }
        }
        addViewModel.isLoading.observe(this){
            showLoading(it)
        }
        addViewModel.uploadResult.observe(this) {result ->
            result?.let {
                if (it.isSuccess) {
                    showToast("Story berhasil diunggah")
                    //listStoryViewModel.fetchStories()
                    navigateToListStoryActivity()
                } else {
                    showToast("Gagal mengunggah story: ${it.exceptionOrNull()?.message}")
                }
            }
        }

    }

    private fun observeUploadResult() {
        addViewModel.uploadResult.observe(this) {result ->
           if (result.isSuccess) {
               result.getOrNull()?.let { message ->
                   showToast(message)
                   navigateToListStoryActivity()
               }
           } else {
               result.exceptionOrNull()?.let { exception ->
                   showToast("Gagal mengunggah: ${exception.message}")
               }
           }
            showLoading(false)
        }
    }

    private fun navigateToListStoryActivity() {
        val intent = Intent(this, ListStoryActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(intent)
        finish()
    }

    private fun upload(description: String, imageUri: Uri) {
        val context = this
        val imageFile = uriToFile(imageUri, context)
        val reducedImageFile = imageFile.reduceFileImage()
        addViewModel.uploadStory(description,reducedImageFile, context)

    }


    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE

    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
       }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) {
            uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.ivAdd.setImageURI(it)
        }
    }

    private fun startCamera() {
        currentImageUri = getImageUri(this)
        launcherIntentCamera.launch(currentImageUri!!)
    }
    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) {
            isSuccess ->
        if (isSuccess) {
            showImage()
        } else {
            currentImageUri = null
        }
    }
}