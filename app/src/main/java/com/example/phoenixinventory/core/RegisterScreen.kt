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
import com.example.phoenixinventory.ui.theme.AppColors
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.example.phoenixinventory.data.FirebaseRepository
import com.example.phoenixinventory.data.User
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/* ---------- Models ---------- */
enum class UserRole { Guest, Employee }
private enum class PwdStrength { Weak, Medium, Strong }

/* ---------- Screen ---------- */
@Composable
fun RegisterScreen(
    onBack: () -> Unit,
    onRegistered: () -> Unit,
    onGoToLogin: () -> Unit,
    onTermsPrivacy: () -> Unit = {}
) {
    val backgroundColor = AppColors.Carbon
    val surfaceColor = AppColors.Charcoal
    val cardColor = AppColors.CardDark
    val onSurfaceColor = AppColors.OnDark
    val mutedColor = AppColors.Muted
    val primaryColor = AppColors.Primary
    val primaryContainerColor = AppColors.PrimaryContainer
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

    // Firebase state
    var isLoading by remember { mutableStateOf(false) }
    val auth = FirebaseAuth.getInstance()
    val firebaseRepo = remember { FirebaseRepository() }
    val scope = rememberCoroutineScope()

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
            /* ---------- Top bar ---------- */
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

            /* ---------- Card ---------- */
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
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Role",
                            color = onSurfaceColor,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center
                        )
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
                        Text("Do you have a valid Driver's License?", color = onSurfaceColor, fontWeight = FontWeight.SemiBold)
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
                                    colors = RadioButtonDefaults.colors(selectedColor = onSurfaceColor, unselectedColor = mutedColor)
                                )
                                Text("Yes", color = onSurfaceColor, fontSize = 16.sp)
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable { hasDriverLicense = false }
                            ) {
                                RadioButton(
                                    selected = hasDriverLicense == false,
                                    onClick = { hasDriverLicense = false },
                                    colors = RadioButtonDefaults.colors(selectedColor = onSurfaceColor, unselectedColor = mutedColor)
                                )
                                Text("No", color = onSurfaceColor, fontSize = 16.sp)
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
                                checkedColor = onSurfaceColor,
                                uncheckedColor = mutedColor
                            )
                        )
                        Spacer(Modifier.width(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("I accept the ", color = mutedColor, fontSize = 14.sp)
                            Text(
                                "Terms & Privacy",
                                color = onSurfaceColor,
                                fontWeight = FontWeight.SemiBold,
                                textDecoration = TextDecoration.Underline,
                                fontSize = 14.sp,
                                modifier = Modifier.clickable { onTermsPrivacy() }
                            )
                        }
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
                                isLoading = true
                                scope.launch {
                                    try {
                                        // Create Firebase Auth account
                                        val authResult = auth.createUserWithEmailAndPassword(email.trim(), password).await()
                                        val userId = authResult.user?.uid ?: throw Exception("Failed to create user")

                                        // Update display name
                                        val profileUpdates = UserProfileChangeRequest.Builder()
                                            .setDisplayName("$first $last")
                                            .build()
                                        authResult.user?.updateProfile(profileUpdates)?.await()

                                        // Create user document in Firestore
                                        val newUser = User(
                                            id = userId,
                                            name = "$first $last",
                                            email = email.trim(),
                                            role = when (role!!) {
                                                UserRole.Employee -> "Employee"
                                                UserRole.Guest -> "Guest"
                                            },
                                            phone = phone,
                                            idNumber = idNumber.ifBlank { null },
                                            company = if (role == UserRole.Guest) company.ifBlank { null } else null,
                                            hasDriverLicense = if (role == UserRole.Employee) hasDriverLicense else null
                                        )

                                        firebaseRepo.addUser(newUser).getOrThrow()

                                        Toast.makeText(ctx, "Registration successful!", Toast.LENGTH_SHORT).show()
                                        onRegistered()
                                    } catch (e: Exception) {
                                        val errorMessage = when {
                                            e.message?.contains("email", ignoreCase = true) == true -> "Email already in use"
                                            e.message?.contains("password", ignoreCase = true) == true -> "Weak password"
                                            e.message?.contains("network", ignoreCase = true) == true -> "Network error"
                                            else -> "Registration failed: ${e.message}"
                                        }
                                        Toast.makeText(ctx, errorMessage, Toast.LENGTH_LONG).show()
                                    } finally {
                                        isLoading = false
                                    }
                                }
                            }
                        },
                        enabled = !isLoading,
                        colors = ButtonDefaults.buttonColors(containerColor = primaryColor, contentColor = onSurfaceColor),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth().height(52.dp)
                    ) {
                        Text(
                            if (isLoading) "Creating Account..." else "Register",
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(Modifier.height(16.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Already have an account? ", color = mutedColor)
                        Text(
                            "Login",
                            color = onSurfaceColor,
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
    val onSurfaceColor = AppColors.OnDark
    val mutedColor = AppColors.Muted
    val primaryContainerColor = AppColors.PrimaryContainer
    Text(label, color = onSurfaceColor, fontWeight = FontWeight.SemiBold)
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
            focusedBorderColor = primaryContainerColor,
            unfocusedBorderColor = primaryContainerColor.copy(alpha = 0.6f),
            errorBorderColor = MaterialTheme.colorScheme.error,
            cursorColor = onSurfaceColor,
            focusedTextColor = onSurfaceColor,
            unfocusedTextColor = onSurfaceColor,
            errorTextColor = onSurfaceColor,
            focusedPlaceholderColor = mutedColor,
            unfocusedPlaceholderColor = mutedColor
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
    val onSurfaceColor = AppColors.OnDark
    val mutedColor = AppColors.Muted
    val primaryContainerColor = AppColors.PrimaryContainer
    Text(label, color = onSurfaceColor, fontWeight = FontWeight.SemiBold)
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
                    tint = onSurfaceColor
                )
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
        supportingText = { error?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = primaryContainerColor,
            unfocusedBorderColor = primaryContainerColor.copy(alpha = 0.6f),
            errorBorderColor = MaterialTheme.colorScheme.error,
            cursorColor = onSurfaceColor,
            focusedTextColor = onSurfaceColor,
            unfocusedTextColor = onSurfaceColor
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
    val onSurfaceColor = AppColors.OnDark
    val mutedColor = AppColors.Muted
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
                colors = RadioButtonDefaults.colors(selectedColor = onSurfaceColor, unselectedColor = mutedColor)
            )
            Spacer(Modifier.width(8.dp))
            Text("Guest", color = onSurfaceColor, fontSize = 16.sp)
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
                colors = RadioButtonDefaults.colors(selectedColor = onSurfaceColor, unselectedColor = mutedColor)
            )
            Spacer(Modifier.width(8.dp))
            Text("Employee", color = onSurfaceColor, fontSize = 16.sp)
        }
    }
}

/* ---------- Helpers ---------- */
private fun String.capitalizeWords(): String =
    split(" ").joinToString(" ") { it.lowercase().replaceFirstChar { c -> c.titlecase() } }

@Composable
private fun PasswordStrengthIndicator(password: String) {
    val mutedColor = AppColors.Muted
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
    Text("Password strength: $label", color = mutedColor, fontSize = 12.sp)
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
