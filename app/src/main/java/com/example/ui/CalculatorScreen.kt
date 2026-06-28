package com.example.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.HistoryItem
import com.example.ui.theme.CalculatorTheme
import com.example.ui.theme.ThemeColors
import com.example.ui.theme.getThemeColors
import com.example.viewmodel.CalculatorViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorAppScreen(
    viewModel: CalculatorViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val history by viewModel.historyState.collectAsStateWithLifecycle()
    val colors = getThemeColors(viewModel.activeTheme)

    var showHistorySheet by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        contentWindowInsets = WindowInsets.safeDrawing
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            colors.surfaceGlass.copy(alpha = 0.15f),
                            colors.background
                        ),
                        center = Offset(0.5f, 0.2f)
                    )
                )
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                // Header Bar (Title & Theme & History Buttons)
                HeaderBar(
                    viewModel = viewModel,
                    colors = colors,
                    onThemeClick = { showThemeDialog = true },
                    onHistoryClick = { showHistorySheet = true }
                )

                // Sub-Screen Tabs
                ScreenSelectorTabs(
                    activeScreen = viewModel.activeScreen,
                    onScreenSelected = { viewModel.switchScreen(it) },
                    colors = colors
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Screen Switcher
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    when (viewModel.activeScreen) {
                        "Calculator" -> {
                            CalculatorLayout(
                                viewModel = viewModel,
                                colors = colors,
                                isLandscape = isLandscape,
                                onHistoryClick = { showHistorySheet = true }
                            )
                        }
                        "Converter" -> {
                            ConverterLayout(
                                viewModel = viewModel,
                                colors = colors
                            )
                        }
                        "Graphing" -> {
                            GraphingLayout(
                                viewModel = viewModel,
                                colors = colors
                            )
                        }
                        "Hub" -> {
                            HubLayout(
                                viewModel = viewModel,
                                colors = colors
                            )
                        }
                    }
                }
            }

            // Theme selection Bottom Sheet Dialog
            if (showThemeDialog) {
                ThemeSelectionDialog(
                    activeTheme = viewModel.activeTheme,
                    colors = colors,
                    onThemeSelected = {
                        viewModel.changeTheme(it)
                        showThemeDialog = false
                    },
                    onDismiss = { showThemeDialog = false }
                )
            }

            // Calculation History Panel (Sheet Overlay)
            AnimatedVisibility(
                visible = showHistorySheet,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
                modifier = Modifier.fillMaxSize()
            ) {
                HistoryPanelOverlay(
                    historyList = history,
                    colors = colors,
                    onClose = { showHistorySheet = false },
                    onUseItem = { item ->
                        viewModel.useHistoryItem(item)
                        showHistorySheet = false
                    },
                    onClearHistory = { viewModel.clearHistory() }
                )
            }
        }
    }
}

@Composable
fun HeaderBar(
    viewModel: CalculatorViewModel,
    colors: ThemeColors,
    onThemeClick: () -> Unit,
    onHistoryClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(Color(0xFFD4AF37), Color(0xFFF4E4BC), Color(0xFFD4AF37))
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )
            ) {
                Text(
                    text = "CITC",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF0B1437),
                    fontFamily = FontFamily.Serif
                )
            }
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Classic IT",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary,
                        fontFamily = FontFamily.Serif
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "👑",
                        fontSize = 16.sp
                    )
                }
                Text(
                    text = "Precision Engineered. Beautifully Calculated.",
                    fontSize = 10.sp,
                    color = colors.textSecondary,
                    fontFamily = FontFamily.SansSerif
                )
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            // Theme selector icon
            IconButton(
                onClick = onThemeClick,
                modifier = Modifier
                    .background(colors.surfaceGlass, RoundedCornerShape(12.dp))
                    .border(1.dp, colors.borderGlass, RoundedCornerShape(12.dp))
                    .testTag("theme_selector_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Palette,
                    contentDescription = "Change Theme",
                    tint = colors.secondary
                )
            }

            // History icon
            IconButton(
                onClick = onHistoryClick,
                modifier = Modifier
                    .background(colors.surfaceGlass, RoundedCornerShape(12.dp))
                    .border(1.dp, colors.borderGlass, RoundedCornerShape(12.dp))
                    .testTag("history_button")
            ) {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = "View History",
                    tint = colors.accent
                )
            }
        }
    }
}

@Composable
fun ScreenSelectorTabs(
    activeScreen: String,
    onScreenSelected: (String) -> Unit,
    colors: ThemeColors
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.surfaceGlass, RoundedCornerShape(16.dp))
            .border(1.dp, colors.borderGlass, RoundedCornerShape(16.dp))
            .padding(4.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        val screens = listOf("Calculator", "Converter", "Graphing", "Hub")
        screens.forEach { screen ->
            val isActive = activeScreen == screen
            val bgCol = if (isActive) colors.surface else Color.Transparent
            val textCol = if (isActive) colors.secondary else colors.textSecondary
            val borderBrush = if (isActive) {
                BorderStroke(1.dp, colors.borderGlass)
            } else {
                null
            }

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(bgCol)
                    .then(if (borderBrush != null) Modifier.border(borderBrush.width, borderBrush.brush, RoundedCornerShape(12.dp)) else Modifier)
                    .clickable { onScreenSelected(screen) }
                    .padding(vertical = 10.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = when (screen) {
                            "Calculator" -> Icons.Default.Calculate
                            "Converter" -> Icons.Default.SwapHoriz
                            "Graphing" -> Icons.Default.ShowChart
                            else -> Icons.Default.Layers
                        },
                        contentDescription = screen,
                        tint = textCol,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = screen,
                        color = textCol,
                        fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

@Composable
fun CalculatorLayout(
    viewModel: CalculatorViewModel,
    colors: ThemeColors,
    isLandscape: Boolean,
    onHistoryClick: () -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Display Area (Top)
        DisplaySection(
            expression = viewModel.expression,
            result = viewModel.result,
            isDegreeMode = viewModel.isDegreeMode,
            colors = colors,
            modifier = Modifier
                .fillMaxWidth()
                .weight(if (isLandscape) 0.35f else 0.25f)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Toggle / Quick function bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // DEG/RAD Switch button
            Button(
                onClick = { viewModel.onButtonClick("DEG") },
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.surfaceGlass,
                    contentColor = colors.textPrimary
                ),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, colors.borderGlass),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                modifier = Modifier.testTag("deg_rad_toggle")
            ) {
                Text(
                    text = if (viewModel.isDegreeMode) "DEG" else "RAD",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }

            // Memory Status Indicator
            if (viewModel.memoryValue != 0.0) {
                Box(
                    modifier = Modifier
                        .background(colors.surfaceGlass, RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "M = ${viewModel.memoryValue}",
                        color = colors.secondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Copy Result Button
            IconButton(
                onClick = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("SciCalc Result", viewModel.result)
                    clipboard.setPrimaryClip(clip)
                    Toast.makeText(context, "Copied to clipboard!", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .background(colors.surfaceGlass, RoundedCornerShape(12.dp))
                    .border(1.dp, colors.borderGlass, RoundedCornerShape(12.dp))
                    .testTag("copy_result_button")
            ) {
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = "Copy Result",
                    tint = colors.textPrimary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Keyboard grid
        if (isLandscape) {
            LandscapeKeyboard(viewModel = viewModel, colors = colors)
        } else {
            PortraitKeyboard(viewModel = viewModel, colors = colors)
        }
    }
}

@Composable
fun DisplaySection(
    expression: String,
    result: String,
    isDegreeMode: Boolean,
    colors: ThemeColors,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(colors.surfaceGlass, RoundedCornerShape(24.dp))
            .border(1.5.dp, colors.borderGlass, RoundedCornerShape(24.dp))
            .padding(20.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.End
        ) {
            // Expression Panel
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.TopEnd
            ) {
                Text(
                    text = expression.ifEmpty { " " },
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        color = colors.textSecondary,
                        textAlign = TextAlign.End
                    ),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Results Panel
            Text(
                text = result,
                style = MaterialTheme.typography.displayMedium.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary,
                    fontSize = 36.sp,
                    textAlign = TextAlign.End
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.testTag("result_text_display")
            )
        }
    }
}

@Composable
fun PortraitKeyboard(viewModel: CalculatorViewModel, colors: ThemeColors) {
    val configuration = LocalConfiguration.current
    val isSmallScreen = configuration.screenHeightDp < 720

    // Standard keyboard plus a scrollable or tabbed scientific pad on top
    var showScientificOnly by remember { mutableStateOf(!isSmallScreen) }

    val btnPadding = if (isSmallScreen) 8.dp else 14.dp
    val btnFontSize = if (isSmallScreen) 15.sp else 18.sp

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        // Toggle for scientific operations row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (showScientificOnly) "Scientific Operations" else "Basic Pad Only",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = colors.textSecondary
            )
            Text(
                text = if (showScientificOnly) "Hide" else "Show",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = colors.secondary,
                modifier = Modifier
                    .clickable { showScientificOnly = !showScientificOnly }
                    .padding(vertical = 2.dp, horizontal = 8.dp)
            )
        }

        AnimatedVisibility(
            visible = showScientificOnly,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // First Row: sin, cos, tan, ln, log
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    listOf("sin", "cos", "tan", "ln", "log").forEach { label ->
                        CalculatorButton(
                            text = label,
                            onClick = { viewModel.onButtonClick(label) },
                            backgroundColor = colors.surfaceGlass,
                            contentColor = colors.secondary,
                            modifier = Modifier.weight(1f),
                            verticalPadding = btnPadding,
                            fontSize = btnFontSize
                        )
                    }
                }
                // Second Row: asin, acos, atan, √, ∛
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    listOf("asin", "acos", "atan", "√", "∛").forEach { label ->
                        CalculatorButton(
                            text = label,
                            onClick = { viewModel.onButtonClick(label) },
                            backgroundColor = colors.surfaceGlass,
                            contentColor = colors.secondary,
                            modifier = Modifier.weight(1f),
                            verticalPadding = btnPadding,
                            fontSize = btnFontSize
                        )
                    }
                }
                // Third Row: x², x³, x^y, π, e
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    listOf("x²", "x³", "x^y", "π", "e").forEach { label ->
                        CalculatorButton(
                            text = label,
                            onClick = { viewModel.onButtonClick(label) },
                            backgroundColor = colors.surfaceGlass,
                            contentColor = colors.secondary,
                            modifier = Modifier.weight(1f),
                            verticalPadding = btnPadding,
                            fontSize = btnFontSize
                        )
                    }
                }
                // Fourth Row: abs, 10^x, e^x, (, )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    listOf("abs", "10^x", "e^x", "(", ")").forEach { label ->
                        CalculatorButton(
                            text = label,
                            onClick = { viewModel.onButtonClick(label) },
                            backgroundColor = colors.surfaceGlass,
                            contentColor = colors.secondary,
                            modifier = Modifier.weight(1f),
                            verticalPadding = btnPadding,
                            fontSize = btnFontSize
                        )
                    }
                }
            }
        }

        // Standard Numbers and Basic Operators Pad (4x5 Grid)
        val rows = listOf(
            listOf("MC", "MR", "M+", "M-", "AC"),
            listOf("7", "8", "9", "DEL", "÷"),
            listOf("4", "5", "6", "%", "×"),
            listOf("1", "2", "3", "mod", "-"),
            listOf("0", ".", "!", "=", "+")
        )

        rows.forEach { row ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                row.forEach { label ->
                    val isNum = label.all { it.isDigit() } || label == "."
                    val isEquals = label == "="
                    val isFn = label in listOf("MC", "MR", "M+", "M-", "AC", "DEL", "mod", "!")

                    val bgCol = when {
                        isEquals -> colors.btnEquals
                        isFn -> colors.btnOperator.copy(alpha = 0.65f)
                        isNum -> colors.btnNumber
                        else -> colors.btnOperator
                    }

                    val textCol = when {
                        isEquals || isFn -> colors.textPrimary
                        isNum -> colors.textPrimary
                        else -> colors.secondary
                    }

                    CalculatorButton(
                        text = label,
                        onClick = { viewModel.onButtonClick(label) },
                        backgroundColor = bgCol,
                        contentColor = textCol,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_$label"),
                        verticalPadding = btnPadding,
                        fontSize = btnFontSize
                    )
                }
            }
        }
    }
}

@Composable
fun LandscapeKeyboard(viewModel: CalculatorViewModel, colors: ThemeColors) {
    // In landscape mode we display all scientific functions on the left side
    // and the normal numbers on the right side.
    // Use very compact padding for landscape
    val btnPadding = 6.dp
    val btnFontSize = 14.sp

    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        // Left Column: Scientific and Advanced buttons (4x6 Grid)
        Column(
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.weight(1.2f)
        ) {
            val rowsLeft = listOf(
                listOf("sin", "cos", "tan", "ln", "log", "√"),
                listOf("asin", "acos", "atan", "∛", "abs", "e^x"),
                listOf("x²", "x³", "x^y", "10^x", "1/x", "e"),
                listOf("π", "(", ")", "!", "%", "mod")
            )
            rowsLeft.forEach { row ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    row.forEach { label ->
                        CalculatorButton(
                            text = label,
                            onClick = { viewModel.onButtonClick(label) },
                            backgroundColor = colors.surfaceGlass,
                            contentColor = colors.secondary,
                            modifier = Modifier.weight(1f),
                            verticalPadding = btnPadding,
                            fontSize = btnFontSize
                        )
                    }
                }
            }
        }

        // Right Column: Numbers, operators, clear, memory (4x5 Grid)
        Column(
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.weight(1f)
        ) {
            val rowsRight = listOf(
                listOf("MC", "MR", "M+", "M-", "AC"),
                listOf("7", "8", "9", "DEL", "÷"),
                listOf("4", "5", "6", "×", "-"),
                listOf("1", "2", "3", "0", "+"),
                listOf(".", "=", "")
            )
            rowsRight.forEach { row ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    row.forEach { label ->
                        if (label.isEmpty()) {
                            Spacer(modifier = Modifier.weight(1f))
                        } else {
                            val isNum = label.all { it.isDigit() } || label == "."
                            val isEquals = label == "="
                            val isFn = label in listOf("MC", "MR", "M+", "M-", "AC", "DEL")

                            val bgCol = when {
                                isEquals -> colors.btnEquals
                                isFn -> colors.btnOperator.copy(alpha = 0.65f)
                                isNum -> colors.btnNumber
                                else -> colors.btnOperator
                            }

                            val textCol = when {
                                isEquals || isFn -> colors.textPrimary
                                isNum -> colors.textPrimary
                                else -> colors.secondary
                            }

                            CalculatorButton(
                                text = label,
                                onClick = { viewModel.onButtonClick(label) },
                                backgroundColor = bgCol,
                                contentColor = textCol,
                                modifier = Modifier
                                    .weight(if (label == "=") 2f else 1f)
                                    .testTag("btn_$label"),
                                verticalPadding = btnPadding,
                                fontSize = btnFontSize
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CalculatorButton(
    text: String,
    onClick: () -> Unit,
    backgroundColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier,
    verticalPadding: androidx.compose.ui.unit.Dp = 14.dp,
    fontSize: androidx.compose.ui.unit.TextUnit = 18.sp
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.90f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "ButtonPressScaleAnim"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(RoundedCornerShape(20.dp))
            .background(backgroundColor)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(bounded = true, color = contentColor.copy(alpha = 0.2f)),
                onClick = onClick
            )
            .padding(vertical = verticalPadding)
    ) {
        Text(
            text = text,
            color = contentColor,
            fontSize = fontSize,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.SansSerif,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ConverterLayout(
    viewModel: CalculatorViewModel,
    colors: ThemeColors
) {
    var expandedType by remember { mutableStateOf(false) }
    var expandedFrom by remember { mutableStateOf(false) }
    var expandedTo by remember { mutableStateOf(false) }

    val lengthUnits = listOf("m", "km", "cm", "mm", "in", "ft")
    val weightUnits = listOf("kg", "g", "lbs", "oz")
    val tempUnits = listOf("°C", "°F", "K")

    val activeUnits = when (viewModel.converterType) {
        "Length" -> lengthUnits
        "Weight" -> weightUnits
        else -> tempUnits
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(colors.surfaceGlass, RoundedCornerShape(24.dp))
            .border(1.5.dp, colors.borderGlass, RoundedCornerShape(24.dp))
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Unit Converter",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = colors.textPrimary
        )

        // Converter Type Dropdown / Select
        Column {
            Text(text = "Converter Mode", fontSize = 12.sp, color = colors.textSecondary)
            Spacer(modifier = Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colors.surface, RoundedCornerShape(14.dp))
                    .border(1.dp, colors.borderGlass, RoundedCornerShape(14.dp))
                    .clickable { expandedType = true }
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = viewModel.converterType, color = colors.secondary, fontWeight = FontWeight.Bold)
                    Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "Dropdown", tint = colors.textPrimary)
                }
                DropdownMenu(
                    expanded = expandedType,
                    onDismissRequest = { expandedType = false },
                    modifier = Modifier.background(colors.surface)
                ) {
                    listOf("Length", "Weight", "Temperature").forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type, color = colors.textPrimary) },
                            onClick = {
                                viewModel.updateConverterType(type)
                                expandedType = false
                            }
                        )
                    }
                }
            }
        }

        // From Unit and Input Value
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "From Unit", fontSize = 12.sp, color = colors.textSecondary)
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(colors.surface, RoundedCornerShape(14.dp))
                        .border(1.dp, colors.borderGlass, RoundedCornerShape(14.dp))
                        .clickable { expandedFrom = true }
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = viewModel.converterFromUnit, color = colors.textPrimary)
                        Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "Dropdown", tint = colors.textPrimary)
                    }
                    DropdownMenu(
                        expanded = expandedFrom,
                        onDismissRequest = { expandedFrom = false },
                        modifier = Modifier.background(colors.surface)
                    ) {
                        activeUnits.forEach { unit ->
                            DropdownMenuItem(
                                text = { Text(unit, color = colors.textPrimary) },
                                onClick = {
                                    viewModel.updateConverterUnits(unit, viewModel.converterToUnit)
                                    expandedFrom = false
                                }
                            )
                        }
                    }
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(text = "To Unit", fontSize = 12.sp, color = colors.textSecondary)
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(colors.surface, RoundedCornerShape(14.dp))
                        .border(1.dp, colors.borderGlass, RoundedCornerShape(14.dp))
                        .clickable { expandedTo = true }
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = viewModel.converterToUnit, color = colors.textPrimary)
                        Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "Dropdown", tint = colors.textPrimary)
                    }
                    DropdownMenu(
                        expanded = expandedTo,
                        onDismissRequest = { expandedTo = false },
                        modifier = Modifier.background(colors.surface)
                    ) {
                        activeUnits.forEach { unit ->
                            DropdownMenuItem(
                                text = { Text(unit, color = colors.textPrimary) },
                                onClick = {
                                    viewModel.updateConverterUnits(viewModel.converterFromUnit, unit)
                                    expandedTo = false
                                }
                            )
                        }
                    }
                }
            }
        }

        // Input Field
        Column {
            Text(text = "Enter Value", fontSize = 12.sp, color = colors.textSecondary)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = viewModel.converterInput,
                onValueChange = { viewModel.updateConverterInput(it) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = colors.textPrimary,
                    unfocusedTextColor = colors.textPrimary,
                    focusedBorderColor = colors.secondary,
                    unfocusedBorderColor = colors.borderGlass,
                    focusedContainerColor = colors.surface,
                    unfocusedContainerColor = colors.surface
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("converter_input_field"),
                singleLine = true,
                placeholder = { Text("0.0", color = colors.textSecondary) }
            )
        }

        // Conversion Result Display
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(colors.surface.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                .border(1.dp, colors.borderGlass.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Converted Result",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.secondary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "${viewModel.converterResult} ${viewModel.converterToUnit}",
                    fontSize = 24.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary,
                    modifier = Modifier.testTag("converter_result_text")
                )
            }
        }
    }
}

@Composable
fun GraphingLayout(
    viewModel: CalculatorViewModel,
    colors: ThemeColors
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Equation Manager card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(colors.surfaceGlass, RoundedCornerShape(20.dp))
                .border(1.5.dp, colors.borderGlass, RoundedCornerShape(20.dp))
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Formula Plotter",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary
            )

            // Add Custom Equation Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = viewModel.graphInputText,
                    onValueChange = { viewModel.graphInputText = it },
                    placeholder = { Text("e.g. sin(x) + x", fontSize = 13.sp, color = colors.textSecondary) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = colors.textPrimary,
                        unfocusedTextColor = colors.textPrimary,
                        focusedBorderColor = colors.secondary,
                        unfocusedBorderColor = colors.borderGlass,
                        focusedContainerColor = colors.surface,
                        unfocusedContainerColor = colors.surface
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .testTag("graph_equation_input"),
                    singleLine = true
                )

                Button(
                    onClick = { viewModel.addEquation(viewModel.graphInputText) },
                    colors = ButtonDefaults.buttonColors(containerColor = colors.secondary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .height(52.dp)
                        .testTag("add_equation_button")
                ) {
                    Text("Plot", color = colors.background, fontWeight = FontWeight.Bold)
                }
            }

            // Plotted Equations Chip List
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                viewModel.selectedGraphEquations.forEachIndexed { idx, eq ->
                    val plotColor = when (idx % 4) {
                        0 -> colors.secondary
                        1 -> colors.accent
                        2 -> Color(0xFF30D158)
                        else -> Color(0xFFFFCC00)
                    }

                    Box(
                        modifier = Modifier
                            .background(plotColor.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                            .border(1.dp, plotColor, RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Canvas(modifier = Modifier.size(8.dp)) {
                                drawCircle(color = plotColor)
                            }
                            Text(
                                text = "y = $eq",
                                color = colors.textPrimary,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Remove Equation",
                                tint = colors.textPrimary.copy(alpha = 0.5f),
                                modifier = Modifier
                                    .size(14.dp)
                                    .clickable { viewModel.removeEquation(eq) }
                            )
                        }
                    }
                }
            }
        }

        // Coordinate Plane Plot Canvas
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(colors.background, RoundedCornerShape(24.dp))
                .border(2.dp, colors.borderGlass, RoundedCornerShape(24.dp))
                .clip(RoundedCornerShape(24.dp))
        ) {
            GraphingCanvas(
                equations = viewModel.selectedGraphEquations,
                viewModel = viewModel,
                colors = colors,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            )

            // Canvas coordinate guide text
            Text(
                text = "X: [-10, 10], Y: [-10, 10]",
                color = colors.textSecondary.copy(alpha = 0.7f),
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(12.dp)
            )
        }
    }
}

@Composable
fun GraphingCanvas(
    equations: List<String>,
    viewModel: CalculatorViewModel,
    colors: ThemeColors,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val minX = -10.0
        val maxX = 10.0
        val minY = -10.0
        val maxY = 10.0

        // Helpers to convert math coordinates to pixel coordinate floats
        fun toPixelX(mathX: Double): Float {
            return ((mathX - minX) / (maxX - minX) * width).toFloat()
        }

        fun toPixelY(mathY: Double): Float {
            return ((1.0 - (mathY - minY) / (maxY - minY)) * height).toFloat()
        }

        // Grid lines
        for (gx in -10..10) {
            val px = toPixelX(gx.toDouble())
            drawLine(
                color = colors.textSecondary.copy(alpha = 0.08f),
                start = Offset(px, 0f),
                end = Offset(px, height),
                strokeWidth = 1f
            )
        }
        for (gy in -10..10) {
            val py = toPixelY(gy.toDouble())
            drawLine(
                color = colors.textSecondary.copy(alpha = 0.08f),
                start = Offset(0f, py),
                end = Offset(width, py),
                strokeWidth = 1f
            )
        }

        // Main axes
        val axisX = toPixelX(0.0)
        val axisY = toPixelY(0.0)
        // Horizontal X-axis
        drawLine(
            color = colors.textSecondary.copy(alpha = 0.35f),
            start = Offset(0f, axisY),
            end = Offset(width, axisY),
            strokeWidth = 2.5f
        )
        // Vertical Y-axis
        drawLine(
            color = colors.textSecondary.copy(alpha = 0.35f),
            start = Offset(axisX, 0f),
            end = Offset(axisX, height),
            strokeWidth = 2.5f
        )

        // Plot equations
        equations.forEachIndexed { index, equation ->
            val path = Path()
            var first = true

            val plotColor = when (index % 4) {
                0 -> colors.secondary
                1 -> colors.accent
                2 -> Color(0xFF30D158)
                else -> Color(0xFFFFCC00)
            }

            val samples = 350
            for (step in 0..samples) {
                val fraction = step.toDouble() / samples
                val mathX = minX + fraction * (maxX - minX)
                val mathY = viewModel.evaluateGraphY(equation, mathX)

                if (!mathY.isNaN() && !mathY.isInfinite()) {
                    val px = toPixelX(mathX)
                    val py = toPixelY(mathY)

                    // Soft bounds check to prevent extreme line breaks
                    if (py >= -height && py <= height * 2f) {
                        if (first) {
                            path.moveTo(px, py)
                            first = false
                        } else {
                            path.lineTo(px, py)
                        }
                    } else {
                        first = true
                    }
                } else {
                    first = true
                }
            }

            drawPath(
                path = path,
                color = plotColor,
                style = Stroke(width = 4.5f, join = StrokeJoin.Round, cap = StrokeCap.Round)
            )
        }
    }
}

@Composable
fun ThemeSelectionDialog(
    activeTheme: CalculatorTheme,
    colors: ThemeColors,
    onThemeSelected: (CalculatorTheme) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Aesthetic Canvas Style", color = colors.textPrimary) },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                CalculatorTheme.values().forEach { theme ->
                    val themeSampleColors = getThemeColors(theme)
                    val isSelected = activeTheme == theme
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) colors.surfaceGlass else Color.Transparent)
                            .border(
                                1.dp,
                                if (isSelected) colors.secondary else Color.Transparent,
                                RoundedCornerShape(12.dp)
                            )
                            .clickable { onThemeSelected(theme) }
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Circular palette preview
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(themeSampleColors.background)
                                    .border(1.dp, themeSampleColors.borderGlass, CircleShape)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .align(Alignment.Center)
                                        .clip(CircleShape)
                                        .background(themeSampleColors.secondary)
                                )
                            }
                            Text(
                                text = theme.displayName,
                                color = colors.textPrimary,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                        if (isSelected) {
                            Icon(imageVector = Icons.Default.Check, contentDescription = "Active", tint = colors.secondary)
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = colors.secondary)
            }
        },
        containerColor = colors.surface,
        shape = RoundedCornerShape(24.dp)
    )
}

@Composable
fun HistoryPanelOverlay(
    historyList: List<HistoryItem>,
    colors: ThemeColors,
    onClose: () -> Unit,
    onUseItem: (HistoryItem) -> Unit,
    onClearHistory: () -> Unit
) {
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable { onClose() }
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .fillMaxHeight(0.7f)
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .background(colors.surface)
                .clickable(enabled = false) {}
                .padding(20.dp)
        ) {
            // Drag handle / Header
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(colors.textSecondary.copy(alpha = 0.3f))
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Calculation History",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Export/Share button
                    IconButton(
                        onClick = {
                            if (historyList.isEmpty()) {
                                Toast.makeText(context, "History is empty", Toast.LENGTH_SHORT).show()
                                return@IconButton
                            }
                            val shareText = historyList.joinToString("\n") {
                                "${it.expression} = ${it.result}"
                            }
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, shareText)
                            }
                            context.startActivity(Intent.createChooser(intent, "Share Calculation History"))
                        },
                        modifier = Modifier.testTag("export_history_button")
                    ) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = "Share", tint = colors.secondary)
                    }

                    // Clear button
                    IconButton(
                        onClick = onClearHistory,
                        modifier = Modifier.testTag("clear_history_button")
                    ) {
                        Icon(imageVector = Icons.Default.DeleteSweep, contentDescription = "Clear All", tint = colors.accent)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (historyList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.HistoryToggleOff,
                            contentDescription = "Empty",
                            tint = colors.textSecondary.copy(alpha = 0.4f),
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = "No calculations yet",
                            color = colors.textSecondary.copy(alpha = 0.6f),
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(historyList) { item ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(colors.surfaceGlass)
                                .border(1.dp, colors.borderGlass.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                                .clickable { onUseItem(item) }
                                .padding(16.dp)
                        ) {
                            Text(
                                text = item.expression,
                                fontSize = 14.sp,
                                fontFamily = FontFamily.Monospace,
                                color = colors.textSecondary,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.End
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "= ${item.result}",
                                fontSize = 18.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = colors.secondary,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.End
                            )
                        }
                    }
                }
            }

            Button(
                onClick = onClose,
                colors = ButtonDefaults.buttonColors(containerColor = colors.secondary),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Close Panel", color = colors.background, fontWeight = FontWeight.Bold)
            }
        }
    }
}
