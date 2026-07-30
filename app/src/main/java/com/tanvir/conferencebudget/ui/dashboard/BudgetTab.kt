package com.tanvir.conferencebudget.ui.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tanvir.conferencebudget.data.model.Category
import com.tanvir.conferencebudget.data.model.SpendingEntry
import com.tanvir.conferencebudget.data.model.SubCategory
import com.tanvir.conferencebudget.data.model.User
import com.tanvir.conferencebudget.viewmodel.AuthViewModel
import com.tanvir.conferencebudget.viewmodel.BudgetViewModel

@Composable
fun BudgetTab(
    budgetViewModel: BudgetViewModel,
    authViewModel: AuthViewModel,
    onNavigateToAddCategory: () -> Unit,
    onNavigateToAddSubCategory: (String?) -> Unit,
    onNavigateToAddSpending: (String) -> Unit
) {
    val categories by budgetViewModel.categories.collectAsState()
    val subCategories by budgetViewModel.subCategories.collectAsState()
    val spendingEntries by budgetViewModel.spendingEntries.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()

    val isAdmin = currentUser?.isFinancialSecretary == true

    val onRecordExpenseClicked: () -> Unit = {
        val targetSubCatId = subCategories.firstOrNull()?.id ?: ""
        if (targetSubCatId.isNotBlank()) onNavigateToAddSpending(targetSubCatId)
    }

    if (categories.isEmpty() && subCategories.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        Icons.Default.Folder,
                        contentDescription = null,
                        modifier = Modifier.size(54.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = "No budget categories created yet",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    if (isAdmin) {
                        Text(
                            text = "As Financial Secretary, start by creating top-level categories and adding sub-categories under them.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = onNavigateToAddCategory,
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Default.CreateNewFolder, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("+ Category")
                                }

                                OutlinedButton(
                                    onClick = { onNavigateToAddSubCategory(null) },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Default.PostAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("+ Sub-Category")
                                }
                            }
                        }
                    } else {
                        Text(
                            text = "Waiting for Financial Secretary (Admin) to create budget categories.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Clean Modern Action Bar at Top
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (isAdmin) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = onNavigateToAddCategory,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.CreateNewFolder, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("+ Category")
                            }

                            OutlinedButton(
                                onClick = { onNavigateToAddSubCategory(null) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.PostAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("+ Sub-Category")
                            }
                        }
                    }

                    if (subCategories.isNotEmpty()) {
                        Button(
                            onClick = onRecordExpenseClicked,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                        ) {
                            Icon(Icons.Default.AddShoppingCart, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Record Expense", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            items(categories) { category ->
                CategorySection(
                    category = category,
                    subCategories = subCategories.filter { it.categoryId == category.id },
                    spendingEntries = spendingEntries,
                    currentUser = currentUser,
                    isAdmin = isAdmin,
                    onDeleteCategory = { budgetViewModel.deleteCategory(category.id) },
                    onAddSubCategory = { onNavigateToAddSubCategory(category.id) },
                    onAddSpending = onNavigateToAddSpending,
                    onDeleteSubCategory = { budgetViewModel.deleteSubCategory(it) },
                    onDeleteSpending = { budgetViewModel.deleteSpendingEntry(it) }
                )
            }
        }
    }
}

@Composable
fun CategorySection(
    category: Category,
    subCategories: List<SubCategory>,
    spendingEntries: List<SpendingEntry>,
    currentUser: User?,
    isAdmin: Boolean,
    onDeleteCategory: () -> Unit,
    onAddSubCategory: () -> Unit,
    onAddSpending: (String) -> Unit,
    onDeleteSubCategory: (String) -> Unit,
    onDeleteSpending: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .width(4.dp)
                            .height(20.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(MaterialTheme.colorScheme.primary)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = category.name,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                if (isAdmin) {
                    IconButton(onClick = onDeleteCategory) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Category", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (subCategories.isEmpty()) {
                Text(
                    text = "No sub-categories in this category.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    subCategories.forEach { subCat ->
                        val subCatEntries = spendingEntries.filter { it.subCategoryId == subCat.id }
                        val actualSpent = subCatEntries.sumOf { it.amount }

                        SubCategoryCard(
                            subCat = subCat,
                            spendingEntries = subCatEntries,
                            actualSpent = actualSpent,
                            currentUser = currentUser,
                            isAdmin = isAdmin,
                            onAddSpending = { onAddSpending(subCat.id) },
                            onDeleteSubCat = { onDeleteSubCategory(subCat.id) },
                            onDeleteSpending = onDeleteSpending
                        )
                    }
                }
            }

            if (isAdmin) {
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedButton(
                    onClick = onAddSubCategory,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Sub-Category under ${category.name}")
                }
            }
        }
    }
}

@Composable
fun SubCategoryCard(
    subCat: SubCategory,
    spendingEntries: List<SpendingEntry>,
    actualSpent: Double,
    currentUser: User?,
    isAdmin: Boolean,
    onAddSpending: () -> Unit,
    onDeleteSubCat: () -> Unit,
    onDeleteSpending: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val isAssignedToCurrentUser = subCat.assignedVolunteerId == currentUser?.uid || 
        (subCat.assignedVolunteerName.isNotBlank() && subCat.assignedVolunteerName == currentUser?.name)
    val canAddSpending = isAdmin || isAssignedToCurrentUser

    val ratio = if (subCat.estimatedCost > 0) (actualSpent / subCat.estimatedCost).coerceIn(0.0, 1.0).toFloat() else 0f

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = subCat.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    if (subCat.details.isNotBlank()) {
                        Text(text = subCat.details, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    if (subCat.assignedVolunteerName.isNotBlank()) {
                        Text(text = "Responsible: ${subCat.assignedVolunteerName}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    }
                }

                if (isAdmin) {
                    IconButton(onClick = onDeleteSubCat) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Budget Comparison
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Estimated", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("৳%,.0f".format(subCat.estimatedCost), fontWeight = FontWeight.Medium)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Actual Spent (${spendingEntries.size} entries)", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    val color = if (actualSpent <= subCat.estimatedCost) Color(0xFF10B981) else Color(0xFFEF4444)
                    Text("৳%,.0f".format(actualSpent), color = color, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            LinearProgressIndicator(
                progress = { ratio },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = if (actualSpent > subCat.estimatedCost) Color(0xFFEF4444) else MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = { expanded = !expanded }) {
                    Text(if (expanded) "Hide Entries" else "View ${spendingEntries.size} Entries", style = MaterialTheme.typography.labelMedium)
                    Icon(if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore, contentDescription = null)
                }

                if (canAddSpending) {
                    Button(
                        onClick = onAddSpending,
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier.height(34.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("+ Spend", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Expandable Granular Spending Entries
            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (spendingEntries.isEmpty()) {
                        Text("No spending entries logged yet.", style = MaterialTheme.typography.bodySmall)
                    } else {
                        spendingEntries.forEach { entry ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = "৳%,.0f • ${entry.spentByName}".format(entry.amount), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                    Text(text = "Date: ${entry.date}${if (entry.note.isNotBlank()) " • " + entry.note else ""}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }

                                if (isAdmin || entry.spentByUserId == currentUser?.uid) {
                                    IconButton(onClick = { onDeleteSpending(entry.id) }, modifier = Modifier.size(24.dp)) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete entry", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
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
