package com.example.phoenixinventory.core

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.Inventory
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/* ---------- Palette (same as other screens) ---------- */
private val Carbon = Color(0xFF0E1116)
private val Charcoal = Color(0xFF151A21)
private val CardDark = Color(0xFF1A2028)
private val OnDark = Color(0xFFE7EBF2)
private val Muted = Color(0xFFBFC8D4)
private val Primary = Color(0xFF0A0C17)
private val PrimaryContainer = Color(0xFF121729)

/* ---------- Model you can reuse later ---------- */
data class NewEquipment(
    val name: String,
    val serialId: String,
    val description: String,
    val condition: String,
    val status: String,
    val permanentCheckout: Boolean,
    val permissionNeeded: Boolean,
    val driversLicenseNeeded: Boolean
)

/* ---------- Screen ---------- */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddItemScreen(
    onBack: () -> Unit,
    onSubmit: (NewEquipment) -> Unit,
    onCancel: () -> Unit
) {
    val ctx = LocalContext.current

    // Form state
    var name by remember { mutableStateOf("") }
    var serial by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }

    val conditions = listOf("Excellent", "Good", "Fair", "Poor")
    val statuses = listOf("Available", "Checked Out", "Under Maintenance", "Retired")

    var condition by remember { mutableStateOf(conditions.first()) }
    var status by remember { mutableStateOf(statuses.first()) }

    var permanent by remember { mutableStateOf(false) }
    var permission by remember { mutableStateOf(false) }
    var license by remember { mutableStateOf(false) }

    // Errors
    var nameErr by remember { mutableStateOf<String?>(null) }
    var serialErr by remember { mutableStateOf<String?>(null) }

    fun validate(): Boolean {
        nameErr = if (name.isBlank()) "Required" else null
        serialErr = if (serial.isBlank()) "Required" else null
        return nameErr == null && serialErr == null
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
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
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
                ) { Icon(Icons.Outlined.Add, contentDescription = null, tint = OnDark) }

                Spacer(Modifier.width(10.dp))
                Column {
                    Text("Add Item", color = OnDark, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text("Add new equipment", color = Muted, fontSize = 13.sp)
                }
            }

            Spacer(Modifier.height(16.dp))

            /* ---------- Card ---------- */
            Surface(
                color = Charcoal,
                shape = RoundedCornerShape(20.dp),
                tonalElevation = 2.dp,
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(16.dp)) {

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.Inventory, contentDescription = null, tint = OnDark)
                        Spacer(Modifier.width(8.dp))
                        Text("New Equipment", color = OnDark, fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
                    }
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "Fill out the details for the new rigging equipment",
                        color = Muted,
                        fontSize = 14.sp
                    )

                    Spacer(Modifier.height(18.dp))

                    // Name *
                    LabeledField(
                        label = "Name *",
                        value = name,
                        onValueChange = { name = it },
                        placeholder = "Equipment name",
                        error = nameErr,
                        keyboard = KeyboardOptions(imeAction = ImeAction.Next),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(12.dp))

                    // Serial/ID *
                    LabeledField(
                        label = "Serial/ID *",
                        value = serial,
                        onValueChange = { serial = it.trim() },
                        placeholder = "Serial number or ID",
                        error = serialErr,
                        keyboard = KeyboardOptions(imeAction = ImeAction.Next),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(12.dp))

                    // Description
                    LabeledTextArea(
                        label = "Description",
                        value = desc,
                        onValueChange = { desc = it },
                        placeholder = "Detailed description of the equipment",
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(12.dp))

                    // Condition *
                    DropdownField(
                        label = "Condition *",
                        options = conditions,
                        selected = condition,
                        onSelected = { condition = it }
                    )

                    Spacer(Modifier.height(12.dp))

                    // Status *
                    DropdownField(
                        label = "Status *",
                        options = statuses,
                        selected = status,
                        onSelected = { status = it }
                    )

                    Spacer(Modifier.height(12.dp))

                    // Checkboxes
                    LabeledCheckbox(
                        checked = permanent,
                        onCheckedChange = { permanent = it },
                        text = "Permanent checkout"
                    )
                    LabeledCheckbox(
                        checked = permission,
                        onCheckedChange = { permission = it },
                        text = "Permission needed"
                    )
                    LabeledCheckbox(
                        checked = license,
                        onCheckedChange = { license = it },
                        text = "Driver's license needed"
                    )

                    Spacer(Modifier.height(18.dp))

                    // Add Button
                    Button(
                        onClick = {
                            if (validate()) {
                                val item = NewEquipment(
                                    name = name.trim(),
                                    serialId = serial.trim(),
                                    description = desc.trim(),
                                    condition = condition,
                                    status = status,
                                    permanentCheckout = permanent,
                                    permissionNeeded = permission,
                                    driversLicenseNeeded = license
                                )
                                Toast.makeText(ctx, "Item added (demo)", Toast.LENGTH_SHORT).show()
                                onSubmit(item)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Primary, contentColor = OnDark),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                    ) {
                        Icon(Icons.Outlined.Add, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Add Item", fontWeight = FontWeight.SemiBold)
                    }

                    Spacer(Modifier.height(12.dp))

                    // Cancel
                    OutlinedButton(
                        onClick = onCancel,
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = OnDark),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text("Cancel")
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

/* ---------- Reusable pieces ---------- */

@Composable
private fun LabeledField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    error: String? = null,
    keyboard: KeyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
) {
    Text(label, color = OnDark, fontWeight = FontWeight.SemiBold)
    Spacer(Modifier.height(6.dp))
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        placeholder = { Text(placeholder) },
        isError = error != null,
        supportingText = { error?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
        keyboardOptions = keyboard,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = PrimaryContainer,
            unfocusedBorderColor = PrimaryContainer.copy(alpha = 0.6f),
            cursorColor = OnDark,
            focusedTextColor = OnDark,
            unfocusedTextColor = OnDark,
            focusedPlaceholderColor = Muted,
            unfocusedPlaceholderColor = Muted
        ),
        modifier = modifier.clip(RoundedCornerShape(14.dp))
    )
}

@Composable
private fun LabeledTextArea(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    Text(label, color = OnDark, fontWeight = FontWeight.SemiBold)
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
            unfocusedTextColor = OnDark,
            focusedPlaceholderColor = Muted,
            unfocusedPlaceholderColor = Muted
        ),
        modifier = modifier.clip(RoundedCornerShape(14.dp))
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
    Text(label, color = OnDark, fontWeight = FontWeight.SemiBold)
    Spacer(Modifier.height(6.dp))

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(
            readOnly = true,
            value = selected,
            onValueChange = {},
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp)),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryContainer,
                unfocusedBorderColor = PrimaryContainer.copy(alpha = 0.6f),
                focusedTextColor = OnDark,
                unfocusedTextColor = OnDark,
                focusedPlaceholderColor = Muted,
                unfocusedPlaceholderColor = Muted
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
        Text(text, color = OnDark)
    }
}

/* ---------- Preview ---------- */
@Preview(showBackground = true, backgroundColor = 0xFF0E1116, widthDp = 412, heightDp = 900)
@Composable
private fun PreviewAddItem() {
    MaterialTheme {
        AddItemScreen(
            onBack = {},
            onSubmit = {},
            onCancel = {}
        )
    }
}

