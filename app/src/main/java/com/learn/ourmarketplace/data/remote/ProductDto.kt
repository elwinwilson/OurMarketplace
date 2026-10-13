package com.learn.ourmarketplace.data.remote

data class ProductDto(
    val id: Int,
    val title: String,
    val description: String,
    val price: Double,
    val image: String
)
