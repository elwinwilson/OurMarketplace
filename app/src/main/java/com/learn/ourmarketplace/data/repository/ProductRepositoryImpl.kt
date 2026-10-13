package com.learn.ourmarketplace.data.repository

import androidx.room.withTransaction
import com.learn.ourmarketplace.data.local.MarketplaceDatabase
import com.learn.ourmarketplace.data.local.ProductEntity
import com.learn.ourmarketplace.data.mapper.toDomain
import com.learn.ourmarketplace.data.mapper.toDto
import com.learn.ourmarketplace.data.mapper.toEntity
import com.learn.ourmarketplace.data.remote.ProductRemoteDataSource
import com.learn.ourmarketplace.domain.model.Product
import com.learn.ourmarketplace.domain.repository.ProductRepository
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import com.learn.ourmarketplace.core.di.IoDispatcher

class ProductRepositoryImpl @Inject constructor(
    private val database: MarketplaceDatabase,
    private val remoteDataSource: ProductRemoteDataSource,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ProductRepository {
    private val productDao = database.productDao()

    override fun observeProducts(): Flow<List<Product>> =
        productDao.observeProducts().map { products -> products.map { it.toDomain() } }

    override fun observeProduct(productId: Int): Flow<Product?> =
        productDao.observeProduct(productId).map { it?.toDomain() }

    override suspend fun refreshProducts() = withContext(ioDispatcher) {
        val remoteProducts = remoteDataSource.fetchProducts()
        database.withTransaction {
            val existingProducts = productDao.getAll().associateBy { it.id }
            productDao.upsertAll(remoteProducts.map { dto ->
                val existing = existingProducts[dto.id]
                dto.toEntity(
                    isFavorite = existing?.isFavorite ?: false,
                    isUserCreated = existing?.isUserCreated ?: false
                )
            })
        }
    }

    override suspend fun setFavorite(productId: Int, isFavorite: Boolean) =
        withContext(ioDispatcher) {
            productDao.setFavorite(productId, isFavorite)
        }

    override suspend fun addProduct(
        title: String,
        description: String,
        price: Double,
        imageUrl: String
    ) = withContext(ioDispatcher) {
        val product = ProductEntity(
            id = productDao.nextUserProductId(),
            title = title,
            description = description,
            price = price,
            imageUrl = imageUrl,
            isFavorite = false,
            isUserCreated = true,
            isSynced = false
        )
        productDao.insertProduct(product)
        product.toDomain()
    }

    override suspend fun syncPendingProducts() = withContext(ioDispatcher) {
        productDao.getPendingSync().forEach { product ->
            remoteDataSource.createProduct(product.toDto())
            productDao.markSynced(product.id)
        }
    }
}
