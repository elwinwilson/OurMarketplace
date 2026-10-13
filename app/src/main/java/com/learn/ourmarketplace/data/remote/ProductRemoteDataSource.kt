package com.learn.ourmarketplace.data.remote

import javax.inject.Inject

class ProductRemoteDataSource @Inject constructor(
    private val productsApi: ProductsApi
) {
    suspend fun fetchProducts() = productsApi.getProducts()
}
