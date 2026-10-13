package com.learn.ourmarketplace.data.mapper

import com.learn.ourmarketplace.data.local.ProductEntity
import com.learn.ourmarketplace.data.remote.ProductDto
import com.learn.ourmarketplace.domain.model.Product

fun ProductEntity.toDomain() = Product(
    id = id,
    title = title,
    description = description,
    price = price,
    imageUrl = imageUrl,
    isFavorite = isFavorite
)

fun ProductDto.toEntity(isFavorite: Boolean = false) = ProductEntity(
    id = id,
    title = title,
    description = description,
    price = price,
    imageUrl = image,
    isFavorite = isFavorite
)
