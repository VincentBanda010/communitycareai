package com.communitycare.ai.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
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
    var search by remember { mutableStateOf("") }
    var status by remember { mutableStateOf(false) }
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
                    Box(Modifier.fillMaxWidth().background(Brush.verticalGradient(listOf(TealPrimary, TealDark)))) {
                    Box(Modifier.size(190.dp).align(Alignment.TopEnd).offset(x = 60.dp, y = (-40).dp).background(White.copy(alpha = .07f), CircleShape))
                    Box(Modifier.size(90.dp).align(Alignment.BottomStart).offset(x = (-35).dp).background(White.copy(alpha = .06f), CircleShape))
                    Column(Modifier.fillMaxWidth().safeDrawingPadding()
                        .padding(horizontal = 20.dp, vertical = 24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text("CommunityCare", color = White.copy(alpha = .8f), style = MaterialTheme.typography.labelLarge)
                                Text("Hello, ${profile.name.substringBefore(' ')}", color = White,
                                    style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
                            IconButton(onClick = { status = true }) { Icon(Icons.Default.NotificationsNone, "Reporting status", tint = White) }
                            IconButton(onClick = { tab = 4 }, modifier = Modifier.background(White.copy(alpha = .14f), CircleShape)) { Icon(Icons.Default.Person, "Open profile", tint = White) }
                        }
                        Text("Caring for Your\nCommunity with AI", color = White, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        Button(onClick = { tab = 2 }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = White, contentColor = TealDark), shape = RoundedCornerShape(14.dp)) {
                            Icon(Icons.Default.MedicalServices, null); Spacer(Modifier.width(8.dp)); Text("Start New Consultation")
                        }
                        OutlinedTextField(search, { search = it }, placeholder = { Text("Search saved patients…") },
                            leadingIcon = { Icon(Icons.Default.Search, null) }, modifier = Modifier.fillMaxWidth(), singleLine = true,
                            shape = RoundedCornerShape(14.dp), colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = White, unfocusedTextColor = White, cursorColor = White,
                                focusedBorderColor = White, unfocusedBorderColor = White.copy(alpha = .4f),
                                focusedPlaceholderColor = White.copy(alpha = .8f), unfocusedPlaceholderColor = White.copy(alpha = .8f),
                                focusedLeadingIconColor = White, unfocusedLeadingIconColor = White))
                    }
                    }
                    Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
                        Text("Quick Actions", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            HomeAction("New Patient", Icons.Default.PersonAdd, Modifier.weight(1f)) { adding = true }
                            HomeAction("Appointments", Icons.Default.CalendarMonth, Modifier.weight(1f)) { mode = "APPOINTMENT"; tab = 1 }
                        }
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            HomeAction("Treatments", Icons.Default.MedicalServices, Modifier.weight(1f)) { mode = "NOTE"; tab = 1 }
                            HomeAction("Emergency", Icons.Default.Emergency, Modifier.weight(1f), AccentRed) { tab = 2 }
                        }
                        Text("Your activity", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            HomeMetric("Registered today", patients.count { Instant.parse(it.createdAt).atZone(ZoneId.systemDefault()).toLocalDate() == today }, Modifier.weight(1f))
                            HomeMetric("Referrals reported", records.count { it.triage != Triage.ROUTINE && it.state == DeliveryState.FORWARDED }, Modifier.weight(1f))
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            HomeMetric("Clinical notes", entries.count { it.kind == "NOTE" }, Modifier.weight(1f))
                            HomeMetric("Urgent reports", records.count { it.triage == Triage.URGENT_REFERRAL }, Modifier.weight(1f))
                        }
                        Card(onClick = { tab = 2 }, colors = CardDefaults.cardColors(containerColor = TealLight)) {
                            Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Sync, null, tint = TealDark); Spacer(Modifier.width(12.dp))
                                Column(Modifier.weight(1f)) {
                                    Text(if (pending > 0) "$pending reports awaiting sync" else "Reports up to date", fontWeight = FontWeight.Bold, color = TealDark)
                                    Text("Open reporting & AI", style = MaterialTheme.typography.bodySmall, color = TealDark)
                                }
                                Icon(Icons.Default.ChevronRight, null, tint = TealDark)
                            }
                        }
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Text("Recent patients", modifier = Modifier.weight(1f), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            TextButton(onClick = { mode = "PATIENT"; tab = 1 }) { Text("View all") }
                        }
                        val shown = patients.filter { it.name.contains(search, ignoreCase = true) }.sortedByDescending { it.createdAt }.take(5)
                        if (shown.isEmpty()) Text(if (search.isBlank()) "Your patient register is empty." else "No patients match your search.", color = TextSecondary)
                        shown.forEach { patient ->
                            Card(onClick = { mode = "PATIENT"; tab = 1 }, colors = CardDefaults.cardColors(containerColor = White)) {
                                Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.AccountCircle, null, tint = TealPrimary, modifier = Modifier.size(36.dp))
                                    Column(Modifier.weight(1f).padding(horizontal = 12.dp)) {
                                        Text(patient.name, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                        Text("${patient.ageAtRegistration} years at registration", color = TextSecondary, style = MaterialTheme.typography.bodySmall)
                                        val note = entries.filter { it.patientId == patient.id && it.kind == "NOTE" }.maxByOrNull { it.at }
                                        Text(note?.text ?: "No clinical note recorded", maxLines = 1, overflow = TextOverflow.Ellipsis, color = TextSecondary, style = MaterialTheme.typography.bodySmall)
                                        Text("Unassessed", style = MaterialTheme.typography.labelSmall, color = TealDark)
                                    }
                                    Icon(Icons.Default.ChevronRight, null, tint = TextSecondary)
                                }
                            }
                        }
                        Text("Care and reporting reminders", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            item { ReminderCard("Delivery check", "$pending reports await confirmation.", Icons.Default.Sync) }
                            item { ReminderCard("Human-confirmed care", "Use approved guidance for every assessment.", Icons.Default.VerifiedUser) }
                            item { ReminderCard("Reporting-quality AI", "Review live reporting patterns in Assess.", Icons.Default.Analytics) }
                        }
                        OutlinedButton(onClick = onSync, enabled = !sync.busy, modifier = Modifier.fillMaxWidth()) {
                            Icon(Icons.Default.Sync, null); Spacer(Modifier.width(8.dp)); Text(if (sync.busy) "Syncing…" else "Sync now")
                        }
                    }
                }
            }
        }
    }
    if (adding) NewPatientDialog(profile, clinic) { adding = false }
    if (status) AlertDialog(onDismissRequest = { status = false }, title = { Text("Reporting status") },
        text = { Text(sync.message ?: "$pending reports await delivery confirmation.") },
        confirmButton = { TextButton(onClick = { status = false; tab = 2 }) { Text("Open reports") } },
        dismissButton = { TextButton(onClick = { status = false }) { Text("Close") } })
    if (details) AlertDialog(onDismissRequest = { details = false }, title = { Text("Data and services") },
        text = { Text("Patient records, appointments and notes are encrypted on this phone. Reports sync to the owned DHIS2 server. AI reviews reporting quality and does not make clinical decisions. Facility chat is shared with your assigned team. This pilot includes engineering test data. Password-reset email awaits Gmail setup; contact your administrator for recovery.") },
        confirmButton = { TextButton(onClick = { details = false }) { Text("Close") } })
    if (logout) AlertDialog(onDismissRequest = { logout = false }, title = { Text("Sign out?") },
        text = { Text("Your saved records stay encrypted on this phone.") },
        confirmButton = { TextButton(onClick = { logout = false; onLogout() }) { Text("Sign out") } },
        dismissButton = { TextButton(onClick = { logout = false }) { Text("Cancel") } })
}

@Composable private fun HomeAction(label: String, icon: ImageVector, modifier: Modifier, tint: Color = TealPrimary, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = modifier, colors = CardDefaults.cardColors(containerColor = White)) {
        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Icon(icon, null, tint = tint, modifier = Modifier.size(28.dp))
            Text(label, fontWeight = FontWeight.SemiBold)
        }
    }
}
@Composable private fun ReminderCard(title: String, text: String, icon: ImageVector) {
    Surface(Modifier.width(230.dp), color = White, shape = RoundedCornerShape(16.dp)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(icon, null, tint = TealPrimary)
            Text(title, fontWeight = FontWeight.Bold)
            Text(text, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
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
