package com.example.gestpro.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.example.gestpro.ui.employees.EmployeeDetailScreen
import com.example.gestpro.ui.employees.EmployeeFilterScreen
import com.example.gestpro.ui.employees.EmployeeFormScreen
import com.example.gestpro.ui.forgotpassword.ForgotPasswordScreen
import com.example.gestpro.ui.home.HomeScreen
import com.example.gestpro.ui.login.LoginScreen
import com.example.gestpro.ui.register.RegisterScreen
import com.example.gestpro.ui.resetpassword.ResetPasswordScreen
import com.example.gestpro.ui.talhoes.InputFormScreen
import com.example.gestpro.ui.talhoes.PlotFormScreen
import com.example.gestpro.ui.talhoes.PlotInputsScreen

object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val HOME = "home"
    const val FORGOT_PASSWORD = "forgot_password"
    const val RESET_PASSWORD = "reset_password?token={token}"

    const val PLOT_INPUTS = "talhao/{plotId}"
    const val PLOT_FORM = "talhao_form?plotId={plotId}"
    const val INPUT_FORM = "talhao/{plotId}/lancamento/{inputId}"

    fun plotInputs(plotId: Long) = "talhao/$plotId"
    fun plotForm(plotId: Long = 0L) = "talhao_form?plotId=$plotId"
    fun inputForm(plotId: Long, inputId: Long = 0L) = "talhao/$plotId/lancamento/$inputId"
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.LOGIN) {
        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToRegister = { navController.navigate(Routes.REGISTER) },
                onForgotPassword = { navController.navigate(Routes.FORGOT_PASSWORD) }
            )
        }
        composable(Routes.REGISTER) {
            RegisterScreen(
                onRegisterSuccess = { navController.popBackStack() },
                onBack = { navController.popBackStack() }
            )
        }
        composable(Routes.FORGOT_PASSWORD) {
            ForgotPasswordScreen(onBack = { navController.popBackStack() })
        }
        composable(
            route = Routes.RESET_PASSWORD,
            arguments = listOf(navArgument("token") {
                type = NavType.StringType
                defaultValue = ""
            }),
            deepLinks = listOf(navDeepLink { uriPattern = "gestpro://reset?token={token}" })
        ) {
            ResetPasswordScreen(
                onSuccess = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                }
            )
        }
        composable(
            "employee_detail/{employeeId}",
            arguments = listOf(navArgument("employeeId") { type = NavType.LongType })
        ) {
            EmployeeDetailScreen(
                onBack = { navController.popBackStack() },
                onEdit = { id -> navController.navigate("employee_form?employeeId=$id") }
            )
        }
        composable(
            "employee_form?employeeId={employeeId}",
            arguments = listOf(navArgument("employeeId") { type = NavType.LongType; defaultValue = 0L })
        ) {
            EmployeeFormScreen(onBack = { navController.popBackStack() })
        }
        composable("employee_filter") {
            EmployeeFilterScreen(onBack = { navController.popBackStack() })
        }
        composable(Routes.HOME) { HomeScreen(
            onNewEmployee = { navController.navigate("employee_form") },
            onOpenEmployee = { id -> navController.navigate("employee_detail/$id") },
            onOpenEmployeeFilter = { navController.navigate("employee_filter") },
            onOpenPlot = { id -> navController.navigate(Routes.plotInputs(id)) },
            onNewPlot = { navController.navigate(Routes.plotForm()) }
        ) }
        composable(
            Routes.PLOT_INPUTS,
            arguments = listOf(navArgument("plotId") { type = NavType.LongType })
        ) {
            PlotInputsScreen(
                onBack = { navController.popBackStack() },
                onEditPlot = { id -> navController.navigate(Routes.plotForm(id)) },
                onNewInput = { id -> navController.navigate(Routes.inputForm(id)) },
                onEditInput = { plotId, inputId -> navController.navigate(Routes.inputForm(plotId, inputId)) }
            )
        }
        composable(
            Routes.PLOT_FORM,
            arguments = listOf(navArgument("plotId") { type = NavType.LongType; defaultValue = 0L })
        ) {
            PlotFormScreen(
                onBack = { navController.popBackStack() },
                onSavedOpenNewInput = { id ->
                    navController.navigate(Routes.plotInputs(id)) { popUpTo(Routes.HOME) }
                    navController.navigate(Routes.inputForm(id))
                },
                onDeleted = { navController.popBackStack(Routes.HOME, inclusive = false) }
            )
        }
        composable(
            Routes.INPUT_FORM,
            arguments = listOf(
                navArgument("plotId") { type = NavType.LongType },
                navArgument("inputId") { type = NavType.LongType }
            )
        ) {
            InputFormScreen(onBack = { navController.popBackStack() })
        }
    }
}