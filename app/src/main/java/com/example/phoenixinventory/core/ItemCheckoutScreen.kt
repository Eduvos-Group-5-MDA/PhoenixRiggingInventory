package com.example.phoenixinventory.core

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.phoenixinventory.data.DataRepository

/* ---------- Palette ---------- */
private val Carbon = Color(0xFF0E1116)
private val Charcoal = Color(0xFF151A21)
private val CardDark = Color(0xFF1A2028)
private val OnDark = Color(0xFFE7EBF2)
private val Muted = Color(0xFFBFC8D4)
private val Primary = Color(0xFF0A0C17)
private val PrimaryContainer = Color(0xFF121729)
private val CheckoutBlue = Color(0xFF0A6CFF)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemCheckoutScreen(
    itemId: String,
    onBack: () -> Unit = {}
) {
    val ctx = LocalContext.current
    val item = remember { DataRepository.getItemById(itemId) }
    val users = remember { DataRepository.getAllUsers() }
    val currentUser = remember { DataRepository.getCurrentUser() }

    var selectedUserId by remember { mutableStateOf(currentUser.id) }
    var notes by remember { mutableStateOf("") }

    if (item == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(Carbon, Charcoal, Carbon))),
            contentAlignment = Alignment.Center
        ) {
            Text("Item not found", color = OnDark)
        }
        return
    }

    // Check if item is already checked out
    val isAlreadyCheckedOut = item.status == "Checked Out"

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
                IconButton(onClick = onBack) {
                    Icon(Icons.Outlined.ArrowBack, contentDescription = "Back", tint = OnDark)
                }
                Spacer(Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(CheckoutBlue),
                    contentAlignment = Alignment.Center
                ) { Icon(Icons.Outlined.Assignment, contentDescription = null, tint = Color.White) }

                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Text("Check Out Item", color = OnDark, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text("Assign to user", color = Muted, fontSize = 13.sp)
                }
            }

            Spacer(Modifier.height(16.dp))

            /* ---------- Warning if already checked out ---------- */
            if (isAlreadyCheckedOut) {
                Surface(
                    color = Color(0xFFF5A524).copy(alpha = 0.15f),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Outlined.Warning,
                            contentDescription = null,
                            tint = Color(0xFFF5A524),
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(
                            "This item is already checked out. Please check it in first before checking out again.",
                            color = OnDark,
                            fontSize = 14.sp,
                            lineHeight = 20.sp
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))
            }

            /* ---------- Item Details ---------- */
            Surface(
                color = Charcoal,
                shape = RoundedCornerShape(20.dp),
                tonalElevation = 2.dp,
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.Inventory, contentDescription = null, tint = OnDark)
                        Spacer(Modifier.width(8.dp))
                        Text("Item Information", color = OnDark, fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
                    }
                    Spacer(Modifier.height(12.dp))

                    DetailRow("Name", item.name)
                    DetailRow("Serial/ID", item.serialId)
                    DetailRow("Condition", item.condition)
                    DetailRow("Status", item.status)

                    if (item.permissionNeeded) {
                        Spacer(Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.Shield, contentDescription = null, tint = Color(0xFFF5A524), modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Permission needed", color = Color(0xFFF5A524), fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                    if (item.driversLicenseNeeded) {
                        Spacer(Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.Badge, contentDescription = null, tint = Color(0xFFF5A524), modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Driver's license needed", color = Color(0xFFF5A524), fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            /* ---------- Checkout Form ---------- */
            Surface(
                color = Charcoal,
                shape = RoundedCornerShape(20.dp),
                tonalElevation = 2.dp,
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("Select User", color = OnDark, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    Spacer(Modifier.height(8.dp))

                    var expanded by remember { mutableStateOf(false) }
                    val selectedUser = users.find { it.id == selectedUserId }

                    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                        OutlinedTextField(
                            readOnly = true,
                            value = selectedUser?.name ?: "",
                            onValueChange = {},
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                            modifier = Modifier.menuAnchor().fillMaxWidth().clip(RoundedCornerShape(14.dp)),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryContainer,
                                unfocusedBorderColor = PrimaryContainer.copy(alpha = 0.6f),
                                focusedTextColor = OnDark,
                                unfocusedTextColor = OnDark
                            )
                        )
                        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                            users.forEach { user ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(user.name, color = OnDark)
                                            Text(user.email, color = Muted, fontSize = 12.sp)
                                        }
                                    },
                                    onClick = {
                                        selectedUserId = user.id
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    Text("Notes (optional)", color = OnDark, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    Spacer(Modifier.height(6.dp))
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        singleLine = false,
                        minLines = 3,
                        placeholder = { Text("Add any notes about this checkout...", color = Muted) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryContainer,
                            unfocusedBorderColor = PrimaryContainer.copy(alpha = 0.6f),
                            cursorColor = OnDark,
                            focusedTextColor = OnDark,
                            unfocusedTextColor = OnDark
                        ),
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp))
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            /* ---------- Action Buttons ---------- */
            Button(
                onClick = {
                    if (isAlreadyCheckedOut) {
                        Toast.makeText(ctx, "Item is already checked out", Toast.LENGTH_SHORT).show()
                    } else {
                        DataRepository.checkOutItem(itemId, selectedUserId, notes)
                        Toast.makeText(ctx, "${item.name} checked out successfully", Toast.LENGTH_SHORT).show()
                        onBack()
                    }
                },
                enabled = !isAlreadyCheckedOut,
                colors = ButtonDefaults.buttonColors(
                    containerColor = CheckoutBlue,
                    contentColor = Color.White,
                    disabledContainerColor = Muted,
                    disabledContentColor = Charcoal
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) {
                Icon(Icons.Outlined.Assignment, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Confirm Check Out", fontWeight = FontWeight.SemiBold)
            }

            Spacer(Modifier.height(12.dp))

            OutlinedButton(
                onClick = onBack,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = OnDark),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Text("Cancel")
            }

            Spacer(Modifier.height(24.dp))
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

/* ---------- Preview ---------- */
@Preview(showBackground = true, backgroundColor = 0xFF0E1116, widthDp = 412, heightDp = 900)
@Composable
private fun PreviewItemCheckout() {
    MaterialTheme {
        ItemCheckoutScreen(itemId = "item1")
    }
}
