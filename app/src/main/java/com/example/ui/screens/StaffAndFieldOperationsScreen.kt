package com.example.ui.screens

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AssessmentBooking
import com.example.data.model.AssessmentTask
import com.example.data.model.BookingStatus
import com.example.data.model.CustomerMineAllocation
import com.example.data.model.FieldTrip
import com.example.data.model.StaffAvailability
import com.example.data.model.StaffMember
import com.example.data.model.StaffRole
import com.example.data.model.TaskPriority
import com.example.data.model.TaskStatus
import com.example.ui.components.AssessmentProgressPipelineView
import com.example.ui.components.AuditLogsView
import com.example.ui.components.CalendarBookingsView
import com.example.ui.components.CustomerMineAllocationView
import com.example.ui.components.DashboardGisMapComponent
import com.example.ui.components.FieldServicesView
import com.example.ui.components.FieldTripManagementView
import com.example.ui.components.IncompleteWorkView
import com.example.ui.components.NotificationsCenterView
import com.example.ui.components.ReportsAndStatisticsView
import com.example.ui.components.StaffDirectoryView
import com.example.ui.components.TaskAssignmentsView
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
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.WorkforceViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

data class OperationsNavSubmodule(
    val title: String,
    val icon: ImageVector,
    val testTag: String
)

val SUBMODULES = listOf(
    OperationsNavSubmodule("Staff Directory", Icons.Default.People, "submod_staff"),
    OperationsNavSubmodule("Assessor Mgmt", Icons.Default.Badge, "submod_assessors"),
    OperationsNavSubmodule("Task Assignments", Icons.Default.Assignment, "submod_tasks"),
    OperationsNavSubmodule("Calendar & Bookings", Icons.Default.CalendarMonth, "submod_calendar"),
    OperationsNavSubmodule("Mine Allocations", Icons.Default.Business, "submod_allocations"),
    OperationsNavSubmodule("Field Services", Icons.Default.Science, "submod_services"),
    OperationsNavSubmodule("Field Trips", Icons.Default.DirectionsCar, "submod_trips"),
    OperationsNavSubmodule("Live GPS Map", Icons.Default.Map, "submod_gps"),
    OperationsNavSubmodule("Incomplete Work", Icons.Default.ErrorOutline, "submod_incomplete"),
    OperationsNavSubmodule("Progress Pipeline", Icons.Default.Timeline, "submod_progress"),
    OperationsNavSubmodule("Reports & Stats", Icons.Default.QueryStats, "submod_reports"),
    OperationsNavSubmodule("Performance", Icons.Default.Engineering, "submod_performance"),
    OperationsNavSubmodule("Notifications", Icons.Default.Notifications, "submod_notifs"),
    OperationsNavSubmodule("Audit Trail", Icons.Default.History, "submod_audit")
)

@Composable
fun StaffAndFieldOperationsScreen(
    viewModel: WorkforceViewModel,
    onBack: () -> Unit
) {
    var selectedSubmoduleIndex by remember { mutableIntStateOf(0) }

    val staffList by viewModel.staffList.collectAsState()
    val tasks by viewModel.taskList.collectAsState()
    val bookings by viewModel.bookingList.collectAsState()
    val allocations by viewModel.allocationsList.collectAsState()
    val fieldServices by viewModel.fieldServicesList.collectAsState()
    val trips by viewModel.fieldTripsList.collectAsState()
    val liveGpsList by viewModel.liveGpsList.collectAsState()
    val notifications by viewModel.notificationsList.collectAsState()
    val auditLogs by viewModel.auditLogsList.collectAsState()
    val isGpsActive by viewModel.isLiveGpsActive.collectAsState()
    val assessments by viewModel.allAssessments.collectAsState()

    val unreadNotifs = notifications.count { !it.isRead }

    // Dialog state controllers
    var showAddStaffDialog by remember { mutableStateOf(false) }
    var showCreateTaskDialog by remember { mutableStateOf(false) }
    var showCreateBookingDialog by remember { mutableStateOf(false) }
    var showStartTripDialog by remember { mutableStateOf(false) }
    var endTripTarget by remember { mutableStateOf<FieldTrip?>(null) }
    var reassignTargetTask by remember { mutableStateOf<AssessmentTask?>(null) }
    var showAllocateSiteDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            Surface(
                color = WorkforceDarkBg,
                border = androidx.compose.foundation.BorderStroke(1.dp, WorkforceCardBorder)
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = onBack, modifier = Modifier.testTag("btn_back_staff_ops")) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                            Column {
                                Text(
                                    text = "STAFF & FIELD OPERATIONS",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 0.8.sp,
                                        color = WorkforceTextPrimary
                                    )
                                )
                                Text(
                                    text = "Workforce Health SA • Assessor Scheduling & Logistics",
                                    style = MaterialTheme.typography.labelSmall.copy(color = WorkforceOrange)
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Live GPS toggle button
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = if (isGpsActive) ComplianceGreen.copy(alpha = 0.2f) else WorkforceSurfaceVariant,
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (isGpsActive) ComplianceGreen else WorkforceCardBorder),
                                modifier = Modifier.clickable { viewModel.toggleLiveGpsTracking() }.testTag("btn_toggle_live_gps")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(if (isGpsActive) ComplianceGreen else WorkforceTextMuted)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isGpsActive) "GPS BROADCAST ON" else "START GPS",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = if (isGpsActive) ComplianceGreen else WorkforceTextSecondary,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 9.sp
                                        )
                                    )
                                }
                            }

                            // Notification badge button
                            Surface(
                                shape = CircleShape,
                                color = WorkforceSurfaceVariant,
                                border = androidx.compose.foundation.BorderStroke(1.dp, WorkforceCardBorder),
                                modifier = Modifier
                                    .size(36.dp)
                                    .clickable { selectedSubmoduleIndex = 12 }
                                    .testTag("btn_header_notifications")
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Notifications,
                                        contentDescription = "Notifications",
                                        tint = if (unreadNotifs > 0) WorkforceRed else WorkforceTextSecondary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    if (unreadNotifs > 0) {
                                        Box(
                                            modifier = Modifier
                                                .align(Alignment.TopEnd)
                                                .padding(2.dp)
                                                .size(8.dp)
                                                .clip(CircleShape)
                                                .background(WorkforceRed)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Scrollable Submodule Navigation Tabs (All 14 Modules)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        SUBMODULES.forEachIndexed { index, submod ->
                            val isSelected = selectedSubmoduleIndex == index
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) WorkforceRed else WorkforceSurfaceVariant,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isSelected) WorkforceRed else WorkforceCardBorder
                                ),
                                modifier = Modifier
                                    .clickable { selectedSubmoduleIndex = index }
                                    .testTag(submod.testTag)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = submod.icon,
                                        contentDescription = null,
                                        tint = if (isSelected) Color.White else WorkforceTextSecondary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = submod.title,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = if (isSelected) Color.White else WorkforceTextPrimary,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        containerColor = WorkforceDarkBg
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            when (selectedSubmoduleIndex) {
                0 -> {
                    // Staff Management
                    StaffDirectoryView(
                        staffList = staffList,
                        onAddStaffClick = { showAddStaffDialog = true },
                        onEditStaffClick = { staff ->
                            viewModel.showNotification("Editing ${staff.fullName}")
                        },
                        onDeactivateStaffClick = { staffId ->
                            viewModel.deactivateStaff(staffId)
                        },
                        onAvailabilityChange = { id, status ->
                            viewModel.staffOpsRepo.setStaffAvailability(id, status)
                        }
                    )
                }
                1 -> {
                    // Assessor Management
                    val assessorsOnly = staffList.filter {
                        it.role == StaffRole.OCCUPATIONAL_HYGIENE_ASSISTANT ||
                        it.role == StaffRole.OCCUPATIONAL_HYGIENIST ||
                        it.role == StaffRole.SENIOR_OCCUPATIONAL_HYGIENIST
                    }
                    StaffDirectoryView(
                        staffList = assessorsOnly,
                        onAddStaffClick = { showAddStaffDialog = true },
                        onEditStaffClick = { staff -> viewModel.showNotification("Assessor ${staff.fullName} updated") },
                        onDeactivateStaffClick = { id -> viewModel.deactivateStaff(id) },
                        onAvailabilityChange = { id, status -> viewModel.staffOpsRepo.setStaffAvailability(id, status) }
                    )
                }
                2 -> {
                    // Task Assignments
                    TaskAssignmentsView(
                        tasks = tasks,
                        onCreateTaskClick = { showCreateTaskDialog = true },
                        onStatusChange = { taskId, status ->
                            viewModel.updateTaskStatus(taskId, status)
                        },
                        onReassignClick = { task ->
                            reassignTargetTask = task
                        }
                    )
                }
                3 -> {
                    // Assessment Scheduling & Calendar Bookings
                    CalendarBookingsView(
                        bookings = bookings,
                        onNewBookingClick = { showCreateBookingDialog = true },
                        onStatusChange = { bookingId, status ->
                            viewModel.updateBookingStatus(bookingId, status)
                        }
                    )
                }
                4 -> {
                    // Customer & Mine Allocation
                    CustomerMineAllocationView(
                        allocations = allocations,
                        onNewAllocationClick = { showAllocateSiteDialog = true }
                    )
                }
                5 -> {
                    // Field Services Management
                    FieldServicesView(services = fieldServices)
                }
                6 -> {
                    // Field Trip Management (Start Trip / End Trip)
                    FieldTripManagementView(
                        trips = trips,
                        onStartTripClick = { showStartTripDialog = true },
                        onEndTripClick = { trip -> endTripTarget = trip }
                    )
                }
                7 -> {
                    // Live GPS Tracking & OpenStreetMap Leaflet GIS
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Live GPS & OpenStreetMap Assessor Tracking",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = WorkforceTextPrimary)
                        )
                        Text(
                            text = "Real Device Coordinates • Monitored Mines & Change House Complexes",
                            style = MaterialTheme.typography.bodySmall.copy(color = WorkforceTextSecondary)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        DashboardGisMapComponent(
                            onSelectSiteForInspection = { site ->
                                viewModel.navigateTo(AppScreen.CustomerSiteSelect)
                            }
                        )
                    }
                }
                8 -> {
                    // Incomplete Work Management
                    IncompleteWorkView(
                        assessments = assessments,
                        onOpenAssessment = { id ->
                            viewModel.navigateTo(AppScreen.AssessmentWizard(id))
                        },
                        onSendReminder = { id ->
                            viewModel.showNotification("Statutory reminder dispatched to appointed assessor.")
                        }
                    )
                }
                9 -> {
                    // Assessment Progress Tracking (12-stage pipeline)
                    AssessmentProgressPipelineView(assessments = assessments)
                }
                10 -> {
                    // Reports & Statistics
                    ReportsAndStatisticsView(
                        assessments = assessments,
                        staffList = staffList,
                        trips = trips,
                        onExportPdfClick = {
                            viewModel.showNotification("Generating comprehensive Field Operations PDF report...")
                        }
                    )
                }
                11 -> {
                    // Staff Performance
                    ReportsAndStatisticsView(
                        assessments = assessments,
                        staffList = staffList,
                        trips = trips,
                        onExportPdfClick = {
                            viewModel.showNotification("Exporting Assessor Performance Audit...")
                        }
                    )
                }
                12 -> {
                    // Notifications & Reminders
                    NotificationsCenterView(
                        notifications = notifications,
                        onMarkReadClick = { id -> viewModel.markNotificationAsRead(id) },
                        onClearAllClick = {
                            viewModel.clearAllNotifications("STF-1001")
                        }
                    )
                }
                13 -> {
                    // Audit Logs
                    AuditLogsView(logs = auditLogs)
                }
            }
        }
    }

    // ==========================================
    // MODAL DIALOGS
    // ==========================================

    // 1. Add Staff Dialog
    if (showAddStaffDialog) {
        var name by remember { mutableStateOf("") }
        var email by remember { mutableStateOf("") }
        var phone by remember { mutableStateOf("+27 ") }
        var jobTitle by remember { mutableStateOf("Occupational Hygiene Assistant") }
        var regNum by remember { mutableStateOf("SAIOH-ROHA-") }

        AlertDialog(
            onDismissRequest = { showAddStaffDialog = false },
            title = { Text("Add New Staff / Assessor Account", color = WorkforceTextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Full Name & Surname") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Official Email Address") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Mobile Contact") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = jobTitle, onValueChange = { jobTitle = it }, label = { Text("Job Title") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = regNum, onValueChange = { regNum = it }, label = { Text("SAIOH / Statutory Reg #") }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (name.isNotBlank() && email.isNotBlank()) {
                            val newStaff = StaffMember(
                                id = "STF-${UUID.randomUUID().toString().take(6).uppercase()}",
                                fullName = name,
                                employeeNumber = "EMP-2026-${(100..999).random()}",
                                email = email,
                                phone = phone,
                                jobTitle = jobTitle,
                                role = StaffRole.OCCUPATIONAL_HYGIENE_ASSISTANT,
                                ohRegistrationNumber = regNum
                            )
                            viewModel.addOrUpdateStaff(newStaff)
                            showAddStaffDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = WorkforceRed)
                ) {
                    Text("Save Staff Member")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddStaffDialog = false }) { Text("Cancel", color = WorkforceTextSecondary) }
            },
            containerColor = WorkforceSurface
        )
    }

    // 2. Create Task Dialog
    if (showCreateTaskDialog) {
        var title by remember { mutableStateOf("Monthly Ablution Sanitation Survey") }
        var mine by remember { mutableStateOf("Letaba Quarry Tzaneen") }
        var facility by remember { mutableStateOf("Change House Complex #1") }
        var dueDate by remember { mutableStateOf("2026-11-15") }
        var instructions by remember { mutableStateOf("Capture compulsory voice remarks on moulds and upload GPS photo.") }

        AlertDialog(
            onDismissRequest = { showCreateTaskDialog = false },
            title = { Text("Assign New Assessment Task", color = WorkforceTextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Task Title") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = mine, onValueChange = { mine = it }, label = { Text("Mine Site Name") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = facility, onValueChange = { facility = it }, label = { Text("Facility / Change House") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = dueDate, onValueChange = { dueDate = it }, label = { Text("Due Date (YYYY-MM-DD)") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = instructions, onValueChange = { instructions = it }, label = { Text("Special Instructions") }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val task = AssessmentTask(
                            id = "TSK-${(100..999).random()}",
                            referenceNumber = "TSK-2026-00${(50..99).random()}",
                            title = title,
                            description = "Statutory MHSA compliance task for $mine.",
                            assignedStaffId = "STF-1001",
                            assignedStaffName = "Nomsa Khumalo",
                            customerOrgId = "CUST_LETABA",
                            customerOrgName = "Letaba Crushers",
                            mineSiteId = "SITE_LETABA",
                            mineSiteName = mine,
                            assessmentType = "Ablution Facilities Sanitation Audit",
                            facilityLocation = facility,
                            priority = TaskPriority.HIGH,
                            assignmentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
                            scheduledDate = dueDate,
                            expectedCompletionDate = dueDate,
                            dueDate = dueDate,
                            instructions = instructions,
                            status = TaskStatus.ASSIGNED
                        )
                        viewModel.createTask(task)
                        showCreateTaskDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = WorkforceRed)
                ) {
                    Text("Assign Task")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateTaskDialog = false }) { Text("Cancel", color = WorkforceTextSecondary) }
            },
            containerColor = WorkforceSurface
        )
    }

    // 3. Book Visit Dialog
    if (showCreateBookingDialog) {
        var site by remember { mutableStateOf("Letaba Quarry Tzaneen") }
        var date by remember { mutableStateOf("2026-11-18") }
        var time by remember { mutableStateOf("08:30") }
        var contact by remember { mutableStateOf("Mr. Du Plessis (Mine Mgr)") }
        var phone by remember { mutableStateOf("+27 15 307 4000") }

        AlertDialog(
            onDismissRequest = { showCreateBookingDialog = false },
            title = { Text("Book Assessment Visit", color = WorkforceTextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = site, onValueChange = { site = it }, label = { Text("Mine Site") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = date, onValueChange = { date = it }, label = { Text("Visit Date (YYYY-MM-DD)") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = time, onValueChange = { time = it }, label = { Text("Arrival Time") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = contact, onValueChange = { contact = it }, label = { Text("Mine Appointment Contact") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Contact Phone") }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val booking = AssessmentBooking(
                            id = "BKG-2026-${(30..99).random()}",
                            customerId = "CUST_LETABA",
                            customerName = "Letaba Crushers",
                            siteId = "SITE_LETABA",
                            siteName = site,
                            assessmentType = "Monthly ablution facilities checklist",
                            facilityLocation = "Main Ablution Complex",
                            assignedAssessorId = "STF-1001",
                            assignedAssessorName = "Nomsa Khumalo",
                            bookingDate = date,
                            arrivalTime = time,
                            appointmentContact = contact,
                            contactTelephone = phone,
                            status = BookingStatus.BOOKED
                        )
                        viewModel.createBooking(booking)
                        showCreateBookingDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = WorkforceRed)
                ) {
                    Text("Confirm Booking")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateBookingDialog = false }) { Text("Cancel", color = WorkforceTextSecondary) }
            },
            containerColor = WorkforceSurface
        )
    }

    // 4. Start Field Trip Dialog
    if (showStartTripDialog) {
        var vehicle by remember { mutableStateOf("DF 42 LK GP") }
        var odo by remember { mutableStateOf("142610") }
        var destination by remember { mutableStateOf("Letaba Quarry Tzaneen") }

        AlertDialog(
            onDismissRequest = { showStartTripDialog = false },
            title = { Text("Start Authorised Field Trip", color = WorkforceTextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "GPS coordinates will be captured and broadcast to operations dashboard during transit.",
                        style = MaterialTheme.typography.bodySmall.copy(color = WorkforceOrange)
                    )
                    OutlinedTextField(value = vehicle, onValueChange = { vehicle = it }, label = { Text("Vehicle Registration") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = odo, onValueChange = { odo = it }, label = { Text("Current Odometer Reading (km)") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = destination, onValueChange = { destination = it }, label = { Text("Destination Mine Site") }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.startFieldTrip(
                            assessorId = "STF-1001",
                            assessorName = "Nomsa Khumalo",
                            customerName = "Letaba Crushers (Pty) Ltd",
                            mineSiteName = destination,
                            destinationAddress = "Farm Tarentaalrant 24LT, Tzaneen",
                            startOdo = odo.toIntOrNull() ?: 142610,
                            startLat = -23.8211,
                            startLng = 30.1633,
                            vehicleReg = vehicle,
                            notes = "Authorised audit travel."
                        )
                        showStartTripDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = WorkforceRed)
                ) {
                    Text("Start Trip & GPS Tracking")
                }
            },
            dismissButton = {
                TextButton(onClick = { showStartTripDialog = false }) { Text("Cancel", color = WorkforceTextSecondary) }
            },
            containerColor = WorkforceSurface
        )
    }

    // 5. End Field Trip Dialog
    if (endTripTarget != null) {
        val target = endTripTarget!!
        var endOdo by remember { mutableStateOf("${target.startOdometer + 45}") }
        var notes by remember { mutableStateOf("Arrived safely at mine gate. Pre-entry briefing attended.") }

        AlertDialog(
            onDismissRequest = { endTripTarget = null },
            title = { Text("End Field Trip & Log Final Odometer", color = WorkforceTextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "Trip: ${target.id} • ${target.mineSiteName}", style = MaterialTheme.typography.bodySmall.copy(color = WorkforceOrange))
                    OutlinedTextField(value = endOdo, onValueChange = { endOdo = it }, label = { Text("Final Odometer Reading (km)") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("Trip Completion Notes") }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.endFieldTrip(
                            tripId = target.id,
                            endOdo = endOdo.toIntOrNull(),
                            endLat = -23.8211,
                            endLng = 30.1633,
                            notes = notes
                        )
                        endTripTarget = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ComplianceGreen)
                ) {
                    Text("Complete & Save Trip Log")
                }
            },
            dismissButton = {
                TextButton(onClick = { endTripTarget = null }) { Text("Cancel", color = WorkforceTextSecondary) }
            },
            containerColor = WorkforceSurface
        )
    }

    // 6. Reassign Task Dialog
    if (reassignTargetTask != null) {
        val targetTask = reassignTargetTask!!
        var selectedStaff by remember { mutableStateOf(staffList.firstOrNull { it.id != targetTask.assignedStaffId } ?: staffList.first()) }
        var reassignNotes by remember { mutableStateOf("Reassigned due to scheduling reallocation.") }

        AlertDialog(
            onDismissRequest = { reassignTargetTask = null },
            title = { Text("Reassign Task ${targetTask.referenceNumber}", color = WorkforceTextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "Currently assigned to: ${targetTask.assignedStaffName}", style = MaterialTheme.typography.bodySmall.copy(color = WorkforceTextSecondary))
                    Text(text = "Select New Assessor:", fontWeight = FontWeight.Bold, color = WorkforceTextPrimary, fontSize = 12.sp)

                    staffList.filter { it.role == StaffRole.OCCUPATIONAL_HYGIENE_ASSISTANT || it.role == StaffRole.OCCUPATIONAL_HYGIENIST }.forEach { staff ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedStaff = staff },
                            shape = RoundedCornerShape(8.dp),
                            color = if (selectedStaff.id == staff.id) WorkforceRed.copy(alpha = 0.2f) else WorkforceSurfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (selectedStaff.id == staff.id) WorkforceRed else WorkforceCardBorder)
                        ) {
                            Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "${staff.fullName} (${staff.jobTitle})",
                                    fontSize = 11.sp,
                                    fontWeight = if (selectedStaff.id == staff.id) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selectedStaff.id == staff.id) WorkforceOrange else WorkforceTextPrimary
                                )
                            }
                        }
                    }

                    OutlinedTextField(value = reassignNotes, onValueChange = { reassignNotes = it }, label = { Text("Reassignment Notes") }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.reassignTask(targetTask.id, selectedStaff.id, selectedStaff.fullName, reassignNotes)
                        reassignTargetTask = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = WorkforceRed)
                ) {
                    Text("Confirm Reassignment")
                }
            },
            dismissButton = {
                TextButton(onClick = { reassignTargetTask = null }) { Text("Cancel", color = WorkforceTextSecondary) }
            },
            containerColor = WorkforceSurface
        )
    }

    // 7. Allocate Site Dialog
    if (showAllocateSiteDialog) {
        var selectedAssessor by remember { mutableStateOf(staffList.first()) }
        var siteName by remember { mutableStateOf("Dwarsrivier Chrome Mine") }
        var notes by remember { mutableStateOf("Lead Occupational Hygiene Assistant appointment.") }

        AlertDialog(
            onDismissRequest = { showAllocateSiteDialog = false },
            title = { Text("Allocate Customer Mine Site", color = WorkforceTextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = siteName, onValueChange = { siteName = it }, label = { Text("Mine Site Name") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("Scope / Statutory Notes") }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val alc = CustomerMineAllocation(
                            id = "ALC-${(200..999).random()}",
                            assessorId = selectedAssessor.id,
                            assessorName = selectedAssessor.fullName,
                            customerId = "CUST_MINE",
                            customerName = "Mining Operation",
                            siteId = "SITE_MINE",
                            siteName = siteName,
                            allocatedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
                            notes = notes
                        )
                        viewModel.allocateCustomerMine(alc)
                        showAllocateSiteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = WorkforceRed)
                ) {
                    Text("Save Allocation")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAllocateSiteDialog = false }) { Text("Cancel", color = WorkforceTextSecondary) }
            },
            containerColor = WorkforceSurface
        )
    }
}
