package com.spinel.gamenotes.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatSessionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: ChatSession): Long

    @Update
    suspend fun updateSession(session: ChatSession)

    @Query("SELECT * FROM chat_sessions ORDER BY updatedAt DESC")
    fun getAllSessionsFlow(): Flow<List<ChatSession>>

    @Query("SELECT * FROM chat_sessions ORDER BY updatedAt DESC")
    suspend fun getAllSessionsDirect(): List<ChatSession>

    @Query("SELECT * FROM chat_sessions WHERE id = :sessionId LIMIT 1")
    suspend fun getSessionById(sessionId: Long): ChatSession?

    @Query("SELECT * FROM chat_sessions ORDER BY updatedAt DESC LIMIT 1")
    suspend fun getLatestSession(): ChatSession?

    @Delete
    suspend fun deleteSession(session: ChatSession)

    @Query("DELETE FROM chat_sessions WHERE id = :sessionId")
    suspend fun deleteSessionById(sessionId: Long)

    @Query("DELETE FROM chat_sessions")
    suspend fun clearAllSessions()
}
