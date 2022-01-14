package com.mtm.uber_mimic

import android.app.Application
import com.google.firebase.FirebaseApp
import com.mtm.uber_mimic.di.mainModule
import com.mtm.uber_mimic.di.requestRideModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber
import timber.log.Timber.Forest.plant


class UberMimicApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG)
            plant(Timber.DebugTree())

        FirebaseApp.initializeApp(this.applicationContext)

        startKoin {
            androidLogger()
            androidContext(this@UberMimicApplication)
            modules(mainModule, requestRideModule)
        }
    }
}