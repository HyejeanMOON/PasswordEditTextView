package com.example.passwordedittextview

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.databinding.DataBindingUtil
import com.example.passwordedittextview.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.editText.setOnPasswordCompleteListener(object : PasswordEditTextView.OnPasswordCompleteListener{
            override fun onComplete(password: String) {
                val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(
                    window.decorView.windowToken,
                    0
                )
                binding.button.isEnabled = true
            }

            override fun onNotComplete() {
                binding.button.isEnabled = false
            }
        })
    }
}