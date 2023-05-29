package com.fahmi.aplikasistoryfahmi

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.fahmi.aplikasistoryfahmi.Story_UserInterface.ListStoryActivity
import com.fahmi.aplikasistoryfahmi.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)



        binding.btnMasuk.setOnClickListener{
            startActivity(Intent(this, LoginActivity::class.java))
        }

        binding.btnDaftar.setOnClickListener{
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        // MAINKAN ANIMASI
        playAnimation()

    }

    // FUNGSI UNTUK ANIMASI
    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.gifFahmiz, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val login = ObjectAnimator.ofFloat(binding.teksviewMulaiJudul, View.ALPHA, 1f).setDuration(500)
        val signup = ObjectAnimator.ofFloat(binding.teksviewMulaiSubJudul, View.ALPHA, 1f).setDuration(500)
        val signupz = ObjectAnimator.ofFloat(binding.gifFahmiz, View.ALPHA, 1f).setDuration(500)
        val title = ObjectAnimator.ofFloat(binding.btnMasuk, View.ALPHA, 1f).setDuration(500)
        val desc = ObjectAnimator.ofFloat(binding.btnDaftar, View.ALPHA, 1f).setDuration(500)

        val together = AnimatorSet().apply {
            playTogether(title, desc)
        }

        AnimatorSet().apply {
            playSequentially(login, signup, signupz, together)
            start()
        }
    }


    // JIKA KELUAR APLIKASI LALU KEMBALI KE APLIKASI MAKA INI ADALAH ACTIVITY YANG AKAN DITAMPILKAN
//    override fun onStart() {
//        super.onStart()
//        if (auth.currentUser != null){
//            Intent(this, ListStoryActivity::class.java).also {
//                it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                startActivity(it)
//            }
//        }
//    }
    override fun onStart() {
        super.onStart()
        val sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val loggedIn = sharedPreferences.getBoolean("loggedIn", false)

        if (loggedIn) {
            startActivity(Intent(this, ListStoryActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
            finish()
        }
    }




}
