package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Assessment
import com.example.data.model.AssessmentStatus
import com.example.data.model.UserRole
import com.example.ui.components.StatusBadge
import com.example.ui.components.WorkforceTopBar
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
fun WebPortalPreviewScreen(
    viewModel: WorkforceViewModel,
    onBack: () -> Unit
) {
    val syncStatus by viewModel.syncManager.syncState.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val assessments by viewModel.allAssessments.collectAsState()

    var reviewDialogAssessment by remember { mutableStateOf<Assessment?>(null) }
    var reviewComments by remember { mutableStateOf("All sections and chemical SDS registers inspected and verified in accordance with MHSA regulations.") }

    Scaffold(
        topBar = {
            WorkforceTopBar(
                title = "Management & Review Portal",
                subtitle = "Occupational Hygienist & Mine Admin Interface",
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Surface(
                    color = WorkforceSurfaceVariant,
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, WorkforceCardBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = Color(0xFFA855F7),
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Web Portal Management Engine",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = WorkforceTextPrimary)
                            )
                            Text(
                                text = "Authorised users can review submitted fieldwork, evaluate findings, approve reports, and manage checklist versions.",
                                style = MaterialTheme.typography.bodySmall.copy(color = WorkforceTextSecondary)
                            )
                        }
                    }
                }
            }

            // MobiWorkforce WordPress Theme & Plugins Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = WorkforceSurface),
                    shape = RoundedCornerShape(12.dp),
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
                                    text = "PORTAL WFHSA THEME & ELEMENTOR KIT",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = WorkforceOrange,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp
                                    )
                                )
                                Text(
                                    text = "Target Endpoint: https://wfhsa.com/new/",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        color = WorkforceTextPrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(999.dp),
                                color = ComplianceGreen.copy(alpha = 0.15f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, ComplianceGreen.copy(alpha = 0.5f))
                            ) {
                                Text(
                                    text = "v1.4.0 ELEMENTOR KIT",
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = ComplianceGreen,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Theme: portalwfhsa-theme.zip • Elementor Kit: portalwfhsa-elementor-kit.zip\nWidgets: portalwfhsa-elementor-widgets.zip • Master: portalwfhsa-complete-suite.zip\nModular Plugins: portalwfhsa-inspections, portalwfhsa-sync, portalwfhsa-user-roles, portalwfhsa-location-selector, portalwfhsa-reports-pdf, portalwfhsa-complete-package.zip",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = WorkforceTextSecondary,
                                fontSize = 11.sp,
                                lineHeight = 16.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = { viewModel.syncCloud() },
                            colors = ButtonDefaults.buttonColors(containerColor = WorkforceOrange),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Test Live Connection to https://wfhsa.com/new/",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Assessments Awaiting Review & Approval (${assessments.size})",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = WorkforceTextPrimary)
                )
            }

            items(assessments) { a ->
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
                                text = a.referenceNumber,
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = WorkforceOrange)
                            )
                            StatusBadge(status = a.status)
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = a.facilityName,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = WorkforceTextPrimary)
                        )
                        Text(
                            text = "${a.customerName} • ${a.siteName}",
                            style = MaterialTheme.typography.bodySmall.copy(color = WorkforceTextSecondary)
                        )
                        Text(
                            text = "Assessor: ${a.assessorName} (${a.assessorRole}) • Score: ${a.complianceScore}%",
                            style = MaterialTheme.typography.bodySmall.copy(color = WorkforceTextMuted, fontSize = 11.sp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    viewModel.loadAssessment(a.id)
                                    viewModel.navigateTo(AppScreen.ReportPreview(a.id))
                                },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Inspect Evidence", color = WorkforceTextPrimary, style = MaterialTheme.typography.labelSmall)
                            }

                            Button(
                                onClick = {
                                    viewModel.loadAssessment(a.id)
                                    reviewDialogAssessment = a
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFA855F7)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(imageVector = Icons.Default.RateReview, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Review & Sign", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
            }
        }
    }

    // Dialog for Hygienist Review & Approval
    if (reviewDialogAssessment != null) {
        val a = reviewDialogAssessment!!
        AlertDialog(
            onDismissRequest = { reviewDialogAssessment = null },
            title = {
                Text(
                    text = "Occupational Hygienist Review (${a.referenceNumber})",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = WorkforceTextPrimary)
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Facility: ${a.facilityName}\nCustomer: ${a.customerName}\nAssessed Score: ${a.complianceScore}%",
                        style = MaterialTheme.typography.bodySmall.copy(color = WorkforceTextSecondary)
                    )

                    OutlinedTextField(
                        value = reviewComments,
                        onValueChange = { reviewComments = it },
                        label = { Text("Professional Evaluation & Recommendations") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 4
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.approveAssessment(reviewComments)
                        reviewDialogAssessment = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ComplianceGreen)
                ) {
                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Approve & Sign-Off")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        viewModel.returnAssessment(reviewComments)
                        reviewDialogAssessment = null
                    }
                ) {
                    Text("Return for Correction", color = ComplianceRed)
                }
            },
            containerColor = WorkforceSurface
        )
    }
}
