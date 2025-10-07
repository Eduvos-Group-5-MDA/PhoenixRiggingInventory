package com.example.phoenixinventory.core

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.phoenixinventory.core.HomeScreen

object Dest {
    const val HOME = "home"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val DASHBOARD = "dashboard"
    const val ADD_ITEM = "add_item"
}

@Composable
fun AppNavHost() {
    val nav = rememberNavController()
    NavHost(navController = nav, startDestination = Dest.HOME) {

        composable(Dest.HOME) {
            HomeScreen(
                onLogin = { nav.navigate(Dest.LOGIN) },
                onRegister = { nav.navigate(Dest.REGISTER) }
            )
        }


        composable(Dest.LOGIN) {
            LoginScreen(
                onBack = { nav.popBackStack() },
                onLoginSuccess = {
                    // GO TO DASHBOARD (do NOT pop back to HOME)
                    nav.navigate(Dest.DASHBOARD) {
                        launchSingleTop = true
                        // optional: remove LOGIN from back stack so Back goes to HOME
                        popUpTo(Dest.LOGIN) { inclusive = true }
                    }
                },
                onGoToRegister = { nav.navigate(Dest.REGISTER) }
            )
        }

        composable(Dest.REGISTER) {
            RegisterScreen(
                onBack = { nav.popBackStack() },
                onRegistered = { nav.popBackStack(Dest.HOME, inclusive = false) },
                onGoToLogin = { nav.navigate(Dest.LOGIN) }
            )
        }
//hi
        composable(Dest.DASHBOARD) {
            DashboardScreen(
                userName = "John Doe",
                email = "stadlerkieran@gmail.com",
                role = "Employee",
                totalItems = 3,
                checkedOut = 2,
                onLogout = {
                    nav.navigate(Dest.HOME) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onViewAllItems = { /* TODO */ },
                onCheckedInOut = { /* TODO */ },
                onAddItem = { nav.navigate(Dest.ADD_ITEM) }, // ✅ goes to Add Item
                onRemoveItem = { /* TODO */ }
            )
        }

        composable(Dest.ADD_ITEM) {
            AddItemScreen(
                onBack = { nav.popBackStack() },
                onSubmit = { /* TODO save to DB later */ nav.popBackStack() },
                onCancel = { nav.popBackStack() }
            )
        }
    }
}
