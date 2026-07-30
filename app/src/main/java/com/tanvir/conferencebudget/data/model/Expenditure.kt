package com.tanvir.conferencebudget.data.model

data class Expenditure(
    val id: String = "",
    val personId: String = "",
    val conferenceId: String = "",
    val item: String = "",
    val amount: Double = 0.0,
    val date: String = "",
    val note: String = ""
)
