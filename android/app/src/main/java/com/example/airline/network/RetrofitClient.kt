package com.example.airline.network

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // Emulateur → 10.0.2.2 | Téléphone physique → ton IP local ex: 192.168.1.X
    //private const val BASE_URL = "http://192.168.1.70:8080/"
    //private const val BASE_URL = "http://172.20.10.3:8080/"

    private const val BASE_URL = "http://192.168.128.73:8080"

//yasmine was here
    // localhost via adb reverse tcp:8080 tcp:8080
    //private const val BASE_URL = "http://localhost:8080/"
    //private const val BASE_URL = "http://10.0.23.149:8080/"
      //private const val BASE_URL = "http://10.80.136.174:8080/"
    // JWT token stored after login — set via TokenManager.setToken(token)
    private var authToken: String? = null

    fun setToken(token: String) { authToken = token }
    fun getToken(): String? = authToken
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