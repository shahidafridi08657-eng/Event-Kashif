package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val EventKashifDarkColorScheme = darkColorScheme(
    primary = DarkPrimaryGreen,
    onPrimary = Color.Black,
    primaryContainer = DarkGreenPressed,
    onPrimaryContainer = Color.White,
    secondary = DarkBlueAccent,
    onSecondary = Color.Black,
    secondaryContainer = DarkElevatedSurface,
    onSecondaryContainer = DarkPrimaryText,
    tertiary = DarkPurpleAccent,
    onTertiary = Color.White,
    background = DarkMainBackground,
    onBackground = DarkPrimaryText,
    surface = DarkCardBackground,
    onSurface = DarkPrimaryText,
    surfaceVariant = DarkElevatedSurface,
    onSurfaceVariant = DarkSecondaryText,
    outline = DarkBorder,
    error = DarkError
)

private val EventKashifLightColorScheme = lightColorScheme(
    primary = LightPrimaryGreen,
    onPrimary = Color.White,
    primaryContainer = LightGreenPressed,
    onPrimaryContainer = Color.White,
    secondary = LightBlueAccent,
    onSecondary = Color.White,
    secondaryContainer = LightElevatedSurface,
    onSecondaryContainer = LightPrimaryText,
    tertiary = LightPurpleAccent,
    onTertiary = Color.White,
    background = LightMainBackground,
    onBackground = LightPrimaryText,
    surface = LightCardBackground,
    onSurface = LightPrimaryText,
    surfaceVariant = LightElevatedSurface,
    onSurfaceVariant = LightSecondaryText,
    outline = LightBorder,
    error = LightError
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) EventKashifDarkColorScheme else EventKashifLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
