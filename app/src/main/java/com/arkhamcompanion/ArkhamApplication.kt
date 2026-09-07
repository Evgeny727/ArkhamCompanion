package com.arkhamcompanion

import android.app.Application
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ArkhamApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Firebase.crashlytics.isCrashlyticsCollectionEnabled = false
        }
    }
}