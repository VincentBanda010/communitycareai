package com.communitycare.ai.data

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.communitycare.ai.BuildConfig
import com.google.gson.Gson
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import net.openid.appauth.AuthState
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.CodeVerifierUtil
import net.openid.appauth.ResponseTypeValues
import net.openid.appauth.TokenResponse

private data class StoredSession(val authorization: String, val profile: Profile)

class AuthManager(context: Context, private val storage: SecureStorage) {
    private val service = AuthorizationService(context.applicationContext)
    private val gson = Gson()
    private val mutex = Mutex()
    private val saved = storage.read("session.bin")?.let { gson.fromJson(it, StoredSession::class.java) }
    private var authorization = saved?.let { AuthState.jsonDeserialize(it.authorization) }
    private val mutableProfile = MutableStateFlow(saved?.profile?.validate())
    val profile = mutableProfile.asStateFlow()

    suspend fun loginIntent(): Intent {
        val configuration = suspendCancellableCoroutine<AuthorizationServiceConfiguration> { continuation ->
            AuthorizationServiceConfiguration.fetchFromIssuer(Uri.parse(BuildConfig.OIDC_ISSUER)) { config, error ->
                if (continuation.isActive) {
                    if (config != null) continuation.resume(config)
                    else continuation.resumeWithException(IOException("Cannot reach the sign-in service", error))
                }
            }
        }
        val request = AuthorizationRequest.Builder(configuration, BuildConfig.OIDC_CLIENT_ID,
            ResponseTypeValues.CODE, Uri.parse(BuildConfig.OIDC_REDIRECT_URI))
            .setScope("openid profile").setPrompt("login")
            .setCodeVerifier(CodeVerifierUtil.generateRandomCodeVerifier()).build()
        return service.getAuthorizationRequestIntent(request)
    }

    suspend fun completeLogin(intent: Intent?, api: BackendApi) = mutex.withLock {
        if (intent == null) throw IOException("Sign-in cancelled")
        val response = AuthorizationResponse.fromIntent(intent)
        val error = AuthorizationException.fromIntent(intent)
        if (response == null) throw IOException("Sign-in was cancelled or could not complete", error)
        if (response.request.clientId != BuildConfig.OIDC_CLIENT_ID ||
            response.request.redirectUri.toString() != BuildConfig.OIDC_REDIRECT_URI ||
            response.request.configuration.discoveryDoc?.issuer?.toString() != BuildConfig.OIDC_ISSUER) {
            throw IOException("Unexpected sign-in response")
        }
        val freshState = AuthState(response, error)
        val token = suspendCancellableCoroutine<TokenResponse> { continuation ->
            service.performTokenRequest(response.createTokenExchangeRequest()) { result, failure ->
                if (continuation.isActive) {
                    if (result != null) continuation.resume(result)
                    else continuation.resumeWithException(IOException("Could not complete sign-in", failure))
                }
            }
        }
        freshState.update(token, null)
        val accessToken = token.accessToken ?: throw IOException("Sign-in did not provide an access token")
        val account = api.me("Bearer $accessToken").checkedBody().validate()
        // Commit the identity and its tokens together, only after the backend
        // confirms this account is assigned to an allowed organisation.
        storage.write("session.bin", gson.toJson(StoredSession(freshState.jsonSerializeString(), account)))
        authorization = freshState
        mutableProfile.value = account
    }

    suspend fun accessToken(expected: Profile): String = mutex.withLock {
        if (mutableProfile.value != expected) throw HttpFailure(401)
        val state = authorization ?: throw HttpFailure(401)
        val token = suspendCancellableCoroutine<String> { continuation ->
            state.performActionWithFreshTokens(service) { accessToken, _, error ->
                if (continuation.isActive) {
                    if (error != null || accessToken == null) {
                        if (error?.error == "invalid_grant") {
                            storage.write("session.bin", null)
                            authorization = null
                            mutableProfile.value = null
                            continuation.resumeWithException(HttpFailure(401))
                        } else continuation.resumeWithException(IOException("Could not refresh sign-in", error))
                    } else continuation.resume(accessToken)
                }
            }
        }
        storage.write("session.bin", gson.toJson(StoredSession(state.jsonSerializeString(), expected)))
        token
    }

    suspend fun logout() = mutex.withLock {
        storage.write("session.bin", null)
        authorization = null
        mutableProfile.value = null
    }
}
