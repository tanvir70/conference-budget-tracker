package com.tanvir.conferencebudget.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tanvir.conferencebudget.data.model.Conference
import com.tanvir.conferencebudget.data.repository.FirestoreRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ConferenceViewModel : ViewModel() {
    private val repository = FirestoreRepository()
    
    val conferences: StateFlow<List<Conference>> = repository.getConferences()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    private val _userName = MutableStateFlow("")
    val userName: StateFlow<String> = _userName.asStateFlow()
    
    fun setUserName(name: String) {
        _userName.value = name
    }
    
    fun addConference(name: String, date: String, estimatedBudget: Double) {
        viewModelScope.launch {
            try {
                val conference = Conference(
                    name = name,
                    date = date,
                    totalEstimatedBudget = estimatedBudget,
                    createdBy = _userName.value
                )
                repository.addConference(conference)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    fun deleteConference(id: String) {
        viewModelScope.launch {
            try {
                repository.deleteConference(id)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
