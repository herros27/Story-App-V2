package com.example.storyapp.view.ui.main

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.storyapp.R
import com.example.storyapp.databinding.ActivityMainBinding
import com.example.storyapp.utils.show
import com.example.storyapp.view.adapter.LoadingStateAdapter
import com.example.storyapp.view.adapter.StoryAdapter
import com.example.storyapp.view.ui.ViewModelFactory
import com.example.storyapp.view.ui.cameraGalery.PhotoActivity
import com.example.storyapp.view.ui.maps.MapsActivity
import com.example.storyapp.view.ui.welcome.WelcomeActivity
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var adapter: StoryAdapter
    private lateinit var rvStory: RecyclerView
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            }
        }

        binding.btnSettings.setOnClickListener {
            val intent = Intent(Settings.ACTION_LOCALE_SETTINGS)
            startActivity(intent)
        }

        binding.btnUpStory.setOnClickListener {
            val intent = Intent(this, PhotoActivity::class.java)
            startActivity(intent)
        }

        rvStory = binding.recyclerView

        setupView()
        playAnimation()
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        setupRecyclerView()
        observeUserEmail()
        observeStories()
        setSupportActionBar(binding.toolbar)
        supportActionBar?.show()
    }

    private fun observeUserEmail() {
        lifecycleScope.launch {
            viewModel.userName().collect { userModel ->
                binding.nameTextView.text = userModel.name
                Log.d("UserModel", "Username: ${userModel.name}")
            }
        }
    }

    private fun setupRecyclerView() {

        rvStory.layoutManager = LinearLayoutManager(this@MainActivity)
        adapter = StoryAdapter()
        rvStory.adapter =adapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                adapter.retry()
            }
        )
    }

    private fun observeStories() {
        viewModel.storiesPage.observe(this){ pagingData ->
            if(pagingData == null){
                binding.progressBar.show(true)
                binding.noInternet.show(false)
                binding.tvEmpty.show(true)
                return@observe
            }else{
                binding.progressBar.show(false)
                binding.noInternet.show(false)
                binding.tvEmpty.show(false)
                adapter.submitData(lifecycle, pagingData)
            }

        }

    }

    private fun playAnimation() {
        
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -200f, 200f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val randomRotationAnimator = ObjectAnimator.ofFloat(binding.imageView, View.ROTATION, 0f, 360f).apply {
            duration = 3000 
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }

        val name = ObjectAnimator.ofFloat(binding.nameTextView, View.ALPHA, 1f).setDuration(100)
        val message = ObjectAnimator.ofFloat(binding.messageTextView, View.ALPHA, 1f).setDuration(100)

        AnimatorSet().apply {
            playSequentially(name, message)
            playTogether(randomRotationAnimator)
            startDelay = 100
        }.start()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                viewModel.logout()
                true
            }
            R.id.action_maps -> {
                val intent = Intent(this, MapsActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}