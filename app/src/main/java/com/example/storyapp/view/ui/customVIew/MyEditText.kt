package com.example.storyapp.view.ui.customVIew

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import com.example.storyapp.R

class MyEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatEditText(context, attrs), TextWatcher {

    private var inputTypeCustom: String? = null

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.MyEditText,
            0, 0
        ).apply {
            try {
                inputTypeCustom = getString(R.styleable.MyEditText_inputTypeCustom)
            } finally {
                recycle()
            }
        }

        inputType = when (inputTypeCustom) {
            "password" -> {
                android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
            }

            "email" -> {
                android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            }

            else -> {
                android.text.InputType.TYPE_CLASS_TEXT
            }
        }

        addTextChangedListener(this)
    }

    override fun afterTextChanged(s: Editable?) {


        when (inputTypeCustom) {
            "Captions", "captions" ->{
                if (s.isNullOrEmpty()) {
                    return
                }
            }
            "Email", "email" -> {
                if (s.isNullOrEmpty()) {
                    error = context.getString(R.string.cannot_be_empty, inputTypeCustom)
                    return
                }
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(s).matches()) {
                    error = context.getString(R.string.email_format_cannot_valid)
                }
            }
            "Password", "password" -> {
                if (s.isNullOrEmpty()) {
                    error = context.getString(R.string.cannot_be_empty, inputTypeCustom)
                    return
                }
                if (s.length < 8) {
                    error = context.getString(R.string.password_must_then_8)
                }
            }
            else -> {
                if (s.isNullOrEmpty()) {
                    error = context.getString(R.string.cannot_be_empty, inputTypeCustom)
                    return
                }
            }
            
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
}
