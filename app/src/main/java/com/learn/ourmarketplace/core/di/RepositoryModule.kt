package com.learn.ourmarketplace.core.di

import com.learn.ourmarketplace.data.repository.ProductRepositoryImpl
import com.learn.ourmarketplace.domain.repository.ProductRepository
import com.learn.ourmarketplace.domain.usecase.ObserveProductsUseCase
import com.learn.ourmarketplace.domain.usecase.AddProductUseCase
import com.learn.ourmarketplace.domain.usecase.RefreshProductsUseCase
import com.learn.ourmarketplace.domain.usecase.SetProductFavoriteUseCase
import com.learn.ourmarketplace.domain.usecase.SyncPendingProductsUseCase
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindProductRepository(implementation: ProductRepositoryImpl): ProductRepository
}

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {
    @Provides
    fun provideObserveProducts(repository: ProductRepository) =
        ObserveProductsUseCase(repository)

    @Provides
    fun provideRefreshProducts(repository: ProductRepository) =
        RefreshProductsUseCase(repository)

    @Provides
    fun provideAddProduct(repository: ProductRepository) =
        AddProductUseCase(repository)

    @Provides
    fun provideSetProductFavorite(repository: ProductRepository) =
        SetProductFavoriteUseCase(repository)

    @Provides
    fun provideSyncPendingProducts(repository: ProductRepository) =
        SyncPendingProductsUseCase(repository)
}
