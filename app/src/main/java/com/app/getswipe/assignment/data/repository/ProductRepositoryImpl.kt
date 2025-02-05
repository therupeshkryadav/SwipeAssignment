package com.app.getswipe.assignment.data.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.core.net.toUri
import com.app.getswipe.assignment.data.api.AddProductResponse
import com.app.getswipe.assignment.data.api.ProductRemoteDataSource
import com.app.getswipe.assignment.data.local.SharedPreferencesDataSource
import com.app.getswipe.assignment.domain.model.Product
import com.app.getswipe.assignment.domain.repository.ProductRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class ProductRepositoryImpl(
    private val productRemoteService: ProductRemoteDataSource,
    private val sharedPreferencesHelper: SharedPreferencesDataSource,
    private val connectivityManager: ConnectivityManager,
    private val context: Context
) : ProductRepository {

    override suspend fun getAllProducts(): List<Product> {
        // Check if the network is available
        if (isNetworkAvailable(connectivityManager)) {
            Log.d("ProductAdded", "Network available! Syncing offline products...")
            Toast.makeText(context, "Products Syncing...", Toast.LENGTH_LONG).show()
            //  Attempt to sync offline products
            syncOfflineProducts()
        } else {
            Log.d("ProductSync", "No network! Offline products remain unsynced.")
        }

        // Fetch and return online products
        return productRemoteService.getProducts()
    }

    // Search products
    override suspend fun searchProducts(query: String): List<Product> {
        return productRemoteService.searchProducts(query)
    }

    override suspend fun addProduct(
        productName: RequestBody,
        productType: RequestBody,
        price: RequestBody,
        tax: RequestBody,
        images: String
    ): AddProductResponse {
        return if (isNetworkAvailable(connectivityManager)) {
            // If network is available, try sending to the server
            sendProductToServer(productName, productType, price, tax, images)
        } else {
            // If network is unavailable, save product offline
            saveProductOffline(productName, productType, price, tax, images)
        }
    }

    private suspend fun sendProductToServer(
        productName: RequestBody,
        productType: RequestBody,
        price: RequestBody,
        tax: RequestBody,
        images: String
    ): AddProductResponse {
        return try {
            // Attempt to send the product to the server
            val response = productRemoteService.addProduct(
                productName = productName,
                productType = productType,
                price = price,
                tax = tax,
                images = images
            )

            response.body() ?: throw Exception("Empty response from server")
        } catch (e: Exception) {
            // Handle network request failure
            throw Exception("Failed to add product to server. Error: ${e.message}")
        }
    }

    private  fun saveProductOffline(
        productName: RequestBody,
        productType: RequestBody,
        price: RequestBody,
        tax: RequestBody,
        images: String
    ): AddProductResponse {
        return try {
            val imagePath = getFilePath(context, images.toUri()).toString()

            // Save the product offline if the network is unavailable
            sharedPreferencesHelper.saveProductOffline(
                productName = productName,
                productType = productType,
                price = price,
                tax = tax,
                images = imagePath
            )

            AddProductResponse(
                success = false,
                message = "Product saved offline. Sync will happen when online."
            )
        } catch (e: Exception) {
            // Handle error while saving offline
            throw Exception("Error saving product offline. Error: ${e.message}")
        }
    }



    // Get offline products to show when the network is not available
    override suspend fun getUploadedProducts(): List<Product> {
        Log.d(
            "ProductXXX",
            "From IMPL --> " + sharedPreferencesHelper.getOfflineProducts().toString()
        )
        return sharedPreferencesHelper.getOfflineProducts()
    }

    private suspend fun syncOfflineProducts() {
        val offlineProducts = getUploadedProducts()
        Log.d("ProductSync", "syncOfflineProducts called")
        if (offlineProducts.isEmpty()) {
            Log.d("ProductSync", "No offline products to sync.")
            return
        }

        for (product in offlineProducts) {
            try {
                val imageUri = product.image?.toUri()
                val imagePath = imageUri.toString()


                val productName = product.product_name.toRequestBody()
                val productType = product.product_type.toRequestBody()
                val price = product.price.toString().toRequestBody()
                val tax = product.tax.toString().toRequestBody()


                val response = productRemoteService.addProduct(
                    productName = productName,
                    productType = productType,
                    price = price,
                    tax = tax,
                    images = imagePath ?: "" // Handle null case
                )

                if (response.isSuccessful) {
                    Log.d(
                        "ProductAdded",
                        "Offline Product 33 synced successfully: ${product.toString()}"
                    )

                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            context,
                            "Offline Product ${product.product_name} Synced Successfully!!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    // Remove the synced product and refresh the list
                    sharedPreferencesHelper.removeOfflineProduct(product)


                } else {
                    Log.e("ProductSync", "Failed to sync product: ${product.product_name}")
                }

            } catch (e: Exception) {
                Log.e("ProductSync", "Error syncing product: ${e.message}")
            }
        }
    }

}


fun isNetworkAvailable(connectivityManager: ConnectivityManager): Boolean {
    val network = connectivityManager.activeNetwork
    val capabilities = connectivityManager.getNetworkCapabilities(network)
    return capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
}

fun getFilePath(context: Context, uri: Uri): String? {
    return when {
        // Check if it's a File URI (file://)
        uri.scheme == "file" -> uri.path

        // Check if it's a Media Store URI (Gallery, Camera)
        uri.scheme == "content" -> getRealPathFromUri(context, uri)

        else -> null
    }
}

private fun getRealPathFromUri(context: Context, uri: Uri): String? {
    val projection = arrayOf(MediaStore.Images.Media.DATA)
    context.contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
        if (cursor.moveToFirst()) {
            val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            return cursor.getString(columnIndex)
        }
    }
    return null
}