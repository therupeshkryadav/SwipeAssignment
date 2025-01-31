package com.app.getswipe.assignment.domain.repository

import com.app.getswipe.assignment.data.api.AddProductResponse
import com.app.getswipe.assignment.domain.model.Product
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface ProductRepository {
    suspend fun getAllProducts(): List<Product>
    suspend fun searchProducts(query: String): List<Product>
    suspend fun addProduct(
        productName: RequestBody,
        productType: RequestBody,
        price: RequestBody,
        tax: RequestBody,
        files: List<MultipartBody.Part>
    ): AddProductResponse

    fun saveProductOffline(
        productName: RequestBody,
        productType: RequestBody,
        price: RequestBody,
        tax: RequestBody,
        files: List<MultipartBody.Part>
    )

    fun getOfflineProducts(): List<Product>
    suspend fun syncOfflineProducts(): Result<Unit>
    fun clearOfflineProducts()
}