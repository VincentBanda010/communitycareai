package com.communitycare.ai.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SignUpScreen(onSignIn: () -> Unit) {
    Column(Modifier.fillMaxSize().safeDrawingPadding().padding(24.dp),
        verticalArrangement = Arrangement.Center) {
        Text("Access your CommunityCare account", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(20.dp))
        Text("Ask your programme administrator to create your health-worker account, assign your facility, or reset your password.")
        Spacer(Modifier.height(16.dp))
        Text("Use the temporary password provided to you on the secure sign-in page. You will be asked to choose a new password.")
        Spacer(Modifier.height(24.dp))
        Button(onClick = onSignIn) { Text("Back to sign in") }
    }
}
