package com.solaisc.notemark.feature.setting.presentation

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.solaisc.notemark.R
import com.solaisc.notemark.feature.note.presentation.list_note.NotesAction
import com.solaisc.notemark.util.components.NoteMarkButton
import com.solaisc.notemark.util.components.NotemarkDialog
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel

@Composable
fun SettingScreen(
    navController: NavController,
    isConnected: Boolean
) {
    val viewModel: SettingViewModel = koinViewModel()
    val state = viewModel.state.collectAsStateWithLifecycle()

    val context = LocalContext.current

    LaunchedEffect(true) {
        viewModel.events.collectLatest { event ->
            when(event) {
                is SettingEvent.Error -> {
                    Toast.makeText(
                        context,
                        event.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
                SettingEvent.Navigate -> {
                    navController.navigate("auth") {
                        popUpTo("note") {
                            inclusive = true
                        }
                    }
                }
            }
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
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
                        text = "SETTINGS",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.titleSmall
                    )
                }
                Spacer(Modifier.height(24.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            viewModel.onAction(SettingAction.OnLogoutClick)
                        }
                        .padding(8.dp)
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.clock),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = "Sync Interval",
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Spacer(Modifier.weight(1f))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Manual Only",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                }
                Spacer(Modifier.height(24.dp))
                Row(
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            viewModel.onAction(SettingAction.OnSyncClick)
                        }
                        .padding(8.dp)
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.refresh),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Sync Data",
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.titleSmall
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = state.value.syncDateText,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                Spacer(Modifier.height(24.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (!isConnected) {
                                viewModel.onAction(SettingAction.OnErrorShown)
                            } else {
                                viewModel.onAction(SettingAction.OnLogoutClick)
                            }
                        }
                        .padding(8.dp)
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.logout),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = "Log out",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.titleSmall
                    )
                }
            }
        }

        if (state.value.isDialogShown) {
            NotemarkDialog(
                title = "Log out",
                onDismiss = {
                    viewModel.onAction(SettingAction.OnDismissDialog)
                },
                description = "You have unsynced changes. What would you like to do before logging out?",
                primaryButton = {
                    NoteMarkButton(
                        text = "Sync Now",
                        color = MaterialTheme.colorScheme.onPrimary,
                        backgroundColor = MaterialTheme.colorScheme.primary,
                        isLoading = false,
                        onClick = {
                            viewModel.onAction(SettingAction.OnConfirmSyncClick)
                        },
                        modifier = Modifier.weight(1f)
                    )
                },
                secondaryButton = {
                    NoteMarkButton(
                        text = "Do Not Sync",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        backgroundColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                        borderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = .3f),
                        isLoading = false,
                        onClick = {
                            viewModel.onAction(SettingAction.OnConfirmNotSyncClick)
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            )
        }

        if (state.value.isSync == true) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}