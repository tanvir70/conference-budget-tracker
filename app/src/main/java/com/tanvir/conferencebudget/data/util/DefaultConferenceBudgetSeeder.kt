package com.tanvir.conferencebudget.data.util

import com.tanvir.conferencebudget.data.model.Category
import com.tanvir.conferencebudget.data.model.SpendingEntry
import com.tanvir.conferencebudget.data.model.SubCategory
import com.tanvir.conferencebudget.data.repository.FirestoreRepository
import kotlinx.coroutines.flow.firstOrNull

object DefaultConferenceBudgetSeeder {

    private data class SubCategorySeed(
        val name: String,
        val details: String,
        val cost: Double,
        val paidAmount: Double,
        val responsiblePerson: String,
        val status: String = "Pending",
        val notes: String = ""
    )

    private data class CategorySeed(
        val categoryName: String,
        val order: Int,
        val items: List<SubCategorySeed>
    )

    private val seedCategories = listOf(
        CategorySeed(
            categoryName = "Venue & Facility Setup",
            order = 1,
            items = listOf(
                SubCategorySeed("Venue (KIB Auditorium)", "Main Auditorium Booking", 235175.0, 235175.0, "Apu", "Cleared", "Booked"),
                SubCategorySeed("KIB Venue Security Money", "Refundable security deposit", 25000.0, 25000.0, "Team", "Cleared", "Team lunch / Refundable"),
                SubCategorySeed("15% VAT on Food", "KIB venue demand tax", 10000.0, 10000.0, "Apu", "Cleared", "Mandatory KIB tax")
            )
        ),
        CategorySeed(
            categoryName = "Food & Catering",
            order = 2,
            items = listOf(
                SubCategorySeed("Lunch", "300 Packs", 81000.0, 41000.0, "Shahriar", "Partial", "300 pack er kotha bole booking dewa hoiche. ticket sell kom hole komaite hobe kisu packet"),
                SubCategorySeed("Snacks", "300 Packs", 42000.0, 0.0, "Shahriar", "Pending"),
                SubCategorySeed("Water", "700 Bottles", 6500.0, 0.0, "Shahriar", "Pending"),
                SubCategorySeed("Tissue Boxes", "8 box square napkin, 5 box facial", 1000.0, 0.0, "Shahriar", "Pending"),
                SubCategorySeed("Soft Drinks", "312 Bottles", 5200.0, 5200.0, "Shahriar", "Cleared"),
                SubCategorySeed("Coffee", "750 Cups", 12750.0, 0.0, "Shahriar", "Pending")
            )
        ),
        CategorySeed(
            categoryName = "Stage, AV & Furniture Logistics",
            order = 3,
            items = listOf(
                SubCategorySeed("LED Screen", "20x10 feet LED Wall", 28000.0, 28000.0, "Apu", "Cleared"),
                SubCategorySeed("LED Base", "Base support frame", 5000.0, 5000.0, "Apu", "Cleared"),
                SubCategorySeed("Carpet", "1 pc Stage Carpet", 4000.0, 4000.0, "Apu", "Cleared"),
                SubCategorySeed("Registration Desk", "Registration & Coffee desks (2 pcs)", 1000.0, 1000.0, "Apu", "Cleared"),
                SubCategorySeed("Single Sofa", "8 pcs Speaker Sofas", 7000.0, 7000.0, "Apu", "Cleared"),
                SubCategorySeed("Furniture & Stage Logistics", "Transport for LED, carpet, desk, sofa", 5000.0, 5000.0, "Apu", "Cleared"),
                SubCategorySeed("Black Seat Covers", "Covers for back seats in KIB", 6500.0, 6500.0, "Apu", "Cleared"),
                SubCategorySeed("Sound System", "KIB's sound system check", 0.0, 0.0, "KIB", "Pending")
            )
        ),
        CategorySeed(
            categoryName = "Media, Videography & Audio Recording",
            order = 4,
            items = listOf(
                SubCategorySeed("Videography & Photography", "2 Videographers + 1 Photographer", 25000.0, 15000.0, "Shahriar", "Partial", "Booked"),
                SubCategorySeed("Q&A Handheld Mics", "Attendee Q&A mics (2 pcs)", 0.0, 0.0, "KIB provided", "Cleared"),
                SubCategorySeed("Clip Mics", "Audio recording clip mics (2 pcs)", 0.0, 0.0, "Shahriar", "Pending")
            )
        ),
        CategorySeed(
            categoryName = "Branding, Photo Booth & Printing",
            order = 5,
            items = listOf(
                SubCategorySeed("Backdrop Photo Booth", "Main Photo Backdrop (1 pc)", 5900.0, 5900.0, "Apu & Mosharraf", "Cleared"),
                SubCategorySeed("Single Photo Booth", "Single photo stand", 3800.0, 0.0, "Apu", "Pending", "Approx 3800"),
                SubCategorySeed("Backdrop Extra & Transport", "Print extra 1200+140 & Shuvo vai cost", 3240.0, 3240.0, "Apu", "Cleared"),
                SubCategorySeed("X-Stand Banners", "3 Standees", 450.0, 450.0, "Shahriar & Mosharraf", "Cleared"),
                SubCategorySeed("ID Cards", "288 Badges (3 Color Themes)", 5628.0, 5628.0, "Shahriar & Mosharraf", "Cleared"),
                SubCategorySeed("Food Tokens", "300 Printed Tokens", 700.0, 700.0, "Shahriar & Mosharraf", "Cleared"),
                SubCategorySeed("Branded Stickers", "1200 Stickers (4 Designs x 300)", 9000.0, 9000.0, "Shahriar & Mosharraf", "Cleared"),
                SubCategorySeed("General Print Items", "Miscellaneous print buffer", 0.0, 0.0, "Shahriar & Mosharraf", "Pending")
            )
        ),
        CategorySeed(
            categoryName = "Swag, Merchandise & Gifts",
            order = 6,
            items = listOf(
                SubCategorySeed("T-Shirts", "290 Pcs (@ ৳360) + 4 Samples", 105650.0, 105650.0, "Apu", "Cleared", "Includes ৳750 + ৳500 samples"),
                SubCategorySeed("Attendee Pens & Notebooks", "290 Sets (@ ~৳100)", 29000.0, 0.0, "Shahriar & Mosharraf", "Pending", "Approx 100 tk per unit"),
                SubCategorySeed("Volunteer & Speaker Mugs", "17 Branded Mugs", 10200.0, 8000.0, "Shahriar & Mosharraf", "Partial", "Advance paid"),
                SubCategorySeed("VIP Pens & Notebooks", "17 Premium Sets", 3910.0, 3910.0, "Shahriar & Mosharraf", "Cleared"),
                SubCategorySeed("Organizer Jute Bags", "25 Premium Jute Bags", 14125.0, 14125.0, "Apu", "Cleared", "Cleared")
            )
        ),
        CategorySeed(
            categoryName = "Tech, Software & SMS Marketing",
            order = 7,
            items = listOf(
                SubCategorySeed("Venue Dedicated Internet", "High-speed Wi-Fi Line", 3500.0, 3500.0, "Apu", "Cleared"),
                SubCategorySeed("SMS Recharge (REVE)", "SMS Credit Top-up", 3000.0, 3000.0, "Apu", "Cleared"),
                SubCategorySeed("SMS Recharge (Durbar)", "SMS Credit Top-up", 1000.0, 1000.0, "Apu", "Cleared"),
                SubCategorySeed("Server & Domain (2025-2026)", "Hosting & Domain renewals", 40000.0, 0.0, "Rokon vai", "Pending"),
                SubCategorySeed("Google Drive Cloud Storage", "2026 Storage subscription", 8100.0, 0.0, "Rokon vai", "Pending"),
                SubCategorySeed("Digital Raffle Draw", "Digital Raffle module", 0.0, 0.0, "Tech Team", "Pending")
            )
        )
    )

    suspend fun preloadDefaultBudgetIfEmpty(repository: FirestoreRepository, conferenceId: String) {
        if (conferenceId.isBlank()) return
        val existingCategories = repository.getCategories(conferenceId).firstOrNull()
        if (!existingCategories.isNullOrEmpty()) {
            // Already initialized for this conference
            return
        }

        for (cSeed in seedCategories) {
            val catId = repository.addCategory(
                Category(
                    conferenceId = conferenceId,
                    name = cSeed.categoryName,
                    order = cSeed.order
                )
            )

            for (subSeed in cSeed.items) {
                val subCatId = repository.addSubCategory(
                    SubCategory(
                        categoryId = catId,
                        conferenceId = conferenceId,
                        name = subSeed.name,
                        details = subSeed.details,
                        cost = subSeed.cost,
                        responsiblePerson = subSeed.responsiblePerson,
                        status = subSeed.status,
                        notes = subSeed.notes
                    )
                )

                if (subSeed.paidAmount > 0) {
                    repository.addSpendingEntry(
                        SpendingEntry(
                            subCategoryId = subCatId,
                            categoryId = catId,
                            conferenceId = conferenceId,
                            amount = subSeed.paidAmount,
                            date = "Aug 08, 2025",
                            note = if (subSeed.notes.isNotBlank()) subSeed.notes else "Initial payment",
                            spentByName = subSeed.responsiblePerson
                        )
                    )
                }
            }
        }
    }
}
