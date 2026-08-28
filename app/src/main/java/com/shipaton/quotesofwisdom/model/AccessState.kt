package com.shipaton.quotesofwisdom.model

enum class AccessState {
    TRIAL_ACTIVE,
    GRACE_TEXT_ONLY,
    LOCKED,
    PRO
}

object LocalAccessPolicy {
    private const val DAY_MS = 24L * 60L * 60L * 1000L
    private const val TRIAL_DAYS = 30L
    private const val GRACE_DAYS = 3L

    fun stateFor(
        firstSeenMillis: Long,
        nowMillis: Long = System.currentTimeMillis(),
        latestSeenMillis: Long = 0L,
        hasPro: Boolean = false
    ): AccessState {
        if (hasPro) return AccessState.PRO
        if (firstSeenMillis <= 0L) return AccessState.TRIAL_ACTIVE

        val effectiveNow = maxOf(nowMillis, latestSeenMillis, firstSeenMillis)
        val elapsed = effectiveNow - firstSeenMillis
        return when {
            elapsed < TRIAL_DAYS * DAY_MS -> AccessState.TRIAL_ACTIVE
            elapsed < (TRIAL_DAYS + GRACE_DAYS) * DAY_MS -> AccessState.GRACE_TEXT_ONLY
            else -> AccessState.LOCKED
        }
    }

    fun canUseTts(state: AccessState): Boolean =
        state == AccessState.TRIAL_ACTIVE || state == AccessState.PRO

    fun canDismissLaunchPaywall(state: AccessState): Boolean =
        state == AccessState.TRIAL_ACTIVE || state == AccessState.GRACE_TEXT_ONLY
}
