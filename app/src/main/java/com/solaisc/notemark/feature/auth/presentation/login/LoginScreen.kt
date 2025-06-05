package com.solaisc.notemark.feature.auth.presentation.login

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
import androidx.window.core.layout.WindowWidthSizeClass
import androidx.window.core.layout.WindowHeightSizeClass
import com.solaisc.notemark.feature.auth.presentation.login.components.LandscapeLoginScreen
import com.solaisc.notemark.feature.auth.presentation.login.components.PortraitLoginScreen
import com.solaisc.notemark.feature.auth.presentation.login.components.TabletLoginScreen
import com.solaisc.notemark.feature.auth.presentation.register.RegisterEvent
import com.solaisc.notemark.util.Orientation
import com.solaisc.notemark.ui.theme.NoteMarkTheme
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel

@Composable
fun LoginScreen(
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: LoginViewModel = koinViewModel()
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
                is LoginEvent.Error -> {
                    keyboardController?.hide()
                    Toast.makeText(
                        context,
                        event.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
                LoginEvent.Success -> {
                    keyboardController?.hide()
                    Toast.makeText(
                        context,
                        "You're Log In.",
                        Toast.LENGTH_LONG
                    ).show()

                    onLoginClick()
                }
            }
        }
    }

    when(orientation) {
        Orientation.Landscape -> {
            LandscapeLoginScreen(
                state = state,
                onAction = { action ->
                    when(action) {
                        LoginAction.OnRegisterClick -> onRegisterClick()
                        else -> Unit
                    }
                    viewModel.onAction(action)
                },
                modifier = modifier.windowInsetsPadding(WindowInsets.navigationBars)
            )
        }
        Orientation.Phone_Portrait -> {
            PortraitLoginScreen(
                state = state,
                onAction = { action ->
                    when(action) {
                        LoginAction.OnRegisterClick -> onRegisterClick()
                        else -> Unit
                    }
                    viewModel.onAction(action)
                },
                modifier = modifier.windowInsetsPadding(WindowInsets.navigationBars)
            )
        }
        Orientation.Tablet_Portrait -> {
            TabletLoginScreen(
                state = state,
                onAction = { action ->
                    when(action) {
                        LoginAction.OnLoginClick -> onRegisterClick()
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
        //LoginScreen()
    }
}