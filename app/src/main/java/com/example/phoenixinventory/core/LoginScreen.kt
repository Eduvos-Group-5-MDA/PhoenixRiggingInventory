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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.input.KeyboardType

// Dark palette (same as Home)
private val Carbon = Color(0xFF0E1116)
private val Charcoal = Color(0xFF151A21)
private val CardDark = Color(0xFF1A2028)
private val OnDark = Color(0xFFE7EBF2)
private val Muted = Color(0xFFBFC8D4)
private val Primary = Color(0xFF0A0C17)
private val PrimaryContainer = Color(0xFF121729)

@Composable
fun LoginScreen(
    onBack: () -> Unit,
    onLoginSuccess: () -> Unit,   // ← call this to go to Dashboard
    onGoToRegister: () -> Unit
) {
    val context = LocalContext.current

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var pwdError by remember { mutableStateOf<String?>(null) }

    fun validate(): Boolean {
        emailError =
            if (email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches())
                "Enter a valid email" else null
        pwdError = if (password.length < 4) "Password must be 4+ characters" else null
        return emailError == null && pwdError == null
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

            // Top bar
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
                ) {
                    Icon(Icons.Outlined.Build, contentDescription = null, tint = OnDark)
                }
                Spacer(Modifier.width(8.dp))
                Text("Login", color = OnDark, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            }

            // Card
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

                    Text(
                        text = "Welcome Back",
                        color = OnDark,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "Sign in to your Phoenix Rigging account",
                        color = Muted,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(20.dp))

                    // Email
                    Text("Email", color = OnDark, fontWeight = FontWeight.SemiBold)
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
                            focusedBorderColor = PrimaryContainer,
                            unfocusedBorderColor = PrimaryContainer.copy(alpha = 0.6f),
                            errorBorderColor = Color(0xFFFF4C4C), // red border when invalid
                            cursorColor = OnDark,
                            focusedTextColor = OnDark,
                            unfocusedTextColor = OnDark,
                            errorTextColor = OnDark, // keep white text even when invalid
                            focusedPlaceholderColor = Muted,
                            unfocusedPlaceholderColor = Muted,
                            errorPlaceholderColor = Muted, // gray placeholder in error state
                            errorSupportingTextColor = Color(0xFFFF4C4C) // red error message
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                    )


                    Spacer(Modifier.height(14.dp))

                    // Password
                    Text("Password", color = OnDark, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(6.dp))
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        singleLine = true,
                        placeholder = { Text("Enter your password") },
                        isError = pwdError != null,
                        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { showPassword = !showPassword }) {
                                Icon(
                                    imageVector = if (showPassword) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                                    contentDescription = "Toggle password",
                                    tint = OnDark
                                )
                            }
                        },
                        supportingText = {
                            pwdError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryContainer,
                            unfocusedBorderColor = PrimaryContainer.copy(alpha = 0.6f),
                            errorBorderColor = Color(0xFFFF4C4C), // red border when invalid
                            cursorColor = OnDark,
                            focusedTextColor = OnDark,
                            unfocusedTextColor = OnDark,
                            errorTextColor = OnDark, // keep white text even when invalid
                            focusedPlaceholderColor = Muted,
                            unfocusedPlaceholderColor = Muted,
                            errorPlaceholderColor = Muted, // gray placeholder in error state
                            errorSupportingTextColor = Color(0xFFFF4C4C) // red error message
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                    )

                    Spacer(Modifier.height(18.dp))

                    // Login button (demo flow → navigate on success)
                    Button(
                        onClick = {
                            if (validate()) {
                                Toast.makeText(context, "Logged in (demo)", Toast.LENGTH_SHORT).show()
                                onLoginSuccess() // ← navigate to Dashboard route in AppNav
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Primary,
                            contentColor = OnDark
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                    ) {
                        Text("Login", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                    }

                    Spacer(Modifier.height(14.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Don't have an account? ", color = Muted)
                        Text(
                            "Register",
                            color = OnDark,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.clickable { onGoToRegister() }
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0E1116, widthDp = 412, heightDp = 900)
@Composable
private fun PreviewLogin() {
    MaterialTheme {
        LoginScreen(
            onBack = {},
            onLoginSuccess = {},
            onGoToRegister = {}
        )
    }
}
