package com.fahmi.aplikasistoryfahmi

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.fahmi.aplikasistoryfahmi.databinding.ActivityRegisterBinding
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@SuppressLint("CheckResult")
class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Validasi Nama Lengkap
        val nama_lengkapStream = RxTextView.textChanges(binding.editTeksRegisterNamalengkap)
            .skipInitialValue()
            .map { nama_lengkap -> nama_lengkap.isEmpty() }
        nama_lengkapStream.subscribe {
            showNameExistAler(it)
        }

        // Validasi Email
        val emailStream = RxTextView.textChanges(binding.editTeksRegisterEmail)
            .skipInitialValue()
            .map { email -> !Patterns.EMAIL_ADDRESS.matcher(email).matches() }
        emailStream.subscribe {
            showEmailValidAlert(it)
        }

        // Validasi Username
        val usernameStream = RxTextView.textChanges(binding.editTeksRegisterUsername)
            .skipInitialValue()
            .map { username -> username.length < 4 }
        usernameStream.subscribe {
            showTextMinimalAlert(it, "Username")
        }

        // Validasi Password
        val sandiStream = RxTextView.textChanges(binding.editTeksRegisterSandi)
            .skipInitialValue()
            .map { sandi -> sandi.length < 8 }
        sandiStream.subscribe {
            showTextMinimalAlert(it, "Sandi")
        }

        // Validasi Ulang Sandi atau Re-Password
        val ulangsandiStream = Observable.merge(
            RxTextView.textChanges(binding.editTeksRegisterSandi)
                .skipInitialValue()
                .map { sandi ->
                    sandi.toString() != binding.editTeksRegisterUlangsandi.text.toString()
                },
            RxTextView.textChanges(binding.editTeksRegisterUlangsandi)
                .skipInitialValue()
                .map { ulangsandi ->
                    ulangsandi.toString() != binding.editTeksRegisterSandi.text.toString()
                })
        ulangsandiStream.subscribe {
            showPasswordConfirmAlert(it)
        }

        // Tombol Nyala Benar atau Salah
        val invalidFieldsStream = Observable.combineLatest(
            nama_lengkapStream,
            emailStream,
            usernameStream,
            sandiStream,
            ulangsandiStream,
            {
                    nama_lengkapInvalid: Boolean, emailInvalid: Boolean, usernameInvalid: Boolean, sandiInvalid: Boolean, ulangsandiInvalid: Boolean,
                ->
                !nama_lengkapInvalid && !emailInvalid && !usernameInvalid && !sandiInvalid && !ulangsandiInvalid
            })
        invalidFieldsStream.subscribe { isValid ->
            if (isValid) {
                binding.tombolDaftar.isEnabled = true
                binding.tombolDaftar.backgroundTintList =
                    ContextCompat.getColorStateList(this, R.color.primary_color)
            } else {
                binding.tombolDaftar.isEnabled = false
                binding.tombolDaftar.backgroundTintList =
                    ContextCompat.getColorStateList(this, android.R.color.darker_gray)
            }
        }

      binding.tombolDaftar.setOnClickListener {
            val name = binding.editTeksRegisterNamalengkap.text.toString()
            val email = binding.editTeksRegisterEmail.text.toString()
          val username = binding.editTeksRegisterUsername.text.toString()
            val password = binding.editTeksRegisterSandi.text.toString()
          val ulangpassword = binding.editTeksRegisterUlangsandi.text.toString()
                register(name, email, password)
        }

    binding.punyaAkun.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }


    private fun showNameExistAler(isNotValid: Boolean) {
        binding.editTeksRegisterNamalengkap.error = if (isNotValid) "Nama tidak boleh kosong!" else null

    }

    private fun showTextMinimalAlert(isNotValid: Boolean, text: String) {
        if (text == "Username")
            binding.editTeksRegisterUsername.error =
                if (isNotValid) "$text harus lebih dari 3 karakter" else null

        if (text == "Sandi")
            binding.editTeksRegisterSandi.error = if (isNotValid) "$text minimal 8 karakter" else null
    }

    private fun showEmailValidAlert(isNotValid: Boolean) {
        binding.editTeksRegisterEmail.error = if (isNotValid) "Email tidak valid" else null
    }

    private fun showPasswordConfirmAlert(isNotValid: Boolean) {
        binding.editTeksRegisterUlangsandi.error = if (isNotValid) "Sandi tidak sesuai" else null
    }
    // RETROFIT INSTANCE API
    private fun register(name: String, email: String, password: String) {
        val apiConfig = ApiConfig()
        val service = apiConfig.getApiService()
        service.registerPengguna(name, email, password).enqueue(object :
            Callback<RegisterResponse> {
            override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null && !responseBody.error) {


                        // Save data to SharedPreferences
                        val sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
                        val editor = sharedPreferences.edit()
                        editor.putString("name", name)
                        editor.putString("email", email)
                        editor.putString("password", password)
                        editor.apply()

                        // Redirect to main activity
                        startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this@RegisterActivity, responseBody?.message ?: "Registration failed", Toast.LENGTH_SHORT).show()
                    }
                } else {
//                    Toast.makeText(this@RegisterActivity, response.message(), Toast.LENGTH_SHORT).show()
                    Toast.makeText(this@RegisterActivity, "Email sudah terdaftar", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                Toast.makeText(this@RegisterActivity, "Failed to connect to API", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
