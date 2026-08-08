package com.tanvir.conferencebudget.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class CategoryStyle(
    val icon: ImageVector,
    val bgColor: Color,
    val iconColor: Color
)

fun getCategoryStyle(categoryName: String): CategoryStyle {
    val nameLower = categoryName.lowercase()
    return when {
        nameLower.contains("food") || nameLower.contains("snack") || nameLower.contains("catering") || nameLower.contains("dinner") || nameLower.contains("lunch") ->
            CategoryStyle(Icons.Default.Restaurant, Color(0xFFFFECEB), Color(0xFFE53935))

        nameLower.contains("transport") || nameLower.contains("bus") || nameLower.contains("car") || nameLower.contains("travel") || nameLower.contains("vehicle") ->
            CategoryStyle(Icons.Default.DirectionsBus, Color(0xFFE3F2FD), Color(0xFF1E88E5))

        nameLower.contains("hotel") || nameLower.contains("housing") || nameLower.contains("accommodation") || nameLower.contains("stay") || nameLower.contains("room") ->
            CategoryStyle(Icons.Default.Hotel, Color(0xFFFFF8E1), Color(0xFFFFA000))

        nameLower.contains("venue") || nameLower.contains("hall") || nameLower.contains("sound") || nameLower.contains("stage") || nameLower.contains("decor") ->
            CategoryStyle(Icons.Default.Mic, Color(0xFFF3E5F5), Color(0xFF8E24AA))

        nameLower.contains("media") || nameLower.contains("photo") || nameLower.contains("video") || nameLower.contains("print") || nameLower.contains("banner") ->
            CategoryStyle(Icons.Default.CameraAlt, Color(0xFFE0F7FA), Color(0xFF00ACC1))

        nameLower.contains("gift") || nameLower.contains("swag") || nameLower.contains("certificate") || nameLower.contains("merch") ->
            CategoryStyle(Icons.Default.CardGiftcard, Color(0xFFFCE4EC), Color(0xFFD81B60))

        nameLower.contains("volunteer") || nameLower.contains("person") || nameLower.contains("staff") || nameLower.contains("people") ->
            CategoryStyle(Icons.Default.People, Color(0xFFE8F5E9), Color(0xFF43A047))

        nameLower.contains("cash") || nameLower.contains("recharge") || nameLower.contains("fund") || nameLower.contains("income") ->
            CategoryStyle(Icons.Default.AccountBalanceWallet, Color(0xFFE0F2F1), Color(0xFF00897B))

        else ->
            CategoryStyle(Icons.Default.Category, Color(0xFFF5F5F5), Color(0xFF616161))
    }
}

@Composable
fun CategoryIconBadge(
    categoryName: String,
    size: Dp = 40.dp
) {
    val style = getCategoryStyle(categoryName)
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(style.bgColor),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = style.icon,
            contentDescription = categoryName,
            tint = style.iconColor,
            modifier = Modifier.size(size * 0.55f)
        )
    }
}
