package com.tanvir.conferencebudget.data.model

data class Conference(
    val id: String = "",
    val name: String = "",
    val date: String = "",
    val totalEstimatedBudget: Double = 0.0,
    val createdAt: Long = System.currentTimeMillis(),
    val createdBy: String = ""
)
