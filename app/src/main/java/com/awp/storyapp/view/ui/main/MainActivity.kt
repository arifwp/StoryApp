package com.awp.storyapp.view.ui.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.awp.storyapp.R
import com.awp.storyapp.databinding.ActivityMainBinding
import com.awp.storyapp.view.*
import com.awp.storyapp.view.add.AddStoryActivity
import com.awp.storyapp.view.helper.Constant
import com.awp.storyapp.view.helper.PrefHelper
import com.awp.storyapp.view.ui.login.LoginActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    lateinit var sharedpref: PrefHelper
    private lateinit var storiesAdapter: StoriesAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var storiesResponse: StoriesResponse

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedpref = PrefHelper(this)

        setRv()
        getAllStories()

    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.logout_menu -> {
                sharedpref.clear()
                moveActivity()
            }
            R.id.add_story -> {
                startActivity(Intent(this, AddStoryActivity::class.java))
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.option_menu, menu)
        return true
    }

    private fun getAllStories() {
        val token = sharedpref.getToken(Constant.PREF_TOKEN)
        val retIn = RetrofitInstance.getRetrofitInstance().create(ApiInterface::class.java)
        if (token != null) {
            retIn.getAllStories("bearer $token").enqueue(object : Callback<StoriesResponse> {
                override fun onResponse(
                    call: Call<StoriesResponse>,
                    response: Response<StoriesResponse>
                ) {
                    val resource: StoriesResponse? = response.body()
                    updatingData(resource!!.stories)
                }

                override fun onFailure(call: Call<StoriesResponse>, t: Throwable) {
                    Toast.makeText(this@MainActivity, getString(R.string.required), Toast.LENGTH_SHORT).show()
                }

            })
        }
    }

    private fun setRv() {
        val linearLayoutManager = LinearLayoutManager(this)
        storiesAdapter = StoriesAdapter()

        recyclerView = binding.rvStory
        recyclerView.apply {
            adapter = storiesAdapter
            layoutManager = linearLayoutManager
        }
    }


    private fun updatingData(stories: List<Story>) {
        val recyclerViewState = recyclerView.layoutManager?.onSaveInstanceState()
        storiesAdapter.submitList(stories)

        recyclerView.layoutManager?.onRestoreInstanceState(recyclerViewState)
    }

    private fun moveActivity() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }


}

