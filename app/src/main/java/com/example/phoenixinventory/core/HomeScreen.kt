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
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.phoenixinventory.R
import androidx.compose.ui.text.style.TextOverflow
import com.example.phoenixinventory.ui.theme.AppColors

/* -------------------- Public screen -------------------- */
@Composable
fun HomeScreen(
    onLogin: () -> Unit = {},
    onRegister: () -> Unit = {}
) {
    val backgroundColor = AppColors.Carbon
    val surfaceColor = AppColors.Charcoal
    val cardColor = AppColors.CardDark
    val onSurfaceColor = AppColors.OnDark
    val mutedColor = AppColors.Muted
    val primaryColor = AppColors.Primary
    val primaryContainerColor = AppColors.PrimaryContainer
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    0f to backgroundColor,
                    0.5f to surfaceColor,
                    1f to backgroundColor
                )
            )
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .align(Alignment.TopCenter)
                .clip(RoundedCornerShape(28.dp))
                .background(cardColor)
                .padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(Modifier.height(8.dp)) // small gap above the emblem if you like
            BigEmblem()
            Spacer(Modifier.height(18.dp))


            Spacer(Modifier.height(8.dp))
            Text(
                text = "Professional rigging equipment management\nsystem",
                color = mutedColor,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp
            )
//hello
            Spacer(Modifier.height(22.dp))

            // Four NON-interactive blocks
            FeatureBlocksAdaptive()

            Spacer(Modifier.height(22.dp))

            // Buttons now call the injected callbacks
            PrimaryButton(
                text = "Login",
                onClick = onLogin, // navigate to Login screen
                containerColor = primaryColor,
                contentColor = onSurfaceColor,
                height = 52.dp
            )
            Spacer(Modifier.height(12.dp))
            SecondaryButton(
                text = "Register",
                onClick = onRegister, // navigate to Register screen
                borderColor = Color(0xFF2A3442),
                textColor = onSurfaceColor,
                height = 52.dp
            )

            Spacer(Modifier.height(18.dp))
            Text(
                text = "Secure equipment management for professional rigging operations",
                color = mutedColor,
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
    val onSurfaceColor = AppColors.OnDark
    val mutedColor = AppColors.Muted
    val primaryContainerColor = AppColors.PrimaryContainer
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
                .background(primaryContainerColor),
            contentAlignment = Alignment.Center
        ) {
            Text("") // visual placeholder to keep spacing if icon not available
            androidx.compose.material3.Icon(
                imageVector = Icons.Outlined.Build,
                contentDescription = null,
                tint = onSurfaceColor
            )
        }
        Spacer(Modifier.width(12.dp))
        Column {
            Text(
                text = "Phoenix Rigging",
                color = onSurfaceColor,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp
            )
            Text(
                text = "Equipment Management",
                color = mutedColor,
                fontSize = 12.sp
            )
        }
        Spacer(Modifier.weight(1f))
    }
}


@Composable
private fun BigEmblem() {
    val primaryContainerColor = AppColors.PrimaryContainer
    Surface(
        shape = RoundedCornerShape(28.dp),
        color = primaryContainerColor,
        tonalElevation = 2.dp,
        shadowElevation = 2.dp,
        modifier = Modifier
            .fillMaxWidth()      // full width inside the card
            .height(180.dp)      // make it taller if you want
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),  // breathing room for the logo
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.companylogo),
                contentDescription = "Company Logo",
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxWidth(0.9f) // scale inside the box; tweak 0.8–1.0
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun FeatureBlocksAdaptive() {
    LazyVerticalGrid(
        // min 160.dp per tile; grid adapts to available width
        columns = GridCells.Adaptive(minSize = 175.dp),
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
    val surfaceColor = AppColors.Charcoal
    val primaryContainerColor = AppColors.PrimaryContainer
    val onSurfaceColor = AppColors.OnDark
    // Not clickable by design
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = surfaceColor,
        tonalElevation = 2.dp,
        shadowElevation = 2.dp,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight() // 👈 This makes it fill vertically
            .padding(top = 8.dp)
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
                    .background(primaryContainerColor),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.material3.Icon(icon, contentDescription = title, tint = onSurfaceColor)
            }
            Spacer(Modifier.width(14.dp))
            Text(
                text = title,
                color = onSurfaceColor,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 2,                       // 👈 force a single line
                overflow = TextOverflow.Ellipsis,   // 👈 avoid wrapping
                modifier = Modifier.weight(1f)      // 👈 give the text the remaining row width
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
            primary = AppColors.Primary,
            onPrimary = AppColors.OnDark,
            background = AppColors.Carbon,
            surface = AppColors.CardDark,
            onSurface = AppColors.OnDark
        )
    ) {
        HomeScreen()
    }
}
