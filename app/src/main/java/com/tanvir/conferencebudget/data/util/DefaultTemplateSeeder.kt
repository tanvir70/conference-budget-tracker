package com.tanvir.conferencebudget.data.util

import com.tanvir.conferencebudget.data.model.Category
import com.tanvir.conferencebudget.data.model.SubCategory
import com.tanvir.conferencebudget.data.repository.FirestoreRepository
import kotlinx.coroutines.flow.firstOrNull

object DefaultTemplateSeeder {

    private data class CategoryTemplate(
        val categoryName: String,
        val order: Int,
        val subCategoryNames: List<String>
    )

    private val defaultTemplates = listOf(
        CategoryTemplate(
            categoryName = "Venue & Facility Setup",
            order = 1,
            subCategoryNames = listOf(
                "Venue (KIB Auditorium)",
                "KIB Venue Security Money",
                "15% VAT on Food"
            )
        ),
        CategoryTemplate(
            categoryName = "Food & Catering",
            order = 2,
            subCategoryNames = listOf(
                "Lunch",
                "Snacks",
                "Water",
                "Tissue Boxes",
                "Soft Drinks",
                "Coffee"
            )
        ),
        CategoryTemplate(
            categoryName = "Stage, AV & Furniture Logistics",
            order = 3,
            subCategoryNames = listOf(
                "LED Screen",
                "LED Base",
                "Carpet",
                "Registration Desk",
                "Single Sofa",
                "Furniture & Stage Logistics",
                "Black Seat Covers",
                "Sound System"
            )
        ),
        CategoryTemplate(
            categoryName = "Media, Videography & Audio Recording",
            order = 4,
            subCategoryNames = listOf(
                "Videography & Photography",
                "Q&A Handheld Mics",
                "Clip Mics"
            )
        ),
        CategoryTemplate(
            categoryName = "Branding, Photo Booth & Printing",
            order = 5,
            subCategoryNames = listOf(
                "Backdrop Photo Booth",
                "Single Photo Booth",
                "Backdrop Extra & Transport",
                "X-Stand Banners",
                "ID Cards",
                "Food Tokens",
                "Branded Stickers",
                "General Print Items"
            )
        ),
        CategoryTemplate(
            categoryName = "Swag, Merchandise & Gifts",
            order = 6,
            subCategoryNames = listOf(
                "T-Shirts",
                "Attendee Pens & Notebooks",
                "Volunteer & Speaker Mugs",
                "VIP Pens & Notebooks",
                "Organizer Jute Bags"
            )
        ),
        CategoryTemplate(
            categoryName = "Tech, Software & SMS Marketing",
            order = 7,
            subCategoryNames = listOf(
                "Venue Dedicated Internet",
                "SMS Recharge (REVE)",
                "SMS Recharge (Durbar)",
                "Server & Domain (2025-2026)",
                "Google Drive Cloud Storage",
                "Digital Raffle Draw"
            )
        )
    )

    suspend fun preloadDefaultTemplatesIfEmpty(repository: FirestoreRepository, conferenceId: String) {
        if (conferenceId.isBlank()) return
        val existingCategories = repository.getCategories(conferenceId).firstOrNull()
        if (!existingCategories.isNullOrEmpty()) {
            return
        }

        for (template in defaultTemplates) {
            val catId = repository.addCategory(
                Category(
                    conferenceId = conferenceId,
                    name = template.categoryName,
                    order = template.order
                )
            )

            for (subName in template.subCategoryNames) {
                repository.addSubCategory(
                    SubCategory(
                        categoryId = catId,
                        conferenceId = conferenceId,
                        name = subName,
                        details = "",
                        cost = 0.0,
                        responsiblePerson = "",
                        status = "Pending",
                        notes = ""
                    )
                )
            }
        }
    }
}
