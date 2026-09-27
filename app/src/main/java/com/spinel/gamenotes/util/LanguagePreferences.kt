package com.spinel.gamenotes.util

import android.content.Context
import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

enum class AppLanguage(val code: String, val displayNameInArabic: String, val displayNameInEnglish: String) {
    ARABIC("ar", "العربية", "Arabic"),
    ENGLISH("en", "الإنجليزية", "English");

    val isRtl: Boolean get() = this == ARABIC

    companion object {
        fun fromCode(code: String?): AppLanguage {
            return if (code?.lowercase()?.startsWith("en") == true) ENGLISH else ARABIC
        }

        fun getSystemDefaultLanguage(): AppLanguage {
            val systemLocale = Locale.getDefault()
            val lang = systemLocale.language?.lowercase() ?: "en"
            return if (lang.startsWith("ar")) ARABIC else ENGLISH
        }
    }
}

object LanguagePreferences {
    private const val PREFS_NAME = "gamenotes_language_prefs"
    private const val KEY_APP_LANGUAGE = "key_app_language"

    private val _currentLanguage = MutableStateFlow(AppLanguage.ARABIC)
    val currentLanguage = _currentLanguage.asStateFlow()

    fun init(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val selectedLang = if (!prefs.contains(KEY_APP_LANGUAGE)) {
            // First run: detect phone system language automatically
            val systemLang = AppLanguage.getSystemDefaultLanguage()
            prefs.edit().putString(KEY_APP_LANGUAGE, systemLang.code).apply()
            systemLang
        } else {
            val savedCode = prefs.getString(KEY_APP_LANGUAGE, "ar")
            AppLanguage.fromCode(savedCode)
        }
        _currentLanguage.value = selectedLang
        updateLocaleAndResources(context, selectedLang)
    }

    fun setLanguage(context: Context, language: AppLanguage) {
        _currentLanguage.value = language
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_APP_LANGUAGE, language.code)
            .apply()
        updateLocaleAndResources(context, language)
    }

    fun getCurrentStrings(): AppStrings =
        if (_currentLanguage.value == AppLanguage.ARABIC) AppStrings.Arabic else AppStrings.English

    @Suppress("DEPRECATION")
    private fun updateLocaleAndResources(context: Context, language: AppLanguage) {
        val locale = if (language == AppLanguage.ARABIC) Locale("ar") else Locale.ENGLISH
        Locale.setDefault(locale)
        val res = context.resources
        val config = Configuration(res.configuration)
        config.setLocale(locale)
        config.setLayoutDirection(locale)
        res.updateConfiguration(config, res.displayMetrics)

        val appCtx = context.applicationContext
        if (appCtx != null && appCtx != context) {
            val appRes = appCtx.resources
            val appConfig = Configuration(appRes.configuration)
            appConfig.setLocale(locale)
            appConfig.setLayoutDirection(locale)
            appRes.updateConfiguration(appConfig, appRes.displayMetrics)
        }
    }

    val LocalAppStrings get() = com.spinel.gamenotes.util.LocalAppStrings
}

val LocalAppStrings = staticCompositionLocalOf { AppStrings.Arabic }

@Composable
fun ProvideAppLanguageAndDirection(content: @Composable () -> Unit) {
    val currentLang by LanguagePreferences.currentLanguage.collectAsState()
    val strings = if (currentLang == AppLanguage.ARABIC) AppStrings.Arabic else AppStrings.English
    val direction = if (currentLang.isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr

    CompositionLocalProvider(
        LocalAppStrings provides strings,
        LocalLayoutDirection provides direction
    ) {
        content()
    }
}
