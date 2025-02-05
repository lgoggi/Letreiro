package com.example.letreiro.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.letreiro.databinding.MovieItemBinding
import com.squareup.picasso.Picasso

class MovieAdapter(private val list: MutableList<MovieData>) :
    RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {

    private var listener: MovieAdapterClickInterface? = null
    fun setListener(listener: MovieAdapterClickInterface) {
        this.listener = listener
    }

    inner class MovieViewHolder(val binding: MovieItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding = MovieItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MovieViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        with(holder) {
            with(list[position]) {
                binding.movieName.text = this.name
                binding.movieDirector.text = this.director
                binding.movieYear.text = this.year
                setPosterImage(this.poster, binding.moviePoster)
                if (this.watched) {
                    binding.watchIcon.visibility = View.INVISIBLE
                    binding.watchedIcon.visibility = View.VISIBLE
                } else {
                    binding.watchIcon.visibility = View.VISIBLE
                    binding.watchedIcon.visibility = View.INVISIBLE
                }

                binding.watchIcon.setOnClickListener {
                    listener?.onWatchedClicked(this)
                }
                binding.watchedIcon.setOnClickListener {
                    listener?.onWatchedClicked(this)
                }

                binding.deleteIcon.setOnClickListener {
                    listener?.onDeleteClicked(this)
                }

            }
        }
    }

    private fun setPosterImage(url: String, imageView: ImageView) {
        Picasso.get().load(url).into(imageView)
    }

    interface MovieAdapterClickInterface {
        fun onWatchedClicked(movieData: MovieData)
        fun onDeleteClicked(movieData: MovieData)
    }
}