package com.app.getswipe.assignment.presentation.ui.screens.productList.component

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil.compose.rememberAsyncImagePainter
import com.app.getswipe.assignment.R
import com.app.getswipe.assignment.domain.model.Product

@Composable
fun ProductItem(product: Product) {
    val productName = product.product_name ?: "Unnamed Product"

    val imageUrl = product.image

    // Use a fallback image URL if image is null or empty
    val finalImageUri = if (imageUrl.isNullOrEmpty()) {
        Uri.parse("android.resource://com.app.getswipe.assignment/${R.drawable.product_placeholder}")
    } else {
        imageUrl.toUri()
    }


    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable { }
            .background(color = Color.LightGray)
            .border(width = 1.dp, color = Color.LightGray, shape = RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(
            containerColor = Color.LightGray
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {


            Image(
                painter = rememberAsyncImagePainter(
                    model = finalImageUri,
                    placeholder = painterResource(id = R.drawable.product_placeholder),
                    error = painterResource(id = R.drawable.product_placeholder)
                ),
                contentDescription = productName,
                modifier = Modifier
                    .fillMaxWidth(0.35f)
                    .fillMaxHeight(1f)
                    .clip(shape = RectangleShape)
                    .border(width = 1.dp, color = Color.DarkGray),
                colorFilter = ColorFilter.tint(
                    color = Color.Transparent,
                    blendMode = BlendMode.Color
                ),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {

                Text(
                    text = productName,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "ProductType: ${product.product_type}",
                    style = MaterialTheme.typography.labelSmall
                )
                Text(
                    text = "Price: ${product.price}",
                    style = MaterialTheme.typography.bodySmall
                )

                Text(
                    text = "Tax: ${product.tax}",
                    fontWeight = FontWeight.W300,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
