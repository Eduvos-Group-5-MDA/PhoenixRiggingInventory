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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.phoenixinventory.data.FirebaseRepository
import com.example.phoenixinventory.data.User
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserEditScreen(
    userId: String,
    onBack: () -> Unit = {}
) {
    val ctx = LocalContext.current
    val firebaseRepo = remember { FirebaseRepository() }
    val scope = rememberCoroutineScope()

    var user by remember { mutableStateOf<User?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var isProcessing by remember { mutableStateOf(false) }

    val backgroundColor = MaterialTheme.colorScheme.background
    val surfaceColor = MaterialTheme.colorScheme.surface
    val cardColor = MaterialTheme.colorScheme.secondary
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val mutedColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
    val primaryContainerColor = MaterialTheme.colorScheme.tertiary

    // Edit state
    var editName by remember { mutableStateOf("") }
    var editEmail by remember { mutableStateOf("") }
    var editRole by remember { mutableStateOf("Employee") }
    var editPhone by remember { mutableStateOf("") }
    var editIdNumber by remember { mutableStateOf("") }
    var editCompany by remember { mutableStateOf("") }
    var editHasDriverLicense by remember { mutableStateOf(false) }

    val roles = listOf("Admin", "Manager", "Employee", "Guest")

    LaunchedEffect(userId) {
        scope.launch {
            isLoading = true
            val result = firebaseRepo.getUserById(userId).getOrNull()
            user = result
            result?.let {
                editName = it.name
                editEmail = it.email
                editRole = it.role
                editPhone = it.phone
                editIdNumber = it.idNumber ?: ""
                editCompany = it.company ?: ""
                editHasDriverLicense = it.hasDriverLicense ?: false
            }
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

    if (user == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor),
            contentAlignment = Alignment.Center
        ) {
            Text("User not found", color = onSurfaceColor)
        }
        return
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
                ) { Icon(Icons.Outlined.Edit, contentDescription = null, tint = onSurfaceColor) }

                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Text("Edit User", color = onSurfaceColor, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text("Update user details", color = mutedColor, fontSize = 13.sp)
                }
            }

            Spacer(Modifier.height(16.dp))

            /* ---------- Edit Form ---------- */
            Surface(
                color = surfaceColor,
                shape = RoundedCornerShape(20.dp),
                tonalElevation = 2.dp,
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(16.dp)) {
                    LabeledField("Name *", editName, { editName = it }, "User's full name", onSurfaceColor, mutedColor, primaryContainerColor)
                    Spacer(Modifier.height(12.dp))
                    LabeledField("Email *", editEmail, { editEmail = it }, "user@example.com", onSurfaceColor, mutedColor, primaryContainerColor, KeyboardType.Email)
                    Spacer(Modifier.height(12.dp))
                    LabeledField("Phone", editPhone, { editPhone = it }, "+1 234 567 8900", onSurfaceColor, mutedColor, primaryContainerColor, KeyboardType.Phone)
                    Spacer(Modifier.height(12.dp))
                    LabeledField("ID Number", editIdNumber, { editIdNumber = it }, "ID or employee number", onSurfaceColor, mutedColor, primaryContainerColor)
                    Spacer(Modifier.height(12.dp))
                    LabeledField("Company", editCompany, { editCompany = it }, "Company name", onSurfaceColor, mutedColor, primaryContainerColor)
                    Spacer(Modifier.height(12.dp))
                    DropdownField("Role *", roles, editRole, { editRole = it }, onSurfaceColor, mutedColor, primaryContainerColor)
                    Spacer(Modifier.height(12.dp))
                    LabeledCheckbox(editHasDriverLicense, { editHasDriverLicense = it }, "Has Driver's License", onSurfaceColor)
                }
            }

            Spacer(Modifier.height(16.dp))

            /* ---------- Action Buttons ---------- */
            Button(
                onClick = {
                    if (editName.isBlank() || editEmail.isBlank()) {
                        Toast.makeText(ctx, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    scope.launch {
                        try {
                            isProcessing = true
                            val updatedUser = user!!.copy(
                                name = editName.trim(),
                                email = editEmail.trim(),
                                role = editRole,
                                phone = editPhone.trim(),
                                idNumber = if (editIdNumber.isBlank()) null else editIdNumber.trim(),
                                company = if (editCompany.isBlank()) null else editCompany.trim(),
                                hasDriverLicense = editHasDriverLicense
                            )
                            val result = firebaseRepo.updateUser(updatedUser)
                            if (result.isSuccess) {
                                Toast.makeText(ctx, "User updated", Toast.LENGTH_SHORT).show()
                                onBack()
                            } else {
                                Toast.makeText(ctx, "Failed to update user: ${result.exceptionOrNull()?.message}", Toast.LENGTH_LONG).show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(ctx, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                        } finally {
                            isProcessing = false
                        }
                    }
                },
                enabled = !isProcessing,
                colors = ButtonDefaults.buttonColors(
                    containerColor = primaryContainerColor,
                    contentColor = onSurfaceColor
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) {
                if (isProcessing) {
                    CircularProgressIndicator(
                        color = onSurfaceColor,
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(Icons.Outlined.CheckCircle, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Save Changes", fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(Modifier.height(12.dp))

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

@Composable
private fun LabeledField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    onSurfaceColor: Color,
    mutedColor: Color,
    primaryContainerColor: Color,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Text(label, color = onSurfaceColor, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
    Spacer(Modifier.height(6.dp))
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        placeholder = { Text(placeholder, color = mutedColor) },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = ImeAction.Next),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = primaryContainerColor,
            unfocusedBorderColor = primaryContainerColor.copy(alpha = 0.6f),
            cursorColor = onSurfaceColor,
            focusedTextColor = onSurfaceColor,
            unfocusedTextColor = onSurfaceColor
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
    onSelected: (String) -> Unit,
    onSurfaceColor: Color,
    mutedColor: Color,
    primaryContainerColor: Color
) {
    var expanded by remember { mutableStateOf(false) }
    Text(label, color = onSurfaceColor, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
    Spacer(Modifier.height(6.dp))

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(
            readOnly = true,
            value = selected,
            onValueChange = {},
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth().clip(RoundedCornerShape(14.dp)),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = primaryContainerColor,
                unfocusedBorderColor = primaryContainerColor.copy(alpha = 0.6f),
                focusedTextColor = onSurfaceColor,
                unfocusedTextColor = onSurfaceColor
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
    label: String,
    onSurfaceColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
        Spacer(Modifier.width(8.dp))
        Text(label, color = onSurfaceColor, fontSize = 14.sp)
    }
}

/* ---------- Preview ---------- */
@Preview(showBackground = true, backgroundColor = 0xFF0E1116, widthDp = 412, heightDp = 900)
@Composable
private fun PreviewUserEdit() {
    MaterialTheme {
        UserEditScreen(userId = "user1")
    }
}
