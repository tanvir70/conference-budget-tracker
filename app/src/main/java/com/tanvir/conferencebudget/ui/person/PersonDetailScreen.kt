package com.tanvir.conferencebudget.ui.person

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tanvir.conferencebudget.ui.common.ProfileDrawerContent
import com.tanvir.conferencebudget.ui.common.UserAvatar
import com.tanvir.conferencebudget.ui.theme.DeepTealPrimary
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
                    title = { Text(person?.name ?: "Person Detail", fontWeight = FontWeight.Bold) },
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
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFFF4F7F6),
                        titleContentColor = Color(0xFF0F172A)
                    )
                )
            },
            bottomBar = {
                Surface(
                    color = Color.White,
                    shadowElevation = 8.dp,
                    shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = if (isAdmin) Arrangement.spacedBy(12.dp) else Arrangement.Center
                    ) {
                        if (isAdmin) {
                            Button(
                                onClick = onRecordCash,
                                modifier = Modifier.weight(1f).height(48.dp),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = DeepTealPrimary)
                            ) {
                                Icon(Icons.Default.AttachMoney, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Give Cash", fontWeight = FontWeight.Bold)
                            }
                        }
                        Button(
                            onClick = onRecordExpense,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                            modifier = Modifier.weight(1f).height(48.dp),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(Icons.Default.AddShoppingCart, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color.White)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Record Expense", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            },
            containerColor = Color(0xFFF4F7F6)
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Wallet Card Summary
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = DeepTealPrimary),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text("CASH BALANCES & EXPENDITURE", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.7f), fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Column {
                                    Text("Total Received", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.7f))
                                    Text("৳%,.0f".format(totalReceived), fontWeight = FontWeight.Bold, color = Color.White, fontSize = 18.sp)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("Total Spent", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.7f))
                                    Text("৳%,.0f".format(totalSpent), fontWeight = FontWeight.Bold, color = Color(0xFFF87171), fontSize = 18.sp)
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider(color = Color.White.copy(alpha = 0.2f))
                            Spacer(modifier = Modifier.height(12.dp))
                            val remainingColor = if (remaining >= 0) Color(0xFF10B981) else Color(0xFFF87171)
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text("REMAINING BALANCE", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.7f), fontWeight = FontWeight.Bold)
                                Text(
                                    text = "৳%,.0f".format(remaining),
                                    color = remainingColor,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }
                    }
                }

                if (cashTransactions.isNotEmpty()) {
                    item {
                        Text("CASH RECEIVED LOGS", style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp), color = DeepTealPrimary, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 4.dp))
                    }
                    items(cashTransactions) { tx ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(1.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = "৳%,.0f".format(tx.amount), color = Color(0xFF10B981), fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                                    Text(text = "From: ${tx.fromWhom} • ${tx.date}", style = MaterialTheme.typography.bodySmall, color = Color(0xFF64748B))
                                    if (tx.note.isNotBlank()) Text(text = tx.note, style = MaterialTheme.typography.labelSmall, color = DeepTealPrimary)
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
                        Text("EXPENDITURES LOGS", style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp), color = DeepTealPrimary, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
                    }
                    items(expenditures) { exp ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(1.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = exp.item, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    Text(text = "৳%,.0f".format(exp.amount), color = Color(0xFFEF4444), fontWeight = FontWeight.Bold)
                                    Text(text = exp.date, style = MaterialTheme.typography.bodySmall, color = Color(0xFF64748B))
                                    if (exp.note.isNotBlank()) Text(text = exp.note, style = MaterialTheme.typography.labelSmall, color = DeepTealPrimary)
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
