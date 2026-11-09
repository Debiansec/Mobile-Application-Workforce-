package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Domain
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import com.example.ui.viewmodel.AppScreen
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
import com.example.data.model.Customer
import com.example.data.model.Facility
import com.example.data.model.Site
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

@Composable
fun CustomerSiteSelectionScreen(
    viewModel: WorkforceViewModel,
    onBack: () -> Unit
) {
    val syncStatus by viewModel.syncManager.syncState.collectAsState()
    val customers by viewModel.customersList.collectAsState()
    val sites by viewModel.sitesList.collectAsState()
    val facilities by viewModel.facilitiesList.collectAsState()

    var selectedCustomer by remember(customers) { mutableStateOf<Customer?>(customers.firstOrNull()) }
    val filteredSites = remember(selectedCustomer, sites) {
        sites.filter { it.customerId == selectedCustomer?.id }
    }
    var selectedSite by remember(filteredSites) { mutableStateOf<Site?>(filteredSites.firstOrNull()) }
    val filteredFacilities = remember(selectedSite, facilities) {
        facilities.filter { it.siteId == selectedSite?.id }
    }
    var selectedFacility by remember(filteredFacilities) { mutableStateOf<Facility?>(filteredFacilities.firstOrNull()) }

    Scaffold(
        topBar = {
            WorkforceTopBar(
                title = "Select Inspection Location",
                subtitle = "Customer, Mine Site & Ablution Facility",
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
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // STEP 1: CUSTOMER
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "1. SELECT AUTHORIZED CUSTOMER",
                        style = MaterialTheme.typography.labelLarge.copy(
                            color = WorkforceOrange,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    )
                    OutlinedButton(
                        onClick = { viewModel.navigateTo(AppScreen.CustomerManagement) },
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = WorkforceOrange),
                        border = androidx.compose.foundation.BorderStroke(1.dp, WorkforceOrange.copy(alpha = 0.5f))
                    ) {
                        Text("Manage Accounts", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    customers.forEach { customer ->
                        val isSelected = customer.id == selectedCustomer?.id
                        SelectableCard(
                            title = customer.name,
                            subtitle = "Code: ${customer.companyCode} • Contact: ${customer.contactPerson}",
                            isSelected = isSelected,
                            icon = Icons.Default.Business,
                            onClick = { selectedCustomer = customer }
                        )
                    }
                }
            }

            // STEP 2: SITE / MINE
            item {
                Text(
                    text = "2. SELECT SITE & PROPERTY",
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = WorkforceOrange,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                if (filteredSites.isEmpty()) {
                    Text("No sites configured for this customer", color = WorkforceTextMuted)
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        filteredSites.forEach { site ->
                            val isSelected = site.id == selectedSite?.id
                            SelectableCard(
                                title = "${site.siteName} (Mine #${site.mineCode})",
                                subtitle = "${site.propertyDescription}, ${site.location}, ${site.province}",
                                details = "Mgr: ${site.mineManager} | Hygienist: ${site.siteOccupationalHygienist}",
                                isSelected = isSelected,
                                icon = Icons.Default.Domain,
                                onClick = { selectedSite = site }
                            )
                        }
                    }
                }
            }

            // STEP 3: FACILITY / MODULE
            item {
                Text(
                    text = "3. SELECT ABLUTION FACILITY",
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = WorkforceOrange,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                if (filteredFacilities.isEmpty()) {
                    Text("No facilities registered for this site", color = WorkforceTextMuted)
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        filteredFacilities.forEach { facility ->
                            val isSelected = facility.id == selectedFacility?.id
                            SelectableCard(
                                title = facility.name,
                                subtitle = facility.buildingDescription,
                                details = "Est. Users: ~${facility.estimatedUsers} employees • Last Visit: ${facility.lastAssessmentDate}",
                                isSelected = isSelected,
                                icon = Icons.Default.MeetingRoom,
                                onClick = { selectedFacility = facility }
                            )
                        }
                    }
                }
            }

            // START BUTTON
            item {
                Spacer(modifier = Modifier.height(10.dp))
                val canStart = selectedCustomer != null && selectedSite != null && selectedFacility != null
                Button(
                    onClick = {
                        if (canStart) {
                            viewModel.startNewAssessment(
                                customer = selectedCustomer!!,
                                site = selectedSite!!,
                                facility = selectedFacility!!
                            )
                        }
                    },
                    enabled = canStart,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .testTag("btn_launch_inspection_checklist"),
                    colors = ButtonDefaults.buttonColors(containerColor = WorkforceRed),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "LAUNCH INSPECTION CHECKLIST",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun SelectableCard(
    title: String,
    subtitle: String,
    details: String? = null,
    isSelected: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) WorkforceSurfaceVariant else WorkforceSurface
        ),
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) WorkforceOrange else WorkforceCardBorder
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (isSelected) WorkforceOrange.copy(alpha = 0.2f) else WorkforceSurfaceVariant,
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (isSelected) WorkforceOrange else WorkforceTextSecondary,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = WorkforceTextPrimary
                    )
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(color = WorkforceTextSecondary)
                )
                if (details != null) {
                    Text(
                        text = details,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = WorkforceTextMuted,
                            fontSize = 11.sp
                        )
                    )
                }
            }

            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = WorkforceOrange,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}
