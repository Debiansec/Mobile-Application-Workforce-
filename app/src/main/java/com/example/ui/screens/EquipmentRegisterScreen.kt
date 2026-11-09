package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Equipment
import com.example.data.model.EquipmentStatus
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

@Composable
fun EquipmentRegisterScreen(
    viewModel: WorkforceViewModel,
    onBack: () -> Unit
) {
    val syncStatus by viewModel.syncManager.syncState.collectAsState()
    val equipmentList = viewModel.equipmentList

    Scaffold(
        topBar = {
            WorkforceTopBar(
                title = "Equipment & Calibration Register",
                subtitle = "Occupational Hygiene Instrumentation",
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
            item {
                Text(
                    text = "SANAS accredited calibration register for sampling loggers, anemometers, and sound meters.",
                    style = MaterialTheme.typography.bodyMedium.copy(color = WorkforceTextSecondary)
                )
            }

            items(equipmentList) { eq ->
                val statusColor = Color(eq.status.colorHex)

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
                                text = eq.assetTag,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = WorkforceOrange
                                )
                            )

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = statusColor.copy(alpha = 0.18f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, statusColor.copy(alpha = 0.5f))
                            ) {
                                Text(
                                    text = eq.status.label.uppercase(),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = statusColor,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    ),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = eq.name,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = WorkforceTextPrimary
                            )
                        )

                        Text(
                            text = "${eq.manufacturer} • Model: ${eq.model}",
                            style = MaterialTheme.typography.bodySmall.copy(color = WorkforceTextSecondary)
                        )
                        Text(
                            text = "Serial No: ${eq.serialNumber}",
                            style = MaterialTheme.typography.bodySmall.copy(color = WorkforceTextMuted, fontSize = 11.sp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Surface(
                            color = WorkforceSurfaceVariant,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("Last Calibrated", style = MaterialTheme.typography.labelSmall.copy(color = WorkforceTextMuted))
                                    Text(eq.lastCalibrationDate, style = MaterialTheme.typography.bodySmall.copy(color = WorkforceTextPrimary, fontWeight = FontWeight.Bold))
                                }
                                Column {
                                    Text("Next Due Date", style = MaterialTheme.typography.labelSmall.copy(color = WorkforceTextMuted))
                                    Text(eq.calibrationDueDate, style = MaterialTheme.typography.bodySmall.copy(color = statusColor, fontWeight = FontWeight.Bold))
                                }
                                Column {
                                    Text("Cert Ref", style = MaterialTheme.typography.labelSmall.copy(color = WorkforceTextMuted))
                                    Text(eq.certificateRef, style = MaterialTheme.typography.bodySmall.copy(color = WorkforceTextPrimary, fontWeight = FontWeight.Bold))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
