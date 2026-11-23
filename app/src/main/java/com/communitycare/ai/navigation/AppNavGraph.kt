package com.communitycare.ai.navigation

import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.communitycare.ai.AppContainer
import com.communitycare.ai.data.Triage
import com.communitycare.ai.data.QualityUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import com.communitycare.ai.ui.screens.*

@Composable
fun AppNavGraph(navController: NavHostController, container: AppContainer, busy: Boolean,
                error: String?, onSignIn: () -> Unit, onRecord: (Triage) -> Boolean,
                onSync: () -> Unit, onLogout: () -> Unit) {
    val profile by container.auth.profile.collectAsState()
    val allRecords by container.encounters.records.collectAsState()
    val sync by container.syncStatus.collectAsState()
    val quality by container.quality.state.collectAsState()
    val scope = rememberCoroutineScope()
    val account = profile
    LaunchedEffect(account) {
        if (account == null) container.quality.clear()
        else while (isActive) {
            container.quality.refresh(account)
            delay(30_000)
        }
    }
    LaunchedEffect(sync.checkedAt) {
        if (account != null && sync.checkedAt != null) container.quality.refresh(account)
    }
    if (account != null) {
        DashboardScreen(account, allRecords.filter { it.belongsTo(account) }, sync,
            error, onRecord, onSync, onSignIn, onLogout,
            if (quality.owner == account.subject && quality.orgUnit == account.orgUnit) quality else QualityUiState(),
            { scope.launch { container.quality.refresh(account) } })
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
