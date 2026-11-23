package com.communitycare.ai.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.communitycare.ai.data.*
import java.util.Locale

@Composable
fun ReportingQualityCard(state: QualityUiState, onRefresh: () -> Unit) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Live DHIS2 reporting review", style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold)
            Text("Organisation-wide reporting patterns. Refreshes while this dashboard is open.")
            if (state.loading) LinearProgressIndicator(Modifier.fillMaxWidth())
            state.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            val data = state.snapshot
            if (data != null) {
                Text("${data.reportCount} reports in the latest DHIS2 snapshot; ${data.validCount} scored-ready and ${data.invalidCount} invalid.")
                Text("Routine ${data.categoryCounts[Triage.ROUTINE.name]} · Referral ${data.categoryCounts[Triage.REFERRAL.name]} · Urgent ${data.categoryCounts[Triage.URGENT_REFERRAL.name]}")
                Text("${data.delayedOver24Hours} arrived more than 24 hours after assessment.")
                Text(if (data.modelStatus == "INSUFFICIENT_DATA")
                    "AI needs at least ${data.minimumSamples} valid reports. No model scores are shown."
                else "Experimental Isolation Forest fitted on ${data.trainingSamples} reports in this snapshot.")
                if (data.modelStatus == "EXPERIMENTAL_SMALL_SAMPLE") Text("Small sample: patterns are particularly unreliable.")
                data.candidates.forEach { item ->
                    HorizontalDivider()
                    Text("Report ${item.eventId} · ${if (item.reviewSuggested) "Review suggested" else "Relative pattern score"}")
                    Text("Score ${String.format(Locale.ROOT, "%.3f", item.anomalyScore)} · Arrival delay ${String.format(Locale.ROOT, "%.2f", item.delayHours)} hours")
                }
                if (data.truncated) Text("Limited to the latest ${data.snapshotLimit} reports; this is not a complete history.")
                Text("Fetched ${data.generatedAt}. Server cache up to ${data.cacheSeconds} seconds.",
                    style = MaterialTheme.typography.bodySmall)
                Text("Model ${data.modelVersion} · Data ${data.datasetFingerprint}", style = MaterialTheme.typography.bodySmall)
                Text(data.notice, style = MaterialTheme.typography.bodySmall)
            } else if (!state.loading && state.error == null) Text("Connect and refresh to read DHIS2 reporting data.")
            Button(onClick = onRefresh, enabled = !state.loading) { Text("Refresh live analysis") }
        }
    }
}
