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
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

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
    val scope = rememberCoroutineScope()
    val auth = remember { FirebaseAuth.getInstance() }

    var showSuccessDialog by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    var email by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf<String?>(null) }

    fun validate(): Boolean {
        emailError = if (email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches())
            "Enter a valid email" else null
        return emailError == null
    }

    fun sendPasswordResetEmail() {
        if (!validate()) return

        isLoading = true
        scope.launch {
            try {
                // Use Firebase Auth language code if needed
                auth.useAppLanguage()
                auth.sendPasswordResetEmail(email.trim()).await()
                isLoading = false
                showSuccessDialog = true
                android.util.Log.d("ForgotPassword", "Password reset email sent successfully to ${email.trim()}")
            } catch (e: Exception) {
                isLoading = false
                android.util.Log.e("ForgotPassword", "Failed to send password reset email", e)
                android.util.Log.e("ForgotPassword", "Exception type: ${e.javaClass.name}")
                android.util.Log.e("ForgotPassword", "Exception message: ${e.message}")

                emailError = when {
                    e.message?.contains("no user record", ignoreCase = true) == true ||
                    e.message?.contains("USER_NOT_FOUND", ignoreCase = true) == true ->
                        "No account found with this email"
                    e.message?.contains("network", ignoreCase = true) == true ->
                        "Network error. Please check your connection"
                    e.message?.contains("INVALID_EMAIL", ignoreCase = true) == true ->
                        "Invalid email address format"
                    e.message?.contains("too many requests", ignoreCase = true) == true ->
                        "Too many requests. Please try again later"
                    e.message?.contains("CAPTCHA", ignoreCase = true) == true ||
                    e.message?.contains("reCAPTCHA", ignoreCase = true) == true ->
                        "Verification failed. Please ensure you're using a real device with Google Play Services, or check Firebase console settings"
                    else -> "Failed to send reset email: ${e.message ?: "Unknown error"}"
                }
                Toast.makeText(context, emailError, Toast.LENGTH_LONG).show()
            }
        }
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
                        text = "Enter your email and we'll send you a link to reset your password",
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
                        onValueChange = { email = it; emailError = null },
                        singleLine = true,
                        placeholder = { Text("Enter your email") },
                        isError = emailError != null,
                        enabled = !isLoading,
                        supportingText = {
                            emailError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
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
                            errorSupportingTextColor = Color(0xFFFF4C4C),
                            disabledTextColor = onSurfaceColor.copy(alpha = 0.6f),
                            disabledBorderColor = primaryContainerColor.copy(alpha = 0.4f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                    )

                    Spacer(Modifier.height(18.dp))

                    /* ---------- Send Reset Email Button ---------- */
                    Button(
                        onClick = { sendPasswordResetEmail() },
                        enabled = !isLoading && email.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = primaryColor,
                            contentColor = onSurfaceColor,
                            disabledContainerColor = primaryColor.copy(alpha = 0.6f),
                            disabledContentColor = onSurfaceColor.copy(alpha = 0.6f)
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                color = onSurfaceColor,
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Send Reset Email", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                        }
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
                        "Email Sent!",
                        color = onSurfaceColor,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Text(
                        "A password reset link has been sent to $email. Please check your email inbox and follow the instructions to reset your password.",
                        color = mutedColor
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        showSuccessDialog = false
                        Toast.makeText(context, "Password reset email sent successfully", Toast.LENGTH_SHORT).show()
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
