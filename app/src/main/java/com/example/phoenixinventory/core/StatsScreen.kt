package com.example.phoenixinventory.core

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Statistics Hub Screen
 *
 * Central hub for accessing advanced statistics and reports.
 * This screen is only accessible to Admin and Manager roles.
 *
 * Available reports:
 * 1. Items Out 30+ Days - Overdue item tracking
 * 2. Total Value Report - Complete inventory valuation
 * 3. Lost/Damaged/Deleted - Problem items and shrinkage report
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    onItemsOut30DaysClick: () -> Unit,
    onTotalValueClick: () -> Unit,
    onLostDamagedDeletedClick: () -> Unit,
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
                    text = "View Stats",
                    color = onSurfaceColor,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(22.dp))
            Text(
                text = "Statistics Reports",
                color = onSurfaceColor,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )

            Spacer(Modifier.height(12.dp))

            /* ---------- Action Rows ---------- */
            StatsActionRow(
                title = "Items Out 30+ Days",
                subtitle = "View all items checked out for over 30 days",
                icon = Icons.Outlined.Schedule,
                iconBg = Color(0xFFEF4444),
                onClick = onItemsOut30DaysClick
            )

            StatsActionRow(
                title = "Total Value Report",
                subtitle = "View all items with their values and total",
                icon = Icons.Outlined.AttachMoney,
                iconBg = Color(0xFF10B981),
                onClick = onTotalValueClick
            )

            StatsActionRow(
                title = "Lost/Damaged/Deleted Items",
                subtitle = "View all lost, damaged, and deleted items with total value",
                icon = Icons.Outlined.ReportProblem,
                iconBg = Color(0xFFF59E0B),
                onClick = onLostDamagedDeletedClick
            )

            Spacer(Modifier.height(16.dp))
            Text(
                text = "These reports provide insights into your inventory status and value.",
                color = mutedColor,
                fontSize = 12.sp
            )
        }
    }
}

/* ---------- Reusable Row ---------- */
@Composable
private fun StatsActionRow(
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
private fun PreviewStats() {
    MaterialTheme {
        StatsScreen(
            onItemsOut30DaysClick = {},
            onTotalValueClick = {},
            onLostDamagedDeletedClick = {},
            onBack = {}
        )
    }
}
