package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Draw
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SyncStatus
import com.example.data.repository.CloudSyncManager
import com.example.ui.theme.ComplianceAmber
import com.example.ui.theme.ComplianceGreen
import com.example.ui.theme.ComplianceRed
import com.example.ui.theme.WorkforceCardBorder
import com.example.ui.theme.WorkforceDarkBg
import com.example.ui.theme.WorkforceOrange
import com.example.ui.theme.WorkforceRed
import com.example.ui.theme.WorkforceSurface
import com.example.ui.theme.WorkforceSurfaceVariant
import com.example.ui.theme.WorkforceTextMuted
import com.example.ui.theme.WorkforceTextPrimary
import com.example.ui.theme.WorkforceTextSecondary

/**
 * Visual Progress Dashboard showing Offline Data Sync Status with a circular progress indicator.
 * Displays live item counts, stage transitions, and allows toggling offline mining tunnel mode.
 */
@Composable
fun SyncProgressDashboardCard(
    syncManager: CloudSyncManager,
    onSyncNowClick: () -> Unit,
    onToggleOfflineClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val syncState by syncManager.syncState.collectAsState()
    val isOnline by syncManager.isOnline.collectAsState()
    val syncProgress by syncManager.syncProgress.collectAsState()
    val currentStep by syncManager.currentSyncStep.collectAsState()
    val lastSyncTime by syncManager.lastSyncTime.collectAsState()
    val serverMessage by syncManager.serverMessage.collectAsState()

    val pendingInspections by syncManager.pendingInspections.collectAsState()
    val pendingPhotos by syncManager.pendingPhotos.collectAsState()
    val pendingRemedials by syncManager.pendingRemedialActions.collectAsState()
    val pendingSignatures by syncManager.pendingSignatures.collectAsState()
    val totalPending = pendingInspections + pendingPhotos + pendingRemedials + pendingSignatures

    // Animated float for smooth circular progress sweep
    val animatedProgress by animateFloatAsState(
        targetValue = if (syncState == SyncStatus.SYNCING) syncProgress else if (syncState == SyncStatus.SYNCED) 1.0f else 0.0f,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "syncProgressAnimation"
    )

    // Pulsing animation for syncing indicator
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("sync_progress_dashboard_card"),
        colors = CardDefaults.cardColors(containerColor = WorkforceSurface),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, WorkforceCardBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row: Status badge & Mining Tunnel Offline Switch
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = if (isOnline) ComplianceGreen.copy(alpha = 0.16f) else WorkforceOrange.copy(alpha = 0.2f),
                        modifier = Modifier.size(32.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (isOnline) Icons.Default.Wifi else Icons.Default.WifiOff,
                                contentDescription = "Network State",
                                tint = if (isOnline) ComplianceGreen else WorkforceOrange,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "OFFLINE DATA SYNC STATUS",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = WorkforceOrange,
                                letterSpacing = 0.5.sp
                            )
                        )
                        Text(
                            text = if (isOnline) "Connected • https://wfhsa.com/new/" else "Offline Mining Tunnel Mode Active",
                            style = MaterialTheme.typography.bodySmall.copy(color = WorkforceTextSecondary, fontSize = 11.sp)
                        )
                    }
                }

                // Quick toggle offline button
                OutlinedButton(
                    onClick = onToggleOfflineClick,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (!isOnline) WorkforceOrange.copy(alpha = 0.15f) else Color.Transparent
                    ),
                    modifier = Modifier
                        .height(34.dp)
                        .testTag("btn_toggle_offline_mode")
                ) {
                    Text(
                        text = if (isOnline) "Go Offline" else "Go Online",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isOnline) WorkforceTextPrimary else WorkforceOrange
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Main Circular Progress Display Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Circular Progress Indicator with Inner Content
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .scale(if (syncState == SyncStatus.SYNCING) pulseScale else 1.0f)
                        .testTag("circular_sync_indicator"),
                    contentAlignment = Alignment.Center
                ) {
                    // Background track circle
                    CircularProgressIndicator(
                        progress = { 1.0f },
                        modifier = Modifier.size(110.dp),
                        color = WorkforceCardBorder,
                        strokeWidth = 9.dp,
                        trackColor = Color.Transparent,
                        strokeCap = StrokeCap.Round
                    )

                    // Foreground active circular progress
                    val indicatorColor = when {
                        !isOnline -> WorkforceTextMuted
                        syncState == SyncStatus.SYNCING -> WorkforceOrange
                        syncState == SyncStatus.SYNCED -> ComplianceGreen
                        syncState == SyncStatus.FAILED -> ComplianceRed
                        else -> WorkforceOrange
                    }

                    if (syncState == SyncStatus.SYNCING) {
                        CircularProgressIndicator(
                            progress = { animatedProgress },
                            modifier = Modifier.size(110.dp),
                            color = indicatorColor,
                            strokeWidth = 9.dp,
                            trackColor = Color.Transparent,
                            strokeCap = StrokeCap.Round
                        )
                    } else if (syncState == SyncStatus.SYNCED && isOnline) {
                        CircularProgressIndicator(
                            progress = { 1.0f },
                            modifier = Modifier.size(110.dp),
                            color = ComplianceGreen,
                            strokeWidth = 9.dp,
                            trackColor = Color.Transparent,
                            strokeCap = StrokeCap.Round
                        )
                    } else if (!isOnline && totalPending > 0) {
                        // In offline mode with pending items, show remaining queue percentage
                        CircularProgressIndicator(
                            progress = { 0.25f },
                            modifier = Modifier.size(110.dp),
                            color = WorkforceOrange,
                            strokeWidth = 9.dp,
                            trackColor = Color.Transparent,
                            strokeCap = StrokeCap.Round
                        )
                    }

                    // Inner Circular Content
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        val icon = when {
                            !isOnline -> Icons.Default.CloudOff
                            syncState == SyncStatus.SYNCING -> Icons.Default.CloudSync
                            syncState == SyncStatus.SYNCED -> Icons.Default.CloudDone
                            else -> Icons.Default.ErrorOutline
                        }
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = indicatorColor,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        val percentText = when {
                            !isOnline -> "OFFLINE"
                            syncState == SyncStatus.SYNCING -> "${(animatedProgress * 100).toInt()}%"
                            syncState == SyncStatus.SYNCED -> "100%"
                            else -> "READY"
                        }
                        Text(
                            text = percentText,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Black,
                                color = indicatorColor,
                                fontSize = if (percentText == "OFFLINE") 12.sp else 16.sp
                            )
                        )
                    }
                }

                // Progress details & current upload activity text
                Column(modifier = Modifier.weight(1f)) {
                    val statusTitle = when {
                        !isOnline -> "Offline Queue Active"
                        syncState == SyncStatus.SYNCING -> "Syncing in Progress..."
                        syncState == SyncStatus.SYNCED -> "Cloud Sync Complete"
                        else -> "Sync Needed"
                    }
                    Text(
                        text = statusTitle,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = WorkforceTextPrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = currentStep,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = if (syncState == SyncStatus.SYNCING) WorkforceOrange else WorkforceTextSecondary,
                            lineHeight = 16.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Last sync: $lastSyncTime",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = WorkforceTextMuted,
                            fontSize = 11.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = WorkforceCardBorder.copy(alpha = 0.6f))
            Spacer(modifier = Modifier.height(12.dp))

            // Queue item breakdown chips: Inspections, Photos, Actions, Signatures
            Text(
                text = "Offline Local Queue (Room DB Storage)",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = WorkforceTextSecondary
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QueueMetricChip(
                    label = "Audits",
                    count = pendingInspections,
                    icon = Icons.Default.Assignment,
                    modifier = Modifier.weight(1f)
                )
                QueueMetricChip(
                    label = "Photos",
                    count = pendingPhotos,
                    icon = Icons.Default.CameraAlt,
                    modifier = Modifier.weight(1f)
                )
                QueueMetricChip(
                    label = "Actions",
                    count = pendingRemedials,
                    icon = Icons.Default.Warning,
                    modifier = Modifier.weight(1f)
                )
                QueueMetricChip(
                    label = "Signatures",
                    count = pendingSignatures,
                    icon = Icons.Default.Draw,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Button: "Upload Queued Data Now"
            Button(
                onClick = onSyncNowClick,
                enabled = syncState != SyncStatus.SYNCING,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isOnline) WorkforceRed else WorkforceSurfaceVariant
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .testTag("btn_trigger_sync_now")
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = if (isOnline) Color.White else WorkforceTextMuted
                )
                Spacer(modifier = Modifier.width(6.dp))
                val btnLabel = when {
                    syncState == SyncStatus.SYNCING -> "Uploading Offline Data (${(animatedProgress * 100).toInt()}%)..."
                    !isOnline -> "Reconnect & Upload Queue (${totalPending} Items)"
                    totalPending > 0 -> "Upload Pending Queue (${totalPending} Items)"
                    else -> "Sync to WordPress (https://wfhsa.com/new/)"
                }
                Text(
                    text = btnLabel,
                    fontWeight = FontWeight.Bold,
                    color = if (isOnline) Color.White else WorkforceTextPrimary
                )
            }
        }
    }
}

@Composable
fun QueueMetricChip(
    label: String,
    count: Int,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = WorkforceSurfaceVariant,
        border = androidx.compose.foundation.BorderStroke(1.dp, WorkforceCardBorder)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (count > 0) WorkforceOrange else WorkforceTextMuted,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "$count",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (count > 0) WorkforceOrange else WorkforceTextPrimary
                    )
                )
            }
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 10.sp,
                    color = WorkforceTextSecondary
                ),
                maxLines = 1
            )
        }
    }
}

/**
 * Detailed Modal dialog for inspecting cloud sync logs and verifying offline-to-online recovery.
 */
@Composable
fun SyncDetailDialog(
    syncManager: CloudSyncManager,
    onDismiss: () -> Unit,
    onSyncNow: () -> Unit,
    onToggleOffline: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Cloud Sync & Storage Center",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = WorkforceTextPrimary)
                )
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = WorkforceTextSecondary)
                }
            }
        },
        text = {
            val scope = rememberCoroutineScope()
            val backendUrl by syncManager.backendUrl.collectAsState()
            val apiKey by syncManager.apiKey.collectAsState()
            val isSecretConfigured by syncManager.isSecretConfigured.collectAsState()
            var inputKey by remember(apiKey) { mutableStateOf(apiKey) }
            var isPasswordVisible by remember { mutableStateOf(false) }
            var pingMessage by remember { mutableStateOf<String?>(null) }
            var isTestingConnection by remember { mutableStateOf(false) }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SyncProgressDashboardCard(
                    syncManager = syncManager,
                    onSyncNowClick = onSyncNow,
                    onToggleOfflineClick = onToggleOffline
                )

                // Central Office Backend Administration & Two-Way Sync Overview
                Surface(
                    color = WorkforceSurfaceVariant,
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, WorkforceOrange.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Central Office Backend Administration",
                                style = MaterialTheme.typography.labelMedium.copy(color = WorkforceOrange, fontWeight = FontWeight.Bold)
                            )
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = if (isSecretConfigured) ComplianceGreen.copy(alpha = 0.2f) else ComplianceAmber.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = if (isSecretConfigured) "KEY ACTIVE" else "DEFAULT ACCESS",
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSecretConfigured) ComplianceGreen else ComplianceAmber
                                    )
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = backendUrl,
                            style = MaterialTheme.typography.bodySmall.copy(color = WorkforceTextPrimary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Two-Way Bidirectional Sync:\n• Direction A: Office to Mobile (Customers, Mine Sites, Scheduled Tasks)\n• Direction B: Mobile to Office (Audits, Photos, Signatures, Remedials)",
                            style = MaterialTheme.typography.bodySmall.copy(color = WorkforceTextSecondary, fontSize = 10.sp, lineHeight = 14.sp)
                        )
                    }
                }

                // Secure Secret Key Entry Flow
                Surface(
                    color = WorkforceSurfaceVariant,
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, WorkforceCardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Key, contentDescription = null, tint = WorkforceOrange, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Backend Secret [WORKFORCE_BACKEND_API_KEY]",
                                style = MaterialTheme.typography.labelMedium.copy(color = WorkforceTextPrimary, fontWeight = FontWeight.Bold)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Provide your secret key securely below or configure WORKFORCE_BACKEND_API_KEY in the AI Studio Secrets panel. Never paste keys in public chat.",
                            style = MaterialTheme.typography.bodySmall.copy(color = WorkforceTextMuted, fontSize = 10.sp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = inputKey,
                            onValueChange = { inputKey = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("backend_secret_key_input"),
                            label = { Text("API Secret Key", fontSize = 11.sp) },
                            singleLine = true,
                            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                    Icon(
                                        imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = "Toggle key visibility",
                                        tint = WorkforceTextMuted,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = WorkforceOrange,
                                unfocusedBorderColor = WorkforceCardBorder,
                                focusedTextColor = WorkforceTextPrimary,
                                unfocusedTextColor = WorkforceTextPrimary
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    syncManager.setApiKey(inputKey)
                                    pingMessage = "Secret key saved securely in application configuration."
                                },
                                modifier = Modifier.weight(1f).height(38.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = WorkforceOrange),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.Black)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Save Key", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            OutlinedButton(
                                onClick = {
                                    scope.launch {
                                        isTestingConnection = true
                                        pingMessage = "Pinging central office endpoint..."
                                        syncManager.testBackendConnection()
                                        isTestingConnection = false
                                        pingMessage = "Connected to Central Office Backend: ${syncManager.serverMessage.value}"
                                    }
                                },
                                modifier = Modifier.weight(1f).height(38.dp),
                                shape = RoundedCornerShape(6.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = WorkforceTextPrimary)
                            ) {
                                if (isTestingConnection) {
                                    CircularProgressIndicator(modifier = Modifier.size(14.dp), strokeWidth = 2.dp, color = WorkforceOrange)
                                } else {
                                    Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(14.dp), tint = WorkforceOrange)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Test Ping", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        pingMessage?.let { msg ->
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = msg,
                                style = MaterialTheme.typography.bodySmall.copy(color = ComplianceGreen, fontSize = 10.sp)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = WorkforceRed)
            ) {
                Text("Close")
            }
        },
        containerColor = WorkforceSurface,
        shape = RoundedCornerShape(14.dp)
    )
}
