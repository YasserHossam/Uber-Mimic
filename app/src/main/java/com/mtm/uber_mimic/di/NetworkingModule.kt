package com.mtm.uber_mimic.di

import com.mtm.uber_mimic.R
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val networkingModule = module {

    val authenticationInterceptor = "auth_interceptor"
    val loggingInterceptor = "logging_interceptor"
    single<Converter.Factory> {
        GsonConverterFactory.create()
    }

    single(named(authenticationInterceptor)) {
        val authorizationHeader = "Authorization"
        val apiKey = androidContext().getString(R.string.foursquare_api_key)

        Interceptor { chain ->
            chain.run {
                proceed(
                    request()
                        .newBuilder()
                        .addHeader(authorizationHeader, apiKey)
                        .build()
                )
            }
        }
    }

    single<Interceptor>(named(loggingInterceptor)) {
        HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
    }

    single {
        OkHttpClient.Builder()
            .addInterceptor(get(named(authenticationInterceptor)))
            .addInterceptor(get(named(loggingInterceptor)))
            .build()
    }

    single<Retrofit> {
        Retrofit.Builder()
            .baseUrl(androidContext().getString(R.string.foursquare_base_url))
            .addConverterFactory(get())
            .client(get())
            .build()
    }
}