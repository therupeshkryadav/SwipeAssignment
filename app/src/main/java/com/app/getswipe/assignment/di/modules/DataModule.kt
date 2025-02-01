package com.app.getswipe.assignment.di.modules

import android.content.Context
import android.net.ConnectivityManager
import com.app.getswipe.assignment.data.api.ProductRemoteDataSource
import com.app.getswipe.assignment.data.api.ProductService
import com.app.getswipe.assignment.data.local.SharedPreferencesDataSource
import com.app.getswipe.assignment.data.repository.ProductRepositoryImpl
import com.app.getswipe.assignment.domain.repository.ProductRepository
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dataModule = module {

    // Provide ConnectivityManager
    single { get<Context>().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager }

    // Provide Retrofit instance
    single {
        Retrofit.Builder()
            .baseUrl("https://app.getswipe.in/") // Replace with actual base URL
            .client(OkHttpClient.Builder().build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Provide ProductService
    single {
        get<Retrofit>().create(ProductService::class.java)
    }
    single { ProductRemoteDataSource(get()) }

    // SharedPreferences
    single { SharedPreferencesDataSource(androidContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)) }

    // Remote Data Source
    single { ProductRemoteDataSource(get()) }

    single<ProductRepository> { ProductRepositoryImpl(get(), get()) }
}