package com.learn.ourmarketplace.domain.usecase

import com.learn.ourmarketplace.domain.repository.ProductRepository

class SetProductFavoriteUseCase(
    private val repository: ProductRepository
) {
    suspend operator fun invoke(productId: Int, isFavorite: Boolean) =
        repository.setFavorite(productId, isFavorite)
}
