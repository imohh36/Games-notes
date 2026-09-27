package com.spinel.gamenotes.util

import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Process
import android.util.Log
import com.spinel.gamenotes.BuildConfig
import com.spinel.gamenotes.ui.CrashReportActivity
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.system.exitProcess

object CrashHandler {
    private const val TAG = "CrashHandler"
    const val CRASH_FILE_NAME = "latest_crash.txt"
    const val EXTRA_CRASH_REPORT = "extra_crash_report"

    fun init(application: Application) {
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()

        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            try {
                val report = generateReport(application, thread, throwable)
                saveCrashToFile(application, report)
                launchCrashActivity(application, report)
            } catch (t: Throwable) {
                Log.e(TAG, "Failed in custom crash handler", t)
            } finally {
                // Terminate crashed process
                defaultHandler?.uncaughtException(thread, throwable) ?: run {
                    Process.killProcess(Process.myPid())
                    exitProcess(10)
                }
            }
        }
    }

    fun recordNonFatal(context: Context, throwable: Throwable) {
        try {
            val report = generateReport(context, Thread.currentThread(), throwable, isFatal = false)
            saveCrashToFile(context, report)
        } catch (t: Throwable) {
            Log.e(TAG, "Failed to record non-fatal error", t)
        }
    }

    private fun generateReport(context: Context, thread: Thread, throwable: Throwable, isFatal: Boolean = true): String {
        val stringWriter = StringWriter()
        throwable.printStackTrace(PrintWriter(stringWriter))
        val stackTrace = stringWriter.toString()

        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
        val currentTime = sdf.format(Date())

        return buildString {
            appendLine("=== تقرير خطأ التطبيق (${if (isFatal) "Fatal Crash" else "Non-Fatal"}) ===")
            appendLine("الوقت: $currentTime")
            appendLine("الإصدار: ${BuildConfig.VERSION_NAME} (Code: ${BuildConfig.VERSION_CODE})")
            appendLine("الجهاز: ${Build.MANUFACTURER} ${Build.MODEL}")
            appendLine("نظام أندرويد: Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})")
            appendLine("اسم الخيط (Thread): ${thread.name} (id: ${thread.id})")
            appendLine("--------------------------------------------------")
            appendLine("نوع الخطأ: ${throwable.javaClass.name}")
            appendLine("الرسالة: ${throwable.message ?: "لا توجد رسالة مرفقة"}")
            appendLine("--------------------------------------------------")
            appendLine("تفاصيل المكدس (Stacktrace):")
            appendLine(stackTrace)
            appendLine("==================================================")
        }
    }

    private fun saveCrashToFile(context: Context, report: String) {
        try {
            val file = File(context.filesDir, CRASH_FILE_NAME)
            file.writeText(report)
            Log.e(TAG, "Crash report saved to ${file.absolutePath}")
        } catch (e: Exception) {
            Log.e(TAG, "Error saving crash report to file", e)
        }
    }

    fun getSavedCrashReport(context: Context): String? {
        return try {
            val file = File(context.filesDir, CRASH_FILE_NAME)
            if (file.exists() && file.length() > 0) file.readText() else null
        } catch (e: Exception) {
            null
        }
    }

    fun clearSavedCrashReport(context: Context) {
        try {
            val file = File(context.filesDir, CRASH_FILE_NAME)
            if (file.exists()) file.delete()
        } catch (_: Exception) {}
    }

    private fun launchCrashActivity(context: Context, report: String) {
        try {
            val intent = Intent(context, CrashReportActivity::class.java).apply {
                putExtra(EXTRA_CRASH_REPORT, report)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start CrashReportActivity", e)
        }
    }
}
