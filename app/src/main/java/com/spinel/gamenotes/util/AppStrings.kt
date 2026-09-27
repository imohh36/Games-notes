package com.spinel.gamenotes.util

abstract class AppStrings {
    // General & App Info
    abstract val appName: String
    abstract val appVersionNotice: String
    abstract val smartNotesSubtitle: String
    abstract val settingsTitle: String
    abstract val settingsSubtitle: String
    abstract val languageSectionTitle: String
    abstract val languageSectionSubtitle: String
    abstract val arabicLanguageName: String
    abstract val englishLanguageName: String

    // Navigation & Tabs
    abstract val tabAll: String
    abstract val addNewTab: String
    abstract val newTabNameHint: String
    abstract val addTabButton: String
    abstract val deleteTabTitle: String
    abstract val deleteTabMessage: (String) -> String
    abstract val deleteConfirm: String
    abstract val cancel: String
    abstract val confirm: String
    abstract val searchPlaceholder: String
    abstract val searchInGamePlaceholder: (String) -> String
    abstract val searchAllPlaceholder: String
    abstract val noNotesFound: String
    abstract val noNotesSubtitle: String
    abstract val addFirstNotePrompt: String
    abstract val gameTabsLabel: String
    abstract val addGameTab: String
    abstract val closeTabMenu: String
    abstract val createGameTabTitle: String
    abstract val gameNameLabel: String
    abstract val gameNamePlaceholder: String
    abstract val addTabConfirmButton: String
    abstract val emptyTabsTitle: String
    abstract val emptyTabsSubtitle: String
    abstract val createTabAction: String
    abstract val newGameInlineChip: String
    abstract val tabHeaderTitle: (String) -> String
    abstract val tabHeaderSubtitle: (Int) -> String
    abstract val tabAddNoteButton: (String) -> String
    abstract val welcomeTitle: String
    abstract val welcomeSubtitle: String
    abstract val noNotesForGameTitle: (String) -> String
    abstract val noNotesForGameSubtitle: (String) -> String
    abstract val noMatchingItemsTitle: String
    abstract val noMatchingItemsSubtitle: String
    abstract val addNoteOrChecklist: String
    abstract val addNoteForGame: (String) -> String
    abstract val notesCountBadge: (Int) -> String
    abstract val selectedTabPrefix: (String) -> String
    abstract val openMenu: String
    abstract val clearSearchCd: String
    abstract val tabAlreadyExistsToast: String
    abstract val tabCreatedSuccessToast: (String) -> String
    abstract val notePinnedToast: String
    abstract val noteUnpinnedToast: String
    abstract val miniTaskListPinnedToast: String
    abstract val overlayStartedToast: String
    abstract val apiKeyClearedToast: String
    abstract val shareRemainingTasksLabel: String

    // Drawer / Theme / Appearance
    abstract val appearanceSectionTitle: String
    abstract val appearanceSectionSubtitle: String
    abstract val textColorLabel: String
    abstract val bgColorLabel: String
    abstract val amoledModeTitle: String
    abstract val amoledModeEnabledDesc: String
    abstract val amoledModeDisabledDesc: String
    abstract val resetDefaultsButton: String
    abstract val selectedIndicator: String

    // Overlay Settings
    abstract val overlayToolTitle: String
    abstract val overlaySectionSubtitle: String
    abstract val overlayPermissionTitle: String
    abstract val overlayPermissionMessage: String
    abstract val overlayPermissionGrant: String
    abstract val launchOverlayButton: String
    abstract val stopOverlayButton: String
    abstract val overlayRunningIndicator: String
    abstract val overlayRunningNotice: String
    abstract val overlayQuickActive: String
    abstract val overlayQuickInactive: String

    // Trash Management
    abstract val trashTitle: String
    abstract val trashSubtitle: String
    abstract val openTrashButton: String
    abstract val trashEmptyTitle: String
    abstract val trashEmptySubtitle: String
    abstract val trashAutoDeleteNotice: String
    abstract val restoreAll: String
    abstract val emptyTrash: String
    abstract val restoreSingle: String
    abstract val deleteForeverSingle: String
    abstract val emptyTrashConfirmTitle: String
    abstract val emptyTrashConfirmMessage: String
    abstract val emptyTrashConfirmAction: String
    abstract val restoreSuccess: String
    abstract val deleteForeverSuccess: String
    abstract val emptyTrashSuccess: String
    abstract val noteDeletedMovedToTrash: String
    abstract val undo: String
    abstract val trashNotesCountDesc: (Int) -> String
    abstract val trashRestoreAnytimePrompt: String
    abstract val trashEmptyCardDesc: String
    abstract val untitledNote: String
    abstract val modifiedDeletedDate: (String) -> String
    abstract val confirmDeleteForeverTitle: String
    abstract val confirmDeleteForeverMessage: (String) -> String
    abstract val confirmEmptyTrashMessageWithCount: (Int) -> String
    abstract val noteTasksCompletedFormat: (Int, Int) -> String
    abstract val emptyTrashFullButton: String

    // AI & Gemini Settings
    abstract val aiSectionHeader: String
    abstract val aiSectionSubtitle: String
    abstract val aiModelSelectionTitle: String
    abstract val aiModelFlashTitle: String
    abstract val aiModelFlashDesc: String
    abstract val aiModelFlashBadge: String
    abstract val aiModelFlashLiteTitle: String
    abstract val aiModelFlashLiteDesc: String
    abstract val aiModelFlashLiteBadge: String
    abstract val aiModelProTitle: String
    abstract val aiModelProDesc: String
    abstract val aiModelProBadge: String
    abstract val apiKeyFieldTitle: String
    abstract val apiKeyMandatoryLabel: String
    abstract val apiKeyFieldHint: String
    abstract val apiKeySavedNotice: String
    abstract val apiKeyMissingNotice: String
    abstract val apiKeyClearButton: String
    abstract val aiLockedTitle: String
    abstract val aiLockedDesc: String
    abstract val aiSelectModelPrompt: String
    abstract val aiChatHistoryTitle: String
    abstract val aiChatHistorySubtitle: String
    abstract val aiChatHistoryEmpty: String
    abstract val aiChatHistoryClearConfirmTitle: String
    abstract val aiChatHistoryClearConfirmMessage: String
    abstract val aiChatHistoryClearSuccess: String
    abstract val chatMessagesCount: (Int) -> String
    abstract val viewChatHistoryButton: String
    abstract val clearChatHistoryButton: String
    abstract val clearAll: String
    abstract val close: String
    abstract val aiAssistantButtonCd: String

    abstract val showApiKey: String
    abstract val hideApiKey: String
    abstract val confirmDeleteChat: String
    abstract val deleteGameTabAction: String
    abstract val noteMovedToTrashNotice: (String) -> String
    abstract val clearInputText: String

    // Mini Task List Setting
    abstract val miniTaskListTitle: String
    abstract val miniTaskListSubtitle: String
    abstract val miniTaskListEnabledDesc: String
    abstract val miniTaskListDisabledDesc: String

    // Backup & Restore
    abstract val backupRestoreTitle: String
    abstract val backupRestoreDesc: String
    abstract val backupButton: String
    abstract val restoreButton: String
    abstract val backupSuccess: String
    abstract val backupFailed: String
    abstract val restoreSuccessMsg: (Int, Int) -> String
    abstract val restoreFailed: String

    // Note Card & Actions
    abstract val pinNote: String
    abstract val unpinNote: String
    abstract val editNote: String
    abstract val deleteNote: String
    abstract val askGemini: String
    abstract val askAi: String
    abstract val pinAsMiniWidget: String
    abstract val openInFullView: String
    abstract val progressTasks: (Int, Int) -> String
    abstract val tasksCompletedSummary: (Int, Int) -> String
    abstract val achievementTasksFormat: (Int, Int) -> String
    abstract val achievementLabel: String
    abstract val regularNoteBadge: String
    abstract val checklistBadge: String
    abstract val showMore: String
    abstract val showLess: String
    abstract val attachedImageZoom: String
    abstract val moreItemsCount: (Int) -> String
    abstract val firstNoteImage: String
    abstract val secondNoteImage: String

    // Note Creation & Types
    abstract val selectCreationTypeTitle: String
    abstract val selectCreationTypeSubtitle: String
    abstract val regularNoteTitle: String
    abstract val regularNoteDesc: String
    abstract val checklistNoteTitle: String
    abstract val checklistNoteDesc: String
    abstract val newTodoListTitle: String
    abstract val editTodoListTitle: String
    abstract val newNoteDialogTitle: String
    abstract val editNoteDialogTitle: String
    abstract val checklistInteractiveSubtitle: String
    abstract val regularNoteSubtitle: String
    abstract val editNoteDialogGameTagLabel: String
    abstract val customGameTagPlaceholder: String
    abstract val exampleGameName: String
    abstract val checklistTitlePlaceholder: String
    abstract val noteTitleLabelPlaceholder: String
    abstract val noteContentFreeTextLabel: String
    abstract val noteContentCraftingPlaceholder: String
    abstract val attachedImagesLabel: String
    abstract val attachImageButton: String
    abstract val attachedImageZoomCd: String
    abstract val deleteImageActionCd: String
    abstract val checklistItemsSectionTitle: String
    abstract val addChecklistPlaceholder: String
    abstract val addChecklistActionCd: String
    abstract val emptyChecklistHint: String
    abstract val deleteChecklistActionCd: String
    abstract val saveAndCreateNote: String
    abstract val saveEditsNote: String
    abstract val newNoteForGamePrompt: (String) -> String
    abstract val newNoteGeneralPrompt: String

    // Full Note Editor
    abstract val newNoteTitleHint: String
    abstract val noteTitlePlaceholder: String
    abstract val defaultGeneralGameTag: String
    abstract val editGameTagPrompt: String
    abstract val backAndSave: String
    abstract val insertChecklistAction: String
    abstract val insertImageAction: String
    abstract val writeNoteContentPlaceholder: String
    abstract val tagsSectionTitle: String
    abstract val addTagPlaceholder: String
    abstract val addTagButton: String
    abstract val quickAddTag: String
    abstract val imageSavedNotice: String
    abstract val noteSavedNotice: String
    abstract val deleteNoteConfirmTitle: String
    abstract val deleteNoteConfirmMessage: String
    abstract val shareNoteAction: String
    abstract val shareNoteChooserTitle: String
    abstract val shareNoteErrorNotice: String
    abstract val pinToFloatingWindow: String
    abstract val pinnedToFloatingSuccess: (String) -> String
    abstract val unpinnedFromFloatingSuccess: String
    abstract val saveFirstToPin: String
    abstract val internalTabsTitle: String
    abstract val newInternalTab: String
    abstract val tabNumber: (Int) -> String
    abstract val editTabName: String
    abstract val deleteTabCd: String
    abstract val addTabCd: String
    abstract val expandTabsCd: String
    abstract val collapseTabsCd: String
    abstract val addInternalTabTitle: String
    abstract val tabNameLabel: String
    abstract val tabNamePlaceholder: String
    abstract val deleteInternalTabConfirm: (String) -> String
    abstract val thisTab: String
    abstract val saveAndClose: String
    abstract val addFreeText: String
    abstract val addImage: String
    abstract val addTask: String
    abstract val firstImageZoomCd: String
    abstract val secondImageZoomCd: String
    abstract val deleteFirstImageCd: String
    abstract val deleteSecondImageCd: String
    abstract val selectOrChangeGameTab: String
    abstract val availableTabsTitle: String
    abstract val orWriteNewGameName: String

    // Checklist Editor
    abstract val newChecklistTitleHint: String
    abstract val taskPlaceholder: String
    abstract val addTaskButton: String
    abstract val allTasksDoneBanner: String
    abstract val progressLabel: String
    abstract val dragToReorder: String
    abstract val checklistTitleLabel: String
    abstract val checklistEditorTitlePlaceholder: String
    abstract val checklistItemsInstructions: String
    abstract val writeTaskPlaceholder: (Int) -> String
    abstract val deleteTaskCd: String
    abstract val shareChecklistAction: String
    abstract val shareChecklistChooserTitle: String

    // Gemini Chat Sheet
    abstract val geminiAssistantTitle: String
    abstract val geminiAssistantSubtitle: String
    abstract val geminiInputHint: String
    abstract val geminiNoApiKeyNotice: String
    abstract val geminiConfigureApiKeyPrompt: String
    abstract val geminiThinking: String
    abstract val geminiQuickSuggestTitle: String
    abstract val geminiQuickSuggestTasks: String
    abstract val geminiQuickSummarize: String
    abstract val geminiQuickTranslate: String
    abstract val geminiCopyResponse: String
    abstract val geminiCopiedNotice: String
    abstract val geminiNewChatSession: String
    abstract val geminiClearSession: String
    abstract val geminiAskAboutNote: (String) -> String
    abstract val geminiWelcomeGreeting: (String) -> String
    abstract val geminiNewChatStarted: String
    abstract val geminiErrorCommunicating: String
    abstract val geminiAttachedImageAnalysis: String
    abstract val geminiLinkedToNote: (String) -> String
    abstract val geminiClearCurrentChatCd: String
    abstract val geminiClearChatConfirmTitle: String
    abstract val geminiClearChatConfirmMessage: String
    abstract val geminiPastSessionsTitle: String
    abstract val geminiNoPastChats: String
    abstract val geminiDeleteSessionCd: String
    abstract val geminiYouLabel: String
    abstract val geminiAiLabel: String
    abstract val geminiQuickQuestion1: String
    abstract val geminiQuickQuestion2: String
    abstract val geminiQuickQuestion3: String
    abstract val geminiChatSessionCreatedNotice: String
    abstract val geminiChatWithNoteTitle: (String) -> String
    abstract val geminiChatHistoryCd: String
    abstract val geminiNewChatCd: String
    abstract val geminiReplyErrorPrefix: (String) -> String
    abstract val geminiChatClearedGreeting: String
    abstract val geminiTextCopiedToInputHint: String
    abstract val geminiAttachImageCd: String
    abstract val geminiInputDisabledHint: String
    abstract val geminiSendMessageCd: String
    abstract val geminiRemoveImageCd: String
    abstract val geminiAttachedImageLabel: String
    abstract val geminiConvertToNote: String
    abstract val geminiConvertToChecklist: String
    abstract val geminiConvertedToNoteSuccess: String
    abstract val geminiConvertedToChecklistSuccess: String
    abstract val geminiNoteFallbackTitle: String
    abstract val geminiTasksFallbackTitle: String

    // Floating Timer
    abstract val timerTitle: String
    abstract val timerSubtitle: String
    abstract val timerTimeUp: String
    abstract val timerCounting: (String) -> String
    abstract val timerStopped: String
    abstract val timerStart: String
    abstract val timerPause: String
    abstract val timerReset: String
    abstract val timerQuickPresets: String
    abstract val timerCustomMinutesHint: String
    abstract val timerSetButton: String
    abstract val timerShowInOverlay: String
    abstract val timerMin: String
    abstract val timerSec: String
    abstract val timerResume: String
    abstract val timerNotificationNotice: String
    abstract val timerTaskNameLabel: String
    abstract val timerTaskNamePlaceholder: String
    abstract val timerDurationMinutesLabel: String
    abstract val timerStartFullButton: String
    abstract val timerPresetMinutes: (Int) -> String
    abstract val timerDefaultLabelFormat: (Int) -> String
    abstract val doneButton: String

    // Image Zoom & Draw
    abstract val imageZoomTitle: String
    abstract val drawingMode: String
    abstract val panZoomMode: String
    abstract val undoDrawing: String
    abstract val clearDrawing: String
    abstract val saveDrawing: String
    abstract val brushSize: String
    abstract val brushColor: String
    abstract val imageSavedToStorage: String
    abstract val fullScreenImageCd: String
    abstract val drawingModeActiveNotice: String
    abstract val drawAnnotateButton: String
    abstract val resetImageCd: String
    abstract val noDrawingsToSave: String
    abstract val imageSaveSuccess: String
    abstract val imageProcessFailed: String
    abstract val imageZoomHelpTip: String

    // Floating Overlay
    abstract val overlayDockSide: String
    abstract val overlayMinimize: String
    abstract val overlayExpandToPanel: String
    abstract val overlayQuickAddTaskHint: String
    abstract val overlayAddQuickTask: String
    abstract val overlayNoNotesInGame: String
    abstract val overlaySwitchToGame: String
    abstract val overlayOpenFullApp: String
    abstract val overlaySearchInOverlay: String
    abstract val overlayCreateNote: String
    abstract val overlayCreateChecklist: String
    abstract val overlayNotificationTitle: String
    abstract val overlayNotificationText: String
    abstract val overlayNotificationStopAction: String
    abstract val overlayNotificationChannelDesc: String
    abstract val overlayClosedToast: String
    abstract val miniWidgetClosedToast: String
    abstract val overlayTasksTitle: String
    abstract val overlayNoTasksCurrently: String
    abstract val overlayDragToMove: String
    abstract val overlayCloseMiniWidget: String
    abstract val overlayGamesLabel: String
    abstract val overlayChannelName: String
    abstract val releaseToDismiss: String
    abstract val pickingImageInProgress: String
    abstract val todoTasks: String
    abstract val overlayToggleDockSide: String
    abstract val overlayMinimizeToBubble: String

    // Color Presets Names
    abstract val colorEmeraldDefault: String
    abstract val colorNeonCyan: String
    abstract val colorGlowingGold: String
    abstract val colorCyberPurple: String
    abstract val colorPureWhite: String
    abstract val colorIceBlue: String
    abstract val colorFieryRed: String
    abstract val colorLimeGreen: String
    abstract val colorDarkCharcoal: String
    abstract val colorObsidianDefault: String
    abstract val colorAmoledPureBlack: String
    abstract val colorMidnightGray: String
    abstract val colorSpaceBlue: String
    abstract val colorCosmicPurple: String
    abstract val colorDeepNavy: String
    abstract val colorLightGrayDay: String

    // Editor Helpers
    abstract val tapToEdit: String
    abstract val twoSideBySideImagesNotice: String
    abstract val deleteBothImages: String
    abstract val longPressToReorder: String
    abstract val noteImageClickToZoom: String
    abstract val zoomImage: String
    abstract val writeTextBesideImagePlaceholder: String
    abstract val alignmentRight: String
    abstract val alignmentLeft: String
    abstract val addSideBySideImage: String
    abstract val deleteImage: String
    abstract val sizeLabel: String
    abstract val resumeWritingNormalTextHint: String

    // Backward-compatible alias properties & UI helpers
    val newChecklistDefaultTitle: String get() = newChecklistTitleHint
    val generalTag: String get() = defaultGeneralGameTag
    val editGameTag: String get() = editGameTagPrompt
    val shareChecklist: String get() = shareChecklistAction
    val checklistItemsInstruction: String get() = checklistItemsInstructions
    val addChecklistItem: String get() = addTaskButton
    val checklistWriteTaskPlaceholder: (Int) -> String get() = writeTaskPlaceholder
    val delete: String get() = deleteConfirm
    val gameTabDialogTitle: String get() = selectOrChangeGameTab
    val availableTabs: String get() = availableTabsTitle
    val backAction: String get() = backAndSave
    val saveAction: String get() = saveAndClose
    val deleteAction: String get() = deleteConfirm
    val confirmAction: String get() = confirm
    val drawingModeActive: String get() = if (arabicLanguageName == "العربية") "وضع الرسم نشط" else "Drawing mode active"
    val drawingAnnotation: String get() = if (arabicLanguageName == "العربية") "رسم وتوضيح" else "Draw & Annotate"
    val resetZoom: String get() = if (arabicLanguageName == "العربية") "إعادة ضبط التكبير" else "Reset Zoom"
    val drawingSavedSuccess: String get() = if (arabicLanguageName == "العربية") "تم حفظ الرسم بنجاح" else "Drawing saved successfully"
    val failedToProcessSaveImage: String get() = if (arabicLanguageName == "العربية") "فشل في حفظ الصورة" else "Failed to save image"
    val errorPrefix: String get() = if (arabicLanguageName == "العربية") "خطأ:" else "Error:"
    val save: String get() = saveAndClose
    val zoomPinchHint: String get() = if (arabicLanguageName == "العربية") "استخدم إصبعين للتكبير والتحريك" else "Pinch with two fingers to zoom & pan"
    val clear: String get() = clearAll
    val editAction: String get() = editNote
    val unpinFromFloating: String get() = unpinnedFromFloatingSuccess
    val shareTasksLabel: String get() = shareRemainingTasksLabel
    val tagLabel: String get() = tagsSectionTitle
    val tagHint: String get() = addTagPlaceholder
    val add: String get() = addTagButton
    val tagChooseColor: String get() = if (arabicLanguageName == "العربية") "اختر لون الوسم:" else "Choose tag color:"


    companion object {
        val Arabic: AppStrings = ArabicAppStrings
        val English: AppStrings = EnglishAppStrings
    }
}

private object ArabicAppStrings : AppStrings() {
    override val appName: String = "مفكرة الألعاب"
    override val appVersionNotice: String = "مفكرة اللاعبين الذكية | الإصدار 1.3.5"
    override val smartNotesSubtitle: String = "مفكرة اللاعبين الذكية"
    override val settingsTitle: String = "إعدادات مفكرة الألعاب"
    override val settingsSubtitle: String = "تخصيص اللغة والمظهر والذكاء الاصطناعي"
    override val languageSectionTitle: String = "لغة التطبيق"
    override val languageSectionSubtitle: String = "التبديل الفوري بين العربية والإنجليزية"
    override val arabicLanguageName: String = "العربية"
    override val englishLanguageName: String = "الإنجليزية"

    override val tabAll: String = "الكل"
    override val addNewTab: String = "إضافة تبويب جديد"
    override val newTabNameHint: String = "اسم اللعبة أو القسم..."
    override val addTabButton: String = "إضافة"
    override val deleteTabTitle: String = "حذف التبويب"
    override val deleteTabMessage: (String) -> String = { tabName -> "هل أنت متأكد من رغبتك في حذف تبويب \"$tabName\"؟ لن يتم حذف الملاحظات المرتبطة به ولكن ستُنقل للقسم العام." }
    override val deleteConfirm: String = "حذف"
    override val cancel: String = "إلغاء"
    override val confirm: String = "تأكيد"
    override val searchPlaceholder: String = "بحث في الملاحظات والمهام..."
    override val searchInGamePlaceholder: (String) -> String = { game -> "ابحث في ملاحظات ومهام $game..." }
    override val searchAllPlaceholder: String = "ابحث في جميع الملاحظات والمهام..."
    override val noNotesFound: String = "لا توجد ملاحظات مطابقة"
    override val noNotesSubtitle: String = "لم يتم العثور على ملاحظات أو مهام تطابق كلمة البحث."
    override val addFirstNotePrompt: String = "اضغط على زر (+) لإضافة أول ملاحظة أو قائمة مهام."
    override val gameTabsLabel: String = "تبويبات الألعاب:"
    override val addGameTab: String = "إضافة لعبة"
    override val closeTabMenu: String = "إغلاق"
    override val createGameTabTitle: String = "إنشاء تبويب لعبة جديد"
    override val gameNameLabel: String = "اسم اللعبة"
    override val gameNamePlaceholder: String = "مثال: ماينكرافت، إلدن رينج، زيلدا..."
    override val addTabConfirmButton: String = "إضافة التبويب"
    override val emptyTabsTitle: String = "شريط التبويبات فارغ"
    override val emptyTabsSubtitle: String = "أضف تبويب لعبتك الأولى (مثل ماينكرافت أو ستاردو فالي) لربط الملاحظات والمهام بها فوراً."
    override val createTabAction: String = "إنشاء تبويب"
    override val newGameInlineChip: String = "+ لعبة جديدة"
    override val tabHeaderTitle: (String) -> String = { game -> "تبويب لعبة: $game" }
    override val tabHeaderSubtitle: (Int) -> String = { count -> "$count ملاحظة / قائمة مهام خاصة بهذه اللعبة" }
    override val tabAddNoteButton: (String) -> String = { game -> "إضافة ملاحظة جديدة لـ $game" }
    override val welcomeTitle: String = "مرحباً بك في مفكرة الألعاب"
    override val welcomeSubtitle: String = "مفكرتك الذكية لتسجيل خطط ألعابك، إحداثياتك، وقوائم مهامك."
    override val noNotesForGameTitle: (String) -> String = { game -> "لا توجد ملاحظات لـ '$game' بعد" }
    override val noNotesForGameSubtitle: (String) -> String = { game -> "ابدأ بتسجيل إحداثياتك، خطط اللعب، أو قائمة مهامك لـ '$game'." }
    override val noMatchingItemsTitle: String = "لا توجد عناصر مطابقة"
    override val noMatchingItemsSubtitle: String = "جرّب البحث بكلمات أخرى أو اختر تبويباً مختلفاً."
    override val addNoteOrChecklist: String = "إضافة ملاحظة أو قائمة مهام"
    override val addNoteForGame: (String) -> String = { game -> "إضافة ملاحظة لـ $game" }
    override val notesCountBadge: (Int) -> String = { count -> "$count عنصر" }
    override val selectedTabPrefix: (String) -> String = { tab -> "تبويب: $tab" }
    override val openMenu: String = "فتح القائمة"
    override val clearSearchCd: String = "مسح البحث"
    override val tabAlreadyExistsToast: String = "التبويب موجود بالفعل وتم اختياره"
    override val tabCreatedSuccessToast: (String) -> String = { name -> "تم إنشاء تبويب '$name' بنجاح" }
    override val notePinnedToast: String = "تم تثبيت الملاحظة في الأعلى 📌"
    override val noteUnpinnedToast: String = "تم إلغاء تثبيت الملاحظة"
    override val miniTaskListPinnedToast: String = "تم تثبيت قائمة المهام المصغرة فوق الشاشة"
    override val overlayStartedToast: String = "تم تفعيل القائمة العائمة بنجاح"
    override val apiKeyClearedToast: String = "تم مسح مفتاح API"
    override val shareRemainingTasksLabel: String = "\nالمهام المتبقية: "

    override val appearanceSectionTitle: String = "المظهر والألوان"
    override val appearanceSectionSubtitle: String = "اختر ألوانك المفضلة للنصوص والخلفية مع حفظ تلقائي فوري"
    override val textColorLabel: String = "لون النصوص والشريط العلوي:"
    override val bgColorLabel: String = "لون الخلفية الأساسية:"
    override val amoledModeTitle: String = "أسود عميق (شاشات أوليد)"
    override val amoledModeEnabledDesc: String = "مفعل: سواد تام موفر للطاقة بنسبة 100%"
    override val amoledModeDisabledDesc: String = "خلفية وأسطح سوداء حقيقية لتوفير البطارية"
    override val resetDefaultsButton: String = "استعادة الألوان الافتراضية"
    override val selectedIndicator: String = "محدد"

    override val overlayToolTitle: String = "الأداة العائمة فوق الألعاب"
    override val overlaySectionSubtitle: String = "لوحة عائمة سريعة لتسجيل الملاحظات أثناء اللعب دون الخروج من اللعبة"
    override val overlayPermissionTitle: String = "إذن الظهور فوق التطبيقات"
    override val overlayPermissionMessage: String = "يحتاج التطبيق لإذن الظهور فوق التطبيقات الأخرى لتفعيل النافذة العائمة أثناء اللعب."
    override val overlayPermissionGrant: String = "منح الإذن الآن"
    override val launchOverlayButton: String = "تشغيل الأداة العائمة"
    override val stopOverlayButton: String = "إيقاف الأداة العائمة"
    override val overlayRunningIndicator: String = "الأداة تعمل الآن"
    override val overlayRunningNotice: String = "يمكنك الآن فتح لعبتك واستخدام الأيقونة العائمة لتدوين الملاحظات أو عرض المهام."
    override val overlayQuickActive: String = "العائمة نشطة"
    override val overlayQuickInactive: String = "العائمة"

    override val trashTitle: String = "سلة المهملات"
    override val trashSubtitle: String = "استعادة الملاحظات المحذوفة أو تفريغ السلة"
    override val openTrashButton: String = "فتح سلة المهملات"
    override val trashEmptyTitle: String = "سلة المهملات فارغة"
    override val trashEmptySubtitle: String = "لا توجد ملاحظات محذوفة حالياً."
    override val trashAutoDeleteNotice: String = "يتم الاحتفاظ بالملاحظات المحذوفة مؤقتاً لتتمكن من استعادتها."
    override val restoreAll: String = "استعادة الكل"
    override val emptyTrash: String = "تفريغ السلة بالكامل"
    override val restoreSingle: String = "استعادة"
    override val deleteForeverSingle: String = "حذف نهائي"
    override val emptyTrashConfirmTitle: String = "تفريغ سلة المهملات؟"
    override val emptyTrashConfirmMessage: String = "سيتم حذف جميع الملاحظات الموجودة في السلة نهائياً وبلا رجعة. هل تريد المتابعة؟"
    override val emptyTrashConfirmAction: String = "نعم، تفريغ السلة"
    override val restoreSuccess: String = "تمت استعادة الملاحظة بنجاح"
    override val deleteForeverSuccess: String = "تم حذف الملاحظة نهائياً"
    override val emptyTrashSuccess: String = "تم إفراغ سلة المهملات بالكامل"
    override val noteDeletedMovedToTrash: String = "تم نقل الملاحظة إلى سلة المهملات"
    override val undo: String = "تراجع"
    override val trashNotesCountDesc: (Int) -> String = { count -> if (count == 0) "لا توجد ملاحظات محذوفة" else "$count ملاحظات محذوفة مؤقتاً" }
    override val trashRestoreAnytimePrompt: String = "يمكنك استعادة الملاحظات في أي وقت:"
    override val trashEmptyCardDesc: String = "عندما تقوم بحذف أي ملاحظة، سيتم تعيينها كـ 'مخفية' ونقلها إلى هنا بأمان حتى لا تفقد بياناتك ويمكنك استعادتها بلمسة واحدة."
    override val untitledNote: String = "ملاحظة بدون عنوان"
    override val modifiedDeletedDate: (String) -> String = { date -> "تاريخ التعديل/الحذف: $date" }
    override val confirmDeleteForeverTitle: String = "تأكيد الحذف النهائي"
    override val confirmDeleteForeverMessage: (String) -> String = { title -> "هل أنت متأكد من رغبتك في حذف '$title' نهائياً؟ لن تتمكن من استعادتها لاحقاً." }
    override val confirmEmptyTrashMessageWithCount: (Int) -> String = { count -> "سيتم حذف جميع الملاحظات الموجودة في سلة المهملات ($count ملاحظات) بشكل دائم ونهائي. هل تريد المتابعة؟" }
    override val noteTasksCompletedFormat: (Int, Int) -> String = { done, total -> "المهام: $done/$total مكتملة" }
    override val emptyTrashFullButton: String = "إفراغ السلة بالكامل"

    override val aiSectionHeader: String = "مساعد الذكاء الاصطناعي"
    override val aiSectionSubtitle: String = "أدخل مفتاحك الخاص لتفعيل ميزات الذكاء الاصطناعي واختيار النموذج"
    override val aiModelSelectionTitle: String = "نموذج الذكاء الاصطناعي:"
    override val aiModelFlashTitle: String = "جيمني فلاش 3.5"
    override val aiModelFlashDesc: String = "استهلاك خفيف وسريع ومثالي للمهام اليومية والأسئلة السريعة."
    override val aiModelFlashBadge: String = "خفيف وسريع"
    override val aiModelFlashLiteTitle: String = "جيمني فلاش لايت 3.8"
    override val aiModelFlashLiteDesc: String = "استهلاك متوازن ومناسب لتحليل الملاحظات المعقدة."
    override val aiModelFlashLiteBadge: String = "استهلاك متوازن"
    override val aiModelProTitle: String = "جيمني برو 3.1"
    override val aiModelProDesc: String = "أعلى قدرة وأعلى دقة في الخطط والاستراتيجيات العميقة."
    override val aiModelProBadge: String = "أعلى دقة"
    override val apiKeyFieldTitle: String = "مفتاح الربط الخاص بك:"
    override val apiKeyMandatoryLabel: String = "مفتاح الربط الخاص بك (إجباري):"
    override val apiKeyFieldHint: String = "الصق مفتاح الربط هنا..."
    override val apiKeySavedNotice: String = "مفتاح الربط محفوظ ومفعل بنجاح."
    override val apiKeyMissingNotice: String = "يرجى إدخال مفتاح الربط لتفعيل مساعد الذكاء الاصطناعي."
    override val apiKeyClearButton: String = "مسح المفتاح"
    override val aiLockedTitle: String = "ميزات الذكاء الاصطناعي مقفلة"
    override val aiLockedDesc: String = "يرجى كتابة أو لصق مفتاح الربط الخاص بك أعلاه لفتح قائمة النماذج وتفعيل المساعد الذكي."
    override val aiSelectModelPrompt: String = "اختر النموذج المناسب لاستهلاكك:"
    override val aiChatHistoryTitle: String = "سجل محادثات الذكاء الاصطناعي"
    override val aiChatHistorySubtitle: String = "عرض المحادثات السابقة ومسح السجل"
    override val aiChatHistoryEmpty: String = "لا توجد محادثات سابقة محفوظة."
    override val aiChatHistoryClearConfirmTitle: String = "مسح سجل المحادثات؟"
    override val aiChatHistoryClearConfirmMessage: String = "هل أنت متأكد من رغبتك في حذف جميع المحادثات السابقة المخزنة؟ لا يمكن التراجع عن هذا الإجراء."
    override val aiChatHistoryClearSuccess: String = "تم مسح سجل المحادثات بنجاح"
    override val chatMessagesCount: (Int) -> String = { count -> "$count رسالة" }
    override val viewChatHistoryButton: String = "عرض السجل"
    override val clearChatHistoryButton: String = "مسح السجل"
    override val clearAll: String = "مسح الكل"
    override val close: String = "إغلاق"
    override val aiAssistantButtonCd: String = "مساعد الذكاء الاصطناعي"
    override val showApiKey: String = "إظهار المفتاح"
    override val hideApiKey: String = "إخفاء المفتاح"
    override val confirmDeleteChat: String = "نعم، احذف السجل"
    override val deleteGameTabAction: String = "حذف التبويب"
    override val noteMovedToTrashNotice: (String) -> String = { "تم نقل \"$it\" إلى سلة المهملات" }
    override val clearInputText: String = "مسح"

    override val miniTaskListTitle: String = "قائمة المهام المصغرة للنافذة العائمة"
    override val miniTaskListSubtitle: String = "فتح قوائم المهام كنافذة مصغرة شفافة وقابلة للتحريك بدلاً من اللوحة الكاملة"
    override val miniTaskListEnabledDesc: String = "مفعل: تصغير المهام إلى ويدجت عائم مصغر وشفاف"
    override val miniTaskListDisabledDesc: String = "معطل: عرض المهام بالحجم الطبيعي (نصف شاشة)"

    override val backupRestoreTitle: String = "النسخ الاحتياطي والاستعادة"
    override val backupRestoreDesc: String = "تصدير أو استعادة قاعدة البيانات الكاملة مع كافة ملفات الصور وقوائم المهام والتبويبات بصيغة ملف مضغوط (ZIP) آمن."
    override val backupButton: String = "نسخ احتياطي"
    override val restoreButton: String = "استعادة"
    override val backupSuccess: String = "تم إنشاء النسخة الاحتياطية بنجاح"
    override val backupFailed: String = "فشل إنشاء النسخة الاحتياطية"
    override val restoreSuccessMsg: (Int, Int) -> String = { notes, tabs -> "تمت استعادة $notes ملاحظة و $tabs تبويب بنجاح" }
    override val restoreFailed: String = "فشل استعادة البيانات من الملف"

    override val pinNote: String = "تثبيت الملاحظة"
    override val unpinNote: String = "إلغاء التثبيت"
    override val editNote: String = "تعديل الملاحظة"
    override val deleteNote: String = "حذف"
    override val askGemini: String = "اسأل المساعد"
    override val askAi: String = "اسأل المساعد"
    override val pinAsMiniWidget: String = "تثبيت كقائمة مصغرة"
    override val openInFullView: String = "فتح بالعرض الكامل"
    override val progressTasks: (Int, Int) -> String = { done, total -> "المهام: $done من $total" }
    override val tasksCompletedSummary: (Int, Int) -> String = { done, total -> "$done / $total مكتمل" }
    override val achievementTasksFormat: (Int, Int) -> String = { done, total -> "الإنجاز: $done من $total مهام" }
    override val achievementLabel: String = "الإنجاز"
    override val regularNoteBadge: String = "ملاحظة غنية"
    override val checklistBadge: String = "قائمة مهام"
    override val showMore: String = "إظهار أكثر"
    override val showLess: String = "إظهار أقل"
    override val attachedImageZoom: String = "صورة مرفقة بالملاحظة - اضغط للتكبير"
    override val moreItemsCount: (Int) -> String = { count -> "... +$count عنصر إضافي" }
    override val firstNoteImage: String = "صورة الملاحظة الأولى"
    override val secondNoteImage: String = "صورة الملاحظة الثانية"

    override val selectCreationTypeTitle: String = "اختر نوع الإضافة"
    override val selectCreationTypeSubtitle: String = "حدد كيف تريد تنظيم خططك وأهدافك للعبة"
    override val regularNoteTitle: String = "ملاحظة عادية"
    override val regularNoteDesc: String = "مخصصة لكتابة النصوص الحرة، إدراج الصور، ومساعد Gemini الذكي."
    override val checklistNoteTitle: String = "قائمة مهام (To-Do List)"
    override val checklistNoteDesc: String = "مخصصة فقط لإضافة مهام يمكن وضع علامة صح عليها أثناء اللعب."
    override val newTodoListTitle: String = "قائمة مهام جديدة"
    override val editTodoListTitle: String = "تعديل قائمة المهام"
    override val newNoteDialogTitle: String = "ملاحظة جديدة"
    override val editNoteDialogTitle: String = "تعديل الملاحظة"
    override val checklistInteractiveSubtitle: String = "مهام تفاعلية وقابلة للإنجاز"
    override val regularNoteSubtitle: String = "نصوص حرة، صور، ومساعد Gemini"
    override val editNoteDialogGameTagLabel: String = "لعبة الملاحظة (التبويب):"
    override val customGameTagPlaceholder: String = "أو اكتب اسم لعبة مخصص (اختياري)"
    override val exampleGameName: String = "مثال: Elden Ring"
    override val checklistTitlePlaceholder: String = "عنوان قائمة المهام (مثال: متطلبات بوابة النذر)"
    override val noteTitleLabelPlaceholder: String = "عنوان الملاحظة (مثال: خطة بناء الدفيئة)"
    override val noteContentFreeTextLabel: String = "محتوى الملاحظة (نصوص حرة)"
    override val noteContentCraftingPlaceholder: String = "اكتب تفاصيل الكرافتينج، المواقع، الإحداثيات، أو التعليمات..."
    override val attachedImagesLabel: String = "الصور والخرائط المرفقة:"
    override val attachImageButton: String = "إرفاق صورة"
    override val attachedImageZoomCd: String = "صورة مرفقة - انقر للتكبير"
    override val deleteImageActionCd: String = "حذف الصورة"
    override val checklistItemsSectionTitle: String = "عناصر قائمة المهام:"
    override val addChecklistPlaceholder: String = "أضف مهمة (مثال: جمع 10 حبات ألماس)"
    override val addChecklistActionCd: String = "إضافة مهمة"
    override val emptyChecklistHint: String = "لم تضف مهاماً بعد. اكتب المهمة واضغط '+' لإضافتها."
    override val deleteChecklistActionCd: String = "حذف المهمة"
    override val saveAndCreateNote: String = "حفظ وإنشاء"
    override val saveEditsNote: String = "حفظ التعديلات"
    override val newNoteForGamePrompt: (String) -> String = { game -> "ملاحظة لـ $game" }
    override val newNoteGeneralPrompt: String = "ملاحظة جديدة"

    override val newNoteTitleHint: String = "ملاحظة جديدة"
    override val noteTitlePlaceholder: String = "عنوان الملاحظة..."
    override val defaultGeneralGameTag: String = "عام (اضغط للتعديل)"
    override val editGameTagPrompt: String = "تعديل اللعبة"
    override val backAndSave: String = "رجوع وحفظ"
    override val insertChecklistAction: String = "إدراج قائمة مهام"
    override val insertImageAction: String = "إدراج صورة"
    override val writeNoteContentPlaceholder: String = "اكتب تفاصيل الملاحظة هنا..."
    override val tagsSectionTitle: String = "الوسوم والتصنيفات:"
    override val addTagPlaceholder: String = "أضف وسم جديد..."
    override val addTagButton: String = "إضافة"
    override val quickAddTag: String = "وسم سريع"
    override val imageSavedNotice: String = "تم حفظ الصورة بنجاح"
    override val noteSavedNotice: String = "تم حفظ الملاحظة بنجاح"
    override val deleteNoteConfirmTitle: String = "حذف الملاحظة؟"
    override val deleteNoteConfirmMessage: String = "هل أنت متأكد من حذف هذه الملاحظة ونقلها إلى سلة المهملات؟"
    override val shareNoteAction: String = "مشاركة الملاحظة"
    override val shareNoteChooserTitle: String = "مشاركة الملاحظة عبر"
    override val shareNoteErrorNotice: String = "فشل في فتح نافذة المشاركة"
    override val pinToFloatingWindow: String = "تثبيت في النافذة العائمة"
    override val pinnedToFloatingSuccess: (String) -> String = { "تم تثبيت ($it) للفتح المباشر في النافذة العائمة" }
    override val unpinnedFromFloatingSuccess: String = "تم إلغاء التثبيت من النافذة العائمة"
    override val saveFirstToPin: String = "يرجى كتابة عنوان أو حفظ الملاحظة أولاً للتثبيت"
    override val internalTabsTitle: String = "التبويبات الداخلية"
    override val newInternalTab: String = "تبويب جديد"
    override val tabNumber: (Int) -> String = { "تبويب $it" }
    override val editTabName: String = "تعديل اسم التبويب"
    override val deleteTabCd: String = "حذف التبويب"
    override val addTabCd: String = "إضافة تبويب"
    override val expandTabsCd: String = "توسيع التبويبات"
    override val collapseTabsCd: String = "طي التبويبات"
    override val addInternalTabTitle: String = "إضافة تبويب داخلي جديد"
    override val tabNameLabel: String = "اسم التبويب"
    override val tabNamePlaceholder: String = "مثلاً: المهام، المواقع، الاستراتيجية..."
    override val deleteInternalTabConfirm: (String) -> String = { "هل أنت متأكد من حذف \"$it\" ومحتوياته؟ لا يمكن التراجع عن هذه الخطوة." }
    override val thisTab: String = "هذا التبويب"
    override val saveAndClose: String = "حفظ وإغلاق"
    override val addFreeText: String = "+ نص حر"
    override val addImage: String = "+ صورة"
    override val addTask: String = "+ مهمة"
    override val firstImageZoomCd: String = "الصورة الأولى - اضغط للتكبير"
    override val secondImageZoomCd: String = "الصورة الثانية - اضغط للتكبير"
    override val deleteFirstImageCd: String = "حذف الصورة الأولى"
    override val deleteSecondImageCd: String = "حذف الصورة الثانية"
    override val selectOrChangeGameTab: String = "اختر أو غيّر تبويب اللعبة"
    override val availableTabsTitle: String = "التبويبات المتاحة:"
    override val orWriteNewGameName: String = "أو اكتب اسم لعبة جديد"

    override val newChecklistTitleHint: String = "قائمة مهام جديدة"
    override val taskPlaceholder: String = "أضف مهمة جديدة..."
    override val addTaskButton: String = "إضافة مهمة"
    override val allTasksDoneBanner: String = "🎉 رائع! تم إنجاز جميع المهام بنجاح"
    override val progressLabel: String = "نسبة الإنجاز"
    override val dragToReorder: String = "اسحب لإعادة الترتيب"
    override val checklistTitleLabel: String = "عنوان قائمة المهام:"
    override val checklistEditorTitlePlaceholder: String = "مثال: مهام اليوم في اللعبة، متطلبات الزعيم..."
    override val checklistItemsInstructions: String = "عناصر المهام (اضغط Enter للانتقال للمهمة التالية تلقائياً):"
    override val writeTaskPlaceholder: (Int) -> String = { num -> "اكتب المهمة $num..." }
    override val deleteTaskCd: String = "حذف المهمة"
    override val shareChecklistAction: String = "مشاركة المهام"
    override val shareChecklistChooserTitle: String = "مشاركة قائمة المهام عبر"

    override val geminiAssistantTitle: String = "المساعد الذكي"
    override val geminiAssistantSubtitle: String = "تحليل الخطط، استراتيجيات اللعب وتلخيص الملاحظات"
    override val geminiInputHint: String = "اسأل المساعد عن استراتيجية أو مهمة..."
    override val geminiNoApiKeyNotice: String = "يتطلب إدخال مفتاح الربط في الإعدادات"
    override val geminiConfigureApiKeyPrompt: String = "يرجى إضافة مفتاح الربط من القائمة الجانبية لتفعيل المساعد الذكي."
    override val geminiThinking: String = "جارٍ التفكير وتوليد الرد..."
    override val geminiQuickSuggestTitle: String = "اقتراحات سريعة:"
    override val geminiQuickSuggestTasks: String = "اقترح خطوات لإنهاء هذه المهمة"
    override val geminiQuickSummarize: String = "لخص هذه الملاحظة"
    override val geminiQuickTranslate: String = "ترجم إلى استراتيجية واضحة"
    override val geminiCopyResponse: String = "نسخ الرد"
    override val geminiCopiedNotice: String = "تم نسخ الرد بنجاح"
    override val geminiNewChatSession: String = "محادثة جديدة"
    override val geminiClearSession: String = "مسح المحادثة"
    override val geminiAskAboutNote: (String) -> String = { title -> "استفسار حول: $title" }
    override val geminiWelcomeGreeting: (String) -> String = { game -> "مرحباً يا بطل! أنا مساعدك الذكي في ألعابك ($game). تم نقل نص ملاحظتك إلى المربع بالأسفل؛ يمكنك تعديله أو إضافة سؤالك قبل الضغط على إرسال!" }
    override val geminiNewChatStarted: String = "بدأت محادثة جديدة! كيف يمكنني مساعدتك في ألعابك اليوم؟"
    override val geminiErrorCommunicating: String = "حدث خطأ أثناء التواصل مع المساعد"
    override val geminiAttachedImageAnalysis: String = "🖼️ [تحليل الصورة المرفقة]"
    override val geminiLinkedToNote: (String) -> String = { title -> "مرتبط بـ: $title" }
    override val geminiClearCurrentChatCd: String = "مسح المحادثة الحالية"
    override val geminiClearChatConfirmTitle: String = "مسح هذه المحادثة"
    override val geminiClearChatConfirmMessage: String = "هل أنت متأكد من رغبتك في حذف رسائل هذه المحادثة؟ لا يمكن التراجع عن هذه الخطوة."
    override val geminiPastSessionsTitle: String = "سجل المحادثات السابقة"
    override val geminiNoPastChats: String = "لا توجد محادثات سابقة محفوظة."
    override val geminiDeleteSessionCd: String = "حذف المحادثة"
    override val geminiYouLabel: String = "أنت"
    override val geminiAiLabel: String = "المساعد"
    override val geminiQuickQuestion1: String = "كيف أنفذ هذا بأفضل طريقة؟"
    override val geminiQuickQuestion2: String = "أين أجد المواد المطلوبة؟"
    override val geminiQuickQuestion3: String = "ما هي التوقيتات المناسبة؟"
    override val geminiChatSessionCreatedNotice: String = "تم بدء محادثة جديدة"
    override val geminiChatWithNoteTitle: (String) -> String = { title -> "محادثة $title" }
    override val geminiChatHistoryCd: String = "سجل المحادثات"
    override val geminiNewChatCd: String = "محادثة جديدة"
    override val geminiReplyErrorPrefix: (String) -> String = { err -> "عذراً، تعذر الحصول على رد: $err" }
    override val geminiChatClearedGreeting: String = "تم مسح المحادثة بنجاح! مرحباً بك، كيف يمكنني مساعدتك في ألعابك اليوم؟"
    override val geminiTextCopiedToInputHint: String = "💡 النص منسوخ للمربع بالأسفل، يمكنك قراءته وتعديله ثم الضغط على إرسال بنفسك."
    override val geminiAttachImageCd: String = "إرفاق صورة للمساعد الذكي"
    override val geminiInputDisabledHint: String = "أدخل مفتاحك في الإعدادات لفتح الذكاء الاصطناعي"
    override val geminiSendMessageCd: String = "إرسال إلى المساعد الذكي"
    override val geminiRemoveImageCd: String = "إزالة الصورة"
    override val geminiAttachedImageLabel: String = "صورة مرفقة للتحليل بواسطة المساعد الذكي"
    override val geminiConvertToNote: String = "تحويل لملاحظة"
    override val geminiConvertToChecklist: String = "تحويل لمهام"
    override val geminiConvertedToNoteSuccess: String = "تم إنشاء ملاحظة جديدة بنجاح"
    override val geminiConvertedToChecklistSuccess: String = "تم إنشاء قائمة مهام بنجاح"
    override val geminiNoteFallbackTitle: String = "ملاحظة من الذكاء الاصطناعي"
    override val geminiTasksFallbackTitle: String = "مهام من الذكاء الاصطناعي"

    override val timerTitle: String = "المؤقت التنازلي العائم"
    override val timerSubtitle: String = "تتبع أوقات الزعماء ومهمات الألعاب"
    override val timerTimeUp: String = "⏰ انتهى الوقت!"
    override val timerCounting: (String) -> String = { label -> "جارٍ العد: $label" }
    override val timerStopped: String = "المؤقت متوقف"
    override val timerStart: String = "بدء"
    override val timerPause: String = "إيقاف مؤقت"
    override val timerReset: String = "إعادة ضبط"
    override val timerQuickPresets: String = "أوقات سريعة جاهزة:"
    override val timerCustomMinutesHint: String = "أدخل الدقائق..."
    override val timerSetButton: String = "ضبط"
    override val timerShowInOverlay: String = "تشغيل كأداة عائمة"
    override val timerMin: String = "د"
    override val timerSec: String = "ث"
    override val timerResume: String = "استئناف"
    override val timerNotificationNotice: String = "يتم إرسال إشعار أندرويد مع تنبيه صوتي عند انتهاء الوقت حتى لو أغلقت التطبيق"
    override val timerTaskNameLabel: String = "اسم المهمة أو الزعيم"
    override val timerTaskNamePlaceholder: String = "مثال: إعادة ظهور الزعيم، وقت الدروع..."
    override val timerDurationMinutesLabel: String = "المدة بالدقائق"
    override val timerStartFullButton: String = "بدء المؤقت"
    override val timerPresetMinutes: (Int) -> String = { mins -> if (mins == 1) "1 دقيقة" else if (mins in 3..10) "$mins دقائق" else "$mins دقيقة" }
    override val timerDefaultLabelFormat: (Int) -> String = { mins -> "مؤقت $mins دقيقة" }
    override val doneButton: String = "تم"

    override val imageZoomTitle: String = "عرض وتحرير الصورة"
    override val drawingMode: String = "وضع الرسم"
    override val panZoomMode: String = "وضع التكبير والتحريك"
    override val undoDrawing: String = "تراجع"
    override val clearDrawing: String = "مسح الرسم"
    override val saveDrawing: String = "حفظ التعديلات"
    override val brushSize: String = "حجم الفرشاة:"
    override val brushColor: String = "لون الفرشاة:"
    override val imageSavedToStorage: String = "تم حفظ الصورة المعدلة بنجاح"
    override val fullScreenImageCd: String = "صورة مكبرة بكامل الشاشة"
    override val drawingModeActiveNotice: String = "وضع الرسم نشط"
    override val drawAnnotateButton: String = "الرسم / التأشير"
    override val resetImageCd: String = "إعادة الضبط"
    override val noDrawingsToSave: String = "لا توجد رسومات لحفظها"
    override val imageSaveSuccess: String = "تم حفظ التعديلات على الصورة بنجاح!"
    override val imageProcessFailed: String = "فشل في معالجة وحفظ الصورة"
    override val imageZoomHelpTip: String = "قرّب بإصبعين للتكبير أو اضغط 'الرسم' للتأشير بالقلم"

    override val overlayDockSide: String = "تبديل جهة الالتصاق"
    override val overlayMinimize: String = "تصغير للأيقونة"
    override val overlayExpandToPanel: String = "فتح اللوحة كاملة"
    override val overlayQuickAddTaskHint: String = "مهمة سريعة جديدة..."
    override val overlayAddQuickTask: String = "إضافة مهمة سريعة"
    override val overlayNoNotesInGame: String = "لا توجد ملاحظات لهذه اللعبة"
    override val overlaySwitchToGame: String = "تبديل اللعبة"
    override val overlayOpenFullApp: String = "فتح في التطبيق الكامل"
    override val overlaySearchInOverlay: String = "بحث سريع..."
    override val overlayCreateNote: String = "ملاحظة جديدة"
    override val overlayCreateChecklist: String = "قائمة مهام جديدة"
    override val overlayNotificationTitle: String = "مفكرة الألعاب عائمة فوق الشاشة"
    override val overlayNotificationText: String = "اضغط لفتح الملاحظات أو اسحب للأسفل للإغلاق"
    override val overlayNotificationStopAction: String = "إيقاف الأداة العائمة"
    override val overlayNotificationChannelDesc: String = "أداة عائمة فوق الألعاب لعرض الملاحظات وقوائم المهام"
    override val overlayClosedToast: String = "تم إغلاق الأداة العائمة"
    override val miniWidgetClosedToast: String = "تم إغلاق قائمة المهام العائمة"
    override val overlayTasksTitle: String = "مهام"
    override val overlayNoTasksCurrently: String = "لا توجد مهام حالياً"
    override val overlayDragToMove: String = "سحب لنقل القائمة"
    override val overlayCloseMiniWidget: String = "إغلاق المصغر"
    override val overlayGamesLabel: String = "الألعاب:"
    override val overlayChannelName: String = "GameNotes أداة عائمة"
    override val releaseToDismiss: String = "أفلت للإلغاء"
    override val pickingImageInProgress: String = "جاري اختيار الصورة..."
    override val todoTasks: String = "مهام"
    override val overlayToggleDockSide: String = "نقل الجانب"
    override val overlayMinimizeToBubble: String = "تصغير للأيقونة"

    override val colorEmeraldDefault: String = "زمردي (افتراضي)"
    override val colorNeonCyan: String = "سماوي نيون"
    override val colorGlowingGold: String = "ذهبي متوهج"
    override val colorCyberPurple: String = "بنفسجي سايبر"
    override val colorPureWhite: String = "أبيض ناصع"
    override val colorIceBlue: String = "أزرق ثلجي"
    override val colorFieryRed: String = "أحمر ناري"
    override val colorLimeGreen: String = "أخضر ليموني"
    override val colorDarkCharcoal: String = "فحمي داكن"
    override val colorObsidianDefault: String = "أوبسيديان (افتراضي)"
    override val colorAmoledPureBlack: String = "أسود نقي أوليد"
    override val colorMidnightGray: String = "رمادي ليلي"
    override val colorSpaceBlue: String = "أزرق الفضاء"
    override val colorCosmicPurple: String = "بنفسجي كوني"
    override val colorDeepNavy: String = "كحلي عميق"
    override val colorLightGrayDay: String = "رمادي فاتح (نهاري)"

    override val tapToEdit: String = "اضغط للتعديل"
    override val twoSideBySideImagesNotice: String = "صورتان متجاورتان بالتساوي (50% / 50%)"
    override val deleteBothImages: String = "حذف الصورتين"
    override val longPressToReorder: String = "اسحب مطولاً لإعادة الترتيب"
    override val noteImageClickToZoom: String = "صورة الملاحظة - انقر للتكبير"
    override val zoomImage: String = "تكبير الصورة"
    override val writeTextBesideImagePlaceholder: String = "اكتب نصاً بجانب الصورة..."
    override val alignmentRight: String = "المحاذاة: يمين"
    override val alignmentLeft: String = "المحاذاة: شمال"
    override val addSideBySideImage: String = "+ صورة بجانبها"
    override val deleteImage: String = "حذف الصورة"
    override val sizeLabel: String = "الحجم:"
    override val resumeWritingNormalTextHint: String = "المس هنا لاستئناف كتابة نص عادي أسفل المهام..."
}

private object EnglishAppStrings : AppStrings() {
    override val appName: String = "GameNotes"
    override val appVersionNotice: String = "Smart Gaming Notes | v1.3.5"
    override val smartNotesSubtitle: String = "Smart Game Notes"
    override val settingsTitle: String = "GameNotes Settings"
    override val settingsSubtitle: String = "Customize language, appearance, and AI assistant"
    override val languageSectionTitle: String = "App Language"
    override val languageSectionSubtitle: String = "Instant switch between English and Arabic"
    override val arabicLanguageName: String = "Arabic"
    override val englishLanguageName: String = "English"

    override val tabAll: String = "All"
    override val addNewTab: String = "Add New Tab"
    override val newTabNameHint: String = "Game or category name..."
    override val addTabButton: String = "Add"
    override val deleteTabTitle: String = "Delete Tab"
    override val deleteTabMessage: (String) -> String = { tabName -> "Are you sure you want to delete tab \"$tabName\"? Associated notes will not be deleted but moved to General." }
    override val deleteConfirm: String = "Delete"
    override val cancel: String = "Cancel"
    override val confirm: String = "Confirm"
    override val searchPlaceholder: String = "Search notes and tasks..."
    override val searchInGamePlaceholder: (String) -> String = { game -> "Search in $game notes & tasks..." }
    override val searchAllPlaceholder: String = "Search all notes and tasks..."
    override val noNotesFound: String = "No matching notes"
    override val noNotesSubtitle: String = "No notes or tasks match your search query."
    override val addFirstNotePrompt: String = "Tap the (+) button to create your first note or checklist."
    override val gameTabsLabel: String = "Game Tabs:"
    override val addGameTab: String = "Add Game"
    override val closeTabMenu: String = "Close"
    override val createGameTabTitle: String = "Create New Game Tab"
    override val gameNameLabel: String = "Game Name"
    override val gameNamePlaceholder: String = "e.g., Minecraft, Elden Ring, Zelda..."
    override val addTabConfirmButton: String = "Add Tab"
    override val emptyTabsTitle: String = "Tabs bar is empty"
    override val emptyTabsSubtitle: String = "Add your first game tab (e.g. Minecraft or Stardew Valley) to organize notes."
    override val createTabAction: String = "Create Tab"
    override val newGameInlineChip: String = "+ New Game"
    override val tabHeaderTitle: (String) -> String = { game -> "Game Tab: $game" }
    override val tabHeaderSubtitle: (Int) -> String = { count -> "$count notes / checklists for this game" }
    override val tabAddNoteButton: (String) -> String = { game -> "Add new note for $game" }
    override val welcomeTitle: String = "Welcome to GameNotes"
    override val welcomeSubtitle: String = "Your smart companion for tracking game plans, coordinates, and checklists."
    override val noNotesForGameTitle: (String) -> String = { game -> "No notes for '$game' yet" }
    override val noNotesForGameSubtitle: (String) -> String = { game -> "Start recording your coordinates, quest plans, or checklist for '$game'." }
    override val noMatchingItemsTitle: String = "No matching items"
    override val noMatchingItemsSubtitle: String = "Try searching with different keywords or select another tab."
    override val addNoteOrChecklist: String = "Add Note or Checklist"
    override val addNoteForGame: (String) -> String = { game -> "Add Note for $game" }
    override val notesCountBadge: (Int) -> String = { count -> "$count items" }
    override val selectedTabPrefix: (String) -> String = { tab -> "Tab: $tab" }
    override val openMenu: String = "Open Menu"
    override val clearSearchCd: String = "Clear Search"
    override val tabAlreadyExistsToast: String = "Tab already exists and is now selected"
    override val tabCreatedSuccessToast: (String) -> String = { name -> "Tab '$name' created successfully" }
    override val notePinnedToast: String = "Note pinned to top 📌"
    override val noteUnpinnedToast: String = "Note unpinned"
    override val miniTaskListPinnedToast: String = "Mini task-list pinned over screen"
    override val overlayStartedToast: String = "Floating tool enabled successfully"
    override val apiKeyClearedToast: String = "API key cleared"
    override val shareRemainingTasksLabel: String = "\nRemaining Tasks: "

    override val appearanceSectionTitle: String = "Appearance & Colors"
    override val appearanceSectionSubtitle: String = "Customize colors and theme with instant auto-save"
    override val textColorLabel: String = "Accent & Top Bar Color:"
    override val bgColorLabel: String = "Primary Background Color:"
    override val amoledModeTitle: String = "AMOLED True Black"
    override val amoledModeEnabledDesc: String = "Active: 100% true black for maximum power saving"
    override val amoledModeDisabledDesc: String = "True pure black backgrounds and surfaces to save battery"
    override val resetDefaultsButton: String = "Reset Colors to Default"
    override val selectedIndicator: String = "Selected"

    override val overlayToolTitle: String = "Floating Overlay Tool"
    override val overlaySectionSubtitle: String = "Quick floating panel for taking notes while gaming without leaving the game"
    override val overlayPermissionTitle: String = "Display Over Other Apps Permission"
    override val overlayPermissionMessage: String = "The app needs permission to display over other apps to enable floating notes while gaming."
    override val overlayPermissionGrant: String = "Grant Permission Now"
    override val launchOverlayButton: String = "Launch Floating Tool"
    override val stopOverlayButton: String = "Stop Floating Tool"
    override val overlayRunningIndicator: String = "Tool is currently active"
    override val overlayRunningNotice: String = "You can now open your game and use the floating icon to view or take notes."
    override val overlayQuickActive: String = "Active Overlay"
    override val overlayQuickInactive: String = "Overlay"

    override val trashTitle: String = "Trash Bin"
    override val trashSubtitle: String = "Restore deleted notes or empty trash"
    override val openTrashButton: String = "Open Trash"
    override val trashEmptyTitle: String = "Trash is Empty"
    override val trashEmptySubtitle: String = "No deleted notes right now."
    override val trashAutoDeleteNotice: String = "Deleted notes are preserved here so you can safely restore them."
    override val restoreAll: String = "Restore All"
    override val emptyTrash: String = "Empty Trash"
    override val restoreSingle: String = "Restore"
    override val deleteForeverSingle: String = "Delete Forever"
    override val emptyTrashConfirmTitle: String = "Empty Trash Bin?"
    override val emptyTrashConfirmMessage: String = "All notes in the trash bin will be permanently deleted. This action cannot be undone."
    override val emptyTrashConfirmAction: String = "Yes, Empty Trash"
    override val restoreSuccess: String = "Note restored successfully"
    override val deleteForeverSuccess: String = "Note permanently deleted"
    override val emptyTrashSuccess: String = "Trash bin emptied successfully"
    override val noteDeletedMovedToTrash: String = "Note moved to Trash"
    override val undo: String = "Undo"
    override val trashNotesCountDesc: (Int) -> String = { count -> if (count == 0) "No deleted notes" else "$count notes temporarily deleted" }
    override val trashRestoreAnytimePrompt: String = "You can restore notes anytime:"
    override val trashEmptyCardDesc: String = "When you delete any note, it is safely moved here as 'hidden' so you never lose data and can restore it with one tap."
    override val untitledNote: String = "Untitled Note"
    override val modifiedDeletedDate: (String) -> String = { date -> "Modified/Deleted date: $date" }
    override val confirmDeleteForeverTitle: String = "Confirm Permanent Delete"
    override val confirmDeleteForeverMessage: (String) -> String = { title -> "Are you sure you want to permanently delete '$title'? You will not be able to recover it later." }
    override val confirmEmptyTrashMessageWithCount: (Int) -> String = { count -> "All notes in the trash bin ($count notes) will be permanently deleted. Do you want to proceed?" }
    override val noteTasksCompletedFormat: (Int, Int) -> String = { done, total -> "Tasks: $done/$total completed" }
    override val emptyTrashFullButton: String = "Empty Trash Completely"

    override val aiSectionHeader: String = "AI Game Assistant"
    override val aiSectionSubtitle: String = "Enter your API key to enable AI features and model selection"
    override val aiModelSelectionTitle: String = "AI Model Selection:"
    override val aiModelFlashTitle: String = "Gemini Flash 3.5"
    override val aiModelFlashDesc: String = "Fast, lightweight consumption ideal for quick queries."
    override val aiModelFlashBadge: String = "Fast & Light"
    override val aiModelFlashLiteTitle: String = "Gemini Flash Lite 3.8"
    override val aiModelFlashLiteDesc: String = "Balanced consumption, suitable for thorough note analysis."
    override val aiModelFlashLiteBadge: String = "Balanced"
    override val aiModelProTitle: String = "Gemini Pro 3.1"
    override val aiModelProDesc: String = "High capacity and maximum precision for deep game strategies."
    override val aiModelProBadge: String = "Top Accuracy"
    override val apiKeyFieldTitle: String = "Your Gemini API Key:"
    override val apiKeyMandatoryLabel: String = "Your Gemini API Key (Required):"
    override val apiKeyFieldHint: String = "Paste your API key here..."
    override val apiKeySavedNotice: String = "API key saved and active."
    override val apiKeyMissingNotice: String = "Please provide an API key to enable AI assistant features."
    override val apiKeyClearButton: String = "Clear Key"
    override val aiLockedTitle: String = "AI Features are Locked"
    override val aiLockedDesc: String = "Please enter or paste your API key above to unlock models and enable the assistant."
    override val aiSelectModelPrompt: String = "Select the model for your usage:"
    override val aiChatHistoryTitle: String = "AI Chat History"
    override val aiChatHistorySubtitle: String = "View past conversations and clear chat logs"
    override val aiChatHistoryEmpty: String = "No saved chat history."
    override val aiChatHistoryClearConfirmTitle: String = "Clear Chat History?"
    override val aiChatHistoryClearConfirmMessage: String = "Are you sure you want to delete all stored chat sessions? This action cannot be undone."
    override val aiChatHistoryClearSuccess: String = "Chat history cleared successfully"
    override val chatMessagesCount: (Int) -> String = { count -> "$count messages" }
    override val viewChatHistoryButton: String = "View History"
    override val clearChatHistoryButton: String = "Clear History"
    override val clearAll: String = "Clear All"
    override val close: String = "Close"
    override val aiAssistantButtonCd: String = "AI Assistant"
    override val showApiKey: String = "Show Key"
    override val hideApiKey: String = "Hide Key"
    override val confirmDeleteChat: String = "Yes, Delete History"
    override val deleteGameTabAction: String = "Delete Tab"
    override val noteMovedToTrashNotice: (String) -> String = { "Moved \"$it\" to trash" }
    override val clearInputText: String = "Clear"

    override val miniTaskListTitle: String = "Floating Mini Task List"
    override val miniTaskListSubtitle: String = "Open checklists as movable semi-transparent widgets instead of the full panel"
    override val miniTaskListEnabledDesc: String = "Enabled: Minimize checklists into a compact floating widget"
    override val miniTaskListDisabledDesc: String = "Disabled: Display checklists in regular size (half screen)"

    override val backupRestoreTitle: String = "Backup & Restore"
    override val backupRestoreDesc: String = "Export or restore the entire database along with all attached images, checklists, and tabs as a secure ZIP archive."
    override val backupButton: String = "Backup"
    override val restoreButton: String = "Restore"
    override val backupSuccess: String = "Backup created successfully"
    override val backupFailed: String = "Failed to create backup"
    override val restoreSuccessMsg: (Int, Int) -> String = { notes, tabs -> "Successfully restored $notes notes and $tabs tabs" }
    override val restoreFailed: String = "Failed to restore data from file"

    override val pinNote: String = "Pin Note"
    override val unpinNote: String = "Unpin Note"
    override val editNote: String = "Edit Note"
    override val deleteNote: String = "Delete"
    override val askGemini: String = "Ask AI"
    override val askAi: String = "Ask AI"
    override val pinAsMiniWidget: String = "Pin as mini widget"
    override val openInFullView: String = "Open Full Screen"
    override val progressTasks: (Int, Int) -> String = { done, total -> "Tasks: $done of $total" }
    override val tasksCompletedSummary: (Int, Int) -> String = { done, total -> "$done / $total completed" }
    override val achievementTasksFormat: (Int, Int) -> String = { done, total -> "Progress: $done of $total tasks" }
    override val achievementLabel: String = "Progress"
    override val regularNoteBadge: String = "Rich Note"
    override val checklistBadge: String = "Checklist"
    override val showMore: String = "Show More"
    override val showLess: String = "Show Less"
    override val attachedImageZoom: String = "Attached image - Tap to zoom"
    override val moreItemsCount: (Int) -> String = { count -> "... +$count more items" }
    override val firstNoteImage: String = "First note image"
    override val secondNoteImage: String = "Second note image"

    override val selectCreationTypeTitle: String = "Select Note Type"
    override val selectCreationTypeSubtitle: String = "Choose how you want to organize your game plans and goals"
    override val regularNoteTitle: String = "Regular Note"
    override val regularNoteDesc: String = "For free-form text, inline images, and Gemini AI assistant."
    override val checklistNoteTitle: String = "To-Do List"
    override val checklistNoteDesc: String = "Dedicated checklist with tap-to-complete tasks while gaming."
    override val newTodoListTitle: String = "New Checklist"
    override val editTodoListTitle: String = "Edit Checklist"
    override val newNoteDialogTitle: String = "New Note"
    override val editNoteDialogTitle: String = "Edit Note"
    override val checklistInteractiveSubtitle: String = "Interactive actionable quest tasks"
    override val regularNoteSubtitle: String = "Free text, images, and Gemini AI"
    override val editNoteDialogGameTagLabel: String = "Game Tab:"
    override val customGameTagPlaceholder: String = "Or type custom game name (optional)"
    override val exampleGameName: String = "e.g., Elden Ring"
    override val checklistTitlePlaceholder: String = "Checklist title (e.g., Nether portal supplies)"
    override val noteTitleLabelPlaceholder: String = "Note title (e.g., Greenhouse build plan)"
    override val noteContentFreeTextLabel: String = "Note content (free text)"
    override val noteContentCraftingPlaceholder: String = "Write crafting details, locations, coordinates, or steps..."
    override val attachedImagesLabel: String = "Attached images & maps:"
    override val attachImageButton: String = "Attach Image"
    override val attachedImageZoomCd: String = "Attached image - tap to zoom"
    override val deleteImageActionCd: String = "Delete image"
    override val checklistItemsSectionTitle: String = "Checklist Items:"
    override val addChecklistPlaceholder: String = "Add task (e.g., mine 10 diamonds)"
    override val addChecklistActionCd: String = "Add task"
    override val emptyChecklistHint: String = "No tasks yet. Type a task and tap '+' to add."
    override val deleteChecklistActionCd: String = "Delete task"
    override val saveAndCreateNote: String = "Save & Create"
    override val saveEditsNote: String = "Save Changes"
    override val newNoteForGamePrompt: (String) -> String = { game -> "Note for $game" }
    override val newNoteGeneralPrompt: String = "New Note"

    override val newNoteTitleHint: String = "New Note"
    override val noteTitlePlaceholder: String = "Note title..."
    override val defaultGeneralGameTag: String = "General (Tap to edit)"
    override val editGameTagPrompt: String = "Edit Game"
    override val backAndSave: String = "Back & Save"
    override val insertChecklistAction: String = "Insert Checklist"
    override val insertImageAction: String = "Insert Image"
    override val writeNoteContentPlaceholder: String = "Write note details here..."
    override val tagsSectionTitle: String = "Tags & Categories:"
    override val addTagPlaceholder: String = "Add a new tag..."
    override val addTagButton: String = "Add"
    override val quickAddTag: String = "Quick Tag"
    override val imageSavedNotice: String = "Image saved successfully"
    override val noteSavedNotice: String = "Note saved successfully"
    override val deleteNoteConfirmTitle: String = "Delete Note?"
    override val deleteNoteConfirmMessage: String = "Are you sure you want to move this note to the Trash bin?"
    override val shareNoteAction: String = "Share Note"
    override val shareNoteChooserTitle: String = "Share note via"
    override val shareNoteErrorNotice: String = "Failed to open share dialog"
    override val pinToFloatingWindow: String = "Pin to Floating Window"
    override val pinnedToFloatingSuccess: (String) -> String = { "Pinned ($it) to open directly in floating window" }
    override val unpinnedFromFloatingSuccess: String = "Unpinned from floating window"
    override val saveFirstToPin: String = "Please write a title or save the note first to pin"
    override val internalTabsTitle: String = "Internal Tabs"
    override val newInternalTab: String = "New Tab"
    override val tabNumber: (Int) -> String = { "Tab $it" }
    override val editTabName: String = "Edit Tab Name"
    override val deleteTabCd: String = "Delete Tab"
    override val addTabCd: String = "Add Tab"
    override val expandTabsCd: String = "Expand Tabs"
    override val collapseTabsCd: String = "Collapse Tabs"
    override val addInternalTabTitle: String = "Add New Internal Tab"
    override val tabNameLabel: String = "Tab Name"
    override val tabNamePlaceholder: String = "e.g. Quests, Locations, Strategy..."
    override val deleteInternalTabConfirm: (String) -> String = { "Are you sure you want to delete \"$it\" and its contents? This cannot be undone." }
    override val thisTab: String = "this tab"
    override val saveAndClose: String = "Save & Close"
    override val addFreeText: String = "+ Text"
    override val addImage: String = "+ Image"
    override val addTask: String = "+ Task"
    override val firstImageZoomCd: String = "First image - Tap to zoom"
    override val secondImageZoomCd: String = "Second image - Tap to zoom"
    override val deleteFirstImageCd: String = "Delete first image"
    override val deleteSecondImageCd: String = "Delete second image"
    override val selectOrChangeGameTab: String = "Select or change game tab"
    override val availableTabsTitle: String = "Available Tabs:"
    override val orWriteNewGameName: String = "Or write a new game name"

    override val newChecklistTitleHint: String = "New Checklist"
    override val taskPlaceholder: String = "Add a new task..."
    override val addTaskButton: String = "Add Task"
    override val allTasksDoneBanner: String = "🎉 Awesome! All tasks are completed"
    override val progressLabel: String = "Completion Progress"
    override val dragToReorder: String = "Drag to reorder"
    override val checklistTitleLabel: String = "Checklist Title:"
    override val checklistEditorTitlePlaceholder: String = "e.g., Today's quests, Boss raid requirements..."
    override val checklistItemsInstructions: String = "Checklist items (Press Enter on keyboard to add next task automatically):"
    override val writeTaskPlaceholder: (Int) -> String = { num -> "Write task $num..." }
    override val deleteTaskCd: String = "Delete task"
    override val shareChecklistAction: String = "Share Tasks"
    override val shareChecklistChooserTitle: String = "Share checklist via"

    override val geminiAssistantTitle: String = "AI Game Assistant"
    override val geminiAssistantSubtitle: String = "Analyze strategies, game plans, and summarize notes"
    override val geminiInputHint: String = "Ask AI about game strategies, boss tips..."
    override val geminiNoApiKeyNotice: String = "API Key required in Settings"
    override val geminiConfigureApiKeyPrompt: String = "Please add your API key from the side menu to enable AI."
    override val geminiThinking: String = "Thinking and generating response..."
    override val geminiQuickSuggestTitle: String = "Quick Suggestions:"
    override val geminiQuickSuggestTasks: String = "Suggest steps to complete this quest"
    override val geminiQuickSummarize: String = "Summarize this note"
    override val geminiQuickTranslate: String = "Turn into an actionable strategy"
    override val geminiCopyResponse: String = "Copy Response"
    override val geminiCopiedNotice: String = "Response copied successfully"
    override val geminiNewChatSession: String = "New Chat Session"
    override val geminiClearSession: String = "Clear Chat"
    override val geminiAskAboutNote: (String) -> String = { title -> "Ask about: $title" }
    override val geminiWelcomeGreeting: (String) -> String = { game -> "Welcome hero! I am your smart game assistant for ($game). Your note content was moved to the prompt below; feel free to edit or ask before sending!" }
    override val geminiNewChatStarted: String = "Started a new conversation! How can I help you in your games today?"
    override val geminiErrorCommunicating: String = "Error communicating with AI assistant"
    override val geminiAttachedImageAnalysis: String = "🖼️ [Analyzing attached image]"
    override val geminiLinkedToNote: (String) -> String = { title -> "Linked to: $title" }
    override val geminiClearCurrentChatCd: String = "Clear current conversation"
    override val geminiClearChatConfirmTitle: String = "Clear this chat?"
    override val geminiClearChatConfirmMessage: String = "Are you sure you want to delete messages from this conversation? This cannot be undone."
    override val geminiPastSessionsTitle: String = "Past Conversations"
    override val geminiNoPastChats: String = "No saved chat history."
    override val geminiDeleteSessionCd: String = "Delete conversation"
    override val geminiYouLabel: String = "You"
    override val geminiAiLabel: String = "AI"
    override val geminiQuickQuestion1: String = "How do I do this efficiently?"
    override val geminiQuickQuestion2: String = "Where do I find these materials?"
    override val geminiQuickQuestion3: String = "What is the optimal strategy?"
    override val geminiChatSessionCreatedNotice: String = "Started a new chat session"
    override val geminiChatWithNoteTitle: (String) -> String = { title -> "Chat: $title" }
    override val geminiChatHistoryCd: String = "Chat history"
    override val geminiNewChatCd: String = "New chat"
    override val geminiReplyErrorPrefix: (String) -> String = { err -> "Sorry, failed to get a response: $err" }
    override val geminiChatClearedGreeting: String = "Chat cleared successfully! Welcome, how can I help you today?"
    override val geminiTextCopiedToInputHint: String = "💡 Text copied to the box below, you can review and edit before sending."
    override val geminiAttachImageCd: String = "Attach image for AI"
    override val geminiInputDisabledHint: String = "Enter your API key in Settings to unlock AI"
    override val geminiSendMessageCd: String = "Send message to AI"
    override val geminiRemoveImageCd: String = "Remove image"
    override val geminiAttachedImageLabel: String = "Attached image for Gemini analysis"
    override val geminiConvertToNote: String = "Convert to Note"
    override val geminiConvertToChecklist: String = "Convert to Tasks"
    override val geminiConvertedToNoteSuccess: String = "Created new note successfully"
    override val geminiConvertedToChecklistSuccess: String = "Created new checklist successfully"
    override val geminiNoteFallbackTitle: String = "AI Note"
    override val geminiTasksFallbackTitle: String = "AI Tasks"

    override val timerTitle: String = "Floating Countdown Timer"
    override val timerSubtitle: String = "Track boss spawns, dungeon timers, and game events"
    override val timerTimeUp: String = "⏰ Time's Up!"
    override val timerCounting: (String) -> String = { label -> "Counting: $label" }
    override val timerStopped: String = "Timer Stopped"
    override val timerStart: String = "Start"
    override val timerPause: String = "Pause"
    override val timerReset: String = "Reset"
    override val timerQuickPresets: String = "Quick Presets:"
    override val timerCustomMinutesHint: String = "Enter minutes..."
    override val timerSetButton: String = "Set"
    override val timerShowInOverlay: String = "Show in Floating Overlay"
    override val timerMin: String = "m"
    override val timerSec: String = "s"
    override val timerResume: String = "Resume"
    override val timerNotificationNotice: String = "An Android notification with sound will alert you when time expires even if the app is closed"
    override val timerTaskNameLabel: String = "Task or Boss Name"
    override val timerTaskNamePlaceholder: String = "e.g., Boss respawn, shield cooldown..."
    override val timerDurationMinutesLabel: String = "Duration in minutes"
    override val timerStartFullButton: String = "Start Timer"
    override val timerPresetMinutes: (Int) -> String = { mins -> if (mins == 1) "1 min" else "$mins mins" }
    override val timerDefaultLabelFormat: (Int) -> String = { mins -> "Timer $mins min" }
    override val doneButton: String = "Done"

    override val imageZoomTitle: String = "View & Edit Image"
    override val drawingMode: String = "Drawing Mode"
    override val panZoomMode: String = "Pan & Zoom Mode"
    override val undoDrawing: String = "Undo"
    override val clearDrawing: String = "Clear Drawing"
    override val saveDrawing: String = "Save Changes"
    override val brushSize: String = "Brush Size:"
    override val brushColor: String = "Brush Color:"
    override val imageSavedToStorage: String = "Modified image saved successfully"
    override val fullScreenImageCd: String = "Full screen enlarged image"
    override val drawingModeActiveNotice: String = "Drawing mode is active"
    override val drawAnnotateButton: String = "Draw / Annotate"
    override val resetImageCd: String = "Reset"
    override val noDrawingsToSave: String = "No drawings to save"
    override val imageSaveSuccess: String = "Changes to image saved successfully!"
    override val imageProcessFailed: String = "Failed to process and save image"
    override val imageZoomHelpTip: String = "Pinch with two fingers to zoom, or tap 'Draw' to annotate"

    override val overlayDockSide: String = "Switch Dock Side"
    override val overlayMinimize: String = "Minimize to Bubble"
    override val overlayExpandToPanel: String = "Expand to Full Panel"
    override val overlayQuickAddTaskHint: String = "New quick task..."
    override val overlayAddQuickTask: String = "Add Quick Task"
    override val overlayNoNotesInGame: String = "No notes for this game"
    override val overlaySwitchToGame: String = "Switch Game"
    override val overlayOpenFullApp: String = "Open in Full App"
    override val overlaySearchInOverlay: String = "Quick search..."
    override val overlayCreateNote: String = "New Note"
    override val overlayCreateChecklist: String = "New Checklist"
    override val overlayNotificationTitle: String = "GameNotes Floating Over Screen"
    override val overlayNotificationText: String = "Tap to open notes or drag down to close"
    override val overlayNotificationStopAction: String = "Stop Floating Tool"
    override val overlayNotificationChannelDesc: String = "Floating tool over games to view notes and checklists"
    override val overlayClosedToast: String = "Floating tool closed"
    override val miniWidgetClosedToast: String = "Floating checklist closed"
    override val overlayTasksTitle: String = "Tasks"
    override val overlayNoTasksCurrently: String = "No tasks right now"
    override val overlayDragToMove: String = "Drag to move widget"
    override val overlayCloseMiniWidget: String = "Close widget"
    override val overlayGamesLabel: String = "Games:"
    override val overlayChannelName: String = "GameNotes Floating Overlay"
    override val releaseToDismiss: String = "Release to dismiss"
    override val pickingImageInProgress: String = "Selecting image..."
    override val todoTasks: String = "Tasks"
    override val overlayToggleDockSide: String = "Switch side"
    override val overlayMinimizeToBubble: String = "Minimize to bubble"

    override val colorEmeraldDefault: String = "Emerald (Default)"
    override val colorNeonCyan: String = "Neon Cyan"
    override val colorGlowingGold: String = "Glowing Gold"
    override val colorCyberPurple: String = "Cyber Purple"
    override val colorPureWhite: String = "Pure White"
    override val colorIceBlue: String = "Ice Blue"
    override val colorFieryRed: String = "Fiery Red"
    override val colorLimeGreen: String = "Lime Green"
    override val colorDarkCharcoal: String = "Dark Charcoal"
    override val colorObsidianDefault: String = "Obsidian (Default)"
    override val colorAmoledPureBlack: String = "Pure AMOLED Black"
    override val colorMidnightGray: String = "Midnight Gray"
    override val colorSpaceBlue: String = "Space Blue"
    override val colorCosmicPurple: String = "Cosmic Purple"
    override val colorDeepNavy: String = "Deep Navy"
    override val colorLightGrayDay: String = "Light Gray (Day)"

    override val tapToEdit: String = "Tap to edit"
    override val twoSideBySideImagesNotice: String = "Two side-by-side images (50% / 50%)"
    override val deleteBothImages: String = "Delete both images"
    override val longPressToReorder: String = "Long press to reorder"
    override val noteImageClickToZoom: String = "Note image - tap to zoom"
    override val zoomImage: String = "Zoom image"
    override val writeTextBesideImagePlaceholder: String = "Write text beside image..."
    override val alignmentRight: String = "Alignment: Right"
    override val alignmentLeft: String = "Alignment: Left"
    override val addSideBySideImage: String = "+ Image beside"
    override val deleteImage: String = "Delete image"
    override val sizeLabel: String = "Size:"
    override val resumeWritingNormalTextHint: String = "Tap here to resume writing regular text below tasks..."
}
