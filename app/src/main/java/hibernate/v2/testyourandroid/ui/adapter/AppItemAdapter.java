package hibernate.v2.testyourandroid.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.turingtechnologies.materialscrollbar.INameableAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import hibernate.v2.testyourandroid.R;
import hibernate.v2.testyourandroid.model.AppItem;

/**
 * Created by himphen on 25/5/16.
 */
public class AppItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements INameableAdapter {

	private List<AppItem> mDataList;
	private ItemClickListener mListener;
	private Context mContext;

	@Override
	public Character getCharacterForElement(int element) {
		Character c = mDataList.get(element).getAppName().charAt(0);
		if (Character.isDigit(c)) {
			c = '#';
		}
		return c;
	}

	public interface ItemClickListener {
		void onItemDetailClick(AppItem catChoice);
	}

	public AppItemAdapter(List<AppItem> mDataList, ItemClickListener mListener) {
		this.mDataList = mDataList;
		this.mListener = mListener;
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		mContext = parent.getContext();

		View itemView = LayoutInflater.from(mContext).inflate(R.layout.list_item_info_app, parent, false);
		return new ItemViewHolder(itemView);
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder rawHolder, int position) {
		AppItem item = mDataList.get(position);
		ItemViewHolder holder = (ItemViewHolder) rawHolder;

		holder.titleTv.setText(item.getAppName());
		holder.contentTv.setText(item.getPackageName());

		Glide.with(mContext)
				.load("")
				.placeholder(item.getIcon())
				.into(holder.iconIv);

		if (item.isSystemApp()) {
			holder.systemAppIndicator.setVisibility(View.VISIBLE);
		} else {
			holder.systemAppIndicator.setVisibility(View.GONE);
		}

		holder.rootView.setTag(item);
		holder.rootView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				mListener.onItemDetailClick((AppItem) view.getTag());
			}
		});
	}

	@Override
	public int getItemCount() {
		return mDataList == null ? 0 : mDataList.size();
	}

	@SuppressWarnings("WeakerAccess")
	public static class ItemViewHolder extends RecyclerView.ViewHolder {

		@BindView(R.id.text1)
		TextView titleTv;
		@BindView(R.id.text2)
		TextView contentTv;
		@BindView(R.id.icon)
		ImageView iconIv;
		@BindView(R.id.root_view)
		LinearLayout rootView;
		@BindView(R.id.systemAppIndicator)
		FrameLayout systemAppIndicator;

		ItemViewHolder(View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}
	}
}