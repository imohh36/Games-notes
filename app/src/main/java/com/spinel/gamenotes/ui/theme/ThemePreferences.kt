package com.spinel.gamenotes.ui.theme

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

data class ColorOption(val name: String, val color: Color) {
    fun getLocalizedName(strings: com.spinel.gamenotes.util.AppStrings): String = when (color.value.toLong() and 0xFFFFFFFFL) {
        0xFF10B981L -> strings.colorEmeraldDefault
        0xFF06B6D4L -> strings.colorNeonCyan
        0xFFF59E0BL -> strings.colorGlowingGold
        0xFFA855F7L -> strings.colorCyberPurple
        0xFFF8FAFCL -> strings.colorPureWhite
        0xFF38BDF8L -> strings.colorIceBlue
        0xFFF43F5EL -> strings.colorFieryRed
        0xFF84CC16L -> strings.colorLimeGreen
        0xFF0F172AL -> if (name.contains("فحمي") || name.contains("Charcoal")) strings.colorDarkCharcoal else strings.colorSpaceBlue
        0xFF0B0F17L -> strings.colorObsidianDefault
        0xFF000000L -> strings.colorAmoledPureBlack
        0xFF18181BL -> strings.colorMidnightGray
        0xFF1E112AL -> strings.colorCosmicPurple
        0xFF0A192FL -> strings.colorDeepNavy
        0xFFF1F5F9L -> strings.colorLightGrayDay
        else -> name
    }
}

object ThemePreferences {
    private const val PREFS_NAME = "gamenotes_theme_prefs"
    private const val KEY_TEXT_COLOR = "key_text_color"
    private const val KEY_BG_COLOR = "key_bg_color"
    private const val KEY_AMOLED_MODE = "key_amoled_mode"

    // Default: Bright Gamer Emerald text on Deep Gamer Obsidian background
    val DEFAULT_TEXT_COLOR = Color(0xFF10B981)
    val DEFAULT_BG_COLOR = Color(0xFF0B0F17)
    val AMOLED_BLACK = Color(0xFF000000)

    val textColorPresets = listOf(
        ColorOption("Emerald (Default)", Color(0xFF10B981)),
        ColorOption("Neon Cyan", Color(0xFF06B6D4)),
        ColorOption("Glowing Gold", Color(0xFFF59E0B)),
        ColorOption("Cyber Purple", Color(0xFFA855F7)),
        ColorOption("Pure White", Color(0xFFF8FAFC)),
        ColorOption("Ice Blue", Color(0xFF38BDF8)),
        ColorOption("Fiery Red", Color(0xFFF43F5E)),
        ColorOption("Lime Green", Color(0xFF84CC16)),
        ColorOption("Dark Charcoal", Color(0xFF0F172A))
    )

    val bgColorPresets = listOf(
        ColorOption("Obsidian (Default)", Color(0xFF0B0F17)),
        ColorOption("Pure AMOLED Black", Color(0xFF000000)),
        ColorOption("Midnight Gray", Color(0xFF18181B)),
        ColorOption("Space Blue", Color(0xFF0F172A)),
        ColorOption("Cosmic Purple", Color(0xFF1E112A)),
        ColorOption("Deep Navy", Color(0xFF0A192F)),
        ColorOption("Light Gray (Day)", Color(0xFFF1F5F9))
    )

    private val _textColor = MutableStateFlow(DEFAULT_TEXT_COLOR)
    val textColor = _textColor.asStateFlow()

    private val _bgColor = MutableStateFlow(DEFAULT_BG_COLOR)
    val bgColor = _bgColor.asStateFlow()

    private val _isAmoledMode = MutableStateFlow(false)
    val isAmoledMode = _isAmoledMode.asStateFlow()

    fun init(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val textInt = prefs.getInt(KEY_TEXT_COLOR, DEFAULT_TEXT_COLOR.toArgb())
        val bgInt = prefs.getInt(KEY_BG_COLOR, DEFAULT_BG_COLOR.toArgb())
        val amoled = prefs.getBoolean(KEY_AMOLED_MODE, false)
        _textColor.value = Color(textInt)
        _isAmoledMode.value = amoled
        _bgColor.value = if (amoled) AMOLED_BLACK else Color(bgInt)
    }

    fun setAmoledMode(context: Context, enabled: Boolean) {
        _isAmoledMode.value = enabled
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_AMOLED_MODE, enabled).apply()
        if (enabled) {
            _bgColor.value = AMOLED_BLACK
        } else {
            val bgInt = prefs.getInt(KEY_BG_COLOR, DEFAULT_BG_COLOR.toArgb())
            _bgColor.value = if (Color(bgInt) == AMOLED_BLACK) DEFAULT_BG_COLOR else Color(bgInt)
        }
    }

    fun setTextColor(context: Context, color: Color) {
        _textColor.value = color
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putInt(KEY_TEXT_COLOR, color.toArgb())
            .apply()
    }

    fun setBgColor(context: Context, color: Color) {
        _bgColor.value = color
        val isAmoled = color == AMOLED_BLACK
        _isAmoledMode.value = isAmoled
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putInt(KEY_BG_COLOR, color.toArgb())
            .putBoolean(KEY_AMOLED_MODE, isAmoled)
            .apply()
    }

    fun resetToDefaults(context: Context) {
        _isAmoledMode.value = false
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_AMOLED_MODE, false)
            .apply()
        setTextColor(context, DEFAULT_TEXT_COLOR)
        setBgColor(context, DEFAULT_BG_COLOR)
    }

    // Helper to calculate readable contrasting content color (for text inside TopBar)
    fun getContrastingContentColor(background: Color): Color {
        val luminance = 0.299 * background.red + 0.587 * background.green + 0.114 * background.blue
        return if (luminance > 0.5) Color(0xFF090D14) else Color(0xFFF8FAFC)
    }
}
