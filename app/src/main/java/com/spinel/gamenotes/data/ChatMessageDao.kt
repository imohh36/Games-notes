package com.spinel.gamenotes.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatMessageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessageEntity): Long

    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
    fun getAllMessagesFlow(): Flow<List<ChatMessageEntity>>

    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
    suspend fun getAllMessagesDirect(): List<ChatMessageEntity>

    @Query("SELECT * FROM chat_messages WHERE chatId = :chatId ORDER BY timestamp ASC")
    fun getMessagesByChatIdFlow(chatId: Long): Flow<List<ChatMessageEntity>>

    @Query("SELECT * FROM chat_messages WHERE chatId = :chatId ORDER BY timestamp ASC")
    suspend fun getMessagesByChatIdDirect(chatId: Long): List<ChatMessageEntity>

    @Query("DELETE FROM chat_messages WHERE chatId = :chatId")
    suspend fun deleteMessagesByChatId(chatId: Long)

    @Query("DELETE FROM chat_messages")
    suspend fun clearAllMessages()
}
