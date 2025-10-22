package com.example.phoenixinventory.core

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.phoenixinventory.data.FirebaseRepository
import com.example.phoenixinventory.data.InventoryItem
import com.example.phoenixinventory.ui.theme.AppColors
import kotlinx.coroutines.launch

/**
 * Statistics Screen: Lost/Damaged/Deleted Items Report
 *
 * Comprehensive report showing all problematic items and their total value loss.
 *
 * Features:
 * - Large red card showing total lost value
 * - Breakdown by category (Lost/Damaged vs Deleted)
 * - Each item shows status badge with appropriate icon and color
 * - Separate sections for active problem items and deleted items
 * - Values displayed in Rands (R)
 * - Admin/Manager only screen
 *
 * This helps track inventory shrinkage and financial impact
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewLostDamagedDeletedScreen(
    onBack: () -> Unit = {}
) {
    val backgroundColor = AppColors.Carbon
    val surfaceColor = AppColors.Charcoal
    val cardColor = AppColors.CardDark
    val onSurfaceColor = AppColors.OnDark
    val mutedColor = AppColors.Muted
    val primaryContainerColor = AppColors.PrimaryContainer

    val firebaseRepo = remember { FirebaseRepository() }
    val scope = rememberCoroutineScope()

    var stolenLostDamagedItems by remember { mutableStateOf<List<InventoryItem>>(emptyList()) }
    var deletedItems by remember { mutableStateOf<List<InventoryItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var totalValue by remember { mutableStateOf(0.0) }

    // Load problem items from both collections
    LaunchedEffect(Unit) {
        scope.launch {
            isLoading = true
            // Fetch items marked as Stolen, Lost, or Damaged
            stolenLostDamagedItems = firebaseRepo.getStolenLostDamagedItems().getOrNull() ?: emptyList()
            // Fetch soft-deleted items
            deletedItems = firebaseRepo.getDeletedItems().getOrNull() ?: emptyList()

            // Calculate total financial impact
            totalValue = stolenLostDamagedItems.sumOf { it.value } + deletedItems.sumOf { it.value }
            isLoading = false
        }
    }

    val allItems = stolenLostDamagedItems + deletedItems

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(backgroundColor, surfaceColor, backgroundColor)))
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
                ) { Icon(Icons.Outlined.ReportProblem, contentDescription = null, tint = onSurfaceColor) }

                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Text("Lost/Damaged/Deleted", color = onSurfaceColor, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text("${allItems.size} items", color = mutedColor, fontSize = 13.sp)
                }
            }

            Spacer(Modifier.height(16.dp))

            /* ---------- Total Value Card ---------- */
            Surface(
                color = Color(0xFFEF4444),
                shape = RoundedCornerShape(20.dp),
                tonalElevation = 4.dp,
                shadowElevation = 4.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Total Lost Value",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "R${"%.2f".format(totalValue)}",
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            /* ---------- Category Stats ---------- */
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CategoryStatCard(
                    title = "Lost/Damaged",
                    count = stolenLostDamagedItems.size,
                    modifier = Modifier.weight(1f)
                )
                CategoryStatCard(
                    title = "Deleted",
                    count = deletedItems.size,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(16.dp))

            /* ---------- Items List ---------- */
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = onSurfaceColor)
                }
            } else if (allItems.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Outlined.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF10B981),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(Modifier.height(12.dp))
                        Text("No lost, damaged, or deleted items", color = mutedColor, fontSize = 16.sp)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Lost/Damaged/Stolen items
                    if (stolenLostDamagedItems.isNotEmpty()) {
                        item {
                            Text(
                                text = "Lost/Damaged/Stolen Items",
                                color = onSurfaceColor,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                        items(stolenLostDamagedItems.sortedByDescending { it.value }) { item ->
                            LostDamagedDeletedItemCard(item = item, isDeleted = false)
                        }
                    }

                    // Deleted items
                    if (deletedItems.isNotEmpty()) {
                        item {
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = "Deleted Items",
                                color = onSurfaceColor,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                        items(deletedItems.sortedByDescending { it.value }) { item ->
                            LostDamagedDeletedItemCard(item = item, isDeleted = true)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryStatCard(
    title: String,
    count: Int,
    modifier: Modifier = Modifier
) {
    val surfaceColor = AppColors.Charcoal
    val onSurfaceColor = AppColors.OnDark
    val mutedColor = AppColors.Muted

    Surface(
        color = surfaceColor,
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 2.dp,
        shadowElevation = 2.dp,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = count.toString(),
                color = onSurfaceColor,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = title,
                color = mutedColor,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun LostDamagedDeletedItemCard(
    item: InventoryItem,
    isDeleted: Boolean
) {
    val surfaceColor = AppColors.Charcoal
    val onSurfaceColor = AppColors.OnDark
    val mutedColor = AppColors.Muted

    val statusColor = when {
        isDeleted -> Color(0xFF6B7280)
        item.status == "Stolen" -> Color(0xFFEF4444)
        item.status == "Lost" -> Color(0xFFF59E0B)
        item.status == "Damaged" -> Color(0xFFEF4444)
        else -> Color(0xFF6B7280)
    }

    val statusIcon = when {
        isDeleted -> Icons.Outlined.Delete
        item.status == "Stolen" -> Icons.Outlined.ReportProblem
        item.status == "Lost" -> Icons.Outlined.SearchOff
        item.status == "Damaged" -> Icons.Outlined.BrokenImage
        else -> Icons.Outlined.Help
    }

    Surface(
        color = surfaceColor,
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 2.dp,
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        text = item.name,
                        color = onSurfaceColor,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Serial: ${item.serialId}",
                        color = mutedColor,
                        fontSize = 13.sp
                    )
                }
                Icon(
                    statusIcon,
                    contentDescription = null,
                    tint = statusColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Status
                Surface(
                    color = statusColor.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = if (isDeleted) "Deleted" else item.status,
                        color = statusColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                // Value
                Text(
                    text = "R${"%.2f".format(item.value)}",
                    color = Color(0xFFEF4444),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
