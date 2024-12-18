package com.dicoding.picodiploma.loginwithanimation.view.story

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityListStoryBinding
import com.dicoding.picodiploma.loginwithanimation.view.ViewModelFactory
import com.dicoding.picodiploma.loginwithanimation.view.detail.DetailActivity

class ListStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityListStoryBinding
    private lateinit var adapter: ListStoryAdapter
    private val viewModel: ListStoryViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupRecyclerView()
        //observeViewModel()
        fetchStories()



    }

    /**private fun observeViewModel() {
        lifecycleScope.launchWhenStarted {
            viewModel.stories.colle
        }

        viewModel.error.observe(this, Observer { message ->
            if (message.isNotEmpty()) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                binding.progressBar.visibility = View.GONE
            }
        })

        viewModel.uploadResult.observe(this) {result ->
            result.onSuccess {
                Toast.makeText(this, "Story berhasil diunggah", Toast.LENGTH_SHORT).show()
                fetchStories()
            }
            result.onFailure {
                Toast.makeText(this, "Gagal mengunggah story", Toast.LENGTH_SHORT).show()
            }
        }
    }**/


    private fun fetchStories() {
        adapter.addLoadStateListener { loadState->
            binding.progressBar.visibility = if (loadState.source.refresh is LoadState.Loading) View.VISIBLE else View.GONE

            val errorState = loadState.source.refresh as? LoadState.Error
            errorState?.let {
                Toast.makeText(this, "Error: ${it.error.message}", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.stories.observe(this) {pagingData ->
            adapter.submitData(lifecycle, pagingData)
        }
        /**binding.progressBar.visibility = View.VISIBLE
        adapter.withLoadStateFooter(
            footer = LoadingStateAdapter{
                adapter.retry()
            }
        )
        viewModel.stories.observe(this, {
            adapter.submitData(lifecycle, it)
        })**/
    }



    private fun setupRecyclerView() {
        adapter = ListStoryAdapter{
            story ->
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra("EXTRA_STORY_ID", story.id)
            startActivity(intent)
        }
        binding.rvListStory.layoutManager = LinearLayoutManager(this)
        binding.rvListStory.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter{adapter.retry()}
        )
    }
}