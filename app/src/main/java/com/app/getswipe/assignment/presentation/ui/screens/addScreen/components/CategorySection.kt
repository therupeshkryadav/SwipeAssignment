package com.app.getswipe.assignment.presentation.ui.screens.addScreen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CategorySection(
    updatedCategory: String,
    isError: Boolean,
    errorMessage: String,
    onCategoryChange: (String) -> Unit,
    onCategorySelection: () -> Unit
) {
    // Track dropdown state
    var isSelected by remember { mutableStateOf(false) }
    var isClick by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Category",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.DarkGray,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        TextField(
            value = updatedCategory,
            onValueChange = {
                isClick = false
                onCategoryChange(it)
                            },
            placeholder = {
                Text(text = "Select a category", color = Color.Gray)
            },
            readOnly = true,
            enabled = false,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(20.dp))
                .border(
                    width = 1.dp,
                    color = when {
                        isError && !isClick -> Color.Red
                        isClick -> Color.LightGray
                        else -> Color.LightGray
                    },
                    shape = RoundedCornerShape(20.dp)
                )
                .clickable(
                    enabled = true,
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null // Disables ripple effect
                ) {
                    isClick = true
                    isSelected = !isSelected // Toggle dropdown arrow state
                    onCategorySelection()
                },
            trailingIcon = {
                Icon(
                    imageVector = if (isSelected) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = "Dropdown icon"
                )
            },
            isError = isError,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                errorContainerColor = Color.White,
                errorIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                errorCursorColor = Color.Gray,
                cursorColor = Color.DarkGray,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                disabledContainerColor = Color.White,
                unfocusedTextColor = Color.Black,
                disabledTextColor = Color.Black
            ),
            textStyle = TextStyle(fontSize = 16.sp)
        )

        if(isError && !isClick){
            Text(
                modifier = Modifier.padding(start = 16.dp,top = 8.dp),
                text = errorMessage,color = Color.Red,
                fontSize = 12.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CategorySectionPreview() {
    var selectedCategory by remember { mutableStateOf("") }

    CategorySection(
        updatedCategory = selectedCategory,
        onCategoryChange = { selectedCategory = it },
        onCategorySelection = {  },
        isError = false,
        errorMessage = "Required!!"
    )
}
