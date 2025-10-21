package com.example.phoenixinventory.core

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.phoenixinventory.data.FirebaseRepository
import com.example.phoenixinventory.data.InventoryItem
import com.example.phoenixinventory.ui.theme.AppColors
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewDeletedItemsScreen(
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val firebaseRepo = remember { FirebaseRepository() }
    val scope = rememberCoroutineScope()

    val backgroundColor = AppColors.Carbon
    val surfaceColor = AppColors.Charcoal
    val cardColor = AppColors.CardDark
    val onSurfaceColor = AppColors.OnDark
    val mutedColor = AppColors.Muted
    val primaryContainerColor = AppColors.PrimaryContainer

    var deletedItems by remember { mutableStateOf<List<InventoryItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var showRestoreDialog by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf<InventoryItem?>(null) }

    // Load deleted items
    LaunchedEffect(Unit) {
        scope.launch {
            isLoading = true
            val result = firebaseRepo.getDeletedItems()
            deletedItems = result.getOrNull() ?: emptyList()
            isLoading = false
        }
    }

    if (showRestoreDialog && selectedItem != null) {
        RestoreConfirmationDialog(
            item = selectedItem!!,
            onConfirm = {
                scope.launch {
                    val result = firebaseRepo.restoreItem(selectedItem!!.id)
                    if (result.isSuccess) {
                        Toast.makeText(context, "Item restored successfully", Toast.LENGTH_SHORT).show()
                        // Reload the list
                        val refreshResult = firebaseRepo.getDeletedItems()
                        deletedItems = refreshResult.getOrNull() ?: emptyList()
                    } else {
                        Toast.makeText(context, "Failed to restore item", Toast.LENGTH_SHORT).show()
                    }
                    showRestoreDialog = false
                    selectedItem = null
                }
            },
            onDismiss = {
                showRestoreDialog = false
                selectedItem = null
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header
            Surface(
                color = cardColor,
                shape = RoundedCornerShape(20.dp),
                tonalElevation = 2.dp,
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
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
                    ) {
                        Icon(Icons.Outlined.Delete, contentDescription = null, tint = onSurfaceColor)
                    }
                    Spacer(Modifier.width(10.dp))
                    Column(Modifier.weight(1f)) {
                        Text("Deleted Items", color = onSurfaceColor, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Text("${deletedItems.size} items", color = mutedColor, fontSize = 13.sp)
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Items List
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = primaryContainerColor)
                }
            } else if (deletedItems.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Outlined.CheckCircle,
                            contentDescription = null,
                            tint = mutedColor,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            "No deleted items",
                            color = mutedColor,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(deletedItems) { item ->
                        DeletedItemCard(
                            item = item,
                            onRestore = {
                                selectedItem = item
                                showRestoreDialog = true
                            },
                            surfaceColor = surfaceColor,
                            onSurfaceColor = onSurfaceColor,
                            mutedColor = mutedColor
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DeletedItemCard(
    item: InventoryItem,
    onRestore: () -> Unit,
    surfaceColor: Color,
    onSurfaceColor: Color,
    mutedColor: Color
) {
    Surface(
        color = surfaceColor,
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 2.dp,
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    item.name,
                    color = onSurfaceColor,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "Serial: ${item.serialId}",
                    color = mutedColor,
                    fontSize = 13.sp
                )
                Spacer(Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(
                        color = AppColors.PrimaryContainer.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            item.condition,
                            color = onSurfaceColor,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Surface(
                        color = Color(0xFFEF4444).copy(alpha = 0.3f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            "Deleted",
                            color = onSurfaceColor,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.width(12.dp))

            Button(
                onClick = onRestore,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF10B981),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    Icons.Outlined.Restore,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text("Restore", fontSize = 14.sp)
            }
        }
    }
}

@Composable
private fun RestoreConfirmationDialog(
    item: InventoryItem,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Restore Item", color = AppColors.OnDark) },
        text = {
            Text(
                "Are you sure you want to restore '${item.name}'? It will be marked as Available.",
                color = AppColors.Muted
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Restore", color = Color(0xFF10B981))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = AppColors.OnDark)
            }
        },
        containerColor = AppColors.CardDark
    )
}
