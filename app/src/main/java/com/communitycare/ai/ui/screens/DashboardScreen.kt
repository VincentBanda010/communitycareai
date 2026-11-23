package com.communitycare.ai.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.communitycare.ai.AppContainer
import com.communitycare.ai.SyncStatus
import com.communitycare.ai.data.*
import com.communitycare.ai.ui.theme.*
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@Composable
fun DashboardScreen(profile: Profile, records: List<LocalEncounter>, sync: SyncStatus,
    error: String?, onRecord: (Triage) -> Boolean, onSync: () -> Unit,
    onSignIn: () -> Unit, onLogout: () -> Unit, quality: QualityUiState,
    onRefreshQuality: () -> Unit, clinic: ClinicStore, app: AppContainer) {
    var tab by remember { mutableStateOf(0) }
    var mode by remember { mutableStateOf("PATIENT") }
    var adding by remember { mutableStateOf(false) }
    var details by remember { mutableStateOf(false) }
    var logout by remember { mutableStateOf(false) }
    val local by clinic.snapshot.collectAsState()
    val patients = local.patients.filter { it.belongsTo(profile) }
    val entries = local.entries.filter { it.belongsTo(profile) }
    val pending = records.count { it.state in setOf(DeliveryState.PENDING, DeliveryState.QUEUED) }
    val today = LocalDate.now()
    val uri = androidx.compose.ui.platform.LocalUriHandler.current
    val tabs = listOf("Home", "Patients", "Assess", "Messages", "Profile")
    val icons = listOf(Icons.Default.Home, Icons.Default.People, Icons.Default.Assignment, Icons.Default.ChatBubbleOutline, Icons.Default.Person)
    Scaffold(containerColor = Color(0xFFF4F9F8), bottomBar = {
        NavigationBar(containerColor = White) {
            tabs.forEachIndexed { index, label -> NavigationBarItem(selected = tab == index,
                onClick = { tab = index; if (index == 1) mode = "PATIENT" },
                icon = { Icon(icons[index], label) }, label = { Text(label) }) }
        }
    }) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when (tab) {
                1 -> ClinicRecordsScreen(profile, clinic, mode, { adding = true }, { tab = 2 })
                2 -> ReportingScreen(profile, records, sync, error, onRecord, onSync, onSignIn, onLogout, quality, onRefreshQuality)
                3 -> MessagesScreen(profile, app)
                4 -> Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).safeDrawingPadding().padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("My profile", style = MaterialTheme.typography.headlineMedium, color = TealDark, fontWeight = FontWeight.Bold)
                    Card(colors = CardDefaults.cardColors(containerColor = White)) {
                        Column(Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(profile.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            Text(profile.username, color = TextSecondary)
                            Text("Facility · ${profile.orgUnit}", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                    OutlinedButton(onClick = { uri.openUri("https://care-login.95.111.247.118.sslip.io/realms/communitycare/account/") }, modifier = Modifier.fillMaxWidth()) { Text("Account settings") }
                    OutlinedButton(onClick = { details = true }, modifier = Modifier.fillMaxWidth()) { Text("Data and service details") }
                    TextButton(onClick = { logout = true }, modifier = Modifier.fillMaxWidth()) { Text("Sign out") }
                }
                else -> Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
                    Column(Modifier.fillMaxWidth().background(Brush.verticalGradient(listOf(TealPrimary, TealDark))).safeDrawingPadding()
                        .padding(horizontal = 20.dp, vertical = 24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text("CommunityCare", color = White.copy(alpha = .8f), style = MaterialTheme.typography.labelLarge)
                                Text("Hello, ${profile.name.substringBefore(' ')}", color = White,
                                    style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
                            IconButton(onClick = { tab = 4 }, modifier = Modifier.background(White.copy(alpha = .14f), CircleShape)) { Icon(Icons.Default.Person, "Open profile", tint = White) }
                        }
                        Text("Your community. Connected care.", color = White.copy(alpha = .85f))
                        Button(onClick = { adding = true }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = White, contentColor = TealDark), shape = RoundedCornerShape(14.dp)) {
                            Icon(Icons.Default.PersonAdd, null); Spacer(Modifier.width(8.dp)); Text("Register patient")
                        }
                    }
                    Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            HomeAction("Appointments", Icons.Default.CalendarMonth, Modifier.weight(1f)) { mode = "APPOINTMENT"; tab = 1 }
                            HomeAction("Care notes", Icons.Default.EditNote, Modifier.weight(1f)) { mode = "NOTE"; tab = 1 }
                        }
                        Text("Today", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            HomeMetric("Patients added", patients.count { Instant.parse(it.createdAt).atZone(ZoneId.systemDefault()).toLocalDate() == today }, Modifier.weight(1f))
                            HomeMetric("Reports saved", records.count { Instant.parse(it.occurredAt).atZone(ZoneId.systemDefault()).toLocalDate() == today }, Modifier.weight(1f))
                        }
                        Card(onClick = { tab = 2 }, colors = CardDefaults.cardColors(containerColor = TealLight)) {
                            Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Sync, null, tint = TealDark); Spacer(Modifier.width(12.dp))
                                Column(Modifier.weight(1f)) {
                                    Text(if (pending > 0) "$pending reports awaiting sync" else "No reports waiting", fontWeight = FontWeight.Bold, color = TealDark)
                                    Text("Open reporting & AI", style = MaterialTheme.typography.bodySmall, color = TealDark)
                                }
                                Icon(Icons.Default.ChevronRight, null, tint = TealDark)
                            }
                        }
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Text("Recent patients", modifier = Modifier.weight(1f), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            TextButton(onClick = { mode = "PATIENT"; tab = 1 }) { Text("View all") }
                        }
                        if (patients.isEmpty()) Text("Your patient register is empty.", color = TextSecondary)
                        patients.sortedByDescending { it.createdAt }.take(3).forEach { patient ->
                            Card(onClick = { mode = "PATIENT"; tab = 1 }, colors = CardDefaults.cardColors(containerColor = White)) {
                                Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.AccountCircle, null, tint = TealPrimary, modifier = Modifier.size(36.dp))
                                    Column(Modifier.weight(1f).padding(horizontal = 12.dp)) {
                                        Text(patient.name, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                        Text("${patient.ageAtRegistration} years at registration", color = TextSecondary, style = MaterialTheme.typography.bodySmall)
                                    }
                                    Icon(Icons.Default.ChevronRight, null, tint = TextSecondary)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    if (adding) NewPatientDialog(profile, clinic) { adding = false }
    if (details) AlertDialog(onDismissRequest = { details = false }, title = { Text("Data and services") },
        text = { Text("Patient records, appointments and notes are encrypted on this phone. Reports sync to the owned DHIS2 server. AI reviews reporting quality and does not make clinical decisions. Facility chat is shared with your assigned team. This pilot includes engineering test data. Password-reset email awaits Gmail setup; contact your administrator for recovery.") },
        confirmButton = { TextButton(onClick = { details = false }) { Text("Close") } })
    if (logout) AlertDialog(onDismissRequest = { logout = false }, title = { Text("Sign out?") },
        text = { Text("Your saved records stay encrypted on this phone.") },
        confirmButton = { TextButton(onClick = { logout = false; onLogout() }) { Text("Sign out") } },
        dismissButton = { TextButton(onClick = { logout = false }) { Text("Cancel") } })
}

@Composable private fun HomeAction(label: String, icon: ImageVector, modifier: Modifier, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = modifier, colors = CardDefaults.cardColors(containerColor = White)) {
        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Icon(icon, null, tint = TealPrimary, modifier = Modifier.size(28.dp))
            Text(label, fontWeight = FontWeight.SemiBold)
        }
    }
}
@Composable private fun HomeMetric(label: String, count: Int, modifier: Modifier) {
    Card(modifier, colors = CardDefaults.cardColors(containerColor = White)) {
        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(count.toString(), style = MaterialTheme.typography.headlineMedium, color = TealDark, fontWeight = FontWeight.Bold)
            Text(label, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        }
    }
}
