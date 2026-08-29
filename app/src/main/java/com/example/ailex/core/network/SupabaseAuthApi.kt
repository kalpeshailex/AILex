package com.example.ailex.core.network

import com.example.ailex.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

/** One verified Supabase Auth session. */
data class SupabaseSession(
    val accessToken: String,
    val refreshToken: String,
    val userId: String
)

class SupabaseAuthException(message: String) : Exception(message)

/**
 * Talks directly to Supabase's Auth (GoTrue) REST API for email/phone OTP
 * sign-in. Plain OkHttp + org.json rather than the full Supabase Kotlin
 * SDK — this app only ever needs two endpoints, and keeping this
 * security-sensitive path small and dependency-free makes it easy to
 * audit. See backend/README.md for the wider architecture: the app talks
 * to Supabase Auth directly; the Cloudflare Worker only handles the data
 * API and never sees credentials.
 */
object SupabaseAuthApi {
    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    private fun request(path: String, body: JSONObject): Request =
        Request.Builder()
            .url("${BuildConfig.SUPABASE_URL}$path")
            .header("apikey", BuildConfig.SUPABASE_ANON_KEY)
            .header("Content-Type", "application/json")
            .post(body.toString().toRequestBody(jsonMediaType))
            .build()

    suspend fun sendEmailOtp(email: String): Result<Unit> =
        sendOtp(JSONObject().put("email", email).put("create_user", true))

    suspend fun sendPhoneOtp(e164Phone: String): Result<Unit> =
        sendOtp(JSONObject().put("phone", e164Phone).put("create_user", true))

    private suspend fun sendOtp(body: JSONObject): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            client.newCall(request("/auth/v1/otp", body)).execute().use { response ->
                if (response.isSuccessful) {
                    Result.success(Unit)
                } else {
                    Result.failure(SupabaseAuthException(errorMessage(response.body?.string())))
                }
            }
        } catch (e: IOException) {
            Result.failure(SupabaseAuthException("Couldn't reach the server. Check your connection and try again."))
        }
    }

    suspend fun verifyEmailOtp(email: String, token: String): Result<SupabaseSession> =
        verify(JSONObject().put("type", "email").put("email", email).put("token", token))

    suspend fun verifyPhoneOtp(e164Phone: String, token: String): Result<SupabaseSession> =
        verify(JSONObject().put("type", "sms").put("phone", e164Phone).put("token", token))

    /** Exchanges a stored refresh token for a fresh session — used on app start (see AppViewModel.restoreSession). */
    suspend fun refreshSession(refreshToken: String): Result<SupabaseSession> = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url("${BuildConfig.SUPABASE_URL}/auth/v1/token?grant_type=refresh_token")
                .header("apikey", BuildConfig.SUPABASE_ANON_KEY)
                .header("Content-Type", "application/json")
                .post(JSONObject().put("refresh_token", refreshToken).toString().toRequestBody(jsonMediaType))
                .build()
            client.newCall(request).execute().use { response ->
                val text = response.body?.string()
                if (response.isSuccessful && text != null) {
                    val json = JSONObject(text)
                    val user = json.getJSONObject("user")
                    Result.success(
                        SupabaseSession(
                            accessToken = json.getString("access_token"),
                            refreshToken = json.getString("refresh_token"),
                            userId = user.getString("id")
                        )
                    )
                } else {
                    Result.failure(SupabaseAuthException(errorMessage(text)))
                }
            }
        } catch (e: IOException) {
            Result.failure(SupabaseAuthException("Couldn't reach the server. Check your connection and try again."))
        }
    }

    private suspend fun verify(body: JSONObject): Result<SupabaseSession> = withContext(Dispatchers.IO) {
        try {
            client.newCall(request("/auth/v1/verify", body)).execute().use { response ->
                val text = response.body?.string()
                if (response.isSuccessful && text != null) {
                    val json = JSONObject(text)
                    val user = json.getJSONObject("user")
                    Result.success(
                        SupabaseSession(
                            accessToken = json.getString("access_token"),
                            refreshToken = json.getString("refresh_token"),
                            userId = user.getString("id")
                        )
                    )
                } else {
                    Result.failure(SupabaseAuthException(errorMessage(text)))
                }
            }
        } catch (e: IOException) {
            Result.failure(SupabaseAuthException("Couldn't reach the server. Check your connection and try again."))
        }
    }

    private fun errorMessage(body: String?): String {
        if (body.isNullOrBlank()) return "Something went wrong. Please try again."
        return try {
            val json = JSONObject(body)
            sequenceOf("msg", "error_description", "error", "message")
                .map { json.optString(it) }
                .firstOrNull { it.isNotBlank() }
                ?: "Something went wrong. Please try again."
        } catch (e: Exception) {
            "Something went wrong. Please try again."
        }
    }
}
