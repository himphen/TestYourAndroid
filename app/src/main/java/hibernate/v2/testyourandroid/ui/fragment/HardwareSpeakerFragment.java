package hibernate.v2.testyourandroid.ui.fragment;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatSpinner;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import hibernate.v2.testyourandroid.C;
import hibernate.v2.testyourandroid.R;

/**
 * Created by himphen on 21/5/16.
 */
public class HardwareSpeakerFragment extends BaseFragment {

	private int vibrateType = 0;
	private boolean isRinging = false;
	private boolean isVibrating = false;
	private MediaPlayer mediaPlayer;
	private Vibrator vibratorService;

	@BindView(R.id.ringButton)
	AppCompatButton ringButton;
	@BindView(R.id.vibrateButton)
	AppCompatButton vibrateButton;
	@BindView(R.id.spinner1)
	AppCompatSpinner spinner;


	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View rootView = inflater.inflate(R.layout.fragment_speaker, container, false);
		ButterKnife.bind(this, rootView);
		return rootView;
	}

	@Override
	public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		init();
	}

	private void init() {
		vibratorService = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);

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

		setListener();
	}

	private void startVibrate() {
		if (vibratorService == null) {
			C.errorNoFeatureDialog(mContext);
		}
		switch (vibrateType) {
			case 0:
				vibratorService.vibrate(30000);
				vibrateButton.setText(R.string.vibrate_stop_button);
				isVibrating = true;
				break;
			case 1:
				vibratorService.vibrate(new long[]{100, 200, 100}, 0);
				vibrateButton.setText(R.string.vibrate_stop_button);
				isVibrating = true;
				break;
		}
	}

	private void setListener() {
		ringButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isRinging) {
					stopPlayer();
				} else {
					startPlayer();
				}
			}
		});
		vibrateButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isVibrating) {
					stopVibrate();
				} else {
					startVibrate();
				}
			}
		});
	}

	@Override
	public void onPause() {
		if (isVibrating) {
			stopVibrate();
		}
		if (isRinging) {
			stopPlayer();
		}
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
		if (isRinging) {
			ringButton.setText(R.string.ring_button);
			isRinging = false;
			if (mediaPlayer != null) {
				mediaPlayer.stop();
				mediaPlayer.release();
				mediaPlayer = null;
			}
		}
	}

	private void stopVibrate() {
		vibratorService.cancel();
		vibrateButton.setText(R.string.vibrate_button);
		isVibrating = false;
	}
}
