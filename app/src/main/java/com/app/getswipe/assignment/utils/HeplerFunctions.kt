package com.app.getswipe.assignment.utils

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
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

fun getFilePathFromUri(context: Context, uri: Uri): String? {

    // Check if the Uri is a content Uri (content provider)
    if (uri.scheme == "content") {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = context.contentResolver.query(uri, projection, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                return it.getString(columnIndex) // Return the file path
            }
        }
    }
    // If the Uri is a file Uri, just return the path directly
    if (uri.scheme == "file") {
        return uri.path
    }
    return null
}