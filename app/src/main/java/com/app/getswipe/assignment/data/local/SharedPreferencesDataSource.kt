package com.app.getswipe.assignment.data.local

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.core.net.toUri
import com.app.getswipe.assignment.data.model.ProductEntity
import com.app.getswipe.assignment.domain.model.Product
import com.app.getswipe.assignment.presentation.ui.screens.addScreen.uriToFile
import com.app.getswipe.assignment.utils.convertRequestBodyToString
import com.app.getswipe.assignment.utils.toProduct
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody

class SharedPreferencesDataSource(private val sharedPreferences: SharedPreferences,private val context: Context) {

    private val gson = Gson()

    // Save a product offline
    fun saveProductOffline(
        images: String,
        productName: RequestBody,
        productType: RequestBody,
        price: RequestBody,
        tax: RequestBody
    ) {
        val product =Product(
            product_name = convertRequestBodyToString(productName),
            product_type = convertRequestBodyToString(productType),
            price = convertRequestBodyToString(price).toDoubleOrNull()?:0.0,
            tax = convertRequestBodyToString(tax).toDoubleOrNull()?:0.0,
            image = images
        )
        val offlineProducts = getOfflineProducts().toMutableList()
        // Add the product to the offline list
        offlineProducts.add(product)

        Log.d("ProductAdded", product.toString())
        Log.d("ProductXXX",getOfflineProducts().toString())

        // Save the updated list to SharedPreferences
        val editor = sharedPreferences.edit()
        val productListJson = gson.toJson(offlineProducts)
        editor.putString(OFFLINE_PRODUCTS_KEY, productListJson)
        editor.apply()
    }


    // Get all offline products
    fun getOfflineProducts(): List<Product> {
        val productListJson = sharedPreferences.getString(OFFLINE_PRODUCTS_KEY, "[]")
        Log.d("ProductXXX",productListJson.toString())
        val productType = object : TypeToken<List<Product>>() {}.type
        return gson.fromJson(productListJson, productType)
    }

    fun removeOfflineProduct(product: Product) {
        val offlineProducts = getOfflineProducts().toMutableList()
        offlineProducts.removeIf {
            it.product_name == product.product_name &&
                    it.product_type == product.product_type &&
                    it.price == product.price &&
                    it.tax == product.tax &&
                    it.image == product.image
        }

        // Update SharedPreferences
        val editor = sharedPreferences.edit()
        editor.putString(OFFLINE_PRODUCTS_KEY, gson.toJson(offlineProducts))
        editor.apply()

        Log.d("ProductXXX", "Product removed from offline storage: ${product.product_name}")
    }



    companion object {
        private const val OFFLINE_PRODUCTS_KEY = "saved_Item_seen"
    }
}




