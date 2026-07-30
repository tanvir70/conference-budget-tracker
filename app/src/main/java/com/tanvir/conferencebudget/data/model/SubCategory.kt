package com.tanvir.conferencebudget.data.model

data class SubCategory(
    val id: String = "",
    val categoryId: String = "",
    val conferenceId: String = "",
    val name: String = "",
    val details: String = "",
    val estimatedCost: Double = 0.0,
    val assignedVolunteerId: String = "",
    val assignedVolunteerName: String = "",
    val status: String = "Pending", // "Pending", "Partial", "Cleared"
    val notes: String = ""
)
