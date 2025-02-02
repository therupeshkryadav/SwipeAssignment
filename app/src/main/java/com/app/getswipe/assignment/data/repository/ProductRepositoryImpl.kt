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
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okio.Path.Companion.toPath

class ProductRepositoryImpl(
    private val productRemoteService: ProductRemoteDataSource,
    private val sharedPreferencesHelper: SharedPreferencesDataSource,
    private val connectivityManager: ConnectivityManager,
    private val context: Context
) : ProductRepository {

    override suspend fun getAllProducts(): List<Product> {
        // ✅ Check if the network is available
        if (isNetworkAvailable(connectivityManager)) {
            Log.d("ProductAdded", "Network available! Syncing offline products...")
            Toast.makeText(context, "Products Syncing...", Toast.LENGTH_LONG).show()
            // ✅ Attempt to sync offline products
            syncOfflineProducts()
        } else {
            Log.d("ProductSync", "No network! Offline products remain unsynced.")
        }

        // ✅ Fetch and return online products
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
        try {

            sharedPreferencesHelper.saveProductOffline(
                productName = productName,
                productType = productType,
                price = price,
                tax = tax,
                images = getFilePath(context, images.toUri())!!
            )
            // ✅ Attempt to send the product to the server
            val response = productRemoteService.addProduct(
                productName = productName,
                productType = productType,
                price = price,
                tax = tax,
                images = images
            )

            if (response.isSuccessful) {
                Log.d(
                    "ProductAdded",
                    "Offline Product Added successfully: ${response.body().toString()}"
                )
                Toast.makeText(
                    context,
                    "Product Added to the Server Offline Product Added successfully: ${response.body().toString()}",
                    Toast.LENGTH_SHORT
                ).show()
                // ✅ Save the product offline first (handles offline cases)

            }else{
                Toast.makeText(
                    context,
                    "Offline Product Added successfully: ${response.body().toString()}",
                    Toast.LENGTH_SHORT
                ).show()
            }
            return response.body() ?: throw Exception("Empty response from server")

        } catch (e: Exception) {
            // ✅ If network request fails, save offline and retry later
            throw Exception("Product saved offline. Sync will happen when online. Error: ${e.message}")

        }
    }

    // Get offline products to show when the network is not available
    override suspend fun getOfflineProducts(): List<Product> {
        Log.d(
            "ProductXXX",
            "From IMPL --> " + sharedPreferencesHelper.getOfflineProducts().toString()
        )
        return sharedPreferencesHelper.getOfflineProducts()
    }

    private suspend fun syncOfflineProducts() {
        val offlineProducts = sharedPreferencesHelper.getOfflineProducts()
        if (offlineProducts.isEmpty()) {
            Log.d("ProductSync", "No offline products to sync.")
            return
        }

        for (product in offlineProducts) {
            try {
                val imageUri = product.image?.toUri()
                val imagePath = imageUri.toString()

                // Convert data for API request
                val productName = product.product_name.toRequestBody()
                val productType = product.product_type.toRequestBody()
                val price = product.price.toString().toRequestBody()
                val tax = product.tax.toString().toRequestBody()

                // ✅ Send to API
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
                    Toast.makeText(
                        context,
                        "Offline Product Synced Successfully!!",
                        Toast.LENGTH_SHORT
                    ).show()
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