package com.spinel.gamenotes.ai

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GenerateContentRequest(
    val contents: List<Content>,
    val systemInstruction: Content? = null
)

@JsonClass(generateAdapter = true)
data class Content(
    val parts: List<Part>,
    val role: String? = null
)

@JsonClass(generateAdapter = true)
data class Part(
    val text: String? = null,
    val inline_data: InlineData? = null
)

@JsonClass(generateAdapter = true)
data class InlineData(
    val mime_type: String,
    val data: String
)

@JsonClass(generateAdapter = true)
data class GenerateContentResponse(
    val candidates: List<Candidate>? = null
)

@JsonClass(generateAdapter = true)
data class Candidate(
    val content: Content? = null
)

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val sender: Sender,
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val imageUriString: String? = null
) {
    enum class Sender { USER, GEMINI }
}
