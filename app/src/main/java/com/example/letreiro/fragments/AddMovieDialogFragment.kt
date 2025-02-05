package com.example.letreiro.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.letreiro.databinding.FragmentAddMovieBinding
import com.example.letreiro.utils.MovieData
import com.google.android.material.textfield.TextInputEditText

class AddMovieDialogFragment : DialogFragment() {
    private lateinit var binding: FragmentAddMovieBinding
    private lateinit var listener: OnDialogNextBtnClickListener
    private var movieData: MovieData? = null

    fun setListener(listener: HomeFragment) {
        this.listener = listener
    }

    companion object {
        const val TAG = "DialogFragment"

        @JvmStatic
        fun newInstance(movie: String, director: String) = AddMovieDialogFragment().apply {
            arguments = Bundle().apply {
                putString("movie", movie)
                putString("director", director)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        binding = FragmentAddMovieBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.addMovieButton.setOnClickListener {
            var movieHashmap = mutableMapOf<String, String>()
            movieHashmap["name"] = binding.movieEditText.text.toString()
            movieHashmap["director"] = "director"
            movieHashmap["year"] = "2001"


            listener.onSaveMovie(movieHashmap, binding.movieEditText)
            dismiss()
        }
        binding.closeButton.setOnClickListener {
            dismiss()
        }
    }


    interface OnDialogNextBtnClickListener {
        fun onSaveMovie(movie: MutableMap<String, String>, movieEdit: TextInputEditText)
    }
}