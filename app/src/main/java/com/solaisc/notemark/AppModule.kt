package com.solaisc.notemark

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.solaisc.notemark.feature.auth.data.AuthRepositoryImpl
import com.solaisc.notemark.feature.auth.domain.AuthRepository
import com.solaisc.notemark.feature.auth.presentation.login.LoginViewModel
import com.solaisc.notemark.feature.auth.presentation.register.RegisterViewModel
import com.solaisc.notemark.feature.note.data.local.NoteDatabase
import com.solaisc.notemark.feature.note.data.local.repository.NoteRoomRepositoryImpl
import com.solaisc.notemark.feature.note.data.network.repository.NoteKtorRepositoryImpl
import com.solaisc.notemark.feature.note.domain.repository.NoteLocalRepository
import com.solaisc.notemark.feature.note.domain.repository.NoteNetworkRepository
import com.solaisc.notemark.feature.note.presentation.input_note.NoteViewModel
import com.solaisc.notemark.feature.note.presentation.list_note.NotesViewModel
import com.solaisc.notemark.feature.setting.presentation.SettingViewModel
import com.solaisc.notemark.util.authentication.SessionStorage
import com.solaisc.notemark.util.authentication.SharedPrefSessionStorage
import com.solaisc.notemark.util.networking.HttpClientFactory
import com.solaisc.notemark.util.networking.connection_observer.AndroidConnectivityObserver
import com.solaisc.notemark.util.networking.connection_observer.ConnectivityObserver
import com.solaisc.notemark.util.networking.sync.CreateNoteWorker
import com.solaisc.notemark.util.networking.sync.DeleteNoteWorker
import com.solaisc.notemark.util.networking.sync.FetchNotesWorker
import com.solaisc.notemark.util.networking.sync.SyncNoteScheduler
import com.solaisc.notemark.util.networking.sync.SyncNoteWorkerScheduler
import kotlinx.coroutines.CoroutineScope
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.androidx.workmanager.dsl.workerOf
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

    single {
        Room.databaseBuilder(
            androidApplication(),
            NoteDatabase::class.java,
            "notemark.db"
        ).build()
    }
    single { get<NoteDatabase>().noteDao }

    single<CoroutineScope> {
        (androidApplication() as NoteMarkApp).applicationScope
    }

    singleOf(::AndroidConnectivityObserver).bind<ConnectivityObserver>()
    singleOf(::SyncNoteWorkerScheduler).bind<SyncNoteScheduler>()
    singleOf(::NoteRoomRepositoryImpl).bind<NoteLocalRepository>()
    singleOf(::NoteKtorRepositoryImpl).bind<NoteNetworkRepository>()

    workerOf(::CreateNoteWorker)
    workerOf(::DeleteNoteWorker)
    workerOf(::FetchNotesWorker)

    viewModelOf(::NoteViewModel)
    viewModelOf(::NotesViewModel)

    viewModelOf(::SettingViewModel)
}



private fun provideSharedPreferences(context: Context): SharedPreferences {
    return context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
}