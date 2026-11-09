package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PriorityHigh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.data.model.Assessment
import com.example.data.model.AssessmentStatus
import com.example.data.model.CustomerMineAllocation
import com.example.data.model.FieldServiceRecord
import com.example.data.model.FieldTrip
import com.example.data.model.IncompleteAssessmentItem
import com.example.data.model.IncompleteReason
import com.example.data.model.StaffMember
import com.example.data.model.TripStatus
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

// ==========================================
// 4. FIELD TRIP MANAGEMENT SUBVIEW (START / END TRIP)
// ==========================================
@Composable
fun FieldTripManagementView(
    trips: List<FieldTrip>,
    onStartTripClick: () -> Unit,
    onEndTripClick: (FieldTrip) -> Unit
) {
    val activeTrip = trips.firstOrNull { it.status == TripStatus.IN_PROGRESS }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Field Trip & Fleet Operations",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = WorkforceTextPrimary
                    )
                )
                Text(
                    text = "Start Trip / End Trip Odometer & GPS Location Logging",
                    style = MaterialTheme.typography.bodySmall.copy(color = WorkforceTextSecondary)
                )
            }
            if (activeTrip == null) {
                Button(
                    onClick = onStartTripClick,
                    colors = ButtonDefaults.buttonColors(containerColor = WorkforceRed),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("btn_start_new_field_trip")
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("START TRIP", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Active Trip Card (if in progress)
        if (activeTrip != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("active_field_trip_card"),
                colors = CardDefaults.cardColors(containerColor = WorkforceSurface),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, WorkforceOrange)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(WorkforceOrange)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "ACTIVE TRIP IN PROGRESS",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    color = WorkforceOrange,
                                    letterSpacing = 0.5.sp
                                )
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = WorkforceOrange.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = activeTrip.vehicleRegistration,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = WorkforceOrange,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                ),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Destination: ${activeTrip.mineSiteName} • ${activeTrip.customerName}",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = WorkforceTextPrimary)
                    )
                    Text(
                        text = "Assessor: ${activeTrip.assessorName} • Started: ${activeTrip.startDateTime}",
                        style = MaterialTheme.typography.bodySmall.copy(color = WorkforceTextSecondary, fontSize = 11.sp)
                    )
                    Text(
                        text = "Start Odometer: ${activeTrip.startOdometer} km • GPS: ${activeTrip.startLat.toString().take(7)}, ${activeTrip.startLng.toString().take(7)}",
                        style = MaterialTheme.typography.bodySmall.copy(color = WorkforceTextMuted, fontSize = 10.sp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { onEndTripClick(activeTrip) },
                        colors = ButtonDefaults.buttonColors(containerColor = ComplianceGreen),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("btn_end_field_trip")
                    ) {
                        Icon(Icons.Default.Stop, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color.White)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("ARRIVED AT MINE — END TRIP", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.White)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }

        Text(
            text = "Trip History & Odometer Logs",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = WorkforceTextPrimary)
        )
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.fillMaxWidth().height(420.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(trips, key = { it.id }) { trip ->
                TripHistoryItemCard(trip = trip)
            }
        }
    }
}

@Composable
fun TripHistoryItemCard(trip: FieldTrip) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        color = WorkforceSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, WorkforceCardBorder)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Surface(
                    modifier = Modifier.size(36.dp),
                    shape = CircleShape,
                    color = Color(trip.status.colorHex).copy(alpha = 0.15f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.DirectionsCar,
                            contentDescription = null,
                            tint = Color(trip.status.colorHex),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "${trip.mineSiteName} (${trip.customerName})",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = WorkforceTextPrimary)
                    )
                    Text(
                        text = "${trip.assessorName} • ${trip.startDateTime}",
                        style = MaterialTheme.typography.labelSmall.copy(color = WorkforceTextSecondary, fontSize = 10.sp)
                    )
                    Text(
                        text = "Vehicle: ${trip.vehicleRegistration} • Distance: ${trip.totalDistanceKm} km",
                        style = MaterialTheme.typography.labelSmall.copy(color = WorkforceOrange, fontSize = 10.sp)
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(4.dp),
                color = Color(trip.status.colorHex).copy(alpha = 0.15f)
            ) {
                Text(
                    text = trip.status.label,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color(trip.status.colorHex),
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.sp
                    ),
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
    }
}

// ==========================================
// 5. INCOMPLETE WORK DASHBOARD SUBVIEW
// ==========================================
@Composable
fun IncompleteWorkView(
    assessments: List<Assessment>,
    onOpenAssessment: (String) -> Unit,
    onSendReminder: (String) -> Unit
) {
    // Generate incomplete work items from real assessments
    val incompleteItems = remember(assessments) {
        assessments.filter {
            it.status == AssessmentStatus.DRAFT ||
            it.status == AssessmentStatus.IN_PROGRESS ||
            it.status == AssessmentStatus.RETURNED_FOR_CORRECTION ||
            it.answeredCount < it.totalQuestions
        }.map { asm ->
            val reasons = mutableListOf<IncompleteReason>()
            if (asm.answeredCount == 0) reasons.add(IncompleteReason.NOT_STARTED)
            if (asm.answeredCount in 1 until asm.totalQuestions) reasons.add(IncompleteReason.INCOMPLETE_CHECKLIST)
            if (asm.status == AssessmentStatus.RETURNED_FOR_CORRECTION) reasons.add(IncompleteReason.RETURNED_FOR_CORRECTIONS)
            if (asm.complianceScore < 85) reasons.add(IncompleteReason.MISSING_MANDATORY_REMARKS)

            IncompleteAssessmentItem(
                assessmentId = asm.id,
                referenceNumber = asm.id.take(14).uppercase(),
                customerName = asm.customerName,
                siteName = asm.siteName,
                facilityName = asm.facilityName,
                assignedAssessorName = asm.assessorName,
                assignedDate = "2026-11-01",
                scheduledDate = "2026-11-08",
                dueDate = "2026-11-10",
                daysOverdue = if (asm.status == AssessmentStatus.RETURNED_FOR_CORRECTION) 2 else 0,
                reasons = reasons.ifEmpty { listOf(IncompleteReason.INCOMPLETE_CHECKLIST) },
                answeredCount = asm.answeredCount,
                totalCount = asm.totalQuestions,
                score = asm.complianceScore
            )
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Incomplete Work & Bottlenecks",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = WorkforceTextPrimary
                    )
                )
                Text(
                    text = "${incompleteItems.size} Audits Require Attention • Missing Signatures & Incomplete Checklists",
                    style = MaterialTheme.typography.bodySmall.copy(color = WorkforceTextSecondary)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (incompleteItems.isEmpty()) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                color = WorkforceSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, WorkforceCardBorder)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = ComplianceGreen, modifier = Modifier.size(40.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("All Assessments Completed & Signed", fontWeight = FontWeight.Bold, color = WorkforceTextPrimary)
                    Text("No outstanding statutory bottlenecks detected.", fontSize = 12.sp, color = WorkforceTextSecondary)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth().height(520.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(incompleteItems, key = { it.assessmentId }) { item ->
                    IncompleteWorkCard(
                        item = item,
                        onOpenClick = { onOpenAssessment(item.assessmentId) },
                        onReminderClick = { onSendReminder(item.assessmentId) }
                    )
                }
            }
        }
    }
}

@Composable
fun IncompleteWorkCard(
    item: IncompleteAssessmentItem,
    onOpenClick: () -> Unit,
    onReminderClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = WorkforceSurface),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, ComplianceRed.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Error, contentDescription = null, tint = ComplianceRed, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = item.siteName,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = WorkforceTextPrimary)
                    )
                }
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = ComplianceRed.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "ATTENTION REQUIRED",
                        style = MaterialTheme.typography.labelSmall.copy(color = ComplianceRed, fontWeight = FontWeight.Bold, fontSize = 9.sp),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "${item.facilityName} • Customer: ${item.customerName}",
                style = MaterialTheme.typography.bodySmall.copy(color = WorkforceTextSecondary, fontSize = 12.sp)
            )
            Text(
                text = "Assigned Assessor: ${item.assignedAssessorName}",
                style = MaterialTheme.typography.bodySmall.copy(color = WorkforceOrange, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Identified Incomplete Reasons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                item.reasons.forEach { reason ->
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = ComplianceRed.copy(alpha = 0.1f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ComplianceRed.copy(alpha = 0.3f))
                    ) {
                        Text(
                            text = reason.label,
                            style = MaterialTheme.typography.labelSmall.copy(color = ComplianceRed, fontSize = 9.sp),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Progress bar
            val progress = if (item.totalCount > 0) item.answeredCount.toFloat() / item.totalCount else 0f
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Checklist Progress: ${item.answeredCount}/${item.totalCount} questions",
                    style = MaterialTheme.typography.labelSmall.copy(color = WorkforceTextMuted, fontSize = 10.sp)
                )
                Text(
                    text = "${(progress * 100).toInt()}%",
                    style = MaterialTheme.typography.labelSmall.copy(color = WorkforceTextPrimary, fontWeight = FontWeight.Bold)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                color = if (progress >= 0.8f) ComplianceGreen else WorkforceOrange,
                trackColor = WorkforceSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onReminderClick,
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Text("Send Reminder", fontSize = 10.sp)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = onOpenClick,
                    colors = ButtonDefaults.buttonColors(containerColor = WorkforceRed),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Text("Continue Audit", fontSize = 10.sp, color = Color.White)
                }
            }
        }
    }
}

// ==========================================
// 6. ASSESSMENT PROGRESS (12-STAGE PIPELINE)
// ==========================================
@Composable
fun AssessmentProgressPipelineView(assessments: List<Assessment>) {
    val stages = listOf(
        "1. Created",
        "2. Assessor Assigned",
        "3. Booked",
        "4. Confirmed",
        "5. Trip Started",
        "6. Arrived",
        "7. Audit Started",
        "8. Checklist Done",
        "9. Submitted",
        "10. Report Built",
        "11. Under Review",
        "12. Final Approved"
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "12-Stage Lifecycle Assessment Pipeline",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = WorkforceTextPrimary)
        )
        Text(
            text = "Traceable Statutory Lifecycle • MHSA Section 24 Regulatory Pipeline",
            style = MaterialTheme.typography.bodySmall.copy(color = WorkforceTextSecondary)
        )

        Spacer(modifier = Modifier.height(14.dp))

        LazyColumn(
            modifier = Modifier.fillMaxWidth().height(520.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(assessments, key = { it.id }) { asm ->
                val currentStageIndex = when (asm.status) {
                    AssessmentStatus.DRAFT -> if (asm.answeredCount > 0) 6 else 1
                    AssessmentStatus.IN_PROGRESS -> 7
                    AssessmentStatus.SUBMITTED_FOR_REVIEW -> 10
                    AssessmentStatus.APPROVED -> 11
                    else -> 2
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = WorkforceSurface),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, WorkforceCardBorder)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${asm.siteName} • ${asm.facilityName}",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = WorkforceTextPrimary)
                            )
                            StatusBadge(status = asm.status)
                        }

                        Text(
                            text = "Assessor: ${asm.assessorName} • Score: ${asm.complianceScore}%",
                            style = MaterialTheme.typography.bodySmall.copy(color = WorkforceOrange, fontSize = 11.sp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Stage indicator pills
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            stages.forEachIndexed { idx, stageTitle ->
                                val isPassed = idx <= currentStageIndex
                                val isCurrent = idx == currentStageIndex
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (isCurrent) WorkforceRed else if (isPassed) ComplianceGreen.copy(alpha = 0.2f) else WorkforceSurfaceVariant,
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (isCurrent) WorkforceRed else if (isPassed) ComplianceGreen.copy(alpha = 0.5f) else WorkforceCardBorder
                                    )
                                ) {
                                    Text(
                                        text = stageTitle,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = if (isCurrent) Color.White else if (isPassed) ComplianceGreen else WorkforceTextMuted,
                                            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                                            fontSize = 9.sp
                                        ),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
