package com.tanvir.conferencebudget.ui.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tanvir.conferencebudget.data.model.Category
import com.tanvir.conferencebudget.data.model.SpendingEntry
import com.tanvir.conferencebudget.data.model.SubCategory
import com.tanvir.conferencebudget.data.model.User
import com.tanvir.conferencebudget.ui.common.CategoryIconBadge
import com.tanvir.conferencebudget.ui.common.CategoryStyle
import com.tanvir.conferencebudget.ui.common.getCategoryStyle
import com.tanvir.conferencebudget.ui.theme.DeepTealPrimary
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

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Admin quick actions top row
        if (isAdmin) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onNavigateToAddCategory,
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DeepTealPrimary)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Add Category", fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = { onNavigateToAddSubCategory(null) },
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Add Sub-Cat", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        if (categories.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = DeepTealPrimary)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Loading & preparing budget items...", color = Color(0xFF64748B))
                    }
                }
            }
        } else {
            items(categories, key = { it.id }) { category ->
                val categorySubCats = subCategories.filter { it.categoryId == category.id }

                CategorySection(
                    category = category,
                    subCategories = categorySubCats,
                    spendingEntries = spendingEntries,
                    currentUser = currentUser,
                    isAdmin = isAdmin,
                    onAddSubCategory = { onNavigateToAddSubCategory(category.id) },
                    onDeleteCategory = { budgetViewModel.deleteCategory(category.id) },
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
    onAddSubCategory: () -> Unit,
    onDeleteCategory: () -> Unit,
    onAddSpending: (String) -> Unit,
    onDeleteSubCategory: (String) -> Unit,
    onDeleteSpending: (String) -> Unit
) {
    val categoryEstimated = subCategories.sumOf { it.cost }
    val categoryActual = spendingEntries
        .filter { entry -> subCategories.any { it.id == entry.subCategoryId } }
        .sumOf { it.amount }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Category Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CategoryIconBadge(categoryName = category.name, size = 42.dp)

                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = category.name,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontSize = 17.sp),
                        color = DeepTealPrimary
                    )
                }
                if (isAdmin) {
                    IconButton(onClick = onDeleteCategory) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Category", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (subCategories.isEmpty()) {
                Text(
                    text = "No sub-categories in this category.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF64748B),
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
                Spacer(modifier = Modifier.height(12.dp))
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

    val isAssignedToCurrentUser = (subCat.responsiblePerson.isNotBlank() && subCat.responsiblePerson.equals(currentUser?.name, ignoreCase = true))
    val canAddSpending = isAdmin || isAssignedToCurrentUser

    val ratio = if (subCat.cost > 0) (actualSpent / subCat.cost).coerceIn(0.0, 1.0).toFloat() else 0f

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFFF8FAFC),
        tonalElevation = 1.dp
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
                        Text(text = subCat.details, style = MaterialTheme.typography.bodySmall, color = Color(0xFF64748B))
                    }
                    if (subCat.responsiblePerson.isNotBlank()) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFE8F5E9),
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            Text(
                                text = "👤 ${subCat.responsiblePerson}",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF047857),
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                if (isAdmin) {
                    IconButton(onClick = onDeleteSubCat) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Budget Comparison
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("COST", style = MaterialTheme.typography.labelSmall, color = Color(0xFF64748B), fontWeight = FontWeight.Bold)
                    Text("৳%,.0f".format(subCat.cost), fontWeight = FontWeight.Medium)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("PAID (${spendingEntries.size} logs)", style = MaterialTheme.typography.labelSmall, color = Color(0xFF64748B), fontWeight = FontWeight.Bold)
                    val color = if (actualSpent <= subCat.cost) Color(0xFF10B981) else Color(0xFFEF4444)
                    Text("৳%,.0f".format(actualSpent), color = color, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { ratio },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = if (actualSpent > subCat.cost) Color(0xFFEF4444) else Color(0xFF10B981),
                trackColor = Color(0xFFE2E8F0)
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
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.White)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("+ Spend", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }

            // Expandable Granular Spending Entries
            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (spendingEntries.isEmpty()) {
                        Text(
                            text = "No spending logged for this item yet.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF64748B),
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    } else {
                        spendingEntries.forEach { entry ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.White, shape = RoundedCornerShape(10.dp))
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = entry.note.ifBlank { "Logged Spend" },
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                        color = Color(0xFF0F172A)
                                    )
                                    Text(
                                        text = "${entry.date} • ${entry.spentByName.ifBlank { "Unknown" }}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFF64748B)
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "৳%,.0f".format(entry.amount),
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                        color = Color(0xFF10B981)
                                    )
                                    if (isAdmin || entry.spentByUserId == currentUser?.uid) {
                                        IconButton(
                                            onClick = { onDeleteSpending(entry.id) },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Delete entry",
                                                tint = Color(0xFFEF4444),
                                                modifier = Modifier.size(16.dp)
                                            )
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
}
