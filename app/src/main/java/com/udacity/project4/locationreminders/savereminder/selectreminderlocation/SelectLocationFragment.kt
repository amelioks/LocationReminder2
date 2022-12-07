package com.udacity.project4.locationreminders.savereminder.selectreminderlocation



import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.udacity.project4.R
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.databinding.FragmentSelectLocationBinding
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.project4.utils.fineAndCoarseLocationPermissionGranted
import com.udacity.project4.utils.setDisplayHomeAsUpEnabled
import org.koin.android.ext.android.inject
import java.util.*


class SelectLocationFragment : BaseFragment(), OnMapReadyCallback {

    companion object {
        private const val REQUEST_LOCATION_PERMISSION = 1
        private val DEFAULT_LAT_LNG = LatLng(37.422160, -122.084270)
    }

    //Use Koin to get the view model of the SaveReminder
    override val _viewModel: SaveReminderViewModel by inject()
    private lateinit var binding: FragmentSelectLocationBinding
    private lateinit var map: GoogleMap
    private var selectedLocationMarker: Marker? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_select_location, container, false)

        binding.viewModel = _viewModel
        binding.lifecycleOwner = this

        setHasOptionsMenu(true)
        setDisplayHomeAsUpEnabled(true)

        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map_selectlocation) as SupportMapFragment
        mapFragment.getMapAsync(this)

        binding.buttonSaveLocation.setOnClickListener {
            onLocationSelected()
        }

//        TODO: add the map setup implementation
//        TODO: zoom to the user location after taking his permission
//        TODO: add style to the map
//        TODO: put a marker to location that the user selected


//        TODO: call this function after the user confirms on the selected location

        return binding.root
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        val zoomLevel = 15f
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(DEFAULT_LAT_LNG, zoomLevel))
        map.addMarker(MarkerOptions().position(DEFAULT_LAT_LNG))
        setPoiClick(map)
        addMapMarker(map)
        setMapStyle(map)
        enableMapLocation()
    }

    private fun setPoiClick(map: GoogleMap) {
        map.setOnPoiClickListener { poi ->
            addPoiMarker(poi)
        }
    }

    private fun addPoiMarker(poi: PointOfInterest) {
        //to reset marker in Poi
        map.clear()
        selectedLocationMarker?.remove()
        selectedLocationMarker = map.addMarker(
            MarkerOptions()
                .position(poi.latLng)
                .title(poi.name)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
        )
        selectedLocationMarker!!.showInfoWindow()
    }

    private fun addMapMarker(map: GoogleMap) {
        map.setOnMapLongClickListener { latLng ->
            val snippet = String.format(
                Locale.getDefault(),
                "Lat: %1$.5f, Long: %2$.5f",
                latLng.latitude,
                latLng.longitude
            )
            selectedLocationMarker?.remove()
            selectedLocationMarker = map.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title(getString(R.string.dropped_pin))
                    .snippet(snippet)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
            )
        }
    }

    private fun setMapStyle(map: GoogleMap) {
        map.setMapStyle(
            MapStyleOptions.loadRawResourceStyle(
                requireContext(),
                R.raw.map_style
            )
        )
    }

    private fun onLocationSelected() {
        selectedLocationMarker?.let {
            _viewModel.reminderSelectedLocationStr.value = it.title
            _viewModel.latitude.value = it.position.latitude
            _viewModel.longitude.value = it.position.longitude
        }
        findNavController().popBackStack()
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.map_options, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.normal_map -> {
            map.mapType = GoogleMap.MAP_TYPE_NORMAL
            true
        }
        R.id.hybrid_map -> {
            map.mapType = GoogleMap.MAP_TYPE_HYBRID
            true
        }
        R.id.satellite_map -> {
            map.mapType = GoogleMap.MAP_TYPE_SATELLITE
            true
        }
        R.id.terrain_map -> {
            map.mapType = GoogleMap.MAP_TYPE_TERRAIN
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    @SuppressLint("MissingPermission")
    fun enableMapLocation() {
        if (requireContext().fineAndCoarseLocationPermissionGranted()) {
            map.setMyLocationEnabled(true)
        } else {
            requestPermissions(
                arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_LOCATION_PERMISSION -> {
                if (grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    enableMapLocation()
                } else {
                    Toast.makeText(context, "Permission is not granted!", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }
}
