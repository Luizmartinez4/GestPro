package com.example.gestpro.ui

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.gestpro.GestProApplication
import com.example.gestpro.ui.login.LoginViewModel
import com.example.gestpro.ui.register.RegisterViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer { LoginViewModel(gestProApplication().userRepository) }
        initializer { RegisterViewModel(gestProApplication().userRepository) }
    }
}

fun CreationExtras.gestProApplication(): GestProApplication =
    this[APPLICATION_KEY] as GestProApplication