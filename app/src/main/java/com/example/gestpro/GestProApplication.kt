package com.example.gestpro

import android.app.Application
import com.example.gestpro.data.email.EmailSender
import com.example.gestpro.data.local.AppDatabase
import com.example.gestpro.data.repository.FarmRepository
import com.example.gestpro.data.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class GestProApplication : Application() {

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val database: AppDatabase by lazy { AppDatabase.getInstance(this) }

    val userRepository: UserRepository by lazy {
        UserRepository(database.userDao(), database.passwordResetDao(), EmailSender())
    }

    val farmRepository: FarmRepository by lazy { FarmRepository(database) }

    override fun onCreate() {
        super.onCreate()
        // Garante os dados fixos na primeira execução (e se o banco for limpo)
        appScope.launch { farmRepository.seedIfEmpty() }
    }
}
