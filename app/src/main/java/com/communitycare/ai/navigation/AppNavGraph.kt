package com.communitycare.ai.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.communitycare.ai.ui.screens.DashboardScreen
import com.communitycare.ai.ui.screens.GetStartedScreen
import com.communitycare.ai.ui.screens.SignInScreen
import com.communitycare.ai.ui.screens.SignUpScreen

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = NavRoutes.GET_STARTED
    ) {
        composable(NavRoutes.GET_STARTED) {
            GetStartedScreen(
                onGetStarted = { navController.navigate(NavRoutes.SIGN_UP) },
                onSignIn     = { navController.navigate(NavRoutes.SIGN_IN) }
            )
        }
        composable(NavRoutes.SIGN_UP) {
            SignUpScreen(
                onSignUp  = { navController.navigate(NavRoutes.DASHBOARD) {
                    popUpTo(NavRoutes.GET_STARTED) { inclusive = true }
                }},
                onSignIn  = { navController.navigate(NavRoutes.SIGN_IN) }
            )
        }
        composable(NavRoutes.SIGN_IN) {
            SignInScreen(
                onSignIn  = { navController.navigate(NavRoutes.DASHBOARD) {
                    popUpTo(NavRoutes.GET_STARTED) { inclusive = true }
                }},
                onSignUp  = { navController.navigate(NavRoutes.SIGN_UP) }
            )
        }
        composable(NavRoutes.DASHBOARD) {
            DashboardScreen()
        }
    }
}
