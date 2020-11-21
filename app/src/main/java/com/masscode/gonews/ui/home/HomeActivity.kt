package com.masscode.gonews.ui.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.masscode.gonews.data.Resource
import com.masscode.gonews.databinding.ActivityHomeBinding
import com.masscode.gonews.ui.adapter.ArticleAdapter
import com.masscode.gonews.ui.base.ViewModelFactory
import timber.log.Timber

class HomeActivity : AppCompatActivity() {

    private lateinit var homeViewModel: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val articleAdapter = ArticleAdapter()
        val factory = ViewModelFactory.getInstance(this)

        homeViewModel = ViewModelProvider(this, factory).get(HomeViewModel::class.java).apply {
            articles.observe(this@HomeActivity, { articles ->
                Timber.d(articles.data?.size.toString())
                if (articles != null) {
                    when (articles) {
                        is Resource.Loading -> binding.progressBar.visibility = View.VISIBLE
                        is Resource.Success -> {
                            binding.progressBar.visibility = View.GONE
                            articleAdapter.setData(articles.data)
                        }
                        is Resource.Error -> {
                            binding.progressBar.visibility = View.GONE
                            binding.viewError.visibility = View.VISIBLE
                        }
                    }
                }
            })
        }

        binding.rvArticle.apply {
            layoutManager = LinearLayoutManager(this@HomeActivity)
            setHasFixedSize(true)
            adapter = articleAdapter
        }
    }
}