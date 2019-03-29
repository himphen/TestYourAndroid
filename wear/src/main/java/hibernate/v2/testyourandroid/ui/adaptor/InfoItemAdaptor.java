package hibernate.v2.testyourandroid.ui.adaptor;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.karumi.headerrecyclerview.HeaderRecyclerViewAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import hibernate.v2.testyourandroid.R;
import hibernate.v2.testyourandroid.model.InfoHeader;
import hibernate.v2.testyourandroid.model.InfoItem;

/**
 * Created by himphen on 25/5/16.
 */
public class InfoItemAdaptor extends HeaderRecyclerViewAdapter<RecyclerView.ViewHolder, InfoHeader, InfoItem, RecyclerView.ViewHolder> {

	private List<InfoItem> mDataList;

	public InfoItemAdaptor(List<InfoItem> mDataList) {
		this.mDataList = mDataList;
	}

	@Override
	public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent, int viewType) {
		Context context = parent.getContext();

		View itemView = LayoutInflater.from(context).inflate(R.layout.header_item_info, parent, false);
		return new HeaderViewHolder(itemView);
	}

	@Override
	public void onBindHeaderViewHolder(RecyclerView.ViewHolder rawHolder, int position) {
		InfoHeader header = getHeader();
		HeaderViewHolder holder = (HeaderViewHolder) rawHolder;
		holder.titleTv.setText(header.getTitleText());
	}

	@Override
	public RecyclerView.ViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
		Context context = parent.getContext();

		View itemView = LayoutInflater.from(context).inflate(R.layout.list_item_info, parent, false);
		return new ItemViewHolder(itemView);
	}

	@Override
	public void onBindItemViewHolder(RecyclerView.ViewHolder rawHolder, int position) {
		// Due to adding header, we need to position -1
		InfoItem item = mDataList.get(position - 1);
		ItemViewHolder holder = (ItemViewHolder) rawHolder;

		holder.titleTv.setText(item.getTitleText());
		holder.contentTv.setText(item.getContentText());
	}

	@Override
	public int getItemCount() {
		return mDataList.size();
	}

	static class HeaderViewHolder extends RecyclerView.ViewHolder {
		@BindView(R.id.titleTv)
		TextView titleTv;

		HeaderViewHolder(View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}
	}

	static class ItemViewHolder extends RecyclerView.ViewHolder {

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