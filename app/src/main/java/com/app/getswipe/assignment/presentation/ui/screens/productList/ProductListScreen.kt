package com.app.getswipe.assignment.presentation.ui.screens.productList

import android.Manifest
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.getswipe.assignment.R
import com.app.getswipe.assignment.presentation.ui.screens.addScreen.LoadingDialog
import com.app.getswipe.assignment.presentation.ui.screens.productList.component.ProductItem
import com.app.getswipe.assignment.presentation.ui.screens.utils.customTopAppBar.CustomTopAppBar
import com.app.getswipe.assignment.presentation.ui.screens.utils.searchBar.SearchBar
import com.app.getswipe.assignment.presentation.viewModel.ProductViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ProductListScreen(
    onAddClick: () -> Unit
) {
    val productViewModel: ProductViewModel = koinViewModel() // Injecting ViewModel using Koin
    val products by productViewModel.products.observeAsState(emptyList())
    val searchQuery by productViewModel.searchQuery.observeAsState("")

    //asking for notification permission
    val postNotificationPermission =
        rememberPermissionState(permission = Manifest.permission.POST_NOTIFICATIONS)

    LaunchedEffect(key1 = true) {
        if (!postNotificationPermission.status.isGranted) {
            postNotificationPermission.launchPermissionRequest()
        }
    }

    LaunchedEffect(true) {
        productViewModel.syncOfflineProducts()
        productViewModel.getAllProducts()
    }



    // Product List UI
    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .navigationBarsPadding()
    ) {
        CustomTopAppBar(
            title = {
                Text(
                    text = "Product List",
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    maxLines = 2,
                    color = Color.Black,
                    overflow = TextOverflow.Ellipsis,  // Handling overflow text
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                )
            },
            navigationIcon = {},
            actions = {}
        )

        SearchBar(
            searchQuery = searchQuery,
            onSearchQueryChange = { productViewModel.searchProducts(it) }
        )

        var isLoading by remember { mutableStateOf(true) } // Track loading state

        // Simulate loading for 2 seconds
        LaunchedEffect(key1 = Unit) {
            delay(2000) // Simulate 2 seconds delay for loading
            isLoading = false // After 2 seconds, stop loading
        }

        // Show loading dialog while isLoading is true
        if (isLoading) {
            LoadingDialog()
        }else{
            Box(modifier = Modifier.padding(top = 16.dp, start = 8.dp, end= 8.dp)) {

                if(products.isEmpty()){
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = "No Products",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(64.dp)
                        )
                        Text(
                            text = "No Products Found. Add some to display!",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 16.dp)
                        )
                    }
                }else{
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(products) { product ->
                            ProductItem(product = product)
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                }
                }

                // Floating Action Button
                FloatingActionButton(
                    onClick = { onAddClick() },
                    containerColor = Color.Black,
                    modifier = Modifier
                        .align(Alignment.BottomEnd) // Align to the bottom-end of the screen
                        .padding(16.dp) // Add padding for spacing
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_add),
                        contentDescription = "Add Product",
                        modifier = Modifier.size(24.dp),
                        colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(Color.White)
                    )
                }
            }
        }
    }

    // Fetch products initially when the screen is loaded
    LaunchedEffect(Unit) {
        productViewModel.getAllProducts()
    }
}









