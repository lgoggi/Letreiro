package com.example.letreiro.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.letreiro.databinding.FragmentAddMovieBinding
import com.example.letreiro.utils.MovieData
import com.example.letreiro.utils.RetrofitInstance
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException


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
            GlobalScope.launch(Dispatchers.IO) {
                Log.v("tag erro", "rodou")
                val response = try {
                    RetrofitInstance.api.getMovie("f4cd5d6c", binding.movieEditText.text.toString())
                } catch (e: HttpException) {
                    Log.v("error", e.message())
                    return@launch
                } catch (e: IOException) {
                    Log.v("error", e.message.toString())
                    return@launch
                }

                if (response.isSuccessful && response.body() != null) {
                    Log.v("tag", response.body().toString())
                    var movieHashmap = mutableMapOf<String, String>()
                    movieHashmap["name"] = response.body()!!.Title.toString()
                    movieHashmap["director"] = response.body()!!.Director.toString()
                    movieHashmap["year"] = response.body()!!.Year.toString()

                    listener.onSaveMovie(movieHashmap, binding.movieEditText)
                    dismiss()
                }
            }

        }
        binding.closeButton.setOnClickListener {
            dismiss()
        }
    }

    interface OnDialogNextBtnClickListener {
        fun onSaveMovie(movie: MutableMap<String, String>, movieEdit: TextInputEditText)
    }
}
