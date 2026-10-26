package com.example.gestpro.ui

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.gestpro.GestProApplication
import com.example.gestpro.ui.employees.EmployeeDetailViewModel
import com.example.gestpro.ui.employees.EmployeeFilterViewModel
import com.example.gestpro.ui.employees.EmployeeFormViewModel
import com.example.gestpro.ui.employees.EmployeeListViewModel
import com.example.gestpro.ui.forgotpassword.ForgotPasswordViewModel
import com.example.gestpro.ui.home.HomeViewModel
import com.example.gestpro.ui.login.LoginViewModel
import com.example.gestpro.ui.register.RegisterViewModel
import com.example.gestpro.ui.resetpassword.ResetPasswordViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer { LoginViewModel(gestProApplication().userRepository) }
        initializer { RegisterViewModel(gestProApplication().userRepository) }
        initializer { ForgotPasswordViewModel(gestProApplication().userRepository) }
        initializer {
            ResetPasswordViewModel(createSavedStateHandle(), gestProApplication().userRepository)
        }
        initializer { HomeViewModel(gestProApplication().farmRepository) }
        initializer {
            val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as GestProApplication
            EmployeeListViewModel(app.employeeRepository, app.employeeFilterStore)
        }
        initializer {
            val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as GestProApplication
            EmployeeDetailViewModel(this.createSavedStateHandle(), app.employeeRepository)
        }
        initializer {
            val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as GestProApplication
            EmployeeFormViewModel(this.createSavedStateHandle(), app.employeeRepository)
        }
        initializer {
            val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as GestProApplication
            EmployeeFilterViewModel(app.employeeFilterStore)
        }
    }
}

fun CreationExtras.gestProApplication(): GestProApplication =
    this[APPLICATION_KEY] as GestProApplication
