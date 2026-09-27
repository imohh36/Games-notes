package com.spinel.gamenotes.ai

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import com.spinel.gamenotes.data.AiModelOption
import com.spinel.gamenotes.data.AppSettingsPreferences
import com.spinel.gamenotes.util.AppLanguage
import com.spinel.gamenotes.util.LanguagePreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

class GeminiRepository {
    private val systemPrompt = """
        أنت المساعد الذكي المخصص للاعبين في تطبيق GameNotes.
        تخصصك هو مساعدة اللاعبين في ألعاب الفيديو مثل:
        - Stardew Valley (مواعيد الزراعة والمواسم، هدايا القرويين، مهام المجتمع Community Center، وصفات الطبخ، الدفيئة).
        - Minecraft (وصفات الكرافتينج، بوابات النذر، أفضل ارتفاعات تعدين الماس Y=-58، التحصينات وتهيئة الإكسير).
        - وألعاب المغامرات وتقمص الأدوار (RPG).
        
        تعليمات الإجابة:
        1. كن موجزاً ودقيقاً ومركزاً على الحل المباشر للاعب حتى لا تشتت انتباهه أثناء اللعب.
        2. استخدم نقاطاً واضحة وجداول مبسطة عند الحاجة.
        3. أجب بنفس لغة سؤال المستخدم (العربية أو الإنجليزية).
    """.trimIndent()

    suspend fun askGemini(
        prompt: String,
        previousChat: List<ChatMessage> = emptyList(),
        modelOption: AiModelOption? = null,
        context: Context? = null,
        imageUriString: String? = null
    ): Result<String> = withContext(Dispatchers.IO) {
        val apiKey = GeminiClient.getApiKey()
        val isAr = LanguagePreferences.currentLanguage.value == AppLanguage.ARABIC
        if (apiKey.isBlank()) {
            return@withContext Result.failure(
                IllegalStateException(
                    if (isAr) "ميزات الذكاء الاصطناعي مقفلة. يرجى إدخال مفتاح Gemini API الخاص بك في الإعدادات."
                    else "AI features are locked. Please enter your Gemini API key in Settings."
                )
            )
        }

        try {
            val contentList = mutableListOf<Content>()
            
            // Add up to 6 recent messages for conversational context
            val recentMessages = previousChat.takeLast(6)
            for (msg in recentMessages) {
                val role = if (msg.sender == ChatMessage.Sender.USER) "user" else "model"
                contentList.add(Content(parts = listOf(Part(text = msg.text)), role = role))
            }

            // Prepare current user turn with text and optional multimodal image
            val userParts = mutableListOf<Part>()
            if (!imageUriString.isNullOrBlank() && context != null) {
                val base64Data = convertUriToBase64(context, imageUriString)
                if (base64Data != null) {
                    userParts.add(
                        Part(
                            inline_data = InlineData(
                                mime_type = "image/jpeg",
                                data = base64Data
                            )
                        )
                    )
                }
            }
            if (prompt.isNotBlank()) {
                userParts.add(Part(text = prompt))
            } else if (userParts.isNotEmpty()) {
                // If only image was sent without text, add default prompt
                userParts.add(
                    Part(
                        text = if (isAr) "حلل هذه الصورة وقدم لي نصائح أو معلومات تفيدني كلاعب عنها."
                        else "Analyze this image and provide helpful gaming tips and strategies about it."
                    )
                )
            }

            if (userParts.isNotEmpty()) {
                contentList.add(Content(parts = userParts, role = "user"))
            }

            val request = GenerateContentRequest(
                contents = contentList,
                systemInstruction = Content(parts = listOf(Part(text = systemPrompt)))
            )

            val activeOption = modelOption ?: AppSettingsPreferences.aiModel.value
            val primaryModel = activeOption.apiModelName

            val response = try {
                GeminiClient.service.generateContent(primaryModel, apiKey, request)
            } catch (e: Exception) {
                // Fallback attempt with standard model in case of preview version availability
                when (activeOption) {
                    AiModelOption.GEMINI_PRO_3_1 -> {
                        try {
                            GeminiClient.service.generateContent("gemini-2.5-pro", apiKey, request)
                        } catch (e2: Exception) {
                            GeminiClient.service.generateContent("gemini-3.5-flash", apiKey, request)
                        }
                    }
                    AiModelOption.GEMINI_FLASH_LITE_3_8 -> {
                        try {
                            GeminiClient.service.generateContent("gemini-2.5-flash", apiKey, request)
                        } catch (e2: Exception) {
                            GeminiClient.service.generateContent("gemini-3.5-flash", apiKey, request)
                        }
                    }
                    AiModelOption.GEMINI_FLASH_3_5 -> {
                        try {
                            GeminiClient.service.generateContent("gemini-2.5-flash", apiKey, request)
                        } catch (e2: Exception) {
                            throw e
                        }
                    }
                }
            }
            val reply = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: if (isAr) "لم يتم استلام رد من النموذج." else "No response received from the model."

            Result.success(reply)
        } catch (e: retrofit2.HttpException) {
            val code = e.code()
            val friendlyMsg = when (code) {
                429 -> if (isAr) "انتهت الحصة المجانية المسموحة بها أو تم تجاوز معدل الطلبات" else "Free quota exceeded or rate limit reached"
                400, 403 -> if (isAr) "عذراً، مفتاح API المدخل غير صالح" else "Sorry, the provided API key is invalid"
                404 -> if (isAr) "النموذج غير متوفر حالياً لهذا المفتاح (HTTP 404)." else "Model currently unavailable for this key (HTTP 404)."
                else -> if (isAr) "خطأ في الاتصال بـ Gemini (رمز: $code)." else "Error connecting to Gemini (code: $code)."
            }
            Result.failure(Exception(friendlyMsg, e))
        } catch (e: Exception) {
            val msg = e.message ?: ""
            val friendlyMsg = when {
                msg.contains("429") || msg.contains("quota", ignoreCase = true) || msg.contains("resource_exhausted", ignoreCase = true) ->
                    if (isAr) "انتهت الحصة المجانية المسموحة بها" else "Free quota exceeded"
                msg.contains("400") || msg.contains("API_KEY_INVALID", ignoreCase = true) || msg.contains("403") ->
                    if (isAr) "عذراً، مفتاح API المدخل غير صالح" else "Sorry, the provided API key is invalid"
                else -> msg.ifBlank { if (isAr) "حدث خطأ غير متوقع أثناء الاتصال بالذكاء الاصطناعي." else "An unexpected error occurred while contacting AI." }
            }
            Result.failure(Exception(friendlyMsg, e))
        }
    }

    /**
     * Sanitizes raw API key input by trimming whitespace, invisible characters, and quotes.
     */
    fun sanitizeApiKey(raw: String): String {
        return raw.trim()
            .replace("\u200B", "") // Zero-width space
            .replace("\uFEFF", "") // BOM
            .replace("\u00A0", " ") // Non-breaking space
            .replace("\"", "")
            .replace("'", "")
            .replace("\n", "")
            .replace("\r", "")
            .trim()
    }

    /**
     * Sends a minimal ping request to test the API key before saving.
     */
    suspend fun pingApiKey(apiKey: String): Result<Boolean> = withContext(Dispatchers.IO) {
        val cleanKey = sanitizeApiKey(apiKey)
        val isAr = LanguagePreferences.currentLanguage.value == AppLanguage.ARABIC
        if (cleanKey.isBlank()) {
            return@withContext Result.failure(IllegalArgumentException(if (isAr) "يرجى إدخال مفتاح API" else "Please enter an API key"))
        }
        if (cleanKey.length < 20) {
            return@withContext Result.failure(IllegalArgumentException(if (isAr) "مفتاح API غير صالح (قصير جداً)" else "Invalid API key (too short)"))
        }
        // General sanity check for API key characters (alphanumeric, dash, underscore)
        if (!cleanKey.matches(Regex("^[a-zA-Z0-9_\\-]{20,80}$"))) {
            return@withContext Result.failure(IllegalArgumentException(if (isAr) "صيغة المفتاح غير صالحة. تأكد من خلوه من الرموز الخاصة." else "Invalid key format. Ensure no special characters."))
        }

        try {
            val testRequest = GenerateContentRequest(
                contents = listOf(Content(parts = listOf(Part(text = "ping")), role = "user"))
            )
            // Test with standard stable Gemini models
            try {
                GeminiClient.service.generateContent("gemini-2.5-flash", cleanKey, testRequest)
            } catch (e: retrofit2.HttpException) {
                if (e.code() == 404 || e.code() == 400) {
                    val body = try { e.response()?.errorBody()?.string() ?: "" } catch (_: Exception) { "" }
                    if (body.contains("API_KEY_INVALID", ignoreCase = true) || body.contains("API key not valid", ignoreCase = true)) {
                        throw e
                    }
                    // Try fallback model
                    GeminiClient.service.generateContent("gemini-1.5-flash", cleanKey, testRequest)
                } else {
                    throw e
                }
            }
            Result.success(true)
        } catch (e: retrofit2.HttpException) {
            val code = e.code()
            val errorBody = try { e.response()?.errorBody()?.string() ?: "" } catch (_: Exception) { "" }
            val errorMsg = when {
                code == 429 || errorBody.contains("RESOURCE_EXHAUSTED", ignoreCase = true) ->
                    if (isAr) "انتهت الحصة المجانية المسموحة بها أو تم تجاوز معدل الطلبات" else "Free quota exceeded or rate limit reached"
                errorBody.contains("API_KEY_INVALID", ignoreCase = true) || errorBody.contains("API key not valid", ignoreCase = true) || code == 403 ->
                    if (isAr) "عذراً، مفتاح API المدخل غير صالح" else "Sorry, the provided API key is invalid"
                code == 400 ->
                    if (errorBody.contains("key", ignoreCase = true)) {
                        if (isAr) "عذراً، مفتاح API المدخل غير صالح" else "Sorry, the provided API key is invalid"
                    } else {
                        if (isAr) "طلب غير صالح من الخادم (رمز 400)" else "Bad request from server (code 400)"
                    }
                else -> if (isAr) "فشل التحقق من المفتاح (رمز: $code)." else "Failed to verify key (code: $code)."
            }
            Result.failure(Exception(errorMsg, e))
        } catch (e: Exception) {
            val msg = e.message ?: ""
            val errorMsg = when {
                msg.contains("429") || msg.contains("quota", ignoreCase = true) || msg.contains("resource_exhausted", ignoreCase = true) ->
                    if (isAr) "انتهت الحصة المجانية المسموحة بها" else "Free quota exceeded"
                msg.contains("API_KEY_INVALID", ignoreCase = true) || msg.contains("API key not valid", ignoreCase = true) ->
                    if (isAr) "عذراً، مفتاح API المدخل غير صالح" else "Sorry, the provided API key is invalid"
                else -> if (isAr) "تعذر الاتصال للتحقق من المفتاح: ${msg.ifBlank { "تأكد من اتصال الإنترنت وصحة المفتاح" }}"
                        else "Could not connect to verify key: ${msg.ifBlank { "Check your internet connection and key" }}"
            }
            Result.failure(Exception(errorMsg, e))
        }
    }

    /**
     * Reads an image URI, resizes if too large, and converts to Base64 string for Gemini API.
     */
    private fun convertUriToBase64(context: Context, uriString: String): String? {
        return try {
            val uri = Uri.parse(uriString)
            val inputStream = when {
                uriString.startsWith("file://") -> {
                    val path = uri.path
                    if (path != null) java.io.FileInputStream(java.io.File(path)) else null
                }
                else -> context.contentResolver.openInputStream(uri)
            } ?: return null

            // Decode image with downsampling to avoid memory/payload limits
            val originalBitmap = BitmapFactory.decodeStream(inputStream)
            inputStream.close()
            if (originalBitmap == null) return null

            val maxDimension = 1024
            val scaledBitmap = if (originalBitmap.width > maxDimension || originalBitmap.height > maxDimension) {
                val ratio = originalBitmap.width.toFloat() / originalBitmap.height.toFloat()
                val targetWidth: Int
                val targetHeight: Int
                if (ratio > 1) {
                    targetWidth = maxDimension
                    targetHeight = (maxDimension / ratio).toInt()
                } else {
                    targetHeight = maxDimension
                    targetWidth = (maxDimension * ratio).toInt()
                }
                Bitmap.createScaledBitmap(originalBitmap, targetWidth, targetHeight, true)
            } else {
                originalBitmap
            }

            val outputStream = ByteArrayOutputStream()
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
            val byteArray = outputStream.toByteArray()
            Base64.encodeToString(byteArray, Base64.NO_WRAP)
        } catch (e: Exception) {
            null
        }
    }

    companion object {
        fun sanitizeApiKey(raw: String): String = GeminiRepository().sanitizeApiKey(raw)
        suspend fun pingApiKey(apiKey: String): Result<Boolean> = GeminiRepository().pingApiKey(apiKey)
    }
}
