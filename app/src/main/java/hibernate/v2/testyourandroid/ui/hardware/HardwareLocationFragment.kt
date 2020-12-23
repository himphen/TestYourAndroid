package hibernate.v2.testyourandroid.ui.hardware

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.databinding.FragmentHardwareLocationBinding
import hibernate.v2.testyourandroid.model.InfoItem
import hibernate.v2.testyourandroid.ui.base.BaseFragment
import hibernate.v2.testyourandroid.ui.base.InfoItemAdapter
import hibernate.v2.testyourandroid.util.Utils
import hibernate.v2.testyourandroid.util.Utils.startSettingsActivity
import hibernate.v2.testyourandroid.util.viewBinding
import java.util.ArrayList

/**
 * Created by himphen on 21/5/16.
 */
class HardwareLocationFragment : BaseFragment(R.layout.fragment_hardware_location) {

    private val binding by viewBinding(FragmentHardwareLocationBinding::bind)
    private var locationManager: LocationManager? = null

    private lateinit var adapter: InfoItemAdapter
    private val list: MutableList<InfoItem> = ArrayList()
    private var mGoogleMap: GoogleMap? = null
    private var mFusedLocationClient: FusedLocationProviderClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val stringArray = resources.getStringArray(R.array.test_gps_string_array)
        for (string in stringArray) {
            list.add(InfoItem(string, getString(R.string.gps_scanning)))
        }
        adapter = InfoItemAdapter(list)
        binding.rvlist.adapter = adapter
        binding.rvlist.layoutManager = LinearLayoutManager(context)

        locationManager = context?.applicationContext?.getSystemService(Context.LOCATION_SERVICE)
                as LocationManager?

        if (!isPermissionsGranted(PERMISSION_NAME)) {
            requestMultiplePermissions.launch(PERMISSION_NAME)
        }
    }

    override fun onResume() {
        super.onResume()
        if (isPermissionsGranted(PERMISSION_NAME)) {
            onStartScanning()
        }
    }

    override fun onPause() {
        onStopScanning()
        super.onPause()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> startSettingsActivity(
                context,
                Settings.ACTION_LOCATION_SOURCE_SETTINGS
            )
        }
        return super.onOptionsItemSelected(item)
    }

    private fun onStartScanning() {
        context?.let { context ->
            val providers = locationManager?.getProviders(true)

            if (providers == null || providers.size == 0) {
                openFunctionDialog()
                return
            }

            val status = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context)
            if (status != ConnectionResult.SUCCESS) {
                if (GoogleApiAvailability.getInstance().isUserResolvableError(status)) {
                    GoogleApiAvailability.getInstance().getErrorDialog(
                        activity, status,
                        1972
                    ).show()
                }

                return
            }

            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

            val fragment = SupportMapFragment.newInstance()
            fragment.getMapAsync(object : OnMapReadyCallback {
                @SuppressLint("MissingPermission")
                override fun onMapReady(googleMap: GoogleMap) {
                    if (!isPermissionsGranted(PERMISSION_NAME)) return

                    mGoogleMap = googleMap
                    mGoogleMap?.let { mGoogleMap ->
                        if (Utils.isDarkMode(context)) {
                            val style = MapStyleOptions.loadRawResourceStyle(
                                context,
                                R.raw.google_map_dark_mode
                            )
                            mGoogleMap.setMapStyle(style)
                        }

                        val mLocationRequest = LocationRequest.create()
                        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                        mLocationRequest.interval = 1000 // Update location every second
                        mLocationRequest.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
                        mGoogleMap.isMyLocationEnabled = true
                        mGoogleMap.moveCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                DEFAULT_LAT_LNG,
                                15f
                            )
                        )
                        mFusedLocationClient?.requestLocationUpdates(
                            mLocationRequest,
                            mLocationCallback,
                            Looper.myLooper()
                        )
                    }
                }
            })

            childFragmentManager.beginTransaction().replace(R.id.mapFragment, fragment).commit()
        }
    }

    private fun onStopScanning() {
        mFusedLocationClient?.removeLocationUpdates(mLocationCallback)
    }

    private fun openFunctionDialog() {
        context?.let {
            MaterialDialog(it)
                .title(R.string.ui_caution)
                .message(R.string.gps_enable_message)
                .positiveButton(R.string.gps_enable_posbtn) {
                    startSettingsActivity(context, Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                }
                .negativeButton(R.string.ui_cancel)
                .show()
        }
    }

    private var mLocationCallback: LocationCallback = object : LocationCallback() {
        @SuppressLint("MissingPermission")
        override fun onLocationResult(locationResult: LocationResult) {
            if (!isPermissionsGranted(PERMISSION_NAME)) return

            val locationList = locationResult.locations
            if (locationList.size > 0) { //The last location in the list is the newest
                val lastKnowLocation = locationList.last()

                mGoogleMap?.let { mGoogleMap ->
                    mGoogleMap.isMyLocationEnabled = true
                    for (i in list.indices) {
                        list[i].contentText = getData(i, lastKnowLocation)
                    }
                    adapter.notifyDataSetChanged()
                    mGoogleMap.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            LatLng(
                                lastKnowLocation.latitude,
                                lastKnowLocation.longitude
                            ), 15f
                        )
                    )
                }
            }
        }
    }

    private fun getData(j: Int, lastKnowLocation: Location): String {
        try {
            return when (j) {
                0 -> lastKnowLocation.latitude.toString()
                1 -> lastKnowLocation.longitude.toString()
                2 -> lastKnowLocation.speed.toString() + " m/s"
                3 -> lastKnowLocation.altitude.toString() + " m"
                4 -> lastKnowLocation.bearing.toString()
                5 -> lastKnowLocation.accuracy.toString()
                else -> "N/A"
            }
        } catch (e: Exception) {
        }
        return "N/A"
    }

    companion object {
        private val DEFAULT_LAT_LNG = LatLng(22.3185392, 114.1707091)
        val PERMISSION_NAME = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }
}