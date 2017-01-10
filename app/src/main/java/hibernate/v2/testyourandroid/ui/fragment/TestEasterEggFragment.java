package hibernate.v2.testyourandroid.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import hibernate.v2.testyourandroid.R;
import xyz.hanks.library.SmallBang;

/**
 * Created by himphen on 21/5/16.
 */
public class TestEasterEggFragment extends BaseFragment {

	@BindView(R.id.gifImageView)
	ImageView gifImageView;

	@BindView(R.id.pressTv)
	TextView pressTv;

	private SmallBang mSmallBang;
	private int pressedCount = 0;

	public TestEasterEggFragment() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View rootView = inflater.inflate(R.layout.fragment_test_easteregg, container, false);
		ButterKnife.bind(this, rootView);
		return rootView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		int icon[] = {
				R.drawable.launcher_hk, R.drawable.launcher_ali, R.drawable.launcher_girl,
				R.drawable.launcher_japan, R.drawable.launcher_korea, R.drawable.launcher_standard,
				R.drawable.launcher_xmas
		};
		int rnd = new Random().nextInt(icon.length);

		GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(gifImageView);
		Glide.with(this)
				.load(icon[rnd])
				.diskCacheStrategy(DiskCacheStrategy.ALL)
				.into(imageViewTarget);

		mSmallBang = SmallBang.attach2Window(mContext);
		gifImageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				mSmallBang.bang(view);

				if (pressedCount++ < 3) {
					pressTv.setText(R.string.press_me_1);
				} else {
					pressTv.setText(R.string.press_me_2);
				}
			}
		});
	}
}
