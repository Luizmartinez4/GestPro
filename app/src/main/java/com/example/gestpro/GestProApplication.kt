package com.example.gestpro

import android.app.Application
import com.example.gestpro.data.email.EmailSender
import com.example.gestpro.data.local.AppDatabase
import com.example.gestpro.data.repository.UserRepository

class GestProApplication : Application() {

    val userRepository: UserRepository by lazy {
        val db = AppDatabase.getInstance(this)
        UserRepository(db.userDao(), db.passwordResetDao(), EmailSender())
    }
}