package com.example.maverick_labs_catalog.services

import android.app.Application
import android.content.Context
import com.example.maverick_labs_catalog.storage.AuthInterceptor
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.util.concurrent.TimeUnit

object ServiceBuilder {

            val URL = "http://192.168.1.3:8000/"

    private val logger = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

    private val okHttp = OkHttpClient.Builder()
        .addInterceptor(logger)
//        .addInterceptor(AuthInterceptor(this))

    private  val builder =Retrofit.Builder().baseUrl(URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttp.build())

     private val retrofit = builder.build()

    fun <T> buildService(serviceType:Class<T>): T {
        return retrofit.create(serviceType)
    }

}