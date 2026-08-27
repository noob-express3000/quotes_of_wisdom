package com.shipaton.quotesofwisdom.notifications

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import com.shipaton.quotesofwisdom.MainActivity

class NotificationGateActivity : Activity() {
    companion object {
        private const val PREFS_NAME = "notification_gate"
        private const val KEY_HAS_LAUNCHED = "has_launched"
        private const val KEY_PERMISSION_PROMPTED = "permission_prompted"
        private const val REQUEST_NOTIFICATIONS = 4200
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        DailyWisdomNotifications.ensureChannels(this)

        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val hasLaunchedBefore = prefs.getBoolean(KEY_HAS_LAUNCHED, false)

        if (!hasLaunchedBefore) {
            prefs.edit().putBoolean(KEY_HAS_LAUNCHED, true).apply()

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                DailyWisdomNotifications.setEnabled(this, true)
            }

            openMainApp()
            return
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            DailyWisdomNotifications.setEnabled(this, true)
            openMainApp()
            return
        }

        if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            DailyWisdomNotifications.setEnabled(this, true)
            openMainApp()
            return
        }

        if (prefs.getBoolean(KEY_PERMISSION_PROMPTED, false)) {
            openMainApp()
            return
        }

        prefs.edit().putBoolean(KEY_PERMISSION_PROMPTED, true).apply()
        requestPermissions(
            arrayOf(Manifest.permission.POST_NOTIFICATIONS),
            REQUEST_NOTIFICATIONS
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_NOTIFICATIONS) {
            val granted = grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED
            DailyWisdomNotifications.setEnabled(this, granted)
            openMainApp()
        }
    }

    private fun openMainApp() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
