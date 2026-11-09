package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Domain
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ChecklistType
import com.example.data.model.Customer
import com.example.data.model.Facility
import com.example.data.model.Site
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
import java.util.UUID

@Composable
fun CustomerSiteManagementScreen(
    viewModel: WorkforceViewModel,
    onBack: () -> Unit
) {
    val syncStatus by viewModel.syncManager.syncState.collectAsState()
    val customers by viewModel.customersList.collectAsState()
    val sites by viewModel.sitesList.collectAsState()
    val facilities by viewModel.facilitiesList.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Customers, 1: Mine Sites, 2: Ablution Facilities
    var searchQuery by remember { mutableStateOf("") }

    // Dialog state
    var editingCustomer by remember { mutableStateOf<Customer?>(null) }
    var isNewCustomer by remember { mutableStateOf(false) }

    var editingSite by remember { mutableStateOf<Site?>(null) }
    var isNewSite by remember { mutableStateOf(false) }

    var editingFacility by remember { mutableStateOf<Facility?>(null) }
    var isNewFacility by remember { mutableStateOf(false) }

    var deletingCustomer by remember { mutableStateOf<Customer?>(null) }
    var deletingSite by remember { mutableStateOf<Site?>(null) }
    var deletingFacility by remember { mutableStateOf<Facility?>(null) }

    Scaffold(
        topBar = {
            WorkforceTopBar(
                title = "Customer Accounts & Properties",
                subtitle = "Legal Entities, Mine Sites & Ablution Facilities",
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
            // Segmented Tab Bar
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(10.dp),
                color = WorkforceSurfaceVariant,
                border = androidx.compose.foundation.BorderStroke(1.dp, WorkforceCardBorder)
            ) {
                Row(modifier = Modifier.padding(3.dp)) {
                    listOf("CUSTOMERS (${customers.size})", "MINE SITES (${sites.size})", "ABLUTIONS (${facilities.size})").forEachIndexed { idx, title ->
                        val isSelected = selectedTab == idx
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) WorkforceRed else Color.Transparent,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedTab = idx }
                                .padding(vertical = 8.dp)
                                .testTag("tab_customer_mgmt_$idx")
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

            // Search Bar & Add Button Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            when (selectedTab) {
                                0 -> "Search company, VAT, CIPC..."
                                1 -> "Search mine, farm, permit, location..."
                                else -> "Search ablution building..."
                            },
                            fontSize = 12.sp,
                            color = WorkforceTextMuted
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = WorkforceTextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("input_search_customers"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = WorkforceSurface,
                        unfocusedContainerColor = WorkforceSurface,
                        focusedBorderColor = WorkforceOrange,
                        unfocusedBorderColor = WorkforceCardBorder,
                        focusedTextColor = WorkforceTextPrimary,
                        unfocusedTextColor = WorkforceTextPrimary
                    ),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true
                )

                Button(
                    onClick = {
                        when (selectedTab) {
                            0 -> {
                                editingCustomer = Customer(
                                    id = "cust_${UUID.randomUUID().toString().take(6)}",
                                    name = "",
                                    tradeName = "",
                                    companyCode = "CUST-${(100..999).random()}",
                                    contactPerson = "",
                                    email = "",
                                    phone = ""
                                )
                                isNewCustomer = true
                            }
                            1 -> {
                                val defaultCustId = customers.firstOrNull()?.id ?: ""
                                editingSite = Site(
                                    id = "site_${UUID.randomUUID().toString().take(6)}",
                                    customerId = defaultCustId,
                                    siteName = "",
                                    propertyDescription = "",
                                    location = "",
                                    province = "Limpopo Province",
                                    mineCode = "${(10000..99999).random()}",
                                    mineManager = "",
                                    mineManagerContact = "",
                                    siteOccupationalHygienist = "",
                                    occupationalMedicalPractitioner = ""
                                )
                                isNewSite = true
                            }
                            else -> {
                                val defaultSiteId = sites.firstOrNull()?.id ?: ""
                                editingFacility = Facility(
                                    id = "fac_${UUID.randomUUID().toString().take(6)}",
                                    siteId = defaultSiteId,
                                    name = "",
                                    checklistType = ChecklistType.MALE_ABLUTION,
                                    buildingDescription = ""
                                )
                                isNewFacility = true
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = WorkforceOrange),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .height(48.dp)
                        .testTag("btn_add_entry")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        when (selectedTab) {
                            0 -> "Customer"
                            1 -> "Mine Site"
                            else -> "Facility"
                        },
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Content List based on active tab
            when (selectedTab) {
                0 -> {
                    val filteredCustomers = customers.filter {
                        it.name.contains(searchQuery, ignoreCase = true) ||
                        it.tradeName.contains(searchQuery, ignoreCase = true) ||
                        it.companyCode.contains(searchQuery, ignoreCase = true) ||
                        it.registrationNumber.contains(searchQuery, ignoreCase = true) ||
                        it.vatNumber.contains(searchQuery, ignoreCase = true) ||
                        it.contactPerson.contains(searchQuery, ignoreCase = true)
                    }

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        items(filteredCustomers, key = { it.id }) { customer ->
                            val siteCount = sites.count { it.customerId == customer.id }
                            CustomerCard(
                                customer = customer,
                                siteCount = siteCount,
                                onEdit = {
                                    editingCustomer = customer
                                    isNewCustomer = false
                                },
                                onDelete = {
                                    deletingCustomer = customer
                                }
                            )
                        }
                    }
                }
                1 -> {
                    val filteredSites = sites.filter {
                        it.siteName.contains(searchQuery, ignoreCase = true) ||
                        it.mineCode.contains(searchQuery, ignoreCase = true) ||
                        it.location.contains(searchQuery, ignoreCase = true) ||
                        it.miningRightNumber.contains(searchQuery, ignoreCase = true) ||
                        it.propertyDescription.contains(searchQuery, ignoreCase = true)
                    }

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        items(filteredSites, key = { it.id }) { site ->
                            val customer = customers.find { it.id == site.customerId }
                            val facilityCount = facilities.count { it.siteId == site.id }
                            SiteCard(
                                site = site,
                                customerName = customer?.name ?: "Unknown Customer",
                                facilityCount = facilityCount,
                                onEdit = {
                                    editingSite = site
                                    isNewSite = false
                                },
                                onDelete = {
                                    deletingSite = site
                                }
                            )
                        }
                    }
                }
                2 -> {
                    val filteredFacilities = facilities.filter {
                        it.name.contains(searchQuery, ignoreCase = true) ||
                        it.buildingDescription.contains(searchQuery, ignoreCase = true)
                    }

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        items(filteredFacilities, key = { it.id }) { facility ->
                            val site = sites.find { it.id == facility.siteId }
                            val customer = customers.find { it.id == site?.customerId }
                            FacilityCard(
                                facility = facility,
                                siteName = site?.siteName ?: "Unknown Site",
                                customerName = customer?.name ?: "",
                                onEdit = {
                                    editingFacility = facility
                                    isNewFacility = false
                                },
                                onDelete = {
                                    deletingFacility = facility
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // Customer Edit/Create Dialog
    editingCustomer?.let { customer ->
        CustomerEditDialog(
            customer = customer,
            isNew = isNewCustomer,
            onDismiss = { editingCustomer = null },
            onSave = { updated ->
                viewModel.saveOrUpdateCustomer(updated, isNewCustomer) { success, msg ->
                    if (success) {
                        editingCustomer = null
                    } else if (msg != null) {
                        viewModel.showNotification(msg)
                    }
                }
            }
        )
    }

    // Site Edit/Create Dialog
    editingSite?.let { site ->
        SiteEditDialog(
            site = site,
            customers = customers,
            isNew = isNewSite,
            onDismiss = { editingSite = null },
            onSave = { updated ->
                viewModel.saveOrUpdateSite(updated, isNewSite) { success, msg ->
                    if (success) {
                        editingSite = null
                    } else if (msg != null) {
                        viewModel.showNotification(msg)
                    }
                }
            }
        )
    }

    // Facility Edit/Create Dialog
    editingFacility?.let { facility ->
        FacilityEditDialog(
            facility = facility,
            sites = sites,
            isNew = isNewFacility,
            onDismiss = { editingFacility = null },
            onSave = { updated ->
                viewModel.saveOrUpdateFacility(updated, isNewFacility) { success, msg ->
                    if (success) {
                        editingFacility = null
                    } else if (msg != null) {
                        viewModel.showNotification(msg)
                    }
                }
            }
        )
    }

    // Delete Customer Confirmation
    deletingCustomer?.let { customer ->
        AlertDialog(
            onDismissRequest = { deletingCustomer = null },
            title = { Text("Delete Customer Account?", color = WorkforceTextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "Are you sure you want to delete '${customer.name}'? This will also remove all associated mine sites, facilities, and inspection links for this entity.",
                    color = WorkforceTextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteCustomer(customer.id) { _, _ ->
                            deletingCustomer = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ComplianceRed)
                ) {
                    Text("Delete Account", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { deletingCustomer = null }) {
                    Text("Cancel", color = WorkforceTextSecondary)
                }
            },
            containerColor = WorkforceSurface
        )
    }

    // Delete Site Confirmation
    deletingSite?.let { site ->
        AlertDialog(
            onDismissRequest = { deletingSite = null },
            title = { Text("Delete Mine Site Property?", color = WorkforceTextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "Are you sure you want to delete '${site.siteName}' (Mine #${site.mineCode})?",
                    color = WorkforceTextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteSite(site.id) { _, _ ->
                            deletingSite = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ComplianceRed)
                ) {
                    Text("Delete Property", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { deletingSite = null }) {
                    Text("Cancel", color = WorkforceTextSecondary)
                }
            },
            containerColor = WorkforceSurface
        )
    }

    // Delete Facility Confirmation
    deletingFacility?.let { facility ->
        AlertDialog(
            onDismissRequest = { deletingFacility = null },
            title = { Text("Delete Ablution Facility?", color = WorkforceTextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Text("Remove '${facility.name}' from site registry?", color = WorkforceTextSecondary)
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteFacility(facility.id) { _, _ ->
                            deletingFacility = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ComplianceRed)
                ) {
                    Text("Delete", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { deletingFacility = null }) {
                    Text("Cancel", color = WorkforceTextSecondary)
                }
            },
            containerColor = WorkforceSurface
        )
    }
}

@Composable
fun CustomerCard(
    customer: Customer,
    siteCount: Int,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("card_customer_${customer.id}"),
        colors = CardDefaults.cardColors(containerColor = WorkforceSurface),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, WorkforceCardBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: Registered Entity & Code
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = WorkforceOrange.copy(alpha = 0.15f),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Business,
                                contentDescription = null,
                                tint = WorkforceOrange,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = customer.name,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = WorkforceTextPrimary
                            )
                        )
                        if (customer.tradeName.isNotBlank() && customer.tradeName != customer.name) {
                            Text(
                                text = "Trade Name: ${customer.tradeName}",
                                style = MaterialTheme.typography.bodySmall.copy(color = WorkforceTextSecondary)
                            )
                        }
                    }
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = WorkforceSurfaceVariant
                ) {
                    Text(
                        text = customer.companyCode,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = WorkforceOrange,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Template Legal Structure Metadata
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (customer.registrationNumber.isNotBlank()) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = WorkforceSurfaceVariant,
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(6.dp)) {
                            Text("CIPC REG NUMBER", fontSize = 9.sp, color = WorkforceTextMuted, fontWeight = FontWeight.Bold)
                            Text(customer.registrationNumber, fontSize = 11.sp, color = WorkforceTextPrimary, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
                if (customer.vatNumber.isNotBlank()) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = WorkforceSurfaceVariant,
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(6.dp)) {
                            Text("SARS VAT NUMBER", fontSize = 9.sp, color = WorkforceTextMuted, fontWeight = FontWeight.Bold)
                            Text(customer.vatNumber, fontSize = 11.sp, color = WorkforceTextPrimary, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            if (customer.controlGroupName.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Control Group: ${customer.controlGroupName} (Reg: ${customer.controlGroupRegNumber.ifBlank { "N/A" }})",
                    style = MaterialTheme.typography.bodySmall.copy(color = WorkforceTextSecondary, fontSize = 11.sp)
                )
            }

            if (customer.physicalAddress.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, tint = WorkforceTextMuted, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = customer.physicalAddress,
                        style = MaterialTheme.typography.bodySmall.copy(color = WorkforceTextSecondary, fontSize = 11.sp)
                    )
                }
            }

            // Key Contact Person
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Rep: ${customer.contactPerson.ifBlank { customer.employerRepName }} • ${customer.phone}",
                    style = MaterialTheme.typography.bodySmall.copy(color = WorkforceTextMuted, fontSize = 11.sp)
                )
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = WorkforceOrange.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "$siteCount Sites",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = WorkforceOrange,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        ),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onEdit,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = WorkforceOrange),
                    border = androidx.compose.foundation.BorderStroke(1.dp, WorkforceOrange.copy(alpha = 0.6f)),
                    modifier = Modifier.height(36.dp)
                ) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Modify Profile", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = ComplianceRed.copy(alpha = 0.7f), modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@Composable
fun SiteCard(
    site: Site,
    customerName: String,
    facilityCount: Int,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("card_site_${site.id}"),
        colors = CardDefaults.cardColors(containerColor = WorkforceSurface),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, WorkforceCardBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: Site Name & Mine Code
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFF38BDF8).copy(alpha = 0.15f),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Domain,
                                contentDescription = null,
                                tint = Color(0xFF38BDF8),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = site.siteName,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = WorkforceTextPrimary
                            )
                        )
                        Text(
                            text = customerName,
                            style = MaterialTheme.typography.bodySmall.copy(color = WorkforceOrange, fontWeight = FontWeight.SemiBold)
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = WorkforceSurfaceVariant
                ) {
                    Text(
                        text = "Mine #${site.mineCode}",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color(0xFF38BDF8),
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Property and Location Details
            Text(
                text = "${site.propertyDescription}, ${site.location}, ${site.province}",
                style = MaterialTheme.typography.bodySmall.copy(color = WorkforceTextSecondary)
            )

            if (site.gpsCoordinates.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, tint = ComplianceGreen, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "GPS: ${site.gpsCoordinates}",
                        style = MaterialTheme.typography.bodySmall.copy(color = ComplianceGreen, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    )
                }
            }

            // Template Mining Right & License
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (site.miningRightNumber.isNotBlank()) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = WorkforceSurfaceVariant,
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(6.dp)) {
                            Text("MINING RIGHT", fontSize = 9.sp, color = WorkforceTextMuted, fontWeight = FontWeight.Bold)
                            Text(site.miningRightNumber, fontSize = 11.sp, color = WorkforceTextPrimary, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
                if (site.waterLicenseNumber.isNotBlank()) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = WorkforceSurfaceVariant,
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(6.dp)) {
                            Text("WATER LICENSE", fontSize = 9.sp, color = WorkforceTextMuted, fontWeight = FontWeight.Bold)
                            Text(site.waterLicenseNumber, fontSize = 11.sp, color = WorkforceTextPrimary, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Appointed Officials
            Text(
                text = "Mgr: ${site.mineManager.ifBlank { "Not Appointed" }} • ROH: ${site.siteOccupationalHygienist.ifBlank { "Not Assigned" }}",
                style = MaterialTheme.typography.bodySmall.copy(color = WorkforceTextMuted, fontSize = 11.sp)
            )
            if (site.hygieneAssistant.isNotBlank()) {
                Text(
                    text = "Asst: ${site.hygieneAssistant} • Doctor: ${site.occupationalMedicalPractitioner}",
                    style = MaterialTheme.typography.bodySmall.copy(color = WorkforceTextMuted, fontSize = 11.sp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = WorkforceSurfaceVariant
                ) {
                    Text(
                        text = "$facilityCount Ablutions",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = WorkforceTextSecondary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        ),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedButton(
                        onClick = onEdit,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF38BDF8)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF38BDF8).copy(alpha = 0.6f)),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Edit Site", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = ComplianceRed.copy(alpha = 0.7f), modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun FacilityCard(
    facility: Facility,
    siteName: String,
    customerName: String,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("card_facility_${facility.id}"),
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFA855F7).copy(alpha = 0.15f),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.MeetingRoom,
                                contentDescription = null,
                                tint = Color(0xFFA855F7),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = facility.name,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = WorkforceTextPrimary
                            )
                        )
                        Text(
                            text = "$siteName • $customerName",
                            style = MaterialTheme.typography.bodySmall.copy(color = WorkforceTextSecondary)
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = WorkforceSurfaceVariant
                ) {
                    Text(
                        text = facility.checklistType.displayName.take(16),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color(0xFFA855F7),
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = facility.buildingDescription,
                style = MaterialTheme.typography.bodySmall.copy(color = WorkforceTextSecondary)
            )

            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Capacity: ~${facility.estimatedUsers} Users",
                    style = MaterialTheme.typography.bodySmall.copy(color = WorkforceTextMuted, fontSize = 11.sp)
                )
                Text(
                    text = "Last Visit: ${facility.lastAssessmentDate}",
                    style = MaterialTheme.typography.bodySmall.copy(color = WorkforceTextMuted, fontSize = 11.sp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                OutlinedButton(
                    onClick = onEdit,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFA855F7)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFA855F7).copy(alpha = 0.6f)),
                    modifier = Modifier.height(36.dp)
                ) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Edit Facility", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = ComplianceRed.copy(alpha = 0.7f), modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@Composable
fun CustomerEditDialog(
    customer: Customer,
    isNew: Boolean,
    onDismiss: () -> Unit,
    onSave: (Customer) -> Unit
) {
    var name by remember { mutableStateOf(customer.name) }
    var tradeName by remember { mutableStateOf(customer.tradeName) }
    var companyCode by remember { mutableStateOf(customer.companyCode) }
    var registrationNumber by remember { mutableStateOf(customer.registrationNumber) }
    var vatNumber by remember { mutableStateOf(customer.vatNumber) }
    var physicalAddress by remember { mutableStateOf(customer.physicalAddress) }
    var postalAddress by remember { mutableStateOf(customer.postalAddress) }
    var controlGroupName by remember { mutableStateOf(customer.controlGroupName) }
    var controlGroupRegNumber by remember { mutableStateOf(customer.controlGroupRegNumber) }
    var controlGroupVatNumber by remember { mutableStateOf(customer.controlGroupVatNumber) }
    var contactPerson by remember { mutableStateOf(customer.contactPerson) }
    var email by remember { mutableStateOf(customer.email) }
    var phone by remember { mutableStateOf(customer.phone) }
    var ceoName by remember { mutableStateOf(customer.ceoName) }
    var ceoContact by remember { mutableStateOf(customer.ceoContact) }
    var employerRepName by remember { mutableStateOf(customer.employerRepName) }
    var employerRepPhone by remember { mutableStateOf(customer.employerRepPhone) }
    var accountsContactName by remember { mutableStateOf(customer.accountsContactName) }
    var accountsContactEmail by remember { mutableStateOf(customer.accountsContactEmail) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                if (isNew) "Add Authorized Customer Account" else "Modify Customer Account",
                color = WorkforceTextPrimary,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(480.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Text("1. LEGAL ENTITY (CIPC & SARS TEMPLATE)", color = WorkforceOrange, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                item {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Registered Entity Name *") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = workforceDialogFieldColors()
                    )
                }
                item {
                    OutlinedTextField(
                        value = tradeName,
                        onValueChange = { tradeName = it },
                        label = { Text("Trading Name (as per CIPC)") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = workforceDialogFieldColors()
                    )
                }
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = registrationNumber,
                            onValueChange = { registrationNumber = it },
                            label = { Text("CIPC Reg No") },
                            placeholder = { Text("2002 / 025895 / 07") },
                            modifier = Modifier.weight(1.2f),
                            colors = workforceDialogFieldColors()
                        )
                        OutlinedTextField(
                            value = vatNumber,
                            onValueChange = { vatNumber = it },
                            label = { Text("SARS VAT No") },
                            placeholder = { Text("4910205428") },
                            modifier = Modifier.weight(1f),
                            colors = workforceDialogFieldColors()
                        )
                    }
                }
                item {
                    OutlinedTextField(
                        value = companyCode,
                        onValueChange = { companyCode = it },
                        label = { Text("Internal Account Code") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = workforceDialogFieldColors()
                    )
                }
                item {
                    OutlinedTextField(
                        value = physicalAddress,
                        onValueChange = { physicalAddress = it },
                        label = { Text("CIPC Registered Physical Address") },
                        placeholder = { Text("3 Sennett Street, Worcester, 6850") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = workforceDialogFieldColors()
                    )
                }
                item {
                    OutlinedTextField(
                        value = postalAddress,
                        onValueChange = { postalAddress = it },
                        label = { Text("Registered Postal Address") },
                        placeholder = { Text("P O Box 505, Worcester, 6849") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = workforceDialogFieldColors()
                    )
                }
                item {
                    Text("2. CONTROL GROUP STRUCTURE", color = WorkforceOrange, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                item {
                    OutlinedTextField(
                        value = controlGroupName,
                        onValueChange = { controlGroupName = it },
                        label = { Text("Control Group Registered Name") },
                        placeholder = { Text("Afrimat Ltd") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = workforceDialogFieldColors()
                    )
                }
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = controlGroupRegNumber,
                            onValueChange = { controlGroupRegNumber = it },
                            label = { Text("Group Reg No") },
                            modifier = Modifier.weight(1f),
                            colors = workforceDialogFieldColors()
                        )
                        OutlinedTextField(
                            value = controlGroupVatNumber,
                            onValueChange = { controlGroupVatNumber = it },
                            label = { Text("Group VAT No") },
                            modifier = Modifier.weight(1f),
                            colors = workforceDialogFieldColors()
                        )
                    }
                }
                item {
                    Text("3. APPOINTED OFFICIALS & CONTACTS", color = WorkforceOrange, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = employerRepName,
                            onValueChange = { employerRepName = it },
                            label = { Text("Employer Rep (2A)") },
                            modifier = Modifier.weight(1f),
                            colors = workforceDialogFieldColors()
                        )
                        OutlinedTextField(
                            value = employerRepPhone,
                            onValueChange = { employerRepPhone = it },
                            label = { Text("Rep Mobile / Tel") },
                            modifier = Modifier.weight(1f),
                            colors = workforceDialogFieldColors()
                        )
                    }
                }
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = ceoName,
                            onValueChange = { ceoName = it },
                            label = { Text("CEO / MD") },
                            modifier = Modifier.weight(1f),
                            colors = workforceDialogFieldColors()
                        )
                        OutlinedTextField(
                            value = ceoContact,
                            onValueChange = { ceoContact = it },
                            label = { Text("CEO Telephone") },
                            modifier = Modifier.weight(1f),
                            colors = workforceDialogFieldColors()
                        )
                    }
                }
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = accountsContactName,
                            onValueChange = { accountsContactName = it },
                            label = { Text("Accounts Contact") },
                            modifier = Modifier.weight(1f),
                            colors = workforceDialogFieldColors()
                        )
                        OutlinedTextField(
                            value = accountsContactEmail,
                            onValueChange = { accountsContactEmail = it },
                            label = { Text("Accounts Email") },
                            modifier = Modifier.weight(1f),
                            colors = workforceDialogFieldColors()
                        )
                    }
                }
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = contactPerson,
                            onValueChange = { contactPerson = it },
                            label = { Text("Site Liaison Person") },
                            modifier = Modifier.weight(1f),
                            colors = workforceDialogFieldColors()
                        )
                        OutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = { Text("Telephone / Mobile") },
                            modifier = Modifier.weight(1f),
                            colors = workforceDialogFieldColors()
                        )
                    }
                }
                item {
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Official Communication Email") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = workforceDialogFieldColors()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        customer.copy(
                            name = name.trim(),
                            tradeName = tradeName.ifBlank { name.trim() },
                            companyCode = companyCode.trim(),
                            registrationNumber = registrationNumber.trim(),
                            vatNumber = vatNumber.trim(),
                            physicalAddress = physicalAddress.trim(),
                            postalAddress = postalAddress.trim(),
                            controlGroupName = controlGroupName.trim(),
                            controlGroupRegNumber = controlGroupRegNumber.trim(),
                            controlGroupVatNumber = controlGroupVatNumber.trim(),
                            contactPerson = contactPerson.ifBlank { employerRepName }.trim(),
                            email = email.trim(),
                            phone = phone.trim(),
                            ceoName = ceoName.trim(),
                            ceoContact = ceoContact.trim(),
                            employerRepName = employerRepName.trim(),
                            employerRepPhone = employerRepPhone.trim(),
                            accountsContactName = accountsContactName.trim(),
                            accountsContactEmail = accountsContactEmail.trim()
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = WorkforceOrange)
            ) {
                Text(if (isNew) "Save Account" else "Update Account", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = WorkforceTextSecondary)
            }
        },
        containerColor = WorkforceSurface
    )
}

@Composable
fun SiteEditDialog(
    site: Site,
    customers: List<Customer>,
    isNew: Boolean,
    onDismiss: () -> Unit,
    onSave: (Site) -> Unit
) {
    var customerId by remember { mutableStateOf(site.customerId) }
    var siteName by remember { mutableStateOf(site.siteName) }
    var propertyDescription by remember { mutableStateOf(site.propertyDescription) }
    var location by remember { mutableStateOf(site.location) }
    var province by remember { mutableStateOf(site.province) }
    var magisterialDistrict by remember { mutableStateOf(site.magisterialDistrict) }
    var mineCode by remember { mutableStateOf(site.mineCode) }
    var gpsCoordinates by remember { mutableStateOf(site.gpsCoordinates) }
    var commoditiesProduced by remember { mutableStateOf(site.commoditiesProduced) }
    var miningRightNumber by remember { mutableStateOf(site.miningRightNumber) }
    var waterLicenseNumber by remember { mutableStateOf(site.waterLicenseNumber) }
    var mineManager by remember { mutableStateOf(site.mineManager) }
    var mineManagerContact by remember { mutableStateOf(site.mineManagerContact) }
    var siteOccupationalHygienist by remember { mutableStateOf(site.siteOccupationalHygienist) }
    var hygieneAssistant by remember { mutableStateOf(site.hygieneAssistant) }
    var occupationalMedicalPractitioner by remember { mutableStateOf(site.occupationalMedicalPractitioner) }
    var safetyOfficer by remember { mutableStateOf(site.safetyOfficer) }
    var residentEngineer by remember { mutableStateOf(site.residentEngineer) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                if (isNew) "Add Mine Site Property" else "Modify Mine Site Property",
                color = WorkforceTextPrimary,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(480.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Text("1. SITE OPERATION IDENTIFICATION", color = Color(0xFF38BDF8), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                item {
                    Text("Select Owning Customer:", color = WorkforceTextSecondary, fontSize = 12.sp)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        customers.take(3).forEach { c ->
                            val isSel = c.id == customerId
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSel) Color(0xFF38BDF8) else WorkforceSurfaceVariant,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { customerId = c.id }
                                    .padding(vertical = 6.dp)
                            ) {
                                Text(
                                    text = c.name.take(16),
                                    textAlign = TextAlign.Center,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSel) Color.White else WorkforceTextPrimary
                                )
                            }
                        }
                    }
                }
                item {
                    OutlinedTextField(
                        value = siteName,
                        onValueChange = { siteName = it },
                        label = { Text("Site / Quarry / Mine Name *") },
                        placeholder = { Text("Kuipersbult Quarry & Ready-Mix") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = workforceDialogFieldColors()
                    )
                }
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = mineCode,
                            onValueChange = { mineCode = it },
                            label = { Text("Mine Code") },
                            placeholder = { Text("10023") },
                            modifier = Modifier.weight(1f),
                            colors = workforceDialogFieldColors()
                        )
                        OutlinedTextField(
                            value = location,
                            onValueChange = { location = it },
                            label = { Text("Town / City") },
                            placeholder = { Text("Lephalale") },
                            modifier = Modifier.weight(1f),
                            colors = workforceDialogFieldColors()
                        )
                    }
                }
                item {
                    OutlinedTextField(
                        value = propertyDescription,
                        onValueChange = { propertyDescription = it },
                        label = { Text("Farm / Property Description") },
                        placeholder = { Text("Remainder of the farm Kuipersbults 511LQ") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = workforceDialogFieldColors()
                    )
                }
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = magisterialDistrict,
                            onValueChange = { magisterialDistrict = it },
                            label = { Text("Magisterial District") },
                            placeholder = { Text("Lephalale") },
                            modifier = Modifier.weight(1f),
                            colors = workforceDialogFieldColors()
                        )
                        OutlinedTextField(
                            value = province,
                            onValueChange = { province = it },
                            label = { Text("Province") },
                            modifier = Modifier.weight(1f),
                            colors = workforceDialogFieldColors()
                        )
                    }
                }
                item {
                    OutlinedTextField(
                        value = gpsCoordinates,
                        onValueChange = { gpsCoordinates = it },
                        label = { Text("GPS Coordinates (Lat / Long)") },
                        placeholder = { Text("-23.70760° S, 27.57939° E") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = workforceDialogFieldColors()
                    )
                }
                item {
                    OutlinedTextField(
                        value = commoditiesProduced,
                        onValueChange = { commoditiesProduced = it },
                        label = { Text("Commodities Produced") },
                        placeholder = { Text("Various sized aggregate and ready-mix concrete") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = workforceDialogFieldColors()
                    )
                }
                item {
                    Text("2. MINING AUTHORISATIONS & PERMITS", color = Color(0xFF38BDF8), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = miningRightNumber,
                            onValueChange = { miningRightNumber = it },
                            label = { Text("Mining Right No") },
                            placeholder = { Text("LP30/5/1/2/2/10023MR") },
                            modifier = Modifier.weight(1f),
                            colors = workforceDialogFieldColors()
                        )
                        OutlinedTextField(
                            value = waterLicenseNumber,
                            onValueChange = { waterLicenseNumber = it },
                            label = { Text("Water License No") },
                            placeholder = { Text("01/A42J/AGI/4378") },
                            modifier = Modifier.weight(1f),
                            colors = workforceDialogFieldColors()
                        )
                    }
                }
                item {
                    Text("3. STATUTORY APPOINTMENTS (MHSA)", color = Color(0xFF38BDF8), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = mineManager,
                            onValueChange = { mineManager = it },
                            label = { Text("Mine Manager (3.1)") },
                            placeholder = { Text("Mr P K Lekgeu") },
                            modifier = Modifier.weight(1f),
                            colors = workforceDialogFieldColors()
                        )
                        OutlinedTextField(
                            value = mineManagerContact,
                            onValueChange = { mineManagerContact = it },
                            label = { Text("Manager Contact") },
                            modifier = Modifier.weight(1f),
                            colors = workforceDialogFieldColors()
                        )
                    }
                }
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = siteOccupationalHygienist,
                            onValueChange = { siteOccupationalHygienist = it },
                            label = { Text("Hygienist (12.1)") },
                            placeholder = { Text("N B Letlape (ROH)") },
                            modifier = Modifier.weight(1f),
                            colors = workforceDialogFieldColors()
                        )
                        OutlinedTextField(
                            value = hygieneAssistant,
                            onValueChange = { hygieneAssistant = it },
                            label = { Text("OH Assistant") },
                            placeholder = { Text("N B Ngobese") },
                            modifier = Modifier.weight(1f),
                            colors = workforceDialogFieldColors()
                        )
                    }
                }
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = occupationalMedicalPractitioner,
                            onValueChange = { occupationalMedicalPractitioner = it },
                            label = { Text("Doctor (OMP)") },
                            placeholder = { Text("Dr K O Sebopa") },
                            modifier = Modifier.weight(1f),
                            colors = workforceDialogFieldColors()
                        )
                        OutlinedTextField(
                            value = safetyOfficer,
                            onValueChange = { safetyOfficer = it },
                            label = { Text("Safety Officer") },
                            modifier = Modifier.weight(1f),
                            colors = workforceDialogFieldColors()
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        site.copy(
                            customerId = customerId,
                            siteName = siteName.trim(),
                            propertyDescription = propertyDescription.trim(),
                            location = location.trim(),
                            province = province.trim(),
                            magisterialDistrict = magisterialDistrict.trim(),
                            mineCode = mineCode.trim(),
                            gpsCoordinates = gpsCoordinates.trim(),
                            commoditiesProduced = commoditiesProduced.trim(),
                            miningRightNumber = miningRightNumber.trim(),
                            waterLicenseNumber = waterLicenseNumber.trim(),
                            mineManager = mineManager.trim(),
                            mineManagerContact = mineManagerContact.trim(),
                            siteOccupationalHygienist = siteOccupationalHygienist.trim(),
                            hygieneAssistant = hygieneAssistant.trim(),
                            occupationalMedicalPractitioner = occupationalMedicalPractitioner.trim(),
                            safetyOfficer = safetyOfficer.trim(),
                            residentEngineer = residentEngineer.trim()
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF38BDF8))
            ) {
                Text(if (isNew) "Save Mine Site" else "Update Site", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = WorkforceTextSecondary)
            }
        },
        containerColor = WorkforceSurface
    )
}

@Composable
fun FacilityEditDialog(
    facility: Facility,
    sites: List<Site>,
    isNew: Boolean,
    onDismiss: () -> Unit,
    onSave: (Facility) -> Unit
) {
    var siteId by remember { mutableStateOf(facility.siteId) }
    var name by remember { mutableStateOf(facility.name) }
    var buildingDescription by remember { mutableStateOf(facility.buildingDescription) }
    var checklistType by remember { mutableStateOf(facility.checklistType) }
    var estimatedUsersText by remember { mutableStateOf(facility.estimatedUsers.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                if (isNew) "Register Ablution Facility" else "Modify Ablution Facility",
                color = WorkforceTextPrimary,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Select Mine Site:", color = WorkforceTextSecondary, fontSize = 12.sp)
                sites.take(3).forEach { s ->
                    val isSel = s.id == siteId
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSel) Color(0xFFA855F7) else WorkforceSurfaceVariant,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { siteId = s.id }
                            .padding(vertical = 4.dp)
                    ) {
                        Text(
                            text = "${s.siteName} (Mine #${s.mineCode})",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSel) Color.White else WorkforceTextPrimary,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Facility Name *") },
                    placeholder = { Text("Quarry Pit Main Ablution Block") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = workforceDialogFieldColors()
                )

                OutlinedTextField(
                    value = buildingDescription,
                    onValueChange = { buildingDescription = it },
                    label = { Text("Building Location & Structure") },
                    placeholder = { Text("Adjacent to Primary Crusher & TMM Bay") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = workforceDialogFieldColors()
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ChecklistType.entries.forEach { type ->
                        val isSel = checklistType == type
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSel) Color(0xFFA855F7) else WorkforceSurfaceVariant,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { checklistType = type }
                                .padding(vertical = 4.dp)
                        ) {
                            Text(
                                text = when (type) {
                                    ChecklistType.MALE_ABLUTION -> "Male"
                                    ChecklistType.FEMALE_ABLUTION -> "Female"
                                    ChecklistType.TOILET_BASIN -> "Basin Unit"
                                    else -> type.displayName.take(10)
                                },
                                textAlign = TextAlign.Center,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSel) Color.White else WorkforceTextPrimary,
                                modifier = Modifier.padding(vertical = 6.dp)
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = estimatedUsersText,
                    onValueChange = { estimatedUsersText = it },
                    label = { Text("Estimated Workforce Users") },
                    placeholder = { Text("120") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = workforceDialogFieldColors()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val usersInt = estimatedUsersText.toIntOrNull() ?: 100
                    onSave(
                        facility.copy(
                            siteId = siteId,
                            name = name.trim(),
                            buildingDescription = buildingDescription.trim(),
                            checklistType = checklistType,
                            estimatedUsers = usersInt
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFA855F7))
            ) {
                Text(if (isNew) "Save Facility" else "Update Facility", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = WorkforceTextSecondary)
            }
        },
        containerColor = WorkforceSurface
    )
}

@Composable
fun workforceDialogFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = WorkforceSurfaceVariant,
    unfocusedContainerColor = WorkforceSurfaceVariant,
    focusedBorderColor = WorkforceOrange,
    unfocusedBorderColor = WorkforceCardBorder,
    focusedTextColor = WorkforceTextPrimary,
    unfocusedTextColor = WorkforceTextPrimary,
    focusedLabelColor = WorkforceOrange,
    unfocusedLabelColor = WorkforceTextSecondary
)
