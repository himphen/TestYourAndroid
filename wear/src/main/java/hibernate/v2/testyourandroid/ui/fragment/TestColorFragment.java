package hibernate.v2.testyourandroid.ui.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import hibernate.v2.testyourandroid.R;

/**
 * Created by himphen on 21/5/16.
 */
public class TestColorFragment extends BaseFragment {

	@BindView(R.id.colorView)
	View colorView;

	private int i = 0;

	public TestColorFragment() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_color, container, false);
		ButterKnife.bind(this, rootView);
		return rootView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		init();
		mContext.getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
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
		colorView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				i++;
				changeColor(i);
			}
		});
	}
}
