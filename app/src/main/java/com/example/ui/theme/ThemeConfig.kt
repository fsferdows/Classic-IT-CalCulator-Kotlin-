package com.example.ui.theme

import androidx.compose.ui.graphics.Color

enum class CalculatorTheme(val displayName: String, val isDark: Boolean) {
    CLASSIC_GOLD("Classic Gold", true),
    COSMIC_DARK("Cosmic Dark", true),
    LIGHT_PREMIUM("Light Premium", false),
    AMBER_SUNSET("Amber Sunset", true),
    EMERALD_ZEN("Emerald Zen", true),
    CYBERPUNK("Cyberpunk", true),
    OLED_DARK("OLED Dark", true)
}

data class ThemeColors(
    val background: Color,
    val surface: Color,
    val surfaceGlass: Color,
    val borderGlass: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val btnNumber: Color,
    val btnOperator: Color,
    val btnFunction: Color,
    val btnEquals: Color,
    val accent: Color,
    val secondary: Color
)

fun getThemeColors(theme: CalculatorTheme): ThemeColors {
    return when (theme) {
        CalculatorTheme.CLASSIC_GOLD -> ThemeColors(
            background = Color(0xFF0A0E27),
            surface = Color(0xFF0B1437),
            surfaceGlass = Color(0x261E3A8A),
            borderGlass = Color(0x66D4AF37),
            textPrimary = Color(0xFFFFFFFF),
            textSecondary = Color(0xFFC0C0C0),
            btnNumber = Color(0xFF0B1437),
            btnOperator = Color(0xFF1E3A8A),
            btnFunction = Color(0xFF111E52),
            btnEquals = Color(0xFFD4AF37),
            accent = Color(0xFFD4AF37),
            secondary = Color(0xFF1E3A8A)
        )
        CalculatorTheme.COSMIC_DARK -> ThemeColors(
            background = Color(0xFF0F0F1E),
            surface = Color(0xFF1E1E2F),
            surfaceGlass = Color(0x1F2A2A3D),
            borderGlass = Color(0x3300D9FF),
            textPrimary = Color(0xFFFFFFFF),
            textSecondary = Color(0xFF8E8EAF),
            btnNumber = Color(0xFF2A2A3D),
            btnOperator = Color(0xFF3D3D5C),
            btnFunction = Color(0xFF00D9FF),
            btnEquals = Color(0xFFFF6B6B),
            accent = Color(0xFFFF6B6B),
            secondary = Color(0xFF00D9FF)
        )
        CalculatorTheme.LIGHT_PREMIUM -> ThemeColors(
            background = Color(0xFFF5F5F7),
            surface = Color(0xFFFFFFFF),
            surfaceGlass = Color(0xB3FFFFFF),
            borderGlass = Color(0x4D1E1E2F),
            textPrimary = Color(0xFF1E1E2F),
            textSecondary = Color(0xFF7A7A8A),
            btnNumber = Color(0xFFE5E5EA),
            btnOperator = Color(0xFFD1D1D6),
            btnFunction = Color(0xFF007AFF),
            btnEquals = Color(0xFFFF3B30),
            accent = Color(0xFFFF3B30),
            secondary = Color(0xFF007AFF)
        )
        CalculatorTheme.AMBER_SUNSET -> ThemeColors(
            background = Color(0xFF141416),
            surface = Color(0xFF1F1F23),
            surfaceGlass = Color(0x1FFF9F0A),
            borderGlass = Color(0x4DFF9F0A),
            textPrimary = Color(0xFFFFFFFF),
            textSecondary = Color(0xFF9E9E9E),
            btnNumber = Color(0xFF2C2C30),
            btnOperator = Color(0xFF3E3E42),
            btnFunction = Color(0xFFFF9F0A),
            btnEquals = Color(0xFFFF453A),
            accent = Color(0xFFFF453A),
            secondary = Color(0xFFFF9F0A)
        )
        CalculatorTheme.EMERALD_ZEN -> ThemeColors(
            background = Color(0xFF0C1612),
            surface = Color(0xFF152A21),
            surfaceGlass = Color(0x1F30D158),
            borderGlass = Color(0x4D30D158),
            textPrimary = Color(0xFFE8F5E9),
            textSecondary = Color(0xFF81C784),
            btnNumber = Color(0xFF1D3B2E),
            btnOperator = Color(0xFF27503E),
            btnFunction = Color(0xFF30D158),
            btnEquals = Color(0xFFFF9F0A),
            accent = Color(0xFFFF9F0A),
            secondary = Color(0xFF30D158)
        )
        CalculatorTheme.CYBERPUNK -> ThemeColors(
            background = Color(0xFF12041D),
            surface = Color(0xFF210936),
            surfaceGlass = Color(0x1FFF007F),
            borderGlass = Color(0x4DFF007F),
            textPrimary = Color(0xFFFFFFFF),
            textSecondary = Color(0xFFB56EFF),
            btnNumber = Color(0xFF2B0F47),
            btnOperator = Color(0xFF42156C),
            btnFunction = Color(0xFF00F5D4),
            btnEquals = Color(0xFFFF007F),
            accent = Color(0xFFFF007F),
            secondary = Color(0xFF00F5D4)
        )
        CalculatorTheme.OLED_DARK -> ThemeColors(
            background = Color(0xFF000000),
            surface = Color(0xFF121212),
            surfaceGlass = Color(0x1F007AFF),
            borderGlass = Color(0x33FFFFFF),
            textPrimary = Color(0xFFFFFFFF),
            textSecondary = Color(0xFF8E8E93),
            btnNumber = Color(0xFF1C1C1E),
            btnOperator = Color(0xFF2C2C2E),
            btnFunction = Color(0xFF0A84FF),
            btnEquals = Color(0xFFFF453A),
            accent = Color(0xFFFF453A),
            secondary = Color(0xFF0A84FF)
        )
    }
}
