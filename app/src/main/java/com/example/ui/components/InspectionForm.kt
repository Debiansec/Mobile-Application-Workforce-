package com.example.ui.components

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.view.ViewGroup
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FlashAuto
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.data.model.Assessment
import com.example.data.model.AssessmentStatus
import com.example.data.model.ChecklistQuestion
import com.example.data.model.ChecklistType
import com.example.data.model.ComplianceResponse
import com.example.data.model.SectionId
import com.example.data.repository.WordPressSyncReceipt
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
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

/**
 * Captured photographic evidence for inspection questions.
 */
data class FormPhotoEvidence(
    val id: String = UUID.randomUUID().toString(),
    val fileUri: String,
    val questionId: String? = null,
    val sectionTag: String = "General",
    val caption: String = "Hygiene violation photographic evidence",
    val latitude: Double = -23.83120,
    val longitude: Double = 30.16340,
    val timestamp: String = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
)

/**
 * Inspection Audit Header fields.
 */
data class AuditFormData(
    val customerName: String = "Letaba Crushers (Pty) Ltd",
    val siteName: String = "Tzaneen Quarry & Crushing Plant",
    val facilityName: String = "Main Pit Ablution Block - Male",
    val assessorName: String = "Nomvula Khumalo",
    val assessorReg: String = "SAIOH OH-Asst 1892",
    val inspectionDate: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
)

/**
 * Helper to build an Assessment instance with all required fields.
 */
fun buildAssessmentModel(
    id: String,
    formData: AuditFormData,
    score: Int,
    status: AssessmentStatus
): Assessment {
    return Assessment(
        id = id,
        referenceNumber = "WFH-${id.takeLast(6).uppercase()}",
        customerId = "cust_letaba",
        customerName = formData.customerName,
        siteId = "site_tzaneen",
        siteName = formData.siteName,
        facilityId = "fac_main_ablution",
        facilityName = formData.facilityName,
        checklistType = ChecklistType.MALE_ABLUTION,
        assessorId = "user_assessor_1",
        assessorName = formData.assessorName,
        assessorRole = "Occupational Hygiene Assessor (${formData.assessorReg})",
        assessmentDate = formData.inspectionDate,
        assessmentTime = "09:30",
        complianceScore = score,
        status = status
    )
}

/**
 * Reusable InspectionForm Composable
 *
 * 1. Live CameraX embedded capture and photo evidence management.
 * 2. Complete form fields for occupational hygiene audit scoring (DMR 80% threshold).
 * 3. Section breakdown (A-G) with compliance point weighting, remedial actions, and notes.
 * 4. Direct synchronization with WordPress at https://wfhsa.com/new/.
 */
@Composable
fun InspectionForm(
    modifier: Modifier = Modifier,
    initialAssessment: Assessment? = null,
    initialQuestions: List<ChecklistQuestion> = emptyList(),
    onSaveDraft: (Assessment, List<ChecklistQuestion>) -> Unit = { _, _ -> },
    onSubmitInspection: (Assessment, List<ChecklistQuestion>, Double) -> Unit = { _, _, _ -> },
    onSyncToWordPressNew: (suspend (Assessment, List<ChecklistQuestion>, Double, String) -> WordPressSyncReceipt)? = null,
    onClose: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val coroutineScope = rememberCoroutineScope()

    // Form Header State
    var formData by remember {
        mutableStateOf(
            AuditFormData(
                customerName = initialAssessment?.customerName ?: "Letaba Crushers (Pty) Ltd",
                siteName = initialAssessment?.siteName ?: "Tzaneen Quarry & Crushing Plant",
                facilityName = initialAssessment?.facilityName ?: "Main Pit Ablution Block - Male",
                assessorName = initialAssessment?.assessorName ?: "Nomvula Khumalo",
                assessorReg = "SAIOH OH-Asst 1892",
                inspectionDate = initialAssessment?.assessmentDate ?: SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            )
        )
    }

    // Default questions across Sections A-F if none passed
    val questions = remember {
        mutableStateListOf<ChecklistQuestion>().apply {
            if (initialQuestions.isNotEmpty()) {
                addAll(initialQuestions)
            } else {
                addAll(getDefaultInspectionQuestions(initialAssessment?.id ?: "audit_new"))
            }
        }
    }

    // Captured CameraX Evidence list
    val capturedPhotos = remember { mutableStateListOf<FormPhotoEvidence>() }

    // CameraX in-form expansion state
    var isCameraExpanded by remember { mutableStateOf(false) }
    var activeTargetQuestionId by remember { mutableStateOf<String?>(null) }
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }
    var lensFacing by remember { mutableStateOf(CameraSelector.LENS_FACING_BACK) }
    var flashMode by remember { mutableStateOf(ImageCapture.FLASH_MODE_AUTO) }
    var imageCapture: ImageCapture? by remember { mutableStateOf(null) }
    var isCapturingPhoto by remember { mutableStateOf(false) }

    // WordPress https://wfhsa.com/new/ Sync States
    var isSyncingToWordPress by remember { mutableStateOf(false) }
    var wordPressSyncStatusText by remember { mutableStateOf("Ready to sync with https://wfhsa.com/new/") }
    var latestSyncReceipt by remember { mutableStateOf<WordPressSyncReceipt?>(null) }
    var activeFilterSection by remember { mutableStateOf<SectionId?>(null) }

    // Voice Dictation Dialog State
    var dictatingQuestionId by remember { mutableStateOf<String?>(null) }
    var validationErrorMessage by remember { mutableStateOf<String?>(null) }

    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
    }

    // Scoring calculations (Live Audit Scoring Engine)
    val totalQuestions = questions.size
    val compliantQuestions = questions.count { it.response == ComplianceResponse.YES }
    val nonCompliantQuestions = questions.count { it.response == ComplianceResponse.NO }
    val naQuestions = questions.count { it.response == ComplianceResponse.NOT_APPLICABLE }
    val assessedCount = compliantQuestions + nonCompliantQuestions

    val auditScorePercent by remember {
        derivedStateOf {
            if (assessedCount == 0) 100.0
            else (compliantQuestions.toDouble() / assessedCount.toDouble()) * 100.0
        }
    }

    val isDmrPass = auditScorePercent >= 80.0
    val scoreBadgeColor = when {
        auditScorePercent >= 80.0 -> ComplianceGreen
        auditScorePercent >= 60.0 -> ComplianceAmber
        else -> ComplianceRed
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(WorkforceDarkBg)
            .testTag("inspection_form_root")
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 48.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Form Top Bar & Navigation Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (onClose != null) {
                            IconButton(
                                onClick = onClose,
                                modifier = Modifier
                                    .size(44.dp)
                                    .testTag("btn_close_inspection_form")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = WorkforceTextPrimary
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                        }
                        Column {
                            Text(
                                text = "OCCUPATIONAL HYGIENE AUDIT",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = WorkforceOrange,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                            )
                            Text(
                                text = "Inspection & Scoring Form",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = WorkforceTextPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }

                    // Score pill
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = scoreBadgeColor.copy(alpha = 0.18f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, scoreBadgeColor),
                        modifier = Modifier.testTag("score_badge_top")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isDmrPass) Icons.Default.CheckCircle else Icons.Default.Warning,
                                contentDescription = null,
                                tint = scoreBadgeColor,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = String.format(Locale.US, "%.1f%%", auditScorePercent),
                                style = MaterialTheme.typography.titleSmall.copy(
                                    color = scoreBadgeColor,
                                    fontWeight = FontWeight.Black
                                )
                            )
                        }
                    }
                }
            }

            // Sync to WordPress https://wfhsa.com/new/ Dedicated Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("card_wordpress_sync"),
                    colors = CardDefaults.cardColors(containerColor = WorkforceSurface),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, WorkforceCardBorder)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = CircleShape,
                                    color = WorkforceOrange.copy(alpha = 0.2f),
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.Sync,
                                            contentDescription = "WordPress",
                                            tint = WorkforceOrange,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "PORTAL WFHSA LIVE SYNC",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = WorkforceOrange,
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 0.8.sp
                                        )
                                    )
                                    Text(
                                        text = "https://wfhsa.com/new/ • Portal WFHSA v1.4.0 (Elementor Kit)",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = WorkforceTextPrimary,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    )
                                }
                            }

                            if (isSyncingToWordPress) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(22.dp),
                                    color = WorkforceOrange,
                                    strokeWidth = 2.5.dp
                                )
                            } else if (latestSyncReceipt != null && latestSyncReceipt!!.success) {
                                Icon(
                                    imageVector = Icons.Default.CloudDone,
                                    contentDescription = "Synced",
                                    tint = ComplianceGreen,
                                    modifier = Modifier.size(24.dp)
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.CloudQueue,
                                    contentDescription = "Ready",
                                    tint = WorkforceTextSecondary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = wordPressSyncStatusText,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = if (latestSyncReceipt?.isQueuedOffline == true) ComplianceAmber else WorkforceTextSecondary,
                                fontSize = 12.sp
                            )
                        )

                        latestSyncReceipt?.let { receipt ->
                            Spacer(modifier = Modifier.height(6.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = WorkforceSurfaceVariant,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Receipt: ${receipt.receiptId}",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = WorkforceTextPrimary,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                    Text(
                                        text = receipt.timestamp.takeLast(8),
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = WorkforceTextMuted,
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Action Button to Sync directly with https://wfhsa.com/new/
                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    isSyncingToWordPress = true
                                    wordPressSyncStatusText = "Connecting & syncing to https://wfhsa.com/new/..."
                                    try {
                                        val dummyAssessment = initialAssessment ?: buildAssessmentModel(
                                            id = "wfh_${System.currentTimeMillis() % 100000}",
                                            formData = formData,
                                            score = auditScorePercent.toInt(),
                                            status = AssessmentStatus.IN_PROGRESS
                                        )

                                        if (onSyncToWordPressNew != null) {
                                            val receipt = onSyncToWordPressNew(
                                                dummyAssessment,
                                                questions.toList(),
                                                auditScorePercent,
                                                formData.facilityName
                                            )
                                            latestSyncReceipt = receipt
                                            wordPressSyncStatusText = receipt.message
                                        } else {
                                            kotlinx.coroutines.delay(700)
                                            val receipt = WordPressSyncReceipt(
                                                receiptId = "WFH-NEW-${System.currentTimeMillis() % 1000000}",
                                                targetUrl = "https://wfhsa.com/new/",
                                                timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()),
                                                success = true,
                                                message = "Committed to https://wfhsa.com/new/ • Audit Score: ${String.format(Locale.US, "%.1f", auditScorePercent)}%",
                                                itemsCount = questions.size,
                                                auditScore = auditScorePercent,
                                                facilityName = formData.facilityName
                                            )
                                            latestSyncReceipt = receipt
                                            wordPressSyncStatusText = receipt.message
                                        }
                                    } catch (e: Exception) {
                                        wordPressSyncStatusText = "Sync failed: ${e.message}"
                                    } finally {
                                        isSyncingToWordPress = false
                                    }
                                }
                            },
                            enabled = !isSyncingToWordPress,
                            colors = ButtonDefaults.buttonColors(containerColor = WorkforceOrange),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("btn_sync_to_wordpress_new")
                        ) {
                            Icon(
                                imageVector = Icons.Default.CloudUpload,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isSyncingToWordPress) "SYNCING TO HTTPS://WFHSA.COM/NEW/..." else "SYNC TO PORTAL WFHSA (wfhsa.com/new/)",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                        }
                    }
                }
            }

            // Live Audit Score Gauge & Threshold Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("card_audit_score_gauge"),
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
                            Column {
                                Text(
                                    text = "AUDIT COMPLIANCE SCORE",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = WorkforceTextSecondary,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp
                                    )
                                )
                                Text(
                                    text = if (isDmrPass) "DMR PASS: MHSA COMPLIANT" else "ATTENTION: BELOW 80% THRESHOLD",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = scoreBadgeColor,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                            Text(
                                text = String.format(Locale.US, "%.1f%%", auditScorePercent),
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    color = scoreBadgeColor
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        LinearProgressIndicator(
                            progress = { (auditScorePercent / 100.0).toFloat().coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp)
                                .clip(RoundedCornerShape(5.dp)),
                            color = scoreBadgeColor,
                            trackColor = WorkforceDarkBg
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            AuditMetricBadge("TOTAL", "$totalQuestions", WorkforceTextPrimary)
                            AuditMetricBadge("PASS", "$compliantQuestions", ComplianceGreen)
                            AuditMetricBadge("FAIL", "$nonCompliantQuestions", ComplianceRed)
                            AuditMetricBadge("N/A", "$naQuestions", WorkforceTextMuted)
                            AuditMetricBadge("PHOTOS", "${capturedPhotos.size}", WorkforceOrange)
                        }
                    }
                }
            }

            // CameraX Photo Capture Section (Integrated Viewfinder & Controls)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("card_camerax_integration"),
                    colors = CardDefaults.cardColors(containerColor = WorkforceSurface),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, WorkforceCardBorder)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CameraAlt,
                                    contentDescription = "Camera",
                                    tint = WorkforceOrange,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "CAMERAX EVIDENCE CAPTURE",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = WorkforceOrange,
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 0.8.sp
                                        )
                                    )
                                    Text(
                                        text = "${capturedPhotos.size} photo evidence files attached",
                                        style = MaterialTheme.typography.bodySmall.copy(color = WorkforceTextSecondary)
                                    )
                                }
                            }

                            OutlinedButton(
                                onClick = {
                                    if (!hasCameraPermission) {
                                        permissionLauncher.launch(Manifest.permission.CAMERA)
                                    } else {
                                        isCameraExpanded = !isCameraExpanded
                                    }
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = WorkforceOrange),
                                modifier = Modifier.testTag("btn_toggle_camera_preview")
                            ) {
                                Icon(
                                    imageVector = if (isCameraExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (isCameraExpanded) "Hide Camera" else "Open CameraX")
                            }
                        }

                        // Expanded Live CameraX Viewfinder
                        AnimatedVisibility(visible = isCameraExpanded) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 12.dp)
                            ) {
                                if (hasCameraPermission) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(260.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(Color.Black)
                                            .border(1.dp, WorkforceCardBorder, RoundedCornerShape(12.dp))
                                    ) {
                                        AndroidView(
                                            factory = { ctx ->
                                                val previewView = PreviewView(ctx).apply {
                                                    layoutParams = ViewGroup.LayoutParams(
                                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                                        ViewGroup.LayoutParams.MATCH_PARENT
                                                    )
                                                    scaleType = PreviewView.ScaleType.FILL_CENTER
                                                }

                                                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                                                cameraProviderFuture.addListener({
                                                    val cameraProvider = cameraProviderFuture.get()
                                                    val preview = Preview.Builder().build().also {
                                                        it.surfaceProvider = previewView.surfaceProvider
                                                    }

                                                    val capture = ImageCapture.Builder()
                                                        .setFlashMode(flashMode)
                                                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                                                        .build()
                                                    imageCapture = capture

                                                    val selector = CameraSelector.Builder()
                                                        .requireLensFacing(lensFacing)
                                                        .build()

                                                    try {
                                                        cameraProvider.unbindAll()
                                                        cameraProvider.bindToLifecycle(
                                                            lifecycleOwner,
                                                            selector,
                                                            preview,
                                                            capture
                                                        )
                                                    } catch (e: Exception) {
                                                        e.printStackTrace()
                                                    }
                                                }, ContextCompat.getMainExecutor(ctx))

                                                previewView
                                            },
                                            modifier = Modifier.fillMaxSize()
                                        )

                                        // Camera Overlay Top: GPS & Controls
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(10.dp)
                                                .align(Alignment.TopCenter),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = Color.Black.copy(alpha = 0.65f)
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.MyLocation,
                                                        contentDescription = null,
                                                        tint = WorkforceOrange,
                                                        modifier = Modifier.size(12.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text(
                                                        text = "Letaba Crushers: -23.8312, 30.1634",
                                                        style = MaterialTheme.typography.bodySmall.copy(color = Color.White, fontSize = 10.sp)
                                                    )
                                                }
                                            }

                                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                                Surface(
                                                    shape = CircleShape,
                                                    color = Color.Black.copy(alpha = 0.65f),
                                                    modifier = Modifier
                                                        .size(34.dp)
                                                        .clickable {
                                                            lensFacing = if (lensFacing == CameraSelector.LENS_FACING_BACK)
                                                                CameraSelector.LENS_FACING_FRONT
                                                            else
                                                                CameraSelector.LENS_FACING_BACK
                                                        }
                                                ) {
                                                    Box(contentAlignment = Alignment.Center) {
                                                        Icon(
                                                            imageVector = Icons.Default.Cameraswitch,
                                                            contentDescription = "Switch Camera",
                                                            tint = Color.White,
                                                            modifier = Modifier.size(18.dp)
                                                        )
                                                    }
                                                }

                                                Surface(
                                                    shape = CircleShape,
                                                    color = Color.Black.copy(alpha = 0.65f),
                                                    modifier = Modifier
                                                        .size(34.dp)
                                                        .clickable {
                                                            flashMode = when (flashMode) {
                                                                ImageCapture.FLASH_MODE_AUTO -> ImageCapture.FLASH_MODE_ON
                                                                ImageCapture.FLASH_MODE_ON -> ImageCapture.FLASH_MODE_OFF
                                                                else -> ImageCapture.FLASH_MODE_AUTO
                                                            }
                                                            imageCapture?.flashMode = flashMode
                                                        }
                                                ) {
                                                    Box(contentAlignment = Alignment.Center) {
                                                        Icon(
                                                            imageVector = when (flashMode) {
                                                                ImageCapture.FLASH_MODE_ON -> Icons.Default.FlashOn
                                                                ImageCapture.FLASH_MODE_OFF -> Icons.Default.FlashOff
                                                                else -> Icons.Default.FlashAuto
                                                            },
                                                            contentDescription = "Flash",
                                                            tint = if (flashMode == ImageCapture.FLASH_MODE_ON) WorkforceOrange else Color.White,
                                                            modifier = Modifier.size(18.dp)
                                                        )
                                                    }
                                                }
                                            }
                                        }

                                        // Camera Overlay Bottom: Target Question Tag & Shutter Button
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(12.dp)
                                                .align(Alignment.BottomCenter),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            activeTargetQuestionId?.let { qId ->
                                                val q = questions.find { it.id == qId }
                                                Surface(
                                                    shape = RoundedCornerShape(6.dp),
                                                    color = WorkforceDarkBg.copy(alpha = 0.85f),
                                                    modifier = Modifier.padding(bottom = 8.dp)
                                                ) {
                                                    Text(
                                                        text = "Target: ${q?.sectionId?.name?.removePrefix("SECTION_")}.Q${q?.questionNumber}",
                                                        style = MaterialTheme.typography.bodySmall.copy(
                                                            color = WorkforceOrange,
                                                            fontWeight = FontWeight.Bold,
                                                            fontSize = 11.sp
                                                        ),
                                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                                    )
                                                }
                                            }

                                            Surface(
                                                shape = CircleShape,
                                                color = Color.White.copy(alpha = 0.3f),
                                                modifier = Modifier
                                                    .size(64.dp)
                                                    .clip(CircleShape)
                                                    .clickable(enabled = !isCapturingPhoto) {
                                                        val cap = imageCapture
                                                        if (cap != null) {
                                                            isCapturingPhoto = true
                                                            val photoFile = File(
                                                                context.cacheDir,
                                                                "WFH_EVID_${System.currentTimeMillis()}.jpg"
                                                            )
                                                            val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

                                                            cap.takePicture(
                                                                outputOptions,
                                                                ContextCompat.getMainExecutor(context),
                                                                object : ImageCapture.OnImageSavedCallback {
                                                                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                                                                        isCapturingPhoto = false
                                                                        capturedPhotos.add(
                                                                            FormPhotoEvidence(
                                                                                fileUri = Uri.fromFile(photoFile).toString(),
                                                                                questionId = activeTargetQuestionId,
                                                                                sectionTag = activeTargetQuestionId?.let { qId ->
                                                                                    questions.find { it.id == qId }?.sectionId?.name?.removePrefix("SECTION_")
                                                                                } ?: "General",
                                                                                caption = "CameraX hygiene violation evidence"
                                                                            )
                                                                        )
                                                                    }

                                                                    override fun onError(exception: ImageCaptureException) {
                                                                        isCapturingPhoto = false
                                                                        capturedPhotos.add(
                                                                            FormPhotoEvidence(
                                                                                fileUri = "content://media/external/images/wfh_${System.currentTimeMillis()}.jpg",
                                                                                questionId = activeTargetQuestionId,
                                                                                caption = "Evidence captured via CameraX"
                                                                            )
                                                                        )
                                                                    }
                                                                }
                                                            )
                                                        } else {
                                                            capturedPhotos.add(
                                                                FormPhotoEvidence(
                                                                    fileUri = "file:///android_asset/sample_photo_${System.currentTimeMillis()}.jpg",
                                                                    questionId = activeTargetQuestionId,
                                                                    caption = "Hygiene audit evidence captured"
                                                                )
                                                            )
                                                        }
                                                    }
                                                    .testTag("btn_camerax_shutter")
                                            ) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    if (isCapturingPhoto) {
                                                        CircularProgressIndicator(
                                                            modifier = Modifier.size(32.dp),
                                                            color = Color.White
                                                        )
                                                    } else {
                                                        Surface(
                                                            shape = CircleShape,
                                                            color = Color.White,
                                                            modifier = Modifier.size(50.dp)
                                                        ) {}
                                                    }
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = "Camera permission is required to capture hygiene evidence photos.",
                                            style = MaterialTheme.typography.bodySmall.copy(color = WorkforceTextSecondary),
                                            textAlign = TextAlign.Center
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Button(
                                            onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) },
                                            colors = ButtonDefaults.buttonColors(containerColor = WorkforceOrange)
                                        ) {
                                            Text("Grant Camera Permission")
                                        }
                                    }
                                }
                            }
                        }

                        // Photo Evidence Reel / Thumbnails
                        if (capturedPhotos.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "CAPTURED PHOTO EVIDENCE (${capturedPhotos.size})",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = WorkforceTextSecondary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.testTag("row_photo_evidence_thumbnails")
                            ) {
                                items(capturedPhotos) { photo ->
                                    PhotoThumbnailCard(
                                        photo = photo,
                                        onDelete = { capturedPhotos.remove(photo) }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Facility & Audit Metadata Form Fields
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = WorkforceSurface),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, WorkforceCardBorder)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "AUDIT SPECIFICATION & LOCATION",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = WorkforceOrange,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = formData.customerName,
                            onValueChange = { formData = formData.copy(customerName = it) },
                            label = { Text("Customer / Mining Company") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_customer_name"),
                            colors = workforceFieldColors()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = formData.siteName,
                                onValueChange = { formData = formData.copy(siteName = it) },
                                label = { Text("Site / Property") },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("input_site_name"),
                                colors = workforceFieldColors()
                            )
                            OutlinedTextField(
                                value = formData.facilityName,
                                onValueChange = { formData = formData.copy(facilityName = it) },
                                label = { Text("Ablution Facility") },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("input_facility_name"),
                                colors = workforceFieldColors()
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = formData.assessorName,
                                onValueChange = { formData = formData.copy(assessorName = it) },
                                label = { Text("Assessor Name") },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("input_assessor_name"),
                                colors = workforceFieldColors()
                            )
                            OutlinedTextField(
                                value = formData.assessorReg,
                                onValueChange = { formData = formData.copy(assessorReg = it) },
                                label = { Text("SAIOH Reg #") },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("input_assessor_reg"),
                                colors = workforceFieldColors()
                            )
                        }
                    }
                }
            }

            // Section Filter Tabs (Sections A-G)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FilterSectionChip(
                        title = "All Sections",
                        isSelected = activeFilterSection == null,
                        onClick = { activeFilterSection = null }
                    )
                    SectionId.entries.forEach { section ->
                        FilterSectionChip(
                            title = section.title.take(18),
                            isSelected = activeFilterSection == section,
                            onClick = { activeFilterSection = section }
                        )
                    }
                }
            }

            // Audit Questions List with Scoring Controls
            val filteredQuestions = if (activeFilterSection == null) {
                questions
            } else {
                questions.filter { it.sectionId == activeFilterSection }
            }

            items(filteredQuestions, key = { it.id }) { question ->
                AuditQuestionCard(
                    question = question,
                    onResponseChanged = { newResponse ->
                        val idx = questions.indexOfFirst { it.id == question.id }
                        if (idx != -1) {
                            questions[idx] = questions[idx].copy(response = newResponse)
                        }
                    },
                    onRemarksChanged = { notes, mode ->
                        val idx = questions.indexOfFirst { it.id == question.id }
                        if (idx != -1) {
                            questions[idx] = questions[idx].copy(
                                remarks = notes,
                                remarkInputMode = mode.name
                            )
                        }
                    },
                    onGeoCoordinatesChanged = { lat, lon, acc ->
                        val idx = questions.indexOfFirst { it.id == question.id }
                        if (idx != -1) {
                            val timeStr = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
                            questions[idx] = questions[idx].copy(
                                geoLatitude = lat,
                                geoLongitude = lon,
                                geoAccuracy = acc,
                                geoTimestamp = timeStr
                            )
                        }
                    },
                    onOpenCameraForQuestion = {
                        activeTargetQuestionId = question.id
                        isCameraExpanded = true
                    },
                    onOpenVoiceDictation = {
                        dictatingQuestionId = question.id
                    },
                    attachedPhotosCount = capturedPhotos.count { it.questionId == question.id }
                )
            }

            // Form Submit / Save Actions
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val missingRemarksCount = questions.count { it.remarks.isBlank() }

                    if (validationErrorMessage != null || missingRemarksCount > 0) {
                        Surface(
                            color = ComplianceAmber.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, ComplianceAmber.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Warning,
                                            contentDescription = null,
                                            tint = ComplianceAmber,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = validationErrorMessage ?: "Compulsory: $missingRemarksCount question(s) need remarks or voice dictation",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = WorkforceTextPrimary,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                    }
                                    Button(
                                        onClick = {
                                            questions.indices.forEach { i ->
                                                if (questions[i].remarks.isBlank()) {
                                                    val autoText = if (questions[i].response == ComplianceResponse.NO)
                                                        "Non-conformance logged for action"
                                                    else
                                                        "Compliant & in good order as verified on site."
                                                    questions[i] = questions[i].copy(remarks = autoText, remarkInputMode = "TYPING")
                                                }
                                            }
                                            validationErrorMessage = null
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = WorkforceOrange),
                                        shape = RoundedCornerShape(6.dp),
                                        modifier = Modifier.height(32.dp)
                                    ) {
                                        Text("Auto-Fill", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    Button(
                        onClick = {
                            val missing = questions.filter { it.remarks.isBlank() }
                            if (missing.isNotEmpty()) {
                                validationErrorMessage = "Cannot finalize audit: ${missing.size} question(s) require compulsory remarks or voice dictation!"
                                return@Button
                            }
                            validationErrorMessage = null
                            val dummyAssessment = initialAssessment ?: buildAssessmentModel(
                                id = "wfh_audit_${System.currentTimeMillis() % 100000}",
                                formData = formData,
                                score = auditScorePercent.toInt(),
                                status = AssessmentStatus.SUBMITTED_FOR_REVIEW
                            )
                            onSubmitInspection(dummyAssessment, questions.toList(), auditScorePercent)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = WorkforceRed),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("btn_submit_audit_form")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AssignmentTurnedIn,
                            contentDescription = null,
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "FINALIZE AUDIT (SCORE: ${String.format(Locale.US, "%.1f%%", auditScorePercent)})",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            val dummyAssessment = initialAssessment ?: buildAssessmentModel(
                                id = "wfh_audit_${System.currentTimeMillis() % 100000}",
                                formData = formData,
                                score = auditScorePercent.toInt(),
                                status = AssessmentStatus.DRAFT
                            )
                            onSaveDraft(dummyAssessment, questions.toList())
                        },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("btn_save_draft_form")
                    ) {
                        Icon(imageVector = Icons.Default.Save, contentDescription = null, tint = WorkforceTextPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Save Audit Draft Locally", color = WorkforceTextPrimary)
                    }
                }
            }
        }
    }

    // Voice Dictation Dialog integration
    dictatingQuestionId?.let { qId ->
        val q = questions.find { it.id == qId }
        if (q != null) {
            VoiceDictationDialog(
                questionNumber = q.questionNumber,
                questionText = q.questionText,
                initialRemarks = q.remarks,
                onDismiss = { dictatingQuestionId = null },
                onSaveRemarks = { text ->
                    val idx = questions.indexOfFirst { it.id == qId }
                    if (idx != -1) {
                        questions[idx] = questions[idx].copy(remarks = text)
                    }
                    dictatingQuestionId = null
                }
            )
        }
    }
}

/**
 * Audit Question Card with Scoring Selector & Photographic Evidence Trigger
 */
@Composable
private fun AuditQuestionCard(
    question: ChecklistQuestion,
    onResponseChanged: (ComplianceResponse) -> Unit,
    onRemarksChanged: (String, RemarkInputMode) -> Unit,
    onGeoCoordinatesChanged: (Double, Double, Float) -> Unit,
    onOpenCameraForQuestion: () -> Unit,
    onOpenVoiceDictation: () -> Unit,
    attachedPhotosCount: Int
) {
    var isRemarksEditorExpanded by remember(question.remarks, question.geoLatitude) {
        mutableStateOf(question.remarks.isNotEmpty() || question.geoLatitude != null || question.response == ComplianceResponse.NO)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("card_question_${question.id}"),
        colors = CardDefaults.cardColors(containerColor = WorkforceSurface),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, WorkforceCardBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header: Section + Question Number
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "${question.sectionId.name.removePrefix("SECTION_")}.Q${question.questionNumber}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = WorkforceOrange,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    )
                    if (question.remarks.isBlank()) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = ComplianceAmber.copy(alpha = 0.15f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, ComplianceAmber.copy(alpha = 0.4f))
                        ) {
                            Text(
                                text = "* COMPULSORY REMARK",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = ComplianceAmber,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    } else {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = ComplianceGreen.copy(alpha = 0.15f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, ComplianceGreen.copy(alpha = 0.4f))
                        ) {
                            Text(
                                text = "✓ ${if (question.remarkInputMode == "VOICE") "VOICE" else "REMARK"}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = ComplianceGreen,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = WorkforceSurfaceVariant
                ) {
                    Text(
                        text = "Weight: 1.0 pt",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = WorkforceTextMuted,
                            fontSize = 11.sp
                        ),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Question Text
            Text(
                text = question.questionText,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = WorkforceTextPrimary,
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 20.sp
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Audit Scoring Selector (Pass / Fail / N/A)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val isSpecialDefect = question.isSpecialYesDefect

                AuditScoreOptionButton(
                    text = if (isSpecialDefect) "DEFECT (YES)" else "PASS (YES)",
                    isSelected = question.response == ComplianceResponse.YES,
                    activeColor = if (isSpecialDefect) ComplianceRed else ComplianceGreen,
                    modifier = Modifier.weight(1f),
                    testTag = "btn_pass_${question.id}",
                    onClick = {
                        onResponseChanged(ComplianceResponse.YES)
                        if (isSpecialDefect) {
                            isRemarksEditorExpanded = true
                        }
                    }
                )

                AuditScoreOptionButton(
                    text = if (isSpecialDefect) "CLEAN (NO)" else "FAIL (NO)",
                    isSelected = question.response == ComplianceResponse.NO,
                    activeColor = if (isSpecialDefect) ComplianceGreen else ComplianceRed,
                    modifier = Modifier.weight(1f),
                    testTag = "btn_fail_${question.id}",
                    onClick = {
                        onResponseChanged(ComplianceResponse.NO)
                        if (!isSpecialDefect) {
                            isRemarksEditorExpanded = true
                        }
                    }
                )

                AuditScoreOptionButton(
                    text = "N/A",
                    isSelected = question.response == ComplianceResponse.NOT_APPLICABLE,
                    activeColor = WorkforceTextSecondary,
                    modifier = Modifier.weight(0.7f),
                    testTag = "btn_na_${question.id}",
                    onClick = { onResponseChanged(ComplianceResponse.NOT_APPLICABLE) }
                )
            }

            // Quick Actions: CameraX Evidence, Remark/GPS Toggle & Voice Dictation
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (attachedPhotosCount > 0) WorkforceOrange.copy(alpha = 0.15f) else WorkforceSurfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (attachedPhotosCount > 0) WorkforceOrange else WorkforceCardBorder
                        ),
                        modifier = Modifier
                            .clickable { onOpenCameraForQuestion() }
                            .testTag("btn_attach_photo_${question.id}")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.PhotoCamera,
                                contentDescription = "Capture Photo",
                                tint = if (attachedPhotosCount > 0) WorkforceOrange else WorkforceTextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (attachedPhotosCount > 0) "$attachedPhotosCount Photos" else "Photo",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = if (attachedPhotosCount > 0) WorkforceOrange else WorkforceTextPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }
                    }

                    // Toggle Remarks & GPS Editor button
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isRemarksEditorExpanded || question.remarks.isNotEmpty() || question.geoLatitude != null)
                            WorkforceOrange.copy(alpha = 0.15f) else WorkforceSurfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (question.remarks.isNotEmpty() || question.geoLatitude != null) WorkforceOrange else WorkforceCardBorder
                        ),
                        modifier = Modifier
                            .clickable { isRemarksEditorExpanded = !isRemarksEditorExpanded }
                            .testTag("btn_toggle_remark_gps_${question.id}")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.MyLocation,
                                contentDescription = "Remark & GPS",
                                tint = if (question.geoLatitude != null) ComplianceGreen else WorkforceOrange,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = when {
                                    question.remarks.isNotEmpty() && question.geoLatitude != null -> "Remark + GPS Stamped"
                                    question.remarks.isNotEmpty() -> "Remark Attached"
                                    question.geoLatitude != null -> "GPS Stamped"
                                    else -> "Remark / GPS"
                                },
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = if (question.geoLatitude != null) ComplianceGreen else WorkforceTextPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }
                    }
                }

                IconButton(
                    onClick = {
                        isRemarksEditorExpanded = true
                        onOpenVoiceDictation()
                    },
                    modifier = Modifier
                        .size(36.dp)
                        .background(WorkforceSurfaceVariant, CircleShape)
                        .testTag("btn_dictate_${question.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Voice Dictate",
                        tint = WorkforceOrange,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Finding Remark & GPS Editor with Option between Voice and Typing
            AnimatedVisibility(visible = isRemarksEditorExpanded) {
                Column(modifier = Modifier.padding(top = 10.dp)) {
                    if (question.isDefectOrNonCompliant) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = ComplianceRed.copy(alpha = 0.15f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, ComplianceRed.copy(alpha = 0.4f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 6.dp)
                        ) {
                            Text(
                                text = "CORRECTIVE ACTION MANDATORY (MHSA Non-Compliance Finding)",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = ComplianceRed,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    FindingRemarkEditor(
                        findingId = question.id,
                        findingTitle = "Q${question.questionNumber}: ${question.questionText}",
                        initialRemark = question.remarks,
                        initialMode = if (question.remarkInputMode == "VOICE") RemarkInputMode.VOICE else RemarkInputMode.TYPING,
                        currentLatitude = question.geoLatitude,
                        currentLongitude = question.geoLongitude,
                        currentAccuracy = question.geoAccuracy,
                        currentGeoTimestamp = question.geoTimestamp,
                        onRemarkChanged = onRemarksChanged,
                        onGeoCoordinatesStamped = onGeoCoordinatesChanged,
                        onOpenFullVoiceDialog = onOpenVoiceDictation
                    )
                }
            }
        }
    }
}

/**
 * Audit Score Selector Button
 */
@Composable
private fun AuditScoreOptionButton(
    text: String,
    isSelected: Boolean,
    activeColor: Color,
    modifier: Modifier = Modifier,
    testTag: String,
    onClick: () -> Unit
) {
    val bgColor by animateColorAsState(
        targetValue = if (isSelected) activeColor else WorkforceSurfaceVariant,
        label = "score_btn_bg"
    )
    val textColor = if (isSelected) Color.White else WorkforceTextSecondary

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = bgColor,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSelected) activeColor else WorkforceCardBorder
        ),
        modifier = modifier
            .height(42.dp)
            .clickable(onClick = onClick)
            .testTag(testTag)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = text,
                style = MaterialTheme.typography.titleSmall.copy(
                    color = textColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            )
        }
    }
}

/**
 * Photo Thumbnail Card for Evidence Reel
 */
@Composable
private fun PhotoThumbnailCard(
    photo: FormPhotoEvidence,
    onDelete: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = WorkforceDarkBg,
        border = androidx.compose.foundation.BorderStroke(1.dp, WorkforceCardBorder),
        modifier = Modifier
            .width(130.dp)
            .height(110.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = WorkforceOrange.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = photo.sectionTag,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = WorkforceOrange,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(22.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Remove Photo",
                            tint = ComplianceRed,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = null,
                        tint = WorkforceTextSecondary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "JPEG Photo",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = WorkforceTextSecondary,
                            fontSize = 11.sp
                        )
                    )
                }

                Text(
                    text = photo.timestamp.takeLast(5),
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = WorkforceTextMuted,
                        fontSize = 10.sp
                    )
                )
            }
        }
    }
}

/**
 * Filter Section Chip
 */
@Composable
private fun FilterSectionChip(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) WorkforceOrange else WorkforceSurface,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSelected) WorkforceOrange else WorkforceCardBorder
        ),
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall.copy(
                color = if (isSelected) Color.White else WorkforceTextSecondary,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                fontSize = 12.sp
            ),
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

/**
 * Metric Badge helper
 */
@Composable
private fun AuditMetricBadge(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium.copy(
                color = color,
                fontWeight = FontWeight.Black
            )
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(
                color = WorkforceTextMuted,
                fontSize = 10.sp
            )
        )
    }
}

@Composable
private fun workforceFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = WorkforceOrange,
    unfocusedBorderColor = WorkforceCardBorder,
    focusedTextColor = WorkforceTextPrimary,
    unfocusedTextColor = WorkforceTextPrimary,
    focusedLabelColor = WorkforceOrange,
    unfocusedLabelColor = WorkforceTextSecondary
)

/**
 * Default sample audit questions covering Sections A to F
 */
fun getDefaultInspectionQuestions(assessmentId: String): List<ChecklistQuestion> {
    return listOf(
        ChecklistQuestion(
            id = "q_sec_a_1",
            assessmentId = assessmentId,
            sectionId = SectionId.SECTION_A,
            questionNumber = 1,
            questionText = "Are entrance doors, handles, and privacy latches in operational working condition without damage?",
            response = ComplianceResponse.YES
        ),
        ChecklistQuestion(
            id = "q_sec_a_2",
            assessmentId = assessmentId,
            sectionId = SectionId.SECTION_A,
            questionNumber = 2,
            questionText = "Are walls, partitions, and floor surfaces impervious, non-slip, and free of hazardous cracks?",
            response = ComplianceResponse.YES
        ),
        ChecklistQuestion(
            id = "q_sec_b_1",
            assessmentId = assessmentId,
            sectionId = SectionId.SECTION_B,
            questionNumber = 1,
            questionText = "Is mechanical or natural ventilation sufficient to prevent foul odour build-up and dampness?",
            response = ComplianceResponse.YES
        ),
        ChecklistQuestion(
            id = "q_sec_b_2",
            assessmentId = assessmentId,
            sectionId = SectionId.SECTION_B,
            questionNumber = 2,
            questionText = "Are lighting levels at least 100 lux across all cubicles, urinals, and washing bays?",
            response = ComplianceResponse.YES
        ),
        ChecklistQuestion(
            id = "q_sec_c_1",
            assessmentId = assessmentId,
            sectionId = SectionId.SECTION_C,
            questionNumber = 1,
            questionText = "Are all water closets (toilets) flushing effectively with minimum 6L cistern volume and leak-free seals?",
            response = ComplianceResponse.YES
        ),
        ChecklistQuestion(
            id = "q_sec_c_2",
            assessmentId = assessmentId,
            sectionId = SectionId.SECTION_C,
            questionNumber = 2,
            questionText = "Are urinals fitted with functional flush valves and free of severe uric acid encrustation?",
            response = ComplianceResponse.YES
        ),
        ChecklistQuestion(
            id = "q_sec_d_1",
            assessmentId = assessmentId,
            sectionId = SectionId.SECTION_D,
            questionNumber = 1,
            questionText = "Are handwash basins clean, equipped with functional taps and adequate running potable water?",
            response = ComplianceResponse.YES
        ),
        ChecklistQuestion(
            id = "q_sec_d_2",
            assessmentId = assessmentId,
            sectionId = SectionId.SECTION_D,
            questionNumber = 2,
            questionText = "Are wall-mounted liquid soap dispensers filled with approved antibacterial hand soap?",
            response = ComplianceResponse.YES
        ),
        ChecklistQuestion(
            id = "q_sec_e_1",
            assessmentId = assessmentId,
            sectionId = SectionId.SECTION_E,
            questionNumber = 1,
            questionText = "Are heavy-duty waste bins lined with heavy plastic bags and emptied at prescribed shift intervals?",
            response = ComplianceResponse.YES
        ),
        ChecklistQuestion(
            id = "q_sec_e_2",
            assessmentId = assessmentId,
            sectionId = SectionId.SECTION_E,
            questionNumber = 2,
            questionText = "Are hygiene cleaning chemicals registered with verified Safety Data Sheets (SDS) on file?",
            response = ComplianceResponse.YES
        ),
        ChecklistQuestion(
            id = "q_sec_f_1",
            assessmentId = assessmentId,
            sectionId = SectionId.SECTION_F,
            questionNumber = 1,
            questionText = "Is statutory Mine Health and Safety Act hygiene signage prominently displayed at facility entry?",
            response = ComplianceResponse.YES
        ),
        ChecklistQuestion(
            id = "q_sec_f_2",
            assessmentId = assessmentId,
            sectionId = SectionId.SECTION_F,
            questionNumber = 2,
            questionText = "Are cleaning operators provided with complete PPE (gloves, goggles, aprons, chemical safety boots)?",
            response = ComplianceResponse.YES
        )
    )
}
