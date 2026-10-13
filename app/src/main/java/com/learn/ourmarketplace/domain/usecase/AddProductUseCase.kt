package com.learn.ourmarketplace.domain.usecase

import com.learn.ourmarketplace.domain.repository.ProductRepository

class AddProductUseCase(
    private val repository: ProductRepository
) {
    suspend operator fun invoke(
        title: String,
        description: String,
        price: Double,
        imageUrl: String
    ) = repository.addProduct(title, description, price, imageUrl)
}
