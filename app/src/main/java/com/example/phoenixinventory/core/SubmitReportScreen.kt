package com.example.phoenixinventory.core

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Report
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
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubmitReportScreen(
    userName: String,
    userEmail: String,
    userId: String,
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

    var reportTitle by remember { mutableStateOf("") }
    var reportDescription by remember { mutableStateOf("") }
    var reportCategory by remember { mutableStateOf("Issue") }
    var reportPriority by remember { mutableStateOf("Medium") }
    var isSubmitting by remember { mutableStateOf(false) }

    val categories = listOf("Issue", "Suggestion", "Bug", "Other")
    val priorities = listOf("Low", "Medium", "High", "Critical")

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
                    Icon(Icons.Outlined.Report, contentDescription = null, tint = onSurfaceColor)
                }
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Text("Submit a Report", color = onSurfaceColor, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text("Report issues, bugs, or suggestions", color = mutedColor, fontSize = 13.sp)
                }
            }

            Spacer(Modifier.height(16.dp))

            // Form
            Surface(
                color = surfaceColor,
                shape = RoundedCornerShape(20.dp),
                tonalElevation = 2.dp,
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(16.dp)) {
                    // Title
                    Text("Title *", color = onSurfaceColor, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(6.dp))
                    OutlinedTextField(
                        value = reportTitle,
                        onValueChange = { reportTitle = it },
                        placeholder = { Text("Brief summary of the issue", color = mutedColor) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = onSurfaceColor,
                            unfocusedTextColor = onSurfaceColor,
                            focusedBorderColor = primaryContainerColor,
                            unfocusedBorderColor = primaryContainerColor.copy(alpha = 0.6f),
                            cursorColor = onSurfaceColor
                        ),
                        shape = RoundedCornerShape(14.dp),
                        singleLine = true
                    )

                    Spacer(Modifier.height(12.dp))

                    // Category
                    Text("Category *", color = onSurfaceColor, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        categories.forEach { category ->
                            FilterChip(
                                selected = reportCategory == category,
                                onClick = { reportCategory = category },
                                label = { Text(category, fontSize = 13.sp) },
                                modifier = Modifier.weight(1f),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = primaryContainerColor,
                                    selectedLabelColor = onSurfaceColor
                                )
                            )
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    // Priority
                    Text("Priority *", color = onSurfaceColor, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        priorities.forEach { priority ->
                            FilterChip(
                                selected = reportPriority == priority,
                                onClick = { reportPriority = priority },
                                label = { Text(priority, fontSize = 13.sp) },
                                modifier = Modifier.weight(1f),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = when (priority) {
                                        "Critical" -> Color(0xFFEF4444)
                                        "High" -> Color(0xFFF97316)
                                        "Medium" -> Color(0xFFF59E0B)
                                        else -> primaryContainerColor
                                    },
                                    selectedLabelColor = onSurfaceColor
                                )
                            )
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    // Description
                    Text("Description *", color = onSurfaceColor, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(6.dp))
                    OutlinedTextField(
                        value = reportDescription,
                        onValueChange = { reportDescription = it },
                        placeholder = { Text("Describe the issue in detail...", color = mutedColor) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = onSurfaceColor,
                            unfocusedTextColor = onSurfaceColor,
                            focusedBorderColor = primaryContainerColor,
                            unfocusedBorderColor = primaryContainerColor.copy(alpha = 0.6f),
                            cursorColor = onSurfaceColor
                        ),
                        shape = RoundedCornerShape(14.dp)
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Submit Button
            Button(
                onClick = {
                    if (reportTitle.isNotBlank() && reportDescription.isNotBlank()) {
                        isSubmitting = true
                        scope.launch {
                            try {
                                val report = Report(
                                    title = reportTitle.trim(),
                                    description = reportDescription.trim(),
                                    category = reportCategory,
                                    priority = reportPriority,
                                    userId = userId,
                                    userName = userName,
                                    userEmail = userEmail
                                )
                                val result = firebaseRepo.createReport(report)
                                if (result.isSuccess) {
                                    Toast.makeText(context, "Report submitted successfully", Toast.LENGTH_SHORT).show()
                                    onBack()
                                } else {
                                    Toast.makeText(context, "Failed to submit report", Toast.LENGTH_LONG).show()
                                }
                            } catch (e: Exception) {
                                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                            } finally {
                                isSubmitting = false
                            }
                        }
                    }
                },
                enabled = reportTitle.isNotBlank() && reportDescription.isNotBlank() && !isSubmitting,
                colors = ButtonDefaults.buttonColors(
                    containerColor = primaryColor,
                    contentColor = onSurfaceColor
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(
                        color = onSurfaceColor,
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(Icons.Outlined.Report, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Submit Report", fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(Modifier.height(12.dp))

            // Cancel Button
            OutlinedButton(
                onClick = onBack,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = onSurfaceColor),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Text("Cancel")
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}
