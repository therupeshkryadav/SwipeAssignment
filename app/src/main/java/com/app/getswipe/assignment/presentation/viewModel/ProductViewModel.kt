package com.app.getswipe.assignment.presentation.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.getswipe.assignment.data.api.AddProductResponse
import com.app.getswipe.assignment.domain.model.Product
import com.app.getswipe.assignment.domain.usecase.AddProductUseCase
import com.app.getswipe.assignment.domain.usecase.GetProductsUseCase
import com.app.getswipe.assignment.domain.usecase.SearchProductsUseCase
import kotlinx.coroutines.launch
import okhttp3.RequestBody

class ProductViewModel(
    private val getProductsUseCase: GetProductsUseCase,
    private val searchProductsUseCase: SearchProductsUseCase,
    private val addProductUseCase: AddProductUseCase
) : ViewModel() {

    private val _products = MutableLiveData<List<Product>>()
    val products: LiveData<List<Product>> = _products

    private val _searchQuery = MutableLiveData<String>()
    val searchQuery: LiveData<String> = _searchQuery

    private val _addProductState = MutableLiveData<AddProductResponse?>()
    val addProductState: LiveData<AddProductResponse?> = _addProductState

    private val _loading = MutableLiveData<Boolean>()

    fun getAllProducts() {
        viewModelScope.launch {
            try {
                val productsList = getProductsUseCase.execute()
                _products.value = productsList
            } catch (e: Exception) {
                _products.value = emptyList()
            }
        }
    }

    fun searchProducts(query: String) {
        _searchQuery.value = query
        _loading.value = true
        viewModelScope.launch {
            try {
                val filteredProducts = searchProductsUseCase.execute(query)
                _products.value = filteredProducts
            } catch (e: Exception) {
                _products.value = emptyList()
            } finally {
                _loading.value = false
            }
        }
    }

    fun addProduct(
        image: String,
        productName: RequestBody,
        productType: RequestBody,
        price: RequestBody,
        tax: RequestBody
    ) {
        viewModelScope.launch {
            try {
                val response = addProductUseCase.execute(
                    images = image,
                    productName = productName,
                    productType = productType,
                    price = price,
                    tax = tax
                )
                _addProductState.value = response
            } catch (e: Exception) {
                _addProductState.value = AddProductResponse(
                    message = "Failed to add product, Saved Offline!!",
                    productDetails = null,
                    productId = -1,
                    success = false
                )
            }
        }
    }
}
