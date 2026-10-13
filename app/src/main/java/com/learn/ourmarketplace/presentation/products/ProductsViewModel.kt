package com.learn.ourmarketplace.presentation.products

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.learn.ourmarketplace.core.di.IoDispatcher
import com.learn.ourmarketplace.core.network.NetworkMonitor
import com.learn.ourmarketplace.domain.model.Product
import com.learn.ourmarketplace.domain.usecase.AddProductUseCase
import com.learn.ourmarketplace.domain.usecase.ObserveProductsUseCase
import com.learn.ourmarketplace.domain.usecase.RefreshProductsUseCase
import com.learn.ourmarketplace.domain.usecase.SetProductFavoriteUseCase
import com.learn.ourmarketplace.domain.usecase.SyncPendingProductsUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.File
import java.io.IOException
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class ProductsUiState(
    val products: List<Product> = emptyList(),
    val initialized: Boolean = false,
    val isRefreshing: Boolean = false,
    val isOnline: Boolean = true,
    val errorMessage: String? = null,
    val syncErrorMessage: String? = null
)

@HiltViewModel
class ProductsViewModel @Inject constructor(
    observeProducts: ObserveProductsUseCase,
    private val refreshProducts: RefreshProductsUseCase,
    private val setProductFavorite: SetProductFavoriteUseCase,
    private val addProductUseCase: AddProductUseCase,
    private val syncPendingProducts: SyncPendingProductsUseCase,
    networkMonitor: NetworkMonitor,
    @param:ApplicationContext private val context: Context,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProductsUiState())
    val uiState: StateFlow<ProductsUiState> = _uiState.asStateFlow()
    private val syncMutex = Mutex()

    init {
        viewModelScope.launch {
            observeProducts().collect { products ->
                _uiState.update { it.copy(products = products, initialized = true) }
            }
        }
        viewModelScope.launch {
            networkMonitor.isOnline.collect { online ->
                _uiState.update { it.copy(isOnline = online) }
                if (online) syncPendingSafely()
            }
        }
        refresh()
    }

    fun refresh() {
        if (_uiState.value.isRefreshing) return
        _uiState.update { it.copy(isRefreshing = true, errorMessage = null) }
        viewModelScope.launch {
            refreshProductsSafely()
        }
    }

    fun toggleFavorite(product: Product) {
        viewModelScope.launch {
            try {
                setProductFavorite(product.id, !product.isFavorite)
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                _uiState.update {
                    it.copy(errorMessage = error.message ?: "Could not update favorite.")
                }
            }
        }
    }

    suspend fun addProduct(
        title: String,
        description: String,
        price: Double,
        imageUri: Uri
    ) {
        val savedImageUri = saveImageToAppStorage(imageUri)
        addProductUseCase(title, description, price, savedImageUri)
        if (_uiState.value.isOnline) {
            viewModelScope.launch { syncPendingSafely() }
        }
    }

    private suspend fun saveImageToAppStorage(imageUri: Uri): String = withContext(ioDispatcher) {
        val imageDirectory = File(context.filesDir, "product-images")
        if (!imageDirectory.exists() && !imageDirectory.mkdirs()) {
            throw IOException("Could not create product image storage.")
        }

        val destination = File(imageDirectory, "${java.util.UUID.randomUUID()}.image")
        val input = context.contentResolver.openInputStream(imageUri)
            ?: throw IOException("Could not read the selected image.")
        try {
            input.use { source ->
                destination.outputStream().use { output -> source.copyTo(output) }
            }
        } catch (error: Exception) {
            destination.delete()
            throw error
        }
        Uri.fromFile(destination).toString()
    }

    private suspend fun syncPendingSafely() {
        try {
            syncMutex.withLock { syncPendingProducts() }
            _uiState.update { it.copy(syncErrorMessage = null) }
        } catch (error: CancellationException) {
            throw error
        } catch (error: Exception) {
            _uiState.update {
                it.copy(syncErrorMessage = error.message ?: "Could not sync new products.")
            }
        }
    }

    private suspend fun refreshProductsSafely() {
        try {
            refreshProducts()
            _uiState.update { it.copy(isRefreshing = false, errorMessage = null) }
        } catch (error: CancellationException) {
            throw error
        } catch (error: Exception) {
            _uiState.update {
                it.copy(
                    isRefreshing = false,
                    errorMessage = error.message ?: "Could not load products."
                )
            }
        }
    }
}
