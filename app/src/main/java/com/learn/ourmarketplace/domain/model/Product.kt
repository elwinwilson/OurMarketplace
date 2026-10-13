package com.learn.ourmarketplace.domain.model

data class Product(
    val id: Int,
    val title: String,
    val description: String,
    val price: Double,
    val imageUrl: String,
    val isFavorite: Boolean = false
)
