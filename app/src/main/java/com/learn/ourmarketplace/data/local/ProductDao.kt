package com.learn.ourmarketplace.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Query("SELECT * FROM products ORDER BY isUserCreated DESC, id ASC")
    fun observeProducts(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE id = :productId")
    fun observeProduct(productId: Int): Flow<ProductEntity?>

    @Query("SELECT * FROM products")
    suspend fun getAll(): List<ProductEntity>

    @Query("SELECT COALESCE(MIN(id), 0) - 1 FROM products WHERE isUserCreated = 1")
    suspend fun nextUserProductId(): Int

    @Query("SELECT * FROM products WHERE isUserCreated = 1 AND isSynced = 0 ORDER BY id ASC")
    suspend fun getPendingSync(): List<ProductEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(products: List<ProductEntity>)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertProduct(product: ProductEntity)

    @Query("UPDATE products SET isFavorite = :isFavorite WHERE id = :productId")
    suspend fun setFavorite(productId: Int, isFavorite: Boolean)

    @Query("UPDATE products SET isSynced = 1 WHERE id = :productId")
    suspend fun markSynced(productId: Int)
}
