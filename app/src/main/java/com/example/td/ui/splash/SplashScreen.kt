package com.example.td.ui.splash

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

private val SplashBackground = Color(0xFF0D1B4E)

private const val INITIAL_DELAY_MS = 400L
private const val DASH_DELAY_MS = 500L
private const val DO_DELAY_MS = 700L

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    var step by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        delay(INITIAL_DELAY_MS)
        step = 1
        delay(DASH_DELAY_MS)
        step = 2
        delay(DO_DELAY_MS)
        onFinished()
    }

    val dashAlpha by animateFloatAsState(
        targetValue = if (step >= 1) 1f else 0f,
        animationSpec = tween(durationMillis = 300),
        label = "dashAlpha"
    )
    val doAlpha by animateFloatAsState(
        targetValue = if (step >= 2) 1f else 0f,
        animationSpec = tween(durationMillis = 300),
        label = "doAlpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SplashBackground),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "To",
                color = Color.White,
                fontSize = 52.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "-",
                color = Color.White.copy(alpha = dashAlpha),
                fontSize = 52.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Do",
                color = Color.White.copy(alpha = doAlpha),
                fontSize = 52.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
