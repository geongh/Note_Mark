package com.solaisc.notemark

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.context.startKoin

class NoteMarkApp: Application() {
    val applicationScope = CoroutineScope(SupervisorJob())

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@NoteMarkApp)
            workManagerFactory()
            modules(
                appModule
            )
        }
    }
}