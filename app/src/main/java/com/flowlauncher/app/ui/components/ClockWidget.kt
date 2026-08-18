package com.flowlauncher.app.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.font.FontWeight
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ClockWidget(use24Hour: Boolean) {
    var timeText by remember { mutableStateOf(formatTime(use24Hour)) }
    var dateText by remember { mutableStateOf(formatDate()) }

    LaunchedEffect(use24Hour) {
        while (true) {
            timeText = formatTime(use24Hour)
            dateText = formatDate()
            delay(1000)
        }
    }

    androidx.compose.foundation.layout.Column {
        Text(
            text = timeText,
            style = MaterialTheme.typography.displayLarge,
            fontWeight = FontWeight.Light,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = dateText,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
        )
    }
}

private fun formatTime(use24Hour: Boolean): String {
    val pattern = if (use24Hour) "HH:mm" else "h:mm a"
    return SimpleDateFormat(pattern, Locale.getDefault()).format(Date())
}

private fun formatDate(): String {
    return SimpleDateFormat("EEEE, MMMM d", Locale.getDefault()).format(Date())
}
