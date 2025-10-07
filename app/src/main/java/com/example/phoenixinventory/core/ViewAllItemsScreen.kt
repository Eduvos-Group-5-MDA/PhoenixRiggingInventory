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
import com.example.phoenixinventory.data.DataRepository
import com.example.phoenixinventory.data.InventoryItem

/* ---------- Palette ---------- */
private val Carbon = Color(0xFF0E1116)
private val Charcoal = Color(0xFF151A21)
private val CardDark = Color(0xFF1A2028)
private val OnDark = Color(0xFFE7EBF2)
private val Muted = Color(0xFFBFC8D4)
private val Primary = Color(0xFF0A0C17)
private val PrimaryContainer = Color(0xFF121729)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewAllItemsScreen(
    onBack: () -> Unit = {},
    onItemClick: (String) -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    var filterStatus by remember { mutableStateOf("All") }
    val items = remember { DataRepository.getAllItems() }

    val filteredItems = items.filter { item ->
        val matchesSearch = item.name.contains(searchQuery, ignoreCase = true) ||
                item.serialId.contains(searchQuery, ignoreCase = true) ||
                item.description.contains(searchQuery, ignoreCase = true)
        val matchesFilter = filterStatus == "All" || item.status == filterStatus
        matchesSearch && matchesFilter
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
                ) { Icon(Icons.Outlined.Inventory, contentDescription = null, tint = OnDark) }

                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Text("All Items", color = OnDark, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text("${filteredItems.size} items", color = Muted, fontSize = 13.sp)
                }
            }

            Spacer(Modifier.height(16.dp))

            /* ---------- Search Bar ---------- */
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search items...", color = Muted) },
                leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null, tint = OnDark) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryContainer,
                    unfocusedBorderColor = PrimaryContainer.copy(alpha = 0.6f),
                    cursorColor = OnDark,
                    focusedTextColor = OnDark,
                    unfocusedTextColor = OnDark
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
                listOf("All", "Available", "Checked Out", "Under Maintenance", "Damaged").forEach { status ->
                    FilterChip(
                        selected = filterStatus == status,
                        onClick = { filterStatus = status },
                        label = { Text(status, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PrimaryContainer,
                            selectedLabelColor = OnDark,
                            containerColor = Charcoal,
                            labelColor = Muted
                        )
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            /* ---------- Items List ---------- */
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

@Composable
private fun ItemCard(
    item: InventoryItem,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = Charcoal,
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
                    .background(PrimaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.Build, contentDescription = null, tint = OnDark)
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(item.name, color = OnDark, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                Text("ID: ${item.serialId}", color = Muted, fontSize = 12.sp)
                Text(item.status, color = getStatusColor(item.status), fontSize = 12.sp, fontWeight = FontWeight.Medium)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("$${item.value}", color = OnDark, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                Text(item.condition, color = Muted, fontSize = 12.sp)
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
