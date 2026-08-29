package com.example.ailex.core.network

import android.net.Uri
import androidx.compose.ui.graphics.Color
import com.example.ailex.BuildConfig
import com.example.ailex.core.common.IncidentStatus
import com.example.ailex.core.common.LegalDomain
import com.example.ailex.domain.incident.EvidenceRef
import com.example.ailex.domain.incident.Incident
import com.example.ailex.domain.incident.IncidentTimelineEvent
import com.example.ailex.ui.theme.Blue600
import com.example.ailex.ui.theme.Caution500
import com.example.ailex.ui.theme.Danger600
import com.example.ailex.ui.theme.Ink400
import com.example.ailex.ui.theme.Success500
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit

class WorkerApiException(message: String) : Exception(message)

/**
 * Talks to the Cloudflare Worker's /incidents API (see backend/README.md).
 * Every call carries the caller's own Supabase access token (SessionTokenHolder)
 * so Postgres RLS scopes every query to that user — plain OkHttp + org.json,
 * matching SupabaseAuthApi's style rather than pulling in a REST/JSON library.
 */
object IncidentsApi {
    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    private val isoFormats = listOf(
        "yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
        "yyyy-MM-dd'T'HH:mm:ssXXX"
    ).map { pattern -> SimpleDateFormat(pattern, Locale.US).apply { timeZone = TimeZone.getTimeZone("UTC") } }

    private fun url(path: String) = "${BuildConfig.WORKER_BASE_URL}$path"

    suspend fun list(token: String): Result<List<Incident>> = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder().url(url("/incidents")).header("Authorization", "Bearer $token").get().build()
            client.newCall(request).execute().use { response ->
                val text = response.body?.string()
                if (response.isSuccessful && text != null) {
                    val array = JSONArray(text)
                    Result.success((0 until array.length()).map { incidentFromJson(array.getJSONObject(it)) })
                } else {
                    Result.failure(WorkerApiException(errorMessage(text)))
                }
            }
        } catch (e: IOException) {
            Result.failure(WorkerApiException("Couldn't reach the server. Check your connection."))
        }
    }

    suspend fun create(token: String, incident: Incident): Result<Incident> = withContext(Dispatchers.IO) {
        try {
            val body = incidentToCreateJson(incident).toString().toRequestBody(jsonMediaType)
            val request = Request.Builder().url(url("/incidents")).header("Authorization", "Bearer $token").post(body).build()
            client.newCall(request).execute().use { response ->
                val text = response.body?.string()
                if (response.isSuccessful && text != null) {
                    Result.success(incidentFromJson(JSONObject(text)))
                } else {
                    Result.failure(WorkerApiException(errorMessage(text)))
                }
            }
        } catch (e: IOException) {
            Result.failure(WorkerApiException("Couldn't reach the server. Check your connection."))
        }
    }

    suspend fun patch(token: String, id: String, fields: JSONObject): Result<Incident> = withContext(Dispatchers.IO) {
        try {
            val body = fields.toString().toRequestBody(jsonMediaType)
            val request = Request.Builder().url(url("/incidents/$id")).header("Authorization", "Bearer $token").patch(body).build()
            client.newCall(request).execute().use { response ->
                val text = response.body?.string()
                if (response.isSuccessful && text != null) {
                    Result.success(incidentFromJson(JSONObject(text)))
                } else {
                    Result.failure(WorkerApiException(errorMessage(text)))
                }
            }
        } catch (e: IOException) {
            Result.failure(WorkerApiException("Couldn't reach the server. Check your connection."))
        }
    }

    suspend fun delete(token: String, id: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder().url(url("/incidents/$id")).header("Authorization", "Bearer $token").delete().build()
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) Result.success(Unit)
                else Result.failure(WorkerApiException(errorMessage(response.body?.string())))
            }
        } catch (e: IOException) {
            Result.failure(WorkerApiException("Couldn't reach the server. Check your connection."))
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

    // ---- Incident <-> JSON mapping (snake_case, matching backend/schema.sql) ----

    fun incidentToCreateJson(incident: Incident): JSONObject = JSONObject().apply {
        put("domain", incident.domain.id)
        put("title", incident.title)
        put("status", incident.status.name)
        put("tags", JSONArray(incident.tags))
        put("date_location", incident.dateLocation)
        put("saved_detail", incident.savedDetail)
        put("summary", incident.summary)
        put("key_facts", keyFactsToJson(incident.keyFacts))
        put("timeline", timelineToJson(incident.timeline))
        put("evidence", evidenceToJson(incident.evidence))
        put("notes", incident.notes)
        put("complaint_edits", complaintEditsToJson(incident.complaintEdits))
    }

    fun keyFactsToJson(keyFacts: List<Pair<String, String>>): JSONArray = JSONArray().apply {
        keyFacts.forEach { (k, v) -> put(JSONObject().put("key", k).put("value", v)) }
    }

    fun timelineToJson(timeline: List<IncidentTimelineEvent>): JSONArray = JSONArray().apply {
        timeline.forEach { event ->
            put(
                JSONObject()
                    .put("title", event.title)
                    .put("when_text", event.whenText)
                    .put("dot_color", dotColorToKey(event.dotColor))
                    .put("body", event.body)
            )
        }
    }

    fun evidenceToJson(evidence: List<EvidenceRef>): JSONArray = JSONArray().apply {
        evidence.forEach { e ->
            put(
                JSONObject()
                    .put("display_name", e.displayName)
                    .put("meta", e.meta)
                    .put("available", e.available)
                    .put("uri", e.uri?.toString())
            )
        }
    }

    fun complaintEditsToJson(edits: Map<Int, String>): JSONObject = JSONObject().apply {
        edits.forEach { (index, text) -> put(index.toString(), text) }
    }

    private fun incidentFromJson(json: JSONObject): Incident {
        val domain = LegalDomain.entries.find { it.id == json.optString("domain") } ?: LegalDomain.POLICE
        val status = runCatching { IncidentStatus.valueOf(json.optString("status")) }.getOrDefault(IncidentStatus.ACTIVE)
        return Incident(
            id = json.getString("id"),
            domain = domain,
            title = json.optString("title"),
            status = status,
            tags = json.optJSONArray("tags").toStringList(),
            dateLocation = json.optStringOrNull("date_location"),
            savedDetail = json.optStringOrNull("saved_detail"),
            summary = json.optString("summary"),
            keyFacts = json.optJSONArray("key_facts").toKeyFactsList(),
            timeline = json.optJSONArray("timeline").toTimelineList(),
            evidence = json.optJSONArray("evidence").toEvidenceList(),
            notes = json.optString("notes"),
            complaintEdits = json.optJSONObject("complaint_edits").toComplaintEditsMap(),
            savedAt = parseTimestamp(json.optString("saved_at"))
        )
    }

    private fun parseTimestamp(value: String?): Long {
        if (value.isNullOrBlank()) return System.currentTimeMillis()
        for (format in isoFormats) {
            try {
                format.parse(value)?.let { return it.time }
            } catch (e: Exception) {
                // try the next pattern
            }
        }
        return System.currentTimeMillis()
    }

    private fun dotColorToKey(color: Color): String = when (color) {
        Success500 -> "success"
        Caution500 -> "caution"
        Danger600 -> "danger"
        Ink400 -> "ink"
        else -> "blue"
    }

    private fun dotColorFromKey(key: String?): Color = when (key) {
        "success" -> Success500
        "caution" -> Caution500
        "danger" -> Danger600
        "ink" -> Ink400
        else -> Blue600
    }

    private fun JSONArray?.toStringList(): List<String> {
        if (this == null) return emptyList()
        return (0 until length()).map { getString(it) }
    }

    private fun JSONObject.optStringOrNull(name: String): String? =
        if (isNull(name)) null else optString(name).ifBlank { null }

    private fun JSONArray?.toKeyFactsList(): List<Pair<String, String>> {
        if (this == null) return emptyList()
        return (0 until length()).map {
            val o = getJSONObject(it)
            o.getString("key") to o.getString("value")
        }
    }

    private fun JSONArray?.toTimelineList(): List<IncidentTimelineEvent> {
        if (this == null) return emptyList()
        return (0 until length()).map {
            val o = getJSONObject(it)
            IncidentTimelineEvent(
                title = o.optString("title"),
                whenText = o.optString("when_text"),
                dotColor = dotColorFromKey(o.optString("dot_color")),
                body = o.optStringOrNull("body")
            )
        }
    }

    private fun JSONArray?.toEvidenceList(): List<EvidenceRef> {
        if (this == null) return emptyList()
        return (0 until length()).map {
            val o = getJSONObject(it)
            EvidenceRef(
                displayName = o.optString("display_name"),
                meta = o.optString("meta"),
                available = o.optBoolean("available", true),
                uri = o.optStringOrNull("uri")?.let { s -> Uri.parse(s) }
            )
        }
    }

    private fun JSONObject?.toComplaintEditsMap(): Map<Int, String> {
        if (this == null) return emptyMap()
        val result = mutableMapOf<Int, String>()
        val keysIterator = keys()
        while (keysIterator.hasNext()) {
            val key = keysIterator.next()
            key.toIntOrNull()?.let { result[it] = getString(key) }
        }
        return result
    }
}
