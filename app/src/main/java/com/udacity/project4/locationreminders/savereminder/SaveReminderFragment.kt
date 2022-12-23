package com.udacity.project4.locationreminders.savereminder

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.ContentValues
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.material.snackbar.Snackbar
import com.udacity.project4.R
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.databinding.FragmentSaveReminderBinding
import com.udacity.project4.locationreminders.geofence.GeofenceBroadcastReceiver
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import com.udacity.project4.utils.REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_RESULT_CODE
import com.udacity.project4.utils.foregroundAndBackgroundLocationPermissionGranted
import com.udacity.project4.utils.requestForegroundAndBackgroundLocationPermissions
import com.udacity.project4.utils.setDisplayHomeAsUpEnabled
import org.koin.android.ext.android.inject

class SaveReminderFragment : BaseFragment() {
    //Get the view model this time as a single to be shared with the another fragment
    override val _viewModel: SaveReminderViewModel by inject()
    private lateinit var binding: FragmentSaveReminderBinding
    private lateinit var reminderDataItem: ReminderDataItem //to hold the reminder data to be saved
    private lateinit var geofencingClient: GeofencingClient


    companion object {
        private const val REQUEST_TURN_DEVICE_LOCATION_ON = 29
        private const val ACTION_GEOFENCE_EVENT =
            "SaveReminderFragment.project4.action.ACTION_GEOFENCE_EVENT"
        private const val GEOFENCE_RADIUS_IN_METERS = 100f

    }

    // A PendingIntent for the Broadcast Receiver that handles geofence transitions
    private val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(requireContext(), GeofenceBroadcastReceiver::class.java)
        intent.action = ACTION_GEOFENCE_EVENT
        PendingIntent.getBroadcast(requireContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_save_reminder, container, false)

        setDisplayHomeAsUpEnabled(true)
        binding.viewModel = _viewModel

        //main entry point for interacting with the geofencing APIs
        geofencingClient = LocationServices.getGeofencingClient(requireActivity())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this
        binding.selectLocation.setOnClickListener {
            //Navigate to another fragment to get the user location
            _viewModel.navigationCommand.value =
                NavigationCommand.To(SaveReminderFragmentDirections.actionSaveReminderFragmentToSelectLocationFragment())
        }

        binding.buttonSaveReminder.setOnClickListener {
            val title = _viewModel.reminderTitle.value
            val description = _viewModel.reminderDescription.value
            val location = _viewModel.reminderSelectedLocationStr.value
            val latitude = _viewModel.latitude.value
            val longitude = _viewModel.longitude.value

            reminderDataItem = ReminderDataItem(
                title,
                description,
                location,
                latitude,
                longitude
            )

            //validating input data
            if (_viewModel.validateEnteredData(reminderDataItem)) {
                checkPermissionAndStartGeofencing()
            }
        }
    }

    //Called to check permissions and if granted, start geofencing
    private fun checkPermissionAndStartGeofencing() {
        if (requireActivity().foregroundAndBackgroundLocationPermissionGranted()) {
            checkDeviceLocationSettingsAndStartGeofence()
        } else {
            requestForegroundAndBackgroundLocationPermissions()
        }
    }

    //Check Device Location and start geofence
    private fun checkDeviceLocationSettingsAndStartGeofence(resolve: Boolean = true) {
        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_LOW_POWER
        }
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val settingsClient = LocationServices.getSettingsClient(requireActivity())
        val locationSettingsResponseTask =
            settingsClient.checkLocationSettings(builder.build())
        locationSettingsResponseTask.addOnFailureListener { exception ->
            if (exception is ResolvableApiException && resolve) {
                try {
                    exception.startResolutionForResult(
                        this@SaveReminderFragment.requireActivity(),
                        REQUEST_TURN_DEVICE_LOCATION_ON
                    )
                } catch (sendEx: IntentSender.SendIntentException) {
                    Log.d(ContentValues.TAG, "Error getting location settings resolution: " + sendEx.message)
                }
            } else {
                Snackbar.make(
                    binding.buttonSaveReminder,
                    R.string.location_required_error, Snackbar.LENGTH_INDEFINITE
                ).setAction(android.R.string.ok) {
                    checkDeviceLocationSettingsAndStartGeofence()
                }.show()
            }
        }
        //If the locationSettingsResponseTask does complete, check that it is successful,
        // if so, it will add the geofence
        locationSettingsResponseTask.addOnCompleteListener {
            if (it.isSuccessful) {
                addNewGeofence()
            }
        }
    }

    // add new geofence
    // save reminder and navigates back
    @SuppressLint("MissingPermission")
    private fun addNewGeofence() {
        val geofence = Geofence.Builder()
            .setRequestId(reminderDataItem.id)
            .setCircularRegion(
                reminderDataItem.latitude!!,
                reminderDataItem.longitude!!,
                GEOFENCE_RADIUS_IN_METERS
            )
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
            .build()

        val geofencingRequest = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofence(geofence)
            .build()

        geofencingClient.removeGeofences(geofencePendingIntent)?.run {
            addOnCompleteListener {
                geofencingClient.addGeofences(geofencingRequest, geofencePendingIntent)?.run {
                    addOnSuccessListener {
                        Log.e("Add Geofence", geofence.requestId)
                        _viewModel.saveReminder(reminderDataItem)
                    }
                    addOnFailureListener {
                        // Failed to add geofence.
                        _viewModel.showSnackBarInt.value = R.string.error_adding_geofence
                    }
                }
            }
        }
        _viewModel.onClear()
    }

    // Callback for the result from requesting permissions.
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        Log.d(ContentValues.TAG, "onRequestPermissionResult")
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_RESULT_CODE &&
            grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            checkDeviceLocationSettingsAndStartGeofence()
        } else {
            Snackbar.make(
                binding.buttonSaveReminder,
                R.string.location_required_error, Snackbar.LENGTH_INDEFINITE
            ).setAction(android.R.string.ok) {
                requestForegroundAndBackgroundLocationPermissions()
            }.show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        //make sure to clear the view model after destroy, as it's a single view model.
        _viewModel.onClear()
    }
}
