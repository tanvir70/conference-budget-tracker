package com.tanvir.conferencebudget.ui.person

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tanvir.conferencebudget.ui.common.ProfileDrawerContent
import com.tanvir.conferencebudget.ui.common.UserAvatar
import com.tanvir.conferencebudget.viewmodel.AuthViewModel
import com.tanvir.conferencebudget.viewmodel.PersonViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonDetailScreen(
    personViewModel: PersonViewModel,
    authViewModel: AuthViewModel,
    onNavigateBack: () -> Unit,
    onRecordCash: () -> Unit,
    onRecordExpense: () -> Unit,
    onNavigateToBulkOnboard: () -> Unit,
    onSignOut: () -> Unit
) {
    val person by personViewModel.person.collectAsState()
    val cashTransactions by personViewModel.cashTransactions.collectAsState()
    val expenditures by personViewModel.expenditures.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()
    
    val totalReceived by personViewModel.totalReceived.collectAsState()
    val totalSpent by personViewModel.totalSpent.collectAsState()
    val remaining by personViewModel.remaining.collectAsState()

    val isAdmin = currentUser?.isFinancialSecretary == true

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ProfileDrawerContent(
                user = currentUser,
                authViewModel = authViewModel,
                onCloseDrawer = { scope.launch { drawerState.close() } },
                onNavigateToBulkOnboard = onNavigateToBulkOnboard,
                onSignOut = onSignOut
            )
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(person?.name ?: "Person Detail") },
                    navigationIcon = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = onNavigateBack) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                            }
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                UserAvatar(user = currentUser, size = 32.dp)
                            }
                        }
                    },
                    actions = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                        IconButton(onClick = {
                            authViewModel.signOut()
                            onSignOut()
                        }) {
                            Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Sign Out")
                        }
                    }
                )
            },
            bottomBar = {
                BottomAppBar {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        horizontalArrangement = if (isAdmin) Arrangement.SpaceEvenly else Arrangement.Center
                    ) {
                        if (isAdmin) {
                            Button(onClick = onRecordCash) {
                                Text("Give Cash")
                            }
                        }
                        Button(
                            onClick = onRecordExpense,
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                            modifier = if (!isAdmin) Modifier.fillMaxWidth() else Modifier
                        ) {
                            Text("Record Expense")
                        }
                    }
                }
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Total Received: ৳%,.0f".format(totalReceived), fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Total Spent: ৳%,.0f".format(totalSpent))
                            Spacer(modifier = Modifier.height(8.dp))
                            val remainingColor = if (remaining >= 0) Color(0xFF4CAF50) else Color(0xFFF44336)
                            Text(
                                text = "Remaining: ৳%,.0f".format(remaining),
                                color = remainingColor,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }

                if (cashTransactions.isNotEmpty()) {
                    item {
                        Text("Cash Received", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
                    }
                    items(cashTransactions) { tx ->
                        Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(1.dp)) {
                            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = "৳%,.0f".format(tx.amount), color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold)
                                    Text(text = "From: ${tx.fromWhom} • ${tx.date}", style = MaterialTheme.typography.bodySmall)
                                    if (tx.note.isNotBlank()) Text(text = tx.note, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                if (isAdmin) {
                                    IconButton(onClick = { personViewModel.deleteCashTransaction(tx.id) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                                    }
                                }
                            }
                        }
                    }
                }

                if (expenditures.isNotEmpty()) {
                    item {
                        Text("Expenditures", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 16.dp))
                    }
                    items(expenditures) { exp ->
                        Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(1.dp)) {
                            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = exp.item, fontWeight = FontWeight.Bold)
                                    Text(text = "৳%,.0f".format(exp.amount), color = Color(0xFFF44336))
                                    Text(text = exp.date, style = MaterialTheme.typography.bodySmall)
                                    if (exp.note.isNotBlank()) Text(text = exp.note, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                if (isAdmin) {
                                    IconButton(onClick = { personViewModel.deleteExpenditure(exp.id) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
