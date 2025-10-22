package com.example.phoenixinventory.core

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.NavigateNext
import androidx.compose.material.icons.outlined.Restore
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Item management hub screen (Admin/Manager only).
 * Central access point for adding, editing, deleting, and restoring inventory items.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageScreen(
    onAddClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onViewDeletedClick: () -> Unit = {},
    onBack: () -> Unit = {}
) {
    val backgroundColor = MaterialTheme.colorScheme.background
    val surfaceColor = MaterialTheme.colorScheme.surface
    val cardColor = MaterialTheme.colorScheme.secondary
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val mutedColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
    val accentColor = Color(0xFFFF6F00)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
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
                    Icon(
                        imageVector = Icons.Outlined.ArrowBack,
                        contentDescription = "Back",
                        tint = onSurfaceColor
                    )
                }
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Manage Items",
                    color = onSurfaceColor,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(22.dp))
            Text(
                text = "Actions",
                color = onSurfaceColor,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )

            Spacer(Modifier.height(12.dp))

            /* ---------- Action Rows ---------- */
            ManageActionRow(
                title = "Add Item",
                subtitle = "Create a new item record",
                icon = Icons.Outlined.AddCircle,
                iconBg = accentColor,
                onClick = onAddClick
            )

            ManageActionRow(
                title = "Edit Item",
                subtitle = "Update an item's details",
                icon = Icons.Outlined.Build,
                iconBg = accentColor,
                onClick = onEditClick
            )

            ManageActionRow(
                title = "Delete Item",
                subtitle = "Remove an item from the inventory",
                icon = Icons.Outlined.Delete,
                iconBg = accentColor,
                onClick = onDeleteClick
            )

            ManageActionRow(
                title = "View Deleted Items",
                subtitle = "View and restore deleted items",
                icon = Icons.Outlined.Restore,
                iconBg = Color(0xFFFF6F00),
                onClick = onViewDeletedClick
            )

            Spacer(Modifier.height(16.dp))
            Text(
                text = "Tip: You can manage all inventory actions here or from the Dashboard.",
                color = mutedColor,
                fontSize = 12.sp
            )
        }
    }
}

/* ---------- Reusable Row ---------- */
@Composable
private fun ManageActionRow(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconBg: Color,
    onClick: () -> Unit
) {
    val surfaceColor = MaterialTheme.colorScheme.surface
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val mutedColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)

    Surface(
        onClick = onClick,
        color = surfaceColor,
        shape = RoundedCornerShape(24.dp),
        tonalElevation = 2.dp,
        shadowElevation = 2.dp,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 84.dp)
            .padding(vertical = 6.dp)
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
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = Color.White)
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(title, color = onSurfaceColor, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                Text(subtitle, color = mutedColor, fontSize = 13.sp)
            }
            Icon(Icons.Outlined.NavigateNext, contentDescription = null, tint = onSurfaceColor)
        }
    }
}

/* ---------- Preview ---------- */
@Preview(showBackground = true, backgroundColor = 0xFF0E1116, widthDp = 412, heightDp = 900)
@Composable
private fun PreviewManage() {
    MaterialTheme {
        ManageScreen(
            onAddClick = {},
            onEditClick = {},
            onDeleteClick = {},
            onViewDeletedClick = {},
            onBack = {}
        )
    }
}
