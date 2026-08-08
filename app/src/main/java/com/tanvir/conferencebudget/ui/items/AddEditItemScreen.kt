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
import com.tanvir.conferencebudget.viewmodel.BudgetViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditItemScreen(
    conferenceId: String,
    itemId: String?,
    budgetViewModel: BudgetViewModel,
    onNavigateBack: () -> Unit
) {
    val subCategories by budgetViewModel.subCategories.collectAsState()
    val existingItem = itemId?.let { id -> subCategories.find { it.id == id } }

    var name by rememberSaveable { mutableStateOf(existingItem?.name ?: "") }
    var details by rememberSaveable { mutableStateOf(existingItem?.details ?: "") }
    var costStr by rememberSaveable { mutableStateOf(existingItem?.cost?.toString() ?: "") }
    var notes by rememberSaveable { mutableStateOf(existingItem?.notes ?: "") }

    LaunchedEffect(existingItem) {
        existingItem?.let { item ->
            name = item.name
            details = item.details
            costStr = item.cost.toString()
            notes = item.notes
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (itemId == null) "Add Sub-Category" else "Edit Sub-Category") },
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
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Sub-Category") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = details,
                onValueChange = { details = it },
                label = { Text("Details") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = costStr,
                onValueChange = { costStr = it },
                label = { Text("Cost (৳)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )

            Button(
                onClick = {
                    existingItem?.let { item ->
                        val updated = item.copy(
                            name = name,
                            details = details,
                            cost = costStr.toDoubleOrNull() ?: item.cost,
                            notes = notes
                        )
                        budgetViewModel.updateSubCategory(updated)
                    }
                    onNavigateBack()
                },
                modifier = Modifier.fillMaxWidth().height(50.dp).padding(top = 8.dp),
                enabled = name.isNotBlank()
            ) {
                Text("Save Changes")
            }
            
            if (itemId != null) {
                OutlinedButton(
                    onClick = {
                        budgetViewModel.deleteSubCategory(itemId)
                        onNavigateBack()
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete Sub-Category")
                }
            }
        }
    }
}
