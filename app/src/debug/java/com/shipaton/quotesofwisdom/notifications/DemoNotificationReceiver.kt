package com.shipaton.quotesofwisdom.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class DemoNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        DailyWisdomNotifications.showDemo(context)
    }
}
