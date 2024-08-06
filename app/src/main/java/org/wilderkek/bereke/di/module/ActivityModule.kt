package org.wilderkek.bereke.di.module

import org.koin.dsl.module
import org.wilderkek.bereke.ui.main.MainActivity
import org.wilderkek.bereke.util.rxtakephoto.RxTakePhoto


fun provideMainActivity(): MainActivity = MainActivity.getInstance()!!
fun provideRxTakePhoto(activity: MainActivity) = RxTakePhoto(activity)
//fun provideRxPermissions(activity: MainActivity): RxPermissions = RxPermissions(activity)

val activityModule = module {
    single { provideMainActivity() }
    single { provideRxTakePhoto(get()) }
    //single { provideRxPermissions(get()) }
}