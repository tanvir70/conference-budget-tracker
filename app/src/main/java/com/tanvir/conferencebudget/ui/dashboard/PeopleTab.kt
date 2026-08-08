package com.tanvir.conferencebudget.ui.dashboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tanvir.conferencebudget.ui.common.UserAvatar
import com.tanvir.conferencebudget.ui.theme.DeepTealPrimary
import com.tanvir.conferencebudget.viewmodel.BudgetViewModel

@Composable
fun PeopleTab(
    budgetViewModel: BudgetViewModel,
    onNavigateToPersonDetail: (String) -> Unit
) {
    val persons by budgetViewModel.persons.collectAsState()

    if (persons.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.People, contentDescription = null, modifier = Modifier.size(54.dp), tint = DeepTealPrimary.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(12.dp))
                Text("No team members added yet.", style = MaterialTheme.typography.bodyLarge, color = Color(0xFF64748B))
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "TEAM MEMBERS & CASH LEDGER",
                    style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
                    color = DeepTealPrimary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                )
            }

            items(persons) { person ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToPersonDetail(person.id) },
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        UserAvatar(user = null, size = 46.dp)

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = person.name,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontSize = 16.sp),
                                color = Color(0xFF0F172A)
                            )
                            Text(
                                text = "Volunteer Ledger",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF64748B)
                            )
                        }

                        Icon(Icons.Default.ChevronRight, contentDescription = "Detail", tint = Color(0xFF94A3B8))
                    }
                }
            }
        }
    }
}
