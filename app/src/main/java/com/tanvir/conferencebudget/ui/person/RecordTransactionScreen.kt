package com.tanvir.conferencebudget.ui.person

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
import com.tanvir.conferencebudget.ui.common.NumericKeypad
import com.tanvir.conferencebudget.ui.theme.DeepTealPrimary
import com.tanvir.conferencebudget.viewmodel.PersonViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordTransactionScreen(
    type: String, // "cash" or "expense"
    personViewModel: PersonViewModel,
    onNavigateBack: () -> Unit
) {
    val isCash = type == "cash"
    
    var amount by rememberSaveable { mutableStateOf("") }
    var sourceOrItem by rememberSaveable { mutableStateOf("") }
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

    val displayAmount = if (amount.isEmpty()) "0" else amount

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isCash) "Give Cash" else "Record Expense", fontWeight = FontWeight.Bold) },
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
            // Header Segmented Badge Card
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
                    Text(
                        text = if (isCash) "💵 Cash Transfer" else "🛒 Expenditure",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = DeepTealPrimary
                    )

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

            // Big Currency Display Box (Image 4 Aesthetic)
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
                        text = "AMOUNT (৳)",
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
                value = sourceOrItem,
                onValueChange = { sourceOrItem = it },
                label = { Text(if (isCash) "From Whom (e.g. Committee Secretary)" else "Item Name (e.g. Bus Tickets)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                singleLine = true
            )

            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Note (optional)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                singleLine = true
            )

            // On-Screen Custom Keypad
            NumericKeypad(
                onDigitClick = { digit ->
                    if (digit == "." && amount.contains(".")) {
                        // ignore double dot
                    } else if (amount.length < 9) {
                        amount += digit
                    }
                },
                onBackspaceClick = {
                    if (amount.isNotEmpty()) {
                        amount = amount.dropLast(1)
                    }
                }
            )

            Spacer(modifier = Modifier.height(4.dp))

            Button(
                onClick = {
                    val parsedAmount = amount.toDoubleOrNull() ?: 0.0
                    val finalDate = date.ifBlank { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date()) }
                    if (isCash) {
                        personViewModel.addCashTransaction(parsedAmount, sourceOrItem, finalDate, note)
                    } else {
                        personViewModel.addExpenditure(sourceOrItem, parsedAmount, finalDate, note)
                    }
                    onNavigateBack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                enabled = amount.isNotBlank() && (amount.toDoubleOrNull() ?: 0.0) > 0 && sourceOrItem.isNotBlank()
            ) {
                Text("Save Transaction", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
            }
        }
    }
}
