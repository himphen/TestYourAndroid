package hibernate.v2.testyourandroid.ui.fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import hibernate.v2.testyourandroid.C;
import hibernate.v2.testyourandroid.R;
import jaygoo.widget.wlv.WaveLineView;

/**
 * Created by himphen on 21/5/16.
 */
public class TestMicFragment extends BaseFragment {

	protected final String PERMISSION_NAME = Manifest.permission.RECORD_AUDIO;

	@BindView(R.id.waveLineView)
	WaveLineView waveLineView;
	@BindView(R.id.playBtn)
	Button playBtn;

	private MediaRecorder mMediaRecorder;
	private MediaPlayer mMediaPlayer;

	private boolean mIsRecording = false;
	private boolean mIsPlaying = false;
	private File mFile;

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

		playBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mIsRecording) {
					try {
						stopRecording();
						if (mFile.exists()) {
							mMediaPlayer = new MediaPlayer();
							mMediaPlayer.setDataSource(mFile.getAbsolutePath());
							mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
								@Override
								public void onCompletion(MediaPlayer mp) {
									stopPlaying();
								}
							});
							mMediaPlayer.prepare();
							mMediaPlayer.start();
							mIsPlaying = true;
							playBtn.setText(R.string.mic_stop_playing);
						}
					} catch (Exception e) {
						waveLineView.stopAnim();
					}

				} else if (mIsPlaying) {
					waveLineView.stopAnim();
					stopPlaying();
				} else {
					waveLineView.startAnim();
					try {
						mMediaRecorder = new MediaRecorder();
						mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
						mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
						mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
						mMediaRecorder.setOutputFile(mFile.getAbsolutePath());
						mMediaRecorder.prepare();
						mMediaRecorder.start();
						mIsRecording = true;
						playBtn.setText(R.string.mic_end);
					} catch (Exception e) {
						Toast.makeText(mContext, "Fail", Toast.LENGTH_SHORT).show();
						waveLineView.stopAnim();
					}
				}
			}
		});
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
			playBtn.setText(R.string.mic_stop_playing);
		}
	}

	private void stopRecording() {
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
			playBtn.setText(R.string.mic_start);
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
