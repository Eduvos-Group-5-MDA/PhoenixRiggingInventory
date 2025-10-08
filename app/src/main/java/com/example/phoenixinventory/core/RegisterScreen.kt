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
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale

/* ---------- Palette ---------- */
private val Carbon = Color(0xFF0E1116)
private val Charcoal = Color(0xFF151A21)
private val CardDark = Color(0xFF1A2028)
private val OnDark = Color(0xFFE7EBF2)
private val Muted = Color(0xFFBFC8D4)
private val Primary = Color(0xFF0A0C17)
private val PrimaryContainer = Color(0xFF121729)

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
    val ctx = LocalContext.current

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
            .background(Brush.verticalGradient(listOf(Carbon, Charcoal, Carbon)))
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
                .widthIn(max = 640.dp)
                .align(Alignment.TopCenter)
                .clip(RoundedCornerShape(28.dp))
                .background(CardDark)
                .padding(16.dp)
        ) {
            /* ---------- Top bar ---------- */
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, bottom = 8.dp)
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
                ) { Icon(Icons.Outlined.Build, null, tint = OnDark) }
                Spacer(Modifier.width(8.dp))
                Text("Register", color = OnDark, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            }

            /* ---------- Card ---------- */
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Charcoal,
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
                    Text("Create Account", color = OnDark, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "Join Phoenix Rigging management\nsystem",
                        color = Muted, fontSize = 14.sp, textAlign = TextAlign.Center, lineHeight = 18.sp
                    )

                    Spacer(Modifier.height(18.dp))

                    /* Name row */
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        LabeledField(
                            label = "Name",
                            value = first,
                            onValueChange = { first = it.capitalizeWords() },
                            placeholder = "First name",
                            error = firstErr,
                            keyboard = KeyboardOptions(imeAction = ImeAction.Next),
                            modifier = Modifier.weight(1f)
                        )
                        LabeledField(
                            label = "Surname",
                            value = last,
                            onValueChange = { last = it.capitalizeWords() },
                            placeholder = "Last name",
                            error = lastErr,
                            keyboard = KeyboardOptions(imeAction = ImeAction.Next),
                            modifier = Modifier.weight(1f)
                        )
                    }

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

                    /* ID Number */
                    LabeledField(
                        label = "ID Number",
                        value = idNumber,
                        onValueChange = { idNumber = it.filter { c -> c.isDigit() }.take(13) },
                        placeholder = "Enter your South African ID number",
                        modifier = Modifier.fillMaxWidth(),
                        keyboard = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next)
                    )

                    Spacer(Modifier.height(12.dp))

                    /* Role */
                    Column(Modifier.fillMaxWidth()) {
                        Text("Role", color = OnDark, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(8.dp))
                        RoleRadio(role = role, onRoleChange = { role = it })
                    }

                    /* Show company name only when Guest selected */
                    if (role == UserRole.Guest) {
                        Spacer(Modifier.height(12.dp))
                        LabeledField(
                            label = "Company Name",
                            value = company,
                            onValueChange = { company = it },
                            placeholder = "Your company (optional)",
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    /* Driver License only for Employees */
                    if (role == UserRole.Employee) {
                        Text("Do you have a valid Driver’s License?", color = OnDark, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(8.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable { hasDriverLicense = true }
                            ) {
                                RadioButton(
                                    selected = hasDriverLicense == true,
                                    onClick = { hasDriverLicense = true },
                                    colors = RadioButtonDefaults.colors(selectedColor = OnDark, unselectedColor = Muted)
                                )
                                Text("Yes", color = OnDark, fontSize = 16.sp)
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable { hasDriverLicense = false }
                            ) {
                                RadioButton(
                                    selected = hasDriverLicense == false,
                                    onClick = { hasDriverLicense = false },
                                    colors = RadioButtonDefaults.colors(selectedColor = OnDark, unselectedColor = Muted)
                                )
                                Text("No", color = OnDark, fontSize = 16.sp)
                            }
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    PasswordField(
                        label = "Password",
                        value = password,
                        onValueChange = { password = it },
                        show = showPwd,
                        onToggle = { showPwd = !showPwd },
                        error = pwdErr
                    )

                    Spacer(Modifier.height(6.dp))
                    PasswordStrengthIndicator(password)

                    Spacer(Modifier.height(12.dp))

                    PasswordField(
                        label = "Confirm Password",
                        value = confirm,
                        onValueChange = { confirm = it },
                        show = showConfirm,
                        onToggle = { showConfirm = !showConfirm },
                        error = confirmErr
                    )

                    Spacer(Modifier.height(12.dp))

                    /* Terms */
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                        Checkbox(
                            checked = acceptTerms,
                            onCheckedChange = { acceptTerms = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = OnDark,
                                uncheckedColor = Muted
                            )
                        )
                        Spacer(Modifier.width(8.dp))
                        val termsText = buildAnnotatedString {
                            append("I accept the ")
                            pushStyle(
                                SpanStyle(
                                    color = OnDark,
                                    fontWeight = FontWeight.SemiBold,
                                    textDecoration = TextDecoration.Underline
                                )
                            )
                            append("Terms & Privacy")
                            pop()
                        }
                        Text(termsText, color = Muted)
                    }
                    if (termsErr != null) {
                        Text(
                            termsErr!!,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp,
                            modifier = Modifier.align(Alignment.Start)
                        )
                    }

                    Spacer(Modifier.height(18.dp))

                    Button(
                        onClick = {
                            if (role == null) {
                                Toast.makeText(ctx, "Please select a role before registering", Toast.LENGTH_SHORT)
                                    .show()
                            } else if (validate()) {
                                Toast.makeText(ctx, "Registered (demo)", Toast.LENGTH_SHORT).show()
                                onRegistered()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Primary, contentColor = OnDark),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth().height(52.dp)
                    ) { Text("Register", fontWeight = FontWeight.SemiBold) }

                    Spacer(Modifier.height(16.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Already have an account? ", color = Muted)
                        Text(
                            "Login",
                            color = OnDark,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.clickable { onGoToLogin() }
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

/* ---------- Reusable Components ---------- */
@Composable
private fun LabeledField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    error: String? = null,
    keyboard: KeyboardOptions = KeyboardOptions.Default
) {
    Text(label, color = OnDark, fontWeight = FontWeight.SemiBold)
    Spacer(Modifier.height(6.dp))
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        placeholder = { Text(placeholder) },
        keyboardOptions = keyboard,
        isError = error != null,
        supportingText = { error?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = PrimaryContainer,
            unfocusedBorderColor = PrimaryContainer.copy(alpha = 0.6f),
            errorBorderColor = MaterialTheme.colorScheme.error,
            cursorColor = OnDark,
            focusedTextColor = OnDark,
            unfocusedTextColor = OnDark,
            errorTextColor = OnDark,
            focusedPlaceholderColor = Muted,
            unfocusedPlaceholderColor = Muted
        ),
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .fillMaxWidth()
    )
}

@Composable
private fun PasswordField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    show: Boolean,
    onToggle: () -> Unit,
    error: String? = null
) {
    Text(label, color = OnDark, fontWeight = FontWeight.SemiBold)
    Spacer(Modifier.height(6.dp))
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        placeholder = { Text(if (label.startsWith("Confirm")) "Confirm password" else "Create password") },
        isError = error != null,
        visualTransformation = if (show) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(onClick = onToggle) {
                Icon(
                    imageVector = if (show) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                    contentDescription = "Toggle password",
                    tint = OnDark
                )
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
        supportingText = { error?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = PrimaryContainer,
            unfocusedBorderColor = PrimaryContainer.copy(alpha = 0.6f),
            errorBorderColor = MaterialTheme.colorScheme.error,
            cursorColor = OnDark,
            focusedTextColor = OnDark,
            unfocusedTextColor = OnDark
        ),
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .fillMaxWidth()
    )
}

@Composable
private fun RoleRadio(
    role: UserRole?,
    onRoleChange: (UserRole) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onRoleChange(UserRole.Guest) }
        ) {
            RadioButton(
                selected = role == UserRole.Guest,
                onClick = { onRoleChange(UserRole.Guest) },
                colors = RadioButtonDefaults.colors(selectedColor = OnDark, unselectedColor = Muted)
            )
            Spacer(Modifier.width(8.dp))
            Text("Guest", color = OnDark, fontSize = 16.sp)
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onRoleChange(UserRole.Employee) }
        ) {
            RadioButton(
                selected = role == UserRole.Employee,
                onClick = { onRoleChange(UserRole.Employee) },
                colors = RadioButtonDefaults.colors(selectedColor = OnDark, unselectedColor = Muted)
            )
            Spacer(Modifier.width(8.dp))
            Text("Employee", color = OnDark, fontSize = 16.sp)
        }
    }
}

/* ---------- Helpers ---------- */
private fun String.capitalizeWords(): String =
    split(" ").joinToString(" ") { it.lowercase().replaceFirstChar { c -> c.titlecase() } }

@Composable
private fun PasswordStrengthIndicator(password: String) {
    val strength = remember(password) { estimateStrength(password) }
    val (label, color) = when (strength) {
        PwdStrength.Weak -> "Weak" to Color(0xFFFF7A7A)
        PwdStrength.Medium -> "Medium" to Color(0xFFFFD166)
        PwdStrength.Strong -> "Strong" to Color(0xFF7AD97A)
    }
    LinearProgressIndicator(
        progress = { ((strength.ordinal + 1) / 3f) },
        color = color,
        trackColor = color.copy(alpha = 0.2f),
        modifier = Modifier
            .fillMaxWidth()
            .height(6.dp)
            .clip(RoundedCornerShape(8.dp))
    )
    Spacer(Modifier.height(4.dp))
    Text("Password strength: $label", color = Muted, fontSize = 12.sp)
}

private fun estimateStrength(pwd: String): PwdStrength {
    var score = 0
    if (pwd.length >= 8) score++
    if (pwd.any { it.isDigit() } && pwd.any { !it.isLetterOrDigit() }) score++
    if (pwd.any(Char::isLowerCase) && pwd.any(Char::isUpperCase)) score++
    return when {
        score >= 3 -> PwdStrength.Strong
        score == 2 -> PwdStrength.Medium
        else -> PwdStrength.Weak
    }
}

private fun passwordError(pwd: String): String? =
    if (pwd.length < 6) "Min 6 characters" else null

@Preview(showBackground = true, backgroundColor = 0xFF0E1116, widthDp = 412, heightDp = 900)
@Composable
private fun PreviewRegister() {
    MaterialTheme {
        RegisterScreen(onBack = {}, onRegistered = {}, onGoToLogin = {})
    }
}
