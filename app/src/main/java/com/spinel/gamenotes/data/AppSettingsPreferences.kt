package com.spinel.gamenotes.data

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class AiModelOption(
    val modelId: String,
    val displayName: String,
    val apiModelName: String,
    val description: String,
    val badge: String
) {
    GEMINI_FLASH_3_5(
        modelId = "gemini-flash-3.5",
        displayName = "Gemini Flash 3.5",
        apiModelName = "gemini-2.5-flash",
        description = "استهلاك خفيف وسريع.",
        badge = "خفيف وسريع"
    ),
    GEMINI_FLASH_LITE_3_8(
        modelId = "gemini-flash-lite-3.8",
        displayName = "Gemini Flash Lite 3.8",
        apiModelName = "gemini-2.5-flash",
        description = "استهلاك متوسط.",
        badge = "استهلاك متوسط"
    ),
    GEMINI_PRO_3_1(
        modelId = "gemini-pro-3.1",
        displayName = "Gemini Pro 3.1",
        apiModelName = "gemini-2.5-pro",
        description = "استهلاك عالي.",
        badge = "استهلاك عالي"
    );

    val id: String get() = modelId

    fun getLocalizedTitle(strings: com.spinel.gamenotes.util.AppStrings): String = when (this) {
        GEMINI_FLASH_3_5 -> strings.aiModelFlashTitle
        GEMINI_FLASH_LITE_3_8 -> strings.aiModelFlashLiteTitle
        GEMINI_PRO_3_1 -> strings.aiModelProTitle
    }

    fun getLocalizedDescription(strings: com.spinel.gamenotes.util.AppStrings): String = when (this) {
        GEMINI_FLASH_3_5 -> strings.aiModelFlashDesc
        GEMINI_FLASH_LITE_3_8 -> strings.aiModelFlashLiteDesc
        GEMINI_PRO_3_1 -> strings.aiModelProDesc
    }

    fun getLocalizedBadge(strings: com.spinel.gamenotes.util.AppStrings): String = when (this) {
        GEMINI_FLASH_3_5 -> strings.aiModelFlashBadge
        GEMINI_FLASH_LITE_3_8 -> strings.aiModelFlashLiteBadge
        GEMINI_PRO_3_1 -> strings.aiModelProBadge
    }

    companion object {
        val FLASH get() = GEMINI_FLASH_3_5
        val FLASH_LITE get() = GEMINI_FLASH_LITE_3_8
        val PRO get() = GEMINI_PRO_3_1

        fun fromString(name: String?): AiModelOption {
            if (name == null) return GEMINI_FLASH_3_5
            return entries.firstOrNull { it.name.equals(name, ignoreCase = true) || it.modelId.equals(name, ignoreCase = true) }
                ?: if (name.contains("pro", ignoreCase = true)) GEMINI_PRO_3_1
                else if (name.contains("lite", ignoreCase = true)) GEMINI_FLASH_LITE_3_8
                else GEMINI_FLASH_3_5
        }
    }
}

object AppSettingsPreferences {
    private const val PREFS_NAME = "gamenotes_app_settings"
    private const val KEY_AI_MODEL = "key_ai_model"
    private const val KEY_MINI_TASK_LIST_ENABLED = "key_mini_task_list_enabled"
    private const val KEY_USER_GEMINI_API_KEY = "key_user_gemini_api_key"
    private const val KEY_PINNED_FLOATING_NOTE_ID = "key_pinned_floating_note_id"
    private const val KEY_PINNED_FLOATING_TAB_INDEX = "key_pinned_floating_tab_index"
    private const val KEY_PINNED_FLOATING_SCROLL_ITEM_INDEX = "key_pinned_floating_scroll_item_index"
    private const val KEY_PINNED_FLOATING_SCROLL_OFFSET = "key_pinned_floating_scroll_offset"

    private val _aiModel = MutableStateFlow(AiModelOption.GEMINI_FLASH_3_5)
    val aiModel = _aiModel.asStateFlow()

    private val _isMiniTaskListEnabled = MutableStateFlow(true)
    val isMiniTaskListEnabled = _isMiniTaskListEnabled.asStateFlow()

    private val _userGeminiApiKey = MutableStateFlow("")
    val userGeminiApiKey = _userGeminiApiKey.asStateFlow()

    private val _pinnedFloatingNoteId = MutableStateFlow(-1L)
    val pinnedFloatingNoteId = _pinnedFloatingNoteId.asStateFlow()

    private val _pinnedFloatingTabIndex = MutableStateFlow(0)
    val pinnedFloatingTabIndex = _pinnedFloatingTabIndex.asStateFlow()

    private val _pinnedFloatingScrollItemIndex = MutableStateFlow(0)
    val pinnedFloatingScrollItemIndex = _pinnedFloatingScrollItemIndex.asStateFlow()

    private val _pinnedFloatingScrollOffset = MutableStateFlow(0)
    val pinnedFloatingScrollOffset = _pinnedFloatingScrollOffset.asStateFlow()

    fun init(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val aiModelName = prefs.getString(KEY_AI_MODEL, AiModelOption.GEMINI_FLASH_3_5.name)
        val miniEnabled = prefs.getBoolean(KEY_MINI_TASK_LIST_ENABLED, true)
        val userApiKey = prefs.getString(KEY_USER_GEMINI_API_KEY, "") ?: ""
        val pinnedNoteId = prefs.getLong(KEY_PINNED_FLOATING_NOTE_ID, -1L)
        val pinnedTabIndex = prefs.getInt(KEY_PINNED_FLOATING_TAB_INDEX, 0)
        val pinnedScrollItemIdx = prefs.getInt(KEY_PINNED_FLOATING_SCROLL_ITEM_INDEX, 0)
        val pinnedScrollOffset = prefs.getInt(KEY_PINNED_FLOATING_SCROLL_OFFSET, 0)

        _aiModel.value = AiModelOption.fromString(aiModelName)
        _isMiniTaskListEnabled.value = miniEnabled
        _userGeminiApiKey.value = userApiKey.trim()
        _pinnedFloatingNoteId.value = pinnedNoteId
        _pinnedFloatingTabIndex.value = pinnedTabIndex
        _pinnedFloatingScrollItemIndex.value = pinnedScrollItemIdx
        _pinnedFloatingScrollOffset.value = pinnedScrollOffset
    }

    fun isMiniTaskListEnabled(context: Context): Boolean {
        return _isMiniTaskListEnabled.value
    }

    fun setMiniTaskListEnabled(context: Context, enabled: Boolean) {
        _isMiniTaskListEnabled.value = enabled
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_MINI_TASK_LIST_ENABLED, enabled)
            .apply()
    }

    fun setAiModel(context: Context, model: AiModelOption) {
        _aiModel.value = model
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_AI_MODEL, model.name)
            .apply()
    }

    fun getUserGeminiApiKey(): String {
        return _userGeminiApiKey.value.trim()
    }

    fun setUserGeminiApiKey(context: Context, key: String) {
        val cleanKey = key.trim()
        _userGeminiApiKey.value = cleanKey
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_USER_GEMINI_API_KEY, cleanKey)
            .apply()
    }

    fun isAiEnabled(): Boolean {
        return _userGeminiApiKey.value.isNotBlank()
    }

    fun getPinnedFloatingNoteId(context: Context? = null): Long {
        if (_pinnedFloatingNoteId.value > 0L) return _pinnedFloatingNoteId.value
        context?.let {
            val prefs = it.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val id = prefs.getLong(KEY_PINNED_FLOATING_NOTE_ID, -1L)
            _pinnedFloatingNoteId.value = id
            return id
        }
        return _pinnedFloatingNoteId.value
    }

    fun getPinnedFloatingTabIndex(context: Context? = null): Int {
        if (_pinnedFloatingNoteId.value > 0L) return _pinnedFloatingTabIndex.value
        context?.let {
            val prefs = it.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val idx = prefs.getInt(KEY_PINNED_FLOATING_TAB_INDEX, 0)
            _pinnedFloatingTabIndex.value = idx
            return idx
        }
        return _pinnedFloatingTabIndex.value
    }

    fun getPinnedFloatingScrollItemIndex(context: Context? = null): Int {
        if (_pinnedFloatingNoteId.value > 0L) return _pinnedFloatingScrollItemIndex.value
        context?.let {
            val prefs = it.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val idx = prefs.getInt(KEY_PINNED_FLOATING_SCROLL_ITEM_INDEX, 0)
            _pinnedFloatingScrollItemIndex.value = idx
            return idx
        }
        return _pinnedFloatingScrollItemIndex.value
    }

    fun getPinnedFloatingScrollOffset(context: Context? = null): Int {
        if (_pinnedFloatingNoteId.value > 0L) return _pinnedFloatingScrollOffset.value
        context?.let {
            val prefs = it.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val offset = prefs.getInt(KEY_PINNED_FLOATING_SCROLL_OFFSET, 0)
            _pinnedFloatingScrollOffset.value = offset
            return offset
        }
        return _pinnedFloatingScrollOffset.value
    }

    fun setPinnedFloatingNote(
        context: Context,
        noteId: Long,
        tabIndex: Int,
        scrollItemIndex: Int = 0,
        scrollOffset: Int = 0
    ) {
        _pinnedFloatingNoteId.value = noteId
        _pinnedFloatingTabIndex.value = tabIndex
        _pinnedFloatingScrollItemIndex.value = scrollItemIndex
        _pinnedFloatingScrollOffset.value = scrollOffset
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putLong(KEY_PINNED_FLOATING_NOTE_ID, noteId)
            .putInt(KEY_PINNED_FLOATING_TAB_INDEX, tabIndex)
            .putInt(KEY_PINNED_FLOATING_SCROLL_ITEM_INDEX, scrollItemIndex)
            .putInt(KEY_PINNED_FLOATING_SCROLL_OFFSET, scrollOffset)
            .apply()
    }

    fun updatePinnedScrollPosition(
        context: Context,
        scrollItemIndex: Int,
        scrollOffset: Int
    ) {
        if (_pinnedFloatingNoteId.value <= 0L) return
        _pinnedFloatingScrollItemIndex.value = scrollItemIndex
        _pinnedFloatingScrollOffset.value = scrollOffset
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putInt(KEY_PINNED_FLOATING_SCROLL_ITEM_INDEX, scrollItemIndex)
            .putInt(KEY_PINNED_FLOATING_SCROLL_OFFSET, scrollOffset)
            .apply()
    }

    fun clearPinnedFloatingNote(context: Context) {
        _pinnedFloatingNoteId.value = -1L
        _pinnedFloatingTabIndex.value = 0
        _pinnedFloatingScrollItemIndex.value = 0
        _pinnedFloatingScrollOffset.value = 0
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .remove(KEY_PINNED_FLOATING_NOTE_ID)
            .remove(KEY_PINNED_FLOATING_TAB_INDEX)
            .remove(KEY_PINNED_FLOATING_SCROLL_ITEM_INDEX)
            .remove(KEY_PINNED_FLOATING_SCROLL_OFFSET)
            .apply()
    }

    fun isNotePinnedToFloating(noteId: Long, tabIndex: Int = -1): Boolean {
        if (_pinnedFloatingNoteId.value != noteId || noteId <= 0L) return false
        if (tabIndex >= 0 && _pinnedFloatingTabIndex.value != tabIndex) return false
        return true
    }
}
