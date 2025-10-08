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
    const val VIEW_ALL_ITEMS = "view_all_items"
    const val CHECKED_OUT_ITEMS = "checked_out_items"
    const val ITEM_DETAIL = "item_detail"
    const val CHECK_IN_OUT = "check_in_out"
    const val MANAGE_USERS = "manage_users"
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
                onBack = { nav.navigate(Dest.HOME) },
                onLoginSuccess = {
                    nav.navigate(Dest.DASHBOARD) {
                        launchSingleTop = true
                        popUpTo(Dest.LOGIN) { inclusive = true }
                    }
                },
                onGoToRegister = { nav.navigate(Dest.REGISTER) }
            )
        }

        composable(Dest.REGISTER) {
            RegisterScreen(
                onBack = { nav.navigate(Dest.HOME) },
                onRegistered = { nav.popBackStack(Dest.HOME, inclusive = false) },
                onGoToLogin = { nav.navigate(Dest.LOGIN) }
            )
        }
//testing
        composable(Dest.DASHBOARD) {
            val totalItems = com.example.phoenixinventory.data.DataRepository.getAllItems().size
            val checkedOut = com.example.phoenixinventory.data.DataRepository.getCheckedOutCount()
            val totalValue = com.example.phoenixinventory.data.DataRepository.getTotalValue()
            val itemsOutOver30Days = com.example.phoenixinventory.data.DataRepository.getItemsOutLongerThan(30).size
            val stolenLostDamagedValue = com.example.phoenixinventory.data.DataRepository.getStolenLostDamagedValue()
            val stolenLostDamagedCount = com.example.phoenixinventory.data.DataRepository.getStolenLostDamagedCount()
            val currentUser = com.example.phoenixinventory.data.DataRepository.getCurrentUser()

            DashboardScreen(
                userName = currentUser.name,
                email = currentUser.email,
                role = currentUser.role,
                totalItems = totalItems,
                checkedOut = checkedOut,
                totalValue = totalValue,
                itemsOutOver30Days = itemsOutOver30Days,
                stolenLostDamagedValue = stolenLostDamagedValue,
                stolenLostDamagedCount = stolenLostDamagedCount,
                onLogout = {
                    nav.navigate(Dest.HOME) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onViewAllItems = { /* TODO */ },
                onCheckedIn = { /* TODO */ },
                onCheckedOut = { /* TODO */ },
                onManageItem = { nav.navigate(Dest.ADD_ITEM) }, // ✅ goes to Add Item
                onRemoveItem = { /* TODO */ }
            )
        }

        composable(Dest.ADD_ITEM) {
            AddItemScreen(
                onBack = { nav.popBackStack() },
                onSubmit = { newItem ->
                    val item = com.example.phoenixinventory.data.InventoryItem(
                        name = newItem.name,
                        serialId = newItem.serialId,
                        description = newItem.description,
                        condition = newItem.condition,
                        status = newItem.status,
                        permanentCheckout = newItem.permanentCheckout,
                        permissionNeeded = newItem.permissionNeeded,
                        driversLicenseNeeded = newItem.driversLicenseNeeded
                    )
                    com.example.phoenixinventory.data.DataRepository.addItem(item)
                    nav.popBackStack()
                },
                onCancel = { nav.popBackStack() }
            )
        }

        composable(Dest.VIEW_ALL_ITEMS) {
            ViewAllItemsScreen(
                onBack = { nav.popBackStack() },
                onItemClick = { itemId ->
                    nav.navigate("${Dest.ITEM_DETAIL}/$itemId")
                }
            )
        }

        composable(Dest.CHECKED_OUT_ITEMS) {
            CheckedOutItemsScreen(
                onBack = { nav.popBackStack() },
                onItemClick = { itemId ->
                    nav.navigate("${Dest.ITEM_DETAIL}/$itemId")
                }
            )
        }

        composable("${Dest.ITEM_DETAIL}/{itemId}") { backStackEntry ->
            val itemId = backStackEntry.arguments?.getString("itemId") ?: return@composable
            ItemDetailScreen(
                itemId = itemId,
                onBack = { nav.popBackStack() },
                onCheckInOut = { id ->
                    nav.navigate("${Dest.CHECK_IN_OUT}/$id")
                }
            )
        }

        composable("${Dest.CHECK_IN_OUT}/{itemId}") { backStackEntry ->
            val itemId = backStackEntry.arguments?.getString("itemId") ?: return@composable
            CheckInOutScreen(
                itemId = itemId,
                onBack = { nav.popBackStack() }
            )
        }

        composable(Dest.MANAGE_USERS) {
            ManageUsersScreen(
                onBack = { nav.popBackStack() }
            )
        }
    }
}
