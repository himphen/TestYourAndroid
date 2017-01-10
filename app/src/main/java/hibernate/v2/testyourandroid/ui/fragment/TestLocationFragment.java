package hibernate.v2.testyourandroid.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import hibernate.v2.testyourandroid.C;
import hibernate.v2.testyourandroid.R;
import hibernate.v2.testyourandroid.model.InfoItem;
import hibernate.v2.testyourandroid.ui.adapter.InfoItemAdapter;

/**
 * Created by himphen on 21/5/16.
 */
public class TestLocationFragment extends BaseFragment implements
		GoogleApiClient.ConnectionCallbacks,
		GoogleApiClient.OnConnectionFailedListener,
		LocationListener,
		OnMapReadyCallback {

	private InfoItemAdapter adapter;
	private List<InfoItem> list = new ArrayList<>();

	@BindView(R.id.rvlist)
	RecyclerView recyclerView;

	private GoogleApiClient mGoogleApiClient;
	private static final LatLng DEFAULT_LATLNG = new LatLng(22.3185392, 114.1707091);

	private GoogleMap googleMap;
	private Location myLocation;

	public TestLocationFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View rootView = inflater.inflate(R.layout.fragment_test_location, container, false);
		ButterKnife.bind(this, rootView);
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		FragmentManager fm = getChildFragmentManager();
		SupportMapFragment fragment = (SupportMapFragment) fm.findFragmentById(R.id.mapFragment);
		if (fragment == null) {
			fragment = SupportMapFragment.newInstance();
			fm.beginTransaction().replace(R.id.mapFragment, fragment).commit();
		}

		fragment.getMapAsync(this);

		int status = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getContext());
		if (status != ConnectionResult.SUCCESS) {
			if (GoogleApiAvailability.getInstance().isUserResolvableError(status)) {
				GoogleApiAvailability.getInstance().getErrorDialog(getActivity(), status,
						1972).show();
			}
		}
		mGoogleApiClient.connect();
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		recyclerView.setLayoutManager(
				new LinearLayoutManager(mContext,
						LinearLayoutManager.VERTICAL, false)
		);
		init();
	}

	@Override
	public void onStart() {
		super.onStart();
		mGoogleApiClient.connect();
	}

	@Override
	public void onStop() {
		super.onStop();
		if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
			mGoogleApiClient.disconnect();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_settings:
				startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void init() {
		String[] stringArray = getResources().getStringArray(R.array.test_gps_string_array);

		for (String aStringArray : stringArray) {
			list.add(new InfoItem(aStringArray, getString(R.string.gps_scanning)));
		}

		adapter = new InfoItemAdapter(list);
		recyclerView.setAdapter(adapter);

		mGoogleApiClient = new GoogleApiClient.Builder(mContext)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.addApi(LocationServices.API)
				.build();

		// Get location from GPS if it's available
		LocationManager lm = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);

		try {
			boolean gpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
			boolean networkEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
			if (!gpsEnabled && !networkEnabled) {
				openFunctionDialog();
			}
		} catch (Exception ignored) {
		}
	}

	public void openFunctionDialog() {
		MaterialDialog.Builder dialog = new MaterialDialog.Builder(mContext)
				.title(R.string.ui_caution)
				.content(R.string.gps_enable_message)
				.negativeText(R.string.ui_cancel)
				.positiveText(R.string.gps_enable_posbtn)
				.onPositive(new MaterialDialog.SingleButtonCallback() {
					@Override
					public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
						startActivity(new Intent(
								Settings.ACTION_LOCATION_SOURCE_SETTINGS));
					}
				});
		dialog.show();
	}

	@Override
	public void onLocationChanged(Location location) {
		mGoogleApiClient.disconnect();

		Log.d(C.TAG, "onLocationChanged");

		if (getContext() != null && GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getContext())
				== ConnectionResult.SUCCESS && googleMap != null) {
			updateMap();
		}
	}

	@Override
	public void onConnected(@Nullable Bundle bundle) {
		if (mContext == null)
			return;
		LocationRequest mLocationRequest = LocationRequest.create();
		mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		mLocationRequest.setInterval(1000); // Update location every second

		LocationServices.FusedLocationApi.requestLocationUpdates(
				mGoogleApiClient, mLocationRequest, this);
	}

	@Override
	public void onConnectionSuspended(int i) {
		updateMap();
	}

	@Override
	public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
		if (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(mContext)
				== ConnectionResult.SUCCESS && googleMap != null) {
			updateMap();
		}
	}

	@Override
	public void onMapReady(GoogleMap googleMap) {
		this.googleMap = googleMap;
		this.googleMap.setMyLocationEnabled(true);
		this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(DEFAULT_LATLNG, 15));
	}

	private void updateMap() {
		googleMap.setMyLocationEnabled(true);
		myLocation = getMyLocation();

		if (myLocation != null) {
			if (list != null) {
				for (int i = 0; i < list.size(); i++) {
					list.get(i).setContentText(getData(i));
				}
			}

			if (adapter != null) {
				adapter.notifyDataSetChanged();
			}

			googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
					new LatLng(
							myLocation.getLatitude(),
							myLocation.getLongitude()
					), 15));
		}
	}

	private Location getMyLocation() {
		Location mfyLocation = null;
		try {
			LocationManager lm = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
			boolean gpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
			if (gpsEnabled) {
				mfyLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			}
			if (mfyLocation == null) {
				boolean networkEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
				if (networkEnabled) {
					mfyLocation = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
				}
			}
		} catch (Exception se) {
			se.printStackTrace();
		}
		return mfyLocation;
	}

	private String getData(int j) {
		try {
			switch (j) {
				case 0:
					return String.valueOf(myLocation.getLatitude());
				case 1:
					return String.valueOf(myLocation.getLongitude());
				case 2:
					return String.valueOf(myLocation.getSpeed()) + " m/s";
				case 3:
					return String.valueOf(myLocation.getAltitude()) + " m";
				case 4:
					return String.valueOf(myLocation.getBearing());
				case 5:
					return String.valueOf(myLocation.getAccuracy());
				default:
					return "N/A";
			}
		} catch (Exception e) {
			return "N/A";
		}
	}
}
