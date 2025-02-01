package com.app.getswipe.assignment.domain.usecase

import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import androidx.core.net.toUri
import com.app.getswipe.assignment.data.api.AddProductResponse
import com.app.getswipe.assignment.data.repository.isNetworkAvailable
import com.app.getswipe.assignment.domain.model.Product
import com.app.getswipe.assignment.domain.repository.ProductRepository
import com.app.getswipe.assignment.presentation.ui.screens.addScreen.uriToFile
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class GetProductsUseCase(
    private val productRepository: ProductRepository,
    private val connectivityManager: ConnectivityManager
) {
    suspend fun execute(): List<Product> {
        if (!isNetworkAvailable(connectivityManager)) {
            return productRepository.getOfflineProducts()
        } else {
            return productRepository.getAllProducts()
        }
    }
}

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
        tax: RequestBody,
        files: List<MultipartBody.Part>
    ): AddProductResponse {

        val response = repository.addProduct(
            productName = productName,
            productType = productType,
            price = price,
            tax = tax,
            files = files
        )

        repository.saveProductOffline(
            productName = productName,
            productType = productType,
            price = price,
            tax = tax,
            files = files,
            images = images
        )

        if (response.success) {
            return response
        } else {
            throw Exception("Failed to add product to Server.\n Product saved offline.")
        }
    }
}

class SyncOfflineProductsUseCase(
    private val productRepository: ProductRepository,
    private val context: Context
) {
    suspend fun execute(): Result<Unit> {
        return try {

            val offlineProducts = productRepository.getOfflineProducts()

            for (product in offlineProducts) {

                Log.d("fatalX", product.toString())

                Log.d("fatalX", product.image.toString())

                val imagePart = product.image!!.toUri().let {
                    val infile = uriToFile(it, context)
                    infile?.let { file ->
                        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                        MultipartBody.Part.createFormData("files[]", file.name, requestFile)
                    }
                }

                val imageParts = listOfNotNull(imagePart)
                // Call the addProduct repository method
                productRepository.addProduct(
                    productName = product.product_name.toRequestBody(),
                    productType = product.product_type.toRequestBody(),
                    price = product.price.toString().toRequestBody(),
                    tax = product.tax.toString().toRequestBody(),
                    files = imageParts
                )
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

