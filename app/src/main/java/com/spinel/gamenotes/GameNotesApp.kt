package com.spinel.gamenotes

import android.app.Application
import android.util.Log
import com.spinel.gamenotes.data.AppDatabase
import com.spinel.gamenotes.data.GameNotesRepository
import com.spinel.gamenotes.util.CrashHandler

class GameNotesApp : Application() {
    @Volatile
    private var _database: AppDatabase? = null
    val database: AppDatabase
        get() = _database ?: synchronized(this) {
            _database ?: AppDatabase.getDatabase(this).also { _database = it }
        }

    @Volatile
    private var _repository: GameNotesRepository? = null
    val repository: GameNotesRepository
        get() = _repository ?: synchronized(this) {
            _repository ?: GameNotesRepository(
                dao = database.gameNoteDao(),
                tabDao = database.gameTabDao(),
                chatDao = database.chatMessageDao(),
                sessionDao = database.chatSessionDao()
            ).also { _repository = it }
        }

    fun reloadDatabase() {
        synchronized(this) {
            val newDb = AppDatabase.getDatabase(this)
            _database = newDb
            _repository = GameNotesRepository(
                dao = newDb.gameNoteDao(),
                tabDao = newDb.gameTabDao(),
                chatDao = newDb.chatMessageDao(),
                sessionDao = newDb.chatSessionDao()
            )
        }
    }

    override fun onCreate() {
        super.onCreate()
        // 1. Initialize global crash handler immediately
        CrashHandler.init(this)
        instance = this

        // 2. Initialize application preferences safely
        try {
            com.spinel.gamenotes.util.LanguagePreferences.init(this)
        } catch (t: Throwable) {
            Log.e("GameNotesApp", "Error initializing LanguagePreferences", t)
            CrashHandler.recordNonFatal(this, t)
        }

        try {
            com.spinel.gamenotes.ui.theme.ThemePreferences.init(this)
        } catch (t: Throwable) {
            Log.e("GameNotesApp", "Error initializing ThemePreferences", t)
            CrashHandler.recordNonFatal(this, t)
        }

        try {
            com.spinel.gamenotes.data.AppSettingsPreferences.init(this)
        } catch (t: Throwable) {
            Log.e("GameNotesApp", "Error initializing AppSettingsPreferences", t)
            CrashHandler.recordNonFatal(this, t)
        }

        // 3. Initialize AdMob safely only if enabled
        if (com.spinel.gamenotes.BuildConfig.ENABLE_ADS) {
            try {
                com.spinel.gamenotes.ads.AdManager.init(this)
            } catch (t: Throwable) {
                Log.e("GameNotesApp", "Error initializing AdManager", t)
                CrashHandler.recordNonFatal(this, t)
            }
        }
    }

    companion object {
        lateinit var instance: GameNotesApp
            private set
    }
}
