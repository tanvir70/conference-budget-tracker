package com.tanvir.conferencebudget.data.model

data class BudgetItem(
    val id: String = "",
    val conferenceId: String = "",
    val name: String = "",
    val details: String = "",
    val responsiblePerson: String = "",
    val pieces: Int = 1,
    val cost: Double = 0.0,
    val actualCost: Double = 0.0,
    val paid: Double = 0.0,
    val status: String = "Pending",
    val notes: String = ""
) {
    val due: Double get() = cost - paid
}
