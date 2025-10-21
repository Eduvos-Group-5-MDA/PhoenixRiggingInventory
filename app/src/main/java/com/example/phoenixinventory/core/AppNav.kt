package com.example.phoenixinventory.core

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.phoenixinventory.data.FirebaseRepository
import com.google.firebase.auth.FirebaseAuth
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
    const val SUBMIT_REPORT = "submit_report"
    const val VIEW_REPORTS = "view_reports"
    const val REPORT_DETAIL = "report_detail"
    const val VIEW_DELETED_ITEMS = "view_deleted_items"
}

// Helper composable to protect admin-only routes
@Composable
fun AdminProtectedRoute(
    navController: NavHostController,
    content: @Composable () -> Unit
) {
    val firebaseRepo = remember { FirebaseRepository() }
    val scope = rememberCoroutineScope()
    var userRole by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        scope.launch {
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            if (userId != null) {
                val user = firebaseRepo.getUserById(userId).getOrNull()
                userRole = user?.role
            }
            isLoading = false
        }
    }

    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else if (userRole?.equals("Admin", ignoreCase = true) == true ||
               userRole?.equals("Manager", ignoreCase = true) == true) {
        content()
    } else {
        // Show access denied screen
        AccessDeniedScreen(
            onBack = {
                Toast.makeText(context, "Admin or Manager access required", Toast.LENGTH_SHORT).show()
                navController.popBackStack()
            }
        )
    }
}

@Composable
fun AccessDeniedScreen(onBack: () -> Unit) {
    val backgroundColor = Color(0xFF0E1116)
    val cardColor = Color(0xFF1A2028)
    val onSurfaceColor = Color(0xFFE7EBF2)
    val mutedColor = Color(0xFFBFC8D4)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            color = cardColor,
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .padding(24.dp)
                .widthIn(max = 400.dp)
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Outlined.Lock,
                    contentDescription = null,
                    tint = Color(0xFFEF4444),
                    modifier = Modifier.size(64.dp)
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    "Access Denied",
                    color = onSurfaceColor,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "This page is only accessible to administrators and managers.",
                    color = mutedColor,
                    fontSize = 14.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(Modifier.height(24.dp))
                Button(
                    onClick = onBack,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF0A6CFF)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Go Back", color = Color.White)
                }
            }
        }
    }
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
                onRegistered = { nav.popBackStack(Dest.HOME, inclusive = false) },
                onGoToLogin = { nav.navigate(Dest.LOGIN) },
                onTermsPrivacy = { nav.navigate(Dest.TERMS_PRIVACY) }
            )
        }

        composable(Dest.DASHBOARD) {
            val firebaseRepo = remember { FirebaseRepository() }
            val scope = rememberCoroutineScope()

            var userName by remember { mutableStateOf("Loading...") }
            var email by remember { mutableStateOf("") }
            var userId by remember { mutableStateOf("") }
            var role by remember { mutableStateOf("") }
            var totalItems by remember { mutableStateOf(0) }
            var checkedOut by remember { mutableStateOf(0) }
            var totalValue by remember { mutableStateOf(0.0) }
            var itemsOutOver30Days by remember { mutableStateOf(0) }
            var stolenLostDamagedValue by remember { mutableStateOf(0.0) }
            var stolenLostDamagedCount by remember { mutableStateOf(0) }

            LaunchedEffect(Unit) {
                scope.launch {
                    // Get current user
                    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
                    if (currentUserId != null) {
                        userId = currentUserId
                        val currentUser = firebaseRepo.getUserById(currentUserId).getOrNull()
                        userName = currentUser?.name ?: "User"
                        email = currentUser?.email ?: ""
                        role = currentUser?.role ?: "Employee"
                    }

                    // Get stats
                    totalItems = firebaseRepo.getAllItems().getOrNull()?.size ?: 0
                    checkedOut = firebaseRepo.getCheckedOutCount().getOrElse { 0 }
                    totalValue = firebaseRepo.getTotalValue().getOrElse { 0.0 }
                    itemsOutOver30Days = firebaseRepo.getItemsOutLongerThan(30).getOrNull()?.size ?: 0
                    stolenLostDamagedValue = firebaseRepo.getStolenLostDamagedValue().getOrElse { 0.0 }
                    stolenLostDamagedCount = firebaseRepo.getStolenLostDamagedCount().getOrElse { 0 }
                }
            }

            DashboardScreen(
                navController = nav,
                userName = userName,
                email = email,
                role = role,
                totalItems = totalItems,
                checkedOut = checkedOut,
                totalValue = totalValue,
                itemsOutOver30Days = itemsOutOver30Days,
                stolenLostDamagedValue = stolenLostDamagedValue,
                stolenLostDamagedCount = stolenLostDamagedCount,
                userId = userId,
                onLogout = {
                    FirebaseAuth.getInstance().signOut()
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
            AdminProtectedRoute(navController = nav) {
                AddItemScreen(
                    onBack = { nav.popBackStack() },
                    onSubmit = { newItem ->
                        val item = com.example.phoenixinventory.data.InventoryItem(
                            name = newItem.name,
                            serialId = newItem.serialId,
                            description = newItem.description,
                            category = newItem.category,
                            condition = newItem.condition,
                            status = newItem.status,
                            value = newItem.value,
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
            AdminProtectedRoute(navController = nav) {
                ManageUsersScreen(
                    onBack = { nav.popBackStack() },
                    onUserClick = { userId ->
                        nav.navigate("${Dest.USER_EDIT}/$userId")
                    }
                )
            }
        }

        composable("${Dest.USER_EDIT}/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: return@composable
            AdminProtectedRoute(navController = nav) {
                UserEditScreen(
                    userId = userId,
                    onBack = { nav.popBackStack() }
                )
            }
        }

        composable(Dest.MANAGE_ITEMS) {
            AdminProtectedRoute(navController = nav) {
                ManageScreen(
                    onAddClick = { nav.navigate(Dest.ADD_ITEM) },
                    onEditClick = { nav.navigate(Dest.VIEW_ALL_ITEMS_EDIT) },
                    onDeleteClick = { nav.navigate(Dest.VIEW_ALL_ITEMS_DELETE) },
                    onViewDeletedClick = { nav.navigate(Dest.VIEW_DELETED_ITEMS) },
                    onBack = { nav.navigate(Dest.DASHBOARD) }
                )
            }
        }

        // View all items for editing
        composable(Dest.VIEW_ALL_ITEMS_EDIT) {
            AdminProtectedRoute(navController = nav) {
                ViewAllItemsScreen(
                    onBack = { nav.popBackStack() },
                    onItemClick = { itemId ->
                        nav.navigate("${Dest.ITEM_EDIT}/$itemId")
                    }
                )
            }
        }

        // View all items for deleting
        composable(Dest.VIEW_ALL_ITEMS_DELETE) {
            AdminProtectedRoute(navController = nav) {
                ViewAllItemsScreen(
                    onBack = { nav.popBackStack() },
                    onItemClick = { itemId ->
                        nav.navigate("${Dest.ITEM_DELETE}/$itemId")
                    }
                )
            }
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
            AdminProtectedRoute(navController = nav) {
                ItemEditScreen(
                    itemId = itemId,
                    onBack = { nav.popBackStack() }
                )
            }
        }

        // Delete specific item
        composable("${Dest.ITEM_DELETE}/{itemId}") { backStackEntry ->
            val itemId = backStackEntry.arguments?.getString("itemId") ?: return@composable
            AdminProtectedRoute(navController = nav) {
                ItemDeleteScreen(
                    itemId = itemId,
                    onBack = { nav.popBackStack() }
                )
            }
        }

        // Checkout specific item
        composable("${Dest.ITEM_CHECKOUT}/{itemId}") { backStackEntry ->
            val itemId = backStackEntry.arguments?.getString("itemId") ?: return@composable
            ItemCheckoutScreen(
                itemId = itemId,
                onBack = { nav.popBackStack() }
            )
        }

        // Submit Report
        composable("${Dest.SUBMIT_REPORT}/{userName}/{email}/{userId}") { backStackEntry ->
            val userName = backStackEntry.arguments?.getString("userName") ?: ""
            val email = backStackEntry.arguments?.getString("email") ?: ""
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            SubmitReportScreen(
                userName = userName,
                userEmail = email,
                userId = userId,
                onBack = { nav.popBackStack() }
            )
        }

        // View Reports (Admin/Manager only)
        composable(Dest.VIEW_REPORTS) {
            AdminProtectedRoute(navController = nav) {
                ViewReportsScreen(
                    onBack = { nav.popBackStack() },
                    onReportClick = { reportId ->
                        nav.navigate("${Dest.REPORT_DETAIL}/$reportId")
                    }
                )
            }
        }

        // Report Detail (Admin/Manager only)
        composable("${Dest.REPORT_DETAIL}/{reportId}") { backStackEntry ->
            val reportId = backStackEntry.arguments?.getString("reportId") ?: return@composable
            AdminProtectedRoute(navController = nav) {
                ReportDetailScreen(
                    reportId = reportId,
                    onBack = { nav.popBackStack() }
                )
            }
        }

        // View Deleted Items (Admin/Manager only)
        composable(Dest.VIEW_DELETED_ITEMS) {
            AdminProtectedRoute(navController = nav) {
                ViewDeletedItemsScreen(
                    onBack = { nav.popBackStack() }
                )
            }
        }

        composable(Dest.TERMS_PRIVACY) {
            TermsPrivacyScreen(onBack = { nav.popBackStack() })
        }
    }
}
