package com.solaisc.notemark.feature.note.presentation.input_note.components

import android.content.pm.ActivityInfo
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.solaisc.notemark.feature.note.presentation.input_note.NoteAction
import com.solaisc.notemark.feature.note.presentation.utils.NoteMode

@Composable
fun BackSection(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isAnimationEnabled: Boolean = false,
    isVisibilityEnabled: Boolean = true,
) {
    if (isAnimationEnabled) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .padding(start = 16.dp)
        ) {
            AnimatedVisibility(
                visible = isVisibilityEnabled,
                enter = fadeIn(animationSpec = tween(durationMillis = 3000)),
                exit = fadeOut(animationSpec = tween(durationMillis = 3000))
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable {
                            onClick()
                        }
                        .padding(2.dp)
                )
            }
            AnimatedVisibility(
                visible = isVisibilityEnabled,
                enter = fadeIn(animationSpec = tween(durationMillis = 3000)),
                exit = fadeOut(animationSpec = tween(durationMillis = 3000))
            ) {
                Spacer(Modifier.width(12.dp))
            }
            AnimatedVisibility(
                visible = isVisibilityEnabled,
                enter = fadeIn(animationSpec = tween(durationMillis = 3000)),
                exit = fadeOut(animationSpec = tween(durationMillis = 3000))
            ) {
                Text(
                    text = "ALL NOTES",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.titleSmall
                )
            }
        }
    } else {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable {
                        onClick()
                    }
                    .padding(2.dp)
            )
            Spacer(Modifier.width(12.dp))
            Text(
                text = "ALL NOTES",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.titleSmall
            )
        }
    }
}