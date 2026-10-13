package com.learn.ourmarketplace.core.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import androidx.core.content.getSystemService
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged

class AndroidNetworkMonitor @Inject constructor(
    @ApplicationContext context: Context
) : NetworkMonitor {
    private val connectivityManager = context.getSystemService<ConnectivityManager>()
        ?: error("Connectivity service is unavailable")

    override val isOnline: Flow<Boolean> = callbackFlow {
        fun updateStatus() {
            val capabilities = connectivityManager.activeNetwork
                ?.let(connectivityManager::getNetworkCapabilities)
            trySend(capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true)
        }

        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) = updateStatus()
            override fun onLost(network: Network) = updateStatus()
            override fun onCapabilitiesChanged(network: Network, capabilities: NetworkCapabilities) =
                updateStatus()
        }

        updateStatus()
        connectivityManager.registerDefaultNetworkCallback(callback)
        awaitClose { connectivityManager.unregisterNetworkCallback(callback) }
    }.distinctUntilChanged()
}
