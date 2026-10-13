package com.learn.ourmarketplace.core.di

import android.content.Context
import androidx.room.Room
import com.learn.ourmarketplace.data.local.MarketplaceDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): MarketplaceDatabase =
        Room.databaseBuilder(context, MarketplaceDatabase::class.java, "marketplace.db").build()
}
