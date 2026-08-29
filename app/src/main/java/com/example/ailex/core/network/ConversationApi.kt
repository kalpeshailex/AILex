package com.example.ailex.core.network

import com.example.ailex.BuildConfig
import com.example.ailex.core.common.LegalDomain
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

class ConversationApiException(message: String) : Exception(message)

data class ConversationActionStep(val step: String, val groundedIn: List<String>)

data class ConversationCitation(
    val sourceId: String,
    val title: String,
    val sectionReference: String?,
    val officialUrl: String?,
    val jurisdiction: String,
    val effectiveDate: String?
)

/**
 * One turn's worth of structured guidance from `POST /conversation/message`
 * (see backend/src/orchestrator/AIOrchestrator.ts). [rawContext] is the
 * backend's opaque `ExtractedContext` JSON, round-tripped verbatim as
 * `previous_context` on the next call so the pipeline doesn't re-ask
 * established facts — the app never needs to understand its internal shape.
 */
data class ConversationTurn(
    val domain: String,
    val scenario: String,
    val summary: String,
    val situation: String,
    val rights: List<String>,
    val obligations: List<String>,
    val authorityPowers: List<String>,
    val actions: List<ConversationActionStep>,
    val avoid: List<String>,
    val preserve: List<String>,
    val escalation: List<String>,
    val citations: List<ConversationCitation>,
    val riskLevel: String,
    val riskReason: String,
    val needsFollowUp: Boolean,
    val nextQuestion: String?,
    val rawContext: JSONObject?
) {
    /** Maps the API's domain string (e.g. "GOVERNMENT_RTS") onto the app's LegalDomain enum, if recognized. */
    val legalDomain: LegalDomain?
        get() = when (domain) {
            "POLICE" -> LegalDomain.POLICE
            "TRAFFIC" -> LegalDomain.TRAFFIC
            "RAILWAY" -> LegalDomain.RAILWAY
            "GOVERNMENT_RTS" -> LegalDomain.GOVERNMENT
            "CYBER" -> LegalDomain.CYBER
            else -> null
        }

    /** A short human title for this turn — e.g. "Upi Fraud" or, failing that, the domain's display name. */
    val displayTitle: String
        get() {
            if (scenario.isNotBlank() && scenario != "UNKNOWN" && scenario != "UNCLEAR") {
                return scenario.split('_').joinToString(" ") { word -> word.lowercase().replaceFirstChar { it.uppercase() } }
            }
            return legalDomain?.displayName ?: "Situation"
        }
}

/**
 * Talks to the Cloudflare Worker's `/conversation/message` API — the real
 * Gemini-backed AI pipeline (see 06_AI_ARCHITECTURE.md and
 * backend/src/orchestrator/AIOrchestrator.ts). Same plain OkHttp + org.json
 * style as IncidentsApi/SupabaseAuthApi, carrying the caller's own Supabase
 * access token.
 */
object ConversationApi {
    private val client = OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
        // The pipeline makes up to 4 sequential model calls per turn (classify,
        // extract, plan, respond) — a generous read timeout avoids a spurious
        // failure on a slower response rather than the model actually failing.
        .readTimeout(45, TimeUnit.SECONDS)
        .build()

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    suspend fun sendMessage(
        token: String,
        message: String,
        language: String = "en",
        previousContext: JSONObject?,
        previousDomain: String?,
        previousScenario: String?
    ): Result<ConversationTurn> = withContext(Dispatchers.IO) {
        try {
            val body = JSONObject().apply {
                put("input_type", "text")
                put("message", message)
                put("language", language)
                previousContext?.let { put("previous_context", it) }
                previousDomain?.let { put("previous_domain", it) }
                previousScenario?.let { put("previous_scenario", it) }
            }
            val request = Request.Builder()
                .url("${BuildConfig.WORKER_BASE_URL}/conversation/message")
                .header("Authorization", "Bearer $token")
                .post(body.toString().toRequestBody(jsonMediaType))
                .build()
            client.newCall(request).execute().use { response ->
                val text = response.body?.string()
                if (response.isSuccessful && text != null) {
                    Result.success(turnFromJson(JSONObject(text)))
                } else {
                    Result.failure(ConversationApiException(errorMessage(text)))
                }
            }
        } catch (e: IOException) {
            Result.failure(ConversationApiException("Couldn't reach the server. Check your connection."))
        }
    }

    private fun errorMessage(body: String?): String {
        if (body.isNullOrBlank()) return "Something went wrong. Please try again."
        return try {
            JSONObject(body).optString("error").ifBlank { "Something went wrong. Please try again." }
        } catch (e: Exception) {
            "Something went wrong. Please try again."
        }
    }

    private fun turnFromJson(json: JSONObject): ConversationTurn {
        val response = json.getJSONObject("response")
        val risk = json.optJSONObject("risk")
        return ConversationTurn(
            domain = json.optString("domain", "UNKNOWN"),
            scenario = json.optString("scenario", ""),
            summary = response.optString("summary"),
            situation = response.optString("situation"),
            rights = response.optJSONArray("rights").toStringList(),
            obligations = response.optJSONArray("obligations").toStringList(),
            authorityPowers = response.optJSONArray("authority_powers").toStringList(),
            actions = response.optJSONArray("actions").toActionSteps(),
            avoid = response.optJSONArray("avoid").toStringList(),
            preserve = response.optJSONArray("preserve").toStringList(),
            escalation = response.optJSONArray("escalation").toStringList(),
            citations = response.optJSONArray("citations").toCitations(),
            riskLevel = risk?.optString("level") ?: "LOW",
            riskReason = risk?.optString("reason") ?: "",
            needsFollowUp = json.optBoolean("needs_follow_up", false),
            nextQuestion = json.optStringOrNull("next_question"),
            rawContext = json.optJSONObject("context")
        )
    }

    private fun JSONArray?.toStringList(): List<String> {
        if (this == null) return emptyList()
        return (0 until length()).map { getString(it) }
    }

    private fun JSONArray?.toActionSteps(): List<ConversationActionStep> {
        if (this == null) return emptyList()
        return (0 until length()).map {
            val o = getJSONObject(it)
            ConversationActionStep(step = o.optString("step"), groundedIn = o.optJSONArray("grounded_in").toStringList())
        }
    }

    private fun JSONArray?.toCitations(): List<ConversationCitation> {
        if (this == null) return emptyList()
        return (0 until length()).map {
            val o = getJSONObject(it)
            ConversationCitation(
                sourceId = o.optString("source_id"),
                title = o.optString("title"),
                sectionReference = o.optStringOrNull("section_reference"),
                officialUrl = o.optStringOrNull("official_url"),
                jurisdiction = o.optString("jurisdiction"),
                effectiveDate = o.optStringOrNull("effective_date")
            )
        }
    }

    private fun JSONObject.optStringOrNull(name: String): String? =
        if (isNull(name)) null else optString(name).ifBlank { null }
}
