package com.app.getswipe.assignment.domain.model

data class Product(
    val product_name: String,
    val product_type: String,
    val price: Double,
    val tax: Double,
    val image: String? = null // Optional image files
)