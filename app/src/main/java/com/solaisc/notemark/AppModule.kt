package com.solaisc.notemark

import android.content.Context
import android.content.SharedPreferences
import com.solaisc.notemark.feature.auth.data.AuthRepositoryImpl
import com.solaisc.notemark.feature.auth.domain.AuthRepository
import com.solaisc.notemark.feature.auth.presentation.login.LoginViewModel
import com.solaisc.notemark.feature.auth.presentation.register.RegisterViewModel
import com.solaisc.notemark.util.authentication.SessionStorage
import com.solaisc.notemark.util.authentication.SharedPrefSessionStorage
import com.solaisc.notemark.util.networking.HttpClientFactory
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val appModule = module {
    single {
        HttpClientFactory(get()).build()
    }

    single { provideSharedPreferences(androidContext()) }

    singleOf(::SharedPrefSessionStorage).bind<SessionStorage>()

    viewModelOf(::MainViewModel)

    singleOf(::AuthRepositoryImpl).bind<AuthRepository>()
    viewModelOf(::LoginViewModel)
    viewModelOf(::RegisterViewModel)
}



private fun provideSharedPreferences(context: Context): SharedPreferences {
    return context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
}