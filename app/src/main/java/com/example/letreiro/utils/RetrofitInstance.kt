package com.example.letreiro.utils

import com.example.letreiro.ApiInterface
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

object RetrofitInstance {
    val api: ApiInterface by lazy {
        Retrofit.Builder().baseUrl("https://omdbapi.com").addConverterFactory(GsonConverterFactory.create())
            .build().create(ApiInterface::class.java)
    }
}