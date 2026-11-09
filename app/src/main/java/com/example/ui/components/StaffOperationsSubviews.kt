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
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PriorityHigh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.example.data.model.AssessmentBooking
import com.example.data.model.AssessmentStatus
import com.example.data.model.AssessmentTask
import com.example.data.model.BookingStatus
import com.example.data.model.CustomerMineAllocation
import com.example.data.model.FieldServiceRecord
import com.example.data.model.FieldTrip
import com.example.data.model.StaffAvailability
import com.example.data.model.StaffMember
import com.example.data.model.StaffRole
import com.example.data.model.TaskPriority
import com.example.data.model.TaskStatus
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
// 1. STAFF DIRECTORY & PROFILES SUBVIEW
// ==========================================
@Composable
fun StaffDirectoryView(
    staffList: List<StaffMember>,
    onAddStaffClick: () -> Unit,
    onEditStaffClick: (StaffMember) -> Unit,
    onDeactivateStaffClick: (String) -> Unit,
    onAvailabilityChange: (String, StaffAvailability) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Staff Directory & Qualifications",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = WorkforceTextPrimary
                    )
                )
                Text(
                    text = "${staffList.count { it.isActive }} Active Personnel • SAIOH & MHSA Registered",
                    style = MaterialTheme.typography.bodySmall.copy(color = WorkforceTextSecondary)
                )
            }
            Button(
                onClick = onAddStaffClick,
                colors = ButtonDefaults.buttonColors(containerColor = WorkforceRed),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                modifier = Modifier.testTag("btn_add_staff_member")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("ADD STAFF", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier.fillMaxWidth().height(520.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(staffList, key = { it.id }) { staff ->
                StaffProfileCard(
                    staff = staff,
                    onEditClick = { onEditStaffClick(staff) },
                    onDeactivateClick = { onDeactivateStaffClick(staff.id) },
                    onAvailabilityChange = { onAvailabilityChange(staff.id, it) }
                )
            }
        }
    }
}

@Composable
fun StaffProfileCard(
    staff: StaffMember,
    onEditClick: () -> Unit,
    onDeactivateClick: () -> Unit,
    onAvailabilityChange: (StaffAvailability) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("staff_card_${staff.id}"),
        colors = CardDefaults.cardColors(containerColor = WorkforceSurface),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, WorkforceCardBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Surface(
                        modifier = Modifier.size(46.dp),
                        shape = CircleShape,
                        color = Color(staff.role.badgeColorHex).copy(alpha = 0.2f),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(staff.role.badgeColorHex))
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = staff.fullName.take(2).uppercase(),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    color = Color(staff.role.badgeColorHex)
                                )
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = staff.fullName,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = WorkforceTextPrimary
                                )
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(staff.role.badgeColorHex).copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = staff.employeeNumber,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color(staff.role.badgeColorHex),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 9.sp
                                    ),
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = staff.role.displayName,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(staff.role.badgeColorHex),
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 11.sp
                            )
                        )
                        Text(
                            text = "${staff.email} • ${staff.phone}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = WorkforceTextMuted,
                                fontSize = 10.sp
                            ),
                            maxLines = 1
                        )
                    }
                }

                // Availability badge
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(staff.availabilityStatus.colorHex).copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(staff.availabilityStatus.colorHex).copy(alpha = 0.4f)),
                    modifier = Modifier.clickable {
                        val nextStatus = when (staff.availabilityStatus) {
                            StaffAvailability.AVAILABLE -> StaffAvailability.ON_ASSIGNMENT
                            StaffAvailability.ON_ASSIGNMENT -> StaffAvailability.TRAVELLING
                            StaffAvailability.TRAVELLING -> StaffAvailability.AVAILABLE
                            else -> StaffAvailability.AVAILABLE
                        }
                        onAvailabilityChange(nextStatus)
                    }
                ) {
                    Text(
                        text = staff.availabilityStatus.label,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color(staff.availabilityStatus.colorHex),
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Qualifications & SAIOH Registration
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = WorkforceSurfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(1.dp, WorkforceCardBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Verified, contentDescription = null, tint = ComplianceGreen, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = staff.ohRegistrationNumber.ifBlank { "SAIOH Registered" },
                            style = MaterialTheme.typography.labelSmall.copy(color = WorkforceTextSecondary, fontSize = 10.sp)
                        )
                    }
                }

                staff.qualifications.take(2).forEach { qual ->
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = WorkforceSurfaceVariant
                    ) {
                        Text(
                            text = qual,
                            style = MaterialTheme.typography.labelSmall.copy(color = WorkforceTextMuted, fontSize = 10.sp),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            // Expandable details (tasks & action buttons)
            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 10.dp)) {
                    HorizontalDivider(color = WorkforceCardBorder)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Department: ${staff.department}",
                        style = MaterialTheme.typography.bodySmall.copy(color = WorkforceTextSecondary, fontSize = 11.sp)
                    )
                    Text(
                        text = "Working Hours: ${staff.workingHours}",
                        style = MaterialTheme.typography.bodySmall.copy(color = WorkforceTextSecondary, fontSize = 11.sp)
                    )
                    Text(
                        text = "Performance: ${staff.completedTasksCount} audits completed • ${staff.outstandingTasksCount} active tasks",
                        style = MaterialTheme.typography.bodySmall.copy(color = WorkforceOrange, fontWeight = FontWeight.SemiBold, fontSize = 11.sp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = onEditClick,
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.height(30.dp)
                        ) {
                            Text("Edit Profile", fontSize = 10.sp)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = onDeactivateClick,
                            colors = ButtonDefaults.buttonColors(containerColor = ComplianceRed.copy(alpha = 0.8f)),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.height(30.dp)
                        ) {
                            Text("Deactivate", fontSize = 10.sp, color = Color.White)
                        }
                    }
                }
            }

            // Expand / Collapse toggle bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp)
                    .clickable { expanded = !expanded },
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = if (expanded) "▲ Show Less" else "▼ View Details & Tasks",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = WorkforceOrange,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    )
                )
            }
        }
    }
}

// ==========================================
// 2. TASK ASSIGNMENTS SUBVIEW
// ==========================================
@Composable
fun TaskAssignmentsView(
    tasks: List<AssessmentTask>,
    onCreateTaskClick: () -> Unit,
    onStatusChange: (String, TaskStatus) -> Unit,
    onReassignClick: (AssessmentTask) -> Unit
) {
    var selectedPriorityFilter by remember { mutableStateOf<TaskPriority?>(null) }
    var selectedStatusFilter by remember { mutableStateOf<TaskStatus?>(null) }

    val filteredTasks = tasks.filter { task ->
        (selectedPriorityFilter == null || task.priority == selectedPriorityFilter) &&
        (selectedStatusFilter == null || task.status == selectedStatusFilter)
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Central Task Assignments",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = WorkforceTextPrimary
                    )
                )
                Text(
                    text = "${tasks.size} Total Assigned • ${tasks.count { it.status == TaskStatus.IN_PROGRESS }} In Progress",
                    style = MaterialTheme.typography.bodySmall.copy(color = WorkforceTextSecondary)
                )
            }
            Button(
                onClick = onCreateTaskClick,
                colors = ButtonDefaults.buttonColors(containerColor = WorkforceRed),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                modifier = Modifier.testTag("btn_create_new_task")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("NEW TASK", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Priority Filter Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            FilterChip(
                selected = selectedPriorityFilter == null,
                onClick = { selectedPriorityFilter = null },
                label = { Text("All Priorities", fontSize = 10.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = WorkforceRed,
                    selectedLabelColor = Color.White
                )
            )
            TaskPriority.entries.forEach { prio ->
                FilterChip(
                    selected = selectedPriorityFilter == prio,
                    onClick = { selectedPriorityFilter = if (selectedPriorityFilter == prio) null else prio },
                    label = { Text(prio.label, fontSize = 10.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(prio.colorHex),
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier.fillMaxWidth().height(520.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filteredTasks, key = { it.id }) { task ->
                TaskCard(
                    task = task,
                    onStatusChange = { onStatusChange(task.id, it) },
                    onReassignClick = { onReassignClick(task) }
                )
            }
        }
    }
}

@Composable
fun TaskCard(
    task: AssessmentTask,
    onStatusChange: (TaskStatus) -> Unit,
    onReassignClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("task_item_${task.id}"),
        colors = CardDefaults.cardColors(containerColor = WorkforceSurface),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, WorkforceCardBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Task Header: Ref # & Priority badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = WorkforceSurfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(1.dp, WorkforceCardBorder)
                    ) {
                        Text(
                            text = task.referenceNumber,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = WorkforceOrange,
                                fontSize = 10.sp
                            ),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Due: ${task.dueDate}",
                        style = MaterialTheme.typography.bodySmall.copy(color = WorkforceTextMuted, fontSize = 11.sp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color(task.priority.colorHex).copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(task.priority.colorHex))
                ) {
                    Text(
                        text = task.priority.label.uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color(task.priority.colorHex),
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp
                        ),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = task.title,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = WorkforceTextPrimary
                )
            )

            Text(
                text = task.description,
                style = MaterialTheme.typography.bodySmall.copy(color = WorkforceTextSecondary, fontSize = 12.sp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Site & Facility details
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = WorkforceDarkBg,
                border = androidx.compose.foundation.BorderStroke(1.dp, WorkforceCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Text(
                        text = "Mine Site: ${task.mineSiteName} • ${task.customerOrgName}",
                        style = MaterialTheme.typography.labelSmall.copy(color = WorkforceTextPrimary, fontWeight = FontWeight.SemiBold)
                    )
                    Text(
                        text = "Facility: ${task.facilityLocation} • Assessor: ${task.assignedStaffName}",
                        style = MaterialTheme.typography.bodySmall.copy(color = WorkforceOrange, fontSize = 11.sp)
                    )
                    if (task.instructions.isNotBlank()) {
                        Text(
                            text = "Instructions: ${task.instructions}",
                            style = MaterialTheme.typography.bodySmall.copy(color = WorkforceTextMuted, fontSize = 10.sp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Status & Quick Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(task.status.colorHex).copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(task.status.colorHex).copy(alpha = 0.5f))
                ) {
                    Text(
                        text = task.status.label,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color(task.status.colorHex),
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedButton(
                        onClick = onReassignClick,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.height(30.dp)
                    ) {
                        Text("Reassign", fontSize = 10.sp)
                    }

                    if (task.status != TaskStatus.COMPLETED) {
                        Button(
                            onClick = { onStatusChange(TaskStatus.COMPLETED) },
                            colors = ButtonDefaults.buttonColors(containerColor = ComplianceGreen),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.height(30.dp)
                        ) {
                            Text("Mark Done", fontSize = 10.sp, color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 3. CALENDAR & BOOKINGS SUBVIEW
// ==========================================
@Composable
fun CalendarBookingsView(
    bookings: List<AssessmentBooking>,
    onNewBookingClick: () -> Unit,
    onStatusChange: (String, BookingStatus) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Calendar & Assessment Bookings",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = WorkforceTextPrimary
                    )
                )
                Text(
                    text = "Interactive Scheduling • Double Booking & Conflict Protection",
                    style = MaterialTheme.typography.bodySmall.copy(color = WorkforceTextSecondary)
                )
            }
            Button(
                onClick = onNewBookingClick,
                colors = ButtonDefaults.buttonColors(containerColor = WorkforceRed),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                modifier = Modifier.testTag("btn_book_assessment_visit")
            ) {
                Icon(Icons.Default.DateRange, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("BOOK VISIT", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier.fillMaxWidth().height(520.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(bookings, key = { it.id }) { booking ->
                BookingCard(booking = booking, onStatusChange = { onStatusChange(booking.id, it) })
            }
        }
    }
}

@Composable
fun BookingCard(booking: AssessmentBooking, onStatusChange: (BookingStatus) -> Unit) {
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = WorkforceOrange, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${booking.bookingDate} (${booking.arrivalTime} - ${booking.departureTime})",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = WorkforceTextPrimary)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color(booking.status.colorHex).copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(booking.status.colorHex))
                ) {
                    Text(
                        text = booking.status.label,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color(booking.status.colorHex),
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp
                        ),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${booking.siteName} • ${booking.customerName}",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold, color = WorkforceTextPrimary)
            )
            Text(
                text = "Type: ${booking.assessmentType} • ${booking.facilityLocation}",
                style = MaterialTheme.typography.bodySmall.copy(color = WorkforceTextSecondary, fontSize = 11.sp)
            )
            Text(
                text = "Assigned Assessor: ${booking.assignedAssessorName} ${if (booking.additionalTeamMembers.isNotBlank()) "+ " + booking.additionalTeamMembers else ""}",
                style = MaterialTheme.typography.bodySmall.copy(color = WorkforceOrange, fontSize = 11.sp)
            )
            Text(
                text = "Mine Contact: ${booking.appointmentContact} (${booking.contactTelephone})",
                style = MaterialTheme.typography.bodySmall.copy(color = WorkforceTextMuted, fontSize = 10.sp)
            )

            if (booking.specialInstructions.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = WorkforceSurfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Instructions: ${booking.specialInstructions}",
                        style = MaterialTheme.typography.bodySmall.copy(color = WorkforceTextMuted, fontSize = 10.sp),
                        modifier = Modifier.padding(6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (booking.status == BookingStatus.BOOKED) {
                    Button(
                        onClick = { onStatusChange(BookingStatus.CONFIRMED) },
                        colors = ButtonDefaults.buttonColors(containerColor = ComplianceGreen),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.height(30.dp)
                    ) {
                        Text("Confirm Booking", fontSize = 10.sp, color = Color.White)
                    }
                } else if (booking.status == BookingStatus.CONFIRMED) {
                    Button(
                        onClick = { onStatusChange(BookingStatus.COMPLETED) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.height(30.dp)
                    ) {
                        Text("Mark Visit Completed", fontSize = 10.sp, color = Color.White)
                    }
                }
            }
        }
    }
}
