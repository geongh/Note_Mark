package com.solaisc.notemark.util.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.solaisc.notemark.ui.theme.interFontRegular

@Composable
fun NoteMarkTextField2(
    state: TextFieldState,
    hint: String,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    titleTextStyle: Boolean = false,
    fontSize: TextUnit = 18.sp,
    lineHeight: TextUnit = 22.sp,
    fontFamily: FontFamily = interFontRegular,
    lineLimits: TextFieldLineLimits = TextFieldLineLimits.SingleLine
) {
    var isFocused by remember {
        mutableStateOf(false)
    }

    BasicTextField(
        state = state,
        textStyle = LocalTextStyle.current.copy(
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = fontSize,
            lineHeight = lineHeight,
            fontFamily = fontFamily
        ),
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = ImeAction.Done
        ),
        lineLimits = lineLimits,
        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
        modifier = modifier
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
            .onFocusChanged {
                isFocused = it.isFocused
            },
        decorator = { innerBox ->
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                if (state.text.isEmpty() && !isFocused) {
                    Text(
                        text = hint,
                        style = if (!titleTextStyle) { MaterialTheme.typography.bodyLarge } else MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                innerBox()
            }
        }
    )
}