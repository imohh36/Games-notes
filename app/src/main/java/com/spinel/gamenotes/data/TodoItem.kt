package com.spinel.gamenotes.data

import java.util.UUID

data class TodoItem(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val isDone: Boolean = false
)
