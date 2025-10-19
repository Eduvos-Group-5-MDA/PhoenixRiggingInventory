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
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.phoenixinventory.data.DataRepository
import com.example.phoenixinventory.ui.theme.AppColors
import kotlinx.coroutines.launch
import java.util.Locale

/* ---------- Models ---------- */
enum class UserRole { Guest, Employee }
private enum class PwdStrength { Weak, Medium, Strong }

/* ---------- Screen ---------- */
@Composable
fun RegisterScreen(
    onBack: () -> Unit,
    onRegistered: () -> Unit,
    onGoToLogin: () -> Unit
) {
    val backgroundColor = AppColors.Carbon
    val surfaceColor = AppColors.Charcoal
    val cardColor = AppColors.CardDark
    val onSurfaceColor = AppColors.OnDark
    val mutedColor = AppColors.Muted
    val primaryColor = AppColors.Primary
    val primaryContainerColor = AppColors.PrimaryContainer
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()

    // Inputs
    var first by remember { mutableStateOf("") }
    var last by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var idNumber by remember { mutableStateOf("") }
    var role by remember { mutableStateOf<UserRole?>(null) } // no default selection
    var company by remember { mutableStateOf("") }
    var hasDriverLicense by remember { mutableStateOf<Boolean?>(null) }
    var password by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }
    var showPwd by remember { mutableStateOf(false) }
    var showConfirm by remember { mutableStateOf(false) }
    var acceptTerms by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var generalError by remember { mutableStateOf<String?>(null) }

    // Errors
    var firstErr by remember { mutableStateOf<String?>(null) }
    var lastErr by remember { mutableStateOf<String?>(null) }
    var emailErr by remember { mutableStateOf<String?>(null) }
    var phoneErr by remember { mutableStateOf<String?>(null) }
    var pwdErr by remember { mutableStateOf<String?>(null) }
    var confirmErr by remember { mutableStateOf<String?>(null) }
    var termsErr by remember { mutableStateOf<String?>(null) }

    fun validate(): Boolean {
        firstErr = if (first.isBlank()) "Required" else null
        lastErr = if (last.isBlank()) "Required" else null
        emailErr = if (email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())
            "Enter a valid email" else null
        phoneErr = if (phone.isBlank() || phone.filter(Char::isDigit).length < 7) "Enter a valid phone" else null
        pwdErr = passwordError(password)
        confirmErr = if (confirm != password) "Passwords do not match" else null
        termsErr = if (!acceptTerms) "You must accept the terms" else null

        return listOf(firstErr, lastErr, emailErr, phoneErr, pwdErr, confirmErr, termsErr).all { it == null }
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
                .widthIn(max = 640.dp)
                .align(Alignment.TopCenter)
                .clip(RoundedCornerShape(28.dp))
                .background(cardColor)
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, bottom = 8.dp)
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
                ) { Icon(Icons.Outlined.Build, null, tint = onSurfaceColor) }
                Spacer(Modifier.width(8.dp))
                Text("Register", color = onSurfaceColor, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            }

            Surface(
                shape = RoundedCornerShape(20.dp),
                color = surfaceColor,
                tonalElevation = 2.dp,
                shadowElevation = 2.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Create Account", color = onSurfaceColor, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "Join Phoenix Rigging management\nsystem",
                        color = mutedColor, fontSize = 14.sp, textAlign = TextAlign.Center, lineHeight = 18.sp
                    )

                    Spacer(Modifier.height(18.dp))

                    generalError?.let {
                        Text(it, color = Color(0xFFFF4C4C), fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(12.dp))
                    }

                    LabeledField(
                        label = "Name",
                        value = first,
                        onValueChange = { first = it.capitalizeWords() },
                        placeholder = "First name",
                        error = firstErr,
                        keyboard = KeyboardOptions(imeAction = ImeAction.Next),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(12.dp))

                    LabeledField(
                        label = "Surname",
                        value = last,
                        onValueChange = { last = it.capitalizeWords() },
                        placeholder = "Last name",
                        error = lastErr,
                        keyboard = KeyboardOptions(imeAction = ImeAction.Next),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(12.dp))

                    LabeledField(
                        label = "Email",
                        value = email,
                        onValueChange = { email = it.trim() },
                        placeholder = "Enter your email",
                        error = emailErr,
                        keyboard = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(12.dp))

                    LabeledField(
                        label = "Phone",
                        value = phone,
                        onValueChange = { phone = it },
                        placeholder = "e.g. 071 234 5678",
                        error = phoneErr,
                        keyboard = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(12.dp))

                    LabeledField(
                        label = "Employee ID",
                        value = idNumber,
                        onValueChange = { idNumber = it },
                        placeholder = "Employee or ID number",
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(12.dp))

                    DropdownField(
                        label = "Role",
                        options = UserRole.values().toList(),
                        selected = role,
                        onSelected = { role = it },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(12.dp))

                    LabeledField(
                        label = "Company",
                        value = company,
                        onValueChange = { company = it },
                        placeholder = "Company name (optional)",
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(12.dp))

                    LabeledToggle(
                        label = "Driver's license",
                        state = hasDriverLicense,
                        onStateChange = { hasDriverLicense = it }
                    )

                    Spacer(Modifier.height(12.dp))

                    PasswordField(
                        label = "Password",
                        value = password,
                        onValueChange = { password = it },
                        error = pwdErr,
                        showPassword = showPwd,
                        onToggleVisibility = { showPwd = !showPwd }
                    )

                    Spacer(Modifier.height(12.dp))

                    PasswordField(
                        label = "Confirm Password",
                        value = confirm,
                        onValueChange = { confirm = it },
                        error = confirmErr,
                        showPassword = showConfirm,
                        onToggleVisibility = { showConfirm = !showConfirm }
                    )

                    Spacer(Modifier.height(12.dp))

                    TermsCheckbox(
                        checked = acceptTerms,
                        onCheckedChange = {
                            acceptTerms = it
                            termsErr = null
                        },
                        error = termsErr,
                        onTermsClicked = { /* could open terms */ }
                    )

                    Spacer(Modifier.height(18.dp))

                    Button(
                        onClick = {
                            if (validate() && !isLoading) {
                                generalError = null
                                isLoading = true
                                scope.launch {
                                    val result = DataRepository.register(
                                        firstName = first.trim(),
                                        lastName = last.trim(),
                                        email = email.trim(),
                                        password = password,
                                        role = when (role) {
                                            UserRole.Employee -> "Employee"
                                            UserRole.Guest, null -> "Employee"
                                        },
                                        phone = phone.trim().ifBlank { null },
                                        company = company.trim().ifBlank { null },
                                        hasDriverLicense = hasDriverLicense == true,
                                        employeeId = idNumber.trim().ifBlank { null }
                                    )
                                    isLoading = false
                                    result.onSuccess {
                                        Toast.makeText(ctx, "Account created", Toast.LENGTH_SHORT).show()
                                        onRegistered()
                                    }.onFailure { error ->
                                        generalError = error.message ?: "Failed to register"
                                    }
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = primaryColor, contentColor = onSurfaceColor),
                        shape = RoundedCornerShape(16.dp),
                        enabled = !isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                    ) {
                        Icon(Icons.Outlined.PersonAdd, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text(if (isLoading) "Creating..." else "Create Account", fontWeight = FontWeight.SemiBold)
                    }

                    Spacer(Modifier.height(16.dp))

                    val loginText = buildAnnotatedString {
                        append("Already have an account? ")
                        pushStyle(SpanStyle(color = primaryContainerColor, fontWeight = FontWeight.Bold))
                        append("Login")
                        pop()
                    }

                    Text(
                        text = loginText,
                        color = mutedColor,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = onGoToLogin)
                    )
                }
            }
        }
    }
}

@Composable
private fun LabeledField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    error: String? = null,
    keyboard: KeyboardOptions = KeyboardOptions.Default,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(label, color = AppColors.OnDark, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        Spacer(Modifier.height(6.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            placeholder = { Text(placeholder, color = AppColors.Muted) },
            isError = error != null,
            keyboardOptions = keyboard,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AppColors.PrimaryContainer,
                unfocusedBorderColor = AppColors.PrimaryContainer.copy(alpha = 0.6f),
                cursorColor = AppColors.OnDark,
                focusedTextColor = AppColors.OnDark,
                unfocusedTextColor = AppColors.OnDark,
                errorBorderColor = Color(0xFFFF4C4C)
            ),
            supportingText = {
                error?.let { Text(it, color = Color(0xFFFF4C4C)) }
            },
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DropdownField(
    label: String,
    options: List<UserRole>,
    selected: UserRole?,
    onSelected: (UserRole) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(label, color = AppColors.OnDark, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        Spacer(Modifier.height(6.dp))
        var expanded by remember { mutableStateOf(false) }
        OutlinedTextField(
            readOnly = true,
            value = selected?.name ?: "Select role",
            onValueChange = {},
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .menuAnchor(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AppColors.PrimaryContainer,
                unfocusedBorderColor = AppColors.PrimaryContainer.copy(alpha = 0.6f),
                focusedTextColor = AppColors.OnDark,
                unfocusedTextColor = AppColors.OnDark
            ),
            label = { Text("Role", color = AppColors.Muted) },
            supportingText = {}
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.name, color = AppColors.OnDark) },
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
private fun LabeledToggle(
    label: String,
    state: Boolean?,
    onStateChange: (Boolean?) -> Unit
) {
    Column {
        Text(label, color = AppColors.OnDark, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        Spacer(Modifier.height(6.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(selected = state == true, onClick = { onStateChange(true) }, label = { Text("Yes") })
            FilterChip(selected = state == false, onClick = { onStateChange(false) }, label = { Text("No") })
        }
    }
}

@Composable
private fun PasswordField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    error: String?,
    showPassword: Boolean,
    onToggleVisibility: () -> Unit
) {
    Column {
        Text(label, color = AppColors.OnDark, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        Spacer(Modifier.height(6.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            placeholder = { Text("Enter $label".lowercase()) },
            isError = error != null,
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = onToggleVisibility) {
                    Icon(
                        imageVector = if (showPassword) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                        contentDescription = "Toggle password",
                        tint = AppColors.OnDark
                    )
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AppColors.PrimaryContainer,
                unfocusedBorderColor = AppColors.PrimaryContainer.copy(alpha = 0.6f),
                cursorColor = AppColors.OnDark,
                focusedTextColor = AppColors.OnDark,
                unfocusedTextColor = AppColors.OnDark,
                errorBorderColor = Color(0xFFFF4C4C)
            ),
            supportingText = {
                error?.let { Text(it, color = Color(0xFFFF4C4C)) }
            },
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
        )
    }
}

@Composable
private fun TermsCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    error: String?,
    onTermsClicked: () -> Unit
) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = checked, onCheckedChange = onCheckedChange)
            Spacer(Modifier.width(6.dp))
            Text(
                text = "I accept the terms and privacy policy",
                color = AppColors.OnDark,
                modifier = Modifier.clickable(onClick = onTermsClicked),
                textDecoration = TextDecoration.Underline
            )
        }
        error?.let { Text(it, color = Color(0xFFFF4C4C)) }
    }
}

private fun String.capitalizeWords(): String = split(" ").joinToString(" ") { word ->
    word.lowercase(Locale.getDefault()).replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
}

private fun passwordError(password: String): String? = when (passwordStrength(password)) {
    PwdStrength.Weak -> "Password too weak"
    else -> null
}

private fun passwordStrength(password: String): PwdStrength {
    var score = 0
    if (password.length >= 8) score++
    if (password.any(Char::isDigit)) score++
    if (password.any { it.isUpperCase() } && password.any { it.isLowerCase() }) score++
    return when (score) {
        0, 1 -> PwdStrength.Weak
        2 -> PwdStrength.Medium
        else -> PwdStrength.Strong
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewRegister() {
    MaterialTheme {
        RegisterScreen(onBack = {}, onRegistered = {}, onGoToLogin = {})
    }
}
