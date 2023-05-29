package com.fahmi.aplikasistoryfahmi

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.fahmi.aplikasistoryfahmi.Story_UserInterface.ListStoryActivity
import com.fahmi.aplikasistoryfahmi.databinding.ActivityLoginBinding
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@SuppressLint("CheckResult")
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tidakPunyaAkun.setOnClickListener {
            startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
        }

        // Validasi Username
        val usernameStream = RxTextView.textChanges(binding.editTeksLoginEmail)
            .skipInitialValue()
            .map { username -> username.isEmpty() or !Patterns.EMAIL_ADDRESS.matcher(username).matches() }
        usernameStream.subscribe {
            showEmailValidAlert(it)
            showTextMinimalAlert(it, "Email")
        }

//        // Validasi Password
//        val sandiStream = RxTextView.textChanges(binding.editTeksLoginSandi)
//            .skipInitialValue()
//            .map { sandi -> sandi.length < 8 }
//        sandiStream.subscribe {
//            showTextMinimalAlert(it, "Password")
//        }


        // Tombol Nyala Benar atau Salah
//        val invalidFieldsStream = Observable.combineLatest(
//
//            usernameStream,
//            sandiStream,
//
//            {
//                    usernameInvalid: Boolean, sandiInvalid: Boolean
//                ->
//                !usernameInvalid && !sandiInvalid
//            })
//        invalidFieldsStream.subscribe { isValid ->

//            if (isValid) {
//                binding.tombolMasuk.isEnabled = true
//                binding.tombolMasuk.backgroundTintList =
//                    ContextCompat.getColorStateList(this, R.color.primary_color)
//            } else {
//                binding.tombolMasuk.isEnabled = false
//                binding.tombolMasuk.backgroundTintList =
//                    ContextCompat.getColorStateList(this, android.R.color.darker_gray)
//            }

            binding.tombolMasuk.setOnClickListener {
                Toast.makeText(this@LoginActivity, binding.editTeksLoginSandi.text, Toast.LENGTH_SHORT).show()
                val email = binding.editTeksLoginEmail.text.toString()
                val password = binding.editTeksLoginSandi.text.toString()
                login(email, password) // tambahkan parameter email dan password
            }

            val sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
            val loggedIn = sharedPreferences.getBoolean("loggedIn", false)

            if (loggedIn) {
                startActivity(Intent(this, ListStoryActivity::class.java))
                finish()
            }

//        }

        setMyButtonEnable()

        binding.editTeksLoginSandi.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                setMyButtonEnable()
            }
            override fun afterTextChanged(s: Editable) {
            }
        })



    }

    private fun login(email: String, password: String) {
        // RETROFIT INSTANCE API
        val apiConfig = ApiConfig()
        val service = apiConfig.getApiService()

        service.loginPengguna(email, password).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null && !responseBody.error) {
                        val loginResult = responseBody.loginResult

                        // Save data to SharedPreferences
                        val sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
                        val editor = sharedPreferences.edit()
                        editor.putString("userId", loginResult.userId)
                        editor.putString("name", loginResult.name)
                        editor.putString("email", email)
                        editor.putString("token", loginResult.token)
                        editor.putBoolean("loggedIn", true)
                        editor.apply()

                        // Redirect to main activity
                        startActivity(Intent(this@LoginActivity,  ListStoryActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this@LoginActivity, responseBody?.message ?: "Login failed", Toast.LENGTH_SHORT).show()
                    }
                } else {
//                    Toast.makeText(this@LoginActivity, response.message(), Toast.LENGTH_SHORT).show()
                    Toast.makeText(this@LoginActivity,"Email / Sandi Salah", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Toast.makeText(this@LoginActivity, "Failed to connect to API", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showEmailValidAlert(isNotValid: Boolean) {
        binding.editTeksLoginEmail.error = if (isNotValid) "Email tidak valid" else null
    }

    private fun showTextMinimalAlert(isNotValid: Boolean , text: String){
        if (text== "Email/Username")
            binding.editTeksLoginEmail.error = if (isNotValid) "$text tidak boleh kosong!" else null

        else if (text == "Password")
            binding.editTeksLoginSandi.error = if (isNotValid) "$text minimal 8 karakter!" else null

    }

    // FUNGSI UNTUK ANIMASI (OPTIONAL)
    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.teksviewMasukSubJudul, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val judul = ObjectAnimator.ofFloat(binding.teksviewMasukJudul, View.ALPHA, 1f).setDuration(500)
        val subjudul = ObjectAnimator.ofFloat(binding.teksviewMasukSubJudul, View.ALPHA, 1f).setDuration(500)
//        val v = ObjectAnimator.ofFloat(binding.inputEmail, View.ALPHA, 1f).setDuration(500)
        val w = ObjectAnimator.ofFloat(binding.editTeksLoginEmail, View.ALPHA, 1f).setDuration(500)
//        val x = ObjectAnimator.ofFloat(binding.inputSandi, View.ALPHA, 1f).setDuration(500)
        val y = ObjectAnimator.ofFloat(binding.editTeksLoginSandi, View.ALPHA, 1f).setDuration(500)
        val z = ObjectAnimator.ofFloat(binding.tidakPunyaAkun, View.ALPHA, 1f).setDuration(500)
        val login = ObjectAnimator.ofFloat(binding.tombolMasuk, View.ALPHA, 1f).setDuration(500)
        val lupasandi = ObjectAnimator.ofFloat(binding.lupaSandi, View.ALPHA, 1f).setDuration(500)

        val together_bersama = AnimatorSet().apply {
            playTogether(login, lupasandi)
        }

        AnimatorSet().apply {
            playSequentially(judul,subjudul , w,y, together_bersama , z)
            start()
        }
    }

    private fun setMyButtonEnable() {
        val result = binding.editTeksLoginSandi.text
        binding.tombolMasuk.isEnabled = result != null && result.toString().isNotEmpty()
    }



}