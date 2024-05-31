package com.example.maverick_labs_catalog.storage

import android.content.Context
import okhttp3.Interceptor
//import retrofit2.Response
import okhttp3.Response

class AuthInterceptor(context: Context) : Interceptor {
    private val sessionManager = SessionManager(context)

    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()

        // If token has been saved, add it to the request
        sessionManager.fetchAuthToken()?.let {
            requestBuilder.addHeader("Authorization", "Token $it")
        }

        return chain.proceed(requestBuilder.build())
    }
}