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
import com.example.phoenixinventory.data.FirebaseRepository
import com.example.phoenixinventory.data.InventoryItem
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

/* ---------- Palette ---------- */
private val Carbon = Color(0xFF0E1116)
private val Charcoal = Color(0xFF151A21)
private val CardDark = Color(0xFF1A2028)
private val OnDark = Color(0xFFE7EBF2)
private val Muted = Color(0xFFBFC8D4)
private val Primary = Color(0xFF0A0C17)
private val PrimaryContainer = Color(0xFF121729)

/**
 * Check-in screen for returning borrowed items.
 * Updates item status to Available and closes the checkout record.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckInOutScreen(
    itemId: String,
    onBack: () -> Unit = {}
) {
    val ctx = LocalContext.current
    val firebaseRepo = remember { FirebaseRepository() }
    val scope = rememberCoroutineScope()

    // State for Firebase data
    var item by remember { mutableStateOf<InventoryItem?>(null) }
    var currentUserId by remember { mutableStateOf("") }
    var currentUserName by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    var isProcessing by remember { mutableStateOf(false) }

    val isCheckOut = item?.status == "Available"
    var notes by remember { mutableStateOf("") }

    // Load data from Firebase
    LaunchedEffect(Unit) {
        scope.launch {
            isLoading = true
            // Get current user ID
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            currentUserId = userId ?: ""

            // Load item and current user info
            item = firebaseRepo.getItemById(itemId).getOrNull()
            val currentUser = firebaseRepo.getUserById(currentUserId).getOrNull()
            currentUserName = currentUser?.name ?: "Current User"
            isLoading = false
        }
    }

    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(Carbon, Charcoal, Carbon))),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = PrimaryContainer)
        }
        return
    }

    // Create a local non-null variable for smart casting
    val currentItem = item
    if (currentItem == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(Carbon, Charcoal, Carbon))),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Outlined.ErrorOutline, contentDescription = null, tint = OnDark, modifier = Modifier.size(64.dp))
                Spacer(Modifier.height(16.dp))
                Text("Item not found", color = OnDark, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(24.dp))
                OutlinedButton(
                    onClick = onBack,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = OnDark)
                ) {
                    Text("Go Back")
                }
            }
        }
        return
    }

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
                        .background(PrimaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        if (isCheckOut) Icons.Outlined.Assignment else Icons.Outlined.CheckCircle,
                        contentDescription = null,
                        tint = OnDark
                    )
                }

                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        if (isCheckOut) "Check Out Item" else "Check In Item",
                        color = OnDark,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(currentItem.name, color = Muted, fontSize = 13.sp)
                }
            }

            Spacer(Modifier.height(16.dp))

            /* ---------- Item Info Card ---------- */
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

                    DetailRow("Name", currentItem.name)
                    DetailRow("Serial/ID", currentItem.serialId)
                    DetailRow("Condition", currentItem.condition)
                    DetailRow("Status", currentItem.status)

                    if (currentItem.permissionNeeded) {
                        Spacer(Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.Shield, contentDescription = null, tint = Color(0xFFF5A524), modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Permission needed", color = Color(0xFFF5A524), fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                    if (currentItem.driversLicenseNeeded) {
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

            /* ---------- Checkout Form (only for checkout) ---------- */
            if (isCheckOut) {
                Surface(
                    color = Charcoal,
                    shape = RoundedCornerShape(20.dp),
                    tonalElevation = 2.dp,
                    shadowElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Checking Out To", color = OnDark, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        Spacer(Modifier.height(8.dp))

                        // Display current user (read-only)
                        Surface(
                            color = Primary,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Outlined.Person,
                                    contentDescription = null,
                                    tint = OnDark,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(Modifier.width(12.dp))
                                Text(
                                    currentUserName,
                                    color = OnDark,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium
                                )
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
            }

            /* ---------- Action Button ---------- */
            Button(
                onClick = {
                    isProcessing = true
                    scope.launch {
                        try {
                            if (isCheckOut) {
                                val result = firebaseRepo.checkOutItem(itemId, currentUserId, notes)
                                if (result.isSuccess) {
                                    Toast.makeText(ctx, "${currentItem.name} checked out successfully", Toast.LENGTH_SHORT).show()
                                    onBack()
                                } else {
                                    Toast.makeText(ctx, "Failed to check out item: ${result.exceptionOrNull()?.message}", Toast.LENGTH_LONG).show()
                                }
                            } else {
                                val result = firebaseRepo.checkInItem(itemId)
                                if (result.isSuccess) {
                                    Toast.makeText(ctx, "${currentItem.name} checked in successfully", Toast.LENGTH_SHORT).show()
                                    onBack()
                                } else {
                                    Toast.makeText(ctx, "Failed to check in item: ${result.exceptionOrNull()?.message}", Toast.LENGTH_LONG).show()
                                }
                            }
                        } catch (e: Exception) {
                            Toast.makeText(ctx, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                        } finally {
                            isProcessing = false
                        }
                    }
                },
                enabled = !isProcessing && !isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isCheckOut) Color(0xFF0A6CFF) else Color(0xFF17C964),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) {
                if (isProcessing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        if (isCheckOut) Icons.Outlined.Assignment else Icons.Outlined.CheckCircle,
                        contentDescription = null
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        if (isCheckOut) "Confirm Check Out" else "Confirm Check In",
                        fontWeight = FontWeight.SemiBold
                    )
                }
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
private fun PreviewCheckOut() {
    MaterialTheme {
        CheckInOutScreen(itemId = "item1")
    }
}
