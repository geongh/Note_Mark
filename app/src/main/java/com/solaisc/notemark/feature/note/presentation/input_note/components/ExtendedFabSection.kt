package com.solaisc.notemark.feature.note.presentation.input_note.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.solaisc.notemark.R

@Composable
fun ExtendedFabSection(
    isReaderModeSelected: Boolean,
    onEditClick: () -> Unit,
    onReaderClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp, top = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(12.dp)
                )
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.pencil),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable {
                        onEditClick()
                    }
                    .padding(16.dp)
            )
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.book),
                contentDescription = null,
                tint = if (isReaderModeSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .background(
                        color = if (isReaderModeSelected) {
                            MaterialTheme.colorScheme.primary.copy(
                                alpha = .1f
                            )
                        } else {
                            Color.Transparent
                        },
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clip(RoundedCornerShape(12.dp))
                    .clickable {
                        onReaderClick()
                    }
                    .padding(16.dp)
            )
        }
    }
}