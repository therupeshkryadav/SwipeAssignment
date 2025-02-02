package com.app.getswipe.assignment.data.api

import com.app.getswipe.assignment.domain.model.Product

data class AddProductResponse(
    val message: String,
    val productDetails: Product? = null,
    val productId: Int? = null,
    val success: Boolean
)