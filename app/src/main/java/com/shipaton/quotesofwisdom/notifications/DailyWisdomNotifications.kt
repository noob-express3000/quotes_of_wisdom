package com.shipaton.quotesofwisdom.notifications

import android.Manifest
import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import com.shipaton.quotesofwisdom.MainActivity
import com.shipaton.quotesofwisdom.R
import java.util.Calendar

object DailyWisdomNotifications {
    internal const val ACTION_DAILY = "com.shipaton.quotesofwisdom.action.DAILY_WISDOM"

    const val DEFAULT_REMINDER_HOUR = 9
    const val DEFAULT_REMINDER_MINUTE = 0

    private const val PREFS_NAME = "daily_wisdom_notifications"
    private const val KEY_ENABLED = "enabled"
    private const val KEY_REMINDER_HOUR = "reminder_hour"
    private const val KEY_REMINDER_MINUTE = "reminder_minute"
    private const val DAILY_CHANNEL_ID = "daily_wisdom"
    private const val DEMO_CHANNEL_ID = "demo_wisdom"
    private const val DAILY_NOTIFICATION_ID = 4100
    private const val DEMO_NOTIFICATION_ID = 4101
    private const val DAILY_ALARM_REQUEST_CODE = 4102
    private const val OPEN_APP_REQUEST_CODE = 4103

    private val reminderCopy = listOf(
        "One good sentence. No doomscrolling required.",
        "A small thought for a very large day.",
        "The philosophers have been waiting.",
        "Thirty quiet seconds might be enough.",
        "Your daily ritual is ready.",
        "A little perspective before the noise starts.",
        "There is still time for one good thought."
    )

    fun isEnabled(context: Context): Boolean =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getBoolean(KEY_ENABLED, false)

    fun reminderHour(context: Context): Int =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getInt(KEY_REMINDER_HOUR, DEFAULT_REMINDER_HOUR)
            .coerceIn(0, 23)

    fun reminderMinute(context: Context): Int =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getInt(KEY_REMINDER_MINUTE, DEFAULT_REMINDER_MINUTE)
            .coerceIn(0, 59)

    fun setReminderTime(context: Context, hour: Int, minute: Int) {
        val safeHour = hour.coerceIn(0, 23)
        val safeMinute = minute.coerceIn(0, 59)

        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putInt(KEY_REMINDER_HOUR, safeHour)
            .putInt(KEY_REMINDER_MINUTE, safeMinute)
            .apply()

        if (isEnabled(context)) scheduleNext(context)
    }

    fun setEnabled(context: Context, enabled: Boolean) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_ENABLED, enabled)
            .apply()

        if (enabled) {
            ensureChannels(context)
            scheduleNext(context)
        } else {
            cancel(context)
        }
    }

    fun canPost(context: Context): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) ==
            PackageManager.PERMISSION_GRANTED

    fun ensureChannels(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val manager = context.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(
            NotificationChannel(
                DAILY_CHANNEL_ID,
                "Daily wisdom",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "One daily reminder to return to Quotes of Wisdom."
                setShowBadge(true)
            }
        )
    }

    fun scheduleNext(context: Context) {
        if (!isEnabled(context)) return

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val now = Calendar.getInstance()
        val next = (now.clone() as Calendar).apply {
            set(Calendar.HOUR_OF_DAY, reminderHour(context))
            set(Calendar.MINUTE, reminderMinute(context))
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (timeInMillis <= now.timeInMillis) add(Calendar.DAY_OF_YEAR, 1)
        }

        alarmManager.setAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            next.timeInMillis,
            dailyAlarmIntent(context)
        )
    }

    fun cancel(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(dailyAlarmIntent(context))
    }

    internal fun showDaily(context: Context) {
        if (!isEnabled(context) || !canPost(context)) return
        ensureChannels(context)

        val day = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
        val body = reminderCopy[day % reminderCopy.size]
        notify(
            context = context,
            channelId = DAILY_CHANNEL_ID,
            notificationId = DAILY_NOTIFICATION_ID,
            title = "A thought for today",
            body = body,
            highPriority = false
        )
    }

    fun showDemo(context: Context) {
        if (!canPost(context)) return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(
                NotificationChannel(
                    DEMO_CHANNEL_ID,
                    "Demo wisdom",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Debug-only notification channel for demo capture."
                    setShowBadge(false)
                }
            )
        }

        notify(
            context = context,
            channelId = DEMO_CHANNEL_ID,
            notificationId = DEMO_NOTIFICATION_ID,
            title = "Quotes of Wisdom",
            body = "One good sentence. No doomscrolling required.",
            highPriority = true
        )
    }

    private fun notify(
        context: Context,
        channelId: String,
        notificationId: Int,
        title: String,
        body: String,
        highPriority: Boolean
    ) {
        val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(context, channelId)
        } else {
            Notification.Builder(context)
        }

        builder
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(Notification.BigTextStyle().bigText(body))
            .setContentIntent(openAppIntent(context))
            .setAutoCancel(true)
            .setCategory(Notification.CATEGORY_REMINDER)
            .setVisibility(Notification.VISIBILITY_PUBLIC)

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            builder.setPriority(
                if (highPriority) Notification.PRIORITY_HIGH else Notification.PRIORITY_DEFAULT
            )
            if (highPriority) builder.setDefaults(Notification.DEFAULT_ALL)
        }

        context.getSystemService(NotificationManager::class.java)
            .notify(notificationId, builder.build())
    }

    private fun dailyAlarmIntent(context: Context): PendingIntent =
        PendingIntent.getBroadcast(
            context,
            DAILY_ALARM_REQUEST_CODE,
            Intent(context, DailyWisdomNotificationReceiver::class.java).apply {
                action = ACTION_DAILY
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

    private fun openAppIntent(context: Context): PendingIntent =
        PendingIntent.getActivity(
            context,
            OPEN_APP_REQUEST_CODE,
            Intent(context, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
}
