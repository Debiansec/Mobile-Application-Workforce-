package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Draw
import androidx.compose.material.icons.filled.Person
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
import com.example.data.model.DigitalSignature
import com.example.ui.components.DigitalSignatureCanvas
import com.example.ui.components.WorkforceTopBar
import com.example.ui.theme.ComplianceGreen
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

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SignatureScreen(
    viewModel: WorkforceViewModel,
    assessmentId: String,
    onBack: () -> Unit
) {
    val syncStatus by viewModel.syncManager.syncState.collectAsState()
    val signatures by viewModel.signaturesList.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    var signerName by remember { mutableStateOf(currentUser.name) }
    var signerRole by remember { mutableStateOf("Occupational Hygiene Field Assessor") }
    var signerDesignation by remember { mutableStateOf(currentUser.registrationNumber) }
    var isSigningActive by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            WorkforceTopBar(
                title = "Digital Signatures & Sign-Off",
                subtitle = "Assessor & Client Representative Attestation",
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
            // Legal / POPIA / Attestation Statement
            item {
                Surface(
                    color = WorkforceSurfaceVariant,
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, WorkforceCardBorder)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "ATTESTATION & DECLARATION",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = WorkforceOrange,
                                letterSpacing = 0.5.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "I hereby confirm that this occupational hygiene inspection was conducted in accordance with South African Mine Health and Safety Act (MHSA) guidelines and WORKFORCE HEALTH SA standard operating procedures. All recorded observations and remarks represent actual facility conditions.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = WorkforceTextSecondary,
                                lineHeight = 18.sp
                            )
                        )
                    }
                }
            }

            // Existing Captured Signatures
            item {
                Text(
                    text = "Captured Attestations (${signatures.size})",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = WorkforceTextPrimary
                    )
                )
            }

            if (signatures.isEmpty()) {
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        color = WorkforceSurface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, WorkforceCardBorder)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "No signatures recorded for this assessment yet.",
                                style = MaterialTheme.typography.bodyMedium.copy(color = WorkforceTextSecondary)
                            )
                        }
                    }
                }
            } else {
                items(signatures) { sig ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = WorkforceSurface),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ComplianceGreen.copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = ComplianceGreen,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = sig.signerName,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = WorkforceTextPrimary
                                    )
                                )
                                Text(
                                    text = "${sig.signerRole} • ${sig.signerDesignation}",
                                    style = MaterialTheme.typography.bodySmall.copy(color = WorkforceOrange)
                                )
                                Text(
                                    text = "Signed: ${sig.signedDate} at ${sig.signedTime} (Electronic Verification Stamped)",
                                    style = MaterialTheme.typography.bodySmall.copy(color = WorkforceTextMuted, fontSize = 11.sp)
                                )
                            }
                        }
                    }
                }
            }

            // Signature Input Form & Canvas
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = WorkforceSurface),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, WorkforceCardBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Capture Signatory",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = WorkforceTextPrimary
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Select Signatory Role (PDF Section 11 & Title Page):",
                            style = MaterialTheme.typography.bodySmall.copy(color = WorkforceTextSecondary, fontSize = 11.sp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        // Multi-row Role Chips
                        val statutoryRoles = listOf(
                            "WFH Occupational Hygiene Assistant" to ("Mr R R M Taukobong" to "Assessor (Legal Appointee)"),
                            "Appointed Occupational Hygienist" to ("R. Steyn" to "ROH 0482 (Instructor)"),
                            "Mine Manager" to ("G. van der Merwe" to "DME Code 10383"),
                            "Engineer / Competent Person" to ("J. Smit" to "Pr. Eng 201104"),
                            "Employee Representative" to ("T. Ndlovu" to "NUM Full-Time Branch"),
                            "SHE Representative" to ("M. Khumalo" to "Occupational Safety Officer")
                        )

                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            statutoryRoles.forEach { (role, defaults) ->
                                val isSelected = signerRole == role
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSelected) WorkforceOrange else WorkforceSurfaceVariant,
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (isSelected) WorkforceOrange else WorkforceCardBorder
                                    ),
                                    modifier = Modifier.clickable {
                                        signerRole = role
                                        signerName = defaults.first
                                        signerDesignation = defaults.second
                                    }
                                ) {
                                    Text(
                                        text = role,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = if (isSelected) Color.White else WorkforceTextPrimary,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            fontSize = 11.sp
                                        ),
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = signerName,
                            onValueChange = { signerName = it },
                            label = { Text("Signer Full Name") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = signerDesignation,
                            onValueChange = { signerDesignation = it },
                            label = { Text("Registration Number / Designation") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "Draw Electronic Signature:",
                            style = MaterialTheme.typography.bodySmall.copy(color = WorkforceTextSecondary, fontWeight = FontWeight.SemiBold)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Interactive Finger/Stylus signature drawing canvas
                        DigitalSignatureCanvas(
                            modifier = Modifier.fillMaxWidth(),
                            onSignatureCaptured = { strokeData ->
                                viewModel.saveSignature(
                                    role = signerRole,
                                    name = signerName,
                                    designation = signerDesignation,
                                    signatureData = strokeData
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}
