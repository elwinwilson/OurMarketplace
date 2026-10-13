package com.learn.ourmarketplace.domain.usecase

import com.learn.ourmarketplace.domain.repository.ProductRepository

class SyncPendingProductsUseCase(
    private val repository: ProductRepository
) {
    suspend operator fun invoke() = repository.syncPendingProducts()
}
