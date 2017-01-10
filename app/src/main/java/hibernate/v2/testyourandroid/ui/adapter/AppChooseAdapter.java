package hibernate.v2.testyourandroid.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import hibernate.v2.testyourandroid.R;
import hibernate.v2.testyourandroid.model.AppChooseItem;

/**
 * Created by himphen on 25/5/16.
 */
public class AppChooseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

	private List<AppChooseItem> mDataList;
	private ItemClickListener mListener;

	public interface ItemClickListener {
		void onItemDetailClick(AppChooseItem catChoice);
	}

	public AppChooseAdapter(List<AppChooseItem> mDataList, ItemClickListener mListener) {
		this.mDataList = mDataList;
		this.mListener = mListener;
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		Context context = parent.getContext();

		View itemView = LayoutInflater.from(context).inflate(R.layout.list_item_info, parent, false);
		return new ItemViewHolder(itemView);
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder rawHolder, int position) {
		AppChooseItem item = mDataList.get(position);
		ItemViewHolder holder = (ItemViewHolder) rawHolder;

		holder.titleTv.setText(item.getTitleText());
		holder.contentTv.setText(item.getContentText());

		holder.rootView.setTag(item);
		holder.rootView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mListener.onItemDetailClick((AppChooseItem) v.getTag());
			}
		});
	}

	@Override
	public int getItemCount() {
		return mDataList == null ? 0 : mDataList.size();
	}

	@SuppressWarnings("WeakerAccess")
	class ItemViewHolder extends RecyclerView.ViewHolder {

		@BindView(R.id.root_view)
		LinearLayout rootView;
		@BindView(R.id.text1)
		TextView titleTv;
		@BindView(R.id.text2)
		TextView contentTv;

		ItemViewHolder(View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}
	}
}