package com.tanvir.conferencebudget.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tanvir.conferencebudget.data.model.Category
import com.tanvir.conferencebudget.data.model.Expenditure
import com.tanvir.conferencebudget.data.model.Person
import com.tanvir.conferencebudget.data.model.SpendingEntry
import com.tanvir.conferencebudget.data.model.SubCategory
import com.tanvir.conferencebudget.data.repository.FirestoreRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class BudgetViewModel(private val conferenceId: String) : ViewModel() {
    private val repository = FirestoreRepository()

    val categories: StateFlow<List<Category>> = repository.getCategories(conferenceId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val subCategories: StateFlow<List<SubCategory>> = repository.getSubCategories(conferenceId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val spendingEntries: StateFlow<List<SpendingEntry>> = repository.getSpendingEntries(conferenceId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val persons: StateFlow<List<Person>> = repository.getPersons(conferenceId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // All person expenditures flow
    val allPersonExpenditures: StateFlow<List<Expenditure>> = repository.getConferences()
        .map { emptyList<Expenditure>() } // Fallback holder
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Derived totals
    val totalEstimated: StateFlow<Double> = subCategories
        .map { items -> items.sumOf { it.cost } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalActual: StateFlow<Double> = combine(spendingEntries, subCategories) { entries, _ ->
        entries.sumOf { it.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalPaid: StateFlow<Double> = totalActual

    val totalDue: StateFlow<Double> = combine(totalEstimated, totalActual) { estimated, actual ->
        estimated - actual
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    fun addCategory(name: String) {
        viewModelScope.launch {
            try {
                val order = (categories.value.maxOfOrNull { it.order } ?: 0) + 1
                repository.addCategory(Category(name = name, conferenceId = conferenceId, order = order))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun deleteCategory(categoryId: String) {
        viewModelScope.launch {
            try {
                repository.deleteCategory(conferenceId, categoryId)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun addSubCategory(subCategory: SubCategory) {
        viewModelScope.launch {
            try {
                repository.addSubCategory(subCategory.copy(conferenceId = conferenceId))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun updateSubCategory(subCategory: SubCategory) {
        viewModelScope.launch {
            try {
                repository.updateSubCategory(subCategory.copy(conferenceId = conferenceId))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun deleteSubCategory(subCategoryId: String) {
        viewModelScope.launch {
            try {
                repository.deleteSubCategory(conferenceId, subCategoryId)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun addSpendingEntry(entry: SpendingEntry) {
        viewModelScope.launch {
            try {
                repository.addSpendingEntry(entry.copy(conferenceId = conferenceId))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun deleteSpendingEntry(entryId: String) {
        viewModelScope.launch {
            try {
                repository.deleteSpendingEntry(conferenceId, entryId)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun addPerson(name: String) {
        viewModelScope.launch {
            try {
                repository.addPerson(Person(name = name, conferenceId = conferenceId))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

class BudgetViewModelFactory(private val conferenceId: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BudgetViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BudgetViewModel(conferenceId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
