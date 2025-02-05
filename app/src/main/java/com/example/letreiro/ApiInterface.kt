package com.example.letreiro


import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiInterface {

    @GET("/")
    suspend fun getMovie(@Query("apikey") apiKey: String ,@Query("t") movieName: String): Response<MovieResponse>
}

data class MovieResponse (
    val Title: String,
    val Director: String,
    val Year: String,
    val Poster: String,
    val Response: String
)
