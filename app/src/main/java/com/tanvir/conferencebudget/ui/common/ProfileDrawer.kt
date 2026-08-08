package com.tanvir.conferencebudget.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tanvir.conferencebudget.data.model.User
import com.tanvir.conferencebudget.ui.theme.DeepTealPrimary
import com.tanvir.conferencebudget.viewmodel.AuthViewModel

@Composable
fun ProfileDrawerContent(
    user: User?,
    authViewModel: AuthViewModel,
    onCloseDrawer: () -> Unit,
    onNavigateToBulkOnboard: () -> Unit,
    onSignOut: () -> Unit
) {
    var showEditNameDialog by remember { mutableStateOf(false) }
    var showChangePassDialog by remember { mutableStateOf(false) }
    var showAvatarPickerState by remember { mutableStateOf(false) }
    var showAdminPromoteState by remember { mutableStateOf(false) }

    var newNameInput by remember(user?.name) { mutableStateOf(user?.name ?: "") }
    var newPassInput by remember { mutableStateOf("") }
    var passErrorMsg by remember { mutableStateOf("") }
    var passSuccessMsg by remember { mutableStateOf("") }
    var adminCodeInput by remember { mutableStateOf("") }
    var adminErrorMsg by remember { mutableStateOf("") }

    val isAdmin = user?.isFinancialSecretary == true

    ModalDrawerSheet(
        modifier = Modifier.width(310.dp),
        drawerContainerColor = Color(0xFFF4F7F6),
        drawerTonalElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Gen-Z Curved Deep Teal Profile Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = DeepTealPrimary),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(contentAlignment = Alignment.BottomEnd) {
                        UserAvatar(user = user, size = 72.dp)
                        Surface(
                            modifier = Modifier
                                .size(26.dp)
                                .clip(CircleShape)
                                .clickable { showAvatarPickerState = true },
                            color = Color(0xFF10B981),
                            shape = CircleShape
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Palette, contentDescription = "Change Avatar", tint = Color.White, modifier = Modifier.size(14.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = (user?.name ?: "").ifEmpty { "User" },
                        style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp, fontWeight = FontWeight.Bold),
                        color = Color.White
                    )

                    Text(
                        text = user?.email ?: "",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.8f)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    FilterChip(
                        selected = true,
                        onClick = { if (!isAdmin) showAdminPromoteState = true },
                        label = { Text(if (isAdmin) "Financial Secretary (Admin)" else "Volunteer (Tap for Admin)", fontWeight = FontWeight.Bold) },
                        leadingIcon = {
                            Icon(Icons.Default.Shield, contentDescription = null, modifier = Modifier.size(16.dp))
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = if (isAdmin) Color(0xFF10B981) else Color(0xFF0F5048),
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text("ACCOUNT SETTINGS", style = MaterialTheme.typography.labelSmall, color = DeepTealPrimary, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp))
            Spacer(modifier = Modifier.height(8.dp))

            NavigationDrawerItem(
                label = { Text("Change Display Name", fontWeight = FontWeight.Medium) },
                icon = { Icon(Icons.Default.Edit, contentDescription = null) },
                selected = false,
                onClick = { showEditNameDialog = true },
                shape = RoundedCornerShape(14.dp)
            )

            NavigationDrawerItem(
                label = { Text("Change Password", fontWeight = FontWeight.Medium) },
                icon = { Icon(Icons.Default.Key, contentDescription = null) },
                selected = false,
                onClick = { showChangePassDialog = true },
                shape = RoundedCornerShape(14.dp)
            )

            NavigationDrawerItem(
                label = { Text("Choose Avatar Color", fontWeight = FontWeight.Medium) },
                icon = { Icon(Icons.Default.Palette, contentDescription = null) },
                selected = false,
                onClick = { showAvatarPickerState = true },
                shape = RoundedCornerShape(14.dp)
            )

            if (!isAdmin) {
                NavigationDrawerItem(
                    label = { Text("Activate Admin Mode", fontWeight = FontWeight.Medium) },
                    icon = { Icon(Icons.Default.Shield, contentDescription = null) },
                    selected = false,
                    onClick = { showAdminPromoteState = true },
                    shape = RoundedCornerShape(14.dp)
                )
            }

            if (isAdmin) {
                NavigationDrawerItem(
                    label = { Text("Bulk Onboard Volunteers", fontWeight = FontWeight.Medium) },
                    icon = { Icon(Icons.Default.PersonAdd, contentDescription = null) },
                    selected = false,
                    onClick = {
                        onCloseDrawer()
                        onNavigateToBulkOnboard()
                    },
                    shape = RoundedCornerShape(14.dp)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            HorizontalDivider(color = Color(0xFFE2E8F0))
            Spacer(modifier = Modifier.height(8.dp))

            NavigationDrawerItem(
                label = { Text("Sign Out", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error) },
                icon = { Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
                selected = false,
                onClick = {
                    onCloseDrawer()
                    authViewModel.signOut()
                    onSignOut()
                },
                shape = RoundedCornerShape(14.dp)
            )
        }
    }

    // Edit Name Dialog
    if (showEditNameDialog) {
        AlertDialog(
            onDismissRequest = { showEditNameDialog = false },
            title = { Text("Change Display Name") },
            text = {
                OutlinedTextField(
                    value = newNameInput,
                    onValueChange = { newNameInput = it },
                    label = { Text("Full Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newNameInput.isNotBlank()) {
                            authViewModel.updateProfileName(newNameInput)
                            showEditNameDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DeepTealPrimary)
                ) { Text("Save") }
            },
            dismissButton = { TextButton(onClick = { showEditNameDialog = false }) { Text("Cancel") } }
        )
    }

    // Change Password Dialog
    if (showChangePassDialog) {
        AlertDialog(
            onDismissRequest = { showChangePassDialog = false; passErrorMsg = ""; passSuccessMsg = "" },
            title = { Text("Change Password") },
            text = {
                Column {
                    OutlinedTextField(
                        value = newPassInput,
                        onValueChange = { newPassInput = it; passErrorMsg = "" },
                        label = { Text("New Password") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
                    )
                    if (passErrorMsg.isNotBlank()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(passErrorMsg, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                    }
                    if (passSuccessMsg.isNotBlank()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(passSuccessMsg, color = Color(0xFF10B981), style = MaterialTheme.typography.bodySmall)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newPassInput.length >= 6) {
                            authViewModel.updatePassword(newPassInput) { success, err ->
                                if (success) {
                                    passSuccessMsg = "Password updated successfully!"
                                    newPassInput = ""
                                } else {
                                    passErrorMsg = err ?: "Failed to update password."
                                }
                            }
                        } else {
                            passErrorMsg = "Password must be at least 6 characters."
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DeepTealPrimary)
                ) { Text("Update Password") }
            },
            dismissButton = {
                TextButton(onClick = { showChangePassDialog = false; passErrorMsg = ""; passSuccessMsg = "" }) { Text("Close") }
            }
        )
    }

    // Avatar Color Picker Dialog
    if (showAvatarPickerState) {
        AlertDialog(
            onDismissRequest = { showAvatarPickerState = false },
            title = { Text("Choose Avatar Color") },
            text = {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    avatarGradients.forEachIndexed { idx, color ->
                        val key = "avatar_$idx"
                        Surface(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .clickable {
                                    authViewModel.updateAvatar(key)
                                    showAvatarPickerState = false
                                },
                            shape = CircleShape,
                            color = color
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(user?.initials ?: "U", color = Color.White, fontWeight = FontWeight.ExtraBold)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showAvatarPickerState = false }) { Text("Done") }
            }
        )
    }

    // Admin Promote Dialog
    if (showAdminPromoteState) {
        AlertDialog(
            onDismissRequest = { showAdminPromoteState = false; adminErrorMsg = "" },
            title = { Text("Activate Admin Mode") },
            text = {
                Column {
                    Text("Enter Master Security Code (e.g. ADMIN2025# or admin):")
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = adminCodeInput,
                        onValueChange = { adminCodeInput = it; adminErrorMsg = "" },
                        label = { Text("Master Code") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
                    )
                    if (adminErrorMsg.isNotBlank()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(adminErrorMsg, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val trimmed = adminCodeInput.trim()
                        if (trimmed.equals("ADMIN2025#", ignoreCase = true) || trimmed.equals("admin", ignoreCase = true)) {
                            authViewModel.makeCurrentUserAdmin()
                            showAdminPromoteState = false
                            adminCodeInput = ""
                            adminErrorMsg = ""
                        } else {
                            adminErrorMsg = "Invalid Master Code!"
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DeepTealPrimary)
                ) { Text("Activate Admin") }
            },
            dismissButton = {
                TextButton(onClick = { showAdminPromoteState = false; adminErrorMsg = "" }) { Text("Cancel") }
            }
        )
    }
}
