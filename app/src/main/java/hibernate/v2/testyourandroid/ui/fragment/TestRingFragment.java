package hibernate.v2.testyourandroid.ui.fragment;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import hibernate.v2.testyourandroid.C;
import hibernate.v2.testyourandroid.R;

/**
 * Created by himphen on 21/5/16.
 */
public class TestRingFragment extends BaseFragment {

	private int vibrateType;
	private boolean isRinging = false;
	private boolean isVibrating = false;
	private MediaPlayer mediaPlayer;
	private Vibrator vibrator;
	private AudioManager audioManager;

	@BindView(R.id.ringButton)
	Button ringButton;
	@BindView(R.id.vibrateButton)
	Button vibrateButton;
	@BindView(R.id.seekBar1)
	SeekBar seekBar;
	@BindView(R.id.spinner1)
	Spinner spinner;

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View rootView = inflater.inflate(R.layout.fragment_test_ring, container, false);
		ButterKnife.bind(this, rootView);
		return rootView;
	}

	@Override
	public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		init();
	}

	private void init() {
		vibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
		String[] array = getResources().getStringArray(R.array.vibrate_string_array);
		ArrayAdapter<String> adapter = new ArrayAdapter<>(mContext,
				android.R.layout.simple_spinner_item, array);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view,
			                           int position, long id) {
				vibrateType = position;
				stopVibrate();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
		try {
			audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);

			if (audioManager == null) {
				throw new Exception();
			}

			seekBar.setMax(audioManager
					.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
			seekBar.setProgress(audioManager
					.getStreamVolume(AudioManager.STREAM_MUSIC));
			seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
				@Override
				public void onProgressChanged(SeekBar arg0, int progress,
				                              boolean arg2) {
					try {
						audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
								progress, 0);
					} catch (SecurityException ignored) {
					}
				}

				@Override
				public void onStartTrackingTouch(SeekBar arg0) {
				}

				@Override
				public void onStopTrackingTouch(SeekBar arg0) {
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		setListener();
	}

	private void startVibrate(int vibrateType2) {
		if (vibrator == null) {
			C.openErrorDialog(mContext);
		}
		switch (vibrateType2) {
			case 0:
				vibrator.vibrate(30000);
				vibrateButton.setText(R.string.vibrate_stop_button);
				isVibrating = true;
				break;
			case 1:
				vibrator.vibrate(new long[]{100, 200, 100}, 0);
				vibrateButton.setText(R.string.vibrate_stop_button);
				isVibrating = true;
				break;
		}
	}

	private void setListener() {
		ringButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isRinging)
					stopPlayer();
				else
					startPlayer();
			}
		});
		vibrateButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isVibrating)
					stopVibrate();
				else
					startVibrate(vibrateType);
			}
		});
	}

	@Override
	public void onPause() {
		if (isVibrating)
			stopVibrate();
		if (isRinging)
			stopPlayer();
		super.onPause();
	}

	private void startPlayer() {
		try {
			mediaPlayer = MediaPlayer.create(mContext, R.raw.testring);
			mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
				@Override
				public void onCompletion(MediaPlayer mp) {
					stopPlayer();
				}
			});
			mediaPlayer.start();
			isRinging = true;
			ringButton.setText(R.string.ring_stop_button);
		} catch (Exception e) {
			Toast.makeText(mContext, "ERROR", Toast.LENGTH_SHORT).show();
		}
	}

	private void stopPlayer() {
		ringButton.setText(R.string.ring_button);
		isRinging = false;
		mediaPlayer.stop();
		mediaPlayer.release();
	}

	private void stopVibrate() {
		vibrator.cancel();
		vibrateButton.setText(R.string.vibrate_button);
		isVibrating = false;
	}
}
