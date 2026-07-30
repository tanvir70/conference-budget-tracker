package com.tanvir.conferencebudget.ui.items

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.tanvir.conferencebudget.data.model.SubCategory
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
                title = { Text("Add Sub-Category") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Category Selector
            ExposedDropdownMenuBox(
                expanded = categoryExpanded,
                onExpandedChange = { categoryExpanded = !categoryExpanded }
            ) {
                OutlinedTextField(
                    value = selectedCategoryName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Parent Category") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = categoryExpanded,
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
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = details,
                onValueChange = { details = it },
                label = { Text("Details (e.g. 300 packs @ ৳270)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = estimatedCostStr,
                onValueChange = { estimatedCostStr = it },
                label = { Text("Estimated Cost (৳)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            // Volunteer Assignment Selector
            ExposedDropdownMenuBox(
                expanded = volunteerExpanded,
                onExpandedChange = { volunteerExpanded = !volunteerExpanded }
            ) {
                OutlinedTextField(
                    value = if (assignedVolunteerName.isBlank()) "Unassigned" else assignedVolunteerName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Assigned Responsible Volunteer") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = volunteerExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = volunteerExpanded,
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
                minLines = 2
            )

            Button(
                onClick = {
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
                },
                modifier = Modifier.fillMaxWidth().height(50.dp).padding(top = 8.dp),
                enabled = name.isNotBlank() && selectedCategoryId.isNotBlank()
            ) {
                Text("Save Sub-Category")
            }
        }
    }
}
