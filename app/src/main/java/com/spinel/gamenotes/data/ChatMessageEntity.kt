package com.spinel.gamenotes.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "chat_messages",
    indices = [Index(value = ["chatId"])]
)
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val chatId: Long = 0L,
    val sender: String, // "USER" or "MODEL"
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)
