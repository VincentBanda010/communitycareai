package com.communitycare.ai.ui.screens

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.communitycare.ai.ui.theme.TealPrimary
import com.communitycare.ai.ui.theme.White

/** Shared styled OutlinedTextField used in auth screens */
@Composable
fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = { Icon(leadingIcon, contentDescription = null, tint = TealPrimary) },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = authTextFieldColors()
    )
}

/** Shared OutlinedTextField colour scheme */
@Composable
fun authTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor   = TealPrimary,
    unfocusedBorderColor = Color(0xFFCCE8EA),
    focusedLabelColor    = TealPrimary,
    cursorColor          = TealPrimary,
    focusedContainerColor   = White,
    unfocusedContainerColor = Color(0xFFF8FEFE)
)
