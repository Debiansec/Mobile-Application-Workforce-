package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Draw
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PriorityHigh
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ActionPriority
import com.example.data.model.ActionStatus
import com.example.data.model.RemedialAction
import com.example.ui.components.WorkforceTopBar
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
import com.example.ui.viewmodel.WorkforceViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun RemedialActionsScreen(
    viewModel: WorkforceViewModel,
    assessmentId: String? = null,
    onBack: () -> Unit
) {
    val syncStatus by viewModel.syncManager.syncState.collectAsState()
    val allActions by viewModel.outstandingRemedialActions.collectAsState()

    var statusFilter by remember { mutableStateOf<ActionStatus?>(null) }

    val baseActions = if (assessmentId != null) {
        allActions.filter { it.assessmentId == assessmentId }
    } else {
        allActions
    }

    val filteredActions = baseActions.filter {
        statusFilter == null || it.status == statusFilter
    }

    var selectedActionForClosure by remember { mutableStateOf<RemedialAction?>(null) }
    var closureEvidenceText by remember { mutableStateOf("") }
    var followUpObservationText by remember { mutableStateOf("") }
    var closureSignatoryName by remember { mutableStateOf("G. van der Merwe (Mine Manager)") }

    Scaffold(
        topBar = {
            WorkforceTopBar(
                title = "Remedial Actions Register",
                subtitle = "${baseActions.count { it.status == ActionStatus.OPEN || it.status == ActionStatus.IN_PROGRESS }} Outstanding • Section 10 Non-Conformances",
                canNavigateBack = true,
                onNavigateBack = onBack,
                syncStatus = syncStatus
            )
        },
        containerColor = WorkforceDarkBg
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Statutory Heading
            item {
                Surface(
                    color = WorkforceSurface,
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, WorkforceCardBorder)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "CENTRAL REMEDIAL ACTIONS REGISTER (SECTION 10)",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = WorkforceOrange,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "All recorded non-conformances require description, photographic evidence, corrective action, responsible person, due date, follow-up observation, and closure verification with signature.",
                            style = MaterialTheme.typography.bodySmall.copy(color = WorkforceTextSecondary, lineHeight = 17.sp)
                        )
                    }
                }
            }

            // Filter Chips
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        null to "All (${baseActions.size})",
                        ActionStatus.OPEN to "Open (${baseActions.count { it.status == ActionStatus.OPEN }})",
                        ActionStatus.IN_PROGRESS to "In Progress (${baseActions.count { it.status == ActionStatus.IN_PROGRESS }})",
                        ActionStatus.RESOLVED to "Resolved (${baseActions.count { it.status == ActionStatus.RESOLVED }})"
                    ).forEach { (status, label) ->
                        val isSelected = statusFilter == status
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) WorkforceOrange else WorkforceSurface,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) WorkforceOrange else WorkforceCardBorder
                            ),
                            modifier = Modifier.clickable { statusFilter = status }
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = if (isSelected) Color.White else WorkforceTextSecondary,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 12.sp
                                ),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            if (filteredActions.isEmpty()) {
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = WorkforceSurface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, WorkforceCardBorder)
                    ) {
                        Column(
                            modifier = Modifier.padding(28.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = ComplianceGreen,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Zero Non-Conformances in this View",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = WorkforceTextPrimary)
                            )
                            Text(
                                text = "All ablution facilities items meet statutory hygiene compliance criteria.",
                                style = MaterialTheme.typography.bodySmall.copy(color = WorkforceTextSecondary)
                            )
                        }
                    }
                }
            } else {
                items(filteredActions) { action ->
                    RemedialActionCard(
                        action = action,
                        onCloseActionClick = {
                            selectedActionForClosure = action
                            closureEvidenceText = "Defect rectified on site. Replacement parts fitted, area sanitized."
                            followUpObservationText = "Observed functional and fully compliant during follow-up inspection walkthrough."
                            closureSignatoryName = "G. van der Merwe (Mine Manager)"
                        }
                    )
                }
            }
        }
    }

    // Modal Dialog: Close Action & Log Verification Signature
    if (selectedActionForClosure != null) {
        val a = selectedActionForClosure!!
        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        AlertDialog(
            onDismissRequest = { selectedActionForClosure = null },
            title = {
                Text(
                    text = "Close Remedial Action (${a.actionReference})",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = WorkforceTextPrimary)
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Finding: ${a.findingDescription}",
                        style = MaterialTheme.typography.bodySmall.copy(color = WorkforceTextSecondary)
                    )

                    OutlinedTextField(
                        value = followUpObservationText,
                        onValueChange = { followUpObservationText = it },
                        label = { Text("Follow-up Observation") },
                        placeholder = { Text("Describe follow-up walkthrough findings") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = WorkforceOrange,
                            unfocusedBorderColor = WorkforceCardBorder,
                            focusedTextColor = WorkforceTextPrimary,
                            unfocusedTextColor = WorkforceTextPrimary
                        )
                    )

                    OutlinedTextField(
                        value = closureEvidenceText,
                        onValueChange = { closureEvidenceText = it },
                        label = { Text("Closure Evidence & Action Taken") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = WorkforceOrange,
                            unfocusedBorderColor = WorkforceCardBorder,
                            focusedTextColor = WorkforceTextPrimary,
                            unfocusedTextColor = WorkforceTextPrimary
                        )
                    )

                    OutlinedTextField(
                        value = closureSignatoryName,
                        onValueChange = { closureSignatoryName = it },
                        label = { Text("Responsible Person Signature (Name & Role)") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = WorkforceOrange,
                            unfocusedBorderColor = WorkforceCardBorder,
                            focusedTextColor = WorkforceTextPrimary,
                            unfocusedTextColor = WorkforceTextPrimary
                        )
                    )

                    Text(
                        text = "Date of completion: $todayStr (Electronic Attestation Stamped)",
                        style = MaterialTheme.typography.bodySmall.copy(color = WorkforceTextMuted, fontSize = 11.sp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val updated = a.copy(
                            status = ActionStatus.RESOLVED,
                            followUpObservation = followUpObservationText,
                            closureEvidence = closureEvidenceText,
                            closureDate = todayStr,
                            closureSignature = closureSignatoryName
                        )
                        viewModel.updateRemedialAction(updated)
                        viewModel.showNotification("Action ${a.actionReference} marked as Closed & Signed")
                        selectedActionForClosure = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ComplianceGreen)
                ) {
                    Text("Verify & Close Action")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedActionForClosure = null }) {
                    Text("Cancel", color = WorkforceTextSecondary)
                }
            },
            containerColor = WorkforceSurface
        )
    }
}

@Composable
fun RemedialActionCard(
    action: RemedialAction,
    onCloseActionClick: () -> Unit
) {
    val priorityColor = Color(action.priority.colorHex)
    val isResolved = action.status == ActionStatus.RESOLVED || action.status == ActionStatus.VERIFIED

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("remedial_action_card_${action.id}"),
        colors = CardDefaults.cardColors(containerColor = WorkforceSurface),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isResolved) ComplianceGreen.copy(alpha = 0.5f) else WorkforceCardBorder
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: Reference, Priority, and Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = action.actionReference,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = WorkforceOrange,
                            letterSpacing = 0.5.sp
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (isResolved) ComplianceGreen.copy(alpha = 0.2f) else WorkforceOrange.copy(alpha = 0.2f),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isResolved) ComplianceGreen else WorkforceOrange
                        )
                    ) {
                        Text(
                            text = action.status.label.uppercase(),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (isResolved) ComplianceGreen else WorkforceOrange,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = priorityColor.copy(alpha = 0.18f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, priorityColor.copy(alpha = 0.5f))
                ) {
                    Text(
                        text = action.priority.label.uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = priorityColor,
                            fontSize = 10.sp
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Finding Description
            Text(
                text = action.findingDescription,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = WorkforceTextPrimary
                )
            )

            Text(
                text = "${action.sectionTitle} • Question #${action.questionNumber}",
                style = MaterialTheme.typography.bodySmall.copy(color = WorkforceTextSecondary)
            )

            // Photographic evidence badge with markup indication
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = WorkforceSurfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(1.dp, WorkforceCardBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = null,
                            tint = if (action.photoUri != null) ComplianceGreen else WorkforceTextMuted,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (action.photoUri != null) "Photo Evidence Attached (Marked Up)" else "Photo Pending",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = if (action.photoUri != null) ComplianceGreen else WorkforceTextMuted,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Corrective Action & Responsible Party
            Surface(
                color = WorkforceSurfaceVariant,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = "Corrective Action Required:",
                        style = MaterialTheme.typography.labelSmall.copy(color = WorkforceTextMuted, fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = action.recommendedAction,
                        style = MaterialTheme.typography.bodySmall.copy(color = WorkforceTextPrimary)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = WorkforceOrange, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Responsible: ${action.responsiblePersonOrDept}",
                                style = MaterialTheme.typography.bodySmall.copy(color = WorkforceOrange, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Event, contentDescription = null, tint = WorkforceTextSecondary, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Target: ${action.targetDate}",
                                style = MaterialTheme.typography.bodySmall.copy(color = WorkforceTextSecondary, fontSize = 11.sp)
                            )
                        }
                    }
                }
            }

            // Follow-up Observation & Closure Details (if closed or follow-up recorded)
            if (action.followUpObservation.isNotBlank() || isResolved) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = if (isResolved) ComplianceGreen.copy(alpha = 0.08f) else WorkforceDarkBg,
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isResolved) ComplianceGreen.copy(alpha = 0.3f) else WorkforceCardBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        if (action.followUpObservation.isNotBlank()) {
                            Text(
                                text = "Follow-up Observation:",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (isResolved) ComplianceGreen else WorkforceOrange,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                text = action.followUpObservation,
                                style = MaterialTheme.typography.bodySmall.copy(color = WorkforceTextPrimary, fontSize = 12.sp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                        }

                        if (isResolved && action.closureSignature.isNotBlank()) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Draw,
                                    contentDescription = null,
                                    tint = ComplianceGreen,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Signed by ${action.closureSignature} on ${action.closureDate}",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = ComplianceGreen,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                if (!isResolved) {
                    Button(
                        onClick = onCloseActionClick,
                        colors = ButtonDefaults.buttonColors(containerColor = ComplianceGreen),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Verify Closure & Sign", style = MaterialTheme.typography.labelSmall)
                    }
                } else {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = ComplianceGreen.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ComplianceGreen.copy(alpha = 0.4f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = ComplianceGreen, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Closed & Verified",
                                style = MaterialTheme.typography.labelSmall.copy(color = ComplianceGreen, fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }
            }
        }
    }
}
