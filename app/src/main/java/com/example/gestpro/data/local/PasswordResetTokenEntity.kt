
package com.example.gestpro.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "password_reset_tokens",
    indices = [Index(value = ["tokenHash"], unique = true)]
)
data class PasswordResetTokenEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val tokenHash: String,
    val expiresAt: Long,
    val used: Boolean = false
)
