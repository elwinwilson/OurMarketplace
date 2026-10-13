package com.learn.ourmarketplace.domain.repository

import com.learn.ourmarketplace.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    fun observeProducts(): Flow<List<Product>>
    fun observeProduct(productId: Int): Flow<Product?>
    suspend fun refreshProducts()
    suspend fun setFavorite(productId: Int, isFavorite: Boolean)
}
