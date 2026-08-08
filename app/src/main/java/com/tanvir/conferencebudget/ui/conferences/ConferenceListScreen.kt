package com.tanvir.conferencebudget.ui.conferences

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Event
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
import com.tanvir.conferencebudget.viewmodel.ConferenceViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConferenceListScreen(
    viewModel: ConferenceViewModel,
    authViewModel: AuthViewModel,
    onNavigateToCreate: () -> Unit,
    onConferenceSelected: (String) -> Unit,
    onNavigateToBulkOnboard: () -> Unit,
    onSignOut: () -> Unit
) {
    val conferences by viewModel.conferences.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()

    val displayName = currentUser?.name?.takeIf { it.isNotBlank() } ?: "User"
    val roleTitle = if (currentUser?.isFinancialSecretary == true) "Admin" else "Volunteer"

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
                    title = {
                        Column {
                            Text("Hi, $displayName!", fontWeight = FontWeight.Bold)
                            Text(
                                text = roleTitle,
                                style = MaterialTheme.typography.labelMedium,
                                color = DeepTealPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            UserAvatar(user = currentUser, size = 36.dp)
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
            floatingActionButton = {
                FloatingActionButton(
                    onClick = onNavigateToCreate,
                    containerColor = Color(0xFF10B981),
                    contentColor = Color.White,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Conference")
                }
            },
            containerColor = Color(0xFFF4F7F6)
        ) { padding ->
            if (conferences.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Event, contentDescription = null, modifier = Modifier.size(54.dp), tint = DeepTealPrimary.copy(alpha = 0.5f))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No conferences yet.\nTap + to create one.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color(0xFF64748B),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(conferences) { conf ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onConferenceSelected(conf.id) },
                            shape = RoundedCornerShape(20.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Row(
                                modifier = Modifier.padding(20.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = conf.name,
                                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, fontSize = 20.sp),
                                        color = DeepTealPrimary
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Date: ${conf.date}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color(0xFF64748B)
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "Budget: ৳%,.0f".format(conf.totalEstimatedBudget),
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                                        color = Color(0xFF10B981)
                                    )
                                }
                                if (currentUser?.isFinancialSecretary == true) {
                                    IconButton(onClick = { viewModel.deleteConference(conf.id) }) {
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
