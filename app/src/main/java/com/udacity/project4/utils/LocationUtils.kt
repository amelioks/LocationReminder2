package com.udacity.project4.utils

import android.Manifest
import android.annotation.TargetApi
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment


val runningQOrLater = android.os.Build.VERSION.SDK_INT >=
        android.os.Build.VERSION_CODES.Q

private const val REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_RESULT_CODE = 33
private const val REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE = 34

//Check if fine background location permission granted
@TargetApi(29)
fun Context.fineAndCoarseLocationPermissionGranted(): Boolean {
    val fineLocationGranted = ActivityCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    val coarseLocationGranted =
        if (runningQOrLater) {
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    return fineLocationGranted && coarseLocationGranted
}

//Check if foreground and background location permission granted
@TargetApi(29)
fun Context.foregroundAndBackgroundLocationPermissionGranted(): Boolean {
    val backgroundPermissionGranted =
        if (runningQOrLater) {
            PackageManager.PERMISSION_GRANTED ==
                    ActivityCompat.checkSelfPermission(
                        this, Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    )
        } else {
            true
        }
    return backgroundPermissionGranted && fineAndCoarseLocationPermissionGranted()
}

// Checks if users have given their location and sets location enabled if so
@TargetApi(29)
fun Fragment.requestForegroundAndBackgroundLocationPermissions() {
    var permissionsArray = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
    val requestCode = when {
        runningQOrLater -> {
            permissionsArray += Manifest.permission.ACCESS_BACKGROUND_LOCATION
            REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_RESULT_CODE
        }
        else -> REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE
    }
    Log.d(ContentValues.TAG, "Request foreground only location permission")
    requestPermissions(
        permissionsArray,
        requestCode
    )
}