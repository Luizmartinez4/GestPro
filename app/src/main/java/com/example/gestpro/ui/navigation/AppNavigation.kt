package com.example.gestpro.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.example.gestpro.ui.forgotpassword.ForgotPasswordScreen
import com.example.gestpro.ui.home.HomeScreen
import com.example.gestpro.ui.login.LoginScreen
import com.example.gestpro.ui.register.RegisterScreen
import com.example.gestpro.ui.resetpassword.ResetPasswordScreen

object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val HOME = "home"
    const val FORGOT_PASSWORD = "forgot_password"
    const val RESET_PASSWORD = "reset_password?token={token}"
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
        composable(Routes.HOME) { HomeScreen() }
    }
}