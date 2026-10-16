package com.example.gestpro.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface PasswordResetDao {

    @Insert
    suspend fun insert(token: PasswordResetTokenEntity): Long

    @Query("SELECT * FROM password_reset_tokens WHERE tokenHash = :tokenHash LIMIT 1")
    suspend fun findByTokenHash(tokenHash: String): PasswordResetTokenEntity?

    @Query("UPDATE password_reset_tokens SET used = 1 WHERE id = :id")
    suspend fun markUsed(id: Long)

    @Query("DELETE FROM password_reset_tokens WHERE userId = :userId")
    suspend fun deleteByUser(userId: Long)
}