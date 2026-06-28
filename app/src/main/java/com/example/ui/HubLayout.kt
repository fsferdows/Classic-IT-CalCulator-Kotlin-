package com.example.ui

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ThemeColors
import com.example.viewmodel.CalculatorViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
fun HubLayout(
    viewModel: CalculatorViewModel,
    colors: ThemeColors
) {
    var activeSubTab by remember { mutableStateOf("Brand") } // Brand, Infra, Scaling
    val haptic = LocalHapticFeedback.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("hub_layout_root")
    ) {
        // Inner Tab Selector
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Brand" to "✨ Brand Hub", "Infra" to "⚙️ Infrastructure", "Scaling" to "🚀 Scaling & Docs").forEach { (tabId, tabName) ->
                val isSelected = activeSubTab == tabId
                Button(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        activeSubTab = tabId
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSelected) colors.accent else colors.surfaceGlass,
                        contentColor = if (isSelected) colors.background else colors.textPrimary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp)
                        .testTag("hub_tab_$tabId")
                ) {
                    Text(text = tabName, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Content panel with vertical scrolling
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            when (activeSubTab) {
                "Brand" -> BrandHubView(viewModel, colors)
                "Infra" -> InfrastructureHubView(viewModel, colors)
                "Scaling" -> ScalingHubView(colors)
            }
        }
    }
}

// -------------------------------------------------------------
// SUB-TAB 1: BRAND HUB & MOCK LANDING PAGE
// -------------------------------------------------------------
@Composable
fun BrandHubView(viewModel: CalculatorViewModel, colors: ThemeColors) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        // 1. Interactive Monogram SVG Logo Renderer
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = colors.surface),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, colors.borderGlass),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "LIVE SVG MONOGRAM CREST (CITC)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.accent,
                        letterSpacing = 2.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Draw the crest on custom Canvas
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(160.dp)
                            .drawBehind {
                                // Draw circular Gold Accent Ring
                                drawCircle(
                                    brush = Brush.radialGradient(
                                        colors = listOf(Color(0xFFF4E4BC), Color(0xFFD4AF37)),
                                        center = center,
                                        radius = size.minDimension / 2.2f
                                    ),
                                    style = Stroke(width = 4.dp.toPx())
                                )
                                // Draw inner Royal Blue glow circle
                                drawCircle(
                                    color = Color(0x1A1E3A8A),
                                    radius = size.minDimension / 2.4f
                                )
                            }
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "👑",
                                fontSize = 32.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "CITC",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFFD4AF37),
                                fontFamily = FontFamily.Serif,
                                letterSpacing = 2.sp
                            )
                            Text(
                                text = "EST. 2026",
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.textSecondary,
                                letterSpacing = 1.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Luxury Tech Identity: Rolex Meets Apple",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Minimalist serif styling with an elegant gold crest crown monogram represents absolute computational accuracy & trust.",
                        fontSize = 11.sp,
                        color = colors.textSecondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }
            }
        }

        // 2. Mock Marketing Landing Page Section
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = colors.surface.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, colors.borderGlass.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .background(colors.accent.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "PREMIUM LANDING PAGE PREVIEW",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.accent
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Precision Engineered.\nBeautifully Calculated.",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = colors.textPrimary,
                        fontFamily = FontFamily.Serif,
                        lineHeight = 32.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "The ultimate multi-platform scientific engine with dynamic styling, real-time formula charting, cloud databases, team workspaces, and developer API integrations.",
                        fontSize = 12.sp,
                        color = colors.textSecondary,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                Toast.makeText(context, "Redirecting to app.classicitcalculator.com...", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = colors.accent, contentColor = colors.background),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Launch App", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        OutlinedButton(
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                Toast.makeText(context, "Investor Pitch Deck opened!", Toast.LENGTH_SHORT).show()
                            },
                            border = BorderStroke(1.dp, colors.borderGlass),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.accent),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Pitch Deck", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // 3. Interactive Pricing Section
        item {
            Text(
                text = "INVESTOR-GRADE SAAS PLANS",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = colors.accent,
                modifier = Modifier.padding(start = 4.dp)
            )
        }

        items(listOf(
            SaaSPlan("Free Tier", "$0", "Essential arithmetic, standard layout, up to 100 calculation history items.", false),
            SaaSPlan("Pro Tier", "$4.99/mo", "Unlimited history, 3D/polar equation graphing, custom luxury themes, offline sync.", true),
            SaaSPlan("Enterprise Tier", "$99+/mo", "Unlimited API requests, team audit log dashboard, SAML SSO, on-premise deployment.", false)
        )) { plan ->
            val isCurrentPlan = (plan.isPro && viewModel.isProUser) || (!plan.isPro && !viewModel.isProUser && plan.price == "$0")
            
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (isCurrentPlan) colors.surface else colors.surfaceGlass
                ),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(
                    if (isCurrentPlan) 2.dp else 1.dp,
                    if (isCurrentPlan) colors.accent else colors.borderGlass.copy(alpha = 0.4f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        if (plan.isPro) {
                            Toast.makeText(context, "Switch to Infrastructure tab to complete Stripe Payment!", Toast.LENGTH_LONG).show()
                        } else if (plan.price == "$0") {
                            viewModel.downgradeToFree()
                            Toast.makeText(context, "Downgraded to Free Tier successfully.", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Enterprise inquiry submitted! Our account manager will email you.", Toast.LENGTH_LONG).show()
                        }
                    }
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = plan.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = colors.textPrimary)
                            Text(text = plan.price, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = colors.accent)
                        }
                        if (isCurrentPlan) {
                            Box(
                                modifier = Modifier
                                    .background(colors.accent.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                                    .border(1.dp, colors.accent, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text("Active", fontSize = 10.sp, color = colors.accent, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = plan.description, fontSize = 11.sp, color = colors.textSecondary)
                }
            }
        }

        // 4. Interactive Accordion FAQ Section
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = colors.surface),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, colors.borderGlass),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "🙋 FAQ Accordion",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    FAQItem("Is mathematical calculation truly accurate?", "Yes. Classic IT Calculator uses precise 128-bit decimal arithmetic engines to completely avoid floating-point inaccuracies.", colors)
                    FAQItem("Does the app sync calculations offline?", "Absolutely. Real-time updates sync securely with Supabase PostgreSQL as soon as an internet connection is established.", colors)
                    FAQItem("How do we use developer APIs?", "Pro and Enterprise accounts can generate access tokens to query computational engines directly in Python/Node.js.", colors)
                }
            }
        }
    }
}

data class SaaSPlan(
    val name: String,
    val price: String,
    val description: String,
    val isPro: Boolean
)

@Composable
fun FAQItem(question: String, answer: String, colors: ThemeColors) {
    var expanded by remember { mutableStateOf(false) }
    val rotationAngle by animateFloatAsState(targetValue = if (expanded) 180f else 0f)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = question,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary,
                modifier = Modifier.weight(0.9f)
            )
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "Expand FAQ",
                tint = colors.accent,
                modifier = Modifier.drawBehind {
                    // subtle rotation animation helper
                }
            )
        }
        AnimatedVisibility(visible = expanded) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = answer,
                fontSize = 11.sp,
                color = colors.textSecondary,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }
        HorizontalDivider(color = colors.borderGlass.copy(alpha = 0.3f), thickness = 1.dp)
    }
}

// -------------------------------------------------------------
// SUB-TAB 2: INFRASTRUCTURE HUB VIEW
// -------------------------------------------------------------
@Composable
fun InfrastructureHubView(viewModel: CalculatorViewModel, colors: ThemeColors) {
    val haptic = LocalHapticFeedback.current
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    // Supabase Auth simulation state
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoggedIn by remember { mutableStateOf(false) }
    var isAuthLoading by remember { mutableStateOf(false) }
    var jwtToken by remember { mutableStateOf("") }

    // Stripe checkout state
    var isProcessingPayment by remember { mutableStateOf(false) }
    var cardNumber by remember { mutableStateOf("") }
    var cardExpiry by remember { mutableStateOf("") }
    var cardCvc by remember { mutableStateOf("") }

    // Live telemetry analytics logs
    val telemetryLogs = remember { mutableStateListOf<String>() }

    LaunchedEffect(Unit) {
        telemetryLogs.add("[Mixpanel] Page view event initialized: activeScreen=Hub")
        telemetryLogs.add("[Sentry] Integration loaded successfully. Uptime monitor ok.")
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        // A. Supabase Auth Section
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = colors.surface),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, colors.borderGlass),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "⚡ SUPABASE AUTHENTICATION",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.accent
                        )
                        Box(
                            modifier = Modifier
                                .background(if (isLoggedIn) Color(0xFF10B981).copy(alpha = 0.2f) else Color(0xFFEF4444).copy(alpha = 0.2f), CircleShape)
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (isLoggedIn) "Connected" else "Offline",
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isLoggedIn) Color(0xFF10B981) else Color(0xFFEF4444)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    if (!isLoggedIn) {
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Email Address", fontSize = 11.sp) },
                            textStyle = TextStyle(fontSize = 12.sp, color = colors.textPrimary),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = colors.accent,
                                unfocusedBorderColor = colors.borderGlass
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("Password", fontSize = 11.sp) },
                            textStyle = TextStyle(fontSize = 12.sp, color = colors.textPrimary),
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = colors.accent,
                                unfocusedBorderColor = colors.borderGlass
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                if (email.isBlank() || password.isBlank()) {
                                    Toast.makeText(context, "Please enter email and password", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                isAuthLoading = true
                                coroutineScope.launch {
                                    delay(1500) // simulate edge API latency
                                    isAuthLoading = false
                                    isLoggedIn = true
                                    jwtToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyNjbGFzc2ljaXQudXNlciI6ImRlbW8iLCJyb2xlIjoiYXV0aGVudGljYXRlZCJ9." + UUID.randomUUID().toString().take(16)
                                    telemetryLogs.add("[Supabase] Post sign-in successful: email=$email")
                                    telemetryLogs.add("[Mixpanel] Identified user: uid=demo_usr_9918")
                                    Toast.makeText(context, "Logged in securely via Supabase Auth!", Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = colors.secondary, contentColor = Color.White),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            if (isAuthLoading) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(18.dp))
                            } else {
                                Text("Secure Login / Create Account", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    } else {
                        Column {
                            Text(text = "User Profile: Active Session", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = colors.textPrimary)
                            Text(text = "Email: $email", fontSize = 11.sp, color = colors.textSecondary)
                            Spacer(modifier = Modifier.height(8.dp))

                            Box(
                                modifier = Modifier
                                    .background(colors.surfaceGlass, RoundedCornerShape(8.dp))
                                    .fillMaxWidth()
                                    .padding(8.dp)
                            ) {
                                Column {
                                    Text("JWT Access Token (AES-256 encrypted):", fontSize = 8.sp, color = colors.accent, fontWeight = FontWeight.Bold)
                                    Text(
                                        text = jwtToken,
                                        fontSize = 9.sp,
                                        fontFamily = FontFamily.Monospace,
                                        color = colors.textPrimary,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedButton(
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    isLoggedIn = false
                                    email = ""
                                    password = ""
                                    jwtToken = ""
                                    telemetryLogs.add("[Supabase] Session destroyed: sign_out_event")
                                    Toast.makeText(context, "Signed out safely.", Toast.LENGTH_SHORT).show()
                                },
                                border = BorderStroke(1.dp, colors.borderGlass),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.accent),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Log Out / Destroy Session", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }

        // B. Stripe Subscriptions Payment Simulation
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = colors.surface),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, colors.borderGlass),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "💳 SECURE STRIPE CHECKOUT",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.accent
                        )
                        Box(
                            modifier = Modifier
                                .background(if (viewModel.isProUser) Color(0xFFD4AF37).copy(alpha = 0.2f) else Color(0x33C0C0C0), CircleShape)
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (viewModel.isProUser) "PRO ACTIVE" else "FREE TIER",
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (viewModel.isProUser) Color(0xFFD4AF37) else colors.textSecondary
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    if (!viewModel.isProUser) {
                        Text(
                            text = "Upgrade to Pro Plan ($4.99/mo) to unlock infinite calculation history, advanced 3D formula charting, and professional developer API tokens.",
                            fontSize = 11.sp,
                            color = colors.textSecondary,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        OutlinedTextField(
                            value = cardNumber,
                            onValueChange = { if (it.length <= 16) cardNumber = it },
                            label = { Text("Card Number (Use 4242 4242...)", fontSize = 11.sp) },
                            placeholder = { Text("4242 4242 4242 4242") },
                            textStyle = TextStyle(fontSize = 12.sp, color = colors.textPrimary),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = colors.accent,
                                unfocusedBorderColor = colors.borderGlass
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = cardExpiry,
                                onValueChange = { if (it.length <= 5) cardExpiry = it },
                                label = { Text("MM/YY", fontSize = 11.sp) },
                                textStyle = TextStyle(fontSize = 12.sp, color = colors.textPrimary),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = colors.accent,
                                    unfocusedBorderColor = colors.borderGlass
                                ),
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = cardCvc,
                                onValueChange = { if (it.length <= 3) cardCvc = it },
                                label = { Text("CVC", fontSize = 11.sp) },
                                textStyle = TextStyle(fontSize = 12.sp, color = colors.textPrimary),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = colors.accent,
                                    unfocusedBorderColor = colors.borderGlass
                                ),
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                if (cardNumber.length < 16) {
                                    Toast.makeText(context, "Please enter a valid card number", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                isProcessingPayment = true
                                coroutineScope.launch {
                                    delay(2000) // payment processing latency
                                    isProcessingPayment = false
                                    viewModel.upgradeToPro()
                                    telemetryLogs.add("[Stripe] Webhook: invoice_paid successful, status=active")
                                    telemetryLogs.add("[Mixpanel] Event tracked: plan_upgraded (tier=Pro)")
                                    Toast.makeText(context, "Stripe Payment Success! Pro Features Unlocked! 🎉", Toast.LENGTH_LONG).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = colors.accent, contentColor = colors.background),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            if (isProcessingPayment) {
                                CircularProgressIndicator(color = colors.background, modifier = Modifier.size(18.dp))
                            } else {
                                Text("Pay $4.99 & Unlock Pro Features", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    } else {
                        Column {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(colors.accent.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                                    .padding(16.dp)
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("🎉", fontSize = 32.sp)
                                    Text("Pro Subscription Unlocked!", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = colors.accent)
                                    Text("Unlimited calculations, custom themes, and full charting enabled.", fontSize = 11.sp, color = colors.textSecondary, textAlign = TextAlign.Center)
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedButton(
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    viewModel.downgradeToFree()
                                    telemetryLogs.add("[Stripe] Subscription canceled by user.")
                                    telemetryLogs.add("[Mixpanel] Event tracked: plan_downgraded (tier=Free)")
                                    Toast.makeText(context, "Downgraded to Free Tier. Stripe subscription canceled.", Toast.LENGTH_SHORT).show()
                                },
                                border = BorderStroke(1.dp, colors.borderGlass),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.textSecondary),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Cancel Stripe Subscription", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }

        // C. Mixpanel & Sentry Live Log Feed
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF070B1E)),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, Color(0xFF1E3A8A)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "📊 LIVE TELEMETRY LOGS (MIXPANEL / SENTRY)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF00FFCC),
                            letterSpacing = 1.sp
                        )
                        IconButton(
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                telemetryLogs.clear()
                                telemetryLogs.add("[Telemetry] Logs cleared.")
                            },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Clear logs", tint = Color.Gray, modifier = Modifier.size(16.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .background(Color(0xFF020410), RoundedCornerShape(12.dp))
                            .border(1.dp, Color(0xFF121B35), RoundedCornerShape(12.dp))
                            .padding(10.dp)
                    ) {
                        if (telemetryLogs.isEmpty()) {
                            Text("No telemetry events yet.", fontSize = 10.sp, color = Color.DarkGray, fontFamily = FontFamily.Monospace)
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(4.dp),
                                reverseLayout = true
                            ) {
                                items(telemetryLogs.toList().reversed()) { log ->
                                    val isSentry = log.contains("Sentry")
                                    val isSupabase = log.contains("Supabase")
                                    val isStripe = log.contains("Stripe")
                                    val logColor = when {
                                        isSentry -> Color(0xFFEF4444)
                                        isSupabase -> Color(0xFF10B981)
                                        isStripe -> Color(0xFFFFB500)
                                        else -> Color(0xFF38BDF8)
                                    }
                                    Text(
                                        text = log,
                                        fontSize = 9.sp,
                                        fontFamily = FontFamily.Monospace,
                                        color = logColor,
                                        lineHeight = 11.sp
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Real-time verification of Sentry capturing fatal errors and Mixpanel streaming user engagement properties on our globally optimized edge network.",
                        fontSize = 10.sp,
                        color = Color.Gray,
                        lineHeight = 13.sp
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// SUB-TAB 3: SCALING ROADMAP & DEPLOYMENT DOCS
// -------------------------------------------------------------
@Composable
fun ScalingHubView(colors: ThemeColors) {
    var expandedDocId by remember { mutableStateOf("architecture") }
    val haptic = LocalHapticFeedback.current

    val documentation = listOf(
        ScalingDoc(
            id = "architecture",
            title = "🌌 Modern Microservices Architecture",
            summary = "A scalable structure utilizing edge deployment for zero-cold starts, complete offline-first database synchronization, and robust enterprise authentication.",
            content = """
                • FRONTEND: Next.js 14 and React 18 deployed directly on Vercel's global Edge Network, securing 99.99% global performance.
                • DATABASE: PostgreSQL instance via Supabase with connection poolers managing up to 10,000 concurrent writes per second.
                • REAL-TIME SYNC: Bi-directional WebSocket syncing utilizing Supabase Realtime channels for collaborative shared workspaces.
                • DISASTER RECOVERY: Daily automated snapshots with multi-region read replicas distributed in US-East, EU-Central, and Asia-Pacific.
            """.trimIndent()
        ),
        ScalingDoc(
            id = "deployment",
            title = "🚀 Investor-Grade Deployment Guide",
            summary = "How the entire multi-platform ecosystem is deployed to AWS/Vercel/Supabase and configured securely.",
            content = """
                1. INFRASTRUCTURE PROVISIONING:
                   • Run 'terraform apply' to launch database clusters and IAM roles automatically.
                   • Configure SSL Certificates on Cloudflare utilizing Strict Security (HSTS) and rate limiters.
                
                2. ENVIRONMENT ENVIRONMENT VARIABLES:
                   • Inject NEXT_PUBLIC_SUPABASE_URL and SUPABASE_SERVICE_ROLE_KEY into Vercel dashboard.
                   • Configure Stripe endpoint webhooks in the AWS Lambda console to process checkout alerts securely.
                   
                3. STORE SUBMISSIONS:
                   • Run Fastlane automated scripts to build, test, and release the Android production-ready bundles directly to Google Play Developer Console.
            """.trimIndent()
        ),
        ScalingDoc(
            id = "seo",
            title = "📈 SEO Strategy & Conversion Plan",
            summary = "How we acquire millions of organic users on search engines using automated mathematical programmatic landing pages.",
            content = """
                • PROGRAMMATIC SEO: Generate 150,000 dynamic URLs covering calculations (e.g. "Calculate 35 percent of 4000", "Quadratic formula solver") targeting long-tail searches.
                • PERFORMANCE MARGINS: Focus landing pages to score 100/100 on Google Lighthouse core web vitals.
                • BACKLINK OUTREACH: Establish content partnerships with tech publications, developer forums, and open-source universities.
                • RICH SNIPPETS: Embed structured Schema.json math markups into blogs and tools to capture direct Google Rich Answer answer panels.
            """.trimIndent()
        ),
        ScalingDoc(
            id = "growth",
            title = "👑 Growth Strategies & Metrics (KPIs)",
            summary = "Monetization benchmarks, acquisition roadmap, and enterprise SaaS customer milestones.",
            content = """
                • USER RETENTION: Dynamic streaks, math milestones, and gamified accomplishment badges.
                • LTV CONVERSION: Proposing upgrade context triggers on advanced operations to lift LTV:CAC ratios to 4.5x+.
                • REVENUE BENCHMARKS: Target $1,000 MRR by month 3, expanding to $10,000 MRR by month 6, and hitting $50,000 MRR by month 12.
                • ENTERPRISE CAPABILITIES: License whitelist packages with custom company subdomain, single sign-on (SSO), and absolute compliance SLAs.
            """.trimIndent()
        )
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        item {
            Text(
                text = "INVESTOR & DEPLOYMENT HUB",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = colors.accent,
                modifier = Modifier.padding(start = 4.dp)
            )
        }

        items(documentation) { doc ->
            val isExpanded = expandedDocId == doc.id
            Card(
                colors = CardDefaults.cardColors(containerColor = colors.surface),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(if (isExpanded) 1.5.dp else 1.dp, if (isExpanded) colors.accent else colors.borderGlass.copy(alpha = 0.4f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        expandedDocId = if (isExpanded) "" else doc.id
                    }
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = doc.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = colors.textPrimary,
                            modifier = Modifier.weight(0.9f)
                        )
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = "Toggle Doc",
                            tint = colors.accent
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = doc.summary,
                        fontSize = 11.sp,
                        color = colors.textSecondary,
                        lineHeight = 15.sp
                    )

                    AnimatedVisibility(visible = isExpanded) {
                        Column {
                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider(color = colors.borderGlass.copy(alpha = 0.3f))
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = doc.content,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                color = colors.textPrimary,
                                lineHeight = 16.sp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(colors.background.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                    .padding(12.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

data class ScalingDoc(
    val id: String,
    val title: String,
    val summary: String,
    val content: String
)
