package com.example.storyapp.view.ui.maps

import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.example.storyapp.R
import com.example.storyapp.databinding.ActivityMapsBinding
import com.example.storyapp.utils.ResultStories
import com.example.storyapp.utils.getCircularBitmap
import com.example.storyapp.utils.show
import com.example.storyapp.utils.showToast
import com.example.storyapp.view.ui.ViewModelFactory
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions

@Suppress("DEPRECATION")
class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private val viewModel by viewModels<MapsViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        binding.toolbar.overflowIcon?.setTint(ContextCompat.getColor(this, android.R.color.white))
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_back)
        }
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        viewModel.getStories()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        setMapStyle()
        getMyLocation()
        addManyMarker()
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getMyLocation()
            }
        }
    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED)
        {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            fusedLocationClient.lastLocation
            .addOnCompleteListener { task ->

                if (task.isSuccessful && task.result != null) {
                    val location = task.result

                    val latitude = location.latitude
                    val longitude = location.longitude

                    Log.d("MyLocation", "Latitude: $latitude, Longitude: $longitude")
                    mMap.isMyLocationEnabled = true
                    val currentLocation = LatLng(latitude, longitude)
//                    mMap.addMarker(MarkerOptions().position(currentLocation).title("Your Location"))
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 12f))
                } else {
                    Log.e("MyLocation", "Failed to get location")
                }
            }
        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun setMapStyle(){
        try {
            val success =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", exception)
        }
    }

    private fun addManyMarker(){
        viewModel.stories.observe(this) { result ->
            when (result) {
                is ResultStories.Loading -> {
                    binding.progressIndicator.show(true)
                }

                is ResultStories.Success -> {
                    binding.progressIndicator.show(false)
                    result.data?.listStory?.forEach { data ->
                        val latLng = data.lat?.let { data.lon?.let { it1 -> LatLng(it, it1) } }

                        val markerWidth = 150
                        val markerHeight = 150
                        Glide.with(this)
                            .asBitmap()
                            .load(data.photoUrl)
                            .override(markerWidth, markerHeight)
                            .into(object : SimpleTarget<Bitmap>() {
                                override fun onResourceReady(
                                    resource: Bitmap,
                                    transition: Transition<in Bitmap>?
                                ) {
                                    val circularBitmap = getCircularBitmap(resource)
                                    val imageMarkerOptions = latLng?.let {
                                        MarkerOptions()
                                            .position(it)
                                            .title(data.name)
                                            .snippet(data.description)
                                            .icon(BitmapDescriptorFactory.fromBitmap(circularBitmap))
                                    }
                                    val marker = imageMarkerOptions?.let { mMap.addMarker(it) }
                                    marker?.tag = data.photoUrl
                                }
                            })
                    }
                }

                is ResultStories.Error -> {
                    binding.progressIndicator.show(false)
                    showToast(this, result.error)
                }
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.map_option, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressedDispatcher.onBackPressed()
                true
            }
            R.id.normal_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
                true
            }
            R.id.satellite_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
                true
            }
            R.id.terrain_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
                true
            }
            R.id.hybrid_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_HYBRID
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    companion object {
        private const val TAG = "MapsActivity"
    }
}

