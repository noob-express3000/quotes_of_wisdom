package com.shipaton.quotesofwisdom.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class DailyWisdomNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            DailyWisdomNotifications.ACTION_DAILY -> {
                DailyWisdomNotifications.showDaily(context)
                DailyWisdomNotifications.scheduleNext(context)
            }

            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_TIME_CHANGED,
            Intent.ACTION_TIMEZONE_CHANGED,
            Intent.ACTION_MY_PACKAGE_REPLACED -> {
                if (DailyWisdomNotifications.isEnabled(context)) {
                    DailyWisdomNotifications.scheduleNext(context)
                }
            }
        }
    }
}
