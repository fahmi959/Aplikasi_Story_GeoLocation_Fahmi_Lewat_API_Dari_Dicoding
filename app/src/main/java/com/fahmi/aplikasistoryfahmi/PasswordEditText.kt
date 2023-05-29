package com.fahmi.aplikasistoryfahmi

import android.content.Context
import android.text.InputType
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.widget.EditText
import androidx.appcompat.widget.AppCompatEditText

class PasswordEditText(context: Context, attrs: AttributeSet?) : AppCompatEditText(context, attrs) {

    init {
        inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        transformationMethod = PasswordTransformationMethod.getInstance()
    }

    override fun onTextChanged(text: CharSequence?, start: Int, lengthBefore: Int, lengthAfter: Int) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter)

        if (text?.length ?: 0 < 8) {
            error = "Password must be at least 8 characters long"
        } else {
            error = null
        }

        val confirmPassword = findViewById<EditText>(R.id.edit_teks_register_ulangsandi)?.text?.toString()

        if (!confirmPassword.isNullOrEmpty() && text.toString() != confirmPassword) {
            findViewById<EditText>(R.id.edit_teks_register_ulangsandi)?.error = "Passwords do not match"
        } else {
            findViewById<EditText>(R.id.edit_teks_register_ulangsandi)?.error = null
        }
    }
}