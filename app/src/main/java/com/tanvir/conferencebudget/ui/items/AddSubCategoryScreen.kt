package com.tanvir.conferencebudget.ui.items

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.tanvir.conferencebudget.data.model.SubCategory
import com.tanvir.conferencebudget.ui.theme.DeepTealPrimary
import com.tanvir.conferencebudget.viewmodel.AuthViewModel
import com.tanvir.conferencebudget.viewmodel.BudgetViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSubCategoryScreen(
    conferenceId: String,
    categoryId: String?,
    budgetViewModel: BudgetViewModel,
    authViewModel: AuthViewModel,
    onNavigateBack: () -> Unit
) {
    val categories by budgetViewModel.categories.collectAsState()
    val allUsers by authViewModel.allUsers.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()

    val isAdmin = currentUser?.isFinancialSecretary == true

    var selectedCategoryId by rememberSaveable { mutableStateOf(categoryId ?: categories.firstOrNull()?.id ?: "") }
    var name by rememberSaveable { mutableStateOf("") }
    var details by rememberSaveable { mutableStateOf("") }
    var estimatedCostStr by rememberSaveable { mutableStateOf("") }
    var assignedVolunteerId by rememberSaveable { mutableStateOf("") }
    var assignedVolunteerName by rememberSaveable { mutableStateOf("") }
    var notes by rememberSaveable { mutableStateOf("") }

    var categoryExpanded by remember { mutableStateOf(false) }
    var volunteerExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(categories) {
        if (selectedCategoryId.isBlank() && categories.isNotEmpty()) {
            selectedCategoryId = categories.first().id
        }
    }

    val selectedCategoryName = categories.find { it.id == selectedCategoryId }?.name ?: "Select Category"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Sub-Category", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFF4F7F6),
                    titleContentColor = Color(0xFF0F172A)
                )
            )
        },
        containerColor = Color(0xFFF4F7F6)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (!isAdmin) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Shield, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Admin Access Required: Only the Financial Secretary can create sub-categories.",
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            // Category Selector
            ExposedDropdownMenuBox(
                expanded = categoryExpanded && isAdmin,
                onExpandedChange = { if (isAdmin) categoryExpanded = !categoryExpanded }
            ) {
                OutlinedTextField(
                    value = selectedCategoryName,
                    onValueChange = {},
                    readOnly = true,
                    enabled = isAdmin,
                    label = { Text("Parent Category") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                )
                ExposedDropdownMenu(
                    expanded = categoryExpanded && isAdmin,
                    onDismissRequest = { categoryExpanded = false }
                ) {
                    categories.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat.name) },
                            onClick = {
                                selectedCategoryId = cat.id
                                categoryExpanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Sub-Category Name (e.g. Lunch, Snacks, Water)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                enabled = isAdmin
            )

            OutlinedTextField(
                value = details,
                onValueChange = { details = it },
                label = { Text("Details (e.g. 300 packs @ ৳270)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                enabled = isAdmin
            )

            OutlinedTextField(
                value = estimatedCostStr,
                onValueChange = { estimatedCostStr = it },
                label = { Text("Estimated Cost (৳)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                enabled = isAdmin
            )

            // Volunteer Assignment Selector
            ExposedDropdownMenuBox(
                expanded = volunteerExpanded && isAdmin,
                onExpandedChange = { if (isAdmin) volunteerExpanded = !volunteerExpanded }
            ) {
                OutlinedTextField(
                    value = if (assignedVolunteerName.isBlank()) "Unassigned" else assignedVolunteerName,
                    onValueChange = {},
                    readOnly = true,
                    enabled = isAdmin,
                    label = { Text("Assigned Responsible Volunteer") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = volunteerExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                )
                ExposedDropdownMenu(
                    expanded = volunteerExpanded && isAdmin,
                    onDismissRequest = { volunteerExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Unassigned") },
                        onClick = {
                            assignedVolunteerId = ""
                            assignedVolunteerName = ""
                            volunteerExpanded = false
                        }
                    )
                    allUsers.forEach { user ->
                        DropdownMenuItem(
                            text = { Text("${user.name} (${user.role})") },
                            onClick = {
                                assignedVolunteerId = user.uid
                                assignedVolunteerName = user.name
                                volunteerExpanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes (optional)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                minLines = 2,
                enabled = isAdmin
            )

            Button(
                onClick = {
                    if (isAdmin) {
                        val subCat = SubCategory(
                            categoryId = selectedCategoryId,
                            conferenceId = conferenceId,
                            name = name.trim(),
                            details = details.trim(),
                            estimatedCost = estimatedCostStr.toDoubleOrNull() ?: 0.0,
                            assignedVolunteerId = assignedVolunteerId,
                            assignedVolunteerName = assignedVolunteerName,
                            notes = notes.trim()
                        )
                        budgetViewModel.addSubCategory(subCat)
                        onNavigateBack()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp).padding(top = 8.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DeepTealPrimary),
                enabled = name.isNotBlank() && selectedCategoryId.isNotBlank() && isAdmin
            ) {
                Text("Save Sub-Category", fontWeight = FontWeight.Bold)
            }
        }
    }
}
