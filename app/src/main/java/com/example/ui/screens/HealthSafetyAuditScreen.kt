package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Assessment
import com.example.data.model.AssessmentStatus
import com.example.data.model.ChecklistType
import com.example.data.model.ComplianceResponse
import com.example.service.LocationService
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
import java.util.UUID

/**
 * Standard Health and Safety Criterion Model for Ablution Facility Audits
 */
data class StandardAuditCriterion(
    val id: String,
    val categoryId: Int,
    val categoryTitle: String,
    val criterionNumber: Int,
    val statutoryReference: String,
    val standardQuestion: String,
    val response: ComplianceResponse = ComplianceResponse.NOT_ASSESSED,
    val remarks: String = "",
    val correctiveAction: String = "",
    val responsiblePerson: String = "",
    val targetDate: String = "",
    val photoUri: String? = null,
    val geoLatitude: Double? = null,
    val geoLongitude: Double? = null,
    val geoTimestamp: String? = null
)

@Composable
fun HealthSafetyAuditScreen(
    viewModel: WorkforceViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val syncStatus by viewModel.syncManager.syncState.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val customers by viewModel.customersList.collectAsState()
    val sites by viewModel.sitesList.collectAsState()
    val facilities by viewModel.facilitiesList.collectAsState()

    // Form Header State
    var selectedCustomer by remember { mutableStateOf(customers.firstOrNull()) }
    val filteredSites = remember(selectedCustomer, sites) {
        sites.filter { it.customerId == selectedCustomer?.id }
    }
    var selectedSite by remember(filteredSites) {
        mutableStateOf(filteredSites.firstOrNull() ?: sites.firstOrNull())
    }
    val filteredFacilities = remember(selectedSite, facilities) {
        facilities.filter { it.siteId == selectedSite?.id }
    }
    var selectedFacility by remember(filteredFacilities) {
        mutableStateOf(filteredFacilities.firstOrNull() ?: facilities.firstOrNull())
    }

    var auditDate by remember {
        mutableStateOf(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()))
    }
    var assessorNotes by remember { mutableStateOf("") }
    var selectedCategoryTab by remember { mutableIntStateOf(0) } // 0: All, 1..8: Specific categories

    // Standard Health & Safety Criteria List
    val criteriaList = remember {
        mutableStateListOf<StandardAuditCriterion>().apply {
            addAll(getStandardHealthSafetyCriteria())
        }
    }

    // Photo selection launcher
    var photoTargetCriterionId by remember { mutableStateOf<String?>(null) }
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { pickedUri ->
            photoTargetCriterionId?.let { targetId ->
                val idx = criteriaList.indexOfFirst { it.id == targetId }
                if (idx != -1) {
                    criteriaList[idx] = criteriaList[idx].copy(photoUri = pickedUri.toString())
                    viewModel.showNotification("Photo attached to criterion #${criteriaList[idx].criterionNumber}")
                }
            }
        }
    }

    // Scoring derivations
    val totalCount = criteriaList.size
    val compliantCount = criteriaList.count { it.response == ComplianceResponse.YES }
    val nonCompliantCount = criteriaList.count { it.response == ComplianceResponse.NO }
    val naCount = criteriaList.count { it.response == ComplianceResponse.NOT_APPLICABLE }
    val assessedCount = compliantCount + nonCompliantCount

    val complianceScorePercent by remember {
        derivedStateOf {
            if (assessedCount == 0) 100.0
            else (compliantCount.toDouble() / assessedCount.toDouble()) * 100.0
        }
    }

    val isDmrPass = complianceScorePercent >= 80.0
    val scoreColor = when {
        complianceScorePercent >= 80.0 -> ComplianceGreen
        complianceScorePercent >= 60.0 -> ComplianceAmber
        else -> ComplianceRed
    }

    Scaffold(
        topBar = {
            WorkforceTopBar(
                title = "Health & Safety Criteria Audit",
                subtitle = "Standard Ablution Audit Form (MHSA / DMR)",
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
                .padding(horizontal = 14.dp),
            contentPadding = PaddingValues(top = 10.dp, bottom = 48.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Screen Header & Live Score Gauge
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("card_hs_audit_score_header"),
                    colors = CardDefaults.cardColors(containerColor = WorkforceSurface),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, WorkforceCardBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "STATUTORY AUDIT SCORE",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = WorkforceTextSecondary,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp
                                    )
                                )
                                Text(
                                    text = if (isDmrPass) "MHSA COMPLIANT (>= 80% THRESHOLD)" else "NON-COMPLIANT: REMEDIAL ACTION REQUIRED",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = scoreColor,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = scoreColor.copy(alpha = 0.18f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, scoreColor)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = if (isDmrPass) Icons.Default.CheckCircle else Icons.Default.Warning,
                                        contentDescription = null,
                                        tint = scoreColor,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = String.format(Locale.US, "%.1f%%", complianceScorePercent),
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            color = scoreColor,
                                            fontWeight = FontWeight.Black
                                        )
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        LinearProgressIndicator(
                            progress = { (complianceScorePercent / 100.0).toFloat().coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp)
                                .clip(RoundedCornerShape(5.dp)),
                            color = scoreColor,
                            trackColor = WorkforceDarkBg
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total: $totalCount Criteria", fontSize = 11.sp, color = WorkforceTextMuted)
                            Text("Compliant: $compliantCount", fontSize = 11.sp, color = ComplianceGreen, fontWeight = FontWeight.Bold)
                            Text("Non-Compliant: $nonCompliantCount", fontSize = 11.sp, color = ComplianceRed, fontWeight = FontWeight.Bold)
                            Text("N/A: $naCount", fontSize = 11.sp, color = WorkforceTextSecondary)
                        }
                    }
                }
            }

            // Customer, Site & Facility Selection Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = WorkforceSurface),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, WorkforceCardBorder)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "AUDIT SPECIFICATION & MINE PROPERTY",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = WorkforceOrange,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        // Customer Picker Row
                        Text("Customer Account (Legal Entity):", fontSize = 11.sp, color = WorkforceTextSecondary, fontWeight = FontWeight.SemiBold)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            customers.forEach { cust ->
                                val isSelected = cust.id == selectedCustomer?.id
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSelected) WorkforceOrange else WorkforceSurfaceVariant,
                                    modifier = Modifier
                                        .clickable { selectedCustomer = cust }
                                        .padding(vertical = 4.dp)
                                ) {
                                    Text(
                                        text = cust.name,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color.White else WorkforceTextPrimary
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Site Picker Row
                        Text("Mine Site / Operation Property:", fontSize = 11.sp, color = WorkforceTextSecondary, fontWeight = FontWeight.SemiBold)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            filteredSites.forEach { site ->
                                val isSelected = site.id == selectedSite?.id
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSelected) Color(0xFF38BDF8) else WorkforceSurfaceVariant,
                                    modifier = Modifier
                                        .clickable { selectedSite = site }
                                        .padding(vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "${site.siteName} (#${site.mineCode})",
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color.White else WorkforceTextPrimary
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Facility Picker Row
                        Text("Ablution Facility Under Audit:", fontSize = 11.sp, color = WorkforceTextSecondary, fontWeight = FontWeight.SemiBold)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            filteredFacilities.forEach { fac ->
                                val isSelected = fac.id == selectedFacility?.id
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSelected) Color(0xFFA855F7) else WorkforceSurfaceVariant,
                                    modifier = Modifier
                                        .clickable { selectedFacility = fac }
                                        .padding(vertical = 4.dp)
                                ) {
                                    Text(
                                        text = fac.name,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color.White else WorkforceTextPrimary
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Assessor Details Row
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = currentUser.name,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Lead Assessor") },
                                modifier = Modifier.weight(1.2f),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = WorkforceSurfaceVariant,
                                    unfocusedContainerColor = WorkforceSurfaceVariant,
                                    focusedBorderColor = WorkforceCardBorder,
                                    unfocusedBorderColor = WorkforceCardBorder,
                                    focusedTextColor = WorkforceTextPrimary,
                                    unfocusedTextColor = WorkforceTextPrimary
                                )
                            )
                            OutlinedTextField(
                                value = auditDate,
                                onValueChange = { auditDate = it },
                                label = { Text("Audit Date") },
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = WorkforceSurfaceVariant,
                                    unfocusedContainerColor = WorkforceSurfaceVariant,
                                    focusedBorderColor = WorkforceCardBorder,
                                    unfocusedBorderColor = WorkforceCardBorder,
                                    focusedTextColor = WorkforceTextPrimary,
                                    unfocusedTextColor = WorkforceTextPrimary
                                )
                            )
                        }
                    }
                }
            }

            // Category Filter Pills
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val categories = listOf(
                        "All Criteria ($totalCount)",
                        "1. Ventilation",
                        "2. Lighting",
                        "3. Potable Water",
                        "4. Soap & Drying",
                        "5. Cleanliness",
                        "6. Privacy & Locks",
                        "7. Chemical Safety",
                        "8. Demarcation"
                    )
                    categories.forEachIndexed { idx, title ->
                        val isSel = selectedCategoryTab == idx
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSel) WorkforceRed else WorkforceSurfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) WorkforceRed else WorkforceCardBorder),
                            modifier = Modifier
                                .clickable { selectedCategoryTab = idx }
                                .padding(vertical = 4.dp)
                        ) {
                            Text(
                                text = title,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSel) Color.White else WorkforceTextSecondary
                            )
                        }
                    }
                }
            }

            // Criteria Items with Radio Buttons & Text Fields
            val filteredCriteria = if (selectedCategoryTab == 0) {
                criteriaList
            } else {
                criteriaList.filter { it.categoryId == selectedCategoryTab }
            }

            items(filteredCriteria, key = { it.id }) { criterion ->
                StandardCriterionItemCard(
                    criterion = criterion,
                    onResponseChange = { newResponse ->
                        val idx = criteriaList.indexOfFirst { it.id == criterion.id }
                        if (idx != -1) {
                            criteriaList[idx] = criteriaList[idx].copy(response = newResponse)
                        }
                    },
                    onRemarksChange = { newRemarks ->
                        val idx = criteriaList.indexOfFirst { it.id == criterion.id }
                        if (idx != -1) {
                            criteriaList[idx] = criteriaList[idx].copy(remarks = newRemarks)
                        }
                    },
                    onCorrectiveActionChange = { newAction ->
                        val idx = criteriaList.indexOfFirst { it.id == criterion.id }
                        if (idx != -1) {
                            criteriaList[idx] = criteriaList[idx].copy(correctiveAction = newAction)
                        }
                    },
                    onResponsiblePersonChange = { newResp ->
                        val idx = criteriaList.indexOfFirst { it.id == criterion.id }
                        if (idx != -1) {
                            criteriaList[idx] = criteriaList[idx].copy(responsiblePerson = newResp)
                        }
                    },
                    onTargetDateChange = { newDate ->
                        val idx = criteriaList.indexOfFirst { it.id == criterion.id }
                        if (idx != -1) {
                            criteriaList[idx] = criteriaList[idx].copy(targetDate = newDate)
                        }
                    },
                    onAcquireGps = {
                        val loc = LocationService.getCurrentGpsLocation(context)
                        val idx = criteriaList.indexOfFirst { it.id == criterion.id }
                        if (idx != -1) {
                            val time = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
                            criteriaList[idx] = criteriaList[idx].copy(
                                geoLatitude = loc.latitude,
                                geoLongitude = loc.longitude,
                                geoTimestamp = time
                            )
                            viewModel.showNotification("GPS Recorded: ${String.format(Locale.US, "%.5f, %.5f", loc.latitude, loc.longitude)}")
                        }
                    },
                    onCapturePhoto = {
                        photoTargetCriterionId = criterion.id
                        photoPickerLauncher.launch("image/*")
                    }
                )
            }

            // Bottom Actions Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = WorkforceSurface),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, WorkforceCardBorder)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                val dummyAssessment = Assessment(
                                    id = "hs_audit_${UUID.randomUUID().toString().take(6)}",
                                    referenceNumber = "WFH-HS-${SimpleDateFormat("yyyyMM", Locale.getDefault()).format(Date())}-${(100..999).random()}",
                                    customerId = selectedCustomer?.id ?: "cust_letaba",
                                    customerName = selectedCustomer?.name ?: "Letaba Crushers",
                                    siteId = selectedSite?.id ?: "site_letaba",
                                    siteName = selectedSite?.siteName ?: "Letaba Quarry",
                                    facilityId = selectedFacility?.id ?: "fac_1",
                                    facilityName = selectedFacility?.name ?: "Main Ablution",
                                    checklistType = selectedFacility?.checklistType ?: ChecklistType.MALE_ABLUTION,
                                    assessorId = currentUser.id,
                                    assessorName = currentUser.name,
                                    assessorRole = currentUser.role.displayName,
                                    assessmentDate = auditDate,
                                    assessmentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()),
                                    complianceScore = complianceScorePercent.toInt(),
                                    status = AssessmentStatus.SUBMITTED_FOR_REVIEW
                                )
                                viewModel.savePdfToPublicDownloads(dummyAssessment)
                                viewModel.showNotification("Audit submitted! Score: ${String.format(Locale.US, "%.1f%%", complianceScorePercent)}")
                                onBack()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = WorkforceRed),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("btn_finalize_hs_audit")
                        ) {
                            Icon(imageVector = Icons.Default.AssignmentTurnedIn, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "FINALIZE AUDIT (SCORE: ${String.format(Locale.US, "%.1f%%", complianceScorePercent)})",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    viewModel.showNotification("Health & Safety Audit draft saved locally.")
                                    onBack()
                                },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(46.dp)
                                    .testTag("btn_save_hs_draft")
                            ) {
                                Icon(imageVector = Icons.Default.Save, contentDescription = null, tint = WorkforceTextPrimary, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Save Draft", color = WorkforceTextPrimary, fontSize = 12.sp)
                            }

                            Button(
                                onClick = {
                                    viewModel.syncToWordPressNew()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = WorkforceOrange),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(46.dp)
                                    .testTag("btn_sync_hs_wordpress")
                            ) {
                                Icon(imageVector = Icons.Default.CloudUpload, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Sync Portal", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Standard Criterion Card displaying Radio Buttons and Text Fields
 */
@Composable
fun StandardCriterionItemCard(
    criterion: StandardAuditCriterion,
    onResponseChange: (ComplianceResponse) -> Unit,
    onRemarksChange: (String) -> Unit,
    onCorrectiveActionChange: (String) -> Unit,
    onResponsiblePersonChange: (String) -> Unit,
    onTargetDateChange: (String) -> Unit,
    onAcquireGps: () -> Unit,
    onCapturePhoto: () -> Unit
) {
    var isExpanded by remember {
        mutableStateOf(criterion.response == ComplianceResponse.NO || criterion.remarks.isNotEmpty() || criterion.photoUri != null)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("card_hs_criterion_${criterion.id}"),
        colors = CardDefaults.cardColors(containerColor = WorkforceSurface),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(
            width = if (criterion.response == ComplianceResponse.NO) 1.5.dp else 1.dp,
            color = if (criterion.response == ComplianceResponse.NO) ComplianceRed.copy(alpha = 0.6f) else WorkforceCardBorder
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header: Category & Criterion Number
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = WorkforceOrange.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "CRITERION ${criterion.criterionNumber}",
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = WorkforceOrange,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = criterion.categoryTitle,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = WorkforceTextSecondary,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }

                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = WorkforceSurfaceVariant
                ) {
                    Text(
                        text = criterion.statutoryReference,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = WorkforceTextMuted,
                            fontSize = 9.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Standard Health and Safety Requirement Question
            Text(
                text = criterion.standardQuestion,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = WorkforceTextPrimary,
                    lineHeight = 20.sp
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Radio Button Group UI (YES / NO / N/A)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(WorkforceSurfaceVariant, RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Radio Option: COMPLIANT (YES)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable { onResponseChange(ComplianceResponse.YES) }
                        .padding(end = 4.dp)
                ) {
                    RadioButton(
                        selected = criterion.response == ComplianceResponse.YES,
                        onClick = { onResponseChange(ComplianceResponse.YES) },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = ComplianceGreen,
                            unselectedColor = WorkforceTextSecondary
                        ),
                        modifier = Modifier.testTag("radio_yes_${criterion.id}")
                    )
                    Text(
                        text = "YES (Pass)",
                        fontSize = 11.sp,
                        fontWeight = if (criterion.response == ComplianceResponse.YES) FontWeight.Bold else FontWeight.Normal,
                        color = if (criterion.response == ComplianceResponse.YES) ComplianceGreen else WorkforceTextSecondary
                    )
                }

                // Radio Option: NON-COMPLIANT (NO)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable {
                            onResponseChange(ComplianceResponse.NO)
                            isExpanded = true
                        }
                        .padding(horizontal = 4.dp)
                ) {
                    RadioButton(
                        selected = criterion.response == ComplianceResponse.NO,
                        onClick = {
                            onResponseChange(ComplianceResponse.NO)
                            isExpanded = true
                        },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = ComplianceRed,
                            unselectedColor = WorkforceTextSecondary
                        ),
                        modifier = Modifier.testTag("radio_no_${criterion.id}")
                    )
                    Text(
                        text = "NO (Fail)",
                        fontSize = 11.sp,
                        fontWeight = if (criterion.response == ComplianceResponse.NO) FontWeight.Bold else FontWeight.Normal,
                        color = if (criterion.response == ComplianceResponse.NO) ComplianceRed else WorkforceTextSecondary
                    )
                }

                // Radio Option: NOT APPLICABLE (N/A)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable { onResponseChange(ComplianceResponse.NOT_APPLICABLE) }
                        .padding(start = 4.dp)
                ) {
                    RadioButton(
                        selected = criterion.response == ComplianceResponse.NOT_APPLICABLE,
                        onClick = { onResponseChange(ComplianceResponse.NOT_APPLICABLE) },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = WorkforceOrange,
                            unselectedColor = WorkforceTextSecondary
                        ),
                        modifier = Modifier.testTag("radio_na_${criterion.id}")
                    )
                    Text(
                        text = "N/A",
                        fontSize = 11.sp,
                        fontWeight = if (criterion.response == ComplianceResponse.NOT_APPLICABLE) FontWeight.Bold else FontWeight.Normal,
                        color = if (criterion.response == ComplianceResponse.NOT_APPLICABLE) WorkforceOrange else WorkforceTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Expand/Collapse Details Row (Text fields & Evidence)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = WorkforceOrange,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isExpanded) "Hide remarks & actions" else "Add remarks, actions & photos",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = WorkforceOrange,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.sp
                        )
                    )
                }

                if (criterion.geoLatitude != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, tint = ComplianceGreen, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text("GPS Tagged", fontSize = 10.sp, color = ComplianceGreen, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Expanded Form Text Fields
            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Text Field: Observations / Remarks
                    OutlinedTextField(
                        value = criterion.remarks,
                        onValueChange = onRemarksChange,
                        label = { Text("Observation / Finding Remark") },
                        placeholder = { Text("Describe observed condition, defect or compliance level...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_remarks_${criterion.id}"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = WorkforceSurfaceVariant,
                            unfocusedContainerColor = WorkforceSurfaceVariant,
                            focusedBorderColor = WorkforceOrange,
                            unfocusedBorderColor = WorkforceCardBorder,
                            focusedTextColor = WorkforceTextPrimary,
                            unfocusedTextColor = WorkforceTextPrimary,
                            focusedLabelColor = WorkforceOrange,
                            unfocusedLabelColor = WorkforceTextSecondary
                        ),
                        maxLines = 3
                    )

                    // Text Field: Corrective Action
                    OutlinedTextField(
                        value = criterion.correctiveAction,
                        onValueChange = onCorrectiveActionChange,
                        label = { Text("Required Remedial / Corrective Action") },
                        placeholder = { Text("Repair hinge, replace light fitting, descale urinals...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_action_${criterion.id}"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = WorkforceSurfaceVariant,
                            unfocusedContainerColor = WorkforceSurfaceVariant,
                            focusedBorderColor = WorkforceOrange,
                            unfocusedBorderColor = WorkforceCardBorder,
                            focusedTextColor = WorkforceTextPrimary,
                            unfocusedTextColor = WorkforceTextPrimary,
                            focusedLabelColor = WorkforceOrange,
                            unfocusedLabelColor = WorkforceTextSecondary
                        ),
                        maxLines = 2
                    )

                    // Text Fields Row: Responsible Person & Target Date
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = criterion.responsiblePerson,
                            onValueChange = onResponsiblePersonChange,
                            label = { Text("Responsible Person") },
                            placeholder = { Text("Engineering / Cleaning Sup") },
                            modifier = Modifier
                                .weight(1.2f)
                                .testTag("input_resp_${criterion.id}"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = WorkforceSurfaceVariant,
                                unfocusedContainerColor = WorkforceSurfaceVariant,
                                focusedBorderColor = WorkforceOrange,
                                unfocusedBorderColor = WorkforceCardBorder,
                                focusedTextColor = WorkforceTextPrimary,
                                unfocusedTextColor = WorkforceTextPrimary,
                                focusedLabelColor = WorkforceOrange,
                                unfocusedLabelColor = WorkforceTextSecondary
                            )
                        )

                        OutlinedTextField(
                            value = criterion.targetDate,
                            onValueChange = onTargetDateChange,
                            label = { Text("Due Date") },
                            placeholder = { Text("2026-11-15") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("input_date_${criterion.id}"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = WorkforceSurfaceVariant,
                                unfocusedContainerColor = WorkforceSurfaceVariant,
                                focusedBorderColor = WorkforceOrange,
                                unfocusedBorderColor = WorkforceCardBorder,
                                focusedTextColor = WorkforceTextPrimary,
                                unfocusedTextColor = WorkforceTextPrimary,
                                focusedLabelColor = WorkforceOrange,
                                unfocusedLabelColor = WorkforceTextSecondary
                            )
                        )
                    }

                    // Action buttons: GPS acquire + Photo capture
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = onAcquireGps,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = ComplianceGreen),
                            border = androidx.compose.foundation.BorderStroke(1.dp, ComplianceGreen.copy(alpha = 0.6f))
                        ) {
                            Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                if (criterion.geoLatitude != null) "GPS: ${String.format(Locale.US, "%.3f, %.3f", criterion.geoLatitude, criterion.geoLongitude)}"
                                else "Tag GPS Location",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Button(
                            onClick = onCapturePhoto,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (criterion.photoUri != null) ComplianceGreen else WorkforceSurfaceVariant
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = if (criterion.photoUri != null) Color.White else WorkforceTextPrimary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                if (criterion.photoUri != null) "Photo Attached" else "Attach Photo",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (criterion.photoUri != null) Color.White else WorkforceTextPrimary
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Standard Criteria Catalog according to South African Mine Health and Safety Act (MHSA)
 * and SANS 10400 Ablution Standards.
 */
fun getStandardHealthSafetyCriteria(): List<StandardAuditCriterion> {
    return listOf(
        StandardAuditCriterion(
            id = "crit_vent_01",
            categoryId = 1,
            categoryTitle = "Ventilation & Airflow Quality",
            criterionNumber = 1,
            statutoryReference = "MHSA Reg 11.2 / SANS 10400-O",
            standardQuestion = "Is mechanical/natural ventilation adequate to prevent moisture accumulation, foul odours, and condensation build-up?"
        ),
        StandardAuditCriterion(
            id = "crit_vent_02",
            categoryId = 1,
            categoryTitle = "Ventilation & Airflow Quality",
            criterionNumber = 2,
            statutoryReference = "MHSA Reg 11.4",
            standardQuestion = "Are mechanical exhaust fans fully operational, free from dust obstruction, and discharging air directly outside the facility?"
        ),
        StandardAuditCriterion(
            id = "crit_light_01",
            categoryId = 2,
            categoryTitle = "Lighting & Electrical Safety",
            criterionNumber = 3,
            statutoryReference = "MHSA Reg 11.3 / 100 Lux Floor",
            standardQuestion = "Is artificial illumination sufficient and uniform across all cubicles and basins (minimum 100 Lux at floor level)?"
        ),
        StandardAuditCriterion(
            id = "crit_light_02",
            categoryId = 2,
            categoryTitle = "Lighting & Electrical Safety",
            criterionNumber = 4,
            statutoryReference = "SANS 10142-1 Vapor Proof",
            standardQuestion = "Are all luminaires fitted with vapor-proof, moisture-resistant enclosures with no exposed live electrical wiring?"
        ),
        StandardAuditCriterion(
            id = "crit_water_01",
            categoryId = 3,
            categoryTitle = "Potable Water & Basins",
            criterionNumber = 5,
            statutoryReference = "SANS 241 Potable Drinking Standard",
            standardQuestion = "Is continuous pressurized potable water available to all washbasins, showers, and toilet flushing units?"
        ),
        StandardAuditCriterion(
            id = "crit_water_02",
            categoryId = 3,
            categoryTitle = "Potable Water & Basins",
            criterionNumber = 6,
            statutoryReference = "MHSA Schedule / 1:15 Ratio",
            standardQuestion = "Are washbasin numbers compliant with the shift workforce ratio (minimum 1 basin per 15 employees on shift)?"
        ),
        StandardAuditCriterion(
            id = "crit_soap_01",
            categoryId = 4,
            categoryTitle = "Soap & Hand Hygiene Dispensers",
            criterionNumber = 7,
            statutoryReference = "MHSA Hygiene Guideline 2026",
            standardQuestion = "Are wall-mounted liquid antibacterial soap dispensers installed, intact, and filled at all hand-washing stations?"
        ),
        StandardAuditCriterion(
            id = "crit_soap_02",
            categoryId = 4,
            categoryTitle = "Soap & Hand Hygiene Dispensers",
            criterionNumber = 8,
            statutoryReference = "SANS 10400 Hygiene",
            standardQuestion = "Are hygienic hand-drying facilities provided (warm air dryers or clean disposable paper towels with a waste bin)?"
        ),
        StandardAuditCriterion(
            id = "crit_clean_01",
            categoryId = 5,
            categoryTitle = "Cleanliness & Disinfection",
            criterionNumber = 9,
            statutoryReference = "MHSA Reg 11.1 Cleanliness",
            standardQuestion = "Are floors, walls, pans, and urinals thoroughly clean, descaled, free of algae/mildew, and sanitised twice daily?"
        ),
        StandardAuditCriterion(
            id = "crit_clean_02",
            categoryId = 5,
            categoryTitle = "Cleanliness & Disinfection",
            criterionNumber = 10,
            statutoryReference = "Health & Safety Hygiene Log",
            standardQuestion = "Is a certified daily cleaning and inspection register displayed, signed off by the facility supervisor each shift?"
        ),
        StandardAuditCriterion(
            id = "crit_clean_03",
            categoryId = 5,
            categoryTitle = "Cleanliness & Disinfection",
            criterionNumber = 11,
            statutoryReference = "MHSA Women in Mining Code",
            standardQuestion = "Are hands-free pedal-operated sanitary disposal bins with fitted liner bags provided in all female cubicles?"
        ),
        StandardAuditCriterion(
            id = "crit_priv_01",
            categoryId = 6,
            categoryTitle = "Privacy, Partitioning & Locks",
            criterionNumber = 12,
            statutoryReference = "SANS 10400 Cubicle Privacy",
            standardQuestion = "Do all toilet cubicles have full privacy partitions and self-closing doors with working internal indicator locks?"
        ),
        StandardAuditCriterion(
            id = "crit_priv_02",
            categoryId = 6,
            categoryTitle = "Privacy, Partitioning & Locks",
            criterionNumber = 13,
            statutoryReference = "MHSA Slip Hazard Prevention",
            standardQuestion = "Are floor surfaces non-slip, waterproof, and sloped towards floor drains to prevent water ponding?"
        ),
        StandardAuditCriterion(
            id = "crit_chem_01",
            categoryId = 7,
            categoryTitle = "Chemical Storage & Hazardous Agents",
            criterionNumber = 14,
            statutoryReference = "Hazardous Chemical Substances Reg 10",
            standardQuestion = "Are all ablution cleaning chemicals stored in a dedicated locked store room away from unauthorized personnel?"
        ),
        StandardAuditCriterion(
            id = "crit_chem_02",
            categoryId = 7,
            categoryTitle = "Chemical Storage & Hazardous Agents",
            criterionNumber = 15,
            statutoryReference = "GHS / 16-point SDS Standards",
            standardQuestion = "Are Safety Data Sheets (SDS) displayed in the chemical store and are cleaning staff provided with rubber PPE gloves & goggles?"
        ),
        StandardAuditCriterion(
            id = "crit_demarc_01",
            categoryId = 8,
            categoryTitle = "Statutory Demarcation & Notices",
            criterionNumber = 16,
            statutoryReference = "MHSA Symbolic Safety Signage",
            standardQuestion = "Are male and female facilities clearly demarcated with symbolic pictograms, and are hand-washing instruction notices posted?"
        )
    )
}
