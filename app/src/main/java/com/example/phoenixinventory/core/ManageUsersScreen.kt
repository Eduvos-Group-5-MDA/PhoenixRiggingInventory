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

/* ---------- Palette ---------- */
private val Carbon = Color(0xFF0E1116)
private val Charcoal = Color(0xFF151A21)
private val CardDark = Color(0xFF1A2028)
private val OnDark = Color(0xFFE7EBF2)
private val Muted = Color(0xFFBFC8D4)
private val Primary = Color(0xFF0A0C17)
private val PrimaryContainer = Color(0xFF121729)

@Composable
fun ManageUsersScreen(
    onBack: () -> Unit = {}
) {
    val users = remember { DataRepository.getAllUsers() }
    var selectedUser by remember { mutableStateOf<User?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Carbon, Charcoal, Carbon)))
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(16.dp)
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
                IconButton(onClick = onBack) {
                    Icon(Icons.Outlined.ArrowBack, contentDescription = "Back", tint = OnDark)
                }
                Spacer(Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(PrimaryContainer),
                    contentAlignment = Alignment.Center
                ) { Icon(Icons.Outlined.Group, contentDescription = null, tint = OnDark) }

                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Text("Manage Users", color = OnDark, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text("${users.size} total users", color = Muted, fontSize = 13.sp)
                }
            }

            Spacer(Modifier.height(16.dp))

            /* ---------- Users List ---------- */
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(users) { user ->
                    UserCard(
                        user = user,
                        isSelected = user == selectedUser,
                        onClick = { selectedUser = if (selectedUser == user) null else user }
                    )
                }
            }

            /* ---------- Selected User Details ---------- */
            if (selectedUser != null) {
                Spacer(Modifier.height(16.dp))

                Surface(
                    color = Charcoal,
                    shape = RoundedCornerShape(20.dp),
                    tonalElevation = 2.dp,
                    shadowElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.Person, contentDescription = null, tint = OnDark)
                            Spacer(Modifier.width(8.dp))
                            Text("User Details", color = OnDark, fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
                        }
                        Spacer(Modifier.height(12.dp))

                        DetailRow("Name", selectedUser!!.name)
                        DetailRow("Email", selectedUser!!.email)
                        DetailRow("Role", selectedUser!!.role)

                        // Get items checked out by this user
                        val userCheckouts = DataRepository.getCheckedOutItems()
                            .filter { it.user.id == selectedUser!!.id }

                        Spacer(Modifier.height(8.dp))
                        DetailRow("Items Checked Out", "${userCheckouts.size}")

                        if (userCheckouts.isNotEmpty()) {
                            Spacer(Modifier.height(8.dp))
                            Text("Checked Out Items:", color = Muted, fontSize = 13.sp)
                            Spacer(Modifier.height(4.dp))
                            userCheckouts.forEach { checkout ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 2.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("• ${checkout.item.name}", color = OnDark, fontSize = 13.sp)
                                    Text(
                                        "${checkout.daysOut} days",
                                        color = if (checkout.daysOut >= 30) Color(0xFFEF4444) else Muted,
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
    }
}

@Composable
private fun UserCard(
    user: User,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = if (isSelected) PrimaryContainer else Charcoal,
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
                    .background(if (isSelected) Charcoal else PrimaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.Person, contentDescription = null, tint = OnDark)
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(user.name, color = OnDark, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                Text(user.email, color = Muted, fontSize = 12.sp)
            }
            Column(horizontalAlignment = Alignment.End) {
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
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Muted, fontSize = 14.sp)
        Text(value, color = OnDark, fontSize = 14.sp, fontWeight = FontWeight.Medium)
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
