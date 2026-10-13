package com.learn.ourmarketplace.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Query("SELECT * FROM products ORDER BY id")
    fun observeProducts(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE id = :productId")
    fun observeProduct(productId: Int): Flow<ProductEntity?>

    @Query("SELECT * FROM products")
    suspend fun getAll(): List<ProductEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun replaceAll(products: List<ProductEntity>)

    @Query("UPDATE products SET isFavorite = :isFavorite WHERE id = :productId")
    suspend fun setFavorite(productId: Int, isFavorite: Boolean)
}
