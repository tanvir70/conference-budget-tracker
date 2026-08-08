package com.tanvir.conferencebudget.data.model

data class SubCategory(
    val id: String = "",
    val categoryId: String = "",
    val conferenceId: String = "",
    val name: String = "",
    val details: String = "",
    val cost: Double = 0.0,
    val responsiblePerson: String = "",
    val status: String = "Pending", // "Pending", "Partial", "Cleared"
    val notes: String = ""
)
