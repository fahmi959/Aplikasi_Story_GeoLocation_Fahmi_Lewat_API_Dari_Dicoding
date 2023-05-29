package com.fahmi.aplikasistoryfahmi

import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.fahmi.aplikasistoryfahmi.databinding.ActivityMapsBinding
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    //use live template logt to create this
    companion object {
        private const val TAG = "MapsActivity"
    }

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)



        // Panggil fungsi untuk mengambil data cerita dari API dan dengan TOKEN AUTHORIZATION AUTHENTICATION
        val sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)
        if (token != null) {
            val authorizationHeader = "Bearer $token"
            GetApiLokasiPengguna(authorizationHeader, null, null, null)
            Toast.makeText(this@MapsActivity, "Token Tersedia", Toast.LENGTH_SHORT).show()
        }
        else{
            Toast.makeText(this@MapsActivity, "Token Tidak Ada", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        addManyMarker()
        // UNTUK MENGUBAH STYLE MAP
        setMapStyle()

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        // Add a marker in Sydney and move the camera
        val kos_fahmi = LatLng(-7.046016, 110.38999)
        mMap.addMarker(
            MarkerOptions()
                .position(kos_fahmi)
                .title("Tanda Kos Lord Fahmi Gunungpati UNNES Semarang")
                .snippet("Fajar Laundry Ni Boss ! No 990 Mantap .")
        )


        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(kos_fahmi, 15f))
//        val kos_fahmi = LatLng(-7.046016, 110.38999)
//        mMap.addMarker(MarkerOptions().position(kos_fahmi).title("Tanda Gunungpati UNNES Semarang"))
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(kos_fahmi))

        mMap.setOnMapLongClickListener { latLng ->
            mMap.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title("Tanda Penanda Saja")
                    .snippet("Lat: ${latLng.latitude} Long: ${latLng.longitude}")
                    .icon(vectorToBitmap(R.drawable.ic_close_black_24dp, Color.parseColor("#3DDC84")))
            )
        }

        mMap.setOnPoiClickListener { pointOfInterest ->
            val poiMarker = mMap.addMarker(
                MarkerOptions()
                    .position(pointOfInterest.latLng)
                    .title(pointOfInterest.name)
                    .snippet("Lat: ${pointOfInterest.latLng.latitude} Long: ${pointOfInterest.latLng.longitude}")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA))
            )
            poiMarker?.showInfoWindow()
        }


    }

    data class TourismPlace(
        val name: String,
        val latitude: Double,
        val longitude: Double
    )

    private fun addManyMarker() {
        val tourismPlace = listOf(
            TourismPlace("Floating Market Lembang", -6.8168954,107.6151046),
            TourismPlace("The Great Asia Africa", -6.8331128,107.6048483),
            TourismPlace("Rabbit Town", -6.8668408,107.608081),
            TourismPlace("Alun-Alun Kota Bandung", -6.9218518,107.6025294),
            TourismPlace("Orchid Forest Cikole", -6.780725, 107.637409),
            TourismPlace("JOK Motor Kesambi Cirebon", -6.733298805639718, 108.553820438683),
        )
        tourismPlace.forEach { tourism ->
            val latLng = LatLng(tourism.latitude, tourism.longitude)
            mMap.addMarker(MarkerOptions()
                .position(latLng)
                .title(tourism.name)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
            )
        }
    }



    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.map_options, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
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
            R.id.update_lokasi_saya -> {
                // AKSES LOKASIKU SAAT INI
                getMyLocation()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }


    private fun vectorToBitmap(@DrawableRes id: Int, @ColorInt color: Int): BitmapDescriptor {
        val vectorDrawable = ResourcesCompat.getDrawable(resources, id, null)
        if (vectorDrawable == null) {
            Log.e("BitmapHelper", "Resource not found")
            return BitmapDescriptorFactory.defaultMarker()
        }
        val bitmap = Bitmap.createBitmap(
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
        DrawableCompat.setTint(vectorDrawable, color)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    // FUNGSI AKSES LOKASI SAAT INI
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
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true

            val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
            fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    val myLatLng = LatLng(location.latitude, location.longitude)
                    val marker = mMap.addMarker(
                        MarkerOptions()
                            .position(myLatLng)
                            .title("Lokasi Saya")
                            .snippet("Lat: ${location.latitude}, Lng: ${location.longitude}")
                            .icon(vectorToBitmap(R.drawable.lokasi_saya, Color.parseColor("#3DDC84")))
                    )
                    marker?.showInfoWindow()
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLatLng, 15f))
                }
            }
        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun setMapStyle() {
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

//    // REAL TIME UPDATE LOKASI
//    // Fungsi untuk memulai pembaruan lokasi secara real-time
//    private fun startLocationUpdates() {
//        locationCallback = object : LocationCallback() {
//            override fun onLocationResult(locationResult: LocationResult) {
//                val location = locationResult.lastLocation
//                location?.let {
//                    val myLatLng = LatLng(location.latitude, location.longitude)
//                    val marker = mMap.addMarker(
//                        MarkerOptions()
//                            .position(myLatLng)
//                            .title("Lokasi Saya")
//                            .snippet("Lat: ${location.latitude}, Lng: ${location.longitude}")
//                            .icon(vectorToBitmap(R.drawable.lokasi_saya, Color.parseColor("#3DDC84")))
//                    )
//                    marker?.showInfoWindow()
//                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLatLng, 15f))
//                }
//            }
//        }
//
//        locationRequest = LocationRequest.create().apply {
//            interval = 10000 // Interval pembaruan lokasi dalam milidetik
//            fastestInterval = 5000 // Interval tercepat untuk pembaruan lokasi dalam milidetik
//            priority = LocationRequest.PRIORITY_HIGH_ACCURACY // Prioritas akurasi lokasi tinggi
//        }
//
//        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
//        if (ContextCompat.checkSelfPermission(
//                this.applicationContext,
//                android.Manifest.permission.ACCESS_FINE_LOCATION
//            ) == PackageManager.PERMISSION_GRANTED
//        ) {
//            fusedLocationProviderClient.requestLocationUpdates(
//                locationRequest,
//                locationCallback,
//                Looper.getMainLooper()
//            )
//        }
//    }
//
//    // Fungsi untuk menghentikan pembaruan lokasi
//    private fun stopLocationUpdates() {
//        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
//        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
//    }


    // GET API RETOFIT LOKASI
    private fun GetApiLokasiPengguna(authorizationHeader: String, page: Int?, size: Int?, location: Int?) {
        val apiConfig = ApiConfig()
        val service = apiConfig.getApiService()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = service.getAllStories(authorizationHeader, page, size, location)
                withContext(Dispatchers.Main) {
                    if (!response.error) {
                        val listStory = response.listStory
                        for (story in listStory) {
                            val latLng = LatLng(story.lat.toDouble(), story.lon.toDouble())
                            mMap.addMarker(MarkerOptions()
                                .position(latLng)
                                .title(story.name)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
                            )
                        }
                    } else {
                        Toast.makeText(this@MapsActivity, response.message, Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MapsActivity, "Failed to connect to API: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}