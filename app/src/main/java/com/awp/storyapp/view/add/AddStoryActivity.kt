package com.awp.storyapp.view.add

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.awp.storyapp.databinding.ActivityAddStoryBinding
import com.awp.storyapp.view.ApiInterface
import com.awp.storyapp.view.response.FileUploadResponse
import com.awp.storyapp.view.RetrofitInstance
import com.awp.storyapp.view.helper.Constant
import com.awp.storyapp.view.helper.PrefHelper
import com.awp.storyapp.view.ui.main.MainActivity
import com.awp.storyapp.view.utils.rotateBitmap
import com.awp.storyapp.view.utils.uriToFile
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class AddStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddStoryBinding

    private lateinit var currentPhotoPath: String
    lateinit var sharedpref: PrefHelper

    private var getFile: File? = null

    companion object {
        const val CAMERA_X_RESULT = 200

        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!authorize()) {
                Toast.makeText(this, "Tidak Diizinkan", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun authorize() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedpref = PrefHelper(this)

        ifGranted()
        setBindButton()

    }

    private fun setBindButton() {
        binding.btnCamera.setOnClickListener { runningCameraX() }
        binding.btnGallery.setOnClickListener { runningGallery() }
        binding.btnUpload.setOnClickListener { uploadImage() }
    }

    private fun runningCameraX() {
        val intent = Intent(this, CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    private fun ifGranted() {
        if (!authorize()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }
    }

    private fun runningGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERA_X_RESULT) {
            val myFile = it.data?.getSerializableExtra("picture") as File
            val isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean

            getFile = myFile
            val result = rotateBitmap(
                BitmapFactory.decodeFile(getFile?.path),
                isBackCamera
            )

            binding.imageIcon.setImageBitmap(result)
        }
    }

    private fun doingService() {
        val file = getFile as File

        val descStory = binding.descStory
        val description = descStory.text.toString().toRequestBody("text/plain".toMediaType())
        val reqImgFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "photo",
            file.name,
            reqImgFile
        )

        val token = sharedpref.getToken(Constant.PREF_TOKEN)
        val retIn = RetrofitInstance.getRetrofitInstance().create(ApiInterface::class.java)
        val service = retIn.uploadImage("bearer $token", imageMultipart, description)

        service.enqueue(object : Callback<FileUploadResponse> {
            override fun onResponse(
                call: Call<FileUploadResponse>,
                response: Response<FileUploadResponse>
            ) {
                if (response.isSuccessful) {
                    val response = response.body()
                    if (response != null && !response.error!!) {
                        Toast.makeText(this@AddStoryActivity, response.message, Toast.LENGTH_SHORT).show()
                        moveActivity()
                    }
                } else {
                    Toast.makeText(this@AddStoryActivity, response.message(), Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<FileUploadResponse>, t: Throwable) {
                Toast.makeText(this@AddStoryActivity, "Gagal instance Retrofit", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri

            val myFile = uriToFile(selectedImg, this@AddStoryActivity)

            getFile = myFile

            binding.imageIcon.setImageURI(selectedImg)
        }
    }

    private fun uploadImage() {
        if (getFile != null) {
            doingService()
        } else {
            Toast.makeText(this@AddStoryActivity, "Please Input Your Picture Bro!", Toast.LENGTH_SHORT).show()
        }
    }


    private fun moveActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }



}