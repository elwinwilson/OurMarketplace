package com.learn.ourmarketplace.domain.repository

import com.learn.ourmarketplace.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    fun observeProducts(): Flow<List<Product>>
    suspend fun refreshProducts()
    suspend fun setFavorite(productId: Int, isFavorite: Boolean)
    suspend fun addProduct(title: String, description: String, price: Double, imageUrl: String): Product
    suspend fun syncPendingProducts()
}
