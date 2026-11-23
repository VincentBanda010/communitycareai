package com.communitycare.ai.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.communitycare.ai.ui.theme.*

@Composable
fun GetStartedScreen(onGetStarted: () -> Unit, onSignIn: () -> Unit) {
    Column(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(TealDark, TealPrimary)))
        .safeDrawingPadding().verticalScroll(rememberScrollState()).padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(20.dp)) {
        Spacer(Modifier.height(72.dp))
        Surface(color = White.copy(alpha = .15f), shape = RoundedCornerShape(28.dp)) {
            Icon(Icons.Default.Favorite, "CommunityCare", Modifier.padding(28.dp).size(64.dp), tint = White)
        }
        Text("CommunityCare AI", color = White, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        Text("Connected care for your community.", color = White, style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center)
        Text("Patient records, team messages and DHIS2 reporting in one place.", color = White.copy(alpha = .8f), textAlign = TextAlign.Center)
        Spacer(Modifier.height(40.dp))
        Button(onClick = onGetStarted, Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = White, contentColor = TealDark)) { Text("Create account") }
        OutlinedButton(onClick = onSignIn, Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = White)) { Text("Sign in") }
        Text("For community health teams", color = White.copy(alpha = .7f), style = MaterialTheme.typography.bodySmall)
    }
}
