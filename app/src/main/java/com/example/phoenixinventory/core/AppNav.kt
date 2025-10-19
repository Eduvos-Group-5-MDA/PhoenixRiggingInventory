package com.example.phoenixinventory.core

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.phoenixinventory.data.InventoryItem
import com.example.phoenixinventory.data.DataRepository
import kotlinx.coroutines.launch

object Dest {
    const val HOME = "home"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val FORGOT_PASSWORD = "forgot_password"
    const val DASHBOARD = "dashboard"
    const val ADD_ITEM = "add_item"
    const val VIEW_ALL_ITEMS = "view_all_items"
    const val VIEW_ALL_ITEMS_EDIT = "view_all_items_edit"
    const val VIEW_ALL_ITEMS_DELETE = "view_all_items_delete"
    const val VIEW_ALL_ITEMS_CHECKOUT = "view_all_items_checkout"
    const val CHECKED_OUT_ITEMS = "checked_out_items"
    const val CHECKED_IN_ITEMS = "checked_in_items"
    const val MY_CHECKED_OUT_ITEMS = "my_checked_out_items"
    const val CHECKOUT_ITEMS_LIST = "checkout_items_list"
    const val CHECKIN_ITEMS_LIST = "checkin_items_list"
    const val ITEM_DETAIL = "item_detail"
    const val ITEM_EDIT = "item_edit"
    const val ITEM_DELETE = "item_delete"
    const val ITEM_CHECKOUT = "item_checkout"
    const val CHECK_IN_OUT = "check_in_out"
    const val MANAGE_USERS = "manage_users"
    const val USER_EDIT = "user_edit"
    const val MANAGE_ITEMS = "manage_items"
    const val TERMS_PRIVACY = "terms_privacy"
}

@Composable
fun AppNavHost() {
    val nav = rememberNavController()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

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
                onGoToRegister = { nav.navigate(Dest.REGISTER) },
                onForgotPassword = { nav.navigate(Dest.FORGOT_PASSWORD) }
            )
        }

        composable(Dest.FORGOT_PASSWORD) {
            ForgotPasswordScreen(
                onBack = { nav.popBackStack() }
            )
        }

        composable(Dest.REGISTER) {
            RegisterScreen(
                onBack = { nav.navigate(Dest.HOME) },
                onRegistered = {
                    nav.navigate(Dest.LOGIN) {
                        launchSingleTop = true
                        popUpTo(Dest.REGISTER) { inclusive = true }
                    }
                },
                onGoToLogin = { nav.navigate(Dest.LOGIN) }
            )
        }

        composable(Dest.DASHBOARD) {
            val items by DataRepository.itemsFlow().collectAsState()
            val checkedOutItems by DataRepository.checkedOutItemsFlow().collectAsState()
            val stats by DataRepository.statsFlow().collectAsState()
            val currentUser by DataRepository.currentUserFlow().collectAsState()

            val totalItems = items.size
            val checkedOut = stats.checkedOutCount
            val totalValue = stats.totalValue
            val itemsOutOver30Days = checkedOutItems.count { it.daysOut >= 30 }
            val stolenLostDamagedValue = stats.stolenLostDamagedValue
            val stolenLostDamagedCount = stats.stolenLostDamagedCount

            DashboardScreen(
                navController = nav, // ✅ pass NavHostController
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
                    DataRepository.logout()
                    nav.navigate(Dest.HOME) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onViewAllItems = { nav.navigate(Dest.VIEW_ALL_ITEMS) },
                onMyCheckedOutItems = { nav.navigate(Dest.MY_CHECKED_OUT_ITEMS) },
                onCheckedIn = { nav.navigate(Dest.CHECKED_IN_ITEMS) },
                onCheckedOut = { nav.navigate(Dest.CHECKED_OUT_ITEMS) },
                onCheckIn = { nav.navigate(Dest.CHECKIN_ITEMS_LIST) },
                onCheckOut = { nav.navigate(Dest.CHECKOUT_ITEMS_LIST) },
                onManageUsers = { nav.navigate(Dest.MANAGE_USERS) },
                onManageItem = { nav.navigate(Dest.MANAGE_ITEMS) }
            )
        }

        composable(Dest.ADD_ITEM) {
            AddItemScreen(
                onBack = { nav.popBackStack() },
                onSubmit = { newItem ->
                    val item = InventoryItem(
                        name = newItem.name,
                        serialId = newItem.serialId,
                        description = newItem.description,
                        condition = newItem.condition,
                        status = newItem.status,
                        permanentCheckout = newItem.permanentCheckout,
                        permissionNeeded = newItem.permissionNeeded,
                        driversLicenseNeeded = newItem.driversLicenseNeeded
                    )
                    scope.launch {
                        val result = DataRepository.addItem(item)
                        result.onSuccess {
                            Toast.makeText(context, "Item added", Toast.LENGTH_SHORT).show()
                            nav.popBackStack()
                        }.onFailure { error ->
                            Toast.makeText(context, error.message ?: "Failed to add item", Toast.LENGTH_LONG).show()
                        }
                    }
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

        composable(Dest.MY_CHECKED_OUT_ITEMS) {
            MyCheckedOutItemsScreen(
                onBack = { nav.popBackStack() },
                onItemClick = { itemId ->
                    nav.navigate("${Dest.ITEM_DETAIL}/$itemId")
                }
            )
        }

        composable(Dest.CHECKED_IN_ITEMS) {
            CheckedInItemsScreen(
                onBack = { nav.popBackStack() },
                onItemClick = { itemId ->
                    nav.navigate("${Dest.ITEM_DETAIL}/$itemId")
                }
            )
        }

        composable(Dest.CHECKOUT_ITEMS_LIST) {
            CheckoutItemsListScreen(
                onBack = { nav.popBackStack() },
                onItemClick = { itemId ->
                    nav.navigate("${Dest.ITEM_CHECKOUT}/$itemId")
                }
            )
        }

        composable(Dest.CHECKIN_ITEMS_LIST) {
            CheckInItemsListScreen(
                onBack = { nav.popBackStack() },
                onItemClick = { itemId ->
                    nav.navigate("${Dest.CHECK_IN_OUT}/$itemId")
                }
            )
        }

        composable("${Dest.ITEM_DETAIL}/{itemId}") { backStackEntry ->
            val itemId = backStackEntry.arguments?.getString("itemId") ?: return@composable
            ItemDetailScreen(
                itemId = itemId,
                onBack = { nav.popBackStack() }
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
                onBack = { nav.popBackStack() },
                onUserClick = { userId ->
                    nav.navigate("${Dest.USER_EDIT}/$userId")
                }
            )
        }

        composable("${Dest.USER_EDIT}/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: return@composable
            UserEditScreen(
                userId = userId,
                onBack = { nav.popBackStack() }
            )
        }

        composable(Dest.MANAGE_ITEMS) {
            // Navigate to Add/Edit/Delete from here
            ManageScreen(
                onAddClick = { nav.navigate(Dest.ADD_ITEM) },
                onEditClick = { nav.navigate(Dest.VIEW_ALL_ITEMS_EDIT) },
                onDeleteClick = { nav.navigate(Dest.VIEW_ALL_ITEMS_DELETE) },
                onBack = { nav.navigate(Dest.DASHBOARD) }
            )
        }

        // View all items for editing
        composable(Dest.VIEW_ALL_ITEMS_EDIT) {
            ViewAllItemsScreen(
                onBack = { nav.popBackStack() },
                onItemClick = { itemId ->
                    nav.navigate("${Dest.ITEM_EDIT}/$itemId")
                }
            )
        }

        // View all items for deleting
        composable(Dest.VIEW_ALL_ITEMS_DELETE) {
            ViewAllItemsScreen(
                onBack = { nav.popBackStack() },
                onItemClick = { itemId ->
                    nav.navigate("${Dest.ITEM_DELETE}/$itemId")
                }
            )
        }

        // View all items for checkout
        composable(Dest.VIEW_ALL_ITEMS_CHECKOUT) {
            ViewAllItemsScreen(
                onBack = { nav.popBackStack() },
                onItemClick = { itemId ->
                    nav.navigate("${Dest.ITEM_CHECKOUT}/$itemId")
                }
            )
        }

        // Edit specific item
        composable("${Dest.ITEM_EDIT}/{itemId}") { backStackEntry ->
            val itemId = backStackEntry.arguments?.getString("itemId") ?: return@composable
            ItemEditScreen(
                itemId = itemId,
                onBack = { nav.popBackStack() }
            )
        }

        // Delete specific item
        composable("${Dest.ITEM_DELETE}/{itemId}") { backStackEntry ->
            val itemId = backStackEntry.arguments?.getString("itemId") ?: return@composable
            ItemDeleteScreen(
                itemId = itemId,
                onBack = { nav.popBackStack() }
            )
        }

        // Checkout specific item
        composable("${Dest.ITEM_CHECKOUT}/{itemId}") { backStackEntry ->
            val itemId = backStackEntry.arguments?.getString("itemId") ?: return@composable
            ItemCheckoutScreen(
                itemId = itemId,
                onBack = { nav.popBackStack() }
            )
        }

        composable(Dest.TERMS_PRIVACY) {
            TermsPrivacyScreen(onBack = { nav.popBackStack() })
        }
    }
}
