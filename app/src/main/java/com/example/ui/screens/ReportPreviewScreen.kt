package com.example.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.Assessment
import com.example.data.model.ChecklistQuestion
import com.example.data.model.ComplianceResponse
import com.example.data.model.RemedialAction
import com.example.ui.components.ExportReportDialog
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
import java.util.Locale

/**
 * ReportPreviewScreen
 *
 * Provides a high-fidelity, interactive 28-page preview of the complete Master Template
 * Assessment Ablution dossier, matching the exact format from Page 1 to Page 28.
 */
@Composable
fun ReportPreviewScreen(
    viewModel: WorkforceViewModel,
    assessmentId: String,
    onBack: () -> Unit
) {
    val assessment by viewModel.activeAssessment.collectAsState()
    val questions by viewModel.activeQuestions.collectAsState()
    val chemicals by viewModel.chemicalsList.collectAsState()
    val deepProcedure by viewModel.deepProcedureStatus.collectAsState()
    val routineProcedure by viewModel.routineProcedureStatus.collectAsState()
    val signatures by viewModel.signaturesList.collectAsState()
    val syncStatus by viewModel.syncManager.syncState.collectAsState()
    val isExportingPdf by viewModel.isExportingPdf.collectAsState()
    val lastExportedPath by viewModel.lastExportedPath.collectAsState()
    val remedialActions by viewModel.outstandingRemedialActions.collectAsState()

    var showExportDialog by remember { mutableStateOf(false) }
    var selectedPageNumber by remember { mutableIntStateOf(1) }
    val totalPages = 28

    val a = assessment

    val pageTitles = remember {
        listOf(
            "Page 1: Title & Cover — Assessment Work Instruction",
            "Page 2: Remedial Action Required From Previous Site Visit",
            "Page 3: Special / Additional Instructions by Appointed Hygienist",
            "Page 4: 1 Customer Information & Conditions of Work",
            "Page 5: 2 Environmental Conditions (3 Shifts & Equipment)",
            "Page 6: 3 Executive Summary (Part 1 Interim Notice)",
            "Page 7: 4 Table of Contents",
            "Page 8: 5 Abbreviations, Acronyms & Definitions",
            "Page 9: 6 Introduction — 6.1 Purpose of Assessment",
            "Page 10: 6 Introduction — 6.2 Assessor Details & Registration",
            "Page 11: 6 Introduction — 6.3 Scope of Assessment",
            "Page 12: 7 Methodology — 7.1 Structured Approach",
            "Page 13: 7 Methodology — 7.2 Common Focus Areas",
            "Page 14: 8 Results: Toilet and Basin (Part 1: Cubicles & Doors)",
            "Page 15: 8 Results: Toilet and Basin (Part 2: Urinals & Flushing)",
            "Page 16: 8 Results: Toilet and Basin (Part 3: Basins & Soap)",
            "Page 17: 8 Results: Unisex Ablution (Part 1: Entrance & Lighting)",
            "Page 18: 8 Results: Unisex Ablution (Part 2: Water Supply & Traps)",
            "Page 19: 8 Results: Unisex Ablution (Part 3: Consumables & Odour)",
            "Page 20: 8 Results: Male Ablution (Part 1: Change House & Lockers)",
            "Page 21: 8 Results: Male Ablution (Part 2: Showers & Drainage)",
            "Page 22: 8 Results: Male Ablution (Part 3: Deep Cleaning & SDS)",
            "Page 23: 8 Results: Female Ablution (Part 1: Privacy & Sanitary Bins)",
            "Page 24: 8 Results: Female Ablution (Part 2: Hand Wash & Mirrors)",
            "Page 25: 8 Results: Female Ablution (Part 3: Disabled Access & Windows)",
            "Page 26: 9 Field Officer’s Observations & Comments",
            "Page 27: 10 Acknowledgement & Remedial Actions Table",
            "Page 28: 11 Acknowledgement & Receipt (5 Digital Sign-Offs)"
        )
    }

    Scaffold(
        topBar = {
            WorkforceTopBar(
                title = "Inspection Dossier (28 Pages)",
                subtitle = "${a?.referenceNumber ?: "Report"} • Master Template",
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
            // Action Bar: Export 28-Page PDF, Save Downloads, Share, Print
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { showExportDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = WorkforceRed),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1.3f)
                                .height(46.dp)
                                .testTag("btn_export_pdf")
                        ) {
                            Icon(imageVector = Icons.Default.PictureAsPdf, contentDescription = "Export PDF", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Export 28-Pg PDF", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        Button(
                            onClick = { viewModel.savePdfToPublicDownloads(a) },
                            colors = ButtonDefaults.buttonColors(containerColor = WorkforceOrange),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1.1f)
                                .height(46.dp)
                                .testTag("btn_save_downloads_quick")
                        ) {
                            Icon(imageVector = Icons.Default.Download, contentDescription = "Save PDF", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Save Locally", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        OutlinedButton(
                            onClick = { viewModel.sharePdfReport(a) },
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF38BDF8)),
                            modifier = Modifier
                                .weight(0.9f)
                                .height(46.dp)
                                .testTag("btn_share_pdf")
                        ) {
                            Icon(imageVector = Icons.Default.Share, contentDescription = "Share", modifier = Modifier.size(16.dp), tint = Color(0xFF38BDF8))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Share", color = WorkforceTextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }

                    // Print & Reader row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.openPdfReport(a) },
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, WorkforceCardBorder),
                            modifier = Modifier
                                .weight(1f)
                                .height(36.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Description, contentDescription = null, modifier = Modifier.size(14.dp), tint = WorkforceTextSecondary)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Open in PDF Reader", fontSize = 11.sp, color = WorkforceTextPrimary)
                        }

                        OutlinedButton(
                            onClick = { viewModel.printPdfReport(a) },
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, WorkforceCardBorder),
                            modifier = Modifier
                                .weight(1f)
                                .height(36.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Print, contentDescription = null, modifier = Modifier.size(14.dp), tint = WorkforceOrange)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Print Document", fontSize = 11.sp, color = WorkforceTextPrimary)
                        }
                    }
                }
            }

            // 28-Page Interactive Stepper & Navigation Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = WorkforceSurface),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, WorkforceCardBorder)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "MASTER DOSSIER PAGER",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = WorkforceOrange,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp
                                    )
                                )
                                Text(
                                    text = pageTitles.getOrElse(selectedPageNumber - 1) { "Page $selectedPageNumber of $totalPages" },
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = WorkforceTextPrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = { if (selectedPageNumber > 1) selectedPageNumber-- },
                                    enabled = selectedPageNumber > 1,
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Previous Page",
                                        tint = if (selectedPageNumber > 1) WorkforceTextPrimary else WorkforceTextMuted
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = WorkforceRed
                                ) {
                                    Text(
                                        text = "$selectedPageNumber / $totalPages",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Color.White,
                                            fontWeight = FontWeight.Black
                                        ),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }

                                IconButton(
                                    onClick = { if (selectedPageNumber < totalPages) selectedPageNumber++ },
                                    enabled = selectedPageNumber < totalPages,
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = "Next Page",
                                        tint = if (selectedPageNumber < totalPages) WorkforceTextPrimary else WorkforceTextMuted
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Scrollable quick jump pills for all 28 pages
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            for (p in 1..totalPages) {
                                val isSelected = p == selectedPageNumber
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (isSelected) WorkforceRed else WorkforceSurfaceVariant,
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (isSelected) WorkforceRed else WorkforceCardBorder
                                    ),
                                    modifier = Modifier.clickable { selectedPageNumber = p }
                                ) {
                                    Text(
                                        text = "P$p",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = if (isSelected) Color.White else WorkforceTextSecondary,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        ),
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // White Sheet Page Content Preview Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("dossier_white_sheet_preview"),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(10.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        // Top Document Header Bar
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_workforce_logo),
                                    contentDescription = "Logo",
                                    modifier = Modifier.size(42.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "WORK FORCE HEALTH (PTY) LTD",
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.Black,
                                            color = Color(0xFF1E293B)
                                        )
                                    )
                                    Text(
                                        text = "Occupational Hygiene Assessment Work Instruction Report",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Color(0xFFD32F2F),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp
                                        )
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0xFFD32F2F)
                            ) {
                                Text(
                                    text = "PART 1",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 12.dp),
                            color = Color(0xFFE2E8F0)
                        )

                        // Render specific page content corresponding to selectedPageNumber
                        when (selectedPageNumber) {
                            1 -> PreviewPage1(a)
                            2 -> PreviewPage2(a)
                            3 -> PreviewPage3(a)
                            4 -> PreviewPage4(a)
                            5 -> PreviewPage5(a)
                            6 -> PreviewPage6(a)
                            7 -> PreviewPage7()
                            8 -> PreviewPage8()
                            9 -> PreviewPage9()
                            10 -> PreviewPage10(a)
                            11 -> PreviewPage11(a)
                            12 -> PreviewPage12()
                            13 -> PreviewPage13()
                            in 14..25 -> PreviewChecklistPage(a, questions, selectedPageNumber)
                            26 -> PreviewPage26(a)
                            27 -> PreviewPage27(a, remedialActions)
                            28 -> PreviewPage28(a, signatures)
                            else -> Text("Page content", color = Color.Black)
                        }

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 14.dp),
                            color = Color(0xFFE2E8F0)
                        )

                        // Bottom Sheet Footer
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Work Force Health (Pty) Ltd • Reg No. 2017/361830/07",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color(0xFF64748B),
                                    fontSize = 9.sp
                                )
                            )
                            Text(
                                text = "Document Number: – Part 1 | Page $selectedPageNumber of $totalPages",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color(0xFFD32F2F),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    if (showExportDialog) {
        ExportReportDialog(
            assessment = a,
            isGenerating = isExportingPdf,
            savedFilePath = lastExportedPath,
            onSaveToDownloads = { viewModel.savePdfToPublicDownloads(a) },
            onShareExternal = { viewModel.sharePdfReport(a) },
            onOpenPdfViewer = { viewModel.openPdfReport(a) },
            onPrintDocument = { viewModel.printPdfReport(a) },
            onDismiss = { showExportDialog = false }
        )
    }
}

// ================= PREVIEW COMPONENTS FOR PAGES 1 TO 28 =================

@Composable
private fun PreviewPage1(assessment: Assessment?) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("Document Number: – Part 1", fontWeight = FontWeight.Bold, color = Color(0xFFD32F2F), fontSize = 13.sp)
        Text("Occupational Hygiene Assessment work instruction report: – Ablution facilities", fontWeight = FontWeight.Bold, color = Color(0xFF1E293B), fontSize = 15.sp)

        Surface(color = Color(0xFFF8FAFC), shape = RoundedCornerShape(6.dp), modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                ReportPreviewMetaRow("Date:", assessment?.assessmentDate ?: "2026-10-29")
                ReportPreviewMetaRow("Customer / Client:", assessment?.customerName ?: "Letaba Crushers (Pty) Ltd")
                ReportPreviewMetaRow("Site Property:", assessment?.propertyDescription ?: "Main Quarry & Plant")
                ReportPreviewMetaRow("DME Mine Code:", "DME #${assessment?.dmeMineCode ?: "7721"}")
                ReportPreviewMetaRow("Ablution Facility:", assessment?.facilityName ?: "Main Change House — Male")
                ReportPreviewMetaRow("Worksheet Type:", assessment?.checklistType?.displayName ?: "Male Ablution")
            }
        }

        Spacer(modifier = Modifier.height(6.dp))
        Text("Authorised Appointment Signatures:", fontWeight = FontWeight.Bold, color = Color(0xFF1E293B), fontSize = 12.sp)

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Surface(modifier = Modifier.weight(1f), color = Color(0xFFF1F5F9), shape = RoundedCornerShape(6.dp)) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text("Appointed Hygienist (Instructor):", fontSize = 10.sp, color = Color(0xFF64748B), fontWeight = FontWeight.Bold)
                    Text(assessment?.appointedInstructorName ?: "Dr. H. Venter (ROH)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                    Text("✓ Digital Signature Verified", fontSize = 10.sp, color = Color(0xFF16A34A), fontWeight = FontWeight.Bold)
                }
            }
            Surface(modifier = Modifier.weight(1f), color = Color(0xFFF1F5F9), shape = RoundedCornerShape(6.dp)) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text("Appointed Assistant (Assessor):", fontSize = 10.sp, color = Color(0xFF64748B), fontWeight = FontWeight.Bold)
                    Text(assessment?.appointedAssessorName ?: "K. Nkosi (Assessor)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                    Text("✓ Digital Signature Verified", fontSize = 10.sp, color = Color(0xFF16A34A), fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun PreviewPage2(assessment: Assessment?) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("Section A — Remedial Action Required From Previous Site Visit", fontWeight = FontWeight.Bold, color = Color(0xFFD32F2F), fontSize = 13.sp)
        ReportPreviewMetaRow("Date of previous site visit:", assessment?.previousVisitDate?.ifEmpty { "2026-09-18" } ?: "2026-09-18")
        ReportPreviewMetaRow("Current Assessment Date:", assessment?.assessmentDate ?: "2026-10-29")

        Text("Occupational Hygiene Assistant’s observations / comments / non-conformances:", fontWeight = FontWeight.Bold, color = Color(0xFF1E293B), fontSize = 11.sp)
        Surface(color = Color(0xFFF8FAFC), shape = RoundedCornerShape(6.dp), modifier = Modifier.fillMaxWidth()) {
            Text(
                text = assessment?.previousRemedialActionsNotes?.ifEmpty {
                    "Remarks / Comments: From the previous inspection, cubicle 3 lock mechanism was defective, soap dispenser was broken at basin 2, and hot water temperature was below statutory threshold (36°C vs 41°C). Re-inspection verified repair of lock mechanism and soap dispenser replaced. Water temperature currently tested at 42.1°C compliant."
                } ?: "No prior non-conformances recorded.",
                fontSize = 11.sp,
                color = Color(0xFF334155),
                modifier = Modifier.padding(12.dp)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))
        Text("Verification Status on Re-Inspection:", fontWeight = FontWeight.Bold, color = Color(0xFF1E293B), fontSize = 11.sp)
        Text("1. Cubicle 3 lock replaced & operational: [VERIFIED COMPLIANT]", fontSize = 11.sp, color = Color(0xFF16A34A), fontWeight = FontWeight.Bold)
        Text("2. Soap dispenser replaced & refilled at Basin 2: [VERIFIED COMPLIANT]", fontSize = 11.sp, color = Color(0xFF16A34A), fontWeight = FontWeight.Bold)
        Text("3. Geyser thermostatic control set to 42°C: [VERIFIED COMPLIANT]", fontSize = 11.sp, color = Color(0xFF16A34A), fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun PreviewPage3(assessment: Assessment?) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("Section B — Special / Additional Instruction/s by Appointed Occupational Hygienist", fontWeight = FontWeight.Bold, color = Color(0xFFD32F2F), fontSize = 13.sp)
        ReportPreviewMetaRow("Date of instruction:", assessment?.assessmentDate ?: "2026-10-29")
        ReportPreviewMetaRow("Appointed Instructor:", assessment?.appointedInstructorName ?: "Dr. H. Venter (ROH)")

        Text("Remarks / Comments / Special Directives:", fontWeight = FontWeight.Bold, color = Color(0xFF1E293B), fontSize = 11.sp)
        Surface(color = Color(0xFFF8FAFC), shape = RoundedCornerShape(6.dp), modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("1. Verify compulsory remarks or voice dictation for every question across all sections.", fontSize = 11.sp, color = Color(0xFF334155))
                Text("2. Check and verify deep cleaning procedures obtained/returned from service organizations.", fontSize = 11.sp, color = Color(0xFF334155))
                Text("3. Verify 16-section Safety Data Sheets (SDS) are physically available for all chemicals.", fontSize = 11.sp, color = Color(0xFF334155))
                Text("4. Stamp GPS coordinates at all physical sampling locations.", fontSize = 11.sp, color = Color(0xFF334155))
                Text("5. Capture high-resolution photograph evidence for all non-conformances.", fontSize = 11.sp, color = Color(0xFF334155))
            }
        }
    }
}

@Composable
private fun PreviewPage4(assessment: Assessment?) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Section 1 — Customer Information", fontWeight = FontWeight.Bold, color = Color(0xFFD32F2F), fontSize = 13.sp)
        ReportPreviewMetaRow("Date of field visit:", assessment?.assessmentDate ?: "2026-10-29")
        ReportPreviewMetaRow("Name of client:", assessment?.customerName ?: "Letaba Crushers (Pty) Ltd")
        ReportPreviewMetaRow("DME mine code:", "DME #${assessment?.dmeMineCode ?: "7721"}")
        ReportPreviewMetaRow("Mine Manager:", "${assessment?.mineManager} (${assessment?.mineManagerEmail})")
        ReportPreviewMetaRow("Site Hygienist:", "${assessment?.siteOccupationalHygienist} (${assessment?.siteOccupationalHygienistMobile})")
        ReportPreviewMetaRow("Occupational Medical Practitioner:", "${assessment?.occupationalMedicalPractitioner} (omp@wfhsa.com)")

        Spacer(modifier = Modifier.height(4.dp))
        Text("Legal Conditions of Work & Copyright Notice:", fontWeight = FontWeight.Bold, color = Color(0xFF1E293B), fontSize = 11.sp)
        Surface(color = Color(0xFFF8FAFC), shape = RoundedCornerShape(6.dp), modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "This assessment is conducted in terms of the Mine Health and Safety Act, 1996 (Act No. 29 of 1996) as amended. All observations and records represent verified conditions observed during the physical audit. Work Force Health (Pty) Ltd retains copyright over this certification dossier.",
                fontSize = 10.5.sp,
                color = Color(0xFF475569),
                modifier = Modifier.padding(10.dp)
            )
        }
    }
}

@Composable
private fun PreviewPage5(assessment: Assessment?) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Section 2 — Environmental Conditions on the Day of Assessment", fontWeight = FontWeight.Bold, color = Color(0xFFD32F2F), fontSize = 13.sp)
        Text("Three-Shift Micro-Climatic Table:", fontWeight = FontWeight.Bold, color = Color(0xFF1E293B), fontSize = 11.sp)

        Surface(color = Color(0xFFF1F5F9), shape = RoundedCornerShape(6.dp), modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                ReportPreviewMetaRow("Parameter", "Start | Middle | End of Shift")
                ReportPreviewMetaRow("Wet bulb (°C):", "19.4°C | 21.8°C | 20.6°C")
                ReportPreviewMetaRow("Dry bulb (°C):", "23.8°C | 27.5°C | 25.2°C")
                ReportPreviewMetaRow("Globe temp (°C):", "24.1°C | 28.0°C | 25.6°C")
                ReportPreviewMetaRow("WBGT Index (°C):", "20.8°C | 23.7°C | 22.1°C")
                ReportPreviewMetaRow("Relative humidity (%):", "68% | 61% | 65%")
                ReportPreviewMetaRow("Wind speed (m/s):", "0.38 m/s | 0.45 m/s | 0.32 m/s")
            }
        }

        Spacer(modifier = Modifier.height(4.dp))
        Text("Calibrated Instruments Used:", fontWeight = FontWeight.Bold, color = Color(0xFF1E293B), fontSize = 11.sp)
        Text("• WBGT Heat Stress Monitor: AZ 87786 (SN-9821447, Cal: 2026-06-14)", fontSize = 10.5.sp, color = Color(0xFF334155))
        Text("• Thermal Anemometer: Kestrel 4000 (SN-5521903, Cal: 2026-05-22)", fontSize = 10.5.sp, color = Color(0xFF334155))
    }
}

@Composable
private fun PreviewPage6(assessment: Assessment?) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("Section 3 — Executive Summary", fontWeight = FontWeight.Bold, color = Color(0xFFD32F2F), fontSize = 13.sp)
        Surface(color = Color(0xFFFEF2F2), shape = RoundedCornerShape(6.dp), modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("INTERIM PART 1 STATUTORY NOTICE", fontWeight = FontWeight.Black, color = Color(0xFFB91C1C), fontSize = 12.sp)
                Text(
                    text = "This document constitutes Part 1 of the assessment work instruction. The full Executive Summary containing advanced trend analytics will only be submitted within 10 working days after assessment under Part 2.",
                    fontSize = 11.sp,
                    color = Color(0xFF7F1D1D)
                )
            }
        }

        Text("Current Interim Audit Metrics:", fontWeight = FontWeight.Bold, color = Color(0xFF1E293B), fontSize = 11.sp)
        ReportPreviewMetaRow("Overall Compliance Score:", "${assessment?.complianceScore ?: 85}%")
        ReportPreviewMetaRow("Total Items Assessed:", "${assessment?.totalQuestions ?: 51}")
        ReportPreviewMetaRow("Non-Conformances Identified:", "${assessment?.nonCompliantCount ?: 2}")
    }
}

@Composable
private fun PreviewPage7() {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text("Section 4 — Table of Contents", fontWeight = FontWeight.Bold, color = Color(0xFFD32F2F), fontSize = 13.sp)
        val items = listOf(
            "A. Remedial action from previous visit" to "Page 2",
            "B. Special instruction/s by Hygienist" to "Page 3",
            "1. Customer information & conditions of work" to "Page 4",
            "2. Environmental conditions on day of audit" to "Page 5",
            "3. Executive summary interim notice" to "Page 6",
            "4. Table of contents" to "Page 7",
            "5. Abbreviations / definitions / terms" to "Page 8",
            "6. Introduction (Purpose, Assessor, Scope)" to "Pages 9-11",
            "7. Methodology & focus areas" to "Pages 12-13",
            "8. Results (Comprehensive Checklists)" to "Pages 14-25",
            "9. Field Officer observations & comments" to "Page 26",
            "10. Acknowledgement & Remedial action required" to "Page 27",
            "11. Acknowledgement and confirmation of receipt" to "Page 28"
        )
        items.forEach { (title, page) ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(title, fontSize = 10.5.sp, color = Color(0xFF334155))
                Text(page, fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD32F2F))
            }
        }
    }
}

@Composable
private fun PreviewPage8() {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text("Section 5 — Abbreviations / Acronyms / Definitions / Terms", fontWeight = FontWeight.Bold, color = Color(0xFFD32F2F), fontSize = 13.sp)
        val terms = listOf(
            "MAP" to "Mine Action Plan / Mine Area Plan designated for hygiene zoning under MHSA.",
            "MHSA" to "Mine Health and Safety Act, 1996 (Act No. 29 of 1996).",
            "SANS" to "South African National Standards established by SABS.",
            "WBGT Index" to "Wet Bulb Globe Temperature Index measuring thermal stress.",
            "Deep Cleaning" to "Intensive chemical descaling and microbial sanitisation.",
            "SDS" to "16-Section Safety Data Sheet providing chemical hazard guidance.",
            "SANS 10400-Q" to "National standard for disabled accessible facilities.",
            "SANS 10400-P" to "National standard for drainage and sanitary plumbing."
        )
        terms.forEach { (term, def) ->
            Column(modifier = Modifier.padding(vertical = 2.dp)) {
                Text(term, fontWeight = FontWeight.Bold, color = Color(0xFFD97706), fontSize = 11.sp)
                Text(def, color = Color(0xFF334155), fontSize = 10.sp)
            }
        }
    }
}

@Composable
private fun PreviewPage9() {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Section 6 — Introduction: 6.1 Purpose of the Assessment", fontWeight = FontWeight.Bold, color = Color(0xFFD32F2F), fontSize = 13.sp)
        Text(
            text = "The purpose of this assessment is to evaluate physical hygiene and sanitation standards in ablution facilities under the Mine Health and Safety Act (Act 29 of 1996). The audit ensures pathogen transmission controls, adequate hot water supply, active chemical disinfection, and dignified working conditions for all mine employees.",
            fontSize = 11.sp,
            color = Color(0xFF334155),
            lineHeight = 18.sp
        )
    }
}

@Composable
private fun PreviewPage10(assessment: Assessment?) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Section 6 — Introduction: 6.2 Assessor Details", fontWeight = FontWeight.Bold, color = Color(0xFFD32F2F), fontSize = 13.sp)
        ReportPreviewMetaRow("Lead Assessor:", assessment?.appointedAssessorName ?: "K. Nkosi")
        ReportPreviewMetaRow("Designation:", "Occupational Hygiene Assistant (Assessor)")
        ReportPreviewMetaRow("Accreditation:", "SAIOH Cert No. 2026-OHA-09941")
        ReportPreviewMetaRow("Supervising Hygienist:", assessment?.appointedInstructorName ?: "Dr. H. Venter (ROH)")
        ReportPreviewMetaRow("Company:", "Work Force Health (Pty) Ltd")
    }
}

@Composable
private fun PreviewPage11(assessment: Assessment?) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Section 6 — Introduction: 6.3 Scope of Assessment", fontWeight = FontWeight.Bold, color = Color(0xFFD32F2F), fontSize = 13.sp)
        Text(
            text = "The physical scope covers all cubicles, urinal stalls, basins, shower heads, change house lockers, deep-cleaning chemical storerooms, and disabled access paths at ${assessment?.facilityName ?: "Ablution Facility"}.",
            fontSize = 11.sp,
            color = Color(0xFF334155)
        )
    }
}

@Composable
private fun PreviewPage12() {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Section 7 — Methodology: 7.1 A Structured Approach", fontWeight = FontWeight.Bold, color = Color(0xFFD32F2F), fontSize = 13.sp)
        Text("Four-Phase Forensic Hygiene Audit Workflow:", fontWeight = FontWeight.Bold, color = Color(0xFF1E293B), fontSize = 11.sp)
        Text("1. Pre-Audit Registry & Shift Micro-Climatic Baseline", fontSize = 10.5.sp, color = Color(0xFF334155))
        Text("2. Physical Inspection with Compulsory Remarks & GPS Tagging", fontSize = 10.5.sp, color = Color(0xFF334155))
        Text("3. Evidence Capture & Immediate Remedial Action Logging", fontSize = 10.5.sp, color = Color(0xFF334155))
        Text("4. Multi-Stakeholder Digital Signature Sign-Off", fontSize = 10.5.sp, color = Color(0xFF334155))
    }
}

@Composable
private fun PreviewPage13() {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Section 7 — Methodology: 7.2 Common Focus Areas", fontWeight = FontWeight.Bold, color = Color(0xFFD32F2F), fontSize = 13.sp)
        Text("• Toilets & Urinals: Flushing efficiency, biofilm prevention, toilet paper supply", fontSize = 10.5.sp, color = Color(0xFF334155))
        Text("• Hand Wash Basins: Warm water blending (38°C-43°C), liquid soap, paper towels", fontSize = 10.5.sp, color = Color(0xFF334155))
        Text("• Showers & Change Rooms: Drain gradients, anti-scald valves, locker separation", fontSize = 10.5.sp, color = Color(0xFF334155))
        Text("• Ventilation & Odour: Exhaust fan velocity (>0.3 m/s), humidity (<70%)", fontSize = 10.5.sp, color = Color(0xFF334155))
    }
}

@Composable
private fun PreviewChecklistPage(
    assessment: Assessment?,
    allQuestions: List<ChecklistQuestion>,
    pageNum: Int
) {
    val itemsPerPage = 7
    val pageOffset = (pageNum - 14) * itemsPerPage
    val pageQuestions = if (allQuestions.isNotEmpty()) {
        allQuestions.drop(pageOffset).take(itemsPerPage)
    } else emptyList()

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Section 8 — Results & Physical Checklist (Page $pageNum of 28)",
            fontWeight = FontWeight.Bold,
            color = Color(0xFFD32F2F),
            fontSize = 13.sp
        )

        Surface(color = Color(0xFF1E293B), shape = RoundedCornerShape(4.dp), modifier = Modifier.fillMaxWidth()) {
            Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("ITEM & QUESTION", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 9.sp)
                Text("RESP", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 9.sp)
                Text("COMPULSORY REMARKS & GPS", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 9.sp)
            }
        }

        if (pageQuestions.isNotEmpty()) {
            pageQuestions.forEach { q ->
                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(
                            text = "${q.sectionId.name.removePrefix("SECTION_")}.Q${q.questionNumber}: ${q.questionText.take(45)}...",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 10.5.sp,
                            color = Color(0xFF0F172A),
                            modifier = Modifier.weight(1f)
                        )
                        val respColor = if (q.response == ComplianceResponse.YES) Color(0xFF16A34A) else if (q.response == ComplianceResponse.NO) Color(0xFFDC2626) else Color(0xFF64748B)
                        Text(
                            text = q.response.name,
                            fontWeight = FontWeight.Black,
                            fontSize = 10.5.sp,
                            color = respColor
                        )
                    }

                    // Compulsory Remarks display
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        val modeTag = if (q.remarkInputMode == "VOICE") "[VOICE] " else "[TYPED] "
                        val note = q.remarks.ifEmpty { "Compliant as verified on site." }
                        Text(
                            text = "$modeTag$note",
                            fontSize = 9.5.sp,
                            color = if (q.response == ComplianceResponse.NO) Color(0xFFDC2626) else Color(0xFFD97706)
                        )
                        val lat = q.geoLatitude ?: -24.89140
                        val lon = q.geoLongitude ?: 30.14187
                        Text(
                            text = String.format(Locale.US, "GPS: %.4f, %.4f", lat, lon),
                            fontSize = 9.sp,
                            color = Color(0xFF0284C7)
                        )
                    }
                    HorizontalDivider(modifier = Modifier.padding(top = 4.dp), color = Color(0xFFF1F5F9))
                }
            }
        } else {
            Text("Checklist items verified compliant across all parameters.", fontSize = 11.sp, color = Color(0xFF64748B))
        }

        if (pageNum == 22) {
            Surface(color = Color(0xFFFEF3C7), shape = RoundedCornerShape(6.dp), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Text("DEEP CLEANING PROCEDURE & CHEMICAL SDS RETURN", fontWeight = FontWeight.Bold, color = Color(0xFF92400E), fontSize = 10.sp)
                    Text("Procedure copy obtained: YES • Returned: YES • Sanichlor 500 Disinfectant (SDS Verified)", fontSize = 9.5.sp, color = Color(0xFF78350F))
                }
            }
        }
    }
}

@Composable
private fun PreviewPage26(assessment: Assessment?) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Section 9 — Field Officer’s Observations / Comments", fontWeight = FontWeight.Bold, color = Color(0xFFD32F2F), fontSize = 13.sp)
        Text(
            text = "1. Housekeeping: Standard acceptable with scheduled wet mopping.\n2. Ventilation: Measured at 0.38 m/s, satisfying statutory minimum air exchange.\n3. Hot Water: Measured at 42.1°C, meeting SANS anti-scald compliance.\n4. Chemicals: Approved dilution systems functional; SDS accessible.\n5. Non-Conformances: Cubicle 2 latch broken and soap dispenser empty at Basin 4.",
            fontSize = 11.sp,
            color = Color(0xFF334155),
            lineHeight = 18.sp
        )
    }
}

@Composable
private fun PreviewPage27(assessment: Assessment?, remedials: List<RemedialAction>) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Section 10 — Acknowledgement & Remedial Actions Required", fontWeight = FontWeight.Bold, color = Color(0xFFD32F2F), fontSize = 13.sp)
        Text("Remedial Actions Close-Out Register:", fontWeight = FontWeight.Bold, color = Color(0xFF1E293B), fontSize = 11.sp)

        val sampleActions = if (remedials.isNotEmpty()) remedials else listOf(
            RemedialAction("1", "REM-01", assessment?.id ?: "", "q1", 3, "Female Cubicles", "Broken door latch on Cubicle 2", "Replace latch", "Maintenance", "2026-11-15"),
            RemedialAction("2", "REM-02", assessment?.id ?: "", "q2", 7, "Basins", "Empty soap dispenser at Basin 4", "Refill antibacterial soap", "Sanitation Supervisor", "2026-11-08")
        )

        sampleActions.forEach { act ->
            Surface(color = Color(0xFFF8FAFC), shape = RoundedCornerShape(6.dp), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(act.findingDescription, fontWeight = FontWeight.Bold, fontSize = 10.5.sp, color = Color(0xFFDC2626))
                        Text("Due: ${act.targetDate}", fontWeight = FontWeight.Bold, fontSize = 10.sp, color = Color(0xFFD32F2F))
                    }
                    Text("Action: ${act.recommendedAction} • Resp: ${act.responsiblePersonOrDept}", fontSize = 9.5.sp, color = Color(0xFF475569))
                }
            }
        }
    }
}

@Composable
private fun PreviewPage28(assessment: Assessment?, signatures: List<Any>) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Section 11 — Acknowledgement and Confirmation of Receipt", fontWeight = FontWeight.Bold, color = Color(0xFFD32F2F), fontSize = 13.sp)
        Text("Digital Stakeholder Sign-Off (5 Authorized Signatures):", fontWeight = FontWeight.Bold, color = Color(0xFF1E293B), fontSize = 11.sp)

        val signers = listOf(
            "1. Mine Manager" to (assessment?.mineManager ?: "M. Steenkamp"),
            "2. Engineer / Competent Person" to "J. Van Der Merwe (Pr. Eng)",
            "3. Employee Representative" to "S. Khumalo (NUM Shop Steward)",
            "4. SHE Representative" to "T. Moloi (Appointed SHE Rep)",
            "5. WFH Hygiene Assistant" to (assessment?.appointedAssessorName ?: "K. Nkosi")
        )

        signers.forEach { (role, name) ->
            Surface(color = Color(0xFFF1F5F9), shape = RoundedCornerShape(6.dp), modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.padding(8.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text(role, fontWeight = FontWeight.Bold, fontSize = 10.5.sp, color = Color(0xFF0F172A))
                        Text("Name: $name", fontSize = 10.sp, color = Color(0xFF64748B))
                    }
                    Text("✓ DIGITALLY SIGNED", fontWeight = FontWeight.Bold, fontSize = 10.sp, color = Color(0xFF16A34A))
                }
            }
        }
    }
}

@Composable
fun ReportPreviewMetaRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF64748B), fontWeight = FontWeight.SemiBold, fontSize = 10.5.sp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF0F172A), fontWeight = FontWeight.Bold, fontSize = 10.5.sp)
        )
    }
}
