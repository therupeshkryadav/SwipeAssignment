package com.app.getswipe.assignment.presentation.ui.screens.addScreen

import NotificationHelper
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.rememberAsyncImagePainter
import com.app.getswipe.assignment.R
import com.app.getswipe.assignment.presentation.ui.screens.addScreen.components.CategorySection
import com.app.getswipe.assignment.presentation.ui.screens.addScreen.components.DetailRow
import com.app.getswipe.assignment.presentation.ui.screens.utils.customTopAppBar.CustomTopAppBar
import com.app.getswipe.assignment.presentation.viewModel.ProductViewModel
import kotlinx.coroutines.delay
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.koin.androidx.compose.koinViewModel
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

@Composable
fun AddScreen(
    navBack: () -> Unit,
    navSuccess: () -> Unit,
    context: Context
) {
    val productViewModel: ProductViewModel = koinViewModel()
    val addProductState by productViewModel.addProductState.observeAsState()

    var selectedProductImageUri by remember { mutableStateOf<Uri?>(null) }
    var productName by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var tax by remember { mutableStateOf("") }
    var productType by remember { mutableStateOf("") }

    val categoryOptions = listOf(
        "General", "Electronics", "Vehicles", "Furniture", "Home Appliances",
        "Kitchen Appliances", "Gadgets & Accessories", "Personal & Lifestyle Products",
        "Tools & Equipment", "Health & Medical Devices", "Wearables", "Others"
    )

    var expanded by remember { mutableStateOf(false) }

    val productImgLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            selectedProductImageUri = uri
                ?: Uri.parse("android.resource://com.app.getswipe.assignment/${R.drawable.product_placeholder}")
        }

    var isNameError by remember { mutableStateOf(false) }
    var isTypeError by remember { mutableStateOf(false) }
    var isPriceError by remember { mutableStateOf(false) }
    var isTaxError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        CustomTopAppBar(
            title = {
                Text(
                    text = "Add Product",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            navigationIcon = {
                IconButton(onClick = { navBack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        tint = Color.Black,
                        contentDescription = "Back"
                    )
                }
            },
            actions = {
                // Floating Action Button
                IconButton(onClick = {
                    // Input validation
                    isNameError = productName.isBlank()
                    isTypeError = productType.isBlank()
                    isPriceError =
                        price.isEmpty() || price.toDoubleOrNull() == null || price.toDouble() < 0.0
                    isTaxError =
                        tax.isEmpty() || tax.toDoubleOrNull() == null || tax.toDouble() < 0.0

                    if (!isNameError && !isTypeError && !isPriceError && !isTaxError) {
                        val imagePart = selectedProductImageUri?.let {
                            val infile = uriToFile(it, context)
                            infile?.let { file ->
                                val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                                MultipartBody.Part.createFormData("files[]", file.name, requestFile)
                            }
                        }

                        val imageParts = listOfNotNull(imagePart)
                        Log.d("images", imageParts.toString())
                        val productNameRequestBody =
                            productName.toRequestBody("text/plain".toMediaTypeOrNull())
                        val productTypeRequestBody =
                            productType.toRequestBody("text/plain".toMediaTypeOrNull())
                        val priceRequestBody =
                            price.toRequestBody("text/plain".toMediaTypeOrNull())
                        val taxRequestBody =
                            tax.toRequestBody("text/plain".toMediaTypeOrNull())

                        productViewModel.addProduct(
                            productName = productNameRequestBody,
                            productType = productTypeRequestBody,
                            price = priceRequestBody,
                            tax = taxRequestBody,
                            files = imageParts
                        )
                    }
                }) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        tint = Color.Black,
                        contentDescription = "Add Product Card Icon"
                    )
                }
            }
        )

        Column(modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()))
        {

            Spacer(Modifier.height(24.dp))
            Image(
                painter = rememberAsyncImagePainter(selectedProductImageUri?: Uri.parse("android.resource://com.app.getswipe.assignment/${R.drawable.product_placeholder}")),
                contentDescription = "Product Image",
                contentScale = ContentScale.Crop,
                alignment = Alignment.Center,
                modifier = Modifier
                    .size(300.dp)
                    .align(Alignment.CenterHorizontally)
                    .aspectRatio(1f)  // Ensures 1:1 ratio
                    .border(width = 1.dp, Color.Black)
            )

            IconButton(
                onClick = { productImgLauncher.launch("image/*") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .background(Color.Black)
            ) {
                Row(
                    modifier = Modifier.wrapContentWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (selectedProductImageUri == null) "Upload Product Image" else "Change Image",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        painter = painterResource(if (selectedProductImageUri == null) R.drawable.ic_upload else R.drawable.ic_change),
                        contentDescription = "Add Product Image",
                        tint = Color.White
                    )
                }
            }

            DetailRow(
                label = "Product Name",
                updatedValue = productName,
                onValueChange = { productName = it },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                ),
                placeHolder = "write product name -->",
                isError = isNameError,
                errorMessage = "Not correctly filled ? ,Product Name Required !!"
            )
            CategorySection(
                updatedCategory = productType,
                isError = isTypeError,
                errorMessage = "Not correctly filled ? ,Product Category Type Required !!",
                onCategoryChange = { productType = it },
                onCategorySelection = { expanded = !expanded }
            )
            // Dropdown Menu
            if (expanded) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(horizontal = 8.dp)
                        .background(Color.White, RoundedCornerShape(20.dp))
                        .border(1.dp, Color.LightGray, RoundedCornerShape(20.dp))
                ) {
                    Column {
                        categoryOptions.forEach { category ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        productType = category
                                        expanded = false
                                    }
                                    .padding(12.dp)
                                    .background(Color.Transparent)
                            ) {
                                Text(
                                    text = category,
                                    fontSize = 16.sp,
                                    color = Color.Black
                                )
                            }
                        }
                    }
                }
            }

            DetailRow(
                label = "Selling Price",
                updatedValue = price,
                onValueChange = { price = it },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                isError = isPriceError,
                errorMessage = "Not correctly filled ? ,Selling Price Required !!",
                placeHolder = "price in rupees"
            )

            DetailRow(label = "Tax",
                updatedValue = tax,
                placeHolder = "Tax %",
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                isError = isTaxError,
                errorMessage = "Not correctly filled ? ,Tax in Percentage %",
                onValueChange = { tax = it })
        }
    }

    // Show Success or Failure Dialog based on the response
    addProductState?.let {
        if (it.success) {
            NotificationHelper.sendNotification(context,"Product name: $productName ","Saved Offline and Added Successfully!!")
            SuccessDialog(it.message, navSuccess = navSuccess)
        } else {
            FailureDialog(it.message, navSuccess = navSuccess)
        }
    }
}

@Composable
fun LoadingDialog() {
    Dialog(onDismissRequest = {}) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Transparent)
                .padding(40.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(Modifier.padding(40.dp), color = Color.Black)
        }
    }
}

@Composable
fun SuccessDialog(message: String, navSuccess: () -> Unit) {
    var isLoading by remember { mutableStateOf(true) }

    // Simulate loading for 2 seconds
    LaunchedEffect(key1 = Unit) {
        delay(2000)
        isLoading = false
    }


    if (isLoading) {
        LoadingDialog()
    } else {
        Dialog(onDismissRequest = {}) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(40.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .background(Color.Gray)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "Success!", textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = message, color = Color.White)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        modifier = Modifier.clickable(
                            enabled = true,
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null // Disables ripple effect
                        ) { navSuccess() }, text = "OK", color = Color.Yellow
                    )
                }
            }
        }
    }
}

@Composable
fun FailureDialog(message: String, navSuccess: () -> Unit) {
    var isLoading by remember { mutableStateOf(true) }


    LaunchedEffect(key1 = Unit) {
        delay(2000)
        isLoading = false
    }

    // Show loading dialog while isLoading is true
    if (isLoading) {
        LoadingDialog()
    } else {
        Dialog(onDismissRequest = {}) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(40.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .background(Color.Red)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "Failure!" , textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = message, color = Color.White)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        modifier = Modifier.clickable(
                            enabled = true,
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null // Disables ripple effect
                        ) { navSuccess() }, text = "OK", color = Color.Yellow
                    )
                }
            }
        }
    }
}

fun uriToFile(uri: Uri, context: Context): File? {
    val contentResolver = context.contentResolver
    val file = File(context.cacheDir, "temp_image.jpg")
    try {
        contentResolver.openInputStream(uri)?.use { inputStream ->
            FileOutputStream(file).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
        return file
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return null
}
