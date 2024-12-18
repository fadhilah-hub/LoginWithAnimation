package com.dicoding.picodiploma.loginwithanimation.view.detail

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityDetailBinding
import com.dicoding.picodiploma.loginwithanimation.view.ViewModelFactory

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private lateinit var detailViewModel: DetailViewModel
    private lateinit var storyId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        storyId = intent.getStringExtra("EXTRA_STORY_ID") ?: ""
        val factory = ViewModelFactory.getInstance(this)
        detailViewModel = ViewModelProvider(this, factory)[DetailViewModel::class.java]

        detailViewModel.fetchStoryDetail(storyId)

        observeViewModel()

    }

    private fun observeViewModel() {
        detailViewModel.storyDetail.observe(this, Observer {
            story ->
            binding.tvDetailName.text = story.name
            binding.tvDetailDescription.text = story.description

            Glide.with(this)
                .load(story.photoUrl)
                .into(binding.ivDetailPhoto)
        })
        detailViewModel.error.observe(this, Observer {
            message ->
            if (message.isNotEmpty()){
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        })
    }
}