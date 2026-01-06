package com.wahyuagast.keamanansisteminformasimobile.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.material3.LocalTextStyle

private val DarkColorScheme = darkColorScheme(
    primary = CustomPrimary,
    secondary = CustomSuccess,
    tertiary = CustomWarning,
    background = CustomBlack, // Dark mode assumption
    surface = CustomBlack
)

private val LightColorScheme = lightColorScheme(
    primary = CustomPrimary,
    secondary = CustomSuccess,
    tertiary = CustomWarning,
    background = CustomBackground,
    surface = CustomWhite,
    onPrimary = CustomWhite,
    onSecondary = CustomWhite,
    onTertiary = CustomWhite,
    onBackground = CustomBlack,
    onSurface = CustomBlack,
)

@Composable
fun KeamananSistemInformasiMobileTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    // Disabling dynamic color to ensure design match
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = {
            // Ensure any Text() that doesn't pass an explicit style still uses our Material typography (Inter)
            CompositionLocalProvider(LocalTextStyle provides Typography.bodyMedium) {
                content()
            }
        }
    )
}