package com.example.phoenixinventory.core

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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.FlowRow
import com.example.phoenixinventory.data.DataRepository
import com.example.phoenixinventory.data.FirebaseRepository
import com.example.phoenixinventory.data.InventoryItem
import com.example.phoenixinventory.ui.theme.AppColors
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewAllItemsScreen(
    onBack: () -> Unit = {},
    onItemClick: (String) -> Unit = {}
) {
    val backgroundColor = AppColors.Carbon
    val surfaceColor = AppColors.Charcoal
    val cardColor = AppColors.CardDark
    val onSurfaceColor = AppColors.OnDark
    val mutedColor = AppColors.Muted
    val primaryColor = AppColors.Primary
    val primaryContainerColor = AppColors.PrimaryContainer

    val firebaseRepo = remember { FirebaseRepository() }
    val scope = rememberCoroutineScope()

    var searchQuery by remember { mutableStateOf("") }
    var filterStatus by remember { mutableStateOf("All") }
    var filterCategory by remember { mutableStateOf("All") }
    var items by remember { mutableStateOf<List<InventoryItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        scope.launch {
            isLoading = true
            items = firebaseRepo.getAllItems().getOrNull() ?: emptyList()
            isLoading = false
        }
    }

    val filteredItems = items.filter { item ->
        val matchesSearch = item.name.contains(searchQuery, ignoreCase = true) ||
                item.serialId.contains(searchQuery, ignoreCase = true) ||
                item.description.contains(searchQuery, ignoreCase = true)
        val matchesStatus = filterStatus == "All" || item.status == filterStatus
        val matchesCategory = filterCategory == "All" || item.category == filterCategory
        matchesSearch && matchesStatus && matchesCategory
    }

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
                ) { Icon(Icons.Outlined.Inventory, contentDescription = null, tint = onSurfaceColor) }

                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Text("All Items", color = onSurfaceColor, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text("${filteredItems.size} items", color = mutedColor, fontSize = 13.sp)
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

            /* ---------- Status Filter Chips ---------- */
            Text("Status", color = mutedColor, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(6.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                listOf("All", "Available", "Checked Out", "Under Maintenance", "Damaged").forEach { status ->
                    FilterChip(
                        selected = filterStatus == status,
                        onClick = { filterStatus = status },
                        label = { Text(status, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = primaryContainerColor,
                            selectedLabelColor = onSurfaceColor,
                            containerColor = surfaceColor,
                            labelColor = mutedColor
                        )
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            /* ---------- Category Filter Chips ---------- */
            Text("Category", color = mutedColor, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(6.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                listOf("All", "Power Tools", "Hand Tools", "Rigging Equipment", "Vehicle", "Miscellaneous").forEach { category ->
                    FilterChip(
                        selected = filterCategory == category,
                        onClick = { filterCategory = category },
                        label = { Text(category, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = primaryContainerColor,
                            selectedLabelColor = onSurfaceColor,
                            containerColor = surfaceColor,
                            labelColor = mutedColor
                        )
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            /* ---------- Items List ---------- */
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
    val surfaceColor = AppColors.Charcoal
    val onSurfaceColor = AppColors.OnDark
    val mutedColor = AppColors.Muted
    val primaryContainerColor = AppColors.PrimaryContainer
    Surface(
        onClick = onClick,
        color = surfaceColor,
        shape = RoundedCornerShape(20.dp),
        tonalElevation = 2.dp,
        shadowElevation = 2.dp,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 90.dp)
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
                    .background(primaryContainerColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.Build, contentDescription = null, tint = onSurfaceColor)
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(item.name, color = onSurfaceColor, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                Text("ID: ${item.serialId}", color = mutedColor, fontSize = 12.sp)
                Text(item.status, color = getStatusColor(item.status), fontSize = 12.sp, fontWeight = FontWeight.Medium)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("R${item.value}", color = onSurfaceColor, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                Text(item.condition, color = mutedColor, fontSize = 12.sp)
            }
        }
    }
}

private fun getStatusColor(status: String): Color {
    return when (status) {
        "Available" -> Color(0xFF17C964)
        "Checked Out" -> Color(0xFF0A6CFF)
        "Under Maintenance" -> Color(0xFFF5A524)
        "Damaged", "Lost", "Stolen" -> Color(0xFFEF4444)
        else -> Color(0xFFBFC8D4)
    }
}

/* ---------- Preview ---------- */
@Preview(showBackground = true, backgroundColor = 0xFF0E1116, widthDp = 412, heightDp = 900)
@Composable
private fun PreviewViewAllItems() {
    MaterialTheme {
        ViewAllItemsScreen()
    }
}
