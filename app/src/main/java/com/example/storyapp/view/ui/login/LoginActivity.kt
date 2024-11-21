package com.example.storyapp.view.ui.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
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
import com.example.storyapp.data.pref.UserModel
import com.example.storyapp.databinding.ActivityLoginBinding
import com.example.storyapp.utils.EspressoIdlingResource
import com.example.storyapp.utils.show
import com.example.storyapp.view.ui.ViewModelFactory
import com.example.storyapp.view.ui.main.MainActivity

class LoginActivity : AppCompatActivity() {
    private val viewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()

        setupAction()
        playAnimation()

        observeLoginResponse()
        observeErrorMessage()
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

    private fun observeErrorMessage() {
        viewModel.errorMessage.observe(this) { message ->
            message?.let {
                binding.progressIndicator.show(false)
                AlertDialog.Builder(this).apply {
                    setTitle("Error!")
                    setMessage(getString(R.string.masukkan_email_dan_password_yang_sesuai))
                    setPositiveButton("OK", null)
                    create()
                    show()
                }
            }
        }
    }

    private fun observeLoginResponse() {

        viewModel.loginResponse.observe(this) { loginResponse ->
            if (loginResponse != null) {
                if (loginResponse.error == false) {
                    val user = loginResponse.loginResult
                    if (user != null) {
                        viewModel.saveSession(
                            UserModel(
                                user.name.toString(),
                                user.token.toString()
                            )
                        )
                        showLoading(false)

                        AlertDialog.Builder(this, R.style.CustomAlertDialog).apply {
                            setTitle(getString(R.string.success))
                            setMessage(getString(R.string.welcome_login, user.name))
                            setPositiveButton(getString(R.string.next)) { _, _ ->
                                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                intent.flags =
                                    Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(intent)
                                finish()
                            }
                            setCancelable(false)
                            create()
                            show()
                        }
                    }
                }
            }

        }
    }

    private fun setupAction() {
        binding.loginButton.setOnClickListener {
            val email = binding.edLoginEmail.text.toString()
            val pass = binding.edLoginPassword.text.toString()
            if (email.isNotEmpty() && pass.isNotEmpty()) {
                showLoading(true)
                viewModel.login(email, pass)
            } else {
                showToast(getString(R.string.email_and_password_cant_blank))
            }
        }
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.ROTATION, 360f, 0f).apply {
            duration = 100000
            repeatCount = ObjectAnimator.INFINITE
            interpolator = LinearInterpolator()
        }.start()

        val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(100)
        val message =
            ObjectAnimator.ofFloat(binding.messageTextView, View.ALPHA, 1f).setDuration(100)
        val emailTextView =
            ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(100)
        val ed_login_emailLayout =
            ObjectAnimator.ofFloat(binding.edLoginEmailLayout, View.ALPHA, 1f).setDuration(100)
        val passwordTextView =
            ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(100)
        val ed_login_passLayout =
            ObjectAnimator.ofFloat(binding.edLoginPasswordLayout, View.ALPHA, 1f).setDuration(100)
        val login = ObjectAnimator.ofFloat(binding.loginButton, View.ALPHA, 1f).setDuration(100)

        AnimatorSet().apply {
            playSequentially(
                title,
                message,
                emailTextView,
                ed_login_emailLayout,
                passwordTextView,
                ed_login_passLayout,
                login
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
