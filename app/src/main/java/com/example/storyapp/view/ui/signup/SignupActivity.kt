package com.example.storyapp.view.ui.signup

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.view.animation.LinearInterpolator
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.storyapp.R
import com.example.storyapp.databinding.ActivitySignupBinding
import com.example.storyapp.utils.RegisterResult
import com.example.storyapp.view.ui.ViewModelFactory

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private val viewModel by viewModels<RegisterViewModel>{
        ViewModelFactory.getInstance(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()
        playAnimation()

        observeRegisterResponse()
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
        supportActionBar?.hide()
    }


    private fun observeRegisterResponse() {
        viewModel.registerResult.observe(this) { result ->
            when (result) {
                is RegisterResult.Success -> {
                    showLoading(false)
                    AlertDialog.Builder(this).apply {
                        setTitle(getString(R.string.success))
                        setMessage(
                            getString(
                                R.string.success_make_account,
                                binding.edRegisterEmail.text
                            )
                        )
                        setPositiveButton(getString(R.string.next)) { _, _ -> finish() }
                        setCancelable(false)
                        create()
                        show()
                    }
                }

                is RegisterResult.Error -> {
                    showLoading(false)
                    val errorMessage =
                        result.errorResponse.message ?: getString(R.string.error_occurred_try_again)
                    showToast(errorMessage)
                }
            }
        }


    }

    private fun setupAction() {
        binding.signupButton.setOnClickListener {
            val email = binding.edRegisterEmail.text.toString()
            val password = binding.edRegisterPassword.text.toString()
            val name = binding.edRegisterName.text.toString()

            if (name.isNotEmpty()&& email.isNotEmpty() && password.isNotEmpty()){
                showLoading(true)
                viewModel.register(name, email, password)
            }else{
                showToast(getString(R.string.must_be_filled))
            }
        }
    }

    
    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.ROTATION, 360f, 0f).apply {
            duration = 10000 
            repeatCount = ObjectAnimator.INFINITE
            interpolator = LinearInterpolator() 
        }.start()

        val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(100)
        val nameTextView =
            ObjectAnimator.ofFloat(binding.nameTextView, View.ALPHA, 1f).setDuration(100)
        val edRegisterNameLayout =
            ObjectAnimator.ofFloat(binding.edRegisterNameLayout, View.ALPHA, 1f).setDuration(100)
        val emailTextView =
            ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(100)
        val edRegisterEmailLayout =
            ObjectAnimator.ofFloat(binding.edRegisterEmailLayout, View.ALPHA, 1f).setDuration(100)
        val passwordTextView =
            ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(100)
        val edRegisterPasswordLayout =
            ObjectAnimator.ofFloat(binding.edRegisterPasswordLayout, View.ALPHA, 1f).setDuration(100)
        val signup = ObjectAnimator.ofFloat(binding.signupButton, View.ALPHA, 1f).setDuration(100)


        AnimatorSet().apply {
            playSequentially(
                title,
                nameTextView,
                edRegisterNameLayout,
                emailTextView,
                edRegisterEmailLayout,
                passwordTextView,
                edRegisterPasswordLayout,
                signup
            )
            startDelay = 100
        }.start()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}