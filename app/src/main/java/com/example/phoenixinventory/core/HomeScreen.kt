package com.example.phoenixinventory.core

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.Widgets
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/* -------------------- Palette (dark) -------------------- */
private val Carbon = Color(0xFF0E1116)
private val Charcoal = Color(0xFF151A21)
private val CardDark = Color(0xFF1A2028)
private val Ink = Color(0xFF0B0F1C)
private val OnDark = Color(0xFFE7EBF2)
private val Muted = Color(0xFFBFC8D4)
private val Primary = Color(0xFF0A0C17)           // deep navy/ink
private val PrimaryContainer = Color(0xFF121729)   // slightly lighter

/* -------------------- Public screen -------------------- */
@Composable
fun HomeScreen(
    onLogin: () -> Unit = {},
    onRegister: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    0f to Carbon,
                    0.5f to Charcoal,
                    1f to Carbon
                )
            )
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .clip(RoundedCornerShape(28.dp))
                .background(CardDark)
                .padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top small badge (title on the left in screenshot)
            TopBadge()

            Spacer(Modifier.height(24.dp))

            // Big center emblem
            BigEmblem()

            Spacer(Modifier.height(18.dp))

            Text(
                text = "Phoenix Rigging",
                color = OnDark,
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Professional rigging equipment management\nsystem",
                color = Muted,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp
            )

            Spacer(Modifier.height(22.dp))

            // Four NON-interactive blocks
            FeatureBlocksAdaptive()

            Spacer(Modifier.height(22.dp))

            // Buttons now call the injected callbacks
            PrimaryButton(
                text = "Login",
                onClick = onLogin, // navigate to Login screen
                containerColor = Primary,
                contentColor = OnDark,
                height = 52.dp
            )
            Spacer(Modifier.height(12.dp))
            SecondaryButton(
                text = "Register",
                onClick = onRegister, // navigate to Register screen
                borderColor = Color(0xFF2A3442),
                textColor = OnDark,
                height = 52.dp
            )

            Spacer(Modifier.height(18.dp))
            Text(
                text = "Secure equipment management for professional rigging operations",
                color = Muted,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }
}

/* -------------------- Pieces -------------------- */

@Composable
private fun TopBadge() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 54.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(PrimaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Text("") // visual placeholder to keep spacing if icon not available
            androidx.compose.material3.Icon(
                imageVector = Icons.Outlined.Build,
                contentDescription = null,
                tint = OnDark
            )
        }
        Spacer(Modifier.width(12.dp))
        Column {
            Text(
                text = "Phoenix Rigging",
                color = OnDark,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp
            )
            Text(
                text = "Equipment Management",
                color = Muted,
                fontSize = 12.sp
            )
        }
        Spacer(Modifier.weight(1f))
    }
}


@Composable
private fun BigEmblem() {
    Box(
        modifier = Modifier
            .size(120.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(PrimaryContainer),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(Ink),
            contentAlignment = Alignment.Center
        ) {
            androidx.compose.material3.Icon(
                imageVector = Icons.Outlined.Build,
                contentDescription = null,
                tint = OnDark,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun FeatureBlocksAdaptive() {
    LazyVerticalGrid(
        // min 160.dp per tile; grid adapts to available width
        columns = GridCells.Adaptive(minSize = 160.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 0.dp) // don’t force height
    ) {
        item { InfoBlock("Equipment Tracking", Icons.Outlined.Widgets) }
        item { InfoBlock("Safety Compliance", Icons.Outlined.Shield) }
        item { InfoBlock("Team Management", Icons.Outlined.Group) }
        item { InfoBlock("Maintenance Logs", Icons.Outlined.Build) }
    }
}

@Composable
private fun InfoBlock(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    // Not clickable by design
    Surface(
        shape = RoundedCornerShape(22.dp),
        color = Charcoal,
        tonalElevation = 2.dp,
        shadowElevation = 2.dp,
        modifier = modifier.height(110.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(PrimaryContainer),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.material3.Icon(icon, contentDescription = title, tint = OnDark)
            }
            Spacer(Modifier.width(14.dp))
            Text(
                text = title,
                color = OnDark,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    containerColor: Color,
    contentColor: Color,
    height: Dp
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
    ) {
        Text(text, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
    }
}

@Composable
private fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    borderColor: Color,
    textColor: Color,
    height: Dp
) {
    androidx.compose.material3.OutlinedButton(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, borderColor),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = textColor),
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
    ) {
        Text(text, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
    }
}

/* -------------------- Preview -------------------- */
@Preview(showBackground = true, backgroundColor = 0xFF0E1116)
@Composable
private fun HomeScreenPreview() {
    MaterialTheme(
        colorScheme = androidx.compose.material3.darkColorScheme(
            primary = Primary,
            onPrimary = OnDark,
            background = Carbon,
            surface = CardDark,
            onSurface = OnDark
        )
    ) {
        HomeScreen()
    }
}
