package com.app.getswipe.assignment.domain.model

import okhttp3.MultipartBody

data class Product(
    val product_name: String,
    val product_type: String,
    val price: Double,
    val tax: Double,
    val image: String? = null, // Optional image files
    val files: List<MultipartBody.Part> ? = null
)