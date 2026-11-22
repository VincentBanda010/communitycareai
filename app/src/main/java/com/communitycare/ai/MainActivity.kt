package com.communitycare.ai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.communitycare.ai.data.HttpFailure
import com.communitycare.ai.navigation.AppNavGraph
import com.communitycare.ai.sync.EncounterSyncWorker
import com.communitycare.ai.ui.theme.CommunityCareTheme
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private var busy by mutableStateOf(false)
    private var error by mutableStateOf<String?>(null)
    private val container get() = (application as CommunityCareApplication).container
    private val authorization = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        lifecycleScope.launch {
            busy = true
            try {
                container?.let {
                    it.auth.completeLogin(result.data, it.api)
                    it.syncStatus.value = SyncStatus()
                }
                EncounterSyncWorker.schedule(this@MainActivity)
                error = null
            } catch (cancelled: CancellationException) { throw cancelled }
            catch (failure: Exception) { error = signInError(failure) }
            finally { busy = false }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CommunityCareTheme {
                Surface(Modifier.fillMaxSize()) {
                    val app = container
                    if (app == null) {
                        Text((application as CommunityCareApplication).startupError ?: "Unable to start the application.",
                            modifier = Modifier.safeDrawingPadding().padding(24.dp))
                    } else {
                        AppNavGraph(rememberNavController(), app, busy, error, onSignIn = {
                            if (!busy) lifecycleScope.launch {
                                busy = true; error = null
                                try { authorization.launch(app.auth.loginIntent()) }
                                catch (cancelled: CancellationException) { throw cancelled }
                                catch (failure: Exception) { error = signInError(failure) }
                                finally { busy = false }
                            }
                        }, onRecord = { triage ->
                            try {
                                val account = app.auth.profile.value ?: throw IllegalStateException("Sign-in required")
                                app.encounters.add(account, triage)
                                error = null
                                EncounterSyncWorker.schedule(this@MainActivity)
                                true
                            } catch (failure: Exception) {
                                error = "Could not save this report. Please try again before leaving this screen."
                                false
                            }
                        }, onSync = { EncounterSyncWorker.schedule(this@MainActivity) }, onLogout = {
                            lifecycleScope.launch {
                                EncounterSyncWorker.cancelImmediate(this@MainActivity)
                                app.auth.logout()
                                app.syncStatus.value = SyncStatus()
                                app.storage.write("sync-status.bin", null)
                                error = null
                            }
                        })
                    }
                }
            }
        }
    }
    private fun signInError(error: Exception) = if (error is HttpFailure && error.status == 403)
        "Your account needs a facility assignment. Contact your administrator."
    else "Sign-in could not complete. Check your connection and try again."
}
