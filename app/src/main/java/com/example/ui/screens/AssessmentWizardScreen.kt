package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AddAlert
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Draw
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.example.data.model.ChecklistQuestion
import com.example.data.model.ComplianceResponse
import com.example.data.model.SectionId
import com.example.data.model.isDefectResponse
import com.example.data.model.isSpecialYesDefectQuestion
import com.example.ui.components.CameraCaptureDialog
import com.example.ui.components.FindingRemarkEditor
import com.example.ui.components.GloveFriendlyComplianceSelector
import com.example.ui.components.RemarkInputMode
import com.example.ui.components.SyncDetailDialog
import com.example.ui.components.VoiceDictationDialog
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

@Composable
fun AssessmentWizardScreen(
    viewModel: WorkforceViewModel,
    assessmentId: String,
    onBack: () -> Unit
) {
    val assessment by viewModel.activeAssessment.collectAsState()
    val questions by viewModel.activeQuestions.collectAsState()
    val currentSectionIndex by viewModel.currentSectionIndex.collectAsState()
    val syncStatus by viewModel.syncManager.syncState.collectAsState()

    val sections = SectionId.values()
    val currentSection = sections[currentSectionIndex]
    val sectionQuestions = questions.filter { it.sectionId == currentSection }

    // Dialog state for logging remedial actions
    var remedialDialogQuestion by remember { mutableStateOf<ChecklistQuestion?>(null) }
    var remedialDescription by remember { mutableStateOf("") }
    var remedialActionText by remember { mutableStateOf("") }
    var remedialResponsible by remember { mutableStateOf("Plant Maintenance Dept") }
    var remedialTargetDate by remember { mutableStateOf("2026-11-06") }
    var remedialPriority by remember { mutableStateOf(ActionPriority.HIGH) }

    // Dialog for photo capture
    var photoDialogQuestion by remember { mutableStateOf<ChecklistQuestion?>(null) }
    var photoCaptionText by remember { mutableStateOf("") }

    // Dialog for voice-to-text dictation
    var voiceDictationQuestion by remember { mutableStateOf<ChecklistQuestion?>(null) }

    // Dialog for cloud sync status center
    var showSyncDialog by remember { mutableStateOf(false) }

    val total = questions.size
    val answered = questions.count { it.response != ComplianceResponse.NOT_ASSESSED }
    val progress = if (total > 0) answered.toFloat() / total.toFloat() else 0f

    if (showSyncDialog) {
        SyncDetailDialog(
            syncManager = viewModel.syncManager,
            onDismiss = { showSyncDialog = false },
            onSyncNow = { viewModel.syncCloud() },
            onToggleOffline = { viewModel.toggleOfflineMode() }
        )
    }

    Scaffold(
        topBar = {
            WorkforceTopBar(
                title = assessment?.referenceNumber ?: "Checklist Assessment",
                subtitle = "${assessment?.facilityName} • Section ${currentSectionIndex + 1} of ${sections.size}",
                canNavigateBack = true,
                onNavigateBack = onBack,
                syncStatus = syncStatus,
                onSyncClick = { showSyncDialog = true }
            )
        },
        bottomBar = {
            // Wizard Navigation: Previous / Next Section buttons
            Surface(
                color = WorkforceDarkBg,
                border = androidx.compose.foundation.BorderStroke(1.dp, WorkforceCardBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = { viewModel.previousSection() },
                        enabled = currentSectionIndex > 0,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .height(48.dp)
                            .testTag("wizard_prev_section_button")
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("PREVIOUS", color = if (currentSectionIndex > 0) WorkforceTextPrimary else WorkforceTextMuted)
                    }

                    if (currentSectionIndex < sections.size - 1) {
                        Button(
                            onClick = { viewModel.nextSection() },
                            colors = ButtonDefaults.buttonColors(containerColor = WorkforceRed),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .height(48.dp)
                                .testTag("wizard_next_section_button")
                        ) {
                            Text("NEXT SECTION", fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
                        }
                    } else {
                        Button(
                            onClick = {
                                val submitted = viewModel.submitAssessment()
                                if (submitted) {
                                    viewModel.navigateTo(AppScreen.ReportPreview(assessmentId))
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ComplianceGreen),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .height(48.dp)
                                .testTag("wizard_finish_submit_button")
                        ) {
                            Icon(imageVector = Icons.Default.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("SUBMIT REVIEW", fontWeight = FontWeight.Bold)
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
        ) {
            // Overall Assessment Header & Progress Bar
            Surface(
                color = WorkforceSurface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = assessment?.facilityName ?: "Monthly ablution facilities checklist *Male",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = WorkforceTextPrimary
                        )
                    )
                    Text(
                        text = "${assessment?.customerName} • ${assessment?.siteName}",
                        style = MaterialTheme.typography.bodySmall.copy(color = WorkforceTextSecondary)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Checklist Progress: $answered / $total items ($((progress * 100).toInt())%)",
                            style = MaterialTheme.typography.labelSmall.copy(color = WorkforceTextSecondary)
                        )
                        val score = assessment?.complianceScore ?: 0
                        val scoreColor = if (score >= 80) ComplianceGreen else if (score >= 60) ComplianceAmber else ComplianceRed
                        Text(
                            text = "Score: $score%",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = scoreColor,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = WorkforceRed,
                        trackColor = WorkforceSurfaceVariant,
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Shortcut buttons for Chemicals, Signatures, Report Preview
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.navigateTo(AppScreen.InspectionFormScreen(assessmentId)) },
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .testTag("shortcut_inspection_form_btn"),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(imageVector = Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(16.dp), tint = WorkforceOrange)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Form (CameraX)", style = MaterialTheme.typography.labelSmall, color = WorkforceTextPrimary)
                        }

                        OutlinedButton(
                            onClick = { viewModel.navigateTo(AppScreen.ChemicalRegister(assessmentId)) },
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .testTag("shortcut_chemicals_btn"),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Science, contentDescription = null, modifier = Modifier.size(16.dp), tint = WorkforceOrange)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Chemicals", style = MaterialTheme.typography.labelSmall, color = WorkforceTextPrimary)
                        }

                        OutlinedButton(
                            onClick = { viewModel.navigateTo(AppScreen.SignatureCapture(assessmentId)) },
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .testTag("shortcut_signatures_btn"),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Draw, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color(0xFF60A5FA))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Sign", style = MaterialTheme.typography.labelSmall, color = WorkforceTextPrimary)
                        }

                        OutlinedButton(
                            onClick = { viewModel.navigateTo(AppScreen.ReportPreview(assessmentId)) },
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .testTag("shortcut_report_btn"),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Description, contentDescription = null, modifier = Modifier.size(16.dp), tint = ComplianceGreen)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("PDF", style = MaterialTheme.typography.labelSmall, color = WorkforceTextPrimary)
                        }
                    }

                    // Compulsory Remarks Status Banner
                    val missingRemarksCount = questions.count { it.remarks.isBlank() }
                    if (missingRemarksCount > 0) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            color = ComplianceAmber.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, ComplianceAmber.copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
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
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Compulsory: $missingRemarksCount questions need remarks/dictation",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = WorkforceTextPrimary,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    )
                                }
                                TextButton(
                                    onClick = { viewModel.autofillCompulsoryRemarks() },
                                    modifier = Modifier.height(30.dp)
                                ) {
                                    Text(
                                        "Auto-Fill Remarks",
                                        color = WorkforceOrange,
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Section Horizontal Stepper Tabs
            val scrollState = rememberScrollState()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(WorkforceSurfaceVariant)
                    .horizontalScroll(scrollState)
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                sections.forEachIndexed { index, sectionId ->
                    val isSelected = index == currentSectionIndex
                    val sectionQs = questions.filter { it.sectionId == sectionId }
                    val sectionDone = sectionQs.isNotEmpty() && sectionQs.all { it.response != ComplianceResponse.NOT_ASSESSED }
                    val letter = sectionId.name.removePrefix("SECTION_")

                    Surface(
                        modifier = Modifier
                            .clickable { viewModel.setSectionIndex(index) }
                            .testTag("tab_section_$letter"),
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) WorkforceRed else if (sectionDone) ComplianceGreen.copy(alpha = 0.2f) else WorkforceSurface,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) WorkforceRed else if (sectionDone) ComplianceGreen else WorkforceCardBorder
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Sec $letter",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else if (sectionDone) ComplianceGreen else WorkforceTextSecondary
                                )
                            )
                            if (sectionDone) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Completed",
                                    tint = ComplianceGreen,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Section Title Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(WorkforceDarkBg)
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Text(
                    text = currentSection.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Black,
                        color = WorkforceTextPrimary,
                        letterSpacing = 0.5.sp
                    )
                )
                Text(
                    text = currentSection.subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(color = WorkforceTextSecondary)
                )
            }

            // Questions List
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(sectionQuestions, key = { it.id }) { question ->
                    QuestionCard(
                        question = question,
                        onResponseSelected = { resp ->
                            viewModel.answerQuestion(question.id, resp)
                            val isDefectTrigger = isDefectResponse(question.sectionId, question.questionNumber, resp)
                            // If non-conformance/defect was triggered (YES for A5/C7/D4/F14 or NO for standard items), prompt remedial action
                            if (isDefectTrigger && !question.hasRemedialAction) {
                                remedialDialogQuestion = question
                                remedialDescription = "Non-conformance / Hazard logged on ${question.sectionId.title} Q${question.questionNumber}: ${question.questionText}"
                                remedialActionText = ""
                            }
                        },
                        onRemarksChanged = { rem, mode ->
                            viewModel.updateQuestionRemarkWithDetails(
                                questionId = question.id,
                                remarks = rem,
                                inputMode = mode.name
                            )
                        },
                        onPhotoClick = {
                            photoDialogQuestion = question
                            photoCaptionText = "Photo evidence for Q${question.questionNumber}"
                        },
                        onGeoClick = {
                            viewModel.acquireDeviceGpsForQuestion(question.id)
                        },
                        onGeoCoordinatesStamped = { lat, lon, acc ->
                            viewModel.stampGeoCoordinates(question.id, lat, lon, acc)
                        },
                        onLogRemedialActionClick = {
                            remedialDialogQuestion = question
                            remedialDescription = "Finding on ${question.sectionId.title} Q${question.questionNumber}: ${question.questionText}"
                            remedialActionText = question.correctiveActionNotes
                        },
                        onOpenChemicalRegister = {
                            viewModel.navigateTo(AppScreen.ChemicalRegister(assessmentId))
                        },
                        onVoiceDictateClick = {
                            voiceDictationQuestion = question
                        }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }

    // Hands-Free Voice-to-Text Dictation Dialog
    if (voiceDictationQuestion != null) {
        val q = voiceDictationQuestion!!
        VoiceDictationDialog(
            questionNumber = q.questionNumber,
            questionText = q.questionText,
            initialRemarks = q.remarks,
            onDismiss = { voiceDictationQuestion = null },
            onSaveRemarks = { newRemarks ->
                viewModel.updateQuestionRemarks(q.id, newRemarks)
                voiceDictationQuestion = null
                viewModel.showNotification("Saved voice remarks for Q${q.questionNumber}")
            }
        )
    }

    // Dialog: Create Remedial Action
    if (remedialDialogQuestion != null) {
        val q = remedialDialogQuestion!!
        AlertDialog(
            onDismissRequest = { remedialDialogQuestion = null },
            title = {
                Text(
                    text = "Log Remedial Action (Q${q.questionNumber})",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = WorkforceTextPrimary)
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = q.questionText,
                        style = MaterialTheme.typography.bodySmall.copy(color = WorkforceTextSecondary)
                    )

                    OutlinedTextField(
                        value = remedialDescription,
                        onValueChange = { remedialDescription = it },
                        label = { Text("Finding Description") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = remedialActionText,
                        onValueChange = { remedialActionText = it },
                        label = { Text("Recommended Corrective Action") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = remedialResponsible,
                        onValueChange = { remedialResponsible = it },
                        label = { Text("Responsible Person / Dept") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = remedialTargetDate,
                        onValueChange = { remedialTargetDate = it },
                        label = { Text("Target Completion Date (YYYY-MM-DD)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.createRemedialAction(
                            question = q,
                            description = remedialDescription,
                            action = remedialActionText,
                            responsible = remedialResponsible,
                            targetDate = remedialTargetDate,
                            priority = remedialPriority
                        )
                        remedialDialogQuestion = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = WorkforceRed)
                ) {
                    Text("Save Remedial Action")
                }
            },
            dismissButton = {
                TextButton(onClick = { remedialDialogQuestion = null }) {
                    Text("Cancel", color = WorkforceTextSecondary)
                }
            },
            containerColor = WorkforceSurface
        )
    }

    // CameraX Live Capture & Evidence Dialog
    if (photoDialogQuestion != null) {
        val q = photoDialogQuestion!!
        CameraCaptureDialog(
            question = q,
            onDismiss = { photoDialogQuestion = null },
            onPhotoCaptured = { photoUri, caption, lat, lon ->
                viewModel.attachPhotoToQuestion(
                    questionId = q.id,
                    uriString = photoUri,
                    caption = caption,
                    lat = lat,
                    lon = lon
                )
                photoDialogQuestion = null
            }
        )
    }
}

@Composable
fun QuestionCard(
    question: ChecklistQuestion,
    onResponseSelected: (ComplianceResponse) -> Unit,
    onRemarksChanged: (String, RemarkInputMode) -> Unit,
    onPhotoClick: () -> Unit,
    onGeoClick: () -> Unit,
    onGeoCoordinatesStamped: (Double, Double, Float) -> Unit = { _, _, _ -> },
    onLogRemedialActionClick: () -> Unit,
    onOpenChemicalRegister: () -> Unit,
    onVoiceDictateClick: () -> Unit = {}
) {
    var isExpandedRemarks by remember(question.remarks, question.geoLatitude, question.response) {
        mutableStateOf(question.remarks.isNotEmpty() || question.geoLatitude != null || question.isDefectOrNonCompliant)
    }

    val quickRemarks = remember {
        listOf(
            "Compliant & in good order",
            "Clean, dry and functional",
            "No defects or leaks observed",
            "Non-conformance logged for action",
            "Requires cleaning & sanitising"
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("question_item_${question.questionNumber}"),
        colors = CardDefaults.cardColors(containerColor = WorkforceSurface),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (question.remarks.isBlank() && question.response != ComplianceResponse.NOT_ASSESSED) ComplianceAmber.copy(alpha = 0.8f)
            else if (question.response != ComplianceResponse.NOT_ASSESSED && question.isDefectOrNonCompliant) ComplianceRed.copy(alpha = 0.8f)
            else if (question.response != ComplianceResponse.NOT_ASSESSED && !question.isDefectOrNonCompliant && question.response != ComplianceResponse.NOT_APPLICABLE) ComplianceGreen.copy(alpha = 0.5f)
            else WorkforceCardBorder
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header: Question Number & Compulsory Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        color = WorkforceSurfaceVariant,
                        shape = RoundedCornerShape(6.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, WorkforceCardBorder)
                    ) {
                        Text(
                            text = "QUESTION ${question.questionNumber}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = WorkforceOrange,
                                letterSpacing = 0.5.sp
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    if (question.remarks.isBlank()) {
                        Surface(
                            color = ComplianceAmber.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(6.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, ComplianceAmber.copy(alpha = 0.5f))
                        ) {
                            Text(
                                text = "* COMPULSORY REMARK",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = ComplianceAmber,
                                    fontSize = 9.sp
                                ),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    } else {
                        Surface(
                            color = ComplianceGreen.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(6.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, ComplianceGreen.copy(alpha = 0.4f))
                        ) {
                            Text(
                                text = "✓ ${if (question.remarkInputMode == "VOICE") "VOICE" else "REMARK"}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = ComplianceGreen,
                                    fontSize = 9.sp
                                ),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }
                }

                if (question.hasRemedialAction) {
                    Surface(
                        color = ComplianceRed.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(6.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ComplianceRed.copy(alpha = 0.4f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = ComplianceRed, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "ACTION LOGGED",
                                style = MaterialTheme.typography.labelSmall.copy(color = ComplianceRed, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Special Defect Question Alert Banner (Section A Q5, C Q7, D Q4, F Q14)
            if (question.isSpecialYesDefect) {
                Surface(
                    color = ComplianceRed.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ComplianceRed.copy(alpha = 0.45f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = ComplianceRed,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "CRITICAL DEFECT CRITERION: Selecting 'YES' logs a Remedial Action for Moulds/Odours",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = ComplianceRed,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        )
                    }
                }
            }

            // Question Text
            Text(
                text = question.questionText,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = WorkforceTextPrimary,
                    lineHeight = 22.sp
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Glove-friendly YES / NO / NA Selector
            GloveFriendlyComplianceSelector(
                selectedResponse = question.response,
                allowsNA = question.allowsNA,
                onSelect = { resp ->
                    onResponseSelected(resp)
                    if (question.remarks.isBlank() || isDefectResponse(question.sectionId, question.questionNumber, resp)) {
                        isExpandedRemarks = true
                    }
                }
            )

            // One-tap quick remark chips
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                quickRemarks.forEach { chipText ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (question.remarks == chipText) WorkforceOrange.copy(alpha = 0.2f) else WorkforceSurfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (question.remarks == chipText) WorkforceOrange else WorkforceCardBorder
                        ),
                        modifier = Modifier.clickable {
                            onRemarksChanged(chipText, RemarkInputMode.TYPING)
                        }
                    ) {
                        Text(
                            text = chipText,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = if (question.remarks == chipText) WorkforceOrange else WorkforceTextPrimary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // Special guidance callout for Question 20 and Question 22 (Chemical registers)
            if (question.sectionId == SectionId.SECTION_F && (question.questionNumber == 20 || question.questionNumber == 22)) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = WorkforceOrange.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, WorkforceOrange.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (question.questionNumber == 20) "Deep-Cleaning Chemical & SDS Register" else "Daily Routine Chemical & SDS Register",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = WorkforceOrange)
                            )
                            Text(
                                text = "Procedure obtained/returned & SDS tracking",
                                style = MaterialTheme.typography.bodySmall.copy(color = WorkforceTextSecondary, fontSize = 11.sp)
                            )
                        }
                        Button(
                            onClick = onOpenChemicalRegister,
                            colors = ButtonDefaults.buttonColors(containerColor = WorkforceOrange),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Text("Open Register", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Quick Action Toolbar: Remarks, Photo, GPS, Remedial Action
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    // Remarks Toggle
                    OutlinedButton(
                        onClick = { isExpandedRemarks = !isExpandedRemarks },
                        shape = RoundedCornerShape(6.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (isExpandedRemarks || question.remarks.isNotEmpty()) WorkforceSurfaceVariant else Color.Transparent
                        ),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp), tint = WorkforceTextSecondary)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (question.remarks.isNotEmpty()) "Remarks (1)" else "Remarks",
                            style = MaterialTheme.typography.labelSmall,
                            color = WorkforceTextPrimary
                        )
                    }

                    // Voice-to-Text Hands-Free Dictation Button
                    OutlinedButton(
                        onClick = {
                            isExpandedRemarks = true
                            onVoiceDictateClick()
                        },
                        shape = RoundedCornerShape(6.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (question.remarks.isNotEmpty()) WorkforceOrange.copy(alpha = 0.15f) else Color.Transparent
                        ),
                        modifier = Modifier
                            .height(34.dp)
                            .testTag("btn_dictate_q_${question.questionNumber}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Voice Dictate Findings",
                            modifier = Modifier.size(14.dp),
                            tint = WorkforceOrange
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Dictate",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = WorkforceOrange
                        )
                    }

                    // Photo evidence
                    OutlinedButton(
                        onClick = onPhotoClick,
                        shape = RoundedCornerShape(6.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (question.photoUri != null) ComplianceGreen.copy(alpha = 0.15f) else Color.Transparent
                        ),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = if (question.photoUri != null) ComplianceGreen else WorkforceTextSecondary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (question.photoUri != null) "Photo" else "Photo",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (question.photoUri != null) ComplianceGreen else WorkforceTextPrimary
                        )
                    }

                    // GPS locate
                    IconButton(
                        onClick = onGeoClick,
                        modifier = Modifier.size(34.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MyLocation,
                            contentDescription = "GEO Coordinates",
                            tint = if (question.geoLatitude != null) ComplianceGreen else WorkforceOrange,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // If finding is non-compliant/defective or has remedial action
                if (question.isDefectOrNonCompliant || question.hasRemedialAction) {
                    TextButton(
                        onClick = onLogRemedialActionClick,
                        modifier = Modifier.height(34.dp)
                    ) {
                        Icon(imageVector = Icons.Default.AddAlert, contentDescription = null, tint = ComplianceRed, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Log Action", color = ComplianceRed, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Attached photo info badge if present
            if (question.photoUri != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    color = WorkforceSurfaceVariant,
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = ComplianceGreen, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${question.photoCaption ?: "Photo"} • ${question.photoTimestamp}",
                            style = MaterialTheme.typography.bodySmall.copy(color = WorkforceTextSecondary, fontSize = 11.sp)
                        )
                    }
                }
            }

            // Expandable Finding Remark & GPS Editor with Option between Voice and Typing
            AnimatedVisibility(visible = isExpandedRemarks) {
                Column(modifier = Modifier.padding(top = 10.dp)) {
                    FindingRemarkEditor(
                        findingId = question.id,
                        findingTitle = "Question ${question.questionNumber}",
                        initialRemark = question.remarks,
                        initialMode = if (question.remarkInputMode == "VOICE") RemarkInputMode.VOICE else RemarkInputMode.TYPING,
                        currentLatitude = question.geoLatitude,
                        currentLongitude = question.geoLongitude,
                        currentAccuracy = question.geoAccuracy,
                        currentGeoTimestamp = question.geoTimestamp,
                        onRemarkChanged = onRemarksChanged,
                        onGeoCoordinatesStamped = onGeoCoordinatesStamped,
                        onOpenFullVoiceDialog = onVoiceDictateClick
                    )
                }
            }
        }
    }
}
