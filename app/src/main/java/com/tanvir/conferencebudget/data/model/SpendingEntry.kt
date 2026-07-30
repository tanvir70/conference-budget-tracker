package com.tanvir.conferencebudget.data.model

data class SpendingEntry(
    val id: String = "",
    val subCategoryId: String = "",
    val categoryId: String = "",
    val conferenceId: String = "",
    val amount: Double = 0.0,
    val date: String = "",
    val note: String = "",
    val spentByUserId: String = "",
    val spentByName: String = ""
)
