package org.wilderkek.bereke.di.module

import android.content.Context
import com.google.android.gms.location.LocationServices
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

fun provideFusedLocationClient(context: Context) =
    LocationServices.getFusedLocationProviderClient(context)

val locationModule = module {
    single { provideFusedLocationClient(androidContext()) }
}