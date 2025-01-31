package com.app.getswipe.assignment.data.local

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.core.net.toUri
import com.app.getswipe.assignment.domain.model.Product
import com.app.getswipe.assignment.utils.convertRequestBodyToString
import com.app.getswipe.assignment.utils.getFilePathFromUri
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.MultipartBody
import okhttp3.RequestBody

class SharedPreferencesDataSource(private val sharedPreferences: SharedPreferences,private val context: Context) {

    private val gson = Gson()

    // Save a product offline
    fun saveProductOffline(
        productName: RequestBody,
        productType: RequestBody,
        price: RequestBody,
        tax: RequestBody,
        files: List<MultipartBody.Part>
    ) {
        // Convert RequestBody to String
        val productNameStr = convertRequestBodyToString(productName)
        val productTypeStr = convertRequestBodyToString(productType)
        val priceDouble = convertRequestBodyToString(price).toDoubleOrNull() ?: 0.0
        val taxDouble = convertRequestBodyToString(tax).toDoubleOrNull() ?: 0.0

        val imageFile = files.firstOrNull()
        val filePath = imageFile?.let { getFilePathFromUri(context, it.toString().toUri()) }

        Log.d("fatalI",filePath.toString())

        val offlineProducts = getOfflineProducts().toMutableList()

        val product = Product(
            product_name = productNameStr,
            product_type = productTypeStr,
            price = priceDouble,
            tax = taxDouble,
            image = filePath
        )

        // Add the product to the offline list
        offlineProducts.add(product)

        // Save the updated list to SharedPreferences
        val editor = sharedPreferences.edit()
        val productListJson = gson.toJson(offlineProducts)
        editor.putString(OFFLINE_PRODUCTS_KEY, productListJson)
        editor.apply()
    }


    // Get all offline products
    fun getOfflineProducts(): List<Product> {
        val productListJson = sharedPreferences.getString(OFFLINE_PRODUCTS_KEY, "[]")
        val productType = object : TypeToken<List<Product>>() {}.type
        return gson.fromJson(productListJson, productType)
    }


    companion object {
        private const val OFFLINE_PRODUCTS_KEY = "added_products_saved"
    }
}




