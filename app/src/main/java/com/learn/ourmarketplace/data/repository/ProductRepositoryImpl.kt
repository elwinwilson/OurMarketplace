package com.learn.ourmarketplace.data.repository

import androidx.room.withTransaction
import com.learn.ourmarketplace.data.local.MarketplaceDatabase
import com.learn.ourmarketplace.data.mapper.toDomain
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
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ProductRepository {
    private val productDao = database.productDao()

    override fun observeProducts(): Flow<List<Product>> =
        productDao.observeProducts().map { products -> products.map { it.toDomain() } }

    override fun observeProduct(productId: Int): Flow<Product?> =
        productDao.observeProduct(productId).map { it?.toDomain() }

    override suspend fun refreshProducts() = withContext(ioDispatcher) {
        val remoteProducts = remoteDataSource.fetchProducts()
        database.withTransaction {
            val favorites = productDao.getAll().associate { it.id to it.isFavorite }
            productDao.replaceAll(remoteProducts.map { dto ->
                dto.toEntity(isFavorite = favorites[dto.id] ?: false)
            })
        }
    }

    override suspend fun setFavorite(productId: Int, isFavorite: Boolean) =
        withContext(ioDispatcher) {
            productDao.setFavorite(productId, isFavorite)
        }
}
