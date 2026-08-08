package com.tanvir.conferencebudget.ui.items

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tanvir.conferencebudget.data.model.SpendingEntry
import com.tanvir.conferencebudget.ui.common.NumericKeypad
import com.tanvir.conferencebudget.ui.theme.DeepTealPrimary
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

    val displayAmount = if (amountStr.isEmpty()) "0" else amountStr

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Log Spending Entry", fontWeight = FontWeight.Bold) },
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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Category info badge card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(categoryName.ifEmpty { "Category" }, style = MaterialTheme.typography.labelSmall, color = Color(0xFF64748B), fontWeight = FontWeight.Bold)
                        Text(subCat?.name ?: "Sub-Category", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = DeepTealPrimary)
                    }

                    FilterChip(
                        selected = true,
                        onClick = { showDatePicker = true },
                        label = { Text(if (date.isBlank()) "Today" else date, fontWeight = FontWeight.Bold) },
                        leadingIcon = {
                            Icon(Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(14.dp))
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFFE8F5E9),
                            selectedLabelColor = Color(0xFF047857)
                        )
                    )
                }
            }

            // Big Currency Entry Box (Image 4 Aesthetic)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "AMOUNT SPENT",
                        style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.5.sp),
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF64748B)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "৳ $displayAmount",
                        style = MaterialTheme.typography.headlineLarge.copy(fontSize = 38.sp, fontWeight = FontWeight.ExtraBold),
                        color = DeepTealPrimary,
                        textAlign = TextAlign.Center
                    )
                }
            }

            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Note / Description (e.g. Booking advance)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                singleLine = true
            )

            // Custom On-Screen Keypad (Image 4 Easy Entry)
            NumericKeypad(
                onDigitClick = { digit ->
                    if (digit == "." && amountStr.contains(".")) {
                        // ignore double dot
                    } else if (amountStr.length < 9) {
                        amountStr += digit
                    }
                },
                onBackspaceClick = {
                    if (amountStr.isNotEmpty()) {
                        amountStr = amountStr.dropLast(1)
                    }
                }
            )

            Spacer(modifier = Modifier.height(4.dp))

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
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                enabled = amountStr.isNotBlank() && (amountStr.toDoubleOrNull() ?: 0.0) > 0
            ) {
                Text("Log Expenditure", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
            }
        }
    }
}
