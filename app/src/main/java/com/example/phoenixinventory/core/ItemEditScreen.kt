package com.example.phoenixinventory.core

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import kotlinx.coroutines.flow.collectAsState
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.phoenixinventory.data.DataRepository

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
fun ItemEditScreen(
    itemId: String,
    onBack: () -> Unit = {}
) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    val items by DataRepository.itemsFlow().collectAsState()
    val item = remember(items, itemId) { items.find { it.id == itemId } }

    var fetchError by remember { mutableStateOf<String?>(null) }

    // Edit state
    var editName by remember { mutableStateOf("") }
    var editSerial by remember { mutableStateOf("") }
    var editDesc by remember { mutableStateOf("") }
    var editCondition by remember { mutableStateOf("Good") }
    var editStatus by remember { mutableStateOf("Available") }
    var editValue by remember { mutableStateOf("0") }
    var editPermanent by remember { mutableStateOf(false) }
    var editPermission by remember { mutableStateOf(false) }
    var editLicense by remember { mutableStateOf(false) }
    var isSaving by remember { mutableStateOf(false) }

    val conditions = listOf("Excellent", "Good", "Fair", "Poor")
    val statuses = listOf("Available", "Checked Out", "Under Maintenance", "Retired", "Damaged", "Lost", "Stolen")

    LaunchedEffect(itemId) {
        if (item == null) {
            val fetched = DataRepository.getItemById(itemId)
            if (fetched == null) {
                fetchError = "Item not found"
            }
        }
    }

    LaunchedEffect(item) {
        if (item != null) {
            editName = item.name
            editSerial = item.serialId
            editDesc = item.description
            editCondition = item.condition
            editStatus = item.status
            editValue = item.value.toString()
            editPermanent = item.permanentCheckout
            editPermission = item.permissionNeeded
            editLicense = item.driversLicenseNeeded
        }
    }

    if (item == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(Carbon, Charcoal, Carbon))),
            contentAlignment = Alignment.Center
        ) {
            if (fetchError != null) {
                Text(fetchError ?: "Item not found", color = OnDark)
            } else {
                CircularProgressIndicator(color = OnDark)
            }
        }
        return
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
                .verticalScroll(rememberScrollState())
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
                ) { Icon(Icons.Outlined.Edit, contentDescription = null, tint = OnDark) }

                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Text("Edit Item", color = OnDark, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text("Update item details", color = Muted, fontSize = 13.sp)
                }
            }

            Spacer(Modifier.height(16.dp))

            /* ---------- Edit Form ---------- */
            Surface(
                color = Charcoal,
                shape = RoundedCornerShape(20.dp),
                tonalElevation = 2.dp,
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(16.dp)) {
                    LabeledField("Name *", editName, { editName = it }, "Equipment name")
                    Spacer(Modifier.height(12.dp))
                    LabeledField("Serial/ID *", editSerial, { editSerial = it }, "Serial number")
                    Spacer(Modifier.height(12.dp))
                    LabeledTextArea("Description", editDesc, { editDesc = it }, "Description")
                    Spacer(Modifier.height(12.dp))
                    LabeledField("Value ($)", editValue, { editValue = it }, "0.00", KeyboardType.Decimal)
                    Spacer(Modifier.height(12.dp))
                    DropdownField("Condition *", conditions, editCondition) { editCondition = it }
                    Spacer(Modifier.height(12.dp))
                    DropdownField("Status *", statuses, editStatus) { editStatus = it }
                    Spacer(Modifier.height(12.dp))
                    LabeledCheckbox(editPermanent, { editPermanent = it }, "Permanent checkout")
                    LabeledCheckbox(editPermission, { editPermission = it }, "Permission needed")
                    LabeledCheckbox(editLicense, { editLicense = it }, "Driver's license needed")
                }
            }

            Spacer(Modifier.height(16.dp))

            /* ---------- Action Buttons ---------- */
            Button(
                onClick = {
                    val updatedItem = item.copy(
                        name = editName.trim(),
                        serialId = editSerial.trim(),
                        description = editDesc.trim(),
                        condition = editCondition,
                        status = editStatus,
                        value = editValue.toDoubleOrNull() ?: 0.0,
                        permanentCheckout = editPermanent,
                        permissionNeeded = editPermission,
                        driversLicenseNeeded = editLicense
                    )
                    scope.launch {
                        isSaving = true
                        val result = DataRepository.updateItem(updatedItem)
                        isSaving = false
                        result.onSuccess {
                            Toast.makeText(ctx, "Item updated", Toast.LENGTH_SHORT).show()
                            onBack()
                        }.onFailure { error ->
                            Toast.makeText(ctx, error.message ?: "Failed to update item", Toast.LENGTH_LONG).show()
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Primary, contentColor = OnDark),
                shape = RoundedCornerShape(16.dp),
                enabled = !isSaving,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Icon(Icons.Outlined.CheckCircle, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(if (isSaving) "Saving..." else "Save Changes", fontWeight = FontWeight.SemiBold)
            }

            Spacer(Modifier.height(12.dp))

            OutlinedButton(
                onClick = onBack,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = OnDark),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Text("Cancel")
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun LabeledField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Text(label, color = OnDark, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
    Spacer(Modifier.height(6.dp))
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        placeholder = { Text(placeholder) },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = ImeAction.Next),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = PrimaryContainer,
            unfocusedBorderColor = PrimaryContainer.copy(alpha = 0.6f),
            cursorColor = OnDark,
            focusedTextColor = OnDark,
            unfocusedTextColor = OnDark
        ),
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp))
    )
}

@Composable
private fun LabeledTextArea(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String
) {
    Text(label, color = OnDark, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
    Spacer(Modifier.height(6.dp))
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = false,
        minLines = 3,
        placeholder = { Text(placeholder) },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = PrimaryContainer,
            unfocusedBorderColor = PrimaryContainer.copy(alpha = 0.6f),
            cursorColor = OnDark,
            focusedTextColor = OnDark,
            unfocusedTextColor = OnDark
        ),
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp))
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DropdownField(
    label: String,
    options: List<String>,
    selected: String,
    onSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Text(label, color = OnDark, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
    Spacer(Modifier.height(6.dp))

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(
            readOnly = true,
            value = selected,
            onValueChange = {},
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth().clip(RoundedCornerShape(14.dp)),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryContainer,
                unfocusedBorderColor = PrimaryContainer.copy(alpha = 0.6f),
                focusedTextColor = OnDark,
                unfocusedTextColor = OnDark
            )
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun LabeledCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    text: String
) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(checkedColor = OnDark, uncheckedColor = Muted)
        )
        Spacer(Modifier.width(8.dp))
        Text(text, color = OnDark, fontSize = 14.sp)
    }
}

/* ---------- Preview ---------- */
@Preview(showBackground = true, backgroundColor = 0xFF0E1116, widthDp = 412, heightDp = 900)
@Composable
private fun PreviewItemEdit() {
    MaterialTheme {
        ItemEditScreen(itemId = "item1")
    }
}
