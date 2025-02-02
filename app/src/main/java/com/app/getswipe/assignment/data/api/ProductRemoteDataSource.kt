package com.app.getswipe.assignment.data.api

import android.content.Context
import androidx.core.net.toUri
import com.app.getswipe.assignment.domain.model.Product
import com.app.getswipe.assignment.presentation.ui.screens.addScreen.uriToFile
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Response

class ProductRemoteDataSource(private val productService: ProductService,private val context: Context) {

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
        images:String // List of images
    ): Response<AddProductResponse> {
        return try {
            val imagePart = images.toUri().let {
                val infile = uriToFile(it, context)
                infile?.let { file ->
                    val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                    MultipartBody.Part.createFormData("files[]", file.name, requestFile)
                }
            }

            val imageParts = listOfNotNull(imagePart)
            productService.addProduct(
                name = productName,
                type = productType,
                price = price,
                tax = tax,
                files = imageParts
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
