package com.solaisc.notemark.feature.auth.presentation.landing

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.window.core.layout.WindowHeightSizeClass
import androidx.window.core.layout.WindowWidthSizeClass
import com.solaisc.notemark.feature.auth.presentation.landing.components.LandscapeLandingScreen
import com.solaisc.notemark.feature.auth.presentation.landing.components.PortraitLandingScreen
import com.solaisc.notemark.feature.auth.presentation.landing.components.TabletLandingScreen
import com.solaisc.notemark.util.Orientation
import com.solaisc.notemark.ui.theme.NoteMarkTheme

@Composable
fun LandingScreen(
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
    modifier: Modifier = Modifier
) {
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

    when(orientation) {
        Orientation.Landscape -> {
            LandscapeLandingScreen(
                onRegisterClick = onRegisterClick,
                onLoginClick = onLoginClick,
                modifier = modifier.windowInsetsPadding(WindowInsets.navigationBars)
            )
        }
        Orientation.Phone_Portrait -> {
            PortraitLandingScreen(
                onRegisterClick = onRegisterClick,
                onLoginClick = onLoginClick,
                modifier = modifier.windowInsetsPadding(WindowInsets.navigationBars)
            )
        }
        Orientation.Tablet_Portrait -> {
            TabletLandingScreen(
                onRegisterClick = onRegisterClick,
                onLoginClick = onLoginClick,
                modifier = modifier.windowInsetsPadding(WindowInsets.navigationBars)
            )
        }
    }
}

@PreviewScreenSizes
@Composable
fun LandingScreenPreview() {
    NoteMarkTheme {
        //LandingScreen()
    }
}