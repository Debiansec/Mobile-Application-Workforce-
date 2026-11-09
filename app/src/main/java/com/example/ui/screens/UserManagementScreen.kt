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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.AssignmentInd
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.User
import com.example.data.model.UserRole
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserManagementScreen(
    viewModel: WorkforceViewModel,
    onBack: () -> Unit
) {
    val users by viewModel.allUsers.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedRoleFilter by remember { mutableStateOf<UserRole?>(null) }

    var userToEdit by remember { mutableStateOf<User?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }
    var userToDelete by remember { mutableStateOf<User?>(null) }

    // Filter users
    val filteredUsers = users.filter { user ->
        val matchesQuery = searchQuery.isBlank() ||
                user.name.contains(searchQuery, ignoreCase = true) ||
                user.email.contains(searchQuery, ignoreCase = true) ||
                user.registrationNumber.contains(searchQuery, ignoreCase = true) ||
                user.department.contains(searchQuery, ignoreCase = true)

        val matchesRole = selectedRoleFilter == null || user.role == selectedRoleFilter
        matchesQuery && matchesRole
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Authorized Personnel & Roles",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = WorkforceTextPrimary
                            )
                        )
                        Text(
                            text = "Manage Profiles, Passwords & Access Control",
                            style = MaterialTheme.typography.bodySmall.copy(color = WorkforceOrange)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = WorkforceTextPrimary
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showAddDialog = true },
                        modifier = Modifier.testTag("topbar_add_user_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add User",
                            tint = WorkforceRed
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = WorkforceDarkBg)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = WorkforceRed,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.testTag("fab_add_user")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Add User", fontWeight = FontWeight.Bold)
                }
            }
        },
        containerColor = WorkforceDarkBg
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(bottom = 90.dp)
        ) {
            // Logged in User Status Banner
            item {
                ActiveProfileBanner(
                    currentUser = currentUser,
                    onEditProfile = { userToEdit = currentUser }
                )
            }

            // Search Bar
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("user_search_input"),
                    placeholder = {
                        Text("Search name, email, role, or SAIOH #...", color = WorkforceTextMuted)
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = WorkforceOrange
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Clear",
                                    tint = WorkforceTextMuted
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = WorkforceSurface,
                        unfocusedContainerColor = WorkforceSurface,
                        focusedBorderColor = WorkforceRed,
                        unfocusedBorderColor = WorkforceCardBorder,
                        focusedTextColor = WorkforceTextPrimary,
                        unfocusedTextColor = WorkforceTextPrimary
                    )
                )
            }

            // Role Filter Chips
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        RoleFilterChip(
                            label = "All (${users.size})",
                            isSelected = selectedRoleFilter == null,
                            color = WorkforceRed,
                            onClick = { selectedRoleFilter = null }
                        )
                    }
                    items(UserRole.entries.toTypedArray()) { role ->
                        val count = users.count { it.role == role }
                        RoleFilterChip(
                            label = "${role.displayName} ($count)",
                            isSelected = selectedRoleFilter == role,
                            color = Color(role.badgeColorHex),
                            onClick = { selectedRoleFilter = role }
                        )
                    }
                }
            }

            // Count summary
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Authorized Personnel (${filteredUsers.size})",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = WorkforceTextPrimary
                        )
                    )
                    Text(
                        text = "Tap to edit profile or switch role",
                        style = MaterialTheme.typography.bodySmall.copy(color = WorkforceTextMuted)
                    )
                }
            }

            // Users List
            if (filteredUsers.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        colors = CardDefaults.cardColors(containerColor = WorkforceSurface),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = WorkforceTextMuted,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "No users found matching '$searchQuery'",
                                style = MaterialTheme.typography.bodyMedium.copy(color = WorkforceTextSecondary)
                            )
                        }
                    }
                }
            } else {
                items(filteredUsers, key = { it.id }) { user ->
                    UserCardItem(
                        user = user,
                        isCurrent = user.id == currentUser.id,
                        onSwitchUser = { viewModel.switchUser(user) },
                        onEditUser = { userToEdit = user },
                        onDeleteUser = { userToDelete = user }
                    )
                }
            }
        }
    }

    // Add New User Dialog
    if (showAddDialog) {
        UserEditorDialog(
            user = null,
            onDismiss = { showAddDialog = false },
            onSave = { newUser ->
                viewModel.saveOrUpdateUser(newUser, isNew = true) { success, _ ->
                    if (success) showAddDialog = false
                }
            }
        )
    }

    // Edit Existing User Dialog
    userToEdit?.let { user ->
        UserEditorDialog(
            user = user,
            onDismiss = { userToEdit = null },
            onSave = { updatedUser ->
                viewModel.saveOrUpdateUser(updatedUser, isNew = false) { success, _ ->
                    if (success) userToEdit = null
                }
            }
        )
    }

    // Delete Confirmation Dialog
    userToDelete?.let { user ->
        AlertDialog(
            onDismissRequest = { userToDelete = null },
            containerColor = WorkforceSurface,
            title = {
                Text(
                    text = "Delete Authorized User?",
                    color = WorkforceTextPrimary,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to remove '${user.name}' (${user.email}) from authorized personnel? This action cannot be undone.",
                    color = WorkforceTextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteUser(user.id) { success, _ ->
                            if (success) userToDelete = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = WorkforceRed)
                ) {
                    Text("Delete User")
                }
            },
            dismissButton = {
                TextButton(onClick = { userToDelete = null }) {
                    Text("Cancel", color = WorkforceTextMuted)
                }
            }
        )
    }
}

@Composable
fun ActiveProfileBanner(
    currentUser: User,
    onEditProfile: () -> Unit
) {
    val roleColor = Color(currentUser.role.badgeColorHex)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = WorkforceSurfaceVariant),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, roleColor.copy(alpha = 0.6f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(50.dp),
                shape = CircleShape,
                color = roleColor.copy(alpha = 0.2f),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, roleColor)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = currentUser.name.take(2).uppercase(),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            color = roleColor
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "ACTIVE SESSION",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = WorkforceOrange,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        color = ComplianceGreen.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "ACTIVE",
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = ComplianceGreen,
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp
                            )
                        )
                    }
                }
                Text(
                    text = currentUser.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = WorkforceTextPrimary
                    )
                )
                Text(
                    text = "${currentUser.role.displayName} • ${currentUser.registrationNumber}",
                    style = MaterialTheme.typography.bodySmall.copy(color = roleColor)
                )
                Text(
                    text = currentUser.email,
                    style = MaterialTheme.typography.bodySmall.copy(color = WorkforceTextMuted)
                )
            }

            OutlinedButton(
                onClick = onEditProfile,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = WorkforceTextPrimary),
                border = androidx.compose.foundation.BorderStroke(1.dp, WorkforceCardBorder),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Edit", fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun UserCardItem(
    user: User,
    isCurrent: Boolean,
    onSwitchUser: () -> Unit,
    onEditUser: () -> Unit,
    onDeleteUser: () -> Unit
) {
    val roleColor = Color(user.role.badgeColorHex)
    val roleIcon = when (user.role) {
        UserRole.FIELD_ASSESSOR -> Icons.Default.AssignmentInd
        UserRole.OCCUPATIONAL_HYGIENIST -> Icons.Default.HealthAndSafety
        UserRole.MINE_MANAGEMENT -> Icons.Default.Engineering
        UserRole.SYSTEM_ADMIN -> Icons.Default.AdminPanelSettings
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("user_card_${user.id}"),
        colors = CardDefaults.cardColors(
            containerColor = if (isCurrent) WorkforceSurfaceVariant else WorkforceSurface
        ),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(
            width = if (isCurrent) 1.5.dp else 1.dp,
            color = if (isCurrent) WorkforceRed else WorkforceCardBorder
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(44.dp),
                    shape = CircleShape,
                    color = roleColor.copy(alpha = 0.18f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, roleColor.copy(alpha = 0.5f))
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = roleIcon,
                            contentDescription = user.role.displayName,
                            tint = roleColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = user.name,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = WorkforceTextPrimary
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (isCurrent) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                color = WorkforceRed.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "YOU",
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = WorkforceRed,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 9.sp
                                    )
                                )
                            }
                        }
                    }

                    Text(
                        text = user.role.displayName,
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = roleColor,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }

                // Edit Button
                IconButton(
                    onClick = onEditUser,
                    modifier = Modifier.testTag("edit_user_${user.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Profile",
                        tint = WorkforceTextMuted
                    )
                }

                // Delete Button (if not current user)
                if (!isCurrent) {
                    IconButton(
                        onClick = onDeleteUser,
                        modifier = Modifier.testTag("delete_user_${user.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete User",
                            tint = ComplianceRed.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // User Info Grid
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(WorkforceDarkBg.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                    .padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Email, contentDescription = null, tint = WorkforceOrange, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = user.email, style = MaterialTheme.typography.bodySmall.copy(color = WorkforceTextSecondary))
                }

                if (user.registrationNumber.isNotBlank()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Badge, contentDescription = null, tint = WorkforceOrange, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Reg: ${user.registrationNumber}", style = MaterialTheme.typography.bodySmall.copy(color = WorkforceTextSecondary))
                    }
                }

                if (user.phone.isNotBlank()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Phone, contentDescription = null, tint = WorkforceOrange, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = user.phone, style = MaterialTheme.typography.bodySmall.copy(color = WorkforceTextSecondary))
                    }
                }

                if (user.department.isNotBlank()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Business, contentDescription = null, tint = WorkforceOrange, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = user.department, style = MaterialTheme.typography.bodySmall.copy(color = WorkforceTextSecondary))
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = if (user.isActive) ComplianceGreen.copy(alpha = 0.15f) else ComplianceRed.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = if (user.isActive) "STATUS: ACTIVE" else "STATUS: DEACTIVATED",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (user.isActive) ComplianceGreen else ComplianceRed,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    )
                }

                if (!isCurrent) {
                    Button(
                        onClick = onSwitchUser,
                        colors = ButtonDefaults.buttonColors(containerColor = WorkforceRed),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("switch_to_user_${user.id}")
                    ) {
                        Icon(imageVector = Icons.Default.SwapHoriz, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Switch to User", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = ComplianceGreen, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Active Now", style = MaterialTheme.typography.bodySmall.copy(color = ComplianceGreen, fontWeight = FontWeight.Bold))
                    }
                }
            }
        }
    }
}

@Composable
fun RoleFilterChip(
    label: String,
    isSelected: Boolean,
    color: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable { onClick() },
        color = if (isSelected) color.copy(alpha = 0.25f) else WorkforceSurface,
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(
            width = if (isSelected) 1.5.dp else 1.dp,
            color = if (isSelected) color else WorkforceCardBorder
        )
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) color else WorkforceTextSecondary
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserEditorDialog(
    user: User?,
    onDismiss: () -> Unit,
    onSave: (User) -> Unit
) {
    val isNew = user == null

    var name by remember { mutableStateOf(user?.name ?: "") }
    var email by remember { mutableStateOf(user?.email ?: "") }
    var password by remember { mutableStateOf(user?.password ?: "Password123!") }
    var role by remember { mutableStateOf(user?.role ?: UserRole.FIELD_ASSESSOR) }
    var registrationNumber by remember { mutableStateOf(user?.registrationNumber ?: "") }
    var phone by remember { mutableStateOf(user?.phone ?: "") }
    var department by remember { mutableStateOf(user?.department ?: "") }
    var isActive by remember { mutableStateOf(user?.isActive ?: true) }

    var isPasswordVisible by remember { mutableStateOf(false) }
    var roleDropdownExpanded by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp)),
            color = WorkforceSurface,
            border = androidx.compose.foundation.BorderStroke(1.5.dp, WorkforceRed)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    Text(
                        text = if (isNew) "Add Authorized User" else "Edit User Profile & Role",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = WorkforceTextPrimary
                        )
                    )
                    Text(
                        text = if (isNew) "Register new personnel with role-based inspection permissions" else "Modify credentials, contact info, and legal role assignment",
                        style = MaterialTheme.typography.bodySmall.copy(color = WorkforceTextSecondary)
                    )
                }

                if (errorMessage != null) {
                    item {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = ComplianceRed.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, ComplianceRed)
                        ) {
                            Text(
                                text = errorMessage ?: "",
                                color = ComplianceRed,
                                modifier = Modifier.padding(10.dp),
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold)
                            )
                        }
                    }
                }

                // Full Name
                item {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Full Name *") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = WorkforceOrange) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = WorkforceTextPrimary,
                            unfocusedTextColor = WorkforceTextPrimary,
                            focusedBorderColor = WorkforceRed,
                            unfocusedBorderColor = WorkforceCardBorder
                        )
                    )
                }

                // Email Address
                item {
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email Address * (Login ID)") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = WorkforceOrange) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = WorkforceTextPrimary,
                            unfocusedTextColor = WorkforceTextPrimary,
                            focusedBorderColor = WorkforceRed,
                            unfocusedBorderColor = WorkforceCardBorder
                        )
                    )
                }

                // Password
                item {
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password * (Min 4 chars)") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = WorkforceOrange) },
                        trailingIcon = {
                            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                Icon(
                                    imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = if (isPasswordVisible) "Hide password" else "Show password",
                                    tint = WorkforceTextMuted
                                )
                            }
                        },
                        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = WorkforceTextPrimary,
                            unfocusedTextColor = WorkforceTextPrimary,
                            focusedBorderColor = WorkforceRed,
                            unfocusedBorderColor = WorkforceCardBorder
                        )
                    )
                }

                // Role Dropdown
                item {
                    Text(
                        text = "Authorized Role & Legal Mandate",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = WorkforceTextSecondary,
                            fontWeight = FontWeight.Bold
                        )
                    )

                    ExposedDropdownMenuBox(
                        expanded = roleDropdownExpanded,
                        onExpandedChange = { roleDropdownExpanded = !roleDropdownExpanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = role.displayName,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = roleDropdownExpanded) },
                            leadingIcon = { Icon(Icons.Default.Security, contentDescription = null, tint = Color(role.badgeColorHex)) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = WorkforceTextPrimary,
                                unfocusedTextColor = WorkforceTextPrimary,
                                focusedBorderColor = WorkforceRed,
                                unfocusedBorderColor = WorkforceCardBorder
                            )
                        )

                        ExposedDropdownMenu(
                            expanded = roleDropdownExpanded,
                            onDismissRequest = { roleDropdownExpanded = false },
                            modifier = Modifier.background(WorkforceSurface)
                        ) {
                            UserRole.entries.forEach { selectableRole ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(
                                                text = selectableRole.displayName,
                                                color = Color(selectableRole.badgeColorHex),
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    },
                                    onClick = {
                                        role = selectableRole
                                        roleDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Professional Registration Number
                item {
                    OutlinedTextField(
                        value = registrationNumber,
                        onValueChange = { registrationNumber = it },
                        label = { Text("SAIOH / Legal Registration # (e.g. SAIOH ROH 0482)") },
                        leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null, tint = WorkforceOrange) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = WorkforceTextPrimary,
                            unfocusedTextColor = WorkforceTextPrimary,
                            focusedBorderColor = WorkforceRed,
                            unfocusedBorderColor = WorkforceCardBorder
                        )
                    )
                }

                // Phone Number
                item {
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Contact Telephone / Mobile") },
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = WorkforceOrange) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = WorkforceTextPrimary,
                            unfocusedTextColor = WorkforceTextPrimary,
                            focusedBorderColor = WorkforceRed,
                            unfocusedBorderColor = WorkforceCardBorder
                        )
                    )
                }

                // Department
                item {
                    OutlinedTextField(
                        value = department,
                        onValueChange = { department = it },
                        label = { Text("Department / Client Organization") },
                        leadingIcon = { Icon(Icons.Default.Business, contentDescription = null, tint = WorkforceOrange) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = WorkforceTextPrimary,
                            unfocusedTextColor = WorkforceTextPrimary,
                            focusedBorderColor = WorkforceRed,
                            unfocusedBorderColor = WorkforceCardBorder
                        )
                    )
                }

                // Active Switch
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(WorkforceDarkBg, RoundedCornerShape(8.dp))
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Account Active Status",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = WorkforceTextPrimary
                                )
                            )
                            Text(
                                text = if (isActive) "User can log in and sign off inspections" else "Account deactivated - login suspended",
                                style = MaterialTheme.typography.bodySmall.copy(color = WorkforceTextMuted)
                            )
                        }
                        Switch(
                            checked = isActive,
                            onCheckedChange = { isActive = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = ComplianceGreen,
                                uncheckedThumbColor = WorkforceTextMuted,
                                uncheckedTrackColor = WorkforceSurface
                            )
                        )
                    }
                }

                // Buttons
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = onDismiss) {
                            Text("Cancel", color = WorkforceTextMuted)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Button(
                            onClick = {
                                if (name.isBlank()) {
                                    errorMessage = "Please enter full name."
                                    return@Button
                                }
                                if (email.isBlank() || !email.contains("@")) {
                                    errorMessage = "Please enter a valid email address."
                                    return@Button
                                }
                                if (password.length < 4) {
                                    errorMessage = "Password must be at least 4 characters."
                                    return@Button
                                }

                                val resolvedId = user?.id ?: "user_${UUID.randomUUID().toString().take(8)}"
                                val resultUser = User(
                                    id = resolvedId,
                                    name = name.trim(),
                                    email = email.trim().lowercase(),
                                    role = role,
                                    registrationNumber = registrationNumber.trim(),
                                    signatureData = user?.signatureData,
                                    password = password.trim(),
                                    phone = phone.trim(),
                                    department = department.trim(),
                                    isActive = isActive
                                )
                                onSave(resultUser)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = WorkforceRed),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("save_user_button")
                        ) {
                            Text(if (isNew) "Create User" else "Save Changes")
                        }
                    }
                }
            }
        }
    }
}
