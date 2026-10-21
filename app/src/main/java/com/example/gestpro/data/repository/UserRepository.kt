package com.example.gestpro.data.repository

import android.database.sqlite.SQLiteConstraintException
import com.example.gestpro.data.email.EmailSender
import com.example.gestpro.data.local.PasswordResetDao
import com.example.gestpro.data.local.PasswordResetTokenEntity
import com.example.gestpro.data.local.UserDao
import com.example.gestpro.data.local.UserEntity
import com.example.gestpro.util.HashUtils

class UserRepository(
    private val userDao: UserDao,
    private val resetDao: PasswordResetDao,
    private val emailSender: EmailSender
) {
    private companion object {
        const val RESET_LINK_BASE = "https://Luizmartinez4.github.io/gestpro-reset/"
        const val TOKEN_VALIDITY_MS = 30 * 60 * 1000L   // 30 minutos
    }

    private fun normalize(email: String) = email.trim().lowercase()

    suspend fun register(email: String, password: String): Result<Unit> =
        try {
            userDao.insert(
                UserEntity(username = normalize(email), passwordHash = HashUtils.sha256(password))
            )
            Result.success(Unit)
        } catch (e: SQLiteConstraintException) {
            Result.failure(Exception("Este email já está cadastrado"))
        } catch (e: Exception) {
            Result.failure(Exception("Erro ao cadastrar usuário"))
        }

    suspend fun login(email: String, password: String): Result<UserEntity> {
        val user = userDao.findByUsername(normalize(email))
        return if (user != null && user.passwordHash == HashUtils.sha256(password)) {
            Result.success(user)
        } else {
            Result.failure(Exception("Usuário ou senha inválido"))
        }
    }

    /** Só envia o email se o endereço estiver cadastrado. */
    suspend fun requestPasswordReset(email: String): Result<Unit> {
        val user = userDao.findByUsername(normalize(email))
            ?: return Result.failure(Exception("Este email não está cadastrado"))

        val token = HashUtils.generateToken()
        resetDao.deleteByUser(user.id)   // invalida links anteriores
        resetDao.insert(
            PasswordResetTokenEntity(
                userId = user.id,
                tokenHash = HashUtils.sha256(token),   // só o hash é guardado
                expiresAt = System.currentTimeMillis() + TOKEN_VALIDITY_MS
            )
        )

        return try {
            emailSender.sendPasswordReset(user.username, "$RESET_LINK_BASE?token=$token")
            Result.success(Unit)
        } catch (e: Exception) {
            resetDao.deleteByUser(user.id)
            Result.failure(Exception("Não foi possível enviar o email. Tente novamente"))
        }
    }

    suspend fun resetPassword(token: String, newPassword: String): Result<Unit> {
        val record = resetDao.findByTokenHash(HashUtils.sha256(token))
            ?: return Result.failure(Exception("Link inválido"))

        if (record.used || record.expiresAt < System.currentTimeMillis()) {
            return Result.failure(Exception("Link expirado ou já utilizado"))
        }

        userDao.updatePasswordHash(record.userId, HashUtils.sha256(newPassword))
        resetDao.markUsed(record.id)
        return Result.success(Unit)
    }
}