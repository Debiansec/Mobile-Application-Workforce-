package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ChemicalRecord
import com.example.data.model.CleaningProcedureStatus
import com.example.data.model.CleaningType
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
import com.example.ui.viewmodel.WorkforceViewModel

@Composable
fun ChemicalRegisterScreen(
    viewModel: WorkforceViewModel,
    assessmentId: String,
    onBack: () -> Unit
) {
    val syncStatus by viewModel.syncManager.syncState.collectAsState()
    val chemicals by viewModel.chemicalsList.collectAsState()
    val deepProcedure by viewModel.deepProcedureStatus.collectAsState()
    val routineProcedure by viewModel.routineProcedureStatus.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }
    val currentCleaningType = if (selectedTab == 0) CleaningType.DEEP_CLEANING else CleaningType.ROUTINE_CLEANING
    val currentProcedure = if (selectedTab == 0) deepProcedure else routineProcedure
    val filteredChemicals = chemicals.filter { it.cleaningType == currentCleaningType }

    // Dialog for adding chemical
    var showAddDialog by remember { mutableStateOf(false) }
    var newChemicalName by remember { mutableStateOf("") }
    var newSupplier by remember { mutableStateOf("") }
    var newSdsReturned by remember { mutableStateOf(true) }
    var newUsageNotes by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            WorkforceTopBar(
                title = "Chemicals & SDS Register",
                subtitle = "Section F Question 20 & 22 Compliance",
                canNavigateBack = true,
                onNavigateBack = onBack,
                syncStatus = syncStatus
            )
        },
        containerColor = WorkforceDarkBg
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Tabs: Deep-Cleaning vs Routine-Cleaning
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = WorkforceSurface,
                contentColor = WorkforceTextPrimary,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = WorkforceRed
                    )
                }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            text = "DEEP-CLEANING (Q20)",
                            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTab == 0) WorkforceRed else WorkforceTextSecondary
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            text = "ROUTINE CLEANING (Q22)",
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTab == 1) WorkforceRed else WorkforceTextSecondary
                        )
                    }
                )
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Section Header info
                item {
                    Text(
                        text = if (selectedTab == 0)
                            "Question 20: Full details of deep-cleaning methods (procedure) & chemicals (with SDS) used by person/organisation conducting deep cleaning."
                        else
                            "Question 22: Full list of chemicals used for daily/weekly/monthly cleaning (excluding deep cleaning) and safety data sheets.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = WorkforceTextPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }

                // Procedure & Documentation Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = WorkforceSurface),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, WorkforceCardBorder)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "PROCEDURE DOCUMENTATION VERIFICATION",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = WorkforceOrange,
                                    letterSpacing = 0.5.sp
                                )
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            // Copy of procedure obtained
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Copy of procedure obtained",
                                        style = MaterialTheme.typography.bodyMedium.copy(color = WorkforceTextPrimary, fontWeight = FontWeight.Medium)
                                    )
                                    Text(
                                        text = "Written standard operating procedure on file",
                                        style = MaterialTheme.typography.bodySmall.copy(color = WorkforceTextSecondary, fontSize = 11.sp)
                                    )
                                }
                                Switch(
                                    checked = currentProcedure.copyOfProcedureObtained,
                                    onCheckedChange = { checked ->
                                        viewModel.updateProcedureStatus(
                                            cleaningType = currentCleaningType,
                                            obtained = checked,
                                            returned = currentProcedure.copyOfProcedureReturned,
                                            contractor = currentProcedure.contractorOrServiceOrg,
                                            remarks = currentProcedure.remarks
                                        )
                                    },
                                    colors = SwitchDefaults.colors(checkedThumbColor = ComplianceGreen, checkedTrackColor = ComplianceGreen.copy(alpha = 0.4f))
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Copy of procedure returned
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Copy of procedure returned",
                                        style = MaterialTheme.typography.bodyMedium.copy(color = WorkforceTextPrimary, fontWeight = FontWeight.Medium)
                                    )
                                    Text(
                                        text = "Audited document returned to hygiene custodian",
                                        style = MaterialTheme.typography.bodySmall.copy(color = WorkforceTextSecondary, fontSize = 11.sp)
                                    )
                                }
                                Switch(
                                    checked = currentProcedure.copyOfProcedureReturned,
                                    onCheckedChange = { checked ->
                                        viewModel.updateProcedureStatus(
                                            cleaningType = currentCleaningType,
                                            obtained = currentProcedure.copyOfProcedureObtained,
                                            returned = checked,
                                            contractor = currentProcedure.contractorOrServiceOrg,
                                            remarks = currentProcedure.remarks
                                        )
                                    },
                                    colors = SwitchDefaults.colors(checkedThumbColor = ComplianceGreen, checkedTrackColor = ComplianceGreen.copy(alpha = 0.4f))
                                )
                            }
                        }
                    }
                }

                // Chemical Register Header
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Chemical Register (${filteredChemicals.size} Products)",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = WorkforceTextPrimary
                            )
                        )
                        Button(
                            onClick = {
                                newChemicalName = ""
                                newSupplier = ""
                                newUsageNotes = ""
                                newSdsReturned = true
                                showAddDialog = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = WorkforceRed),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .height(38.dp)
                                .testTag("btn_add_chemical")
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add Chemical", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }

                if (filteredChemicals.isEmpty()) {
                    item {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            color = WorkforceSurface,
                            border = androidx.compose.foundation.BorderStroke(1.dp, WorkforceCardBorder)
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "No chemicals logged in this register yet.",
                                    style = MaterialTheme.typography.bodyMedium.copy(color = WorkforceTextSecondary)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Tap 'Add Chemical' to log product name, manufacturer, and verify Safety Data Sheet (SDS).",
                                    style = MaterialTheme.typography.bodySmall.copy(color = WorkforceTextMuted),
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                } else {
                    items(filteredChemicals) { chem ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = WorkforceSurface),
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, WorkforceCardBorder)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = chem.chemicalName,
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = WorkforceTextPrimary
                                            )
                                        )
                                        Text(
                                            text = "Manufacturer / Supplier: ${chem.manufacturerOrSupplier.ifEmpty { "Not specified" }}",
                                            style = MaterialTheme.typography.bodySmall.copy(color = WorkforceTextSecondary)
                                        )
                                    }

                                    // SDS returned chip
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (chem.sdsReturned) ComplianceGreen.copy(alpha = 0.2f) else ComplianceRed.copy(alpha = 0.2f),
                                        border = androidx.compose.foundation.BorderStroke(
                                            1.dp,
                                            if (chem.sdsReturned) ComplianceGreen else ComplianceRed
                                        )
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = if (chem.sdsReturned) Icons.Default.Check else Icons.Default.Clear,
                                                contentDescription = null,
                                                tint = if (chem.sdsReturned) ComplianceGreen else ComplianceRed,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = if (chem.sdsReturned) "SDS AVAILABLE" else "SDS MISSING",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (chem.sdsReturned) ComplianceGreen else ComplianceRed,
                                                    fontSize = 10.sp
                                                )
                                            )
                                        }
                                    }
                                }

                                if (chem.usageNotes.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Usage: ${chem.usageNotes}",
                                        style = MaterialTheme.typography.bodySmall.copy(color = WorkforceTextMuted)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Dialog: Add chemical
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = {
                Text(
                    text = "Add Chemical Product",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = WorkforceTextPrimary)
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = newChemicalName,
                        onValueChange = { newChemicalName = it },
                        label = { Text("Chemical / Product Name") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = newSupplier,
                        onValueChange = { newSupplier = it },
                        label = { Text("Manufacturer / Supplier") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = newUsageNotes,
                        onValueChange = { newUsageNotes = it },
                        label = { Text("Dilution / Usage Procedure Notes") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Safety Data Sheet (SDS) Returned & Attached",
                            style = MaterialTheme.typography.bodySmall.copy(color = WorkforceTextPrimary),
                            modifier = Modifier.weight(1f)
                        )
                        Switch(
                            checked = newSdsReturned,
                            onCheckedChange = { newSdsReturned = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = ComplianceGreen)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newChemicalName.isNotBlank()) {
                            viewModel.addChemical(
                                cleaningType = currentCleaningType,
                                name = newChemicalName,
                                supplier = newSupplier,
                                sdsReturned = newSdsReturned,
                                notes = newUsageNotes
                            )
                            showAddDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = WorkforceRed)
                ) {
                    Text("Save Chemical")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancel", color = WorkforceTextSecondary)
                }
            },
            containerColor = WorkforceSurface
        )
    }
}
