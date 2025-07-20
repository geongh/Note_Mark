package com.solaisc.notemark.feature.note.presentation.input_note.components

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solaisc.notemark.ui.theme.spacegFontBold

@Composable
fun ColumnScope.NoteSection(
    title: String,
    content: String,
    createdAt: String,
    lastEdited: String
) {
    Text(
        text = title,
        fontSize = 32.sp,
        lineHeight = 36.sp,
        fontFamily = spacegFontBold,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    )
    Spacer(Modifier.height(24.dp))
    MetaDataSection(
        createdAt = createdAt,
        lastEdited = lastEdited
    )
    Spacer(Modifier.height(24.dp))
    Text(
        text = content,
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .weight(1f)
            .verticalScroll(rememberScrollState())
    )
}