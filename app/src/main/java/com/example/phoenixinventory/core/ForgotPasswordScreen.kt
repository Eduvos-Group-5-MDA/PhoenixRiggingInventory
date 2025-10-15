package com.example.phoenixinventory.core

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.LockReset
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.phoenixinventory.ui.theme.AppColors

@Composable
fun ForgotPasswordScreen(
    onBack: () -> Unit = {}
) {
    val backgroundColor = AppColors.Carbon
    val surfaceColor = AppColors.Charcoal
    val cardColor = AppColors.CardDark
    val onSurfaceColor = AppColors.OnDark
    val mutedColor = AppColors.Muted
    val primaryColor = AppColors.Primary
    val primaryContainerColor = AppColors.PrimaryContainer
    val context = LocalContext.current
    var showSuccessDialog by remember { mutableStateOf(false) }

    var email by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var showNewPassword by remember { mutableStateOf(false) }
    var showConfirmPassword by remember { mutableStateOf(false) }

    var emailError by remember { mutableStateOf<String?>(null) }
    var newPasswordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }

    fun validate(): Boolean {
        emailError = if (email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches())
            "Enter a valid email" else null
        newPasswordError = if (newPassword.length < 4) "Password must be 4+ characters" else null
        confirmPasswordError = when {
            confirmPassword.isBlank() -> "Please confirm your password"
            confirmPassword != newPassword -> "Passwords do not match"
            else -> null
        }
        return emailError == null && newPasswordError == null && confirmPasswordError == null
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

            /* ---------- Header ---------- */
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
                ) {
                    Icon(Icons.Outlined.LockReset, contentDescription = null, tint = onSurfaceColor)
                }
                Spacer(Modifier.width(8.dp))
                Text("Reset Password", color = onSurfaceColor, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
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

                    Text(
                        text = "Forgot Your Password?",
                        color = onSurfaceColor,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "Enter your email and create a new password",
                        color = mutedColor,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(20.dp))

                    /* ---------- Email Field ---------- */
                    Text("Email", color = onSurfaceColor, fontWeight = FontWeight.SemiBold, modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.height(6.dp))
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        singleLine = true,
                        placeholder = { Text("Enter your email") },
                        isError = emailError != null,
                        supportingText = {
                            emailError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = primaryContainerColor,
                            unfocusedBorderColor = primaryContainerColor.copy(alpha = 0.6f),
                            errorBorderColor = Color(0xFFFF4C4C),
                            cursorColor = onSurfaceColor,
                            focusedTextColor = onSurfaceColor,
                            unfocusedTextColor = onSurfaceColor,
                            errorTextColor = onSurfaceColor,
                            focusedPlaceholderColor = mutedColor,
                            unfocusedPlaceholderColor = mutedColor,
                            errorPlaceholderColor = mutedColor,
                            errorSupportingTextColor = Color(0xFFFF4C4C)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                    )

                    Spacer(Modifier.height(14.dp))

                    /* ---------- New Password Field ---------- */
                    Text("New Password", color = onSurfaceColor, fontWeight = FontWeight.SemiBold, modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.height(6.dp))
                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        singleLine = true,
                        placeholder = { Text("Enter new password") },
                        isError = newPasswordError != null,
                        visualTransformation = if (showNewPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { showNewPassword = !showNewPassword }) {
                                Icon(
                                    imageVector = if (showNewPassword) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                                    contentDescription = "Toggle password",
                                    tint = onSurfaceColor
                                )
                            }
                        },
                        supportingText = {
                            newPasswordError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Next
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = primaryContainerColor,
                            unfocusedBorderColor = primaryContainerColor.copy(alpha = 0.6f),
                            errorBorderColor = Color(0xFFFF4C4C),
                            cursorColor = onSurfaceColor,
                            focusedTextColor = onSurfaceColor,
                            unfocusedTextColor = onSurfaceColor,
                            errorTextColor = onSurfaceColor,
                            focusedPlaceholderColor = mutedColor,
                            unfocusedPlaceholderColor = mutedColor,
                            errorPlaceholderColor = mutedColor,
                            errorSupportingTextColor = Color(0xFFFF4C4C)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                    )

                    Spacer(Modifier.height(14.dp))

                    /* ---------- Confirm Password Field ---------- */
                    Text("Confirm Password", color = onSurfaceColor, fontWeight = FontWeight.SemiBold, modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.height(6.dp))
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        singleLine = true,
                        placeholder = { Text("Confirm new password") },
                        isError = confirmPasswordError != null,
                        visualTransformation = if (showConfirmPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { showConfirmPassword = !showConfirmPassword }) {
                                Icon(
                                    imageVector = if (showConfirmPassword) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                                    contentDescription = "Toggle password",
                                    tint = onSurfaceColor
                                )
                            }
                        },
                        supportingText = {
                            confirmPasswordError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = primaryContainerColor,
                            unfocusedBorderColor = primaryContainerColor.copy(alpha = 0.6f),
                            errorBorderColor = Color(0xFFFF4C4C),
                            cursorColor = onSurfaceColor,
                            focusedTextColor = onSurfaceColor,
                            unfocusedTextColor = onSurfaceColor,
                            errorTextColor = onSurfaceColor,
                            focusedPlaceholderColor = mutedColor,
                            unfocusedPlaceholderColor = mutedColor,
                            errorPlaceholderColor = mutedColor,
                            errorSupportingTextColor = Color(0xFFFF4C4C)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                    )

                    Spacer(Modifier.height(18.dp))

                    /* ---------- Reset Password Button ---------- */
                    Button(
                        onClick = {
                            if (validate()) {
                                showSuccessDialog = true
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = primaryColor,
                            contentColor = onSurfaceColor
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                    ) {
                        Text("Reset Password", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                    }

                    Spacer(Modifier.height(14.dp))

                    /* ---------- Back to Login ---------- */
                    TextButton(onClick = onBack) {
                        Text(
                            "Back to Login",
                            color = onSurfaceColor,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        /* ---------- Success Dialog ---------- */
        if (showSuccessDialog) {
            AlertDialog(
                onDismissRequest = { },
                title = {
                    Text(
                        "Password Changed!",
                        color = onSurfaceColor,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Text(
                        "Your password has been successfully changed. You can now log in with your new password.",
                        color = mutedColor
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        showSuccessDialog = false
                        Toast.makeText(context, "Password changed successfully", Toast.LENGTH_SHORT).show()
                        onBack()
                    }) {
                        Text("Continue to Login", color = onSurfaceColor, fontWeight = FontWeight.Bold)
                    }
                },
                containerColor = cardColor,
                icon = {
                    Icon(
                        Icons.Outlined.LockReset,
                        contentDescription = null,
                        tint = Color(0xFF17C964)
                    )
                }
            )
        }
    }
}

/* ---------- Preview ---------- */
@Preview(showBackground = true, backgroundColor = 0xFF0E1116, widthDp = 412, heightDp = 900)
@Composable
private fun PreviewForgotPassword() {
    MaterialTheme {
        ForgotPasswordScreen()
    }
}
