package com.fahmi.aplikasistoryfahmi.Story_UserInterface

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.fahmi.aplikasistoryfahmi.*
import com.fahmi.aplikasistoryfahmi.databinding.ActivityTambahStoryBinding
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import com.fahmi.aplikasistoryfahmi.ApiConfig
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*
import java.util.*


class TambahStoryActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityTambahStoryBinding
    private var selectedImage: Uri? = null
    private lateinit var mMap: GoogleMap

        companion object {
        const val CAMERA_X_RESULT = 200
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTambahStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Permission Set listener untuk tombol pilih gambar
            if (!allPermissionsGranted()) {
                ActivityCompat.requestPermissions(
                    this,
                    TambahStoryActivity.REQUIRED_PERMISSIONS,
                    TambahStoryActivity.REQUEST_CODE_PERMISSIONS
                )
            }
            binding.cameraXButton.setOnClickListener { startCameraX() }
            binding.cameraButton.setOnClickListener { startTakePhoto() }
            binding.btnPilihGambar.setOnClickListener { startGallery() }

        // Initialize the map
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


        // Set listener untuk tombol tambah cerita
        binding.btnTambahCerita.setOnClickListener {
            // Panggil fungsi untuk mengambil data cerita dari API dan dengan TOKEN AUTHORIZATION AUTHENTICATION
            val sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
            val token = sharedPreferences.getString("token", null)
            if (token != null) {
                val authorizationHeader = "Bearer $token"
                uploadStory(authorizationHeader)
            } else {
                Toast.makeText(this@TambahStoryActivity, "Token Tidak Ada", Toast.LENGTH_SHORT).show()
            }
            // Validasi input
            val description = binding.edAddDescription.text.toString()
            if (description.isBlank()) {
                binding.edAddDescription.error = "Deskripsi tidak boleh kosong"
                return@setOnClickListener
            }
            if (selectedImage != null) {
                Toast.makeText(this, "Gambar Masuk", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

        }

        // AKTIFKAN SERTA PAKAI LOKASI SAYA SAAT INI

        binding.switchItemLokasi.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // Switch dinyalakan, lakukan tindakan yang diinginkan
                getMyLocation()
//                binding.edAddLatitude.setText("-1.8")
//                binding.edAddLongitude.setText("-1.9")
                binding.edAddLatitude.isFocusable = false
                binding.edAddLatitude.isFocusableInTouchMode = false
                binding.edAddLongitude.isFocusable = false
                binding.edAddLongitude.isFocusableInTouchMode = false
                binding.edAddLatitude.setTextColor(ContextCompat.getColor(this@TambahStoryActivity, R.color.white))
                binding.edAddLongitude.setTextColor(ContextCompat.getColor(this@TambahStoryActivity, R.color.white))
                binding.edAddLatitude.setBackgroundColor(ContextCompat.getColor(this@TambahStoryActivity, R.color.pink_200))
                binding.edAddLongitude.setBackgroundColor(ContextCompat.getColor(this@TambahStoryActivity, R.color.pink_200))
                Toast.makeText(this@TambahStoryActivity, "Pengambilan Lokasi Dinyalakan", Toast.LENGTH_SHORT).show()
            } else {
                // Switch dimatikan, lakukan tindakan yang diinginkan
                binding.edAddLatitude.setText(null)
                binding.edAddLongitude.setText(null)
                binding.edAddLatitude.isFocusable = true
                binding.edAddLatitude.isFocusableInTouchMode = true
                binding.edAddLongitude.isFocusable = true
                binding.edAddLongitude.isFocusableInTouchMode = true
                binding.edAddLatitude.setTextColor(ContextCompat.getColor(this@TambahStoryActivity, R.color.pink_500))
                binding.edAddLongitude.setTextColor(ContextCompat.getColor(this@TambahStoryActivity, R.color.pink_500))
                binding.edAddLatitude.setBackgroundResource(R.drawable.edittext_underline)
                binding.edAddLongitude.setBackgroundResource(R.drawable.edittext_underline)
                Toast.makeText(this@TambahStoryActivity, "Pengambilan Lokasi Dimatikan", Toast.LENGTH_SHORT).show()
            }
        }

    }




    private fun allPermissionsGranted() = TambahStoryActivity.REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    // MEMUNCULKAN HASIL KAMERA X
    private fun startCameraX() {
        val intent = Intent(this, KameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    // MENGAMBIL GAMBAR DARI KAMERA
    private fun startTakePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)

        createCustomTempFile(application).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                this@TambahStoryActivity,
                // SESUAIKAN DENGAN PACKAGE
                "com.fahmi.aplikasistoryfahmi",
                it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intent)
        }


    }

    // MENGAMBIL FOTO ATAU GAMBAR DARI GALLERY
    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
        Toast.makeText(this, "Berhasil Membuka Gallery", Toast.LENGTH_SHORT).show()
    }

    // VALIDASI UNGGAH ATAU UPLOAD GAMBAR KE SERVER
    private var getFile: File? = null
    private fun uploadStory(authorizationHeader: String) {
        if (getFile != null) {
            val file = com.fahmi.aplikasistoryfahmi.reduceFileImage(getFile as File)
// INPUTANNYA
            val description = binding.edAddDescription.text.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                file.name,
                requestImageFile
            )

            val latString = binding.edAddLatitude.text.toString()
            val lonString = binding.edAddLongitude.text.toString()
            val lat = if (latString.isNotEmpty()) latString.toRequestBody("text/plain".toMediaTypeOrNull()) else null
            val lon = if (lonString.isNotEmpty()) lonString.toRequestBody("text/plain".toMediaTypeOrNull()) else null
// RETROFITNYA
            val service = ApiConfig().getApiService().addNewStory(authorizationHeader, description, lat, lon, imageMultipart)
            service.enqueue(object : Callback<ApiResponse<StoryResponse>> {
                override fun onResponse(
                    call: Call<ApiResponse<StoryResponse>>,
                    response: Response<ApiResponse<StoryResponse>>
                ) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody != null && !responseBody.error) {
                            Toast.makeText(
                                this@TambahStoryActivity,
                                responseBody.message,
                                Toast.LENGTH_SHORT
                            ).show()
                            startActivity(Intent(this@TambahStoryActivity, ListStoryActivity::class.java))
                        } else {
                            Toast.makeText(
                                this@TambahStoryActivity,
                                "Error Response",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            this@TambahStoryActivity,
                            response.message(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<ApiResponse<StoryResponse>>, t: Throwable) {
                    Toast.makeText(
                        this@TambahStoryActivity,
                        "Gagal instance Retrofit",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        } else {
            Toast.makeText(
                this@TambahStoryActivity,
                "Silakan masukkan berkas gambar terlebih dahulu.", Toast.LENGTH_SHORT).show()
        }
    }


    // INTENT KAMERA X
    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == TambahStoryActivity.CAMERA_X_RESULT) {
            val myFile = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                it.data?.getSerializableExtra("picture", File::class.java)
            } else {
                @Suppress("DEPRECATION")
                it.data?.getSerializableExtra("picture")
            } as? File

            val isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean

            myFile?.let { file ->
                rotateFile(file, isBackCamera)
                getFile = file
                binding.imgAddStory.setImageBitmap(BitmapFactory.decodeFile(file.path))
            }
        }
    }

    //FUNGSI ROTASI KAMERA DI KAMERA X
    fun rotateFile(file: File, isBackCamera: Boolean) {
        try {
            val exifInterface = ExifInterface(file.path)
            val rotation: Int = when {
                isBackCamera -> ExifInterface.ORIENTATION_ROTATE_90
                else -> ExifInterface.ORIENTATION_ROTATE_270
            }
            exifInterface.setAttribute(ExifInterface.TAG_ORIENTATION, rotation.toString())
            exifInterface.saveAttributes()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }




    // INTENT KAMERA
    private lateinit var currentPhotoPath: String
    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val myFile = File(currentPhotoPath)

            // KODE DIBAWAH INI UNTUK KIRIM KE SERVER RETROFIT APISERVICE
            getFile = myFile

            val result = BitmapFactory.decodeFile(myFile.path)
            binding.imgAddStory.setImageBitmap(result)
        }
    }

//    // KOMPILASI Camera menjadi Format Template File
//    private fun createCustomTempFile(context: Context): File {
//        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
//        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
//        return File.createTempFile(
//            "JPEG_${timeStamp}_",
//            ".jpg",
//            storageDir
//        )
//    }

    // INISIALISASI FUNGSI KETIKA MEMANGGIL KAMERA ACTIVITY
    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImg, this@TambahStoryActivity)


            // KODE DIBAWAH INI UNTUK KIRIM KE SERVER RETROFIT APISERVICE
            getFile = myFile

            binding.imgAddStory.setImageURI(selectedImg)
        }
    }



    // FUNGSI DAN PERMISSION LOKASI SAYA SAAT INI KETIKA DI PAKAI ATAU DI AKTIFKAN
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
                    binding.edAddLatitude.setText(location.latitude.toString())
                    binding.edAddLongitude.setText(location.longitude.toString())
                }
            }
        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        // Inisialisasi mMap di sini
        // ...
    }




}