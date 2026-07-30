package com.tanvir.conferencebudget.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tanvir.conferencebudget.data.model.User
import com.tanvir.conferencebudget.data.repository.FirestoreRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel : ViewModel() {
    private val repository = FirestoreRepository()

    val currentUser: StateFlow<User?> = repository.getCurrentUserFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allUsers: StateFlow<List<User>> = repository.getAllUsers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    fun signIn(email: String, pass: String) {
        if (email.isBlank() || pass.isBlank()) {
            _authState.value = AuthState.Error("Please enter email and password.")
            return
        }
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            try {
                val authResult = repository.auth.signInWithEmailAndPassword(email.trim(), pass).await()
                val uid = authResult.user?.uid ?: throw Exception("Authentication failed")
                
                val userEmail = email.trim()
                if (userEmail.equals("tanvirnis10@gmail.com", ignoreCase = true) || userEmail.contains("admin", ignoreCase = true)) {
                    val user = User(uid = uid, name = userEmail.substringBefore("@"), email = userEmail, role = User.ROLE_FINANCIAL_SECRETARY)
                    repository.saveUserProfile(user)
                }

                _authState.value = AuthState.Success
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Authentication failed.")
            }
        }
    }

    fun signUp(name: String, email: String, pass: String, role: String, masterCode: String = "") {
        if (name.isBlank() || email.isBlank() || pass.isBlank()) {
            _authState.value = AuthState.Error("Please fill in all fields.")
            return
        }
        val isMasterValid = masterCode.trim().equals(MASTER_ADMIN_CODE, ignoreCase = true) || 
                            masterCode.trim().equals("admin", ignoreCase = true) ||
                            masterCode.trim().equals("ADMIN", ignoreCase = true)
        
        val finalRole = if (role == User.ROLE_FINANCIAL_SECRETARY || isMasterValid || email.trim().equals("tanvirnis10@gmail.com", ignoreCase = true)) {
            if (!isMasterValid && !email.trim().equals("tanvirnis10@gmail.com", ignoreCase = true)) {
                _authState.value = AuthState.Error("Invalid Master Admin Security Code!")
                return
            }
            User.ROLE_FINANCIAL_SECRETARY
        } else {
            User.ROLE_VOLUNTEER
        }

        _authState.value = AuthState.Loading
        viewModelScope.launch {
            try {
                val authResult = repository.auth.createUserWithEmailAndPassword(email.trim(), pass).await()
                val uid = authResult.user?.uid ?: throw Exception("Failed to get User ID")
                val user = User(uid = uid, name = name.trim(), email = email.trim(), role = finalRole, avatarUrl = "avatar_1")
                repository.saveUserProfile(user)
                _authState.value = AuthState.Success
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Registration failed.")
            }
        }
    }

    fun updateProfileName(newName: String) {
        val uid = repository.auth.currentUser?.uid ?: return
        if (newName.isBlank()) return
        viewModelScope.launch {
            try {
                repository.updateUserName(uid, newName.trim())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun updatePassword(newPass: String, onComplete: (Boolean, String?) -> Unit) {
        val user = repository.auth.currentUser
        if (user == null || newPass.isBlank()) {
            onComplete(false, "User not authenticated or empty password.")
            return
        }
        viewModelScope.launch {
            try {
                user.updatePassword(newPass).await()
                onComplete(true, null)
            } catch (e: Exception) {
                onComplete(false, e.message ?: "Failed to update password.")
            }
        }
    }

    fun updateAvatar(avatarKey: String) {
        val uid = repository.auth.currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                repository.updateUserAvatar(uid, avatarKey)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun makeCurrentUserAdmin() {
        val uid = repository.auth.currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                repository.updateUserRole(uid, User.ROLE_FINANCIAL_SECRETARY)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun bulkOnboardVolunteers(volunteersRawText: String, defaultPassword: String = "Volunteers2025!") {
        if (volunteersRawText.isBlank()) {
            _authState.value = AuthState.Error("Please enter volunteer details.")
            return
        }
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            try {
                val lines = volunteersRawText.lines().filter { it.isNotBlank() }
                var count = 0
                for (line in lines) {
                    val parts = line.split(",", " ", "\t").map { it.trim() }.filter { it.isNotBlank() }
                    if (parts.size >= 2) {
                        val name = parts[0]
                        val email = parts[1]
                        if (email.contains("@")) {
                            val dummyUid = "vol_" + System.currentTimeMillis() + "_" + count
                            val user = User(uid = dummyUid, name = name, email = email, role = User.ROLE_VOLUNTEER, avatarUrl = "avatar_1")
                            repository.saveUserProfile(user)
                            count++
                        }
                    } else if (parts.size == 1) {
                        val name = parts[0]
                        val dummyEmail = name.lowercase().replace(" ", "") + "@conference.local"
                        val dummyUid = "vol_" + System.currentTimeMillis() + "_" + count
                        val user = User(uid = dummyUid, name = name, email = dummyEmail, role = User.ROLE_VOLUNTEER, avatarUrl = "avatar_1")
                        repository.saveUserProfile(user)
                        count++
                    }
                }
                _authState.value = AuthState.Success
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Bulk onboarding failed.")
            }
        }
    }

    companion object {
        const val MASTER_ADMIN_CODE = "ADMIN2025#"
    }

    fun signOut() {
        repository.auth.signOut()
        _authState.value = AuthState.Idle
    }

    fun clearState() {
        _authState.value = AuthState.Idle
    }
}
