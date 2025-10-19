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
import com.example.phoenixinventory.data.DataRepository
import kotlinx.coroutines.flow.collectAsState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserEditScreen(
    userId: String,
    onBack: () -> Unit = {}
) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()

    val users by DataRepository.usersFlow().collectAsState()
    val user = remember(users, userId) { users.find { it.id == userId } }
    var fetchError by remember { mutableStateOf<String?>(null) }
    var isSaving by remember { mutableStateOf(false) }

    LaunchedEffect(userId) {
        if (user == null) {
            val fetched = DataRepository.getUserById(userId)
            if (fetched == null) {
                fetchError = "User not found"
            }
        }
    }

    val backgroundColor = MaterialTheme.colorScheme.background
    val surfaceColor = MaterialTheme.colorScheme.surface
    val cardColor = MaterialTheme.colorScheme.secondary
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val mutedColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
    val primaryContainerColor = MaterialTheme.colorScheme.tertiary

    var editName by remember { mutableStateOf(user?.name ?: "") }
    var editEmail by remember { mutableStateOf(user?.email ?: "") }
    var editRole by remember { mutableStateOf(user?.role ?: "Employee") }

    LaunchedEffect(user) {
        if (user != null) {
            editName = user.name
            editEmail = user.email
            editRole = user.role
        }
    }

    val roles = listOf("Admin", "Manager", "Employee")

    if (user == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor),
            contentAlignment = Alignment.Center
        ) {
            if (fetchError != null) {
                Text(fetchError ?: "User not found", color = onSurfaceColor)
            } else {
                CircularProgressIndicator(color = onSurfaceColor)
            }
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
                    DropdownField("Role *", roles, editRole, { editRole = it }, onSurfaceColor, mutedColor, primaryContainerColor)
                }
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    if (editName.isBlank() || editEmail.isBlank()) {
                        Toast.makeText(ctx, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    scope.launch {
                        isSaving = true
                        val updatedUser = user.copy(
                            name = editName.trim(),
                            email = editEmail.trim(),
                            role = editRole
                        )
                        val result = DataRepository.updateUser(updatedUser)
                        isSaving = false
                        result.onSuccess {
                            Toast.makeText(ctx, "User updated", Toast.LENGTH_SHORT).show()
                            onBack()
                        }.onFailure { error ->
                            Toast.makeText(ctx, error.message ?: "Failed to update user", Toast.LENGTH_LONG).show()
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = primaryContainerColor,
                    contentColor = onSurfaceColor
                ),
                enabled = !isSaving,
                shape = RoundedCornerShape(16.dp),
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
                colors = ButtonDefaults.outlinedButtonColors(contentColor = onSurfaceColor),
                shape = RoundedCornerShape(16.dp),
                enabled = !isSaving,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
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
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
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
    Text(label, color = onSurfaceColor, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
    Spacer(Modifier.height(6.dp))
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(
            readOnly = true,
            value = selected,
            onValueChange = {},
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = primaryContainerColor,
                unfocusedBorderColor = primaryContainerColor.copy(alpha = 0.6f),
                focusedTextColor = onSurfaceColor,
                unfocusedTextColor = onSurfaceColor
            ),
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option, color = onSurfaceColor) },
                    onClick = {
                        onSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

/* ---------- Preview ---------- */
@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun PreviewUserEdit() {
    MaterialTheme {
        UserEditScreen(userId = "user1")
    }
}
