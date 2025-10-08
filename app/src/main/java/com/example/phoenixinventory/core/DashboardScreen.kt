package com.example.phoenixinventory.core

import androidx.compose.foundation.background
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
    userName: String,
    email: String,
    role: String,
    totalItems: Int,
    checkedOut: Int,
    totalValue: Double,
    itemsOutOver30Days: Int,
    stolenLostDamagedValue: Double,
    stolenLostDamagedCount: Int,
    onLogout: () -> Unit = {},
    onViewAllItems: () -> Unit = {},
    onCheckedIn: () -> Unit = {},
    onCheckedOut: () -> Unit = {},
    onManageItem: () -> Unit = {},
    onRemoveItem: () -> Unit = {}
) {
    var showLogoutDialog by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Carbon, Charcoal, Carbon)))
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
                .widthIn(max = 720.dp)
                .align(Alignment.TopCenter)
                .clip(RoundedCornerShape(28.dp))
                .background(CardDark)
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
                        .background(PrimaryContainer),
                    contentAlignment = Alignment.Center
                ) { Icon(Icons.Outlined.Build, contentDescription = null, tint = OnDark) }

                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Text("Dashboard", color = OnDark, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text("Welcome back, $userName", color = Muted, fontSize = 13.sp)
                }
                IconButton(onClick = { showLogoutDialog = true }) {
                    Icon(Icons.Outlined.Logout, contentDescription = "Logout", tint = OnDark)
                }
            }

            Spacer(Modifier.height(16.dp))

            /* ---------- Profile Card ---------- */
            Surface(
                color = Charcoal,
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
                                .background(PrimaryContainer),
                            contentAlignment = Alignment.Center
                        ) { Icon(Icons.Outlined.Person, contentDescription = null, tint = OnDark) }

                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(userName, color = OnDark, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                            Text(email, color = Muted, fontSize = 13.sp)
                        }
                    }

                    Spacer(Modifier.height(14.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Role:", color = OnDark, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.weight(1f))
                        Text(role, color = OnDark, fontWeight = FontWeight.SemiBold)
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
                    value = checkedOut,
                    label = "Checked Out Items",
                    icon = Icons.Outlined.Assignment,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                StatCard(
                    value = "$${"%.0f".format(totalValue)}",
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
                            text = "$${"%.0f".format(stolenLostDamagedValue)}",
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
                iconBg = Color(0xFF0A6CFF),
                onClick = onViewAllItems
            )

            ActionRow(
                title = "Checked Out Items",
                subtitle = "View all items that have been checked out.",
                icon = Icons.Outlined.CheckCircle,
                iconBg = Color(0xFF17C964),
                onClick = onCheckedOut
            )

            ActionRow(
                title = "Checked In Items",
                subtitle = "View all items that have not been checked .",
                icon = Icons.Outlined.CheckCircle,
                iconBg = Color(0xFF17C964),
                onClick = onCheckedOut
            )

            ActionRow(
                title = "Manage Items",
                subtitle = "Add, Remove, Edit items.",
                icon = Icons.Outlined.Add,
                iconBg = Color(0xFF8B5CF6),
                onClick = onManageItem
            )

            ActionRow(
                title = "Check In Item",
                subtitle = "Check in an item that has been checked out.",
                icon = Icons.Outlined.Add,
                iconBg = Color(0xFF8B5CF6),
                onClick = onManageItem
            )

            ActionRow(
                title = "Check Out Item",
                subtitle = "Check out an item.",
                icon = Icons.Outlined.Add,
                iconBg = Color(0xFF8B5CF6),
                onClick = onManageItem
            )

            ActionRow(
                title = "Manage User",
                subtitle = "Manage or edit an employee or guest account.",
                icon = Icons.Outlined.Add,
                iconBg = Color(0xFF8B5CF6),
                onClick = onManageItem
            )


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

/* ---------- Preview ---------- */
@Preview(showBackground = true, backgroundColor = 0xFF0E1116, widthDp = 412, heightDp = 900)
@Composable
private fun PreviewDashboard() {
    MaterialTheme {
        DashboardScreen(
            userName = "John Doe",
            email = "john.doe@gmail.com",
            role = "Employee",
            totalItems = 5,
            checkedOut = 2,
            totalValue = 2490.0,
            itemsOutOver30Days = 1,
            stolenLostDamagedValue = 520.0,
            stolenLostDamagedCount = 1
        )
    }
}
