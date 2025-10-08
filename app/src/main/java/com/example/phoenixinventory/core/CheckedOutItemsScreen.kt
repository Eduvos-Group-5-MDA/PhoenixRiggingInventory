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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.phoenixinventory.data.CheckedOutItemDetail
import com.example.phoenixinventory.data.DataRepository

/* ---------- Palette ---------- */
private val Carbon = Color(0xFF0E1116)
private val Charcoal = Color(0xFF151A21)
private val CardDark = Color(0xFF1A2028)
private val OnDark = Color(0xFFE7EBF2)
private val Muted = Color(0xFFBFC8D4)
private val Primary = Color(0xFF0A0C17)
private val PrimaryContainer = Color(0xFF121729)

@Composable
fun CheckedOutItemsScreen(
    onBack: () -> Unit = {},
    onItemClick: (String) -> Unit = {}
) {
    val checkedOutItems = remember { DataRepository.getCheckedOutItems() }

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
                ) { Icon(Icons.Outlined.Assignment, contentDescription = null, tint = OnDark) }

                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Text("Checked Out", color = OnDark, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text("${checkedOutItems.size} items currently out", color = Muted, fontSize = 13.sp)
                }
            }

            Spacer(Modifier.height(16.dp))

            /* ---------- Items List ---------- */
            if (checkedOutItems.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Outlined.CheckCircle,
                            contentDescription = null,
                            tint = Muted,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(Modifier.height(16.dp))
                        Text("No items checked out", color = Muted, fontSize = 16.sp)
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(checkedOutItems) { detail ->
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
    Surface(
        onClick = onClick,
        color = Charcoal,
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
                        .background(PrimaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Outlined.Build, contentDescription = null, tint = OnDark)
                }
                Spacer(Modifier.width(14.dp))
                Column(Modifier.weight(1f)) {
                    Text(detail.item.name, color = OnDark, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    Text("ID: ${detail.item.serialId}", color = Muted, fontSize = 12.sp)
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
                    Text("Checked out by:", color = Muted, fontSize = 12.sp)
                    Text(detail.user.name, color = OnDark, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Days out:", color = Muted, fontSize = 12.sp)
                    Text(
                        "${detail.daysOut} days",
                        color = if (detail.daysOut >= 30) Color(0xFFEF4444) else OnDark,
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
