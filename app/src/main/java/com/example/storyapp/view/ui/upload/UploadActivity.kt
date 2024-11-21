package com.example.storyapp.view.ui.upload

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.storyapp.R
import com.example.storyapp.databinding.ActivityUploadBinding
import com.example.storyapp.utils.ResultStories
import com.example.storyapp.utils.reduceFileImage
import com.example.storyapp.utils.uriToFile
import com.example.storyapp.view.ui.ViewModelFactory
import com.example.storyapp.view.ui.main.MainActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class UploadActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUploadBinding
    private val viewModel by viewModels<UploadViewModel>{
        ViewModelFactory.getInstance(this)
    }

    private var currentImageUri: Uri? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d("UploadActivity", "onCreate called uri : ${intent.getStringExtra(EXTRA_IMAGE_URI)}")
        currentImageUri = Uri.parse((intent.getStringExtra(EXTRA_IMAGE_URI)))

        binding.ivContent.setImageURI(currentImageUri)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        setupAction()
    }

    private fun uploadImage(lan:Float?,lon:Float?) {
        currentImageUri?.let {

            val imageFile = uriToFile(it, this).reduceFileImage()
            val description = binding.edAddDescription.text.toString()

            if (description.isEmpty()) {
                showToast(getString(R.string.description))
                binding.btnUpStory.isEnabled = true
            }else{
                binding.btnUpStory.isEnabled = false
                viewModel.uploadStory(description, imageFile,lan,lon).observe(this) { response ->
                    if (response != null) {
                        when(response) {
                            is ResultStories.Loading -> {
                                showLoading(true)
                            }
                            is ResultStories.Success -> {
                                showLoading(false)
                                showToast(getString(R.string.succes_upload))
                                val intent = Intent(this, MainActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(intent)
                                finish()                         }
                            is ResultStories.Error -> {
                                showLoading(false)
                                showToast(response.error)
                            }
                        }
                    }
                }
            }

        } ?: showToast(getString(R.string.nothing_image_selected))
    }

    private fun setupAction(){
        binding.btnUpStory.setOnClickListener {
            val includeLocation = binding.switchIncludeLocation?.isChecked
            if (includeLocation == true) {
                getLocationAndUploadStory()
            } else {
                uploadImage(0f, 0f) 
            }

        }

    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun getLocationAndUploadStory() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    
                    val lat = it.latitude
                    val lon = it.longitude

                    
                    uploadImage(lat.toFloat(), lon.toFloat())

                } ?: run {
                    showToast(getString(R.string.location_not_available))
                }
            }
        } else {
            
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE

            )
            binding.btnUpStory.isEnabled = true
        }
    }

    companion object {
        private const val EXTRA_IMAGE_URI = "imageUri"
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
                fun createIntent(context: Context, imageUri: Uri): Intent {
            return Intent(context, UploadActivity::class.java).apply {
                putExtra(EXTRA_IMAGE_URI, imageUri.toString())
            }
        }
    }
}