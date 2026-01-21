package com.example.glimpse.customization

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.glimpse.HUDScreen
import com.example.glimpse.SharedViewModel
import com.example.glimpse.WidgetType
import com.example.glimpse.icons.DragHandle
import sh.calvin.reorderable.ReorderableCollectionItemScope
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

/*
FR4 - Change.Positions
Widget position customization features
 */


fun isExclusiveWidget(widget: WidgetType): Boolean {
    return widget in setOf(WidgetType.Barcode, WidgetType.Chat, WidgetType.Face, WidgetType.Settings)
}

@Composable
fun HUDEditorScreen(
    navController: NavController,
    sharedViewModel: SharedViewModel
) {
    // Track widget assignments for each position
    var widgetAssignments by remember { mutableStateOf(sharedViewModel.widgetAssignments) }

    // Track whether to display one widget or multiple widgets
    var isSingleWidgetMode by remember { mutableStateOf(sharedViewModel.isSingleWidgetMode) }

    // Track the selected position in single-widget mode
    var selectedPosition by remember { mutableStateOf(sharedViewModel.selectedPosition) }

    // Track whether changes have been submitted
    var submitted by remember { mutableStateOf(false) }

    // Track the order and visibility of widgets in the menu
    var widgetMenuItems by remember { mutableStateOf(sharedViewModel.widgetMenuItems) }

    // Calculate the number of visible widgets
    val visibleWidgetCount = widgetMenuItems.count { it.isVisible }

    // Save original assignments to revert if not submitted
    val originalAssignments = sharedViewModel.widgetAssignments
    val originalIsSingleWidgetMode = sharedViewModel.isSingleWidgetMode
    val originalPosition = sharedViewModel.selectedPosition
    val originalMenuItems = sharedViewModel.widgetMenuItems

    // Update the sharedViewModel when assignments or mode changes
    LaunchedEffect(widgetAssignments, isSingleWidgetMode, selectedPosition, widgetMenuItems) {
        sharedViewModel.widgetAssignments = widgetAssignments
        sharedViewModel.isSingleWidgetMode = isSingleWidgetMode
        sharedViewModel.selectedPosition = selectedPosition
        sharedViewModel.widgetMenuItems = widgetMenuItems
    }

    fun shouldShowItem(item: WidgetMenuItem): Boolean {
        return isSingleWidgetMode || isExclusiveWidget(item.widget)
    }

    LaunchedEffect(isSingleWidgetMode) {
        widgetMenuItems = widgetMenuItems.map { menuItem ->
            menuItem.copy(isVisible = shouldShowItem(menuItem) && menuItem.isVisible)
        }
    }

    // Revert to original settings if not submitted
    DisposableEffect(Unit) {
        onDispose {
            if (!submitted) {
                sharedViewModel.widgetAssignments = originalAssignments
                sharedViewModel.isSingleWidgetMode = originalIsSingleWidgetMode
                sharedViewModel.selectedPosition = originalPosition
                sharedViewModel.widgetMenuItems = originalMenuItems
            }
        }
    }

    // Reset to default settings
    fun resetToDefaults() {
        widgetAssignments = mapOf(
            WidgetPosition.Top to null,
            WidgetPosition.MiddleLeft to null,
            WidgetPosition.MiddleRight to null,
            WidgetPosition.Bottom to null,
        )
        isSingleWidgetMode = true
        selectedPosition = WidgetPosition.Top
        widgetMenuItems = WidgetType.allWidgetTypes.map { WidgetMenuItem(it, true) }
    }

    // Handle widget assignment with mutual exclusivity for camera widgets
    fun assignWidget(position: WidgetPosition, widget: WidgetType?) {
        val updatedAssignments = widgetAssignments.toMutableMap()

        if (widget != null && isExclusiveWidget(widget)) {
            // Unassign any other camera widget
            updatedAssignments.forEach { (existingPosition, existingWidget) ->
                if (existingWidget != null && isExclusiveWidget(existingWidget)) {
                    updatedAssignments[existingPosition] = null
                }
            }
        }

        // Assign the new widget
        updatedAssignments[position] = widget
        widgetAssignments = updatedAssignments
    }

    val lazyListState = rememberLazyListState()
    val reorderableLazyListState = rememberReorderableLazyListState(lazyListState) { from, to ->
        val numHeaders = 3 // Adjust based on number of items prior to the ordering list

        // Filter based on single or multi mode
        val filteredItems = widgetMenuItems.filter { shouldShowItem(it) }

        // Get the actual indexes in the full widgetMenuItems list
        val fromItem = filteredItems.getOrNull(from.index - numHeaders)
        val toItem = filteredItems.getOrNull(to.index - numHeaders)

        if (fromItem != null && toItem != null) {
            val fromIndex = widgetMenuItems.indexOf(fromItem)
            val toIndex = widgetMenuItems.indexOf(toItem)

            if (fromIndex in widgetMenuItems.indices && toIndex in widgetMenuItems.indices) {
                widgetMenuItems = widgetMenuItems.toMutableList().apply {
                    add(toIndex, removeAt(fromIndex))
                }
            }
        }
    }

    // Use LazyColumn instead of Column to avoid nested scrolling
    LazyColumn(
        state = lazyListState,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Color.White, RoundedCornerShape(12.dp))
            ) {
                HUDScreen(
                    navController = navController,
                    isPreview = true,
                    sharedViewModel = sharedViewModel
                )
            }
        }

        item {
            // Toggle for single-widget vs. multi-widget mode
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Single Widget Mode", fontWeight = FontWeight.Bold)
                Switch(
                    checked = isSingleWidgetMode,
                    onCheckedChange = { isSingleWidgetMode = it },
                    modifier = Modifier.testTag("ModeSwitch")
                )
            }
        }

        item {
            Text(
                text = "Customize Widget Menu",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        val filteredItems = widgetMenuItems.filter { shouldShowItem(it) }
        itemsIndexed(filteredItems, key = { _, item -> item.widget.toString() }) { _, item ->
            ReorderableItem(reorderableLazyListState, item.widget.toString()) {
                val interactionSource = remember { MutableInteractionSource() }
                WidgetMenuItemRow(
                    scope = this,
                    item = item,
                    onCheckedChange = { checked ->
                        widgetMenuItems = widgetMenuItems.map { menuItem ->
                            if (menuItem.widget == item.widget) {
                                menuItem.copy(isVisible = checked)
                            } else {
                                menuItem
                            }
                        }.sortedByDescending { it.isVisible }
                    },
                    isCheckboxEnabled = !(item.isVisible && visibleWidgetCount == 1) && item.widget != WidgetType.Settings,
                    interactionSource = interactionSource
                )
            }
        }

        if (isSingleWidgetMode) {
            item {
                Text(
                    text = "Widget Assignments",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                // Dropdown to select the position for the single widget
                var positionDropdownExpanded by remember { mutableStateOf(false) }
                Box {
                    Button(onClick = { positionDropdownExpanded = true }) {
                        Text(text = selectedPosition.toString())
                    }
                    DropdownMenu(
                        expanded = positionDropdownExpanded,
                        onDismissRequest = { positionDropdownExpanded = false }
                    ) {
                        WidgetPosition.entries.forEach { position ->
                            DropdownMenuItem(
                                onClick = {
                                    selectedPosition = position
                                    positionDropdownExpanded = false
                                },
                                text = {
                                    Text(text = position.toString())
                                }
                            )
                        }
                    }
                }
            }

        } else {
            item {
                Text(
                    text = "Widget Assignments",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            items(WidgetPosition.entries.size) { index ->
                val position = WidgetPosition.entries[index]
                WidgetAssignmentRow(
                    position = position,
                    selectedWidget = widgetAssignments[position],
                    onWidgetSelected = { widget ->
                        assignWidget(position, widget)
                    }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            // Reset and Save buttons
            Button(
                onClick = { resetToDefaults() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                Text("Reset to Defaults")
            }
        }

        item {
            Button(
                onClick = {
                    submitted = true
                    sharedViewModel.savePreferences()
                    navController.popBackStack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Text("Save")
            }
        }
    }
}

@Composable
fun WidgetMenuItemRow(
    scope: ReorderableCollectionItemScope,
    item: WidgetMenuItem,
    onCheckedChange: (Boolean) -> Unit,
    interactionSource: MutableInteractionSource,
    isCheckboxEnabled: Boolean
) {
    val hapticFeedback = LocalHapticFeedback.current
    Card(
        onClick = {},
        interactionSource = interactionSource,
    ) {
        Row(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.weight(1f)
            ) {
                Checkbox(
                    checked = item.isVisible,
                    onCheckedChange = onCheckedChange,
                    enabled = isCheckboxEnabled
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = item.widget.icon,
                    contentDescription = item.widget.toString(),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = item.widget.toString())
            }
            IconButton(
                modifier = with(scope) {
                    Modifier
                        .draggableHandle(
                            onDragStarted = {
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                            },
                            onDragStopped = {
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                            },
                            interactionSource = interactionSource,
                        )
                },
                onClick = {},
            ) {
                Icon(DragHandle, contentDescription = "Reorder")
            }
        }
    }
}

data class WidgetMenuItem(
    val widget: WidgetType,
    val isVisible: Boolean
)

@Composable
fun WidgetAssignmentRow(
    position: WidgetPosition, // The position to assign the widget to
    selectedWidget: WidgetType?, // The currently selected widget for this position
    onWidgetSelected: (WidgetType?) -> Unit // Callback when a widget is selected
) {
    var expanded by remember { mutableStateOf(false) } // State for dropdown menu visibility

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Display the position name (e.g., "Top Left")
        Text(
            text = position.toString(),
        )

        // Dropdown menu for selecting a widget
        Box {
            // Button to open the dropdown menu
            Button(onClick = { expanded = true }) {
                Text(
                    text = selectedWidget?.toString() ?: "None"
                ) // Display the selected widget or "None"
            }

            // Dropdown menu with all available widgets
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false } // Close the dropdown when dismissed
            ) {
                // Add an option for "None" (to unassign the widget)
                DropdownMenuItem(
                    onClick = {
                        onWidgetSelected(null)
                        expanded = false
                    },
                    text = {
                        Text(text = "None") // Add the "None" label
                    }
                )
                DropdownMenuItem(
                    onClick = {
                        onWidgetSelected(WidgetType.Barcode) // Assign Barcode as the default for Camera
                        expanded = false // Close the dropdown
                    },
                    text = {
                        Text(text = "Camera") // Add the "Camera" label
                    }
                )

                // Show other widgets (excluding Chat, Face, and Barcode)
                WidgetType.allWidgetTypes.forEach { widget ->
                    if (!isExclusiveWidget(widget)) {
                        DropdownMenuItem(
                            onClick = {
                                onWidgetSelected(widget) // Assign the selected widget
                                expanded = false // Close the dropdown
                            },
                            text = {
                                Text(text = widget.toString())
                            }
                        )
                    }
                }
            }
        }
    }
}
