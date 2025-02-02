package com.app.getswipe.assignment.data.model

import okhttp3.MultipartBody

data class ProductEntity(
    val productName: String,
    val productType: String,
    val price: Double,
    val tax: Double,
    val files: List<MultipartBody.Part>? = null,// Optional image files
    val images: String
)