package com.tanvir.conferencebudget.ui.theme

import androidx.compose.ui.graphics.Color

// Modern Gen-Z Deep Teal & Mint Emerald Palette (Inspired by TrackMyBill & Rocket Money)

// Primary Deep Teal Branding
val DeepTealPrimary = Color(0xFF093A34)      // Main Header / Hero Card Container
val DeepTealDark = Color(0xFF052723)         // Ultra Dark Teal Surface
val DeepTealLight = Color(0xFF0F5048)        // Light Teal Card Fill

// Vivid Mint & Emerald Accents
val MintEmerald = Color(0xFF10B981)          // Primary Action Accent & Positive Balance
val MintEmeraldLight = Color(0xFFD1FAE5)     // Light Mint Badge Fill
val MintEmeraldDark = Color(0xFF047857)      // Dark Mint Text

// Soft Sage & Soft Sand Neutral Surfaces
val SageSoft = Color(0xFFE8F5E9)             // Soft Sage Badge Background
val SandBackground = Color(0xFFF4F7F6)       // Ultra-Clean Sand Off-White App Background
val CardSurfaceWhite = Color(0xFFFFFFFF)     // Pure White Card Container
val CardBorderStroke = Color(0xFFE2E8F0)     // Subtle Border Stroke

// Status & Accent Colors
val AccentOrange = Color(0xFFF59E0B)         // Warning / Partial Status
val AccentRed = Color(0xFFEF4444)            // Due / Over-budget Red
val AccentBlue = Color(0xFF0284C7)           // Info Sky Blue
val AccentPurple = Color(0xFF8B5CF6)         // Accent Purple

// Material 3 Color Scheme Tokens
val md_theme_light_primary = DeepTealPrimary
val md_theme_light_onPrimary = Color(0xFFFFFFFF)
val md_theme_light_primaryContainer = DeepTealPrimary
val md_theme_light_onPrimaryContainer = Color(0xFFFFFFFF)

val md_theme_light_secondary = MintEmerald
val md_theme_light_onSecondary = Color(0xFFFFFFFF)
val md_theme_light_secondaryContainer = MintEmeraldLight
val md_theme_light_onSecondaryContainer = MintEmeraldDark

val md_theme_light_tertiary = AccentBlue
val md_theme_light_onTertiary = Color(0xFFFFFFFF)
val md_theme_light_tertiaryContainer = Color(0xFFE0F2FE)
val md_theme_light_onTertiaryContainer = Color(0xFF0369A1)

val md_theme_light_surface = CardSurfaceWhite
val md_theme_light_onSurface = Color(0xFF0F172A)
val md_theme_light_surfaceVariant = SandBackground
val md_theme_light_onSurfaceVariant = Color(0xFF475569)

val md_theme_dark_primary = MintEmerald
val md_theme_dark_onPrimary = DeepTealDark
val md_theme_dark_primaryContainer = DeepTealPrimary
val md_theme_dark_onPrimaryContainer = Color(0xFFE6FFFA)

val md_theme_dark_secondary = MintEmerald
val md_theme_dark_onSecondary = DeepTealDark
val md_theme_dark_secondaryContainer = DeepTealLight
val md_theme_dark_onSecondaryContainer = MintEmeraldLight

val md_theme_dark_tertiary = AccentBlue
val md_theme_dark_onTertiary = DeepTealDark
val md_theme_dark_tertiaryContainer = Color(0xFF0369A1)
val md_theme_dark_onTertiaryContainer = Color(0xFFE0F2FE)

val md_theme_dark_surface = DeepTealDark
val md_theme_dark_onSurface = Color(0xFFF8FAFC)
val md_theme_dark_surfaceVariant = DeepTealLight
val md_theme_dark_onSurfaceVariant = Color(0xFF94A3B8)
