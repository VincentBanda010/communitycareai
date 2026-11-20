package com.communitycare.ai.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.communitycare.ai.ui.theme.*

// ── Data models ───────────────────────────────────────────────────────────

data class QuickAction(val label: String, val icon: ImageVector, val color: Color)
data class RecentPatient(
    val name: String,
    val age: String,
    val diagnosis: String,
    val severity: String,   // "critical" | "moderate" | "stable"
    val time: String,
    val initials: String
)
data class HealthTip(val title: String, val body: String, val icon: ImageVector)

// ── Screen ────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen() {
    var selectedTab by remember { mutableStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }

    val quickActions = listOf(
        QuickAction("New Patient",    Icons.Filled.PersonAdd,     TealPrimary),
        QuickAction("Appointments",   Icons.Filled.CalendarMonth, Color(0xFF5390D9)),
        QuickAction("Treatments",     Icons.Filled.MedicalServices, AccentGreen),
        QuickAction("Emergency",      Icons.Filled.Emergency,     AccentRed)
    )

    val recentPatients = listOf(
        RecentPatient("Chisomo Banda",   "7 yrs",  "Malaria (RDT+)",     "critical",  "08:30 AM", "CB"),
        RecentPatient("Thandiwe Phiri",  "32 yrs", "ANC Visit — 28 wks", "stable",    "09:15 AM", "TP"),
        RecentPatient("Limbani Mwale",   "4 yrs",  "Severe Malnutrition","critical",  "10:00 AM", "LM"),
        RecentPatient("Grace Nkhoma",    "55 yrs", "Hypertension F/U",   "moderate",  "10:45 AM", "GN"),
        RecentPatient("Moses Chirwa",    "2 yrs",  "Diarrhoea + Fever",  "moderate",  "11:20 AM", "MC")
    )

    val healthTips = listOf(
        HealthTip("IMCI Protocol", "Follow integrated management steps for under-5 cases.", Icons.Filled.ChildCare),
        HealthTip("Danger Signs",  "Convulsions, unable to drink, lethargy → refer immediately.", Icons.Filled.Warning),
        HealthTip("Malaria RDT",   "Positive RDT in febrile child: treat with ACT within 1 hour.", Icons.Filled.BugReport)
    )

    Scaffold(
        containerColor = Color(0xFFF0FAFA),
        bottomBar = { BottomNavBar(selectedTab) { selectedTab = it } }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {

            // ── Top hero banner ───────────────────────────────────
            HeroBanner(searchQuery) { searchQuery = it }

            Spacer(Modifier.height(16.dp))

            // ── Quick actions ─────────────────────────────────────
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                SectionHeader("Quick Actions", showAll = false)
                Spacer(Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    quickActions.forEach { action ->
                        QuickActionCard(action)
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // ── Stats row ─────────────────────────────────────────
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(statCards) { stat ->
                    StatCard(stat)
                }
            }

            Spacer(Modifier.height(20.dp))

            // ── Upcoming / Recent patients ─────────────────────────
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                SectionHeader("Recent Patients", showAll = true)
                Spacer(Modifier.height(10.dp))
                recentPatients.forEach { patient ->
                    PatientCard(patient)
                    Spacer(Modifier.height(10.dp))
                }
            }

            // ── Health tips carousel ───────────────────────────────
            Spacer(Modifier.height(4.dp))
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                SectionHeader("Clinical Reminders", showAll = true)
                Spacer(Modifier.height(10.dp))
            }
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(healthTips) { tip ->
                    HealthTipCard(tip)
                }
            }

            Spacer(Modifier.height(24.dp))

            // ── Sync status banner ─────────────────────────────────
            SyncStatusBanner()

            Spacer(Modifier.height(16.dp))
        }
    }
}

// ── Hero Banner ───────────────────────────────────────────────────────────

@Composable
private fun HeroBanner(searchQuery: String, onSearchChange: (String) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(listOf(TealPrimary, TealDark))
            )
    ) {
        // Decorative circles
        Box(
            modifier = Modifier
                .size(130.dp)
                .align(Alignment.TopEnd)
                .offset(x = 30.dp, y = (-20).dp)
                .clip(CircleShape)
                .background(White.copy(alpha = 0.08f))
        )
        Box(
            modifier = Modifier
                .size(80.dp)
                .align(Alignment.BottomStart)
                .offset(x = (-10).dp, y = 30.dp)
                .clip(CircleShape)
                .background(White.copy(alpha = 0.08f))
        )

        Column(modifier = Modifier.padding(start = 20.dp, end = 16.dp, top = 52.dp, bottom = 24.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Good Morning 👋", fontSize = 13.sp, color = White.copy(alpha = 0.80f))
                    Text("HSA Thandi M.", fontWeight = FontWeight.Black, fontSize = 20.sp, color = White)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(onClick = {}) {
                        Icon(Icons.Filled.Notifications, null, tint = White, modifier = Modifier.size(24.dp))
                    }
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(TealLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("TM", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TealDark)
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Hero sub-headline
            Text(
                "Caring for Your\nCommunity with AI",
                fontWeight = FontWeight.Black,
                fontSize = 22.sp,
                color = White,
                lineHeight = 28.sp
            )

            Spacer(Modifier.height(6.dp))

            Text(
                "AI-powered triage · offline-ready · WHO protocols",
                fontSize = 12.sp,
                color = White.copy(alpha = 0.75f)
            )

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {},
                shape = RoundedCornerShape(22.dp),
                colors = ButtonDefaults.buttonColors(containerColor = White),
                elevation = ButtonDefaults.buttonElevation(4.dp),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp)
            ) {
                Icon(Icons.Filled.PersonAdd, null, tint = TealPrimary, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Start New Consultation", fontWeight = FontWeight.Bold, color = TealPrimary, fontSize = 14.sp)
            }
        }
    }

    // Search bar below the banner
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .offset(y = (-20).dp)
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            placeholder = { Text("Search patients, symptoms…", fontSize = 13.sp, color = Color(0xFF9DB5BC)) },
            leadingIcon = { Icon(Icons.Filled.Search, null, tint = TealPrimary) },
            trailingIcon = { Icon(Icons.Filled.Tune, null, tint = TealPrimary) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor   = TealPrimary,
                unfocusedBorderColor = Color.Transparent,
                focusedContainerColor   = White,
                unfocusedContainerColor = White
            )
        )
    }
}

// ── Quick Action Card ─────────────────────────────────────────────────────

@Composable
private fun QuickActionCard(action: QuickAction) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(76.dp)
            .clickable {}
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(action.color.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(action.icon, contentDescription = action.label, tint = action.color, modifier = Modifier.size(28.dp))
        }
        Spacer(Modifier.height(6.dp))
        Text(action.label, fontSize = 11.sp, color = TextPrimary, fontWeight = FontWeight.Medium,
            maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

// ── Stats ─────────────────────────────────────────────────────────────────

data class StatItem(val value: String, val label: String, val icon: ImageVector, val color: Color)

val statCards = listOf(
    StatItem("12",  "Today's Patients", Icons.Filled.Group,       TealPrimary),
    StatItem("3",   "Referrals Sent",   Icons.Filled.Send,        AccentAmber),
    StatItem("8",   "Treated",          Icons.Filled.CheckCircle, AccentGreen),
    StatItem("1",   "Critical",         Icons.Filled.Emergency,   AccentRed)
)

@Composable
private fun StatCard(stat: StatItem) {
    Surface(
        modifier = Modifier.width(130.dp),
        shape = RoundedCornerShape(18.dp),
        color = CardBg,
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(stat.color.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(stat.icon, null, tint = stat.color, modifier = Modifier.size(22.dp))
            }
            Spacer(Modifier.width(10.dp))
            Column {
                Text(stat.value, fontWeight = FontWeight.Black, fontSize = 20.sp, color = TextPrimary)
                Text(stat.label, fontSize = 11.sp, color = TextSecondary, lineHeight = 14.sp)
            }
        }
    }
}

// ── Patient Card ──────────────────────────────────────────────────────────

@Composable
private fun PatientCard(patient: RecentPatient) {
    val severityColor = when (patient.severity) {
        "critical" -> AccentRed
        "moderate" -> AccentAmber
        else       -> AccentGreen
    }
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = CardBg,
        shadowElevation = 3.dp
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(TealLight.copy(alpha = 0.4f)),
                contentAlignment = Alignment.Center
            ) {
                Text(patient.initials, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TealDark)
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(patient.name, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = TextPrimary)
                Text("${patient.age}  ·  ${patient.diagnosis}",
                    fontSize = 12.sp, color = TextSecondary,
                    maxLines = 1, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.AccessTime, null, tint = TextSecondary, modifier = Modifier.size(12.dp))
                    Spacer(Modifier.width(3.dp))
                    Text(patient.time, fontSize = 11.sp, color = TextSecondary)
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(severityColor.copy(alpha = 0.12f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(patient.severity.replaceFirstChar { it.uppercase() },
                        fontSize = 11.sp, color = severityColor, fontWeight = FontWeight.SemiBold)
                }
                Spacer(Modifier.height(6.dp))
                Icon(Icons.Filled.ChevronRight, null, tint = TextSecondary, modifier = Modifier.size(20.dp))
            }
        }
    }
}

// ── Health Tip Card ───────────────────────────────────────────────────────

@Composable
private fun HealthTipCard(tip: HealthTip) {
    Surface(
        modifier = Modifier.width(220.dp),
        shape = RoundedCornerShape(18.dp),
        color = CardBg,
        shadowElevation = 3.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(TealPrimary.copy(alpha = 0.10f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(tip.icon, null, tint = TealPrimary, modifier = Modifier.size(22.dp))
            }
            Spacer(Modifier.height(10.dp))
            Text(tip.title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
            Spacer(Modifier.height(4.dp))
            Text(tip.body, fontSize = 12.sp, color = TextSecondary, lineHeight = 16.sp)
        }
    }
}

// ── Sync Status Banner ────────────────────────────────────────────────────

@Composable
private fun SyncStatusBanner() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        color = TealPrimary.copy(alpha = 0.08f)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(TealPrimary.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.WifiOff, null, tint = TealPrimary, modifier = Modifier.size(18.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("Offline Mode Active", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = TealDark)
                Text("12 records pending sync  •  Last sync: 2 hrs ago",
                    fontSize = 11.sp, color = TextSecondary)
            }
            TextButton(onClick = {}) {
                Text("Sync", color = TealPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }
    }
}

// ── Section header ────────────────────────────────────────────────────────

@Composable
private fun SectionHeader(title: String, showAll: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)
        if (showAll) {
            TextButton(onClick = {}) {
                Text("View All  ›", fontSize = 13.sp, color = TealPrimary, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

// ── Bottom Navigation Bar ─────────────────────────────────────────────────

@Composable
private fun BottomNavBar(selected: Int, onSelect: (Int) -> Unit) {
    val items = listOf(
        Triple("Home",       Icons.Filled.Home,            Icons.Outlined.Home),
        Triple("Patients",   Icons.Filled.Group,           Icons.Outlined.Group),
        Triple("Diagnose",   Icons.Filled.MonitorHeart,    Icons.Outlined.MonitorHeart),
        Triple("Messages",   Icons.Filled.Message,         Icons.Outlined.Message),
        Triple("Profile",    Icons.Filled.Person,          Icons.Outlined.Person)
    )

    NavigationBar(
        containerColor = White,
        tonalElevation = 8.dp
    ) {
        items.forEachIndexed { idx, (label, filledIcon, outlinedIcon) ->
            NavigationBarItem(
                selected = selected == idx,
                onClick  = { onSelect(idx) },
                icon = {
                    if (idx == 2) {
                        // Prominent centre FAB-style button
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(CircleShape)
                                .background(
                                    if (selected == idx) TealPrimary
                                    else TealPrimary.copy(alpha = 0.12f)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                filledIcon, null,
                                tint = if (selected == idx) White else TealPrimary,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    } else {
                        Icon(
                            if (selected == idx) filledIcon else outlinedIcon,
                            contentDescription = label,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                label = { Text(label, fontSize = 10.sp) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor   = TealPrimary,
                    selectedTextColor   = TealPrimary,
                    unselectedIconColor = TextSecondary,
                    unselectedTextColor = TextSecondary,
                    indicatorColor      = TealPrimary.copy(alpha = 0.08f)
                )
            )
        }
    }
}
