package com.communitycare.ai.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.communitycare.ai.ui.theme.*

@Composable
fun SignInScreen(
    onSignIn: () -> Unit,
    onSignUp: () -> Unit, busy: Boolean = false, error: String? = null
) {
    var email           by remember { mutableStateOf("") }
    var password        by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var rememberMe      by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(listOf(Color(0xFFD4F1F4), Color(0xFFB0E8EC), TealLight))
            )
    ) {
        // Top arc
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(bottomStart = 999.dp, bottomEnd = 999.dp))
                .background(Brush.verticalGradient(listOf(TealPrimary, TealDark)))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(44.dp))

            // ── Header ────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(White.copy(alpha = 0.25f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.LocalHospital, null, tint = White, modifier = Modifier.size(38.dp))
            }

            Spacer(Modifier.height(10.dp))
            Text("Welcome Back", fontWeight = FontWeight.Black, fontSize = 24.sp, color = White)
            Text("Sign in to your account", fontSize = 13.sp, color = White.copy(alpha = 0.85f))

            Spacer(Modifier.height(32.dp))

            // ── Card ──────────────────────────────────────────────
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = White,
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    Icon(Icons.Filled.VerifiedUser, null, tint = TealPrimary, modifier = Modifier.size(44.dp))
                    Text("CommunityCare worker sign in", fontWeight = FontWeight.Bold, fontSize = 19.sp, color = TealDark)
                    Text("Continue to the secure sign-in page, then return to your patient dashboard.", color = TextSecondary)
                    error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                    Button(onClick = onSignIn, enabled = !busy, modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(26.dp)) {
                        if (busy) CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp)
                        else Text("Sign in securely", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                    Text("Your programme administrator assigns your facility. After signing in, local records are encrypted on this phone.", fontSize = 12.sp, color = TextSecondary)
                    TextButton(onClick = onSignUp, enabled = !busy) { Text("Account or password help") }

                }
            }

            Spacer(Modifier.height(24.dp))

            TextButton(onClick = onSignUp) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(color = TextSecondary)) { append("Need a nurse account?  ") }
                        withStyle(SpanStyle(color = TealPrimary, fontWeight = FontWeight.Bold)) { append("Request access") }
                    },
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun SocialButton(modifier: Modifier, label: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    OutlinedButton(
        onClick = {},
        modifier = modifier.height(46.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary)
    ) {
        Icon(icon, contentDescription = label, tint = TealPrimary, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(6.dp))
        Text(label, fontSize = 13.sp)
    }
}
