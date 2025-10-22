package com.example.phoenixinventory.core

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.phoenixinventory.data.DataRepository
import com.example.phoenixinventory.data.User
import com.example.phoenixinventory.data.FirebaseRepository
import com.example.phoenixinventory.data.CheckedOutItemDetail
import kotlinx.coroutines.launch

/**
 * User management screen (Admin/Manager only).
 * Lists all users with options to edit or delete user accounts.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageUsersScreen(
    onBack: () -> Unit = {},
    onUserClick: (String) -> Unit = {}
) {
    val backgroundColor = MaterialTheme.colorScheme.background
    val surfaceColor = MaterialTheme.colorScheme.surface
    val cardColor = MaterialTheme.colorScheme.secondary
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val mutedColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
    val primaryContainerColor = MaterialTheme.colorScheme.tertiary

    var users by remember { mutableStateOf<List<User>>(emptyList()) }
    var selectedUser by remember { mutableStateOf<User?>(null) }
    var userCheckouts by remember { mutableStateOf<List<CheckedOutItemDetail>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var currentUser by remember { mutableStateOf<User?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var userToDelete by remember { mutableStateOf<User?>(null) }
    var deleteErrorMessage by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var filterRole by remember { mutableStateOf("All") }
    val firebaseRepo = remember { FirebaseRepository() }
    val scope = rememberCoroutineScope()

    // Check if current user is admin or manager
    // For now, we'll assume the current user is an admin if they can access this screen
    // In a real app, this would come from authentication
    val canDeleteUsers = true  // Temporarily set to true for testing

    LaunchedEffect(Unit) {
        scope.launch {
            isLoading = true
            users = firebaseRepo.getAllUsers().getOrNull() ?: emptyList()
            userCheckouts = firebaseRepo.getCheckedOutItems().getOrNull() ?: emptyList()
            currentUser = firebaseRepo.getCurrentUser().getOrNull()
            isLoading = false
        }
    }

    // Filter users based on search and role
    val filteredUsers = users.filter { user ->
        val matchesSearch = user.name.contains(searchQuery, ignoreCase = true) ||
                user.email.contains(searchQuery, ignoreCase = true) ||
                user.phone.contains(searchQuery, ignoreCase = true)
        val matchesFilter = filterRole == "All" || user.role == filterRole
        matchesSearch && matchesFilter
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(16.dp)
                .widthIn(max = 720.dp)
                .align(Alignment.TopCenter)
                .clip(RoundedCornerShape(28.dp))
                .background(cardColor)
                .padding(16.dp)
        ) {

            /* ---------- Header ---------- */
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Outlined.ArrowBack, contentDescription = "Back", tint = onSurfaceColor)
                }
                Spacer(Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(primaryContainerColor),
                    contentAlignment = Alignment.Center
                ) { Icon(Icons.Outlined.Group, contentDescription = null, tint = onSurfaceColor) }

                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Text("Manage Users", color = onSurfaceColor, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text("${filteredUsers.size} users", color = mutedColor, fontSize = 13.sp)
                }
            }

            Spacer(Modifier.height(16.dp))

            /* ---------- Search Bar ---------- */
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search users...", color = mutedColor) },
                leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null, tint = onSurfaceColor) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = primaryContainerColor,
                    unfocusedBorderColor = primaryContainerColor.copy(alpha = 0.6f),
                    cursorColor = onSurfaceColor,
                    focusedTextColor = onSurfaceColor,
                    unfocusedTextColor = onSurfaceColor
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
            )

            Spacer(Modifier.height(12.dp))

            /* ---------- Filter Chips ---------- */
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                listOf("All", "Admin", "Manager", "Employee", "Guest").forEach { role ->
                    FilterChip(
                        selected = filterRole == role,
                        onClick = { filterRole = role },
                        label = { Text(role, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = primaryContainerColor,
                            selectedLabelColor = onSurfaceColor,
                            containerColor = surfaceColor,
                            labelColor = mutedColor
                        )
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            /* ---------- Users List ---------- */
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = primaryContainerColor)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(filteredUsers) { user ->
                        UserCard(
                            user = user,
                            isSelected = user == selectedUser,
                            onClick = { selectedUser = if (selectedUser == user) null else user },
                            onEditClick = { onUserClick(user.id) },
                            onDeleteClick = if (canDeleteUsers) {
                                {
                                    userToDelete = user
                                    showDeleteDialog = true
                                    deleteErrorMessage = null
                                }
                            } else null,
                            canDelete = canDeleteUsers && user.id != currentUser?.id
                        )
                    }
                }
            }

            /* ---------- Selected User Details ---------- */
            if (selectedUser != null) {
                Spacer(Modifier.height(16.dp))

                Surface(
                    color = surfaceColor,
                    shape = RoundedCornerShape(20.dp),
                    tonalElevation = 2.dp,
                    shadowElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.Person, contentDescription = null, tint = onSurfaceColor)
                            Spacer(Modifier.width(8.dp))
                            Text("User Details", color = onSurfaceColor, fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
                        }
                        Spacer(Modifier.height(12.dp))

                        DetailRow("Name", selectedUser!!.name)
                        DetailRow("Email", selectedUser!!.email)
                        DetailRow("Role", selectedUser!!.role)

                        // Get items checked out by this user
                        val selectedUserCheckouts = userCheckouts.filter { it.user.id == selectedUser!!.id }

                        Spacer(Modifier.height(8.dp))
                        DetailRow("Items Checked Out", "${selectedUserCheckouts.size}")

                        if (selectedUserCheckouts.isNotEmpty()) {
                            Spacer(Modifier.height(8.dp))
                            Text("Checked Out Items:", color = mutedColor, fontSize = 13.sp)
                            Spacer(Modifier.height(4.dp))
                            selectedUserCheckouts.forEach { checkout ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 2.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("• ${checkout.item.name}", color = onSurfaceColor, fontSize = 13.sp)
                                    Text(
                                        "${checkout.daysOut} days",
                                        color = if (checkout.daysOut >= 30) Color(0xFFEF4444) else mutedColor,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
        }

        /* ---------- Delete Confirmation Dialog ---------- */
        if (showDeleteDialog && userToDelete != null) {
            AlertDialog(
                onDismissRequest = {
                    showDeleteDialog = false
                    userToDelete = null
                    deleteErrorMessage = null
                },
                title = {
                    Text("Delete User", fontWeight = FontWeight.Bold)
                },
                text = {
                    Column {
                        Text("Are you sure you want to permanently delete ${userToDelete!!.name}?")
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "This action cannot be undone.",
                            color = Color(0xFFEF4444),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        if (deleteErrorMessage != null) {
                            Spacer(Modifier.height(12.dp))
                            Text(
                                deleteErrorMessage!!,
                                color = Color(0xFFEF4444),
                                fontSize = 13.sp
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            scope.launch {
                                val result = firebaseRepo.hardDeleteUser(userToDelete!!.id)
                                if (result.isSuccess) {
                                    // Refresh the user list
                                    users = firebaseRepo.getAllUsers().getOrNull() ?: emptyList()
                                    userCheckouts = firebaseRepo.getCheckedOutItems().getOrNull() ?: emptyList()
                                    if (selectedUser?.id == userToDelete!!.id) {
                                        selectedUser = null
                                    }
                                    showDeleteDialog = false
                                    userToDelete = null
                                    deleteErrorMessage = null
                                } else {
                                    deleteErrorMessage = result.exceptionOrNull()?.message
                                        ?: "Failed to delete user"
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFEF4444)
                        )
                    ) {
                        Text("Delete")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showDeleteDialog = false
                            userToDelete = null
                            deleteErrorMessage = null
                        }
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
private fun UserCard(
    user: User,
    isSelected: Boolean,
    onClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: (() -> Unit)? = null,
    canDelete: Boolean = false
) {
    val surfaceColor = MaterialTheme.colorScheme.surface
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val mutedColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
    val primaryContainerColor = MaterialTheme.colorScheme.tertiary

    Surface(
        onClick = onClick,
        color = if (isSelected) primaryContainerColor else surfaceColor,
        shape = RoundedCornerShape(20.dp),
        tonalElevation = 2.dp,
        shadowElevation = 2.dp,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 80.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) surfaceColor else primaryContainerColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.Person, contentDescription = null, tint = onSurfaceColor)
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(user.name, color = onSurfaceColor, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                Text(user.email, color = mutedColor, fontSize = 12.sp)
            }
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(getRoleColor(user.role)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        user.role,
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconButton(
                        onClick = onEditClick,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Outlined.Edit,
                            contentDescription = "Edit User",
                            tint = onSurfaceColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    if (canDelete && onDeleteClick != null) {
                        IconButton(
                            onClick = onDeleteClick,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                Icons.Outlined.Delete,
                                contentDescription = "Delete User",
                                tint = Color(0xFFEF4444),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val mutedColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = mutedColor, fontSize = 14.sp)
        Text(value, color = onSurfaceColor, fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}

private fun getRoleColor(role: String): Color {
    return when (role) {
        "Admin" -> Color(0xFFEF4444)
        "Manager" -> Color(0xFF8B5CF6)
        "Employee" -> Color(0xFF0A6CFF)
        else -> Color(0xFFBFC8D4)
    }
}

/* ---------- Preview ---------- */
@Preview(showBackground = true, backgroundColor = 0xFF0E1116, widthDp = 412, heightDp = 900)
@Composable
private fun PreviewManageUsers() {
    MaterialTheme {
        ManageUsersScreen()
    }
}
