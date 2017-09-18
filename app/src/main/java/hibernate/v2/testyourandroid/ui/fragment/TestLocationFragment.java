package hibernate.v2.testyourandroid.ui.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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

	protected final String PERMISSION_NAME_1 = Manifest.permission.ACCESS_FINE_LOCATION;
	protected final String PERMISSION_NAME_2 = Manifest.permission.ACCESS_COARSE_LOCATION;

	private InfoItemAdapter adapter;
	private List<InfoItem> list = new ArrayList<>();

	@BindView(R.id.rvlist)
	RecyclerView recyclerView;

	private GoogleApiClient mGoogleApiClient;
	private static final LatLng DEFAULT_LAT_LNG = new LatLng(22.3185392, 114.1707091);

	private GoogleMap googleMap;
	private Location lastKnowLocation;

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
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		recyclerView.setLayoutManager(
				new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
	}

	@Override
	public void onResume() {
		super.onResume();
		if (ContextCompat.checkSelfPermission(mContext, PERMISSION_NAME_1) == PackageManager.PERMISSION_GRANTED
				&& ContextCompat.checkSelfPermission(mContext, PERMISSION_NAME_2) == PackageManager.PERMISSION_GRANTED) {
			init();
		} else {
			requestPermissions(new String[]{
					PERMISSION_NAME_1,
					PERMISSION_NAME_2
			}, PERMISSION_REQUEST_CODE);
		}
	}

	@Override
	public void onPause() {
		super.onPause();
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

		list.clear();
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

		LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);

		List<String> providers = locationManager.getProviders(true);
		if (providers.size() == 0) {
			openFunctionDialog();
		}

		FragmentManager fm = getChildFragmentManager();
		SupportMapFragment fragment = (SupportMapFragment) fm.findFragmentById(R.id.mapFragment);
		if (fragment == null) {
			fragment = SupportMapFragment.newInstance();
			fm.beginTransaction().replace(R.id.mapFragment, fragment).commit();
		}

		fragment.getMapAsync(this);

		int status = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(mContext);
		if (status != ConnectionResult.SUCCESS) {
			if (GoogleApiAvailability.getInstance().isUserResolvableError(status)) {
				GoogleApiAvailability.getInstance().getErrorDialog(getActivity(), status,
						1972).show();
			}
		} else {
			mGoogleApiClient.connect();
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
						startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
					}
				});
		dialog.show();
	}

	@Override
	public void onLocationChanged(Location location) {
		Log.d(C.TAG, "onLocationChanged");

		lastKnowLocation = location;
		updateMap();
	}

	@Override
	public void onConnected(@Nullable Bundle bundle) {
		Log.d(C.TAG, "onConnected");
		LocationRequest mLocationRequest = LocationRequest.create();
		mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		mLocationRequest.setInterval(1000); // Update location every second
		if (ContextCompat.checkSelfPermission(mContext, PERMISSION_NAME_1) == PackageManager.PERMISSION_GRANTED
				&& ContextCompat.checkSelfPermission(mContext, PERMISSION_NAME_2) == PackageManager.PERMISSION_GRANTED) {
			if (mGoogleApiClient.isConnected()) {
				LocationServices.FusedLocationApi.requestLocationUpdates(
						mGoogleApiClient, mLocationRequest, this);
			}
		}
	}

	@Override
	public void onConnectionSuspended(int i) {
		Log.d(C.TAG, "onConnectionSuspended");
		Toast.makeText(mContext, R.string.wifi_reload_fail, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
		Log.d(C.TAG, "onConnectionFailed");
		Toast.makeText(mContext, R.string.wifi_reload_fail, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onMapReady(GoogleMap googleMap) {
		Log.d(C.TAG, "onMapReady");

		this.googleMap = googleMap;
		if (ContextCompat.checkSelfPermission(mContext, PERMISSION_NAME_1) == PackageManager.PERMISSION_GRANTED
				&& ContextCompat.checkSelfPermission(mContext, PERMISSION_NAME_2) == PackageManager.PERMISSION_GRANTED) {
			this.googleMap.setMyLocationEnabled(true);
			this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(DEFAULT_LAT_LNG, 15));
		}
	}

	private void updateMap() {
		if (ContextCompat.checkSelfPermission(mContext, PERMISSION_NAME_1) == PackageManager.PERMISSION_GRANTED
				&& ContextCompat.checkSelfPermission(mContext, PERMISSION_NAME_2) == PackageManager.PERMISSION_GRANTED
				&& googleMap != null) {
			googleMap.setMyLocationEnabled(true);

			if (lastKnowLocation != null) {
				for (int i = 0; i < list.size(); i++) {
					list.get(i).setContentText(getData(i));
				}
				adapter.notifyDataSetChanged();

				googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
						new LatLng(
								lastKnowLocation.getLatitude(),
								lastKnowLocation.getLongitude()
						), 15));
			}
		}
	}

	private String getData(int j) {
		try {
			switch (j) {
				case 0:
					return String.valueOf(lastKnowLocation.getLatitude());
				case 1:
					return String.valueOf(lastKnowLocation.getLongitude());
				case 2:
					return String.valueOf(lastKnowLocation.getSpeed()) + " m/s";
				case 3:
					return String.valueOf(lastKnowLocation.getAltitude()) + " m";
				case 4:
					return String.valueOf(lastKnowLocation.getBearing());
				case 5:
					return String.valueOf(lastKnowLocation.getAccuracy());
				default:
					return "N/A";
			}
		} catch (Exception e) {
			return "N/A";
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == PERMISSION_REQUEST_CODE) {
			if (grantResults.length == 2) {
				if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
					init();
				} else {
					C.openErrorPermissionDialog(mContext);
				}
			} else {
				C.openErrorPermissionDialog(mContext);
			}
		}
	}
}
