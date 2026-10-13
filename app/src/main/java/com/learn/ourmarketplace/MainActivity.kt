package com.learn.ourmarketplace

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import com.learn.ourmarketplace.presentation.products.MarketplaceNavHost
import com.learn.ourmarketplace.presentation.products.ProductsViewModel
import com.learn.ourmarketplace.ui.theme.OurMarketplaceTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: ProductsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen: SplashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        splashScreen.setKeepOnScreenCondition { !viewModel.uiState.value.initialized }
        enableEdgeToEdge()

        setContent {
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            OurMarketplaceTheme {
                MarketplaceNavHost(
                    uiState = uiState,
                    onFavoriteClick = viewModel::toggleFavorite,
                    onRefresh = viewModel::refresh
                )
            }
        }
    }
}
