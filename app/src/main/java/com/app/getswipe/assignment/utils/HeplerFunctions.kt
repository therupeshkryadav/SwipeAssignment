package com.app.getswipe.assignment.utils

import android.util.Base64
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okio.Buffer
import java.io.File

fun convertRequestBodyToString(requestBody: RequestBody): String {
    val buffer = Buffer()
    requestBody.writeTo(buffer)
    return buffer.readUtf8()
}

fun convertFileToBase64(filePart: MultipartBody.Part): String? {
    val file = File(filePart.body.toString() ?: "")
    return if (file.exists()) {
        val byteArray = file.readBytes()
        Base64.encodeToString(byteArray, Base64.DEFAULT) // Convert to Base64
    } else {
        null
    }
}