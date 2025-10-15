package com.example.phoenixinventory.core

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/* ---------- Palette (same as other screens) ---------- */
private val Carbon = Color(0xFF0E1116)
private val Charcoal = Color(0xFF151A21)
private val CardDark = Color(0xFF1A2028)
private val OnDark = Color(0xFFE7EBF2)
private val Muted = Color(0xFFBFC8D4)
private val PrimaryContainer = Color(0xFF121729)

/* ---------- Screen ---------- */
@Composable
fun TermsPrivacyScreen(
    onBack: () -> Unit
) {
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
                .widthIn(max = 720.dp)
                .align(Alignment.TopCenter)
                .clip(RoundedCornerShape(28.dp))
                .background(CardDark)
                .padding(16.dp)
        ) {
            /* ---------- Header ---------- */
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
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
                    Icon(Icons.Outlined.Description, contentDescription = null, tint = OnDark)
                }
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Terms & Privacy",
                    color = OnDark,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(16.dp))

            /* ---------- Content Card ---------- */
            Surface(
                color = Charcoal,
                shape = RoundedCornerShape(20.dp),
                tonalElevation = 2.dp,
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(16.dp)) {

                    SectionTitle("Terms of Service")
                    Paragraph(
                        "By using this application, you agree to comply with all applicable " +
                                "laws and these Terms. You are responsible for any activity " +
                                "that occurs under your account and for keeping your credentials secure."
                    )
                    Paragraph(
                        "You may not misuse the service, attempt to access data without authorization, " +
                                "or interfere with the app’s normal operation. We may update these Terms " +
                                "from time to time; continued use constitutes acceptance of any changes."
                    )

                    Spacer(Modifier.height(16.dp))
                    Divider(color = PrimaryContainer.copy(alpha = 0.5f))
                    Spacer(Modifier.height(16.dp))

                    SectionTitle("Privacy Policy")
                    Paragraph(
                        "We collect basic information you provide (such as name and email) to deliver " +
                                "core functionality. Device data may be used to improve performance and reliability."
                    )
                    Paragraph(
                        "We do not sell your personal data. Limited third-party services may process " +
                                "data on our behalf (e.g., analytics) in accordance with this policy. " +
                                "You can request access, correction, or deletion of your data at any time."
                    )
                    Paragraph(
                        "Security measures are applied to protect information; however, no method " +
                                "is 100% secure. For questions about privacy, please contact support."
                    )

                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = "Last updated: 10 Oct 2025",
                        color = Muted,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Start
                    )
                }
            }
        }
    }
}

/* ---------- Small helpers ---------- */
@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        color = OnDark,
        fontSize = 16.sp,
        fontWeight = FontWeight.SemiBold
    )
    Spacer(Modifier.height(8.dp))
}

@Composable
private fun Paragraph(text: String) {
    Text(
        text = text,
        color = Muted,
        fontSize = 14.sp,
        lineHeight = 20.sp
    )
    Spacer(Modifier.height(10.dp))
}

/* ---------- Preview ---------- */
@Preview(showBackground = true, backgroundColor = 0xFF0E1116, widthDp = 412, heightDp = 900)
@Composable
private fun PreviewTermsPrivacy() {
    MaterialTheme {
        TermsPrivacyScreen(onBack = {})
    }
}
