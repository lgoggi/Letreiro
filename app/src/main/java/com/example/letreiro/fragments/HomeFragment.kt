package com.example.letreiro.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.letreiro.R
import com.example.letreiro.databinding.FragmentHomeBinding
import com.example.letreiro.utils.MovieAdapter
import com.example.letreiro.utils.MovieData
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HomeFragment : Fragment(), AddMovieDialogFragment.OnDialogNextBtnClickListener,
    MovieAdapter.MovieAdapterClickInterface {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var navControl: NavController
    private lateinit var binding: FragmentHomeBinding
    private var addMovieFrag: AddMovieDialogFragment? = null
    private lateinit var movieAdapter: MovieAdapter
    private lateinit var movieList: MutableList<MovieData>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init(view)
        getMovieList()

        binding.addMovieButton.setOnClickListener {
            if (addMovieFrag != null) {
                childFragmentManager.beginTransaction().remove(addMovieFrag!!).commit()
            }
            addMovieFrag = AddMovieDialogFragment()
            addMovieFrag!!.setListener(this)
            addMovieFrag!!.show(
                childFragmentManager,
                "AddMovieDialogFragment"
            )
        }

        binding.exitButton.setOnClickListener {
            auth.signOut()
            navControl.navigate(R.id.action_homeFragment_to_signInFragment)
        }

    }


    private fun init(view: View) {
        navControl = Navigation.findNavController(view)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference.child("movies")
            .child(auth.currentUser?.uid.toString())

        binding.movieRecyclerView.setHasFixedSize(true)
        binding.movieRecyclerView.layoutManager = LinearLayoutManager(context)
        movieList = mutableListOf()
        movieAdapter = MovieAdapter(movieList)
        movieAdapter.setListener(this)
        binding.movieRecyclerView.adapter = movieAdapter
    }

    private fun getMovieList() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                movieList.clear()
                for (movieSnapshot in snapshot.children) {
                    val newMovieHolder: HashMap<String, String> =
                        movieSnapshot.value as HashMap<String, String>
                    val newMovie = MovieData(
                        movieSnapshot.key.toString(),
                        newMovieHolder["name"] as String,
                        newMovieHolder["director"] as String,
                        newMovieHolder["year"] as String,
                        if (newMovieHolder["watched"] == null) false else newMovieHolder["watched"] as Boolean,
                        newMovieHolder["poster"] as String,
                    )
                    movieList.add(newMovie as MovieData)

                }
                movieAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onSaveMovie(movie: MutableMap<String, String>, movieEdit: TextInputEditText) {
        database.push().setValue(movie).addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(context, "movie added", Toast.LENGTH_SHORT).show()
                movieEdit.text = null
            } else {
                Toast.makeText(context, it.exception?.message, Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun onWatchedClicked(movieData: MovieData) {
        val map = HashMap<String, Any>()
        val newMovie = MovieData(
            movieData.movieId,
            movieData.name,
            movieData.director,
            movieData.year,
            !movieData.watched,
            movieData.poster
        )
        map[movieData.movieId] = newMovie
        database.updateChildren(map)
    }

    override fun onDeleteClicked(movieData: MovieData) {
        database.child(movieData.movieId).removeValue().addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(context, "movie removed", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, it.exception?.message, Toast.LENGTH_SHORT).show()
            }
        }
    }
}