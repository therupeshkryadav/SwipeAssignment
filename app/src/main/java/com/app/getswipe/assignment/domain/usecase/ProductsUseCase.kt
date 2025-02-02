package com.app.getswipe.assignment.domain.usecase

import android.net.ConnectivityManager
import android.util.Log
import com.app.getswipe.assignment.data.api.AddProductResponse
import com.app.getswipe.assignment.data.repository.isNetworkAvailable
import com.app.getswipe.assignment.domain.model.Product
import com.app.getswipe.assignment.domain.repository.ProductRepository
import okhttp3.RequestBody

class GetProductsUseCase(
    private val productRepository: ProductRepository,
    private val connectivityManager: ConnectivityManager
) {
    suspend fun execute(): List<Product> {
        return try {
            if (!isNetworkAvailable(connectivityManager)) {
                productRepository.getOfflineProducts()
            } else {
                productRepository.getAllProducts()
            }
        } catch (e: Exception) {
            // Handle the exception properly (log it, return a fallback value, etc.)
            productRepository.getOfflineProducts()
        }
    }}
class SearchProductsUseCase(private val productRepository: ProductRepository) {
    suspend fun execute(query: String): List<Product> {
        return productRepository.searchProducts(query)
    }
}

class AddProductUseCase(
    private val repository: ProductRepository
) {
    suspend fun execute(
        images: String,
        productName: RequestBody,
        productType: RequestBody,
        price: RequestBody,
        tax: RequestBody
    ): AddProductResponse {
        return try {
            val response = repository.addProduct(
                productName = productName,
                productType = productType,
                price = price,
                tax = tax,
                images = images
            )
            response
        }catch (e: Exception){
            throw Exception("Failed to add product to the server ${e.message}")
        }
    }
}

