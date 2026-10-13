package com.learn.ourmarketplace.core.di

import com.learn.ourmarketplace.core.network.AndroidNetworkMonitor
import com.learn.ourmarketplace.core.network.NetworkMonitor
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkMonitorModule {
    @Binds
    @Singleton
    abstract fun bindNetworkMonitor(monitor: AndroidNetworkMonitor): NetworkMonitor
}
