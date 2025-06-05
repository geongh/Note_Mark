package com.solaisc.notemark.feature.auth.presentation.register

import android.widget.Toast
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowHeightSizeClass
import androidx.window.core.layout.WindowWidthSizeClass
import com.solaisc.notemark.feature.auth.presentation.register.components.LandscapeRegisterScreen
import com.solaisc.notemark.feature.auth.presentation.register.components.PortraitRegisterScreen
import com.solaisc.notemark.feature.auth.presentation.register.components.TabletRegisterScreen
import com.solaisc.notemark.util.Orientation
import com.solaisc.notemark.ui.theme.NoteMarkTheme
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel

@Composable
fun RegisterScreen(
    onLoginClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: RegisterViewModel = koinViewModel()
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

    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(true) {
        viewModel.events.collectLatest { event ->
            when(event) {
                is RegisterEvent.Error -> {
                    keyboardController?.hide()
                    Toast.makeText(
                        context,
                        event.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
                RegisterEvent.Success -> {
                    keyboardController?.hide()
                    Toast.makeText(
                        context,
                        "You're registered.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    when(orientation) {
        Orientation.Landscape -> {
            LandscapeRegisterScreen(
                state = state,
                onAction = { action ->
                    when(action) {
                        RegisterAction.OnLoginClick -> onLoginClick()
                        else -> Unit
                    }
                    viewModel.onAction(action)
                },
                modifier = modifier.windowInsetsPadding(WindowInsets.navigationBars)
            )
        }
        Orientation.Phone_Portrait -> {
            PortraitRegisterScreen(
                state = state,
                onAction = { action ->
                    when(action) {
                        RegisterAction.OnLoginClick -> onLoginClick()
                        else -> Unit
                    }
                    viewModel.onAction(action)
                },
                modifier = modifier.windowInsetsPadding(WindowInsets.navigationBars)
            )
        }
        Orientation.Tablet_Portrait -> {
            TabletRegisterScreen(
                state = state,
                onAction = { action ->
                    when(action) {
                        RegisterAction.OnLoginClick -> onLoginClick()
                        else -> Unit
                    }
                    viewModel.onAction(action)
                },
                modifier = modifier.windowInsetsPadding(WindowInsets.navigationBars)
            )
        }
    }
}

@PreviewScreenSizes
@Composable
fun LoginScreenPreview() {
    NoteMarkTheme {
        //RegisterScreen()
    }
}