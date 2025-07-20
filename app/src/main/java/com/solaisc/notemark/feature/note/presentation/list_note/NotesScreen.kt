package com.solaisc.notemark.feature.note.presentation.list_note

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.window.core.layout.WindowHeightSizeClass
import androidx.window.core.layout.WindowWidthSizeClass
import com.solaisc.notemark.R
import com.solaisc.notemark.feature.note.presentation.list_note.components.NoteItem
import com.solaisc.notemark.ui.theme.Gradient1
import com.solaisc.notemark.ui.theme.Gradient2
import com.solaisc.notemark.ui.theme.NoteMarkTheme
import com.solaisc.notemark.util.Orientation
import com.solaisc.notemark.util.components.NoteMarkButton
import com.solaisc.notemark.util.components.NotemarkDialog
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel

@Composable
fun NotesScreen(
    navController: NavController,
    isConnected: Boolean
) {
    val viewModel: NotesViewModel = koinViewModel()
    val state = viewModel.state.collectAsStateWithLifecycle()

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
                is NotesEvent.Navigate -> {
                    navController.navigate("add_note?id=${event.id}&input_mode=${event.inputMode}") {
                        popUpTo("add_note") {
                            inclusive = true
                        }
                    }
                }
            }
        }
    }

    Scaffold(
        floatingActionButton = {
            Box(
                modifier = Modifier
                    .padding(end = 8.dp, bottom = 24.dp)
                    .background(
                        brush = Brush.linearGradient(
                            listOf(Gradient1, Gradient2)
                        ),
                        shape = RoundedCornerShape(24.dp)
                    )
                    .clip(RoundedCornerShape(24.dp))
                    .clickable {
                        viewModel.onAction(NotesAction.OnNewNotesClick)
                    }
                    .padding(20.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
        modifier = Modifier
            .windowInsetsPadding(if (orientation != Orientation.Landscape) WindowInsets.navigationBars else WindowInsets.navigationBars.add(WindowInsets.displayCutout))
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
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "NoteMark",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontSize = 28.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold
                        )
                        if (!isConnected) {
                            Spacer(Modifier.width(8.dp))
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.cloud_off),
                                contentDescription = null
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.settings),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier
                               .clip(CircleShape)
                               .clickable {
                                    navController.navigate("setting_dashboard") {
                                        popUpTo("setting_dashboard") {
                                            inclusive = true
                                        }
                                    }
                               }
                               .padding(2.dp)
                        )
                        Spacer(Modifier.width(12.dp))
                        Box(
                            modifier = Modifier
                                .background(
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clip(RoundedCornerShape(12.dp))
                                .padding(2.dp)
                                .size(36.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = state.value.initial,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(horizontal = 16.dp)
                ) {
                    if (state.value.list.size > 0) {
                        Spacer(Modifier.height(16.dp))
                        LazyVerticalStaggeredGrid(
                            columns = StaggeredGridCells.Fixed(if (orientation == Orientation.Landscape) 3 else 2),
                            verticalItemSpacing = 16.dp,
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier
                                .fillMaxSize()
                        ) {
                            items(state.value.list) { note ->
                                NoteItem(
                                    note = note,
                                    onClick = {
                                        viewModel.onAction(NotesAction.OnEditNoteClick(note.id))
                                    },
                                    onLongTap = {
                                        viewModel.onAction(NotesAction.OnItemLongTap(note.id))
                                    }
                                )
                            }
                        }
                    } else {
                        Spacer(Modifier.height(64.dp))
                        Text(
                            text = "You’ve got an empty board,\n" +
                                    "let’s place your first note on it!",
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }

        if (state.value.isDialogShow == true) {
            NotemarkDialog(
                title = "Delete Note?",
                onDismiss = {
                    viewModel.onAction(NotesAction.OnItemLongTap(""))
                },
                description = "Are you sure you want to delete this note?\n" +
                        "This action cannot be undone.",
                primaryButton = {
                    NoteMarkButton(
                        text = "Delete",
                        color = MaterialTheme.colorScheme.onPrimary,
                        backgroundColor = MaterialTheme.colorScheme.primary,
                        isLoading = false,
                        onClick = {
                            if (state.value.idToDelete != null) {
                                viewModel.onAction(NotesAction.OnDeleteClick(state.value.idToDelete!!))
                            }
                        },
                        modifier = Modifier.weight(1f)
                    )
                },
                secondaryButton = {
                    NoteMarkButton(
                        text = "Cancel",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        backgroundColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                        borderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = .3f),
                        isLoading = false,
                        onClick = {
                            viewModel.onAction(NotesAction.OnItemLongTap(""))
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
fun NotesScreePreview() {
    NoteMarkTheme {
        NotesScreen(rememberNavController(), true)
    }
}