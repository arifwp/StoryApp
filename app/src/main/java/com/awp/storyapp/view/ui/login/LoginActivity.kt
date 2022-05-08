package com.awp.storyapp.view.ui.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.awp.storyapp.databinding.ActivityLoginBinding
import com.awp.storyapp.view.ApiInterface
import com.awp.storyapp.view.body.LoginBody
import com.awp.storyapp.view.LoginResponse
import com.awp.storyapp.view.RetrofitInstance
import com.awp.storyapp.view.helper.Constant
import com.awp.storyapp.view.helper.PrefHelper
import com.awp.storyapp.view.ui.main.MainActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    lateinit var sharedpref: PrefHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedpref = PrefHelper(this)

        startAnimating()

        binding.buttonLogin.setOnClickListener {
            val email = binding.email.text.toString().trim()
            val password = binding.password.text.toString().trim()

            if (email.isEmpty()){
                binding.email.error = "Email Required"
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(binding.email.text.toString()).matches()) {
                binding.email.error = "Invalid Email"
            } else if (password.isEmpty()){
                binding.password.error = "Password Required"
            } else if (password.length < 6){
                binding.password.error = "Minimum 6 character"
            } else {
                login(binding.email.text.toString(),binding.password.text.toString())
            }

        }


    }

    private fun login(email: String, password: String) {
        val retIn = RetrofitInstance.getRetrofitInstance().create(ApiInterface::class.java)
        val loginInfo = LoginBody(email, password)
        retIn.login(loginInfo).enqueue(object : Callback<LoginResponse>{

            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if(response.code() == 200) {
                    val loginResult = response.body()
                    loginResult?.loginResult!!.token?.let { sharedpref.put(Constant.PREF_TOKEN, it) }
                    sharedpref.put(Constant.PREF_IS_LOGIN, true)
                    Toast.makeText(this@LoginActivity, "Login Success", Toast.LENGTH_SHORT).show()
                    moveActivity()
                } else {
                    Toast.makeText(this@LoginActivity, "Login failed!", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Toast.makeText(this@LoginActivity, t.message, Toast.LENGTH_SHORT).show()
            }

        })
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

    private fun startAnimating() {

        val animateImg = ObjectAnimator.ofFloat(binding.imgIlustrationLogin, View.ALPHA, 1f).setDuration(500)
        val animateTextTitle = ObjectAnimator.ofFloat(binding.loginTitle, View.ALPHA, 1f).setDuration(500)
        val animateFormEmail = ObjectAnimator.ofFloat(binding.email, View.ALPHA, 1f).setDuration(500)
        val animateFormPassword = ObjectAnimator.ofFloat(binding.password, View.ALPHA, 1f).setDuration(500)
        val animateBtnLogin = ObjectAnimator.ofFloat(binding.buttonLogin, View.ALPHA, 1f).setDuration(500)

        val assembling = AnimatorSet().apply {
            playTogether(animateFormEmail, animateFormPassword)
        }

        AnimatorSet().apply {
            playSequentially(animateImg, animateTextTitle, animateBtnLogin, assembling)
            start()
        }
    }
}

