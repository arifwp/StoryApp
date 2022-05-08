package com.awp.storyapp.view.ui.first

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import com.awp.storyapp.databinding.ActivityFirstBinding
import com.awp.storyapp.view.helper.Constant
import com.awp.storyapp.view.helper.PrefHelper
import com.awp.storyapp.view.ui.login.LoginActivity
import com.awp.storyapp.view.ui.main.MainActivity
import com.awp.storyapp.view.ui.register.RegisterActivity

class FirstActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFirstBinding
    lateinit var sharedpref: PrefHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFirstBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedpref = PrefHelper(this)

        setupView()
        moveActivityWithButton()
        startAnimating()
    }

    override fun onStart() {
        super.onStart()
        if (sharedpref.getBoolean(Constant.PREF_IS_LOGIN)) {
            moveActivity()
        }
    }

    private fun moveActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun moveActivityWithButton() {
        binding.buttonLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        binding.buttonRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun startAnimating() {

        val animateImg = ObjectAnimator.ofFloat(binding.imgIlustrationLogin, View.ALPHA, 1f).setDuration(500)
        val animateBtnLogin = ObjectAnimator.ofFloat(binding.buttonLogin, View.ALPHA, 1f).setDuration(500)
        val animateBtnRegister = ObjectAnimator.ofFloat(binding.buttonRegister, View.ALPHA, 1f).setDuration(500)
        val animateTextTitle = ObjectAnimator.ofFloat(binding.firstTitle, View.ALPHA, 1f).setDuration(500)
        val animateTextSubTitle = ObjectAnimator.ofFloat(binding.firstSubTitle, View.ALPHA, 1f).setDuration(500)

        val assembling = AnimatorSet().apply {
            playTogether(animateBtnLogin, animateBtnRegister)
        }

        AnimatorSet().apply {
            playSequentially(animateImg, animateTextTitle, animateTextSubTitle, assembling)
            start()
        }

    }
}