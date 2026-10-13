package com.learn.ourmarketplace.core.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.learn.ourmarketplace.data.remote.MockProductsInterceptor
import com.learn.ourmarketplace.data.remote.ProductsApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideGson(): Gson = GsonBuilder().create()

    @Provides
    @Singleton
    fun provideOkHttpClient(gson: Gson): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(MockProductsInterceptor(gson))
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BASIC
                }
            )
            .build()

    @Provides
    @Singleton
    fun provideProductsApi(client: OkHttpClient, gson: Gson): ProductsApi =
        Retrofit.Builder()
            .baseUrl("https://marketplace.mock/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ProductsApi::class.java)
}
