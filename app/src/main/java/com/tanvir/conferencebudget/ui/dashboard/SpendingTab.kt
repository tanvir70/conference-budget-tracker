package com.tanvir.conferencebudget.ui.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ReceiptLong
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
import com.tanvir.conferencebudget.ui.common.CategoryIconBadge
import com.tanvir.conferencebudget.ui.theme.DeepTealPrimary
import com.tanvir.conferencebudget.viewmodel.BudgetViewModel

@Composable
fun SpendingTab(
    budgetViewModel: BudgetViewModel,
    onNavigateToEditItem: (String) -> Unit
) {
    val spendingEntries by budgetViewModel.spendingEntries.collectAsState()
    val subCategories by budgetViewModel.subCategories.collectAsState()
    val categories by budgetViewModel.categories.collectAsState()

    if (spendingEntries.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.ReceiptLong, contentDescription = null, modifier = Modifier.size(54.dp), tint = DeepTealPrimary.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(12.dp))
                Text("No spending entries logged yet.", style = MaterialTheme.typography.bodyLarge, color = Color(0xFF64748B))
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
                    text = "RECENT ACTIVITY",
                    style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
                    color = DeepTealPrimary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                )
            }

            items(spendingEntries) { entry ->
                val subCat = subCategories.find { it.id == entry.subCategoryId }
                val catName = categories.find { it.id == entry.categoryId }?.name ?: subCat?.name ?: "Expense"

                Card(
                    modifier = Modifier.fillMaxWidth(),
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
                        CategoryIconBadge(categoryName = catName, size = 46.dp)

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = subCat?.name ?: "Expenditure",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontSize = 16.sp),
                                color = Color(0xFF0F172A)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${entry.date} • ${entry.spentByName}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF64748B)
                            )
                            if (entry.note.isNotBlank()) {
                                Text(
                                    text = entry.note,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = DeepTealPrimary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        Text(
                            text = "৳%,.0f".format(entry.amount),
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                            color = Color(0xFF10B981)
                        )
                    }
                }
            }
        }
    }
}
