package hibernate.v2.testyourandroid.ui.hardware

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
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
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.databinding.FragmentHardwareLocationBinding
import hibernate.v2.testyourandroid.model.InfoItem
import hibernate.v2.testyourandroid.ui.base.BaseFragment
import hibernate.v2.testyourandroid.ui.base.InfoItemAdapter
import hibernate.v2.testyourandroid.util.Utils
import hibernate.v2.testyourandroid.util.Utils.startSettingsActivity
import hibernate.v2.testyourandroid.util.ext.disableChangeAnimation
import hibernate.v2.testyourandroid.util.ext.isPermissionsGranted

/**
 * Created by himphen on 21/5/16.
 */
class HardwareLocationFragment : BaseFragment<FragmentHardwareLocationBinding>() {

    override val permissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentHardwareLocationBinding =
        FragmentHardwareLocationBinding.inflate(inflater, container, false)

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
        stringArray.forEachIndexed { index, string ->
            list.add(InfoItem(string, getString(R.string.gps_scanning)))
        }
        adapter = InfoItemAdapter().apply {
            setData(list)
        }
        viewBinding?.rvlist?.adapter = adapter
        viewBinding?.rvlist?.layoutManager = LinearLayoutManager(context)
        viewBinding?.rvlist?.disableChangeAnimation()

        locationManager = context?.applicationContext?.getSystemService(Context.LOCATION_SERVICE)
            as LocationManager?

        if (!isPermissionsGranted(permissions)) {
            permissionLifecycleObserver?.requestPermissions(permissions)
        }
    }

    override fun onResume() {
        super.onResume()
        if (isPermissionsGranted(permissions)) {
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
        activity?.let { activity ->
            val providers = locationManager?.getProviders(true)

            if (providers == null || providers.size == 0) {
                openFunctionDialog()
                return
            }

            val status = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(activity)
            if (status != ConnectionResult.SUCCESS) {
                if (GoogleApiAvailability.getInstance().isUserResolvableError(status)) {
                    GoogleApiAvailability.getInstance().getErrorDialog(
                        activity, status,
                        1972
                    )?.show()
                }

                return
            }

            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)

            val fragment = SupportMapFragment.newInstance()
            fragment.getMapAsync(object : OnMapReadyCallback {
                @SuppressLint("MissingPermission")
                override fun onMapReady(googleMap: GoogleMap) {
                    if (!isPermissionsGranted(permissions)) return

                    mGoogleMap = googleMap
                    mGoogleMap?.let { mGoogleMap ->
                        if (Utils.isDarkMode(activity)) {
                            val style = MapStyleOptions.loadRawResourceStyle(
                                activity,
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
                            Looper.getMainLooper()
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
            MaterialAlertDialogBuilder(it)
                .setTitle(R.string.ui_caution)
                .setMessage(R.string.gps_enable_message)
                .setPositiveButton(R.string.gps_enable_posbtn) { dialog, which ->
                    startSettingsActivity(context, Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                }
                .setNegativeButton(R.string.ui_cancel, null)
                .show()
        }
    }

    private var mLocationCallback: LocationCallback = object : LocationCallback() {
        @SuppressLint("MissingPermission")
        override fun onLocationResult(locationResult: LocationResult) {
            if (!isPermissionsGranted(permissions)) return

            val locationList = locationResult.locations
            if (locationList.size > 0) { // The last location in the list is the newest
                val lastKnowLocation = locationList.last()

                mGoogleMap?.let { mGoogleMap ->
                    mGoogleMap.isMyLocationEnabled = true
                    for (i in list.indices) {
                        list[i].contentText = getData(i, lastKnowLocation)
                    }
                    adapter.setData(list)
                    mGoogleMap.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            LatLng(
                                lastKnowLocation.latitude,
                                lastKnowLocation.longitude
                            ),
                            15f
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
    }
}
