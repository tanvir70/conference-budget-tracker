package com.tanvir.conferencebudget.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tanvir.conferencebudget.ui.common.ProfileDrawerContent
import com.tanvir.conferencebudget.ui.common.UserAvatar
import com.tanvir.conferencebudget.viewmodel.AuthViewModel
import com.tanvir.conferencebudget.viewmodel.BudgetViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    conferenceId: String,
    conferenceName: String,
    budgetViewModel: BudgetViewModel,
    authViewModel: AuthViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToAddCategory: () -> Unit,
    onNavigateToAddSubCategory: (String?) -> Unit,
    onNavigateToAddSpending: (String) -> Unit,
    onNavigateToEditItem: (String) -> Unit,
    onNavigateToPersonDetail: (String) -> Unit,
    onNavigateToBulkOnboard: () -> Unit,
    onSignOut: () -> Unit
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Budget", "Spending", "People")

    val currentUser by authViewModel.currentUser.collectAsState()
    val subCategories by budgetViewModel.subCategories.collectAsState()

    val totalEst by budgetViewModel.totalEstimated.collectAsState()
    val totalAct by budgetViewModel.totalActual.collectAsState()
    val totalPaid by budgetViewModel.totalPaid.collectAsState()
    val totalDue by budgetViewModel.totalDue.collectAsState()

    val displayName = currentUser?.name?.takeIf { it.isNotBlank() } ?: "User"
    val roleTitle = if (currentUser?.isFinancialSecretary == true) "Admin" else "Volunteer"
    val isAdmin = currentUser?.isFinancialSecretary == true

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    var showNoSubCatDialog by remember { mutableStateOf(false) }

    val spentRatio = if (totalEst > 0) (totalAct / totalEst).coerceIn(0.0, 1.0).toFloat() else 0f
    val percentSpent = (spentRatio * 100).toInt()

    if (showNoSubCatDialog) {
        AlertDialog(
            onDismissRequest = { showNoSubCatDialog = false },
            title = { Text("No Sub-Categories Created Yet") },
            text = { Text("Please create a category and sub-category first before logging an expenditure.") },
            confirmButton = {
                TextButton(onClick = {
                    showNoSubCatDialog = false
                    if (isAdmin) onNavigateToAddCategory()
                }) {
                    Text(if (isAdmin) "Create Category" else "OK")
                }
            }
        )
    }

    val onRecordExpenseClicked: () -> Unit = {
        val targetSubCatId = subCategories.firstOrNull()?.id ?: ""
        if (targetSubCatId.isNotBlank()) {
            onNavigateToAddSpending(targetSubCatId)
        } else {
            showNoSubCatDialog = true
        }
    }

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
                    title = {
                        Column {
                            Text(conferenceName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable { scope.launch { drawerState.open() } }
                            ) {
                                Text(
                                    text = "$displayName • $roleTitle",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    },
                    navigationIcon = {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(start = 4.dp)) {
                            IconButton(onClick = onNavigateBack) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                            }
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                UserAvatar(user = currentUser, size = 34.dp)
                            }
                        }
                    },
                    actions = {
                        if (isAdmin) {
                            IconButton(onClick = onNavigateToBulkOnboard) {
                                Icon(Icons.Default.PersonAdd, contentDescription = "Bulk Onboard Volunteers")
                            }
                        }
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Side Menu")
                        }
                        IconButton(onClick = {
                            authViewModel.signOut()
                            onSignOut()
                        }) {
                            Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Sign Out")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.onSurface
                    )
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                // Glassmorphic Hero Budget Summary Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                                        MaterialTheme.colorScheme.primaryContainer
                                    )
                                )
                            )
                            .padding(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("ESTIMATED BUDGET", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                Text(
                                    "৳%,.0f".format(totalEst),
                                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text("SPENT SO FAR", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                Text(
                                    "৳%,.0f".format(totalAct),
                                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Visual Budget Progress Bar
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Budget Utilized ($percentSpent%)", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Medium)
                            Text(
                                text = "Remaining: ৳%,.0f".format(totalDue),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (totalDue < 0) Color(0xFFEF4444) else MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        LinearProgressIndicator(
                            progress = { spentRatio },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = if (spentRatio > 0.9f) Color(0xFFEF4444) else MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.15f)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = onRecordExpenseClicked,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Icon(Icons.Default.AddShoppingCart, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Record Expense", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                    }
                }

                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = { Text(title, fontWeight = FontWeight.Bold, fontSize = 15.sp) }
                        )
                    }
                }

                Box(modifier = Modifier.weight(1f)) {
                    when (selectedTabIndex) {
                        0 -> BudgetTab(
                            budgetViewModel = budgetViewModel,
                            authViewModel = authViewModel,
                            onNavigateToAddCategory = onNavigateToAddCategory,
                            onNavigateToAddSubCategory = onNavigateToAddSubCategory,
                            onNavigateToAddSpending = onNavigateToAddSpending
                        )
                        1 -> SpendingTab(budgetViewModel, onNavigateToEditItem)
                        2 -> PeopleTab(budgetViewModel, onNavigateToPersonDetail)
                    }
                }
            }
        }
    }
}
