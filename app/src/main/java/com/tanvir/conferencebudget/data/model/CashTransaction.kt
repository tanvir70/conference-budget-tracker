package com.tanvir.conferencebudget.data.model

data class CashTransaction(
    val id: String = "",
    val personId: String = "",
    val conferenceId: String = "",
    val amount: Double = 0.0,
    val fromWhom: String = "",
    val date: String = "",
    val note: String = ""
)
