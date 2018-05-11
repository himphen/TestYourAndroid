package hibernate.v2.testyourandroid.ui.activity;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import hibernate.v2.testyourandroid.C;
import hibernate.v2.testyourandroid.R;

public class TestColorActivity extends Activity {

	private View colorView;
	boolean testMode = false;
	int i = 0;

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
	public boolean onContextItemSelected(MenuItem item) {
		changeColor(item.getItemId());
		return super.onContextItemSelected(item);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_color);
		init();
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}

	@Override
	public void onDestroy() {
		timer.cancel();
		super.onDestroy();
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
		colorView = findViewById(R.id.colorView);
		colorView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if (!testMode) {
					i++;
					changeColor(i);
				}
			}
		});
		colorView.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				CharSequence[] arrayName = getResources().getStringArray(R.array.color_string_array);
				MaterialDialog.Builder dialog = new MaterialDialog.Builder(TestColorActivity.this)
						.title(R.string.color_choose_title)
						.items(arrayName)
						.itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
							@Override
							public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
								i = which;
								changeColor(which);
								return false;
							}
						})
						.negativeText(R.string.ui_cancel);
				dialog.show();
				return false;
			}
		});
		openDialogTutor();
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
		dialog.show();
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
		dialog.show();

	}

}
