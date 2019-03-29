package hibernate.v2.testyourandroid.ui.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blankj.utilcode.util.RegexUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hibernate.v2.testyourandroid.R;

/**
 * Created by himphen on 21/5/16.
 */
public class ToolQRScannerSuccessFragment extends BaseFragment {

	@BindView(R.id.contentTv)
	AppCompatTextView contentTv;
	@BindView(R.id.formatTv)
	AppCompatTextView formatTv;
	@BindView(R.id.urlButton)
	AppCompatButton urlButton;

	private String content;
	private String barcodeFormatName;

	public static ToolQRScannerSuccessFragment newInstance(String content, String barcodeFormatName) {
		ToolQRScannerSuccessFragment fragment = new ToolQRScannerSuccessFragment();
		Bundle args = new Bundle();
		args.putString("content", content);
		args.putString("barcodeFormatName", barcodeFormatName);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_tool_qr_scanner_success, container, false);
		ButterKnife.bind(this, rootView);
		return rootView;
	}

	@Override
	public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		content = getArguments().getString("content");
		barcodeFormatName = getArguments().getString("barcodeFormatName");

		contentTv.setText(content);
		formatTv.setText(barcodeFormatName);

		if (RegexUtils.isURL(content)) {
			urlButton.setVisibility(View.VISIBLE);
		}
	}

	@OnClick(R.id.shareButton)
	public void onClickShare() {
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_SEND);
		intent.putExtra(Intent.EXTRA_TEXT, content);
		intent.setType("text/plain");
		startActivity(Intent.createChooser(intent, getResources().getText(R.string.ui_share)));
	}

	@OnClick(R.id.urlButton)
	public void onClickUrl() {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse(content));
		startActivity(intent);
	}
}
