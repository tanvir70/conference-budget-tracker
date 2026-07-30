package com.tanvir.conferencebudget.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tanvir.conferencebudget.data.model.CashTransaction
import com.tanvir.conferencebudget.data.model.Expenditure
import com.tanvir.conferencebudget.data.model.Person
import com.tanvir.conferencebudget.data.repository.FirestoreRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PersonViewModel(
    private val conferenceId: String,
    private val personId: String
) : ViewModel() {
    private val repository = FirestoreRepository()
    
    val person: StateFlow<Person?> = repository.getPersons(conferenceId)
        .map { persons -> persons.find { it.id == personId } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )
        
    val cashTransactions: StateFlow<List<CashTransaction>> = repository.getCashTransactions(conferenceId, personId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
        
    val expenditures: StateFlow<List<Expenditure>> = repository.getExpenditures(conferenceId, personId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
        
    val totalReceived: StateFlow<Double> = cashTransactions
        .map { txs -> txs.sumOf { it.amount } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)
        
    val totalSpent: StateFlow<Double> = expenditures
        .map { exps -> exps.sumOf { it.amount } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)
        
    val remaining: StateFlow<Double> = combine(totalReceived, totalSpent) { received, spent ->
        received - spent
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)
    
    fun addCashTransaction(amount: Double, fromWhom: String, date: String, note: String) {
        viewModelScope.launch {
            try {
                val tx = CashTransaction(
                    personId = personId,
                    conferenceId = conferenceId,
                    amount = amount,
                    fromWhom = fromWhom,
                    date = date,
                    note = note
                )
                repository.addCashTransaction(conferenceId, personId, tx)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    fun addExpenditure(item: String, amount: Double, date: String, note: String) {
        viewModelScope.launch {
            try {
                val exp = Expenditure(
                    personId = personId,
                    conferenceId = conferenceId,
                    item = item,
                    amount = amount,
                    date = date,
                    note = note
                )
                repository.addExpenditure(conferenceId, personId, exp)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    fun deleteCashTransaction(txId: String) {
        viewModelScope.launch {
            try {
                repository.deleteCashTransaction(conferenceId, personId, txId)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    fun deleteExpenditure(expId: String) {
        viewModelScope.launch {
            try {
                repository.deleteExpenditure(conferenceId, personId, expId)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

class PersonViewModelFactory(
    private val conferenceId: String,
    private val personId: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PersonViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PersonViewModel(conferenceId, personId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
