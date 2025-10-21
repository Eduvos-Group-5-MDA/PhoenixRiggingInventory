package com.example.phoenixinventory.core

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.phoenixinventory.data.FirebaseRepository
import com.example.phoenixinventory.data.Report
import com.example.phoenixinventory.ui.theme.AppColors
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewReportsScreen(
    onBack: () -> Unit = {},
    onReportClick: (String) -> Unit = {}
) {
    val firebaseRepo = remember { FirebaseRepository() }
    val scope = rememberCoroutineScope()

    val backgroundColor = AppColors.Carbon
    val surfaceColor = AppColors.Charcoal
    val cardColor = AppColors.CardDark
    val onSurfaceColor = AppColors.OnDark
    val mutedColor = AppColors.Muted
    val primaryColor = AppColors.Primary
    val primaryContainerColor = AppColors.PrimaryContainer

    var reports by remember { mutableStateOf<List<Report>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var filterStatus by remember { mutableStateOf("All") }
    var unresolvedCount by remember { mutableStateOf(0) }

    val dateFormat = remember { SimpleDateFormat("MMM d, yyyy 'at' h:mm a", Locale.getDefault()) }

    // Load reports
    LaunchedEffect(filterStatus) {
        scope.launch {
            isLoading = true
            val result = when (filterStatus) {
                "Unresolved" -> firebaseRepo.getReportsByStatus("Unresolved")
                "Resolved" -> firebaseRepo.getReportsByStatus("Resolved")
                else -> firebaseRepo.getAllReports()
            }
            reports = result.getOrNull() ?: emptyList()

            // Get unresolved count
            val countResult = firebaseRepo.getUnresolvedReportsCount()
            unresolvedCount = countResult.getOrNull() ?: 0

            isLoading = false
        }
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
                Column(Modifier.padding(16.dp)) {
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
                        ) {
                            Icon(Icons.Outlined.Report, contentDescription = null, tint = onSurfaceColor)
                        }
                        Spacer(Modifier.width(10.dp))
                        Column(Modifier.weight(1f)) {
                            Text("Reports", color = onSurfaceColor, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            Text("${reports.size} total • $unresolvedCount unresolved", color = mutedColor, fontSize = 13.sp)
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    // Filter chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = filterStatus == "All",
                            onClick = { filterStatus = "All" },
                            label = { Text("All") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = primaryContainerColor,
                                selectedLabelColor = onSurfaceColor
                            )
                        )
                        FilterChip(
                            selected = filterStatus == "Unresolved",
                            onClick = { filterStatus = "Unresolved" },
                            label = { Text("Unresolved ($unresolvedCount)") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFFF59E0B),
                                selectedLabelColor = Color.White
                            )
                        )
                        FilterChip(
                            selected = filterStatus == "Resolved",
                            onClick = { filterStatus = "Resolved" },
                            label = { Text("Resolved") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF10B981),
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Reports List
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = primaryContainerColor)
                }
            } else if (reports.isEmpty()) {
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
                            "No reports found",
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
                    items(reports) { report ->
                        ReportCard(
                            report = report,
                            dateFormat = dateFormat,
                            onClick = { onReportClick(report.id) },
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
private fun ReportCard(
    report: Report,
    dateFormat: SimpleDateFormat,
    onClick: () -> Unit,
    surfaceColor: Color,
    onSurfaceColor: Color,
    mutedColor: Color
) {
    Surface(
        color = surfaceColor,
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 2.dp,
        shadowElevation = 2.dp,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    report.title,
                    color = onSurfaceColor,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.width(8.dp))
                // Status badge
                Surface(
                    color = if (report.status == "Resolved") Color(0xFF10B981) else Color(0xFFF59E0B),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        report.status,
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // Description preview
            Text(
                report.description,
                color = mutedColor,
                fontSize = 14.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(Modifier.height(12.dp))

            // Metadata row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Category and Priority
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(
                        color = AppColors.PrimaryContainer.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            report.category,
                            color = onSurfaceColor,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Surface(
                        color = when (report.priority) {
                            "Critical" -> Color(0xFFEF4444)
                            "High" -> Color(0xFFF97316)
                            "Medium" -> Color(0xFFF59E0B)
                            else -> Color(0xFF6B7280)
                        }.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            report.priority,
                            color = onSurfaceColor,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                // User and date
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        report.userName,
                        color = mutedColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                    report.createdAt?.let {
                        Text(
                            dateFormat.format(it),
                            color = mutedColor,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }
    }
}
