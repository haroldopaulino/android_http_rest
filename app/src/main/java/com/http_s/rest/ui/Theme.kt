package com.http_s.rest.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColors = darkColorScheme(
    primary = Color(0xFF8ECAFF),
    onPrimary = Color(0xFF00344F),
    secondary = Color(0xFFBBC7D6),
    tertiary = Color(0xFFD6BEE4),
    background = Color(0xFF101418),
    surface = Color(0xFF171C20),
    surfaceVariant = Color(0xFF40484F),
    onBackground = Color(0xFFE1E3E8),
    onSurface = Color(0xFFE1E3E8)
)

private val LightColors = lightColorScheme()

@Composable
fun HttpRestTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme || isSystemInDarkTheme()) DarkColors else LightColors,
        typography = MaterialTheme.typography,
        content = content
    )
}
