package com.example.glimpse

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.media.AudioManager
import android.provider.Settings
import android.view.WindowManager
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.navigation.NavController
import com.example.glimpse.customization.WidgetPosition
import com.example.glimpse.notification.NotificationManager
import com.example.glimpse.voice.PicovoiceWakeWordListener
import com.example.glimpse.voice.VoiceAction
import com.example.glimpse.voice.WakeWordListener
import com.example.glimpse.weather.scheduleWeatherUpdate
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HUDScreen(
    navController: NavController,
    isPreview: Boolean = false,
    sharedViewModel: SharedViewModel
) {
    val context = LocalContext.current
    val window = (context as Activity).window
    val coroutineScope = rememberCoroutineScope()
    val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    if (!isPreview) {
        NotificationManager(sharedViewModel)
        // Force device into landscape mode
        (context as? Activity)?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    }

    var wakeWordListener by remember { mutableStateOf<WakeWordListener?>(null) }
    var selectedWidget by remember { mutableIntStateOf(0) }
    var adaptiveBrightness by remember { mutableStateOf(false) }
    var isMenuVisible by remember { mutableStateOf(true) }
    var timerJob by remember { mutableStateOf<Job?>(null) }

    // FR25 - Off.Widget: This toggle allows for control of turning the HUD on and off
    // Can be accomplished by a double tap or (soon to come) voice command
    val hudOn = remember { mutableStateOf(true) }

    fun setBrightness() {
        if (!isPreview) {
            val layoutParams = window.attributes
            layoutParams.screenBrightness = sharedViewModel.brightness
            window.attributes = layoutParams
        }
    }

    fun setVolume() {
        if (!isPreview) {
            val targetVolume =
                (audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC) * sharedViewModel.volume).toInt()
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, targetVolume, 0)
        }
    }

    // Start or restart the hide menu timer
    fun startMenuTimer() {
        isMenuVisible = true
        if (!isPreview) {
            timerJob?.cancel() // Cancel any previous job
            timerJob = coroutineScope.launch {
                delay(10000)
                isMenuVisible = false
            }
        }
    }

    LifecycleResumeEffect(Unit) {
        val originalVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        if (!isPreview) {
            // If the user has adaptive brightness on we want to disable this setting in the HUD
            adaptiveBrightness = Settings.System.getInt(
                context.contentResolver,
                Settings.System.SCREEN_BRIGHTNESS_MODE
            ) == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC
            if (adaptiveBrightness)
                Settings.System.putInt(
                    context.contentResolver,
                    Settings.System.SCREEN_BRIGHTNESS_MODE,
                    Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL
                )

            // Keep awake
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

            // Set to user saved settings
            setBrightness()
            setVolume()

            // Start scheduling location and weather fetching
            scheduleWeatherUpdate(context)

            // Setup hotword detection
            if (wakeWordListener == null)
                wakeWordListener =
                    PicovoiceWakeWordListener(
                        context,
                        "Glimpse.ppn",
                        "Glimpse.rhn",
                        onIntentRecognized = { intent, slots ->
                            selectedWidget = WidgetType.allWidgetTypes.indexOf(
                                VoiceAction.action(
                                    intent,
                                    slots,
                                    sharedViewModel,
                                    hudOn
                                ) ?: WidgetType.allWidgetTypes[selectedWidget]
                            )
                        },
                        onWakeWordDetected = {
                            startMenuTimer()
                        }
                    )
            sharedViewModel.wakeWordListener = wakeWordListener
            wakeWordListener?.startListening()

            // Hide system status and navigation bars
            val insetsController = WindowCompat.getInsetsController(
                context.window,
                context.window.decorView
            )
            insetsController.apply {
                hide(WindowInsetsCompat.Type.statusBars())
                hide(WindowInsetsCompat.Type.navigationBars())
            }
            startMenuTimer()
        }
        onPauseOrDispose {
            if (!isPreview) {
                wakeWordListener?.stopListening()
                sharedViewModel.wakeWordListener = null

                // Restore user settings
                if (adaptiveBrightness)
                    Settings.System.putInt(
                        context.contentResolver,
                        Settings.System.SCREEN_BRIGHTNESS_MODE,
                        Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC
                    )

                audioManager.setStreamVolume(
                    AudioManager.STREAM_MUSIC,
                    originalVolume,
                    0
                )

                window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            }
        }

    }

    LaunchedEffect(sharedViewModel.widgetMenuItems) {
        if (isPreview) {
            selectedWidget = 0
        }
    }

    LaunchedEffect(selectedWidget) {
        if (!sharedViewModel.isSingleWidgetMode) {
            val visibleWidgets = sharedViewModel.widgetMenuItems.filter { it.isVisible }
            val selectedWidgetType = visibleWidgets[selectedWidget].widget
            if (selectedWidgetType in listOf(
                    WidgetType.Barcode,
                    WidgetType.Face,
                    WidgetType.Chat
                )
            ) {
                // Find the current position of Barcode, Face, or Chat in widgetAssignments
                val currentPosition = sharedViewModel.widgetAssignments.entries
                    .find {
                        it.value in listOf(
                            WidgetType.Barcode,
                            WidgetType.Face,
                            WidgetType.Chat
                        )
                    }?.key

                if (currentPosition != null) {
                    // Update the grid spot with the newly selected widget
                    sharedViewModel.widgetAssignments =
                        sharedViewModel.widgetAssignments.toMutableMap().apply {
                            this[currentPosition] = selectedWidgetType
                        }
                }
            }
        }
    }


    // Ensure settings changes are applied
    LaunchedEffect(sharedViewModel.brightness) {
        setBrightness()
    }

    LaunchedEffect(sharedViewModel.volume) {
        setVolume()
    }

    var currentScroll by remember { mutableFloatStateOf(0f) }
    var hudHeight by remember { mutableIntStateOf(0) }
    var hudWidth by remember { mutableIntStateOf(0) }

    Box(
        modifier = Modifier
            .graphicsLayer { clip = true }
            .background(sharedViewModel.hudBackgroundColor)
    ) {
        Box(
            modifier = Modifier
                .graphicsLayer {
                    scaleX =
                        if (sharedViewModel.mirrorX) -sharedViewModel.scaleX else sharedViewModel.scaleX
                    scaleY =
                        if (sharedViewModel.mirrorY) -sharedViewModel.scaleY else sharedViewModel.scaleY
                    translationX = sharedViewModel.offsetX
                    translationY = sharedViewModel.offsetY
                    rotationX = sharedViewModel.rotationX
                    rotationY = sharedViewModel.rotationY
                    rotationZ = sharedViewModel.rotationZ
                }
                .fillMaxSize()
                .onGloballyPositioned {
                    hudHeight = it.size.height
                    hudWidth = it.size.width
                }
                .pointerInput(Unit) {
                    if (!isPreview) {
                        detectTapGestures(
                            onLongPress = { navController.popBackStack() },
                            onPress = {
                                val insetsController = WindowCompat.getInsetsController(
                                    context.window,
                                    context.window.decorView
                                )
                                insetsController.apply {
                                    hide(WindowInsetsCompat.Type.statusBars())
                                    hide(WindowInsetsCompat.Type.navigationBars())
                                }
                                startMenuTimer()
                            },
                            onDoubleTap = {
                                // FR25 - Off.Widget
                                // Users can double tap to toggle the HUD on and off
                                hudOn.value = !hudOn.value }
                        )
                        detectHorizontalDragGestures { _, _ ->
                            startMenuTimer()
                        }
                        detectVerticalDragGestures { _, _ ->
                            startMenuTimer()
                        }

                    }
                }
                .background(sharedViewModel.hudBackgroundColor)
        ) {
            if (hudOn.value) {
                Column(modifier = Modifier.padding(start = 8.dp, end = 8.dp)) {
                    // Display widgets based on mode
                    val menuHeight = with(LocalDensity.current) { (0.18f * hudHeight).toDp() }
                    val visibleWidgets = sharedViewModel.widgetMenuItems.filter { it.isVisible }
                    val gridHeight =
                        with(LocalDensity.current) { (hudHeight.toDp() - menuHeight) / 3 }
                    var gridWidth = with(LocalDensity.current) { (hudWidth.toDp() - 16.dp) / 3 }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(with(LocalDensity.current) { hudHeight.toDp() - menuHeight })
                    ) {
                        val previewText: @Composable () -> Unit = {
                            Text(
                                text = "Text",
                                style = TextStyle(
                                    color = sharedViewModel.hudForegroundColor,
                                    fontSize = 36.sp,
                                    fontFamily = sharedViewModel.selectedFont.fontResource,
                                ),
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                        if (sharedViewModel.isSingleWidgetMode) {
                            // Single widget mode: Display only the selected widget at the selected position
                            if (selectedWidget >= visibleWidgets.size)
                                selectedWidget = 0
                            val widget = visibleWidgets[selectedWidget].widget
                            val toDisplay: @Composable () -> Unit = if (isPreview) {
                                previewText
                            } else {
                                widget.composable(sharedViewModel)
                            }
                            // Special case for Settings widget - always center it
                            if (widget == WidgetType.Settings) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.Center)
                                        .fillMaxSize()
                                ) {
                                    toDisplay.invoke()
                                }
                            } else {
                                // Normal widget behavior
                                if (sharedViewModel.selectedPosition == WidgetPosition.Top) {
                                    gridWidth =
                                        with(LocalDensity.current) { (hudWidth.toDp() - 16.dp) }
                                }
                                Box(
                                    modifier = Modifier
                                        .align(sharedViewModel.selectedPosition.toAlignment())
                                        .sizeIn(
                                            minWidth = gridWidth,
                                            minHeight = gridHeight,
                                            maxWidth = gridWidth,
                                            maxHeight = gridHeight
                                        )
                                ) {
                                    toDisplay.invoke()
                                }
                            }
                        } else {
                            // Multi-widget mode logic with Settings special case
                            sharedViewModel.widgetAssignments.forEach { (position, widget) ->
                                if (widget != null) {
                                    val toDisplay: @Composable () -> Unit = if (isPreview) {
                                        previewText
                                    } else {
                                        widget.composable(sharedViewModel)
                                    }
                                    if (widget == WidgetType.Settings) {
                                        // Special full-screen centered display for Settings
                                        Box(
                                            modifier = Modifier
                                                .align(Alignment.Center)
                                                .fillMaxSize()
                                        ) {
                                            toDisplay.invoke()
                                        }
                                    } else {
                                        // Normal widget positioning
                                        if (position == WidgetPosition.Top) {
                                            gridWidth =
                                                with(LocalDensity.current) { (hudWidth.toDp() - 16.dp) }
                                        }
                                        Box(
                                            modifier = Modifier
                                                .align(position.toAlignment())
                                                .sizeIn(
                                                    minWidth = gridWidth,
                                                    minHeight = gridHeight,
                                                    maxWidth = gridWidth,
                                                    maxHeight = gridHeight
                                                )
                                        ) {
                                            toDisplay.invoke()
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Bottom menu
                    Box(modifier = Modifier
                        .height(menuHeight)
                        .padding(0.dp, 0.dp, 0.dp, 8.dp)) {
                        if (isMenuVisible) {
                            PrimaryTabRow(
                                selectedTabIndex = selectedWidget,
                                containerColor = Color.DarkGray.copy(alpha = sharedViewModel.hudMenuOpacity),
                                contentColor = sharedViewModel.hudForegroundColor,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))

                                    .scrollable(
                                        orientation = Orientation.Vertical,
                                        state = rememberScrollableState { delta ->
                                            // FR6 - Control.Widget
                                            // Allow the user to scroll on the menu bar to change the selected widget
                                            currentScroll += delta
                                            if (abs(currentScroll) >= 45) {
                                                // We have scrolled far enough to change tabs
                                                val change = if (currentScroll > 0) 1 else -1
                                                val curVisibleWidgets =
                                                    sharedViewModel.widgetMenuItems.filter { it.isVisible }
                                                selectedWidget =
                                                    (selectedWidget + change + curVisibleWidgets.size) % curVisibleWidgets.size

                                                // Reset scroll accumulator
                                                currentScroll = 0f
                                            }
                                            delta
                                        }
                                    )
                                    .fillMaxSize(),
                                // Remove default selected indicator
                                divider = {},
                                indicator = {}
                            ) {
                                var menuFontSize by remember { mutableStateOf(24.sp) }
                                visibleWidgets.forEachIndexed { index, widgetMenuItem ->
                                    Tab(
                                        selected = selectedWidget == index,
                                        onClick = {
                                            // FR6 - Control.Widget
                                            // Allow the user to click on the menu bar to change the selected widget
                                            selectedWidget = index
                                        },
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(50))
                                            .height(menuHeight)
                                            .background(
                                                color = if (index == selectedWidget) sharedViewModel.hudForegroundColor else Color.Transparent,
                                                shape = RoundedCornerShape(50)
                                            ),
                                        selectedContentColor = sharedViewModel.hudForegroundColor
                                    ) {
                                        if (sharedViewModel.iconMode) {
                                            Icon(
                                                imageVector = widgetMenuItem.widget.icon,
                                                contentDescription = widgetMenuItem.widget.toString(),
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .padding(0.dp, 4.dp)
                                                    .align(Alignment.CenterHorizontally),
                                                tint = if (index == selectedWidget) sharedViewModel.hudBackgroundColor else sharedViewModel.hudForegroundColor
                                            )
                                        } else {
                                            Box(
                                                modifier = Modifier.fillMaxSize(),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = widgetMenuItem.widget.toString(),
                                                    maxLines = 1,
                                                    style = TextStyle(
                                                        color = if (index == selectedWidget) sharedViewModel.hudBackgroundColor else sharedViewModel.hudForegroundColor,
                                                        fontSize = menuFontSize,
                                                        fontFamily = sharedViewModel.selectedFont.fontResource,
                                                        fontWeight = sharedViewModel.hudFontWeight,
                                                        textAlign = TextAlign.Center
                                                    ),
                                                    onTextLayout = {
                                                        if (it.multiParagraph.didExceedMaxLines) {
                                                            // Shrink font size if needed
                                                            menuFontSize *= .9F
                                                        }
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            Spacer(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .scrollable(
                                        orientation = Orientation.Vertical,
                                        state = rememberScrollableState { delta ->
                                            startMenuTimer()
                                            delta
                                        })
                            )
                        }
                    }

                }
            }
        }
    }
}

// Extension function to convert WidgetPosition to Alignment
fun WidgetPosition.toAlignment(): Alignment {
    return when (this) {
        WidgetPosition.Top -> Alignment.TopCenter
        WidgetPosition.MiddleLeft -> Alignment.CenterStart
        WidgetPosition.MiddleRight -> Alignment.CenterEnd
        WidgetPosition.Bottom -> Alignment.BottomCenter
    }
}