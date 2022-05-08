package com.awp.storyapp.view.ui.register

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.awp.storyapp.databinding.ActivityRegisterBinding
import com.awp.storyapp.view.ApiInterface
import com.awp.storyapp.view.body.RegisterBody
import com.awp.storyapp.view.RetrofitInstance
import com.awp.storyapp.view.response.RegisterResponse
import com.awp.storyapp.view.ui.login.LoginActivity
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        startAnimating()

        binding.buttonRegister.setOnClickListener {
            val name = binding.name.text.toString().trim()
            val email = binding.email.text.toString().trim()
            val password = binding.password.text.toString().trim()

            if (name.isEmpty()){
                binding.name.error = "Name Required"
            } else if (email.isEmpty()){
                binding.email.error = "Email Required"
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(binding.email.text.toString()).matches()) {
                binding.email.error = "Invalid Email"
            } else if (password.isEmpty()){
                binding.password.error = "Password Required"
            } else if (password.length < 6){
                binding.password.error = "Minimum 6 character"
            } else {
                register(binding.name.text.toString(),binding.email.text.toString(),binding.password.text.toString())
            }

        }

    }


    private fun register(name: String, email: String, password: String){

        val retIn = RetrofitInstance.getRetrofitInstance().create(ApiInterface::class.java)
        val registerInfo = RegisterBody(name,email,password)

        retIn.register(registerInfo).enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(
                call: Call<RegisterResponse>,
                response: Response<RegisterResponse>
            ) {
                if (response.code() == 200) {
                    Toast.makeText(this@RegisterActivity, "Registration success!", Toast.LENGTH_SHORT).show()
                    moveActivity()
                }
                else{
                    Toast.makeText(this@RegisterActivity, response.message(), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                Toast.makeText(this@RegisterActivity, t.message, Toast.LENGTH_SHORT).show()
            }

        })

    }

    private fun moveActivity() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    private fun startAnimating() {

        val animateImg = ObjectAnimator.ofFloat(binding.imgIlustrationLogin, View.ALPHA, 1f).setDuration(500)
        val animateRegisterTitle = ObjectAnimator.ofFloat(binding.registerTitle, View.ALPHA, 1f).setDuration(500)
        val animateFormName = ObjectAnimator.ofFloat(binding.name, View.ALPHA, 1f).setDuration(500)
        val animateFormEmail = ObjectAnimator.ofFloat(binding.email, View.ALPHA, 1f).setDuration(500)
        val animateFormPassword = ObjectAnimator.ofFloat(binding.password, View.ALPHA, 1f).setDuration(500)
        val animateBtnLogin = ObjectAnimator.ofFloat(binding.buttonRegister, View.ALPHA, 1f).setDuration(500)

        val assembling = AnimatorSet().apply {
            playTogether(animateFormName, animateFormEmail, animateFormPassword)
        }

        AnimatorSet().apply {
            playSequentially(animateImg, animateRegisterTitle, animateBtnLogin, assembling)
            start()
        }

    }
}