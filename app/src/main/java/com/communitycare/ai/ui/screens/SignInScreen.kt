package com.communitycare.ai.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.communitycare.ai.ui.theme.*

@Composable
fun SignInScreen(onSignIn: () -> Unit, onSignUp: () -> Unit, busy: Boolean = false, error: String? = null) {
    Column(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(TealLight, TealSurface)))
        .safeDrawingPadding().padding(24.dp), verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(Icons.Default.LocalHospital, contentDescription = null, tint = TealDark, modifier = Modifier.size(64.dp))
        Spacer(Modifier.height(20.dp))
        Text("Welcome back", style = MaterialTheme.typography.headlineLarge, color = TealDark, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(16.dp))
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Sign in with your CommunityCare account", style = MaterialTheme.typography.titleMedium)
                Text("Your browser will open our secure sign-in page. Use the account provided by your administrator.")
                error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                Button(onClick = onSignIn, enabled = !busy, modifier = Modifier.fillMaxWidth()) {
                    if (busy) CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp)
                    else Text("Sign in securely")
                }
                Text("After your first sign-in, you can save reports offline. Delivery resumes when a connection is available.",
                    style = MaterialTheme.typography.bodySmall)
            }
        }
        TextButton(onClick = onSignUp, enabled = !busy) { Text("Need an account or password help?") }
    }
}
