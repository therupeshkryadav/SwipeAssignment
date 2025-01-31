package com.app.getswipe.assignment.data.api

import com.app.getswipe.assignment.domain.model.Product
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response

class ProductRemoteDataSource(private val productService: ProductService) {

    // Fetch all products
    suspend fun getProducts(): List<Product> {
        return try {
            productService.getProducts()  // Assuming this returns a list of products from the API
        } catch (e: Exception) {
            emptyList()  // Return empty list in case of error
        }
    }

    // Add a new product with multiple images
    suspend fun addProduct(
        productName: RequestBody,
        productType: RequestBody,
        price: RequestBody,
        tax: RequestBody,
        files: List<MultipartBody.Part>  // List of images
    ): Response<AddProductResponse> {
        return try {
            productService.addProduct(
                name = productName,
                type = productType,
                price = price,
                tax = tax,
                files = files
            )
        } catch (e: Exception) {
            throw Exception("Failed to add product: ${e.message}")  // Throw a custom exception for better error handling
        }
    }

    // Search products based on a query string (client-side filtering)
    suspend fun searchProducts(query: String): List<Product> {
        return try {
            val products = productService.getProducts()  // Fetch all products
            val filteredProducts = products.filter {
                it.product_name.contains(query, ignoreCase = true) ||  // Check if the product name contains the query
                        it.product_type.contains(query, ignoreCase = true)     // Check if the product type contains the query
            }
            filteredProducts
        } catch (e: Exception) {
            emptyList()  // Return empty list in case of an error
        }
    }
}
