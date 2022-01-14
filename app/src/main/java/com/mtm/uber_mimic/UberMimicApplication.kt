package com.mtm.uber_mimic

import android.app.Application
import com.mtm.uber_mimic.di.requestRideModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class UberMimicApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@UberMimicApplication)
            modules(requestRideModule)
        }
    }
}