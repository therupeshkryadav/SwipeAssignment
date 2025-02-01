package com.app.getswipe.assignment.presentation.ui.screens.addScreen.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DetailRow(
    label: String,
    isError: Boolean,
    errorMessage: String,
    placeHolder: String = "",
    keyboardOptions: KeyboardOptions,
    updatedValue: String,
    onValueChange: (String) -> Unit
) {
    var isErrorTrack = isError
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Text(
            text = label,
            color = Color.DarkGray,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(4.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center
        ) {
            TextField(
                value = updatedValue,
                onValueChange = {
                    isErrorTrack = false
                    onValueChange(it)
                },
                enabled = true,
                placeholder = { Text(text = placeHolder, color = Color.LightGray) },
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(8.dp)
                    .border(
                        width = 1.dp,
                        color = if (isErrorTrack) Color.Red else Color.LightGray,
                        shape = RoundedCornerShape(20.dp)
                    ),
                isError = isError, // Hide error on click
                textStyle = TextStyle(fontSize = 16.sp),
                singleLine = true,
                keyboardOptions = keyboardOptions,
                shape = RoundedCornerShape(20.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent,
                    errorContainerColor = Color.White,
                    errorCursorColor = Color.DarkGray,
                    cursorColor = Color.DarkGray,
                    unfocusedContainerColor = Color.White,
                    disabledContainerColor = Color.White,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    unfocusedTextColor = Color.Black,
                    focusedTextColor = Color.Black,
                    disabledTextColor = Color.Black
                )
            )

            if (isErrorTrack) {
                Text(
                    modifier = Modifier.padding(start = 16.dp),
                    text = errorMessage,
                    color = Color.Red,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DetailRowPreview() {
    DetailRow(
        label = "Product Name",
        updatedValue = "new",
        placeHolder = "---",
        onValueChange = { },
        isError = true,
        errorMessage = "Required !!",
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number, // Sets numeric keyboard
            imeAction = ImeAction.Done // Sets "Done" button on keyboard
        )
    )
}
