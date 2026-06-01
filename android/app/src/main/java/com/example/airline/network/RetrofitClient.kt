package com.example.airline.network

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // Emulateur → 10.0.2.2 | Téléphone physique → ton IP local ex: 192.168.1.X
    private const val BASE_URL = "http://10.0.2.2:8080/"

    private var authToken: String? = null

    fun setToken(token: String) { authToken = token }
    fun clearToken() { authToken = null }

    private val authInterceptor = Interceptor { chain ->
        val request = chain.request().newBuilder().apply {
            authToken?.let { header("Authorization", "Bearer $it") }
        }.build()
        chain.proceed(request)
    }

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(logging)
        .build()

    // Instance Retrofit partagée (utilisée par tous les services)
    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // ApiService existant (checkin, boarding pass, etc.)
    val api: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}