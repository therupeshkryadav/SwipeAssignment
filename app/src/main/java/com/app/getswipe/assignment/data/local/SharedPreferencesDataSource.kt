package com.app.getswipe.assignment.data.local

import android.content.SharedPreferences
import android.util.Log
import com.app.getswipe.assignment.domain.model.Product
import com.app.getswipe.assignment.utils.convertRequestBodyToString
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.RequestBody

class SharedPreferencesDataSource(private val sharedPreferences: SharedPreferences) {

    private val gson = Gson()

    // Save a product offline
    fun saveProductOffline(
        productName: RequestBody,
        productType: RequestBody,
        price: RequestBody,
        tax: RequestBody,
        files: String
    ) {
        // Convert RequestBody to String
        val productNameStr = convertRequestBodyToString(productName)
        val productTypeStr = convertRequestBodyToString(productType)
        val priceDouble = convertRequestBodyToString(price).toDoubleOrNull() ?: 0.0
        val taxDouble = convertRequestBodyToString(tax).toDoubleOrNull() ?: 0.0


        Log.d("fatalI",files)

        val offlineProducts = getOfflineProducts().toMutableList()

        val product = Product(
            product_name = productNameStr,
            product_type = productTypeStr,
            price = priceDouble,
            tax = taxDouble,
            image = files
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
        private const val OFFLINE_PRODUCTS_KEY = "saved_products"
    }
}




