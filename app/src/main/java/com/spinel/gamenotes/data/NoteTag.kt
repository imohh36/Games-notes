package com.spinel.gamenotes.data

import java.util.UUID

data class NoteTag(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val colorHex: String = "#10B981"
)
