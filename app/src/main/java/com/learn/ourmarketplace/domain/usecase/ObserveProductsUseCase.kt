package com.learn.ourmarketplace.domain.usecase

import com.learn.ourmarketplace.domain.repository.ProductRepository

class ObserveProductsUseCase(
    private val repository: ProductRepository
) {
    operator fun invoke() = repository.observeProducts()
}
