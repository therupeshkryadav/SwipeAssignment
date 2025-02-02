package com.app.getswipe.assignment.domain.repository

import com.app.getswipe.assignment.data.api.AddProductResponse
import com.app.getswipe.assignment.domain.model.Product
import okhttp3.RequestBody

interface ProductRepository {
    suspend fun getAllProducts(): List<Product>
    suspend fun searchProducts(query: String): List<Product>
    suspend fun addProduct(
        productName: RequestBody,
        productType: RequestBody,
        price: RequestBody,
        tax: RequestBody,
        images: String
    ): AddProductResponse

    suspend fun getOfflineProducts(): List<Product>
}