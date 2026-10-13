package com.learn.ourmarketplace.domain.usecase

import com.learn.ourmarketplace.domain.repository.ProductRepository

class RefreshProductsUseCase(
    private val repository: ProductRepository
) {
    suspend operator fun invoke() = repository.refreshProducts()
}
