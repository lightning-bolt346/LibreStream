package com.example.ui.player

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.util.Consumer

@Composable
fun rememberPipMode(): Boolean {
    val context = LocalContext.current
    var isPip by remember { mutableStateOf(false) }

    DisposableEffect(context) {
        val activity = context.findActivity() as? androidx.activity.ComponentActivity
        val listener = Consumer<androidx.core.app.PictureInPictureModeChangedInfo> { info ->
            isPip = info.isInPictureInPictureMode
        }
        activity?.addOnPictureInPictureModeChangedListener(listener)
        onDispose {
            activity?.removeOnPictureInPictureModeChangedListener(listener)
        }
    }

    return isPip
}

fun Context.findActivity(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    return null
}
