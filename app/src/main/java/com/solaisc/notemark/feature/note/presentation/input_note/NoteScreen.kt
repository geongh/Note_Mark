package com.solaisc.notemark.feature.note.presentation.input_note

import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.window.core.layout.WindowHeightSizeClass
import androidx.window.core.layout.WindowWidthSizeClass
import com.solaisc.notemark.R
import com.solaisc.notemark.ui.theme.NoteMarkTheme
import com.solaisc.notemark.ui.theme.spacegFontBold
import com.solaisc.notemark.util.NoteMode
import com.solaisc.notemark.util.Orientation
import com.solaisc.notemark.util.components.NoteMarkButton
import com.solaisc.notemark.util.components.NoteMarkTextField2
import com.solaisc.notemark.util.components.NotemarkDialog
import com.solaisc.notemark.util.toDateString
import com.solaisc.notemark.util.toDateTimeString
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel
import java.time.Duration
import java.time.LocalDateTime

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
                    currentNoteMode = state.value.noteMode
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

    BackHandler(enabled = true) {
        viewModel.onAction(NoteAction.OnBackClick)
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
                        }
                        else -> {
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
                                            navController.navigateUp()
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
                    Spacer(Modifier.height(12.dp))
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
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
                                    lineLimits = TextFieldLineLimits.MultiLine(),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp)
                                        .weight(1f)
                                )
                            }
                            else -> {
                                Text(
                                    text = state.value.note?.title ?: "",
                                    fontSize = 32.sp,
                                    lineHeight = 36.sp,
                                    fontFamily = spacegFontBold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp)
                                )
                                Spacer(Modifier.height(24.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(
                                            text = "Date Created",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Spacer(Modifier.height(4.dp))
                                        Text(
                                            text = state.value.note?.createdAt?.toDateTimeString() ?: "",
                                            style = MaterialTheme.typography.titleSmall,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                    Column(
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(
                                            text = "Last Edited",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Spacer(Modifier.height(4.dp))
                                        Text(
                                            text = state.value.lastEdited,
                                            style = MaterialTheme.typography.titleSmall,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                                Spacer(Modifier.height(24.dp))
                                Text(
                                    text = state.value.note?.content ?: "",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp)
                                        .weight(1f)
                                )
                            }
                        }
                    }
                    if (currentNoteMode != NoteMode.Input_Mode) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
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
                                            viewModel.onAction(NoteAction.OnModeChangeClick(NoteMode.Input_Mode))
                                            currentNoteMode = NoteMode.Input_Mode
                                        }
                                        .padding(16.dp)
                                )
                                Icon(
                                    imageVector = ImageVector.vectorResource(R.drawable.book),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable {
                                            viewModel.onAction(NoteAction.OnModeChangeClick(NoteMode.Reader_Mode))
                                            currentNoteMode = NoteMode.Reader_Mode
                                            noteModeVisibility = false
                                            activity?.requestedOrientation =
                                                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                                        }
                                        .padding(16.dp)
                                )

                            }
                        }
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
                        }
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .weight(.2f)
                                .padding(start = 16.dp)
                        ) {
                            AnimatedVisibility(
                                visible = noteModeVisibility,
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
                                            activity?.requestedOrientation =
                                                ActivityInfo.SCREEN_ORIENTATION_FULL_USER
                                            navController.navigateUp()
                                        }
                                        .padding(2.dp)
                                )
                            }
                            AnimatedVisibility(
                                visible = noteModeVisibility,
                                enter = fadeIn(animationSpec = tween(durationMillis = 3000)),
                                exit = fadeOut(animationSpec = tween(durationMillis = 3000))
                            ) {
                                Spacer(Modifier.width(12.dp))
                            }
                            AnimatedVisibility(
                                visible = noteModeVisibility,
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
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(.6f)
                            .verticalScroll(rememberScrollState())
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
                                lineLimits = TextFieldLineLimits.MultiLine(),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                                    .weight(1f)
                            )
                        } else {
                            Text(
                                text = state.value.note?.title ?: "",
                                fontSize = 32.sp,
                                lineHeight = 36.sp,
                                fontFamily = spacegFontBold,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                            )
                            Spacer(Modifier.height(24.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                            ) {
                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = "Date Created",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        text = state.value.note?.createdAt?.toDateTimeString() ?: "",
                                        style = MaterialTheme.typography.titleSmall,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = "Last edited",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        text = state.value.lastEdited,
                                        style = MaterialTheme.typography.titleSmall,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                            Spacer(Modifier.height(24.dp))
                            Text(
                                text = state.value.note?.content ?: "",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                                    .weight(1f)
                            )
                            if (currentNoteMode != NoteMode.Input_Mode) {
                                AnimatedVisibility(
                                    visible = noteModeVisibility,
                                    enter = fadeIn(
                                        animationSpec = tween(durationMillis = 3000)
                                    ),
                                    exit = fadeOut(
                                        animationSpec = tween(durationMillis = 3000)
                                    )
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(bottom = 8.dp),
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
                                                        viewModel.onAction(
                                                            NoteAction.OnModeChangeClick(
                                                                NoteMode.Input_Mode
                                                            )
                                                        )
                                                        currentNoteMode = NoteMode.Input_Mode
                                                        activity?.requestedOrientation =
                                                            ActivityInfo.SCREEN_ORIENTATION_FULL_USER
                                                    }
                                                    .padding(16.dp)
                                            )
                                            Icon(
                                                imageVector = ImageVector.vectorResource(R.drawable.book),
                                                contentDescription = null,
                                                tint = if (selectedReaderMode) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                                modifier = Modifier
                                                    .background(
                                                        color = if (selectedReaderMode) {
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
                                                        if (!selectedReaderMode) {
                                                            //activity?.requestedOrientation =
                                                            //    ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
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
                                                    .padding(16.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (currentNoteMode == NoteMode.Input_Mode) {
                        Box(
                            modifier = Modifier.weight(.2f),
                            contentAlignment = Alignment.TopEnd
                        ) {
                            if (state.value.isLoading) {
                                CircularProgressIndicator(
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                                    modifier = Modifier.size(20.dp)
                                )
                            } else {
                                Text(
                                    text = "SAVE NOTE",
                                    color = MaterialTheme.colorScheme.primary,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier
                                        .padding(end = 16.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .clickable {
                                            viewModel.onAction(NoteAction.OnNoteSaveClick)
                                        }
                                        .padding(4.dp)
                                )
                            }
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