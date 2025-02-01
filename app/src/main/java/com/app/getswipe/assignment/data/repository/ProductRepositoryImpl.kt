package com.app.getswipe.assignment.data.repository

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.app.getswipe.assignment.data.api.AddProductResponse
import com.app.getswipe.assignment.data.api.ProductRemoteDataSource
import com.app.getswipe.assignment.data.local.SharedPreferencesDataSource
import com.app.getswipe.assignment.domain.model.Product
import com.app.getswipe.assignment.domain.repository.ProductRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class ProductRepositoryImpl(
    private val productRemoteService: ProductRemoteDataSource,
    private val sharedPreferencesHelper: SharedPreferencesDataSource
) : ProductRepository {

    // Fetch all products from API
    override suspend fun getAllProducts(): List<Product> {
        return productRemoteService.getProducts()
    }

    // Search products
    override suspend fun searchProducts(query: String): List<Product> {
        return productRemoteService.searchProducts(query)
    }

    // Add product locally and remotely (handles offline case)
    override suspend fun addProduct(
        productName: RequestBody,
        productType: RequestBody,
        price: RequestBody,
        tax: RequestBody,
        files: List<MultipartBody.Part>
    ): AddProductResponse {
        return try {
            val response =
                productRemoteService.addProduct(productName, productType, price, tax, files)

            response.body() ?: throw Exception("Empty response from server")

        } catch (e: Exception) {
            throw Exception("Failed to add any product: ${e.message}")
        }
    }

    // Save product offline
    override fun saveProductOffline(
        productName: RequestBody,
        productType: RequestBody,
        price: RequestBody,
        tax: RequestBody,
        images:String,
        files: List<MultipartBody.Part>
    ) {
        Log.d("CheckXX", images + "   " + files)
        sharedPreferencesHelper.saveProductOffline(productName, productType, price, tax, images ,files)
    }

    // Get offline products to show when the network is not available
    override fun getOfflineProducts(): List<Product> {
        return sharedPreferencesHelper.getOfflineProducts()
    }

    // Sync offline products to the API when network is available
    override suspend fun syncOfflineProducts(): Result<Unit> {
        val offlineProducts = getOfflineProducts()
        return try {
            offlineProducts.forEach { product ->
                // Prepare files for uploading
                val files = product.image?.split(",")?.map {
                    MultipartBody.Part.createFormData(
                        "file",
                        it,
                        it.toRequestBody()  // Assuming `it` is the file path or URL
                    )
                } ?: emptyList()

                // Add product remotely
                addProduct(
                    productName = product.product_name.toRequestBody(),
                    productType = product.product_type.toRequestBody(),
                    price = product.price.toString().toRequestBody(),
                    tax = product.tax.toString().toRequestBody(),
                    files = files
                )
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}

fun isNetworkAvailable(connectivityManager: ConnectivityManager): Boolean {
    val network = connectivityManager.activeNetwork
    val capabilities = connectivityManager.getNetworkCapabilities(network)
    return capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
}



