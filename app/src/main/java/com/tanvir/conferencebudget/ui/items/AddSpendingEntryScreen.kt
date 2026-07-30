package com.tanvir.conferencebudget.ui.items

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.tanvir.conferencebudget.data.model.SpendingEntry
import com.tanvir.conferencebudget.viewmodel.AuthViewModel
import com.tanvir.conferencebudget.viewmodel.BudgetViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSpendingEntryScreen(
    conferenceId: String,
    subCategoryId: String,
    budgetViewModel: BudgetViewModel,
    authViewModel: AuthViewModel,
    onNavigateBack: () -> Unit
) {
    val subCategories by budgetViewModel.subCategories.collectAsState()
    val categories by budgetViewModel.categories.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()

    val subCat = subCategories.find { it.id == subCategoryId }
    val categoryName = categories.find { it.id == subCat?.categoryId }?.name ?: ""

    var amountStr by rememberSaveable { mutableStateOf("") }
    var date by rememberSaveable { mutableStateOf("") }
    var note by rememberSaveable { mutableStateOf("") }

    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDatePicker = false
                        datePickerState.selectedDateMillis?.let { millis ->
                            val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                            date = sdf.format(Date(millis))
                        }
                    }
                ) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Log Spending Entry") },
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Category: $categoryName", style = MaterialTheme.typography.labelMedium)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text("Sub-Category: ${subCat?.name ?: ""}", style = MaterialTheme.typography.titleMedium)
                    if (!subCat?.assignedVolunteerName.isNullOrBlank()) {
                        Text("Assigned: ${subCat?.assignedVolunteerName}", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            OutlinedTextField(
                value = amountStr,
                onValueChange = { amountStr = it },
                label = { Text("Amount Spent (৳)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = date,
                onValueChange = {},
                readOnly = true,
                label = { Text("Date") },
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Default.CalendarToday, contentDescription = "Select Date")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker = true }
            )

            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Note / Description (e.g. Booking advance)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    val entry = SpendingEntry(
                        subCategoryId = subCategoryId,
                        categoryId = subCat?.categoryId ?: "",
                        conferenceId = conferenceId,
                        amount = amountStr.toDoubleOrNull() ?: 0.0,
                        date = date.ifBlank { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date()) },
                        note = note.trim(),
                        spentByUserId = currentUser?.uid ?: "",
                        spentByName = currentUser?.name ?: "Unknown"
                    )
                    budgetViewModel.addSpendingEntry(entry)
                    onNavigateBack()
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = amountStr.isNotBlank()
            ) {
                Text("Log Expenditure")
            }
        }
    }
}
