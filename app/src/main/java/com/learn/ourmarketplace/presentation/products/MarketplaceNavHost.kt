package com.learn.ourmarketplace.presentation.products

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

@Composable
fun MarketplaceNavHost(
    uiState: ProductsUiState,
    onFavoriteClick: (com.learn.ourmarketplace.domain.model.Product) -> Unit,
    onRefresh: () -> Unit,
    onAddProduct: suspend (String, String, Double, Uri) -> Unit
) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "products") {
        composable("products") {
            ProductListScreen(
                uiState = uiState,
                onProductClick = { product ->
                    navController.navigate("product/${product.id}")
                },
                onFavoriteClick = onFavoriteClick,
                onRefresh = onRefresh,
                onAddProductClick = { navController.navigate("add-product") }
            )
        }
        composable("add-product") {
            AddProductScreen(
                onBack = { navController.popBackStack() },
                onAddProduct = { title, description, price, imageUri ->
                    onAddProduct(title, description, price, imageUri)
                    navController.popBackStack()
                }
            )
        }
        composable(
            route = "product/{productId}",
            arguments = listOf(navArgument("productId") { type = NavType.IntType })
        ) { entry ->
            val productId = entry.arguments?.getInt("productId")
            ProductDetailsScreen(
                product = uiState.products.firstOrNull { it.id == productId },
                onBack = { navController.popBackStack() },
                onFavoriteClick = onFavoriteClick
            )
        }
    }
}
