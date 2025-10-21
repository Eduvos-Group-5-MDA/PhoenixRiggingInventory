package com.example.phoenixinventory.core

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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.phoenixinventory.data.FirebaseRepository
import com.example.phoenixinventory.data.InventoryItem
import com.example.phoenixinventory.ui.theme.AppColors
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemDetailScreen(
    itemId: String,
    onBack: () -> Unit = {}
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
    var item by remember { mutableStateOf<InventoryItem?>(null) }
    var checkoutUserName by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(itemId) {
        scope.launch {
            isLoading = true
            item = firebaseRepo.getItemById(itemId).getOrNull()

            // Get checkout info if item is checked out
            if (item?.status == "Checked Out") {
                val checkoutDetail = firebaseRepo.getCurrentCheckout(itemId).getOrNull()
                checkoutUserName = checkoutDetail?.user?.name
            }

            isLoading = false
        }
    }

    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(backgroundColor, surfaceColor, backgroundColor))),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = primaryContainerColor)
        }
        return
    }

    if (item == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(backgroundColor, surfaceColor, backgroundColor))),
            contentAlignment = Alignment.Center
        ) {
            Text("Item not found", color = onSurfaceColor)
        }
        return
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
                    Text("Item Details", color = onSurfaceColor, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text("View mode", color = mutedColor, fontSize = 13.sp)
                }
            }

            Spacer(Modifier.height(16.dp))

            /* ---------- Item Card ---------- */
            Surface(
                color = surfaceColor,
                shape = RoundedCornerShape(20.dp),
                tonalElevation = 2.dp,
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(16.dp)) {
                    DetailRow("Name", item?.name ?: "")
                    DetailRow("Serial/ID", item?.serialId ?: "")
                    DetailRow("Description", item?.description ?: "")
                    DetailRow("Category", item?.category ?: "")
                    DetailRow("Condition", item?.condition ?: "")
                    DetailRow("Status", item?.status ?: "")
                    DetailRow("Value", "R${item?.value ?: 0.0}")

                    // Show checkout info if item is checked out
                    android.util.Log.d("ItemDetailScreen", "Display check - Status: ${item?.status}, checkoutUserName: $checkoutUserName")
                    if (item?.status == "Checked Out" && checkoutUserName != null) {
                        Spacer(Modifier.height(8.dp))
                        Surface(
                            color = Color(0xFF0A6CFF).copy(alpha = 0.15f),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Outlined.Person,
                                    contentDescription = null,
                                    tint = Color(0xFF0A6CFF),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Column {
                                    Text(
                                        "Checked out to:",
                                        color = mutedColor,
                                        fontSize = 12.sp
                                    )
                                    Text(
                                        checkoutUserName!!,
                                        color = onSurfaceColor,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                    }

                    if (item?.permanentCheckout == true) DetailRow("Permanent Checkout", "Yes")
                    if (item?.permissionNeeded == true) DetailRow("Permission Needed", "Yes")
                    if (item?.driversLicenseNeeded == true) DetailRow("Driver's License", "Required")
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    val onSurfaceColor = AppColors.OnDark
    val mutedColor = AppColors.Muted
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = mutedColor, fontSize = 14.sp)
        Text(value, color = onSurfaceColor, fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}

/* ---------- Preview ---------- */
@Preview(showBackground = true, backgroundColor = 0xFF0E1116, widthDp = 412, heightDp = 900)
@Composable
private fun PreviewItemDetail() {
    MaterialTheme {
        ItemDetailScreen(itemId = "item1")
    }
}
