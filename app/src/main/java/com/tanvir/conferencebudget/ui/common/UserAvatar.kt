package com.tanvir.conferencebudget.ui.common

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tanvir.conferencebudget.data.model.User

val avatarGradients = listOf(
    Color(0xFF10B981), // Emerald
    Color(0xFF0D5C50), // Teal
    Color(0xFF0284C7), // Sky Blue
    Color(0xFFF59E0B), // Amber
    Color(0xFFEC4899), // Pink
    Color(0xFF8B5CF6)  // Purple
)

fun getAvatarColor(avatarKey: String): Color {
    val index = (avatarKey.hashCode() and 0x7FFFFFFF) % avatarGradients.size
    return avatarGradients[index]
}

@Composable
fun UserAvatar(
    user: User?,
    size: Dp = 38.dp,
    onClick: (() -> Unit)? = null
) {
    val initials = user?.initials ?: "U"
    val avatarKey = (user?.avatarUrl ?: "").ifEmpty { user?.uid ?: "1" }
    val bgColor = getAvatarColor(avatarKey)

    Surface(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .border(1.5.dp, Color.White.copy(alpha = 0.6f), CircleShape)
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        shape = CircleShape,
        color = bgColor,
        tonalElevation = 4.dp
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = initials,
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
                fontSize = (size.value * 0.42f).sp
            )
        }
    }
}
