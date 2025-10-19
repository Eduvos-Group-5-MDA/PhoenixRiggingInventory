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
import kotlinx.coroutines.flow.collectAsState
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
import com.example.phoenixinventory.data.InventoryItem
import com.example.phoenixinventory.ui.theme.AppColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutItemsListScreen(
    onBack: () -> Unit = {},
    onItemClick: (String) -> Unit = {}
) {
    val backgroundColor = MaterialTheme.colorScheme.background
    val surfaceColor = MaterialTheme.colorScheme.surface
    val cardColor = MaterialTheme.colorScheme.secondary
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val mutedColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
    val primaryContainerColor = MaterialTheme.colorScheme.tertiary
    val checkoutBlue = Color(0xFF0A6CFF)

    var searchQuery by remember { mutableStateOf("") }
    var filterCondition by remember { mutableStateOf("All") }

    val allItems by DataRepository.itemsFlow().collectAsState()
    val availableItems = remember(allItems) {
        allItems.filter { it.status == "Available" }
    }

    val filteredItems = availableItems.filter { item ->
        val matchesSearch = item.name.contains(searchQuery, ignoreCase = true) ||
                item.serialId.contains(searchQuery, ignoreCase = true) ||
                item.description.contains(searchQuery, ignoreCase = true)
        val matchesFilter = filterCondition == "All" || item.condition == filterCondition
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
                        .background(checkoutBlue),
                    contentAlignment = Alignment.Center
                ) { Icon(Icons.Outlined.FileUpload, contentDescription = null, tint = Color.White) }

                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Text("Check Out Item", color = onSurfaceColor, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text("${filteredItems.size} items available", color = mutedColor, fontSize = 13.sp)
                }
            }

            Spacer(Modifier.height(16.dp))

            /* ---------- Search Bar ---------- */
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search items...", color = mutedColor) },
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
                listOf("All", "Excellent", "Good", "Fair", "Poor").forEach { condition ->
                    FilterChip(
                        selected = filterCondition == condition,
                        onClick = { filterCondition = condition },
                        label = { Text(condition, fontSize = 12.sp) },
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

            /* ---------- Items List ---------- */
            if (filteredItems.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Outlined.Inventory,
                            contentDescription = null,
                            tint = mutedColor,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            if (searchQuery.isEmpty() && filterCondition == "All") "No items available for checkout" else "No items found",
                            color = mutedColor,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                        if (searchQuery.isNotEmpty() || filterCondition != "All") {
                            Text(
                                "Try adjusting your search or filters",
                                color = mutedColor.copy(alpha = 0.7f),
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredItems) { item ->
                        ItemCard(item = item, onClick = { onItemClick(item.id) })
                    }
                }
            }
        }
    }
}

@Composable
private fun ItemCard(
    item: InventoryItem,
    onClick: () -> Unit
) {
    val surfaceColor = MaterialTheme.colorScheme.surface
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val mutedColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
    val primaryContainerColor = MaterialTheme.colorScheme.tertiary
    val checkoutBlue = Color(0xFF0A6CFF)

    Surface(
        onClick = onClick,
        color = surfaceColor,
        shape = RoundedCornerShape(20.dp),
        tonalElevation = 2.dp,
        shadowElevation = 2.dp,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 100.dp)
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
                    .background(checkoutBlue),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.Build, contentDescription = null, tint = Color.White)
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(item.name, color = onSurfaceColor, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                Text("ID: ${item.serialId}", color = mutedColor, fontSize = 12.sp)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Available", color = Color(0xFF17C964), fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    Text("•", color = mutedColor, fontSize = 12.sp)
                    Text(item.condition, color = mutedColor, fontSize = 12.sp)
                }

                // Show special requirements
                if (item.permissionNeeded || item.driversLicenseNeeded) {
                    Spacer(Modifier.height(4.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Outlined.Warning,
                            contentDescription = null,
                            tint = Color(0xFFF5A524),
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            when {
                                item.permissionNeeded && item.driversLicenseNeeded -> "Permission & License Required"
                                item.permissionNeeded -> "Permission Required"
                                else -> "License Required"
                            },
                            color = Color(0xFFF5A524),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("$${item.value}", color = onSurfaceColor, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(4.dp))
                Icon(
                    Icons.Outlined.ChevronRight,
                    contentDescription = null,
                    tint = mutedColor
                )
            }
        }
    }
}

/* ---------- Preview ---------- */
@Preview(showBackground = true, backgroundColor = 0xFF0E1116, widthDp = 412, heightDp = 900)
@Composable
private fun PreviewCheckoutItemsList() {
    MaterialTheme {
        CheckoutItemsListScreen()
    }
}
