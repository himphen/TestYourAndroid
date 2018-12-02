package hibernate.v2.testyourandroid.ui.fragment;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.AppUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.psdev.licensesdialog.LicensesDialog;
import hibernate.v2.testyourandroid.C;
import hibernate.v2.testyourandroid.R;

public class MainAboutFragment extends BaseFragment {

	@BindView(R.id.versionTv)
	TextView versionTv;
	@BindView(R.id.logoIv)
	ImageView logoIv;

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_main_about, container, false);
		ButterKnife.bind(this, view);
		return view;
	}

	@Override
	public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		versionTv.setText(String.format("%s %s", getString(R.string.ui_version), AppUtils.getAppVersionName()));

		Glide.with(mContext)
				.load(R.drawable.android_resources)
				.apply(new RequestOptions()
						.diskCacheStrategy(DiskCacheStrategy.ALL))
				.into(logoIv);
	}

	@OnClick(R.id.moreButton)
	public void onClickMore() {
		try {
			Uri uri = Uri.parse("market://search?q=pub:\"Hibernate\"");
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			startActivity(intent);
		} catch (ActivityNotFoundException e) {
			C.notAppFound(mContext);
		}
	}

	@OnClick(R.id.shareButton)
	public void onClickShare() {
		String text = getString(R.string.share_message) + "\n\nhttps://play.google.com/store/apps/details?id=hibernate.v2.testyourandroid";

		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_SEND);
		intent.putExtra(Intent.EXTRA_TEXT, text);
		intent.setType("text/plain");
		startActivity(Intent.createChooser(intent, getString(R.string.share_button)));
	}

	@OnClick(R.id.feedbackButton)
	public void onClickFeedback() {
		Intent intent = new Intent(Intent.ACTION_SEND);

		String text = "Android Version: " + android.os.Build.VERSION.RELEASE + "\n";
		text += "SDK Level: " + String.valueOf(android.os.Build.VERSION.SDK_INT) + "\n";
		text += "Version: " + AppUtils.getAppVersionName() + "\n";
		text += "Brand: " + Build.BRAND + "\n";
		text += "Model: " + Build.MODEL + "\n\n\n";

		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_EMAIL, "hibernatev2@gmail.com");
		intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.feedback_title));
		intent.putExtra(Intent.EXTRA_TEXT, text);

		startActivity(intent);
	}

	@OnClick(R.id.creditButton)
	public void onClickCredit() {
		new LicensesDialog.Builder(mContext)
				.setNotices(R.raw.notices)
				.setThemeResourceId(R.style.LicenseDialogTheme)
				.build()
				.show();
	}
}