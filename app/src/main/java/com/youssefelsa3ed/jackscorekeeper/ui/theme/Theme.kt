package com.youssefelsa3ed.jackscorekeeper.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

object AppColors {
    // Primary Colors
    val Primary = Color(0xFFD4A574)
    val OnPrimary = Color(0xFF3E2723)
    val PrimaryContainer = Color(0xFF1F1F1F)
    val OnPrimaryContainer = Color(0xFFFFFFFF)

    // Secondary Colors
    val Secondary = Color(0xFFCCC2B8)
    val OnSecondary = Color(0xFF322F2A)
    val SecondaryContainer = Color(0xFF49453F)
    val OnSecondaryContainer = Color(0xFFE8DDD4)

    // Tertiary Colors
    val Tertiary = Color(0xFFA4CFCF)
    val OnTertiary = Color(0xFF003737)
    val TertiaryContainer = Color(0xFF1E4E4E)
    val OnTertiaryContainer = Color(0xFFC0EBEB)

    // Error Colors
    val Error = Color(0xFFFFB4AB)
    val OnError = Color(0xFF690005)
    val ErrorContainer = Color(0xFF93000A)
    val OnErrorContainer = Color(0xFFFFDAD6)

    // Background Colors
    val Background = Color(0xFF141218) // Original: Dark background
    val OnBackground = Color(0xFFE6E1E5) // Light text on dark background

    // Surface Colors
    val Surface = Color(0xFF141218) // Dark surface
    val OnSurface = Color(0xFFE6E1E5) // Light text on dark surface
    val SurfaceVariant = Color(0xFF4A4458)
    val OnSurfaceVariant = Color(0xFFCCC2DC)
    val SurfaceTint = Color(0xFFD4A574)

    // Outline Colors
    val Outline = Color(0xFF958DA5)
    val OutlineVariant = Color(0xFF4A4458)

    // Additional Surface Colors (dark variations)
    val SurfaceDim = Color(0xFF141218)
    val SurfaceBright = Color(0xFF3B383E)
    val SurfaceContainerLowest = Color(0xFF0F0D13)
    val SurfaceContainerLow = Color(0xFF1C1B20)
    val SurfaceContainer = Color(0xFF201F25)
    val SurfaceContainerHigh = Color(0xFF2B292F)
    val SurfaceContainerHighest = Color(0xFF36343A)

    // Inverse Colors
    val InverseSurface = Color(0xFFE6E1E5)
    val InverseOnSurface = Color(0xFF322F35)
    val InversePrimary = Color(0xFF8B5A2B)
    val SuccessContainer = Color(0xFF0D4F3C) // Deep emerald
    val OnSuccessContainer = Color(0xFF4ADE80) // Bright green text

    // Scrim
    val Scrim = Color(0xFF000000)
}


val DarkColorScheme = darkColorScheme(
    primary = AppColors.Primary,
    onPrimary = AppColors.OnPrimary,
    primaryContainer = AppColors.PrimaryContainer,
    onPrimaryContainer = AppColors.OnPrimaryContainer,
    secondary = AppColors.Secondary,
    onSecondary = AppColors.OnSecondary,
    secondaryContainer = AppColors.SecondaryContainer,
    onSecondaryContainer = AppColors.OnSecondaryContainer,
    tertiary = AppColors.Tertiary,
    onTertiary = AppColors.OnTertiary,
    tertiaryContainer = AppColors.TertiaryContainer,
    onTertiaryContainer = AppColors.OnTertiaryContainer,
    error = AppColors.Error,
    onError = AppColors.OnError,
    errorContainer = AppColors.ErrorContainer,
    onErrorContainer = AppColors.OnErrorContainer,
    background = AppColors.Background,
    onBackground = AppColors.OnBackground,
    surface = AppColors.Surface,
    onSurface = AppColors.OnSurface,
    surfaceVariant = AppColors.SurfaceVariant,
    onSurfaceVariant = AppColors.OnSurfaceVariant,
    outline = AppColors.Outline,
    outlineVariant = AppColors.OutlineVariant,
    scrim = AppColors.Scrim,
    inverseSurface = AppColors.InverseSurface,
    inverseOnSurface = AppColors.InverseOnSurface,
    inversePrimary = AppColors.InversePrimary,
    surfaceDim = AppColors.SurfaceDim,
    surfaceBright = AppColors.SurfaceBright,
    surfaceContainerLowest = AppColors.SurfaceContainerLowest,
    surfaceContainerLow = AppColors.SurfaceContainerLow,
    surfaceContainer = AppColors.SurfaceContainer,
    surfaceContainerHigh = AppColors.SurfaceContainerHigh,
    surfaceContainerHighest = AppColors.SurfaceContainerHighest
)

@Composable
fun JackScoreKeeperTheme(
    isDynamicColorEnabled: Boolean = false,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val colorScheme = when {
        isDynamicColorEnabled && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            dynamicDarkColorScheme(context)
        }
        else -> DarkColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            setupSystemBars(window, view)
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
}

private fun setupSystemBars(window: android.view.Window, view: android.view.View) {
    /*window.statusBarColor = colorScheme.surface.toArgb()
    window.navigationBarColor = colorScheme.surface.toArgb()*/
    window.setNavigationBarContrastEnforced(false)
    val controller = WindowCompat.getInsetsController(window, view)
    controller.isAppearanceLightStatusBars = false
    controller.isAppearanceLightNavigationBars = false
}