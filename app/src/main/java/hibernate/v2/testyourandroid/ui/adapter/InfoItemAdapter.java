package hibernate.v2.testyourandroid.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import hibernate.v2.testyourandroid.R;
import hibernate.v2.testyourandroid.model.InfoItem;

/**
 * Created by himphen on 25/5/16.
 */
public class InfoItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

	private List<InfoItem> mDataList;
	private Context mContext;

	public InfoItemAdapter(List<InfoItem> mDataList) {
		this.mDataList = mDataList;
	}

	@NonNull
	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		mContext = parent.getContext();

		View itemView;
		itemView = LayoutInflater.from(mContext).inflate(R.layout.list_item_info, parent, false);
		return new ItemViewHolder(itemView);
	}

	@Override
	public void onBindViewHolder(@NonNull RecyclerView.ViewHolder rawHolder, int position) {
		InfoItem item = mDataList.get(position);
		ItemViewHolder holder = (ItemViewHolder) rawHolder;

		holder.titleTv.setText(item.getTitleText());
		holder.contentTv.setText(item.getContentText());
	}

	@Override
	public int getItemCount() {
		return mDataList.size();
	}

	static class ItemViewHolder extends RecyclerView.ViewHolder {
		@BindView(R.id.text1)
		TextView titleTv;
		@BindView(R.id.text2)
		TextView contentTv;

		public ItemViewHolder(View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}
	}
}