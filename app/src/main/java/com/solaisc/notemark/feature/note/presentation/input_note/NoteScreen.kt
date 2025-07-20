package com.solaisc.notemark.feature.note.presentation.input_note

import android.content.pm.ActivityInfo
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.window.core.layout.WindowHeightSizeClass
import androidx.window.core.layout.WindowWidthSizeClass
import com.solaisc.notemark.feature.note.presentation.input_note.components.BackSection
import com.solaisc.notemark.feature.note.presentation.input_note.components.ClearIcon
import com.solaisc.notemark.feature.note.presentation.input_note.components.ExtendedFabSection
import com.solaisc.notemark.feature.note.presentation.input_note.components.NoteSection
import com.solaisc.notemark.feature.note.presentation.input_note.components.SaveButton
import com.solaisc.notemark.ui.theme.NoteMarkTheme
import com.solaisc.notemark.ui.theme.spacegFontBold
import com.solaisc.notemark.feature.note.presentation.utils.NoteMode
import com.solaisc.notemark.util.Orientation
import com.solaisc.notemark.util.components.NoteMarkButton
import com.solaisc.notemark.util.components.NoteMarkTextField2
import com.solaisc.notemark.util.components.NotemarkDialog
import com.solaisc.notemark.feature.note.presentation.utils.toDateTimeString
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel

@Composable
fun NoteScreen(
    navController: NavController
) {
    val viewModel: NoteViewModel = koinViewModel()
    val state = viewModel.state.collectAsStateWithLifecycle()

    var currentNoteMode by remember { mutableStateOf(state.value.noteMode) }
    var selectedReaderMode by remember { mutableStateOf(state.value.isReaderModeSelected) }
    val activity = LocalActivity.current

    var noteModeVisibility by remember { mutableStateOf(true) }

    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass

    val orientation = if (windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.COMPACT && windowSizeClass.windowHeightSizeClass == WindowHeightSizeClass.MEDIUM) {
        Orientation.Phone_Portrait
    } else if (windowSizeClass.windowHeightSizeClass == WindowHeightSizeClass.COMPACT || (windowSizeClass.windowHeightSizeClass == WindowHeightSizeClass.MEDIUM && windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.EXPANDED)) {
        Orientation.Landscape
    } else if (windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.MEDIUM && windowSizeClass.windowHeightSizeClass == WindowHeightSizeClass.EXPANDED) {
        Orientation.Tablet_Portrait
    } else {
        Orientation.Tablet_Portrait
    }

    LaunchedEffect(true) {
        viewModel.events.collectLatest { event ->
            when(event) {
                is NoteEvent.Navigate -> {
                    navController.navigateUp()
                }

                is NoteEvent.ChangeMode -> {
                    currentNoteMode = event.noteMode
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        if (currentNoteMode == NoteMode.Input_Mode) {
            focusRequester.requestFocus()
            keyboardController?.show()
        }
    }

    LaunchedEffect(noteModeVisibility, currentNoteMode) {
        if (noteModeVisibility && currentNoteMode == NoteMode.Reader_Mode) {
            delay(5000L)
            noteModeVisibility = false
        }
    }

    LaunchedEffect(
        state.value.titleText,
        state.value.contentText
    ) {
        viewModel.onAction(NoteAction.OnNoteSaveClick)
    }

    BackHandler(enabled = true) {
        when(currentNoteMode) {
            NoteMode.Input_Mode -> viewModel.onAction(NoteAction.OnBackClick)
            NoteMode.Reader_Mode -> {
                viewModel.onAction(
                    NoteAction.OnModeChangeClick(
                        NoteMode.View_Mode
                    )
                )
                currentNoteMode = NoteMode.View_Mode
                activity?.requestedOrientation =
                    ActivityInfo.SCREEN_ORIENTATION_FULL_USER
                selectedReaderMode = !selectedReaderMode
            }
            NoteMode.View_Mode -> navController.navigateUp()
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
        modifier = Modifier.windowInsetsPadding(if (orientation != Orientation.Landscape) WindowInsets.navigationBars else WindowInsets.navigationBars.add(
            WindowInsets.displayCutout))
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    noteModeVisibility = true
                }
        ) {
            if (orientation != Orientation.Landscape) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    when(currentNoteMode) {
                        NoteMode.Input_Mode -> {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                ClearIcon(
                                    onClick = { viewModel.onAction(NoteAction.OnBackClick) }
                                )
                                SaveButton(
                                    isSaving = state.value.isLoading,
                                    onClick = { viewModel.onAction(NoteAction.OnNoteSaveClick) }
                                )
                            }
                        }
                        else -> {
                            BackSection(onClick = { navController.navigateUp() })
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    Column(
                        modifier = Modifier
                            .weight(1f)
                    ) {
                        when(currentNoteMode) {
                            NoteMode.Input_Mode -> {
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
                                    imeAction = ImeAction.Default,
                                    lineLimits = TextFieldLineLimits.MultiLine(),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp)
                                        .weight(1f)
                                )
                            }
                            else -> {
                                NoteSection(
                                    title = state.value.note?.title ?: "",
                                    content = state.value.note?.content ?: "",
                                    createdAt = state.value.note?.createdAt?.toDateTimeString() ?: "",
                                    lastEdited = state.value.lastEdited
                                )
                            }
                        }
                    }
                    if (currentNoteMode != NoteMode.Input_Mode) {
                        ExtendedFabSection(
                            isReaderModeSelected = false,
                            onEditClick = {
                                viewModel.onAction(NoteAction.OnModeChangeClick(NoteMode.Input_Mode))
                                currentNoteMode = NoteMode.Input_Mode
                            },
                            onReaderClick = {
                                viewModel.onAction(NoteAction.OnModeChangeClick(NoteMode.Reader_Mode))
                                currentNoteMode = NoteMode.Reader_Mode
                                noteModeVisibility = false
                                activity?.requestedOrientation =
                                    ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                            }
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp)
                        )
                    }
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 8.dp)
                ) {
                    if (currentNoteMode == NoteMode.Input_Mode) {
                        Box(
                            modifier = Modifier
                                .weight(.2f)
                                .padding(start = 16.dp)
                        ) {
                            ClearIcon(
                                onClick = { viewModel.onAction(NoteAction.OnBackClick) }
                            )
                        }
                    } else {
                        BackSection(
                            modifier = Modifier.weight(.2f),
                            onClick = {
                                if (currentNoteMode == NoteMode.Reader_Mode) {
                                    viewModel.onAction(
                                        NoteAction.OnModeChangeClick(
                                            NoteMode.View_Mode
                                        )
                                    )
                                    currentNoteMode = NoteMode.View_Mode
                                    activity?.requestedOrientation =
                                        ActivityInfo.SCREEN_ORIENTATION_FULL_USER

                                    selectedReaderMode = !selectedReaderMode
                                } else {
                                    navController.navigateUp()
                                }
                            },
                            isAnimationEnabled = true,
                            isVisibilityEnabled = noteModeVisibility
                        )
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(.6f)
                    ) {
                        if (currentNoteMode == NoteMode.Input_Mode) {
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
                                imeAction = ImeAction.Default,
                                lineLimits = TextFieldLineLimits.MultiLine(),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                                    .weight(1f)
                            )
                        } else {
                            NoteSection(
                                title = state.value.note?.title ?: "",
                                content = state.value.note?.content ?: "",
                                createdAt = state.value.note?.createdAt?.toDateTimeString() ?: "",
                                lastEdited = state.value.lastEdited
                            )
                            if (currentNoteMode != NoteMode.Input_Mode) {
                                if (noteModeVisibility) {
                                    AnimatedVisibility(
                                        visible = noteModeVisibility,
                                        enter = fadeIn(
                                            animationSpec = tween(durationMillis = 3000)
                                        ),
                                        exit = fadeOut(
                                            animationSpec = tween(durationMillis = 3000)
                                        )
                                    ) {
                                        ExtendedFabSection(
                                            isReaderModeSelected = selectedReaderMode,
                                            onEditClick = {
                                                viewModel.onAction(
                                                    NoteAction.OnModeChangeClick(
                                                        NoteMode.Input_Mode
                                                    )
                                                )
                                                currentNoteMode = NoteMode.Input_Mode
                                                activity?.requestedOrientation =
                                                    ActivityInfo.SCREEN_ORIENTATION_FULL_USER
                                            },
                                            onReaderClick = {
                                                if (!selectedReaderMode) {
                                                    viewModel.onAction(
                                                        NoteAction.OnModeChangeClick(
                                                            NoteMode.Reader_Mode
                                                        )
                                                    )
                                                    currentNoteMode = NoteMode.Reader_Mode
                                                    noteModeVisibility = false
                                                } else {
                                                    viewModel.onAction(
                                                        NoteAction.OnModeChangeClick(
                                                            NoteMode.View_Mode
                                                        )
                                                    )
                                                    currentNoteMode = NoteMode.View_Mode
                                                    activity?.requestedOrientation =
                                                        ActivityInfo.SCREEN_ORIENTATION_FULL_USER
                                                }
                                                selectedReaderMode = !selectedReaderMode
                                            }
                                        )
                                    }
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(bottom = 16.dp)
                                    )
                                }
                            }
                        }
                    }

                    if (currentNoteMode == NoteMode.Input_Mode) {
                        Box(
                            modifier = Modifier.weight(.2f),
                            contentAlignment = Alignment.TopEnd
                        ) {
                            SaveButton(
                                isSaving = state.value.isLoading,
                                onClick = { viewModel.onAction(NoteAction.OnNoteSaveClick) },
                                modifier = Modifier.padding(end = 16.dp)
                            )
                        }
                    } else {
                        Box(
                            modifier = Modifier.weight(.2f)
                        )
                    }
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