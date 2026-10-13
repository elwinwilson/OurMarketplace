package com.learn.ourmarketplace.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val description: String,
    val price: Double,
    val imageUrl: String,
    val isFavorite: Boolean,
    @ColumnInfo(defaultValue = "0") val isUserCreated: Boolean = false,
    @ColumnInfo(defaultValue = "1") val isSynced: Boolean = true
)
