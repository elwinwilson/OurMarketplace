package com.learn.ourmarketplace.presentation.products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.learn.ourmarketplace.core.network.NetworkMonitor
import com.learn.ourmarketplace.domain.model.Product
import com.learn.ourmarketplace.domain.usecase.ObserveProductsUseCase
import com.learn.ourmarketplace.domain.usecase.RefreshProductsUseCase
import com.learn.ourmarketplace.domain.usecase.SetProductFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProductsUiState(
    val products: List<Product> = emptyList(),
    val initialized: Boolean = false,
    val isRefreshing: Boolean = false,
    val isOnline: Boolean = true,
    val errorMessage: String? = null
)

@HiltViewModel
class ProductsViewModel @Inject constructor(
    observeProducts: ObserveProductsUseCase,
    private val refreshProducts: RefreshProductsUseCase,
    private val setProductFavorite: SetProductFavoriteUseCase,
    networkMonitor: NetworkMonitor
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProductsUiState())
    val uiState: StateFlow<ProductsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            observeProducts().collect { products ->
                _uiState.update { it.copy(products = products, initialized = true) }
            }
        }
        viewModelScope.launch {
            networkMonitor.isOnline.collect { online ->
                _uiState.update { it.copy(isOnline = online) }
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
