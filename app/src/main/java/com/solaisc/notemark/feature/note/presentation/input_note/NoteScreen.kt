package com.solaisc.notemark.feature.note.presentation.input_note

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.solaisc.notemark.ui.theme.NoteMarkTheme
import com.solaisc.notemark.ui.theme.spacegFontBold
import com.solaisc.notemark.util.components.NoteMarkButton
import com.solaisc.notemark.util.components.NoteMarkTextField2
import com.solaisc.notemark.util.components.NotemarkDialog
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel

@Composable
fun NoteScreen(
    navController: NavController
) {
    val viewModel: NoteViewModel = koinViewModel()
    val state = viewModel.state.collectAsStateWithLifecycle()

    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(true) {
        viewModel.events.collectLatest { event ->
            when(event) {
                is NoteEvent.Navigate -> {
                    navController.navigateUp()
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        keyboardController?.show()
    }

    BackHandler(enabled = true) {
        viewModel.onAction(NoteAction.OnBackClick)
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .clickable {
                                viewModel.onAction(NoteAction.OnBackClick)
                            }
                    )
                    if (state.value.isLoading) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    else {
                        Text(
                            text = "SAVE NOTE",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .clickable {
                                    viewModel.onAction(NoteAction.OnNoteSaveClick)
                                }
                                .padding(4.dp)
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    NoteMarkTextField2(
                        state = state.value.titleText,
                        hint = "Note Title",
                        fontSize = 32.sp,
                        lineHeight = 36.sp,
                        fontFamily = spacegFontBold,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .focusRequester(focusRequester),
                        titleTextStyle = true
                    )
                    Spacer(Modifier.height(24.dp))
                    NoteMarkTextField2(
                        state = state.value.contentText,
                        hint = "Tap to enter note content",
                        lineLimits = TextFieldLineLimits.MultiLine(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .weight(1f)
                    )
                }
            }
        }

        if (state.value.isDialogShow == true) {
            NotemarkDialog(
                title = "Discard Changes?",
                onDismiss = {
                    viewModel.onAction(NoteAction.OnDismissDialog)
                },
                description = "You have unsaved changes. If you discard now, all changes will be lost.",
                primaryButton = {
                    NoteMarkButton(
                        text = "Keep Editing",
                        color = MaterialTheme.colorScheme.onPrimary,
                        backgroundColor = MaterialTheme.colorScheme.primary,
                        isLoading = false,
                        onClick = {
                            viewModel.onAction(NoteAction.OnDismissDialog)
                        },
                        modifier = Modifier.weight(1f)
                    )
                },
                secondaryButton = {
                    NoteMarkButton(
                        text = "Discard",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        backgroundColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                        borderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = .3f),
                        isLoading = false,
                        onClick = {
                            viewModel.onAction(NoteAction.OnDiscardChangeClick(state.value.id))
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            )
        }
    }
}

@Preview
@Composable
fun NoteScreenPreview() {
    NoteMarkTheme {
        NoteScreen(rememberNavController())
    }
}