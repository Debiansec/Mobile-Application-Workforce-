package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.R
import com.example.ui.components.DashboardGisMapComponent
import com.example.ui.components.TodayInspectionSummaryChart
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Biotech
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Web
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Assessment
import com.example.data.model.AssessmentStatus
import com.example.data.model.ChecklistType
import com.example.ui.components.ExportReportDialog
import com.example.ui.components.InspectionAnalyticsSection
import androidx.compose.runtime.mutableIntStateOf
import com.example.data.model.SyncStatus
import com.example.ui.components.StatusBadge
import com.example.ui.components.SyncDetailDialog
import com.example.ui.components.SyncProgressDashboardCard
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
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.WorkforceViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun DashboardScreen(
    viewModel: WorkforceViewModel
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val allUsers by viewModel.allUsers.collectAsState()
    val customers by viewModel.customersList.collectAsState()
    val sites by viewModel.sitesList.collectAsState()
    val syncStatus by viewModel.syncManager.syncState.collectAsState()
    val assessments by viewModel.allAssessments.collectAsState()
    val outstandingActions by viewModel.outstandingRemedialActions.collectAsState()
    val staffList by viewModel.staffList.collectAsState()
    val taskList by viewModel.taskList.collectAsState()
    val fieldTrips by viewModel.fieldTripsList.collectAsState()
    var showSyncDialog by remember { mutableStateOf(false) }
    var exportTargetAssessment by remember { mutableStateOf<Assessment?>(null) }
    val isExportingPdf by viewModel.isExportingPdf.collectAsState()
    val lastExportedPath by viewModel.lastExportedPath.collectAsState()
    var selectedDashboardTab by remember { mutableIntStateOf(0) }

    val drafts = assessments.filter { it.status == AssessmentStatus.DRAFT || it.status == AssessmentStatus.IN_PROGRESS }
    val awaitingReview = assessments.filter { it.status == AssessmentStatus.SUBMITTED_FOR_REVIEW }
    val approved = assessments.filter { it.status == AssessmentStatus.APPROVED }

    if (showSyncDialog) {
        SyncDetailDialog(
            syncManager = viewModel.syncManager,
            onDismiss = { showSyncDialog = false },
            onSyncNow = { viewModel.syncCloud() },
            onToggleOffline = { viewModel.toggleOfflineMode() }
        )
    }

    if (exportTargetAssessment != null) {
        ExportReportDialog(
            assessment = exportTargetAssessment,
            isGenerating = isExportingPdf,
            savedFilePath = lastExportedPath,
            onSaveToDownloads = {
                viewModel.savePdfToPublicDownloads(exportTargetAssessment)
            },
            onShareExternal = {
                viewModel.sharePdfReport(exportTargetAssessment)
            },
            onOpenPdfViewer = {
                viewModel.openPdfReport(exportTargetAssessment)
            },
            onPrintDocument = {
                viewModel.printPdfReport(exportTargetAssessment)
            },
            onDismiss = {
                exportTargetAssessment = null
            }
        )
    }

    Scaffold(
        topBar = {
            WorkforceTopBar(
                title = "WORKFORCE HEALTH SA",
                subtitle = "${currentUser.name} (${currentUser.role.displayName})",
                syncStatus = syncStatus,
                onSyncClick = { showSyncDialog = true },
                actions = {
                    IconButton(
                        onClick = { viewModel.navigateTo(AppScreen.UserManagement) },
                        modifier = Modifier.testTag("dashboard_user_management_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ManageAccounts,
                            contentDescription = "User Management & Roles",
                            tint = WorkforceOrange
                        )
                    }
                    IconButton(
                        onClick = { viewModel.logout() },
                        modifier = Modifier.testTag("dashboard_logout_btn")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Logout",
                            tint = WorkforceTextMuted
                        )
                    }
                }
            )
        },
        containerColor = WorkforceDarkBg
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Segmented Top Tab Switcher: Field Operations vs Analytics & Trends
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(10.dp),
                color = WorkforceSurfaceVariant,
                border = androidx.compose.foundation.BorderStroke(1.dp, WorkforceCardBorder)
            ) {
                Row(modifier = Modifier.padding(3.dp)) {
                    listOf("FIELD OPERATIONS", "PROGRESS & 30D TRENDS").forEachIndexed { idx, title ->
                        val isSelected = selectedDashboardTab == idx
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) WorkforceRed else Color.Transparent,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedDashboardTab = idx }
                                .padding(vertical = 8.dp)
                                .testTag("tab_dashboard_$idx")
                        ) {
                            Text(
                                text = title,
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else WorkforceTextSecondary,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }
            }

            if (selectedDashboardTab == 1) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("analytics_scroll_column"),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        InspectionAnalyticsSection(assessments = assessments)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("dashboard_scroll_column"),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Mining Operations Hero Banner (Uploaded Asset with Branding & Miner)
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("dashboard_hero_banner_card"),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = WorkforceSurface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, WorkforceCardBorder)
                        ) {
                            Column {
                                Image(
                                    painter = painterResource(id = R.drawable.hero_mining_banner),
                                    contentDescription = "WorkForce Health SA Mining Operations Hero Banner",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(138.dp)
                                        .clip(RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp)),
                                    contentScale = ContentScale.Crop
                                )
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(WorkforceSurfaceVariant)
                                        .padding(horizontal = 14.dp, vertical = 9.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .clip(CircleShape)
                                                .background(WorkforceRed)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "WORKFORCE HEALTH SA • MINING HYGIENE SUITE",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Black,
                                                color = WorkforceTextPrimary,
                                                letterSpacing = 0.5.sp
                                            )
                                        )
                                    }
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = ComplianceGreen.copy(alpha = 0.15f),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, ComplianceGreen.copy(alpha = 0.4f))
                                    ) {
                                        Text(
                                            text = "MHSA SECTION 24",
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = ComplianceGreen,
                                                fontSize = 9.sp
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Interactive Leaflet GIS Cadastral OpenStreetMap Under Hero Banner
                    item {
                        DashboardGisMapComponent(
                            onSelectSiteForInspection = { siteGis ->
                                viewModel.navigateTo(AppScreen.CustomerSiteSelect)
                            }
                        )
                    }

                    // Summary Component: Total Inspections Completed, Pending, and Synced Today with Chart
                    item {
                        TodayInspectionSummaryChart(
                            assessments = assessments,
                            onViewPendingClick = { selectedDashboardTab = 0 },
                            onViewCompletedClick = { selectedDashboardTab = 0 }
                        )
                    }

                    // Active Authorized User & Role Card
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.navigateTo(AppScreen.UserManagement) }
                                .testTag("dashboard_active_user_card"),
                            colors = CardDefaults.cardColors(containerColor = WorkforceSurface),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, WorkforceCardBorder)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    modifier = Modifier.size(42.dp),
                                    shape = CircleShape,
                                    color = Color(currentUser.role.badgeColorHex).copy(alpha = 0.2f),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(currentUser.role.badgeColorHex))
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = currentUser.name.take(2).uppercase(),
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                fontWeight = FontWeight.Black,
                                                color = Color(currentUser.role.badgeColorHex)
                                            )
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = currentUser.name,
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = WorkforceTextPrimary
                                            )
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            color = Color(currentUser.role.badgeColorHex).copy(alpha = 0.15f),
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(
                                                text = currentUser.role.displayName,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    color = Color(currentUser.role.badgeColorHex),
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 9.sp
                                                )
                                            )
                                        }
                                    }
                                    Text(
                                        text = "${currentUser.email} • ${currentUser.registrationNumber}",
                                        style = MaterialTheme.typography.bodySmall.copy(color = WorkforceTextMuted),
                                        maxLines = 1
                                    )
                                }
                                OutlinedButton(
                                    onClick = { viewModel.navigateTo(AppScreen.UserManagement) },
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = WorkforceOrange),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, WorkforceOrange.copy(alpha = 0.5f)),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text("Roles", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    // Quick Analytics Teaser Card
                    item {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = WorkforceSurface,
                            border = androidx.compose.foundation.BorderStroke(1.dp, WorkforceCardBorder),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedDashboardTab = 1 }
                                .testTag("analytics_teaser_card")
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(horizontal = 14.dp, vertical = 10.dp)
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(ComplianceGreen)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "30-Day Inspection Velocity: ${assessments.size} Audits Recorded",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = WorkforceTextPrimary,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    )
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "View Trends",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = WorkforceOrange,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = null,
                                        tint = WorkforceOrange,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Hero Action: START NEW ASSESSMENT
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("start_new_assessment_card"),
                            colors = CardDefaults.cardColors(containerColor = WorkforceRed),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "START NEW INSPECTION",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 1.sp,
                                        color = Color.White
                                    )
                                )
                                Text(
                                    text = "Module: Monthly Ablution Facilities Checklist (Male)",
                                    style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.9f))
                                )
                            }
                            Surface(
                                shape = CircleShape,
                                color = Color.White.copy(alpha = 0.2f),
                                modifier = Modifier.size(44.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Start",
                                        tint = Color.White,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = { viewModel.navigateTo(AppScreen.CustomerSiteSelect) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("btn_select_customer_site")
                            ) {
                                Text(
                                    text = "CUSTOMER & SITE",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = WorkforceRed,
                                        fontSize = 12.sp
                                    )
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null,
                                    tint = WorkforceRed,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            Button(
                                onClick = { viewModel.navigateTo(AppScreen.InspectionFormScreen()) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Black.copy(alpha = 0.4f)),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("btn_launch_inspection_form")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CameraAlt,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "INSPECTION FORM",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        fontSize = 12.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Visual Progress Dashboard for Offline Data Sync Status
            item {
                SyncProgressDashboardCard(
                    syncManager = viewModel.syncManager,
                    onSyncNowClick = { viewModel.syncCloud() },
                    onToggleOfflineClick = { viewModel.toggleOfflineMode() }
                )
            }

            // Quick Operational Metric Grid
            item {
                Text(
                    text = "Operational Hygiene Metrics",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = WorkforceTextPrimary,
                        letterSpacing = 0.5.sp
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricMiniCard(
                        title = "In Progress",
                        count = drafts.size.toString(),
                        icon = Icons.Default.PendingActions,
                        accentColor = WorkforceOrange,
                        modifier = Modifier.weight(1f)
                    )
                    MetricMiniCard(
                        title = "For Review",
                        count = awaitingReview.size.toString(),
                        icon = Icons.Default.Assignment,
                        accentColor = Color(0xFF38BDF8),
                        modifier = Modifier.weight(1f)
                    )
                    MetricMiniCard(
                        title = "Approved",
                        count = approved.size.toString(),
                        icon = Icons.Default.CheckCircle,
                        accentColor = ComplianceGreen,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Reference Site Banner: Letaba Crushers
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = WorkforceSurface),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, WorkforceCardBorder)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = WorkforceOrange.copy(alpha = 0.15f),
                            modifier = Modifier.size(42.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = WorkforceOrange,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Today's Assigned Site Visit",
                                style = MaterialTheme.typography.labelMedium.copy(color = WorkforceOrange, fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "Letaba Crushers (Pty) Ltd — Mine #14466",
                                style = MaterialTheme.typography.titleSmall.copy(color = WorkforceTextPrimary, fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "Farm Tarentaalrant 24LT • Tzaneen, Limpopo",
                                style = MaterialTheme.typography.bodySmall.copy(color = WorkforceTextSecondary)
                            )
                        }
                    }
                }
            }

            // Dashboard Menu Grid Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Operations & Management Suite",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = WorkforceTextPrimary,
                            letterSpacing = 0.5.sp
                        )
                    )
                    Text(
                        text = "Mine Safety Hub",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = WorkforceOrange,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            // Main Feature: Staff & Field Operations Suite
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.navigateTo(AppScreen.StaffAndFieldOps) }
                        .testTag("menu_btn_staff_field_ops"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = WorkforceSurface),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, WorkforceOrange)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    modifier = Modifier.size(46.dp),
                                    shape = RoundedCornerShape(10.dp),
                                    color = WorkforceOrange.copy(alpha = 0.2f),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, WorkforceOrange)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.People,
                                            contentDescription = null,
                                            tint = WorkforceOrange,
                                            modifier = Modifier.size(26.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "STAFF & FIELD OPERATIONS",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Black,
                                            letterSpacing = 0.5.sp,
                                            color = WorkforceTextPrimary
                                        )
                                    )
                                    Text(
                                        text = "Staff, Assessor, Tasks, Scheduling & GPS",
                                        style = MaterialTheme.typography.bodySmall.copy(color = WorkforceTextSecondary)
                                    )
                                }
                            }
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = WorkforceOrange.copy(alpha = 0.15f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, WorkforceOrange.copy(alpha = 0.5f))
                            ) {
                                Text(
                                    text = "14 MODULES",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Black,
                                        color = WorkforceOrange,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                color = WorkforceSurfaceVariant,
                                border = androidx.compose.foundation.BorderStroke(1.dp, WorkforceCardBorder)
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text(
                                        text = "Active Staff",
                                        style = MaterialTheme.typography.labelSmall.copy(color = WorkforceTextMuted, fontSize = 10.sp)
                                    )
                                    Text(
                                        text = "${staffList.size} Registered",
                                        style = MaterialTheme.typography.titleSmall.copy(color = WorkforceTextPrimary, fontWeight = FontWeight.Bold)
                                    )
                                }
                            }
                            Surface(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                color = WorkforceSurfaceVariant,
                                border = androidx.compose.foundation.BorderStroke(1.dp, WorkforceCardBorder)
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text(
                                        text = "Pending Tasks",
                                        style = MaterialTheme.typography.labelSmall.copy(color = WorkforceTextMuted, fontSize = 10.sp)
                                    )
                                    Text(
                                        text = "${taskList.count { it.status != com.example.data.model.TaskStatus.COMPLETED }} Assigned",
                                        style = MaterialTheme.typography.titleSmall.copy(color = WorkforceOrange, fontWeight = FontWeight.Bold)
                                    )
                                }
                            }
                            Surface(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                color = WorkforceSurfaceVariant,
                                border = androidx.compose.foundation.BorderStroke(1.dp, WorkforceCardBorder)
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text(
                                        text = "Field Trips",
                                        style = MaterialTheme.typography.labelSmall.copy(color = WorkforceTextMuted, fontSize = 10.sp)
                                    )
                                    Text(
                                        text = "${fieldTrips.count { it.status == com.example.data.model.TripStatus.IN_PROGRESS }} Active GPS",
                                        style = MaterialTheme.typography.titleSmall.copy(color = ComplianceGreen, fontWeight = FontWeight.Bold)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = { viewModel.navigateTo(AppScreen.StaffAndFieldOps) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .testTag("btn_launch_staff_field_ops"),
                            colors = ButtonDefaults.buttonColors(containerColor = WorkforceOrange),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "OPEN STAFF & FIELD OPERATIONS",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            // Dashboard Grid Row 1: Customer Accounts & Mine Sites + Health & Safety Audit
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    DashboardGridCard(
                        title = "Customer Accounts & Mine Sites",
                        badge = "${customers.size} Accounts • ${sites.size} Sites",
                        description = "Afrimat, Kuipersbult Quarry, CIPC legal details, mining rights & water licenses",
                        icon = Icons.Default.Business,
                        accentColor = WorkforceOrange,
                        modifier = Modifier.weight(1f),
                        testTag = "menu_btn_customers",
                        onClick = { viewModel.navigateTo(AppScreen.CustomerManagement) }
                    )

                    DashboardGridCard(
                        title = "Health & Safety Criteria Audit",
                        badge = "Standard Radio Form",
                        description = "Standard ablution criteria with radio buttons, remarks, photos & GPS tags",
                        icon = Icons.Default.FactCheck,
                        accentColor = ComplianceGreen,
                        modifier = Modifier.weight(1f),
                        testTag = "menu_btn_hs_audit",
                        onClick = { viewModel.navigateTo(AppScreen.HealthSafetyAudit) }
                    )
                }
            }

            // Dashboard Grid Row 2: Remedial Actions + Equipment Register
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    DashboardGridCard(
                        title = "Remedial Actions Register",
                        badge = "${outstandingActions.size} Open Actions",
                        description = "Non-conformances, target closure dates, responsible persons & sign-offs",
                        icon = Icons.Default.Warning,
                        accentColor = ComplianceRed,
                        modifier = Modifier.weight(1f),
                        testTag = "menu_btn_remedials",
                        onClick = { viewModel.navigateTo(AppScreen.RemedialActions()) }
                    )

                    DashboardGridCard(
                        title = "Equipment Register",
                        badge = "3 Calibration Loggers",
                        description = "WBGT heat stress checkers, Kestrel airflow meters & calibration tracking",
                        icon = Icons.Default.Build,
                        accentColor = Color(0xFF60A5FA),
                        modifier = Modifier.weight(1f),
                        testTag = "menu_btn_equipment",
                        onClick = { viewModel.navigateTo(AppScreen.EquipmentRegister) }
                    )
                }
            }

            // Dashboard Grid Row 3: Authorized Users & Roles + Web Management Portal
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    DashboardGridCard(
                        title = "Authorized Users & Roles",
                        badge = "${allUsers.size} Users",
                        description = "Login credentials, SAIOH registration profiles & statutory hygiene permissions",
                        icon = Icons.Default.ManageAccounts,
                        accentColor = Color(0xFF10B981),
                        modifier = Modifier.weight(1f),
                        testTag = "menu_btn_users",
                        onClick = { viewModel.navigateTo(AppScreen.UserManagement) }
                    )

                    DashboardGridCard(
                        title = "Web Management Portal",
                        badge = "wfhsa.com/new/",
                        description = "Executive reporting portal, live cloud sync & remote client access",
                        icon = Icons.Default.Web,
                        accentColor = Color(0xFFA855F7),
                        modifier = Modifier.weight(1f),
                        testTag = "menu_btn_web_portal",
                        onClick = { viewModel.navigateTo(AppScreen.WebPortalPreview) }
                    )
                }
            }

            // Assessments List Section
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recent Inspections & Assessments",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = WorkforceTextPrimary
                        )
                    )
                    Text(
                        text = "${assessments.size} Total",
                        style = MaterialTheme.typography.bodySmall.copy(color = WorkforceTextMuted)
                    )
                }
            }

            if (assessments.isEmpty()) {
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = WorkforceSurface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, WorkforceCardBorder)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "No assessments recorded yet.",
                                style = MaterialTheme.typography.bodyMedium.copy(color = WorkforceTextSecondary)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = { viewModel.navigateTo(AppScreen.CustomerSiteSelect) },
                                colors = ButtonDefaults.buttonColors(containerColor = WorkforceRed)
                            ) {
                                Text("Create First Inspection")
                            }
                        }
                    }
                }
            } else {
                items(assessments) { assessment ->
                    AssessmentListItem(
                        assessment = assessment,
                        onOpen = {
                            viewModel.loadAssessment(assessment.id)
                            viewModel.navigateTo(AppScreen.AssessmentWizard(assessment.id))
                        },
                        onViewReport = {
                            viewModel.loadAssessment(assessment.id)
                            viewModel.navigateTo(AppScreen.ReportPreview(assessment.id))
                        },
                        onExportPdf = {
                            viewModel.loadAssessment(assessment.id)
                            exportTargetAssessment = assessment
                        }
                    )
                }
            }

            // Role Switcher shortcut at bottom
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.navigateTo(AppScreen.Login) }
                        .testTag("switch_user_banner"),
                    shape = RoundedCornerShape(10.dp),
                    color = WorkforceSurfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(1.dp, WorkforceCardBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Logged in as ${currentUser.name}",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = WorkforceTextPrimary
                                )
                            )
                            Text(
                                text = "Tap to switch role (Assessor, Hygienist, Mine Manager, Admin)",
                                style = MaterialTheme.typography.bodySmall.copy(color = WorkforceTextSecondary)
                            )
                        }
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = WorkforceOrange,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
    }
}
}

@Composable
fun MetricMiniCard(
    title: String,
    count: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = WorkforceSurface),
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, WorkforceCardBorder)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall.copy(color = WorkforceTextSecondary)
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = count,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Black,
                    color = WorkforceTextPrimary
                )
            )
        }
    }
}

@Composable
fun DashboardGridCard(
    title: String,
    badge: String,
    description: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier,
    testTag: String = "",
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clickable { onClick() }
            .testTag(testTag),
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
                Surface(
                    shape = CircleShape,
                    color = accentColor.copy(alpha = 0.16f),
                    modifier = Modifier.size(38.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icon,
                            contentDescription = title,
                            tint = accentColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = accentColor.copy(alpha = 0.14f)
                ) {
                    Text(
                        text = badge,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = accentColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp
                        ),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = WorkforceTextPrimary,
                    fontSize = 13.sp
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = WorkforceTextSecondary,
                    fontSize = 10.sp,
                    lineHeight = 14.sp
                ),
                maxLines = 2
            )
        }
    }
}

@Composable
fun QuickNavButton(
    title: String,
    badge: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(10.dp),
        color = WorkforceSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, WorkforceCardBorder)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                shape = CircleShape,
                color = color.copy(alpha = 0.16f),
                modifier = Modifier.size(36.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = color,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = WorkforceTextPrimary
                )
            )
            Text(
                text = badge,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = color,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.sp
                )
            )
        }
    }
}

@Composable
fun AssessmentListItem(
    assessment: Assessment,
    onOpen: () -> Unit,
    onViewReport: () -> Unit,
    onExportPdf: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpen() }
            .testTag("assessment_card_${assessment.id}"),
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
                    text = assessment.referenceNumber,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = WorkforceOrange,
                        letterSpacing = 0.5.sp
                    )
                )
                StatusBadge(status = assessment.status)
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = assessment.facilityName,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = WorkforceTextPrimary
                )
            )

            Text(
                text = "${assessment.customerName} • ${assessment.siteName}",
                style = MaterialTheme.typography.bodySmall.copy(color = WorkforceTextSecondary)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Progress & Compliance rating
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Items: ${assessment.answeredCount}/${assessment.totalQuestions} Answered",
                    style = MaterialTheme.typography.bodySmall.copy(color = WorkforceTextSecondary)
                )
                if (assessment.answeredCount > 0) {
                    val scoreColor = if (assessment.complianceScore >= 80) ComplianceGreen else if (assessment.complianceScore >= 60) ComplianceAmber else ComplianceRed
                    Text(
                        text = "Compliance: ${assessment.complianceScore}%",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = scoreColor,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onOpen,
                    colors = ButtonDefaults.buttonColors(containerColor = WorkforceSurfaceVariant),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1.1f)
                ) {
                    Text("Checklist", color = WorkforceTextPrimary, style = MaterialTheme.typography.labelMedium, fontSize = 12.sp)
                }

                Button(
                    onClick = onViewReport,
                    colors = ButtonDefaults.buttonColors(containerColor = WorkforceRed),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1.1f)
                ) {
                    Icon(imageVector = Icons.Default.Description, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Report", style = MaterialTheme.typography.labelMedium, fontSize = 12.sp)
                }

                OutlinedButton(
                    onClick = onExportPdf,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF38BDF8)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF38BDF8)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .weight(0.9f)
                        .testTag("btn_export_pdf_${assessment.id}")
                ) {
                    Icon(imageVector = Icons.Default.PictureAsPdf, contentDescription = "Export PDF", modifier = Modifier.size(14.dp), tint = Color(0xFF38BDF8))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("PDF", style = MaterialTheme.typography.labelMedium, color = WorkforceTextPrimary, fontSize = 12.sp)
                }
            }
        }
    }
}
