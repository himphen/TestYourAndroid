package hibernate.v2.testyourandroid.ui.fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import hibernate.v2.testyourandroid.C;
import hibernate.v2.testyourandroid.R;
import hibernate.v2.testyourandroid.ui.view.VoiceView;

/**
 * Created by himphen on 21/5/16.
 */
public class TestMicFragment extends BaseFragment {

	protected final String PERMISSION_NAME = Manifest.permission.RECORD_AUDIO;

	@BindView(R.id.voiceview)
	VoiceView voiceView;

	@BindView(R.id.voiceTv)
	TextView voiceTv;

	private MediaRecorder mMediaRecorder;
	private MediaPlayer mMediaPlayer;
	private Handler mHandler;

	private boolean mIsRecording = false;
	private boolean mIsPlaying = false;
	private File mFile;

	private Runnable r = new Runnable() {
		@Override
		public void run() {
			float radius = (float) Math.log10(Math.max(1, mMediaRecorder.getMaxAmplitude() - 500)) * C.convertDpToPixel(20, mContext);

			voiceView.animateRadius(radius);
			if (mIsRecording) {
				mHandler.postDelayed(this, 50);
			}
		}
	};

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_test_mic, container, false);
		ButterKnife.bind(this, rootView);
		return rootView;
	}

	@Override
	public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		if (ContextCompat.checkSelfPermission(mContext, PERMISSION_NAME) == PackageManager.PERMISSION_GRANTED) {
			init();
		} else {
			requestPermissions(new String[]{PERMISSION_NAME}, PERMISSION_REQUEST_CODE);
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		stopRecording();
		stopPlaying();
	}

	private void init() {
		mFile = new File(mContext.getFilesDir(), "TestYourAndroidMicTest.3gp");

		voiceView.setOnRecordListener(new VoiceView.OnRecordListener() {
			@Override
			public void onRecordStart() {
				stopPlaying();
				try {
					mMediaRecorder = new MediaRecorder();
					mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
					mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
					mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
					mMediaRecorder.setOutputFile(mFile.getAbsolutePath());
					mMediaRecorder.prepare();
					mMediaRecorder.start();
					mIsRecording = true;
					voiceTv.setText(R.string.mic_end);
					mHandler.post(r);
				} catch (Exception e) {
					Toast.makeText(mContext, "Fail", Toast.LENGTH_SHORT).show();
					e.printStackTrace();
				}
			}

			@Override
			public void onRecordFinish() {
				try {
					stopRecording();
					if (mFile.exists()) {
						mMediaPlayer = new MediaPlayer();
						mMediaPlayer.setDataSource(mFile.getAbsolutePath());
						mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
							@Override
							public void onCompletion(MediaPlayer mp) {
								voiceTv.setText(R.string.mic_start);
								mMediaPlayer.release();
								mMediaPlayer = null;
								mIsPlaying = false;
							}
						});
						mMediaPlayer.prepare();
						mMediaPlayer.start();
						voiceTv.setText(R.string.mic_stop_playing);
						mIsPlaying = true;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		mHandler = new Handler(Looper.getMainLooper());
	}

	private void stopPlaying() {
		if (mMediaPlayer != null) {
			if (mIsPlaying) {
				mIsPlaying = false;
				try {
					mMediaPlayer.stop();
				} catch (Exception ignored) {
				}
			}
			mMediaPlayer.release();
			mMediaPlayer = null;
			voiceTv.setText(R.string.mic_start);
		}
	}

	private void stopRecording() {
		if (mHandler != null) {
			mHandler.removeCallbacks(r);
		}
		if (mMediaRecorder != null) {
			if (mIsRecording) {
				mIsRecording = false;
				try {
					mMediaRecorder.stop();
				} catch (Exception ignored) {
				}
			}
			mMediaRecorder.release();
			mMediaRecorder = null;
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == PERMISSION_REQUEST_CODE) {
			if (hasAllPermissionsGranted(grantResults)) {
				init();
			} else {
				C.openErrorPermissionDialog(mContext);
			}
		}
	}
}
