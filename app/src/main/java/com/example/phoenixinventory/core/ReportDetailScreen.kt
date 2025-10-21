package com.example.phoenixinventory.core

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.phoenixinventory.data.FirebaseRepository
import com.example.phoenixinventory.data.Report
import com.example.phoenixinventory.ui.theme.AppColors
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportDetailScreen(
    reportId: String,
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
    val primaryColor = AppColors.Primary
    val primaryContainerColor = AppColors.PrimaryContainer

    var report by remember { mutableStateOf<Report?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var isProcessing by remember { mutableStateOf(false) }
    var currentUserName by remember { mutableStateOf("") }
    var currentUserId by remember { mutableStateOf("") }

    val dateFormat = remember { SimpleDateFormat("EEEE, MMM d, yyyy 'at' h:mm a", Locale.getDefault()) }

    // Load report and current user info
    LaunchedEffect(reportId) {
        scope.launch {
            isLoading = true

            // Get current user info
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            currentUserId = userId ?: ""
            val user = firebaseRepo.getUserById(currentUserId).getOrNull()
            currentUserName = user?.name ?: ""

            // Load report
            val result = firebaseRepo.getReportById(reportId)
            report = result.getOrNull()
            isLoading = false
        }
    }

    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = primaryContainerColor)
        }
        return
    }

    if (report == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.Outlined.Error,
                    contentDescription = null,
                    tint = mutedColor,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(Modifier.height(16.dp))
                Text("Report not found", color = mutedColor, fontSize = 18.sp)
                Spacer(Modifier.height(16.dp))
                Button(onClick = onBack) {
                    Text("Go Back")
                }
            }
        }
        return
    }

    val currentReport = report!!

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
            // Header
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
                    Icon(Icons.Outlined.Description, contentDescription = null, tint = onSurfaceColor)
                }
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Text("Report Details", color = onSurfaceColor, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text(currentReport.status, color = mutedColor, fontSize = 13.sp)
                }
            }

            Spacer(Modifier.height(16.dp))

            // Report Content
            Surface(
                color = surfaceColor,
                shape = RoundedCornerShape(20.dp),
                tonalElevation = 2.dp,
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(16.dp)) {
                    // Title
                    Text(
                        currentReport.title,
                        color = onSurfaceColor,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.height(12.dp))

                    // Status badge
                    Surface(
                        color = if (currentReport.status == "Resolved") Color(0xFF10B981) else Color(0xFFF59E0B),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                if (currentReport.status == "Resolved") Icons.Outlined.CheckCircle else Icons.Outlined.PendingActions,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                currentReport.status,
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    Divider(color = mutedColor.copy(alpha = 0.2f))

                    Spacer(Modifier.height(16.dp))

                    // Metadata
                    InfoRow("Category", currentReport.category, onSurfaceColor, mutedColor)
                    Spacer(Modifier.height(12.dp))
                    InfoRow("Priority", currentReport.priority, onSurfaceColor, mutedColor)
                    Spacer(Modifier.height(12.dp))
                    InfoRow("Submitted By", currentReport.userName, onSurfaceColor, mutedColor)
                    Spacer(Modifier.height(12.dp))
                    InfoRow("Email", currentReport.userEmail, onSurfaceColor, mutedColor)
                    Spacer(Modifier.height(12.dp))
                    currentReport.createdAt?.let {
                        InfoRow("Submitted On", dateFormat.format(it), onSurfaceColor, mutedColor)
                        Spacer(Modifier.height(12.dp))
                    }

                    if (currentReport.status == "Resolved") {
                        currentReport.resolvedByName?.let {
                            InfoRow("Resolved By", it, onSurfaceColor, mutedColor)
                            Spacer(Modifier.height(12.dp))
                        }
                        currentReport.resolvedAt?.let {
                            InfoRow("Resolved On", dateFormat.format(it), onSurfaceColor, mutedColor)
                            Spacer(Modifier.height(12.dp))
                        }
                    }

                    Divider(color = mutedColor.copy(alpha = 0.2f))

                    Spacer(Modifier.height(16.dp))

                    // Description
                    Text("Description", color = onSurfaceColor, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        currentReport.description,
                        color = mutedColor,
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Action Buttons
            if (currentReport.status == "Unresolved") {
                Button(
                    onClick = {
                        isProcessing = true
                        scope.launch {
                            try {
                                val result = firebaseRepo.updateReportStatus(
                                    reportId = reportId,
                                    status = "Resolved",
                                    resolvedBy = currentUserId,
                                    resolvedByName = currentUserName
                                )
                                if (result.isSuccess) {
                                    Toast.makeText(context, "Report marked as resolved", Toast.LENGTH_SHORT).show()
                                    onBack()
                                } else {
                                    Toast.makeText(context, "Failed to update report", Toast.LENGTH_SHORT).show()
                                }
                            } catch (e: Exception) {
                                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                            } finally {
                                isProcessing = false
                            }
                        }
                    },
                    enabled = !isProcessing,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF10B981),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth().height(52.dp)
                ) {
                    if (isProcessing) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(Icons.Outlined.CheckCircle, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Mark as Resolved", fontWeight = FontWeight.SemiBold)
                    }
                }
            } else {
                Button(
                    onClick = {
                        isProcessing = true
                        scope.launch {
                            try {
                                val result = firebaseRepo.updateReportStatus(
                                    reportId = reportId,
                                    status = "Unresolved",
                                    resolvedBy = null,
                                    resolvedByName = null
                                )
                                if (result.isSuccess) {
                                    Toast.makeText(context, "Report marked as unresolved", Toast.LENGTH_SHORT).show()
                                    onBack()
                                } else {
                                    Toast.makeText(context, "Failed to update report", Toast.LENGTH_SHORT).show()
                                }
                            } catch (e: Exception) {
                                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                            } finally {
                                isProcessing = false
                            }
                        }
                    },
                    enabled = !isProcessing,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFF59E0B),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth().height(52.dp)
                ) {
                    if (isProcessing) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(Icons.Outlined.PendingActions, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Mark as Unresolved", fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // Back Button
            OutlinedButton(
                onClick = onBack,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = onSurfaceColor),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Text("Back to Reports")
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun InfoRow(
    label: String,
    value: String,
    onSurfaceColor: Color,
    mutedColor: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            label,
            color = mutedColor,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )
        Text(
            value,
            color = onSurfaceColor,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
