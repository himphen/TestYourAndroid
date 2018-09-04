package hibernate.v2.testyourandroid.ui.fragment;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appbrain.AdService;
import com.appbrain.AppBrain;
import com.blankj.utilcode.util.ScreenUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.divyanshu.draw.activity.DrawingActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import hibernate.v2.testyourandroid.C;
import hibernate.v2.testyourandroid.R;
import hibernate.v2.testyourandroid.model.GridItem;
import hibernate.v2.testyourandroid.ui.activity.HardwareCameraActivity;
import hibernate.v2.testyourandroid.ui.activity.HardwareFingerprintActivity;
import hibernate.v2.testyourandroid.ui.activity.HardwareFlashlightActivity;
import hibernate.v2.testyourandroid.ui.activity.HardwareLocationActivity;
import hibernate.v2.testyourandroid.ui.activity.HardwareMicrophoneActivity;
import hibernate.v2.testyourandroid.ui.activity.HardwareNFCActivity;
import hibernate.v2.testyourandroid.ui.activity.HardwareScreenActivity;
import hibernate.v2.testyourandroid.ui.activity.HardwareSpeakerActivity;
import hibernate.v2.testyourandroid.ui.activity.HardwareTouchActivity;
import hibernate.v2.testyourandroid.ui.activity.InfoAndroidVersionActivity;
import hibernate.v2.testyourandroid.ui.activity.InfoAppTypeChooseActivity;
import hibernate.v2.testyourandroid.ui.activity.InfoBatteryActivity;
import hibernate.v2.testyourandroid.ui.activity.InfoBluetoothActivity;
import hibernate.v2.testyourandroid.ui.activity.InfoCPUActivity;
import hibernate.v2.testyourandroid.ui.activity.InfoCameraActivity;
import hibernate.v2.testyourandroid.ui.activity.InfoDeviceActivity;
import hibernate.v2.testyourandroid.ui.activity.InfoGSMActivity;
import hibernate.v2.testyourandroid.ui.activity.InfoSystemMonitorActivity;
import hibernate.v2.testyourandroid.ui.activity.InfoWifiActivity;
import hibernate.v2.testyourandroid.ui.activity.MainActivity;
import hibernate.v2.testyourandroid.ui.activity.SensorAccelerometerActivity;
import hibernate.v2.testyourandroid.ui.activity.SensorCompassActivity;
import hibernate.v2.testyourandroid.ui.activity.SensorGravityActivity;
import hibernate.v2.testyourandroid.ui.activity.SensorHumidityActivity;
import hibernate.v2.testyourandroid.ui.activity.SensorLightActivity;
import hibernate.v2.testyourandroid.ui.activity.SensorPressureActivity;
import hibernate.v2.testyourandroid.ui.activity.SensorProximityActivity;
import hibernate.v2.testyourandroid.ui.activity.SensorStepActivity;
import hibernate.v2.testyourandroid.ui.activity.SensorTemperatureActivity;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;
import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection;

public class MainTestFragment extends BaseFragment {

	private SectionedRecyclerViewAdapter sectionAdapter;

	@BindView(R.id.gridRv)
	RecyclerView recyclerView;
	private int columnCount;

	public static MainTestFragment newInstance() {
		return new MainTestFragment();
	}

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_main_gridview, container, false);
		ButterKnife.bind(this, view);
		return view;
	}

	@Override
	public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		sectionAdapter = new SectionedRecyclerViewAdapter();

		Integer[] hardwareImageArray = {
				R.drawable.ic_icon_screen, R.drawable.ic_icon_drawing, R.drawable.ic_icon_touch,
				R.drawable.ic_icon_camera, R.drawable.ic_icon_fingerprint,
				R.drawable.ic_icon_flashlight, R.drawable.ic_icon_speaker, R.drawable.ic_icon_microphone,
				R.drawable.ic_icon_nfc, R.drawable.ic_icon_location
		};

		Class[] hardwareClassArray = {
				HardwareScreenActivity.class, DrawingActivity.class, HardwareTouchActivity.class,
				HardwareCameraActivity.class, HardwareFingerprintActivity.class,
				HardwareFlashlightActivity.class, HardwareSpeakerActivity.class, HardwareMicrophoneActivity.class,
				HardwareNFCActivity.class, HardwareLocationActivity.class
		};

		Integer[] sensorImageArray = {
				R.drawable.ic_icon_step, R.drawable.ic_icon_temperature, R.drawable.ic_icon_compass,
				R.drawable.ic_icon_light, R.drawable.ic_icon_accelerometer,
				R.drawable.ic_icon_chip, R.drawable.ic_icon_chip,
				R.drawable.ic_icon_chip, R.drawable.ic_icon_humidity
		};

		Class[] sensorClassArray = {
				SensorStepActivity.class, SensorTemperatureActivity.class, SensorCompassActivity.class,
				SensorLightActivity.class, SensorAccelerometerActivity.class,
				SensorProximityActivity.class, SensorPressureActivity.class,
				SensorGravityActivity.class, SensorHumidityActivity.class
		};

		Integer[] infoImageArray = {
				R.drawable.ic_icon_system_monitor, R.drawable.ic_icon_wifi, R.drawable.ic_icon_bluetooth,
				R.drawable.ic_icon_cpu, R.drawable.ic_icon_device, R.drawable.ic_icon_android,
				R.drawable.ic_icon_battery, R.drawable.ic_icon_camera,
				R.drawable.ic_icon_network, R.drawable.ic_icon_apps
		};

		Class[] infoClassArray = {
				InfoSystemMonitorActivity.class, InfoWifiActivity.class, InfoBluetoothActivity.class,
				InfoCPUActivity.class, InfoDeviceActivity.class, InfoAndroidVersionActivity.class,
				InfoBatteryActivity.class, InfoCameraActivity.class,
				InfoGSMActivity.class, InfoAppTypeChooseActivity.class
		};

		String[] otherStringArray = {
				"rate", "donate", "app_brain"
		};

		sectionAdapter.addSection(new MovieSection(getString(R.string.main_title_information), addList(
				getResources().getStringArray(R.array.info_string_array),
				infoImageArray,
				infoClassArray
		)));

		sectionAdapter.addSection(new MovieSection(getString(R.string.main_title_hardware), addList(
				getResources().getStringArray(R.array.hardware_string_array),
				hardwareImageArray,
				hardwareClassArray
		)));

		sectionAdapter.addSection(new MovieSection(getString(R.string.main_title_sensor), addList(
				getResources().getStringArray(R.array.sensor_string_array),
				sensorImageArray,
				sensorClassArray
		)));


		sectionAdapter.addSection(new MovieSection(getString(R.string.main_title_other), addList(
				getResources().getStringArray(R.array.other_string_array),
				otherImageArray(),
				otherStringArray
		)));

		columnCount = ScreenUtils.isTablet() && ScreenUtils.isLandscape() ? 4 : 3;

		GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), columnCount);
		gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
			@Override
			public int getSpanSize(int position) {
				switch (sectionAdapter.getSectionItemViewType(position)) {
					case SectionedRecyclerViewAdapter.VIEW_TYPE_HEADER:
						return columnCount;
					default:
						return 1;
				}
			}
		});

		recyclerView.setHasFixedSize(true);
		recyclerView.setLayoutManager(gridLayoutManager);
		recyclerView.setAdapter(sectionAdapter);
	}

	private List<GridItem> addList(String[] stringArray, Integer[] imageArray, Class[] classArray) {
		List<GridItem> list = new ArrayList<>();
		for (int i = 0; i < stringArray.length; i++) {
			list.add(new GridItem(stringArray[i], imageArray[i], classArray[i]));
		}

		return list;
	}

	private List<GridItem> addList(String[] stringArray, Integer[] imageArray, String[] string2Array) {
		List<GridItem> list = new ArrayList<>();
		for (int i = 0; i < stringArray.length; i++) {
			list.add(new GridItem(stringArray[i], imageArray[i], string2Array[i]));
		}

		return list;
	}

	private class MovieSection extends StatelessSection {
		private String title;
		private List<GridItem> gridItemList;

		MovieSection(String title, List<GridItem> gridItemList) {
			super(SectionParameters.builder()
					.itemResourceId(R.layout.item_main_icon)
					.headerResourceId(R.layout.item_main_header)
					.build());

			this.title = title;
			this.gridItemList = gridItemList;
		}

		@Override
		public int getContentItemsTotal() {
			return gridItemList.size();
		}

		@Override
		public RecyclerView.ViewHolder getItemViewHolder(View view) {
			return new ItemViewHolder(view);
		}

		@Override
		public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
			GridItem item = gridItemList.get(position);
			ItemViewHolder itemViewHolder = (ItemViewHolder) holder;

			Glide.with(itemViewHolder.mainIv.getContext())
					.load(item.getMainImageId())
					.apply(new RequestOptions()
							.fitCenter()
							.diskCacheStrategy(DiskCacheStrategy.ALL))
					.into(itemViewHolder.mainIv);

			itemViewHolder.mainTv.setText(item.getMainText());

			itemViewHolder.rootView.setTag(item);
			itemViewHolder.rootView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					GridItem gridItem = (GridItem) v.getTag();

					if (gridItem.getIntentClass() != null) {
						Intent intent = new Intent().setClass(mContext, gridItem.getIntentClass());
						startActivity(intent);
					} else {
						switch (gridItem.getActionType()) {
							case "donate":
								((MainActivity) getActivity()).checkPayment();
								break;
							case "rate":
								try {
									Uri uri = Uri.parse("market://details?id=hibernate.v2.testyourandroid");
									Intent intent = new Intent(Intent.ACTION_VIEW, uri);
									startActivity(intent);
								} catch (ActivityNotFoundException e) {
									C.notAppFound(mContext);
								}
								break;
							case "app_brain":
								AdService ads = AppBrain.getAds();
								ads.setOfferWallClickListener(mContext, v);
								break;
						}
					}
				}
			});
		}

		@Override
		public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
			return new HeaderViewHolder(view);
		}

		@Override
		public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
			HeaderViewHolder headerHolder = (HeaderViewHolder) holder;
			headerHolder.headerTv.setText(title);
		}
	}

	public Integer[] otherImageArray() {
		switch (new Random().nextInt(3)) {
			case 0:
				return new Integer[]{
						R.drawable.ic_icon_rating, R.drawable.ic_icon_donate_1, R.drawable.ic_icon_app_brain
				};
			case 1:
				return new Integer[]{
						R.drawable.ic_icon_rating, R.drawable.ic_icon_donate_2, R.drawable.ic_icon_app_brain
				};
			default:
				return new Integer[]{
						R.drawable.ic_icon_rating, R.drawable.ic_icon_donate_3, R.drawable.ic_icon_app_brain
				};
		}
	}

	static class HeaderViewHolder extends RecyclerView.ViewHolder {
		@BindView(R.id.root_view)
		LinearLayout rootView;
		@BindView(R.id.headerTv)
		TextView headerTv;

		HeaderViewHolder(View view) {
			super(view);
			ButterKnife.bind(this, itemView);
		}
	}

	static class ItemViewHolder extends RecyclerView.ViewHolder {
		@BindView(R.id.root_view)
		LinearLayout rootView;
		@BindView(R.id.mainIv)
		ImageView mainIv;
		@BindView(R.id.mainTv)
		TextView mainTv;

		public ItemViewHolder(View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}
	}


}