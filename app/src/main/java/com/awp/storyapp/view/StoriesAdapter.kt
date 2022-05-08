package com.awp.storyapp.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.awp.storyapp.databinding.LayoutStoryBinding
import com.awp.storyapp.view.ui.main.DetailActivity
import com.awp.storyapp.view.ui.main.DetailActivity.Companion.EXTRA_DETAIL
import com.bumptech.glide.Glide

class StoriesAdapter : ListAdapter<Story, StoriesAdapter.ViewHolder>(DiffCallback) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val story = getItem(position)
        holder.bind(holder.itemView.context, story)
    }


    class ViewHolder(private val binding: LayoutStoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(context: Context, story: Story) {
            binding.apply {

                nameStory.text = story.name
                descStory.text = story.description
                Glide
                    .with(context)
                    .load(story.photoUrl)
                    .into(imageStory)
                dateStory.text = story.createdAt

                root.setOnClickListener {
                    val optionsCompat: ActivityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                            root.context as Activity,
                            Pair(imageStory, "story_image"),
                            Pair(nameStory, "username"),
                            Pair(dateStory, "date"),
                            Pair(descStory, "description")
                        )

                    Intent(context, DetailActivity::class.java).also { intent ->
                        intent.putExtra(EXTRA_DETAIL, story)
                        context.startActivity(intent, optionsCompat.toBundle())
                    }
                }
            }
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Story>() {
            override fun areItemsTheSame(oldItem: Story, newItem: Story): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Story, newItem: Story): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            LayoutStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

}