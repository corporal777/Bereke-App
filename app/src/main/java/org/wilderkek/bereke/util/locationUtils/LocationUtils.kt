package org.wilderkek.bereke.util.locationUtils

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Context.LOCATION_SERVICE
import android.content.IntentSender.SendIntentException
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import com.tbruyelle.rxpermissions2.RxPermissions
import com.tbruyelle.rxpermissions2.RxPermissionsFragment
import com.yandex.metrica.impl.ob.Bo
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import org.wilderkek.bereke.util.rxtakephoto.GPSNotEnabledException
import org.wilderkek.bereke.util.rxtakephoto.PermissionNotGrantedException


class LocationUtils(val fragment: Fragment) {

    private val launcher =
        fragment.registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { activityResult ->
            if (activityResult.resultCode == Activity.RESULT_OK) {
                Log.e("Status: ", "GPS Enabled")
                updateLocationRequest()
            } else locationSubject.onError(GPSNotEnabledException())
        }

    private val locationSubject: PublishSubject<Location> = PublishSubject.create()

    private val locationRequest: LocationRequest = LocationRequest.create()
        .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        .setNumUpdates(1)

    private val locationProvider: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(fragment.requireContext())


    fun getLocation(): Observable<Location> {
        return checkPermissions(fragment).flatMap {
            if (it) {
                isGPSEnabled(fragment)
                Observable.defer { locationSubject }
            } else Observable.error(PermissionNotGrantedException())
        }
    }


    private fun checkPermissions(fragment: Fragment): Observable<Boolean> {
        return RxPermissions(fragment).request(
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }

    private fun isGPSEnabled(context: Fragment) {
        val locationManager =
            context.requireActivity().getSystemService(LOCATION_SERVICE) as LocationManager
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            updateLocationRequest()
        } else {
            enableGPS(context)
        }
    }

    private fun enableGPS(context: Fragment) {
        val googleApiClient = GoogleApiClient.Builder(context.requireContext())
            .addApi(LocationServices.API)
            .addConnectionCallbacks(object : GoogleApiClient.ConnectionCallbacks {
                override fun onConnected(bundle: Bundle?) {}
                override fun onConnectionSuspended(i: Int) {}
            })
            .addOnConnectionFailedListener { _ -> }
            .build()
        googleApiClient.connect()


        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        builder.setAlwaysShow(true)

        val settingsClient: SettingsClient =
            LocationServices.getSettingsClient(context.requireActivity())
        val task: Task<LocationSettingsResponse> =
            settingsClient.checkLocationSettings(builder.build())

        task.addOnSuccessListener {
            updateLocationRequest()
        }

        task.addOnFailureListener { e ->
            if (e is ResolvableApiException) {
                try {
                    e.printStackTrace()
                    launcher.launch(IntentSenderRequest.Builder(e.resolution).build())
                } catch (sendIntentException: SendIntentException) {
                    locationSubject.onError(sendIntentException)
                    sendIntentException.printStackTrace()
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun updateLocationRequest() {
        val callback = object : LocationCallback() {
            override fun onLocationResult(location: LocationResult) {
                location.lastLocation?.let {
                    locationSubject.onNext(it)
                    locationProvider.removeLocationUpdates(this)
                }
            }
        }

        locationProvider.requestLocationUpdates(
            locationRequest,
            callback,
            Looper.getMainLooper()
        )
            .addOnFailureListener {
                locationSubject.onError(it)
                it.printStackTrace()
                locationProvider.removeLocationUpdates(callback)
            }
    }

    fun clearFields(){
        launcher.unregister()
    }
}