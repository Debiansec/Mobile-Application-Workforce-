package com.example.ui.components

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
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Assessment
import com.example.data.model.AuditLogEvent
import com.example.data.model.CustomerMineAllocation
import com.example.data.model.FieldServiceRecord
import com.example.data.model.FieldTrip
import com.example.data.model.StaffMember
import com.example.data.model.StaffNotification
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
// 7. CUSTOMER & MINE ALLOCATION VIEW
// ==========================================
@Composable
fun CustomerMineAllocationView(
    allocations: List<CustomerMineAllocation>,
    onNewAllocationClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Customer & Mine Allocations",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = WorkforceTextPrimary)
                )
                Text(
                    text = "Appointed Assessor Allocation Matrix • Statutory MHSA Coverage",
                    style = MaterialTheme.typography.bodySmall.copy(color = WorkforceTextSecondary)
                )
            }
            Button(
                onClick = onNewAllocationClick,
                colors = ButtonDefaults.buttonColors(containerColor = WorkforceRed),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text("ALLOCATE SITE", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier.fillMaxWidth().height(520.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(allocations, key = { it.id }) { alc ->
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
                                text = alc.siteName,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = WorkforceTextPrimary)
                            )
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = ComplianceGreen.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "ALLOCATED",
                                    style = MaterialTheme.typography.labelSmall.copy(color = ComplianceGreen, fontWeight = FontWeight.Bold, fontSize = 9.sp),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "Customer Organisation: ${alc.customerName}",
                            style = MaterialTheme.typography.bodySmall.copy(color = WorkforceTextSecondary, fontSize = 12.sp)
                        )
                        Text(
                            text = "Lead Assessor: ${alc.assessorName} • Appointed by: ${alc.allocatedBy}",
                            style = MaterialTheme.typography.bodySmall.copy(color = WorkforceOrange, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        )
                        if (alc.notes.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Scope: ${alc.notes}",
                                style = MaterialTheme.typography.bodySmall.copy(color = WorkforceTextMuted, fontSize = 10.sp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 8. FIELD SERVICES MANAGEMENT VIEW
// ==========================================
@Composable
fun FieldServicesView(services: List<FieldServiceRecord>) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Field Services Allocation & Surveys",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = WorkforceTextPrimary)
        )
        Text(
            text = "Occupational Hygiene • Noise, Dust, Thermal & Sanitation Coverage",
            style = MaterialTheme.typography.bodySmall.copy(color = WorkforceTextSecondary)
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier.fillMaxWidth().height(520.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(services, key = { it.id }) { srv ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = WorkforceSurface),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, WorkforceCardBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier.size(42.dp),
                            shape = CircleShape,
                            color = WorkforceRed.copy(alpha = 0.15f)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Science, contentDescription = null, tint = WorkforceRed, modifier = Modifier.size(20.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = srv.category.code,
                                    style = MaterialTheme.typography.labelSmall.copy(color = WorkforceOrange, fontWeight = FontWeight.Bold)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = srv.siteName,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = WorkforceTextPrimary)
                                )
                            }
                            Text(
                                text = srv.title,
                                style = MaterialTheme.typography.bodySmall.copy(color = WorkforceTextSecondary, fontSize = 11.sp)
                            )
                            Text(
                                text = "Assigned: ${srv.assignedAssessorName} • Date: ${srv.scheduledDate}",
                                style = MaterialTheme.typography.labelSmall.copy(color = WorkforceTextMuted, fontSize = 10.sp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 9. REPORTS & STATISTICS DASHBOARD VIEW
// ==========================================
@Composable
fun ReportsAndStatisticsView(
    assessments: List<Assessment>,
    staffList: List<StaffMember>,
    trips: List<FieldTrip>,
    onExportPdfClick: () -> Unit
) {
    val totalAudits = assessments.size
    val compliantCount = assessments.count { it.complianceScore >= 85 }
    val avgScore = if (totalAudits > 0) assessments.map { it.complianceScore }.average().toInt() else 0
    val totalTrips = trips.size
    val totalKm = trips.sumOf { it.totalDistanceKm }.toInt()

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Operational Intelligence & Reports",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = WorkforceTextPrimary)
                )
                Text(
                    text = "Live Database Metrics • Traceable Statutory Statistics",
                    style = MaterialTheme.typography.bodySmall.copy(color = WorkforceTextSecondary)
                )
            }
            Button(
                onClick = onExportPdfClick,
                colors = ButtonDefaults.buttonColors(containerColor = WorkforceRed),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("EXPORT PDF", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // High level KPI Metric Grid
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatCard(title = "Total Audits", value = "$totalAudits", subtitle = "$compliantCount Passed", accent = ComplianceGreen, modifier = Modifier.weight(1f))
            StatCard(title = "Average Score", value = "$avgScore%", subtitle = "MHSA Benchmark", accent = WorkforceOrange, modifier = Modifier.weight(1f))
            StatCard(title = "Total Travel", value = "$totalKm km", subtitle = "$totalTrips Field Trips", accent = Color(0xFF0284C7), modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "Assessor Performance Comparison",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = WorkforceTextPrimary)
        )
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.fillMaxWidth().height(380.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(staffList.filter { it.role == com.example.data.model.StaffRole.OCCUPATIONAL_HYGIENE_ASSISTANT || it.role == com.example.data.model.StaffRole.OCCUPATIONAL_HYGIENIST }) { staff ->
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
                        Column {
                            Text(text = staff.fullName, fontWeight = FontWeight.Bold, color = WorkforceTextPrimary, fontSize = 13.sp)
                            Text(text = "${staff.jobTitle} • ${staff.employeeNumber}", fontSize = 10.sp, color = WorkforceTextSecondary)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column(horizontalAlignment = Alignment.End) {
                                Text(text = "${staff.completedTasksCount} Completed", fontWeight = FontWeight.Bold, color = ComplianceGreen, fontSize = 12.sp)
                                Text(text = "${staff.outstandingTasksCount} Active Tasks", fontSize = 10.sp, color = WorkforceOrange)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(title: String, value: String, subtitle: String, accent: Color, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        color = WorkforceSurface,
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, accent.copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(text = title, fontSize = 10.sp, color = WorkforceTextSecondary, fontWeight = FontWeight.SemiBold)
            Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.Black, color = WorkforceTextPrimary)
            Text(text = subtitle, fontSize = 9.sp, color = accent, fontWeight = FontWeight.Bold)
        }
    }
}

// ==========================================
// 10. NOTIFICATIONS & REMINDERS CENTER
// ==========================================
@Composable
fun NotificationsCenterView(
    notifications: List<StaffNotification>,
    onMarkReadClick: (String) -> Unit,
    onClearAllClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Internal Notifications & Reminders",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = WorkforceTextPrimary)
                )
                Text(
                    text = "${notifications.count { !it.isRead }} Unread • Task Assignments, Reminders & Overdue Alerts",
                    style = MaterialTheme.typography.bodySmall.copy(color = WorkforceTextSecondary)
                )
            }
            if (notifications.isNotEmpty()) {
                IconButton(onClick = onClearAllClick) {
                    Icon(Icons.Default.DeleteSweep, contentDescription = "Clear All", tint = WorkforceTextMuted)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (notifications.isEmpty()) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                color = WorkforceSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, WorkforceCardBorder)
            ) {
                Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = ComplianceGreen, modifier = Modifier.size(36.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("All Caught Up", fontWeight = FontWeight.Bold, color = WorkforceTextPrimary)
                    Text("No pending statutory alerts or unread notices.", fontSize = 11.sp, color = WorkforceTextSecondary)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth().height(520.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(notifications, key = { it.id }) { notif ->
                    Surface(
                        modifier = Modifier.fillMaxWidth().clickable { onMarkReadClick(notif.id) },
                        shape = RoundedCornerShape(10.dp),
                        color = if (notif.isRead) WorkforceSurface else WorkforceSurfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (notif.isRead) WorkforceCardBorder else WorkforceRed.copy(alpha = 0.5f)
                        )
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                modifier = Modifier.size(36.dp),
                                shape = CircleShape,
                                color = if (notif.isRead) WorkforceSurfaceVariant else WorkforceRed.copy(alpha = 0.2f)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Notifications, contentDescription = null, tint = if (notif.isRead) WorkforceTextMuted else WorkforceRed, modifier = Modifier.size(18.dp))
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = notif.category, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = WorkforceOrange)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = notif.formattedTime, fontSize = 9.sp, color = WorkforceTextMuted)
                                }
                                Text(text = notif.title, fontWeight = FontWeight.Bold, color = WorkforceTextPrimary, fontSize = 12.sp)
                                Text(text = notif.message, fontSize = 11.sp, color = WorkforceTextSecondary)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 11. AUDIT LOGS SUBVIEW
// ==========================================
@Composable
fun AuditLogsView(logs: List<AuditLogEvent>) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Permanent Statutory Audit Trail",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = WorkforceTextPrimary)
        )
        Text(
            text = "Traceable Event Logging • MHSA Governance & Administrative Actions",
            style = MaterialTheme.typography.bodySmall.copy(color = WorkforceTextSecondary)
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier.fillMaxWidth().height(520.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(logs, key = { it.id }) { log ->
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    color = WorkforceSurface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, WorkforceCardBorder)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = log.action, fontWeight = FontWeight.Bold, color = WorkforceOrange, fontSize = 11.sp)
                            Text(text = log.formattedDate, fontSize = 9.sp, color = WorkforceTextMuted)
                        }
                        Text(text = "Operator: ${log.operatorName} • Entity: ${log.targetEntity}", fontSize = 11.sp, color = WorkforceTextPrimary)
                        Text(text = log.details, fontSize = 10.sp, color = WorkforceTextSecondary)
                    }
                }
            }
        }
    }
}
