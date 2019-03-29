package hibernate.v2.testyourandroid.ui.activity;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import androidx.annotation.NonNull;
import android.view.View;
import android.view.WindowManager;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hibernate.v2.testyourandroid.C;
import hibernate.v2.testyourandroid.R;

public class HardwareScreenActivity extends BaseActivity {

	@BindView(R.id.colorView)
	View colorView;

	private boolean testMode = false;
	private int i = 0;

	private CountDownTimer timer = new CountDownTimer(1200000, 100) {
		@Override
		public void onFinish() {
			finish();
		}

		@Override
		public void onTick(long millisUntilFinished) {
			i++;
			changeColor(i);
		}
	};

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		C.detectLanguage(this);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_color);
		ButterKnife.bind(this);
		init();
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}

	@Override
	public void onPause() {
		timer.cancel();
		super.onPause();
	}

	private void changeColor(int j) {
		switch (j) {
			case 0:
				colorView.setBackgroundColor(Color.RED);
				break;
			case 1:
				colorView.setBackgroundColor(Color.GREEN);
				break;
			case 2:
				colorView.setBackgroundColor(Color.BLUE);
				break;
			case 3:
				colorView.setBackgroundColor(Color.CYAN);
				break;
			case 4:
				colorView.setBackgroundColor(Color.MAGENTA);
				break;
			case 5:
				colorView.setBackgroundColor(Color.YELLOW);
				break;
			case 6:
				colorView.setBackgroundColor(Color.BLACK);
				break;
			case 7:
				colorView.setBackgroundColor(Color.WHITE);
				break;
			case 8:
				colorView.setBackgroundColor(Color.GRAY);
				break;
			case 9:
				colorView.setBackgroundColor(Color.DKGRAY);
				break;
			case 10:
				colorView.setBackgroundColor(Color.LTGRAY);
				i = -1;
				break;
		}
	}

	private void init() {
		openDialogTutor();
	}

	@OnClick(R.id.colorView)
	public void onClickColorView() {
		if (!testMode) {
			i++;
			changeColor(i);
		}
	}

	private void openDialogTestMode() {
		MaterialDialog.Builder dialog = new MaterialDialog.Builder(this)
				.title(R.string.ui_caution)
				.content(R.string.color_test_message)
				.positiveText(R.string.ui_okay)
				.onPositive(new MaterialDialog.SingleButtonCallback() {
					@Override
					public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
						testMode = true;
						timer.start();
					}
				})
				.negativeText(R.string.ui_cancel);
		if (mContext.hasWindowFocus()) {
			dialog.show();
		}
	}

	private void openDialogTutor() {
		MaterialDialog.Builder dialog = new MaterialDialog.Builder(this)
				.title(R.string.color_title)
				.content(R.string.color_message)
				.negativeText(R.string.color_normal_btn)
				.positiveText(R.string.color_test_btn)
				.onPositive(new MaterialDialog.SingleButtonCallback() {
					@Override
					public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
						openDialogTestMode();
					}
				});
		if (mContext.hasWindowFocus()) {
			dialog.show();
		}
	}

}
