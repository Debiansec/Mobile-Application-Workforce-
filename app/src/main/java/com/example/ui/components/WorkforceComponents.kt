package com.example.ui.components

import android.graphics.Bitmap
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.AssessmentStatus
import com.example.data.model.ComplianceResponse
import com.example.data.model.SyncStatus
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkforceTopBar(
    title: String,
    subtitle: String? = null,
    canNavigateBack: Boolean = false,
    onNavigateBack: () -> Unit = {},
    syncStatus: SyncStatus = SyncStatus.SYNCED,
    onSyncClick: () -> Unit = {},
    actions: @Composable () -> Unit = {}
) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = WorkforceDarkBg,
            titleContentColor = WorkforceTextPrimary
        ),
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.testTag("nav_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = WorkforceTextPrimary
                    )
                }
            } else {
                Image(
                    painter = painterResource(id = R.drawable.ic_workforce_logo),
                    contentDescription = "WorkForce Health SA Logo",
                    modifier = Modifier
                        .padding(start = 12.dp, end = 4.dp)
                        .size(36.dp)
                        .clip(RoundedCornerShape(6.dp))
                )
            }
        },
        title = {
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    ),
                    maxLines = 1
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall.copy(color = WorkforceTextSecondary),
                        maxLines = 1
                    )
                }
            }
        },
        actions = {
            // Cloud Sync Indicator
            SyncStatusIndicator(syncStatus = syncStatus, onClick = onSyncClick)
            actions()
        }
    )
}

@Composable
fun SyncStatusIndicator(
    syncStatus: SyncStatus,
    onClick: () -> Unit
) {
    val (icon, color, label) = when (syncStatus) {
        SyncStatus.SYNCED -> Triple(Icons.Default.CloudDone, ComplianceGreen, "Synced")
        SyncStatus.PENDING_SYNC -> Triple(Icons.Default.CloudSync, WorkforceOrange, "Pending")
        SyncStatus.SYNCING -> Triple(Icons.Default.Refresh, WorkforceOrange, "Syncing...")
        SyncStatus.OFFLINE -> Triple(Icons.Default.CloudOff, WorkforceTextMuted, "Offline")
        SyncStatus.FAILED -> Triple(Icons.Default.ErrorOutline, ComplianceRed, "Failed")
    }

    Surface(
        modifier = Modifier
            .padding(end = 8.dp)
            .clickable { onClick() }
            .testTag("sync_status_badge"),
        shape = RoundedCornerShape(16.dp),
        color = color.copy(alpha = 0.15f),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = color,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = color,
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
    }
}

@Composable
fun StatusBadge(status: AssessmentStatus) {
    val (color, text) = when (status) {
        AssessmentStatus.DRAFT -> Pair(WorkforceTextMuted, "Draft")
        AssessmentStatus.IN_PROGRESS -> Pair(WorkforceOrange, "In Progress")
        AssessmentStatus.SUBMITTED_FOR_REVIEW -> Pair(Color(0xFF38BDF8), "Awaiting Review")
        AssessmentStatus.RETURNED_FOR_CORRECTION -> Pair(ComplianceRed, "Returned")
        AssessmentStatus.APPROVED -> Pair(ComplianceGreen, "Approved")
        AssessmentStatus.ARCHIVED -> Pair(WorkforceTextSecondary, "Archived")
    }

    Surface(
        shape = RoundedCornerShape(6.dp),
        color = color.copy(alpha = 0.18f),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.5f))
    ) {
        Text(
            text = text.uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(
                color = color,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp
            ),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}

@Composable
fun GloveFriendlyComplianceSelector(
    selectedResponse: ComplianceResponse,
    allowsNA: Boolean = false,
    onSelect: (ComplianceResponse) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // YES Button (Glove-friendly)
        val isYes = selectedResponse == ComplianceResponse.YES
        val yesBg by animateColorAsState(if (isYes) ComplianceGreen else WorkforceSurfaceVariant)
        val yesContentColor by animateColorAsState(if (isYes) Color.White else WorkforceTextSecondary)

        Button(
            onClick = { onSelect(ComplianceResponse.YES) },
            modifier = Modifier
                .weight(1f)
                .height(52.dp)
                .testTag("compliance_yes_button"),
            colors = ButtonDefaults.buttonColors(containerColor = yesBg, contentColor = yesContentColor),
            shape = RoundedCornerShape(10.dp),
            border = if (isYes) androidx.compose.foundation.BorderStroke(2.dp, Color.White) else androidx.compose.foundation.BorderStroke(1.dp, WorkforceCardBorder)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Yes",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "YES",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        }

        // NO Button (Glove-friendly)
        val isNo = selectedResponse == ComplianceResponse.NO
        val noBg by animateColorAsState(if (isNo) ComplianceRed else WorkforceSurfaceVariant)
        val noContentColor by animateColorAsState(if (isNo) Color.White else WorkforceTextSecondary)

        Button(
            onClick = { onSelect(ComplianceResponse.NO) },
            modifier = Modifier
                .weight(1f)
                .height(52.dp)
                .testTag("compliance_no_button"),
            colors = ButtonDefaults.buttonColors(containerColor = noBg, contentColor = noContentColor),
            shape = RoundedCornerShape(10.dp),
            border = if (isNo) androidx.compose.foundation.BorderStroke(2.dp, Color.White) else androidx.compose.foundation.BorderStroke(1.dp, WorkforceCardBorder)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = "No",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "NO",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        }

        // Optional N/A Button
        if (allowsNA) {
            val isNA = selectedResponse == ComplianceResponse.NOT_APPLICABLE
            val naBg by animateColorAsState(if (isNA) Color(0xFF475569) else WorkforceSurfaceVariant)

            Button(
                onClick = { onSelect(ComplianceResponse.NOT_APPLICABLE) },
                modifier = Modifier
                    .weight(0.7f)
                    .height(52.dp)
                    .testTag("compliance_na_button"),
                colors = ButtonDefaults.buttonColors(containerColor = naBg, contentColor = Color.White),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(text = "N/A", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
            }
        }
    }
}

@Composable
fun DigitalSignatureCanvas(
    modifier: Modifier = Modifier,
    onSignatureCaptured: (String) -> Unit
) {
    val points = remember { mutableStateListOf<Offset>() }

    Column(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF0B0D13))
                .border(2.dp, WorkforceCardBorder, RoundedCornerShape(12.dp))
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            points.add(offset)
                        },
                        onDrag = { change, _ ->
                            points.add(change.position)
                        }
                    )
                }
                .testTag("signature_canvas_area")
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                if (points.size > 1) {
                    val path = Path()
                    path.moveTo(points.first().x, points.first().y)
                    for (i in 1 until points.size) {
                        path.lineTo(points[i].x, points[i].y)
                    }
                    drawPath(
                        path = path,
                        color = Color.White,
                        style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                    )
                }
            }

            if (points.isEmpty()) {
                Text(
                    text = "Sign with finger or stylus here",
                    style = MaterialTheme.typography.bodyMedium.copy(color = WorkforceTextMuted),
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            OutlinedButton(
                onClick = { points.clear() },
                modifier = Modifier.testTag("clear_signature_button")
            ) {
                Text("Clear Signature", color = WorkforceTextSecondary)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Button(
                onClick = {
                    if (points.isNotEmpty()) {
                        // Encode simple path token
                        val summary = "SIG_STROKES_${points.size}_${System.currentTimeMillis()}"
                        onSignatureCaptured(summary)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = WorkforceRed),
                modifier = Modifier.testTag("save_signature_button")
            ) {
                Text("Confirm Signature", fontWeight = FontWeight.Bold)
            }
        }
    }
}
