package com.shipaton.quotesofwisdom.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Calendar

private val Context.quotesDataStore by preferencesDataStore(name = "quotes_of_wisdom")

data class AppPreferences(
    val themeId: String = "parchment",
    val favoriteIds: Set<Int> = emptySet(),
    val firstSeenMillis: Long = 0L,
    val streak: Int = 0,
    val bestStreak: Int = 0,
    val lastOpenDay: Int = 0,
    val proVoiceName: String = "",
    val proSpeechRate: Float = 1.0f,
    val debugAccessOverride: String = ""
)

data class StreakUpdate(
    val streak: Int,
    val bestStreak: Int,
    val brokePreviousStreak: Boolean
)

class AppPreferencesRepository(private val context: Context) {
    private object Keys {
        val themeId = stringPreferencesKey("theme_id")
        val favoriteIds = stringSetPreferencesKey("favorite_ids")
        val firstSeenMillis = longPreferencesKey("first_seen_millis")
        val streak = intPreferencesKey("streak")
        val bestStreak = intPreferencesKey("best_streak")
        val lastOpenDay = intPreferencesKey("last_open_day")
        val proVoiceName = stringPreferencesKey("pro_voice_name")
        val proSpeechRate = floatPreferencesKey("pro_speech_rate")
        val debugAccessOverride = stringPreferencesKey("debug_access_override")
    }

    val preferences: Flow<AppPreferences> = context.quotesDataStore.data.map { prefs ->
        AppPreferences(
            themeId = prefs[Keys.themeId] ?: "parchment",
            favoriteIds = prefs[Keys.favoriteIds]
                ?.mapNotNull(String::toIntOrNull)
                ?.toSet()
                .orEmpty(),
            firstSeenMillis = prefs[Keys.firstSeenMillis] ?: 0L,
            streak = prefs[Keys.streak] ?: 0,
            bestStreak = prefs[Keys.bestStreak] ?: 0,
            lastOpenDay = prefs[Keys.lastOpenDay] ?: 0,
            proVoiceName = prefs[Keys.proVoiceName] ?: "",
            proSpeechRate = prefs[Keys.proSpeechRate] ?: 1.0f,
            debugAccessOverride = prefs[Keys.debugAccessOverride] ?: ""
        )
    }

    suspend fun ensureTrialStarted(nowMillis: Long = System.currentTimeMillis()): Long {
        var start = nowMillis
        context.quotesDataStore.edit { prefs ->
            start = prefs[Keys.firstSeenMillis] ?: nowMillis
            if (prefs[Keys.firstSeenMillis] == null) {
                prefs[Keys.firstSeenMillis] = nowMillis
            }
        }
        return start
    }

    suspend fun setTheme(themeId: String) {
        context.quotesDataStore.edit { it[Keys.themeId] = themeId }
    }

    suspend fun setProVoice(voiceName: String) {
        context.quotesDataStore.edit { it[Keys.proVoiceName] = voiceName }
    }

    suspend fun setProSpeechRate(rate: Float) {
        context.quotesDataStore.edit { it[Keys.proSpeechRate] = rate.coerceIn(0.7f, 1.4f) }
    }

    suspend fun setDebugAccessOverride(stateName: String?) {
        context.quotesDataStore.edit { prefs ->
            if (stateName.isNullOrBlank()) {
                prefs.remove(Keys.debugAccessOverride)
            } else {
                prefs[Keys.debugAccessOverride] = stateName
            }
        }
    }

    suspend fun toggleFavorite(quoteId: Int) {
        context.quotesDataStore.edit { prefs ->
            val current = prefs[Keys.favoriteIds].orEmpty().toMutableSet()
            val key = quoteId.toString()
            if (!current.add(key)) current.remove(key)
            prefs[Keys.favoriteIds] = current
        }
    }

    suspend fun recordColdOpen(now: Calendar = Calendar.getInstance()): StreakUpdate {
        val today = dayToken(now)
        val yesterdayCalendar = (now.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, -1) }
        val yesterday = dayToken(yesterdayCalendar)
        var result = StreakUpdate(1, 1, false)

        context.quotesDataStore.edit { prefs ->
            val previousDay = prefs[Keys.lastOpenDay] ?: 0
            val previousStreak = prefs[Keys.streak] ?: 0
            val previousBest = prefs[Keys.bestStreak] ?: 0

            val broke = previousDay != 0 && previousDay != today && previousDay != yesterday
            val nextStreak = when (previousDay) {
                today -> previousStreak.coerceAtLeast(1)
                yesterday -> (previousStreak + 1).coerceAtLeast(1)
                else -> 1
            }
            val nextBest = maxOf(previousBest, nextStreak)

            prefs[Keys.lastOpenDay] = today
            prefs[Keys.streak] = nextStreak
            prefs[Keys.bestStreak] = nextBest
            result = StreakUpdate(nextStreak, nextBest, broke && previousStreak > 0)
        }
        return result
    }

    private fun dayToken(calendar: Calendar): Int =
        calendar.get(Calendar.YEAR) * 400 + calendar.get(Calendar.DAY_OF_YEAR)
}
