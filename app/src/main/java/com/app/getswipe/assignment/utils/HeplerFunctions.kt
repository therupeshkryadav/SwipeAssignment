package com.app.getswipe.assignment.utils

import com.app.getswipe.assignment.data.model.ProductEntity
import com.app.getswipe.assignment.domain.model.Product
import okhttp3.RequestBody
import okio.Buffer


fun convertRequestBodyToString(requestBody: RequestBody): String {
    val buffer = Buffer()
    requestBody.writeTo(buffer)
    return buffer.readUtf8()
}


fun ProductEntity.toProduct(): Product {
    return Product(
        product_name = this.productName,
        product_type = this.productType,
        price = this.price,
        tax = this.tax,
        image = this.images
    )
}

fun List<ProductEntity>.toProductList(): List<Product> {
    return this.map { it.toProduct() }
}