package com.tanvir.conferencebudget.data.model

data class User(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val role: String = ROLE_VOLUNTEER, // "FINANCIAL_SECRETARY" or "VOLUNTEER"
    val avatarUrl: String = "avatar_1"
) {
    companion object {
        const val ROLE_FINANCIAL_SECRETARY = "FINANCIAL_SECRETARY"
        const val ROLE_VOLUNTEER = "VOLUNTEER"
    }

    val isFinancialSecretary: Boolean 
        get() = role == ROLE_FINANCIAL_SECRETARY || 
                role.contains("ADMIN", ignoreCase = true) || 
                role.contains("SECRETARY", ignoreCase = true)

    val initials: String
        get() {
            if (name.isBlank()) return "U"
            val parts = name.trim().split(" ").filter { it.isNotBlank() }
            return if (parts.size >= 2) {
                "${parts[0].first().uppercaseChar()}${parts[1].first().uppercaseChar()}"
            } else {
                name.take(2).uppercase()
            }
        }
}
