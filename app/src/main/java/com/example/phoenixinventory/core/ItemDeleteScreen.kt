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
import com.example.phoenixinventory.data.FirebaseRepository
import com.example.phoenixinventory.data.InventoryItem
import kotlinx.coroutines.launch

/* ---------- Palette ---------- */
private val Carbon = Color(0xFF0E1116)
private val Charcoal = Color(0xFF151A21)
private val CardDark = Color(0xFF1A2028)
private val OnDark = Color(0xFFE7EBF2)
private val Muted = Color(0xFFBFC8D4)
private val PrimaryContainer = Color(0xFF121729)
private val DangerRed = Color(0xFFEF4444)

/**
 * Item deletion screen (Admin/Manager only).
 * Marks items as deleted (soft delete) - they can be restored later.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemDeleteScreen(
    itemId: String,
    onBack: () -> Unit = {}
) {
    val ctx = LocalContext.current
    val firebaseRepo = remember { FirebaseRepository() }
    val scope = rememberCoroutineScope()

    var item by remember { mutableStateOf<InventoryItem?>(null) }
    var checkoutUserName by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var isProcessing by remember { mutableStateOf(false) }
    var showConfirmDialog by remember { mutableStateOf(false) }

    LaunchedEffect(itemId) {
        scope.launch {
            isLoading = true
            item = firebaseRepo.getItemById(itemId).getOrNull()

            // Get checkout info if item is checked out
            if (item?.status == "Checked Out") {
                val checkoutDetail = firebaseRepo.getCurrentCheckout(itemId).getOrNull()
                checkoutUserName = checkoutDetail?.user?.name
            }

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
                        .background(DangerRed),
                    contentAlignment = Alignment.Center
                ) { Icon(Icons.Outlined.Delete, contentDescription = null, tint = Color.White) }

                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Text("Delete Item", color = OnDark, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text("Review before deleting", color = Muted, fontSize = 13.sp)
                }
            }

            Spacer(Modifier.height(16.dp))

            /* ---------- Warning Card ---------- */
            Surface(
                color = DangerRed.copy(alpha = 0.15f),
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
                        tint = DangerRed,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        "This action cannot be undone. The item will be permanently removed from the inventory.",
                        color = OnDark,
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            /* ---------- Item Details ---------- */
            Surface(
                color = Charcoal,
                shape = RoundedCornerShape(20.dp),
                tonalElevation = 2.dp,
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(16.dp)) {
                    DetailRow("Name", item?.name ?: "")
                    DetailRow("Serial/ID", item?.serialId ?: "")
                    DetailRow("Description", item?.description ?: "")
                    DetailRow("Condition", item?.condition ?: "")
                    DetailRow("Status", item?.status ?: "")
                    DetailRow("Value", "R${item?.value ?: 0.0}")

                    // Show checkout info if item is checked out
                    if (item?.status == "Checked Out" && checkoutUserName != null) {
                        Spacer(Modifier.height(8.dp))
                        Surface(
                            color = Color(0xFF0A6CFF).copy(alpha = 0.15f),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Outlined.Person,
                                    contentDescription = null,
                                    tint = Color(0xFF0A6CFF),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Column {
                                    Text(
                                        "Checked out to:",
                                        color = Muted,
                                        fontSize = 12.sp
                                    )
                                    Text(
                                        checkoutUserName!!,
                                        color = OnDark,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                    }

                    if (item?.permanentCheckout == true) DetailRow("Permanent Checkout", "Yes")
                    if (item?.permissionNeeded == true) DetailRow("Permission Needed", "Yes")
                    if (item?.driversLicenseNeeded == true) DetailRow("Driver's License", "Required")
                }
            }

            Spacer(Modifier.height(16.dp))

            /* ---------- Action Buttons ---------- */
            Button(
                onClick = { showConfirmDialog = true },
                enabled = !isProcessing,
                colors = ButtonDefaults.buttonColors(
                    containerColor = DangerRed,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) {
                if (isProcessing) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(Icons.Outlined.Delete, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Delete Item", fontWeight = FontWeight.SemiBold)
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

        /* ---------- Confirmation Dialog ---------- */
        if (showConfirmDialog) {
            AlertDialog(
                onDismissRequest = {
                    if (!isProcessing) {
                        showConfirmDialog = false
                    }
                },
                title = { Text("Confirm Deletion", color = OnDark, fontWeight = FontWeight.Bold) },
                text = {
                    Text(
                        "Are you absolutely sure you want to delete \"${item?.name}\"? This action cannot be undone.",
                        color = Muted
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            scope.launch {
                                try {
                                    isProcessing = true
                                    val result = firebaseRepo.deleteItem(itemId)
                                    if (result.isSuccess) {
                                        Toast.makeText(ctx, "Item deleted successfully", Toast.LENGTH_SHORT).show()
                                        onBack()
                                    } else {
                                        Toast.makeText(ctx, "Failed to delete item: ${result.exceptionOrNull()?.message}", Toast.LENGTH_LONG).show()
                                        isProcessing = false
                                        showConfirmDialog = false
                                    }
                                } catch (e: Exception) {
                                    Toast.makeText(ctx, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                                    isProcessing = false
                                    showConfirmDialog = false
                                }
                            }
                        },
                        enabled = !isProcessing
                    ) {
                        Text("Delete", color = DangerRed, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showConfirmDialog = false },
                        enabled = !isProcessing
                    ) {
                        Text("Cancel", color = OnDark)
                    }
                },
                containerColor = CardDark,
                icon = {
                    Icon(Icons.Outlined.Warning, contentDescription = null, tint = DangerRed)
                }
            )
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Muted, fontSize = 14.sp)
        Text(value, color = OnDark, fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}

/* ---------- Preview ---------- */
@Preview(showBackground = true, backgroundColor = 0xFF0E1116, widthDp = 412, heightDp = 900)
@Composable
private fun PreviewItemDelete() {
    MaterialTheme {
        ItemDeleteScreen(itemId = "item1")
    }
}
