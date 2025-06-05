package com.solaisc.notemark

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.solaisc.notemark.feature.auth.presentation.landing.LandingScreen
import com.solaisc.notemark.feature.auth.presentation.login.LoginScreen
import com.solaisc.notemark.feature.auth.presentation.register.RegisterScreen
import com.solaisc.notemark.feature.note.EmptyScreen

@Composable
fun NavigationRoot(
    navController: NavHostController,
    isLoggedIn: Boolean
) {
    NavHost(
        navController = navController,
        startDestination = if(isLoggedIn) "note" else "auth"
    ) {
        authGraph(navController)
        noteGraph(navController)
    }
}

private fun NavGraphBuilder.authGraph(navController: NavHostController) {
    navigation(
        startDestination = "landing",
        route = "auth"
    ) {
        composable(route = "landing") {
            LandingScreen(
                onLoginClick = { navController.navigate("login") },
                onRegisterClick = { navController.navigate("register") }
            )
        }
        composable("login") {
            LoginScreen(
                onLoginClick = {
                    navController.navigate("note") {
                        popUpTo("auth") {
                            inclusive = true
                        }
                    }
                },
                onRegisterClick = {
                    navController.navigate("register") {
                        popUpTo("login") {
                            inclusive = true
                            saveState = true
                        }
                        restoreState = true
                    }
                }
            )
        }
        composable(route = "register") {
            RegisterScreen(
                onLoginClick = {
                    navController.navigate("login") {
                        popUpTo("register") {
                            inclusive = true
                            saveState = true
                        }
                        restoreState = true
                    }
                }
            )
        }
    }
}

private fun NavGraphBuilder.noteGraph(navController: NavHostController) {
    navigation(
        startDestination = "empty",
        route = "note"
    ) {
        composable(route = "empty") {
            EmptyScreen()
        }
    }
}