package com.shipaton.quotesofwisdom

import android.app.Application
import com.shipaton.quotesofwisdom.billing.RevenueCatController

class QuotesApplication : Application() {
    val revenueCatController: RevenueCatController by lazy {
        RevenueCatController(applicationContext)
    }

    override fun onCreate() {
        super.onCreate()
        revenueCatController.configure()
    }
}
