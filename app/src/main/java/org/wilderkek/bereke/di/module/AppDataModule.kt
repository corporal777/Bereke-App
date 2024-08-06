package org.wilderkek.bereke.di.module

import android.content.Context
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import org.wilderkek.bereke.di.data.AppData
import org.wilderkek.bereke.di.data.AppPrefs
import org.wilderkek.bereke.di.data.AppPrefsImpl
import org.wilderkek.bereke.util.NotificationUtil

val appDataModule = module {

    fun provideAppPrefs(context: Context): AppPrefs = AppPrefsImpl(context)
    fun provideAppData(appPrefs: AppPrefs, context: Context): AppData = AppData(appPrefs, context)
    fun provideNotificationUtil(context: Context) = NotificationUtil(context)

    single { provideAppPrefs(androidContext()) }
    single { provideAppData(get(), androidContext()) }
    single { provideNotificationUtil(androidContext()) }
}