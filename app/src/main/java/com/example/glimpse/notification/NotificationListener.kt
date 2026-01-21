package com.example.glimpse.notification

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.example.glimpse.SharedViewModel
import kotlinx.coroutines.delay

/*
FR8 - Notification.Visible
Notification management. The notification listener receives notifications and gives them
to the notification composable to display. Android Popups are prevented by turning Do not disturb
on when entering the HUD (and reverting to user settings when exiting)
 */
class NotificationListener : NotificationListenerService() {
    companion object {
        private var listener: ((String, String, String) -> Unit)? = null

        fun setListener(callback: (title: String, text: String, appName: String) -> Unit) {
            listener = callback
        }
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        val title = sbn.notification.extras.getString(Notification.EXTRA_TITLE) ?: ""
        val text =
            sbn.notification.extras.getCharSequence(Notification.EXTRA_TEXT)?.toString() ?: ""

        if (sbn.isOngoing)
        // These notifications are not relevant to display in HUD
            return

        val packageName = sbn.packageName
        val appName = try {
            val appInfo = applicationContext.packageManager.getApplicationInfo(packageName, 0)
            applicationContext.packageManager.getApplicationLabel(appInfo).toString()
        } catch (e: Exception) {
            // No provided name, take from package name
            val appNameIndex = packageName.lastIndexOf(".")
            if (appNameIndex != -1 && appNameIndex < packageName.length - 1) {
                packageName.substring(appNameIndex + 1).replaceFirstChar { it.uppercase() }
            } else
                packageName
        }

        listener?.invoke(title, text, appName)
    }
}

@Composable
fun NotificationManager(sharedViewModel: SharedViewModel) {
    val context = LocalContext.current
    val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    var userNotificationPolicy: NotificationManager.Policy?
    var userInterruptionFilter: Int?
    var title by remember { mutableStateOf("") }
    var text by remember { mutableStateOf("") }
    var appName by remember { mutableStateOf("") }
    var notificationCount by remember { mutableIntStateOf(0) }


    LifecycleResumeEffect(Unit) {
        // Remember user settings
        userNotificationPolicy = notificationManager.notificationPolicy
        userInterruptionFilter = notificationManager.currentInterruptionFilter

        notificationManager.notificationPolicy = NotificationManager.Policy(
            NotificationManager.Policy.PRIORITY_CATEGORY_MEDIA,
            NotificationManager.Policy.CONVERSATION_SENDERS_NONE,
            NotificationManager.Policy.CONVERSATION_SENDERS_NONE
        )
        notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_PRIORITY)

        NotificationListener.setListener { ti, te, n ->
            title = ti
            text = te
            appName = n
            notificationCount++
        }

        onPauseOrDispose {
            // Restore the original DND state when the activity is paused or stopped
            if (userNotificationPolicy != null) {
                notificationManager.notificationPolicy = userNotificationPolicy
                notificationManager.setInterruptionFilter(userInterruptionFilter!!)
            }
        }
    }

    NotificationItem(appName, title, text, notificationCount, sharedViewModel)
}

@Composable
fun NotificationItem(
    appName: String,
    title: String,
    text: String,
    notificationCount: Int,
    sharedViewModel: SharedViewModel
) {
    var hudHeight by remember { mutableIntStateOf(0) }
    var isVisible by remember { mutableStateOf(false) }

    // Auto-dismiss after 7 seconds
    // FR9 - Notification.DoNotDisturb: Apply do not disturb settings here
    LaunchedEffect(notificationCount) {
        isVisible = false
        if (!sharedViewModel.dnd && notificationCount > 0) {
            isVisible = true
            delay(7000)
            isVisible = false
        }
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
        exit = fadeOut() + slideOutVertically(targetOffsetY = { it }),
        modifier = Modifier
            .fillMaxSize()
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
            .zIndex(1f)
    ) {
        Box(modifier = Modifier
            .zIndex(1f)
            .fillMaxSize()
            .onGloballyPositioned {
                hudHeight = it.size.height
            },
            contentAlignment = Alignment.BottomCenter) {
            Column(modifier = Modifier.padding(start = 6.dp, end = 6.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(bottom = 7.dp)
                        .zIndex(1f)
                        .clip(RoundedCornerShape(50))
                        .fillMaxWidth()
                        .height(with(LocalDensity.current) { (0.18f * hudHeight).toDp() })
                        .background(Color.DarkGray.copy(alpha = sharedViewModel.hudMenuOpacity))
                        .pointerInput(Unit) {
                            detectVerticalDragGestures { change, dragAmount ->
                                change.consume()
                                // Allow user to swipe down notification
                                if (dragAmount > 3) {
                                    isVisible = false
                                }
                            }
                        }
                ) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = appName,
                        color = sharedViewModel.hudForegroundColor,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = sharedViewModel.selectedFont.fontResource,
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = title,
                            color = sharedViewModel.hudForegroundColor,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = sharedViewModel.selectedFont.fontResource,
                        )
                        Text(
                            text = text,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = sharedViewModel.hudForegroundColor,
                            fontSize = 20.sp,
                            fontFamily = sharedViewModel.selectedFont.fontResource,
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(
                        onClick = {
                            isVisible = false
                        },
                        modifier = Modifier
                            .clip(CircleShape)
                            .border(1.dp, sharedViewModel.hudForegroundColor, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = sharedViewModel.hudForegroundColor
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }
        }
    }
}


