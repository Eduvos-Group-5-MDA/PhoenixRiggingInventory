package com.example.phoenixinventory.core

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.phoenixinventory.ui.theme.ThemeState

/* ---------- Palette (same as other screens) ---------- */
private val Carbon = Color(0xFF0E1116)
private val Charcoal = Color(0xFF151A21)
private val CardDark = Color(0xFF1A2028)
private val Ink = Color(0xFF0B0F1C)
private val OnDark = Color(0xFFE7EBF2)
private val Muted = Color(0xFFBFC8D4)
private val Primary = Color(0xFF0A0C17)
private val PrimaryContainer = Color(0xFF121729)

/* ---------- Screen ---------- */
@Composable
fun DashboardScreen(
    navController: NavHostController,              // 👈 Added for navigation
    userName: String,
    email: String,
    role: String,
    totalItems: Int,
    checkedOut: Int,
    totalValue: Double,
    itemsOutOver30Days: Int,
    stolenLostDamagedValue: Double,
    stolenLostDamagedCount: Int,
    userId: String,
    onLogout: () -> Unit = {},
    onViewAllItems: () -> Unit = {},
    onMyCheckedOutItems: () -> Unit = {},
    onCheckedIn: () -> Unit = {},
    onCheckedOut: () -> Unit = {},
    onCheckIn: () -> Unit = {},
    onCheckOut: () -> Unit = {},
    onManageItem: () -> Unit = {},
    onManageUsers: () -> Unit = {}
) {
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }

    val backgroundColor = MaterialTheme.colorScheme.background
    val surfaceColor = MaterialTheme.colorScheme.surface
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val secondaryColor = MaterialTheme.colorScheme.secondary

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
                .widthIn(max = 720.dp)
                .align(Alignment.TopCenter)
                .clip(RoundedCornerShape(28.dp))
                .background(surfaceColor)
                .padding(16.dp)
        ) {

            /* ---------- Header ---------- */
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.tertiary),
                    contentAlignment = Alignment.Center
                ) { Icon(Icons.Outlined.Build, contentDescription = null, tint = onSurfaceColor) }

                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Text("Dashboard", color = onSurfaceColor, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text("Welcome back, $userName", color = onSurfaceColor.copy(alpha = 0.7f), fontSize = 13.sp)
                }
                IconButton(onClick = { showSettingsDialog = true }) {
                    Icon(Icons.Outlined.Settings, contentDescription = "Settings", tint = onSurfaceColor)
                }
                IconButton(onClick = { showLogoutDialog = true }) {
                    Icon(Icons.Outlined.Logout, contentDescription = "Logout", tint = onSurfaceColor)
                }
            }

            Spacer(Modifier.height(16.dp))

            /* ---------- Profile Card ---------- */
            Surface(
                color = secondaryColor,
                shape = RoundedCornerShape(20.dp),
                tonalElevation = 2.dp,
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.tertiary),
                            contentAlignment = Alignment.Center
                        ) { Icon(Icons.Outlined.Person, contentDescription = null, tint = onSurfaceColor) }

                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(userName, color = onSurfaceColor, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                            Text(email, color = onSurfaceColor.copy(alpha = 0.7f), fontSize = 13.sp)
                        }
                    }

                    Spacer(Modifier.height(14.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Role:", color = onSurfaceColor, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.weight(1f))
                        Text(role, color = onSurfaceColor, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            /* ---------- Stats ---------- */
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                StatCard(
                    value = totalItems.toString(),
                    label = "Total Items",
                    icon = Icons.Outlined.Inventory,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    value = checkedOut.toString(),
                    label = "Checked Out Items",
                    icon = Icons.Outlined.Assignment,
                    modifier = Modifier.weight(1f)
                )
            }

            // Admin/Manager-only stats
            if (role.equals("Admin", ignoreCase = true) || role.equals("Manager", ignoreCase = true)) {
                Spacer(Modifier.height(12.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    StatCard(
                        value = "R${"%.0f".format(totalValue)}",
                        label = "Total Value",
                        icon = Icons.Outlined.AttachMoney,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        value = itemsOutOver30Days.toString(),
                        label = "30+ Days Out",
                        icon = Icons.Outlined.Warning,
                        iconTint = if (itemsOutOver30Days > 0) Color(0xFFEF4444) else OnDark,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // Stolen/Lost/Damaged Card (full width)
            if (stolenLostDamagedCount > 0) {
                Surface(
                    color = Charcoal,
                    shape = RoundedCornerShape(20.dp),
                    tonalElevation = 2.dp,
                    shadowElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.ReportProblem,
                                contentDescription = null,
                                tint = Color(0xFFEF4444),
                                modifier = Modifier.size(26.dp)
                            )
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Stolen/Lost/Damaged",
                                    color = OnDark,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "$stolenLostDamagedCount items",
                                    color = Muted,
                                    fontSize = 12.sp
                                )
                            }
                        }
                        Text(
                            text = "R${"%.0f".format(stolenLostDamagedValue)}",
                            color = Color(0xFFEF4444),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(Modifier.height(12.dp))
            }

            Spacer(Modifier.height(22.dp))
            Text("Quick Actions", color = OnDark, fontWeight = FontWeight.Bold, fontSize = 18.sp)

            Spacer(Modifier.height(12.dp))

            /* ---------- Action Rows ---------- */
            ActionRow(
                title = "View All Items",
                subtitle = "Browse all items.",
                icon = Icons.Outlined.FormatListBulleted,
                iconBg = Color(0xFFFF6F00),
                onClick = onViewAllItems
            )

            ActionRow(
                title = "My Checked Out Items",
                subtitle = "View all of the items that you currently have checked out.",
                icon = Icons.Outlined.Inventory2,
                iconBg = Color(0xFFFF6F00),
                onClick = onMyCheckedOutItems
            )

            // Admin/Manager-only: View all checked out items
            if (role.equals("Admin", ignoreCase = true) || role.equals("Manager", ignoreCase = true)) {
                ActionRow(
                    title = "Checked Out Items",
                    subtitle = "View all items currently checked out,",
                    icon = Icons.Outlined.ArrowCircleUp,
                    iconBg = Color(0xFFFF6F00),
                    onClick = onCheckedOut
                )
            }

            // Admin/Manager-only: View all checked in items
            if (role.equals("Admin", ignoreCase = true) || role.equals("Manager", ignoreCase = true)) {
                ActionRow(
                    title = "Checked In Items",
                    subtitle = "View all items currently not checked out,",
                    icon = Icons.Outlined.ArrowCircleDown,
                    iconBg = Color(0xFFFF6F00),
                    onClick = onCheckedIn
                )
            }

            // Admin/Manager-only: Manage Items
            if (role.equals("Admin", ignoreCase = true) || role.equals("Manager", ignoreCase = true)) {
                ActionRow(
                    title = "Manage Item",
                    subtitle = "Add, edit or delete item,",
                    icon = Icons.Outlined.Build,
                    iconBg = Color(0xFFFF6F00),
                    onClick = {
                        navController.navigate(Dest.MANAGE_ITEMS)
                        onManageItem()
                    }
                )
            }

            ActionRow(
                title = "Check Out Item",
                subtitle = "Check out an item,",
                icon = Icons.Outlined.FileUpload,
                iconBg = Color(0xFFFF6F00),
                onClick = onCheckOut
            )

            ActionRow(
                title = "Check In Item",
                subtitle = "Check in an item that you currently have checked out,",
                icon = Icons.Outlined.FileDownload,
                iconBg = Color(0xFFFF6F00),
                onClick = onCheckIn
            )

            // Admin/Manager-only actions
            if (role.equals("Admin", ignoreCase = true) || role.equals("Manager", ignoreCase = true)) {
                ActionRow(
                    title = "Manage Users",
                    subtitle = "View and manage users",
                    icon = Icons.Outlined.Group,
                    iconBg = Color(0xFFFF6F00),
                    onClick = onManageUsers
                )

                ActionRow(
                    title = "View Reports",
                    subtitle = "View and manage user reports",
                    icon = Icons.Outlined.Report,
                    iconBg = Color(0xFFFF6F00),
                    onClick = {
                        navController.navigate(Dest.VIEW_REPORTS)
                    }
                )
            }

            Spacer(Modifier.height(24.dp))
        }

        /* ---------- Logout Confirmation Dialog ---------- */
        if (showLogoutDialog) {
            AlertDialog(
                onDismissRequest = { showLogoutDialog = false },
                title = { Text("Logout", color = OnDark) },
                text = { Text("Are you sure you want to log out?", color = Muted) },
                confirmButton = {
                    TextButton(onClick = {
                        showLogoutDialog = false
                        onLogout()
                    }) {
                        Text("Yes", color = Color(0xFFEF4444))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showLogoutDialog = false }) {
                        Text("Cancel", color = OnDark)
                    }
                },
                containerColor = CardDark
            )
        }

        /* ---------- Settings Dialog ---------- */
        if (showSettingsDialog) {
            SettingsDialog(
                onDismiss = { showSettingsDialog = false },
                onReportClick = {
                    showSettingsDialog = false
                    navController.navigate("${Dest.SUBMIT_REPORT}/$userName/$email/$userId")
                }
            )
        }
    }
}

/* ---------- Pieces ---------- */

@Composable
private fun StatCard(
    value: String,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    iconTint: Color = OnDark
) {
    Surface(
        color = Charcoal,
        shape = RoundedCornerShape(20.dp),
        tonalElevation = 2.dp,
        shadowElevation = 2.dp,
        modifier = modifier
            .height(110.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = value,
                    color = OnDark,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = label,
                    color = Muted,
                    fontSize = 13.sp
                )
            }
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(26.dp)
            )
        }
    }
}

@Composable
private fun ActionRow(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconBg: Color,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = Charcoal,
        shape = RoundedCornerShape(24.dp),
        tonalElevation = 2.dp,
        shadowElevation = 2.dp,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 84.dp)
            .padding(vertical = 6.dp)
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
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = Color.White)
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(title, color = OnDark, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                Text(subtitle, color = Muted, fontSize = 13.sp)
            }
            Icon(Icons.Outlined.NavigateNext, contentDescription = null, tint = OnDark)
        }
    }
}

/* ---------- Settings Dialog ---------- */
@Composable
private fun SettingsDialog(
    onDismiss: () -> Unit,
    onReportClick: () -> Unit
) {
    val isDarkMode = ThemeState.isDarkMode

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f)),
        contentAlignment = Alignment.Center
    ) {
        // Background scrim - clicking dismisses dialog
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    onDismiss()
                }
        )

        // Settings card
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = CardDark,
            tonalElevation = 8.dp,
            shadowElevation = 8.dp,
            modifier = Modifier
                .padding(16.dp)
                .widthIn(max = 400.dp)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                // Header with close button
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        Icons.Outlined.Settings,
                        contentDescription = null,
                        tint = OnDark,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        "Settings",
                        color = OnDark,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            Icons.Outlined.Close,
                            contentDescription = "Close",
                            tint = OnDark
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))

                // Report button
                Surface(
                    onClick = onReportClick,
                    color = Charcoal,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Icon(
                            Icons.Outlined.Report,
                            contentDescription = null,
                            tint = OnDark,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(Modifier.width(16.dp))
                        Text(
                            "Report an Issue",
                            color = OnDark,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))

                // Dark/Light mode toggle
                Surface(
                    color = Charcoal,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Icon(
                            if (isDarkMode) Icons.Outlined.DarkMode else Icons.Outlined.LightMode,
                            contentDescription = null,
                            tint = OnDark,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(Modifier.width(16.dp))
                        Text(
                            if (isDarkMode) "Dark Mode" else "Light Mode",
                            color = OnDark,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.weight(1f)
                        )
                        Switch(
                            checked = isDarkMode,
                            onCheckedChange = { ThemeState.isDarkMode = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = OnDark,
                                checkedTrackColor = PrimaryContainer,
                                uncheckedThumbColor = Muted,
                                uncheckedTrackColor = Primary
                            )
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))

                // Version number
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Surface(
                        color = PrimaryContainer,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            "Version 1.0.30",
                            color = Muted,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}


/* ---------- Preview ---------- */
@Preview(showBackground = true, backgroundColor = 0xFF0E1116, widthDp = 412, heightDp = 900)
@Composable
private fun PreviewDashboard() {
    val nav = rememberNavController()
    MaterialTheme {
        DashboardScreen(
            navController = nav,                 // 👈 preview-safe nav controller
            userName = "John Doe",
            email = "john.doe@gmail.com",
            role = "Employee",
            totalItems = 5,
            checkedOut = 2,
            totalValue = 2490.0,
            itemsOutOver30Days = 1,
            stolenLostDamagedValue = 520.0,
            stolenLostDamagedCount = 1,
            userId = "preview-user-id"
        )
    }
}
