package com.tanvir.conferencebudget.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tanvir.conferencebudget.ui.common.ProfileDrawerContent
import com.tanvir.conferencebudget.ui.common.UserAvatar
import com.tanvir.conferencebudget.ui.theme.DeepTealPrimary
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
                                    color = DeepTealPrimary,
                                    fontWeight = FontWeight.SemiBold
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
                                UserAvatar(user = currentUser, size = 36.dp)
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
            ) {
                // TrackMyBill Inspired Deep Teal Hero Balance Container (Image 3 Aesthetic)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = DeepTealPrimary),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    "ESTIMATED BUDGET",
                                    style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White.copy(alpha = 0.7f)
                                )
                                Text(
                                    "৳%,.0f".format(totalEst),
                                    style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.ExtraBold),
                                    color = Color.White
                                )
                            }

                            Surface(
                                shape = CircleShape,
                                color = Color.White.copy(alpha = 0.15f),
                                modifier = Modifier.padding(start = 8.dp)
                            ) {
                                Text(
                                    text = "$percentSpent% Used",
                                    color = Color.White,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Sub-metrics pills (Spent vs Remaining)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color.White.copy(alpha = 0.1f))
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("SPENT", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.7f), fontWeight = FontWeight.Bold)
                                Text("৳%,.0f".format(totalAct), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color(0xFF10B981))
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text("REMAINING / DUE", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.7f), fontWeight = FontWeight.Bold)
                                Text(
                                    "৳%,.0f".format(totalDue),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (totalDue < 0) Color(0xFFF87171) else Color.White
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Progress Utilization Bar
                        LinearProgressIndicator(
                            progress = { spentRatio },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = if (spentRatio > 0.9f) Color(0xFFF87171) else Color(0xFF10B981),
                            trackColor = Color.White.copy(alpha = 0.2f)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = onRecordExpenseClicked,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                        ) {
                            Icon(Icons.Default.AddShoppingCart, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Record Expense", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.White)
                        }
                    }
                }

                // Gen-Z Segmented Pill Control
                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = Color.Transparent,
                    contentColor = DeepTealPrimary,
                    divider = {}
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = {
                                Text(
                                    text = title,
                                    fontWeight = if (selectedTabIndex == index) FontWeight.ExtraBold else FontWeight.Medium,
                                    fontSize = 15.sp
                                )
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

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
