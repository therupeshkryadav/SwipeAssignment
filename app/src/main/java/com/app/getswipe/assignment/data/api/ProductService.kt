package com.app.getswipe.assignment.data.api

import com.app.getswipe.assignment.domain.model.Product
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ProductService {

    // Get the list of products
    @GET("api/public/get")
    suspend fun getProducts(): List<Product>

    // Add a new product
    @Multipart
    @POST("api/public/add")
    suspend fun addProduct(
        @Part("product_name") name: RequestBody,
        @Part("product_type") type: RequestBody,
        @Part("price") price: RequestBody,
        @Part("tax") tax: RequestBody,
        @Part files: List<MultipartBody.Part>
    ): Response<AddProductResponse>
}
