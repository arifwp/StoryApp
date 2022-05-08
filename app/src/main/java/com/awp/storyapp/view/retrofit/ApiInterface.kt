package com.awp.storyapp.view

import com.awp.storyapp.view.body.LoginBody
import com.awp.storyapp.view.body.RegisterBody
import com.awp.storyapp.view.response.FileUploadResponse
import com.awp.storyapp.view.response.RegisterResponse
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface ApiInterface {

    @Headers("Content-Type:application/json")
    @POST("login")
    fun login(
        @Body info: LoginBody
    ): retrofit2.Call<LoginResponse>

    @Headers("Content-Type:application/json")
    @POST("register")
    fun register(
        @Body info: RegisterBody
    ): retrofit2.Call<RegisterResponse>

    @GET("stories")
    fun getAllStories(
        @Header("Authorization") token:String
    ): retrofit2.Call<StoriesResponse>

    @Multipart
    @POST("stories")
    fun uploadImage(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody
    ): retrofit2.Call<FileUploadResponse>

}


class RetrofitInstance {
    companion object {
        val BASE_URL: String = "https://story-api.dicoding.dev/v1/"

        val interceptor: HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
            this.level = HttpLoggingInterceptor.Level.BODY
        }

        val client: OkHttpClient = OkHttpClient.Builder().apply{
            this.addInterceptor(interceptor)
        }.build()
        fun getRetrofitInstance(): Retrofit {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
    }
}