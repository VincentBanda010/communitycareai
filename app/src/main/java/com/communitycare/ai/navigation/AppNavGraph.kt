package com.communitycare.ai.navigation

import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.communitycare.ai.AppContainer
import com.communitycare.ai.data.Triage
import com.communitycare.ai.ui.screens.*

@Composable
fun AppNavGraph(navController: NavHostController, container: AppContainer, busy: Boolean,
                error: String?, onSignIn: () -> Unit, onRecord: (Triage) -> Boolean,
                onSync: () -> Unit, onLogout: () -> Unit) {
    val profile by container.auth.profile.collectAsState()
    val allRecords by container.encounters.records.collectAsState()
    val sync by container.syncStatus.collectAsState()
    val account = profile
    if (account != null) {
        DashboardScreen(account, allRecords.filter { it.belongsTo(account) }, sync,
            error, onRecord, onSync, onSignIn, onLogout)
        return
    }
    NavHost(navController = navController, startDestination = NavRoutes.GET_STARTED) {
        composable(NavRoutes.GET_STARTED) {
            GetStartedScreen(onGetStarted = { navController.navigate(NavRoutes.SIGN_IN) },
                onSignIn = { navController.navigate(NavRoutes.SIGN_IN) })
        }
        composable(NavRoutes.SIGN_UP) {
            SignUpScreen(onSignIn = { navController.navigate(NavRoutes.SIGN_IN) })
        }
        composable(NavRoutes.SIGN_IN) {
            SignInScreen(onSignIn, { navController.navigate(NavRoutes.SIGN_UP) }, busy, error)
        }
    }
}
