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
import com.example.phoenixinventory.data.CheckedOutItemDetail
import com.example.phoenixinventory.data.DataRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckedOutItemsScreen(
    onBack: () -> Unit = {},
    onItemClick: (String) -> Unit = {}
) {
    val backgroundColor = MaterialTheme.colorScheme.background
    val surfaceColor = MaterialTheme.colorScheme.surface
    val cardColor = MaterialTheme.colorScheme.secondary
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val mutedColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
    val primaryContainerColor = MaterialTheme.colorScheme.tertiary

    var searchQuery by remember { mutableStateOf("") }
    var filterDaysOut by remember { mutableStateOf("All") }

    val allCheckedOutItems by DataRepository.checkedOutItemsFlow().collectAsState()

    // Filter items based on search and filter criteria
    val filteredItems = allCheckedOutItems.filter { detail ->
        val matchesSearch = detail.item.name.contains(searchQuery, ignoreCase = true) ||
                detail.item.serialId.contains(searchQuery, ignoreCase = true) ||
                detail.item.description.contains(searchQuery, ignoreCase = true) ||
                detail.user.name.contains(searchQuery, ignoreCase = true)

        val matchesFilter = when (filterDaysOut) {
            "All" -> true
            "30+ Days" -> detail.daysOut >= 30
            "Under 30 Days" -> detail.daysOut < 30
            else -> true
        }

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
                        .background(primaryContainerColor),
                    contentAlignment = Alignment.Center
                ) { Icon(Icons.Outlined.Assignment, contentDescription = null, tint = onSurfaceColor) }

                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Text("Checked Out Items", color = onSurfaceColor, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text("${filteredItems.size} items", color = mutedColor, fontSize = 13.sp)
                }
            }

            Spacer(Modifier.height(16.dp))

            /* ---------- Search Bar ---------- */
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search items or users...", color = mutedColor) },
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
                listOf("All", "30+ Days", "Under 30 Days").forEach { filter ->
                    FilterChip(
                        selected = filterDaysOut == filter,
                        onClick = { filterDaysOut = filter },
                        label = { Text(filter, fontSize = 12.sp) },
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
                            Icons.Outlined.CheckCircle,
                            contentDescription = null,
                            tint = mutedColor,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            if (searchQuery.isEmpty() && filterDaysOut == "All") "No items checked out" else "No items found",
                            color = mutedColor,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                        if (searchQuery.isNotEmpty() || filterDaysOut != "All") {
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
                    items(filteredItems) { detail ->
                        CheckedOutItemCard(detail = detail, onClick = { onItemClick(detail.item.id) })
                    }
                }
            }
        }
    }
}

@Composable
private fun CheckedOutItemCard(
    detail: CheckedOutItemDetail,
    onClick: () -> Unit
) {
    val surfaceColor = MaterialTheme.colorScheme.surface
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val mutedColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
    val primaryContainerColor = MaterialTheme.colorScheme.tertiary

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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
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
                    Text(detail.item.name, color = onSurfaceColor, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    Text("ID: ${detail.item.serialId}", color = mutedColor, fontSize = 12.sp)
                }
                // Warning badge for items out 30+ days
                if (detail.daysOut >= 30) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFEF4444)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "30+ days",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Checked out by:", color = mutedColor, fontSize = 12.sp)
                    Text(detail.user.name, color = onSurfaceColor, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Days out:", color = mutedColor, fontSize = 12.sp)
                    Text(
                        "${detail.daysOut} days",
                        color = if (detail.daysOut >= 30) Color(0xFFEF4444) else onSurfaceColor,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

/* ---------- Preview ---------- */
@Preview(showBackground = true, backgroundColor = 0xFF0E1116, widthDp = 412, heightDp = 900)
@Composable
private fun PreviewCheckedOutItems() {
    MaterialTheme {
        CheckedOutItemsScreen()
    }
}
